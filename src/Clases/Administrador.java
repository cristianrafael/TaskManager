/*
 * Esta clase es el HILO PRINCIPAL(PADRE)
 * El proposito es administrar los demas hilos que se van creando
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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Cristian
 */
public class Administrador extends Thread{
    
    javax.swing.JTable tabla; //La tabla donde se van a mostrar los estados de los procesos (La misma que se crea en Main.java)
    DefaultTableModel tablaModelo; //El modelo de la tabla (se usa para insertar datos)
    List<Proceso> procesos; //Arreglo de los procesos(hilos) que se van creando
    Random rand; //Variable que sirve para generar numeros aleatorios
    int proceso_actual;
    boolean terminado;
    
    //Variables para archivos
    List<String[]> arreglo;
    String[] unidad = {"","","","",""};
    public Administrador(javax.swing.JTable tabla)
    {
        super("Main"); //Inicializamos el hilo con el nombre que este llevará
                
        this.tabla = tabla; //Seteamos la tabla que viene del main.java
        
        arreglo = new ArrayList(); //Arreglo de cadenas para generar procesos aleatorios(proviene del archivo procesos.txt)
        procesos = new ArrayList(); //Inicializamos el arreglo de procesos.
        
        tablaModelo = new DefaultTableModel(); //Aqui inicializamos el modelo de la tabla, seguido del nombre de sus columnas
        tablaModelo.addColumn("Nombre");
        tablaModelo.addColumn("PID");
        tablaModelo.addColumn("Estado");
        tablaModelo.addColumn("Tiempo transcurrido");
        tablaModelo.addColumn("Tiempo restante");
        tablaModelo.addColumn("Memoria");
        
        rand = new Random(); //Se inicializa la variable random
        tabla.setModel(tablaModelo); //Le asignamos a la tabla, el modelo que acabamos de inicializar.
        
        cargarArchivo();//Cargamos el archivo
        
        proceso_actual = 0;
        terminado = false;
    }
    @Override
    public void run(){
        
        unidad = arreglo.get(rand.nextInt(arreglo.size())); 
        String[] row = {unidad[0],unidad[1],"En espera...", "0 seg",""+ unidad[2] +" seg", ""+ unidad[3] + " " + unidad[4]};
        tablaModelo.addRow(row);
        Proceso task = new Proceso(unidad[0],tabla,tabla.getRowCount()-1, Integer.parseInt(unidad[2]),null);
        procesos.add(task);
        task.start();
        System.out.println("Se creo el primero sin fallas");
        do{
            try {
                sleep(5000);
                unidad = arreglo.get(rand.nextInt(arreglo.size()));
                String[] row2 = {unidad[0],unidad[1],"En espera...", "0 seg",""+ unidad[2] +" seg", ""+ unidad[3] + " " + unidad[4]};
                tablaModelo.addRow(row2);
                Proceso task2 = new Proceso(unidad[0],tabla,tabla.getRowCount()-1, Integer.parseInt(unidad[2]),procesos.get(procesos.size()-1));
                procesos.add(task2);
                task2.start();
            
            } catch (InterruptedException ex) {
                Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);
            }
            
                   
            
            System.out.println("Hilo corriendo");
        }while(!terminado);
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
    public void pausarReanudarProceso(){
        int row = tabla.getSelectedRow();
        if(row != -1)
            procesos.get(row).pausar_reanudar();
        else
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un proceso");
    }
    public void detenerProceso(){
        int row = tabla.getSelectedRow();
        if(row >= 0)
        {
            if(!procesos.get(row).terminado()) //Si el proceso que se desea terminar ya esta terminado pues para que hacer lo de adentro
            {
                if(row == 0) //Primer caso, si detenemos el primero
                {
                    if(procesos.size()>1) //Primer caso opcion A (Que exista mas de un proceso)
                        procesos.get(1).setProceso(null);
                    //Primer caso opcion B (Que no exista otro proceso ademas de ese, entonces no hacemos nada)
                }
                else if(row == (procesos.size()-1)) //Segundo caso, estamos terminando el ultimo proceso
                {
                    //Aqui practicamente no hariamos nada, puesto que no hay mas procesos
                }
                else //Tercer caso, el proceso tiene papa e hijo (ancestro y descendiente)
                {
                    //Practicamente vamos a heredarle el proceso ancestro al sucesor.
                    procesos.get(row+1).setProceso(procesos.get(row).getProceso());
                }
                procesos.get(row).detener();
            }
        }
        else
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un proceso");
    }
}
