/*
 * Esta clase es el HILO PRINCIPAL(PADRE)
 * El proposito es administrar los demas hilos que se van creando
 */
package Clases;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Cristian
 */
public class Administrador extends Thread{
    
    javax.swing.JTable tablaProcesos,tablaMemoria; //La tabla donde se van a mostrar los estados de los procesos (La misma que se crea en Main.java)    
    List<Proceso> procesos; //Cola de procesos
    Generador generador; //Generador de procesos en automatico
    boolean terminado; //Iteracion infinita del hilo hasta que se indique lo contrario  
    
    int[] bloquesDisponibles; //Bloques disponibles para almacenar los procesos
    int[][] matriz;
    int paginas; //Variable auxiliar para colocar las paginas necesarias para colocar el proceso que esta esperando
    int procesoActual;
    
    javax.swing.JLabel memDisponible;
    javax.swing.JLabel memUsada;
    
    public Administrador(javax.swing.JTable tablaProcesos,javax.swing.JTable tablaMemoria, javax.swing.JLabel memUsada, javax.swing.JLabel memDisponible)
    {
        super("Main"); //Inicializamos el hilo con el nombre que este llevará
        
        this.tablaProcesos = tablaProcesos; //Seteamos las tablas que viene del main.java
        this.tablaMemoria = tablaMemoria; 
        this.memDisponible = memDisponible;
        this.memUsada = memUsada;
        procesos = new ArrayList();//Inicializar la lista de procesos
        terminado = false;
        
        //Inicializamos la memoria
        bloquesDisponibles = new int[1];
        bloquesDisponibles[0] = 80;
        procesoActual = 0;
        
        matriz = new int[20][4];
        for(int i = 0; i<20; i++)
        {
            //El -1 significa que la posicion esta disponible, cualquier otro valor es porque esta ocupado
            matriz[i][0] = -1;
            matriz[i][1] = -1;
            matriz[i][2] = -1;
            matriz[i][3] = -1;
        }
        
        generador = new Generador(procesos,tablaProcesos, tablaMemoria,bloquesDisponibles,matriz); //Inicializamos el generador de procesos automatizado 3000
    }
    @Override
    public void run(){
        generador.start();
        do
        {   
            System.out.print("Estado -> ");
            
            if(procesos.size() > procesoActual)
            {
                paginas = procesos.get(procesoActual).getPaginas();
                
                while(bloquesDisponibles[0] < paginas)
                {
                    try
                    {
                        sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                bloquesDisponibles[0] -= paginas;
                memUsada.setText("Memoria usada: " + ((80 - bloquesDisponibles[0])*100) + " KB");
                memDisponible.setText("Memoria disponible : " + (bloquesDisponibles[0]*100)+ " KB");
                for(int i = 0; i<20; i++)
                {
                    for(int j = 0; j<4; j++)
                    {
                        if(matriz[i][j] == -1)
                        {
                            procesos.get(procesoActual).addCoordenada(i,j);
                            paginas--;
                            if(paginas == 0)
                                break;
                        }
                    }
                    if(paginas == 0)
                        break;
                }
                procesos.get(procesoActual).start();
                procesoActual++;        
                
            }
            
            try {
                sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        while(!terminado);       
    }
    public void pausarReanudarProceso(){
        int row = tablaProcesos.getSelectedRow();
        if(row != -1)
            procesos.get(row).pausar_reanudar();
        else
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un proceso");
    }
    public void detenerProceso(){
        int row = tablaProcesos.getSelectedRow();
        if(row != -1)
            procesos.get(row).detener();
        else
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un proceso");
    }
}
