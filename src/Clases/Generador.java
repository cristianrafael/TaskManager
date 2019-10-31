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
    
    javax.swing.JTable tablaProcesos; //La tabla donde se van a mostrar los estados de los procesos (La misma que se crea en Main.java)
    javax.swing.JTable tablaMemoria; //La tabla donde se van a mostrar la memoria ocupada de los procesos (La misma que se crea en Main.java)
    DefaultTableModel modeloProcesos; //El modelo de la tabla "tablaProcesos" (se usa para insertar datos)
    DefaultTableModel modeloMemoria; //El modelo de la tabla "tablaMemoria" (se usa para insertar datos)    
    
    List<Proceso> procesos; //Arreglo de los procesos(hilos) que se van creando
    boolean terminado; //Bandera que termina el while del run
    
    //Variables para archivos
    List<String[]> arreglo;
    String[] unidad = {"","","","",""};
    Random rand; //Variable que sirve para generar numeros aleatorios
    
    int[] bloquesDisponibles;
    int[][] matriz;
    Colores colores;
    public Generador(List<Proceso> procesos, javax.swing.JTable tablaProcesos, javax.swing.JTable tablaMemoria, int[] bloquesDisponibles, int[][] matriz)
    {
        this.tablaProcesos = tablaProcesos;
        this.tablaMemoria = tablaMemoria;
        this.procesos = procesos;
        this.bloquesDisponibles = bloquesDisponibles;
        this.matriz = matriz;
        modeloProcesos = new DefaultTableModel(); //Aqui inicializamos el modelo de la tabla, seguido del nombre de sus columnas
        modeloProcesos.addColumn("Nombre");
        modeloProcesos.addColumn("PID");
        modeloProcesos.addColumn("Estado");
        modeloProcesos.addColumn("Tiempo transcurrido");
        modeloProcesos.addColumn("Tiempo restante");
        modeloProcesos.addColumn("Memoria");
        tablaProcesos.setModel(modeloProcesos); //Le asignamos a la tabla, el modelo que acabamos de inicializar.
        
        modeloMemoria = new DefaultTableModel(); //Aqui inicializamos el modelo de la tabla, seguid del nombre de sus columnas
        modeloMemoria.addColumn(" ");
        modeloMemoria.addColumn(" ");
        modeloMemoria.addColumn(" ");
        modeloMemoria.addColumn(" ");
        tablaMemoria.setModel(modeloMemoria); //Le asignamos a la tabla, el modelo que acabamos de inicializar
        colores = new Colores();
        tablaMemoria.setDefaultRenderer(Object.class, colores);
        for(int i = 0; i<20 ; i++)
        {
            String[] row = {"","","",""};
            modeloMemoria.addRow(row);
        }
        
        rand = new Random(); //Se inicializa la variable random
        arreglo = new ArrayList(); //Arreglo de cadenas para generar procesos aleatorios(proviene del archivo procesos.txt)
        
        cargarArchivo();//Cargamos el archivo
    }
    @Override
    public void run()
    {
        do{
            //Agregamos un proceso aleatorio del arreglo de procesos
            unidad = arreglo.get(rand.nextInt(arreglo.size())); 

            //Estructuramos nuestra fila para la tabla con ese proceso aleatorio
            String[] row = {unidad[0],unidad[1],"Creado","0 seg",""+ unidad[2] +" seg", ""+ unidad[3] + " " + unidad[4]};

            //Agregamos la nueva fila a la tabla de procesos
            modeloProcesos.addRow(row);

            //Ahora vamos a crear el proceso como tal con la misma informacion de arriba
            Proceso task = new Proceso(unidad[0],tablaProcesos,tablaMemoria,tablaProcesos.getRowCount()-1, Integer.parseInt(unidad[2]),Integer.parseInt(unidad[3])/100 ,Integer.parseInt(unidad[1]), bloquesDisponibles,matriz);

            //Agregamos el proceso creado a la lista de procesos
            procesos.add(task);
            
            //Dormimos el hilo por un tiempo para crear el siguiente proceso en la lista
            try{sleep(500);}
            catch (InterruptedException ex) { Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);}
            
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

                linea = buffer.readLine();
            }                
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
