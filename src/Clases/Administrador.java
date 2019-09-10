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
    javax.swing.JLabel memo,proc,disc;
    java.awt.Panel panel;
    javax.swing.JProgressBar bar_mem,bar_pro,bar_dis;
    
    DefaultTableModel tablaModelo; //El modelo de la tabla (se usa para insertar datos)
    List<Proceso> procesos; //Arreglo de los procesos(hilos) que se van creando
    Random rand; //Variable que sirve para generar numeros aleatorios
    int ultimo_terminado;
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
    public Administrador(javax.swing.JTable tabla, java.awt.Panel panel,javax.swing.JLabel memo, javax.swing.JLabel proc, javax.swing.JLabel disc,javax.swing.JProgressBar bar_mem, javax.swing.JProgressBar bar_pro,javax.swing.JProgressBar bar_dis)
    {
        super("Main"); //Inicializamos el hilo con el nombre que este llevará
                
        this.tabla = tabla; //Seteamos la tabla que viene del main.java
        this.panel = panel;
        this.memo = memo;
        this.proc = proc;
        this.disc = disc;
        this.bar_mem = bar_mem;
        this.bar_pro = bar_pro;
        this.bar_dis = bar_dis;
        
        procesos = new ArrayList(); //Inicializamos el arreglo de procesos.
        
        grafica = new Grafica(procesos,panel);
        
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
        
        ultimo_terminado = -1;
        terminado = false;
        
        mem = pro = dis = 0;
        
    }
    @Override
    public void run(){        
        grafica.start();
        do{
            mem = grafica.getMemoria(); memo.setText(""+mem+"%");bar_mem.setValue(mem);
            pro = grafica.getProcesador(); proc.setText(""+pro+"%");bar_pro.setValue(pro);
            dis = grafica.getDisco(); disc.setText(""+dis+"%");bar_dis.setValue(dis);
            
            for(int i = ultimo_terminado + 1; i<procesos.size(); i++)
            {
                mem = grafica.getMemoria(); memo.setText(""+mem+"%");bar_mem.setValue(mem);
                pro = grafica.getProcesador(); proc.setText(""+pro+"%");bar_pro.setValue(pro);
                dis = grafica.getDisco(); disc.setText(""+dis+"%");bar_dis.setValue(dis);
                if(procesos.get(i).getEstado().equals("En espera"))
                {
                    if(procesos.get(i).getMemoria() + mem <= 100)
                        if(procesos.get(i).getProcesador() + pro <= 100)
                            if(procesos.get(i).getDisco() + dis <= 100)
                            {
                                procesos.get(i).start();
                                try {
                                    sleep(1000);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                }
                else if(procesos.get(i).getEstado().equals("Terminado"))
                {
                    if(i == (ultimo_terminado +1))
                        ultimo_terminado++;
                }       
            }
            
            
            //System.out.println("Memoria: "+grafica.getMemoria()+ ", pro: "+grafica.getProcesador()+", dis: "+grafica.getDisco());
            
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
            //System.out.println("Hijo ejecutandose");
            
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
                    String[] row = {fila[0], fila[1], "En espera...", "0 seg", ""+fila[2]+" seg", ""+fila[3]+"%", ""+fila[4]+"%",""+fila[5]+"%"};
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
    
    public Grafica getGrafica(){
        return grafica;
    }
    
    public DefaultTableModel getModelo(){
        return tablaModelo;
    }
}
