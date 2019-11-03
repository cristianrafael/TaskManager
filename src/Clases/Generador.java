/*
 * Esta clase se creo con el proposito de crear procesos de manera automatica.
 */
package Clases;

import Main.Inicio;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Cristian
 */
public class Generador extends Thread{
    
    javax.swing.JTable tabla; //La tabla donde se van a mostrar los estados de los procesos (La misma que se crea en Main.java)
    DefaultTableModel tablaModelo; //El modelo de la tabla (se usa para insertar datos)
    List<Proceso> procesos; //Arreglo de los procesos(hilos) que se van creando
    //Colores colores = new Colores();
    
    //Variables para archivos
    List<String[]> arreglo;
    String[] unidad = {"","","","","",""};
    Random rand; //Variable que sirve para generar numeros aleatorios
    public Generador(List<Proceso> procesos, javax.swing.JTable tabla)
    {
        this.tabla = tabla;
        this.procesos = procesos;
        
        tablaModelo = new DefaultTableModel(); //Aqui inicializamos el modelo de la tabla, seguido del nombre de sus columnas
        tablaModelo.addColumn("Nombre");
        tablaModelo.addColumn("PID");
        tablaModelo.addColumn("Estado");
        tablaModelo.addColumn("Rafagas Realizadas (CPU)");
        tablaModelo.addColumn("Rafagas Restantes (CPU)");
        tablaModelo.addColumn("Memoria");
        tabla.setModel(tablaModelo); //Le asignamos a la tabla, el modelo que acabamos de inicializar.
        rand = new Random(); //Se inicializa la variable random
        arreglo = new ArrayList(); //Arreglo de cadenas para generar procesos aleatorios(proviene del archivo procesos.txt)
        cargarArchivo();//Cargamos el archivo
        //tabla.setDefaultRenderer (Object.class, colores);
    }
    @Override
    public void run()
    {
        do{
            unidad = arreglo.get(rand.nextInt(arreglo.size()));
            String[] row2 = {unidad[0],unidad[1],"En espera...","0",""+ unidad[2], ""+ unidad[3] + " " + unidad[4]};
            tablaModelo.addRow(row2);
            Proceso task2 = new Proceso(unidad[0],tabla,tabla.getRowCount()-1, Integer.parseInt(unidad[2]));
            procesos.add(task2);
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Hilo corriendo");
        }while(true);
    }
    private void cargarArchivo(){
        
        int num_token; //El numero de token elige la posicion del arreglo para saber que token es.
        String token; //el token concatena los caracteres leidos para obtener una cadena
        File archivo = new File("procesos.txt");//Archivo para cargar los proceso
        
        try {
            FileReader lector = new FileReader(archivo); //Lector que recorre el archivo
            BufferedReader buffer = new BufferedReader(lector); //buffer
            String linea = buffer.readLine(); //La linea sirve para leer linea por linea el archivo, 
            while(linea != null) //Mientras la linea actual no sea nula
            {   
                num_token = 0; //Incializamos con el primer token
                token = ""; //Vaciamos el token

                String[] fila = {"","","","",""};

                for(int i=0; i< linea.length(); i++)
                {
                    if(linea.charAt(i) == ' ')//Si el caracter leido es un espacio en blanco
                    {
                        if(!token.equals(""))//Si el token contiene algo
                        {
                            fila[num_token] = token;//Verificamos que numero de token es para saber que parametro estamos concatenando
                            token = "";
                            num_token++;
                        }
                    } 
                    else
                        token += linea.charAt(i);
                }
                fila[num_token] = token;//Agregamos el ultimo token ( Como no hay espacio tiene que ser el ultimo)

                arreglo.add(fila);

                //System.out.println(" " + fila[0] + " " + fila[1] + " " + fila[2] + " " + fila[3] + " " + fila[4]);
                //String[] row = {fila[0], fila[1], "En espera...", "0 seg", ""+fila[2]+" seg", ""+fila[3]+" "+fila[4]};
                //tablaModelo.addRow(row);
                //Proceso task = new Proceso(fila[0],tabla,tabla.getRowCount()-1, Integer.parseInt(fila[2]));
                //procesos.add(task);

                linea = buffer.readLine();
            }                
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
