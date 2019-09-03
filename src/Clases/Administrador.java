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
    int proceso_siguiente;
    boolean terminado;
    //Variables para archivos
    File archivo; //Archivo para cargar los proceso
    FileReader lector; //Lector que recorre el archivo
    BufferedReader buffer; //buffer
    String linea,token; //La linea sirve para leer linea por linea el archivo, el token concatena los caracteres leidos para obtener una cadena
    int num_token; //El numero de token elige la posicion del arreglo para saber que token es.
    String[] fila = {"","","","","","",""};
    
    Grafica grafica;
    
    int mem,pro,dis;
    public Administrador(javax.swing.JTable tabla)
    {
        super("Main"); //Inicializamos el hilo con el nombre que este llevará
                
        this.tabla = tabla; //Seteamos la tabla que viene del main.java
        
        procesos = new ArrayList(); //Inicializamos el arreglo de procesos.
        
        grafica = new Grafica(procesos);
        
        tablaModelo = new DefaultTableModel(); //Aqui inicializamos el modelo de la tabla, seguido del nombre de sus columnas
        tablaModelo.addColumn("Nombre");
        tablaModelo.addColumn("PID");
        tablaModelo.addColumn("Estado");
        tablaModelo.addColumn("Tiempo transcurrido");
        tablaModelo.addColumn("Tiempo restante");
        tablaModelo.addColumn("Memoria");
        tablaModelo.addColumn("Procesador");
        tablaModelo.addColumn("Disco");
        
        rand = new Random(); //Se inicializa la variable random
        tabla.setModel(tablaModelo); //Le asignamos a la tabla, el modelo que acabamos de inicializar.
        
        cargarArchivo();//Cargamos el archivo
        
        proceso_siguiente = 0;
        terminado = false;
        
        mem = pro = dis = 0;
        
    }
    @Override
    public void run(){        
        grafica.start();
        do{
            calcularRecursos();
            for(int i= proceso_siguiente; i<procesos.size(); i++)
            {
                if((procesos.get(i).getMemoria() + mem  <= 100) && (procesos.get(i).getProcesador() + pro <=100) && (procesos.get(i).getDisco() + dis <= 100))
                    procesos.get(i).start();
            }
            
            
            /*if(procesos.get(proceso_actual).getEstado().equals("Terminado"))
            {
                System.out.println(procesos.get(proceso_actual).getEstado().equals("Terminado"));
                if(proceso_actual < procesos.size())
                {
                    proceso_actual++;
                    procesos.get(proceso_actual).start();
                    
             
                    int PID = rand.nextInt(10) + 1;
                    int tiempo = rand.nextInt(15)+ 2;
                    int memoria = rand.nextInt(10) +3;
                    int procesador = rand.nextInt(20) +1;
                    int disco = rand.nextInt(20) +2;
                    
                    String[] row = {"Usuario",""+PID+"","En espera...", "0 seg",""+tiempo+" seg", ""+memoria+"",""+procesador+"",""+disco+""};
                    tablaModelo.addRow(row);
                    Proceso task = new Proceso("Usuario",tabla,tabla.getRowCount()-1, tiempo,memoria,procesador,disco);
                    procesos.add(task);
                }
                else
                    terminado = true;
            }*/
            
            System.out.println("Hilo ejecutandose");
        }while(!terminado);
    }
    private void cargarArchivo(){
        
        archivo = new File("procesos.txt");
        if(archivo != null)
            try {
                lector = new FileReader(archivo);
                buffer = new BufferedReader(lector);
                linea = buffer.readLine();
                while(linea != null) //Mientras la linea actual no sea nula
                {   
                    //System.out.println(linea);
                    num_token = 0; //Incializamos con el primer token
                    token = ""; //Vaciamos el token
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
                                
                    System.out.println(" " + fila[0] + " " + fila[1] + " " + fila[2] + " " + fila[3] + " " + fila[4] + " " + fila[5]);
                    String[] row = {fila[0], fila[1], "En espera...", "0 seg", ""+fila[2]+" seg", ""+fila[3]+"", ""+fila[4]+"",""+fila[5]+""};
                    tablaModelo.addRow(row);
                    Proceso task = new Proceso(fila[0],tabla,tabla.getRowCount()-1, Integer.parseInt(fila[2]),Integer.parseInt(fila[3]),Integer.parseInt(fila[4]),Integer.parseInt(fila[5]));
                    procesos.add(task);
                    //task.start();
                    linea = buffer.readLine();
                }                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    private void calcularRecursos(){
        mem = pro = dis = 0;
        for(int i=0; i<procesos.size(); i++)
        {
            if(procesos.get(i).getEstado().equals("Ejecutandose") || procesos.get(i).getEstado().equals("Pausado"))
            {
                mem += procesos.get(i).getMemoria();
                pro += procesos.get(i).getProcesador();
                dis += procesos.get(i).getDisco();
            }
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
        if(row != -1)
            procesos.get(row).detener();
        else
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un proceso");
    }
}
