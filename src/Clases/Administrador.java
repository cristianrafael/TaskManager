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
    
    javax.swing.JTable tablaProcesos,tablaMemoria, tablaDisco; //La tablas donde se van a mostrar los estados de los procesos (La misma que se crea en Main.java)   
    javax.swing.JLabel memDisponible,memUsada, disDisponbile, disUsado; //Las etiquetas que muestran la info de la memoria/disco
    
    List<Proceso> procesos; //Cola de procesos
    Generador generador; //Generador de procesos automatico
    
    int[][] memMatriz = new int[20][4];//La matriz de posiciones de la memoria ram
    int[][] disMatriz = new int[20][8]; //La matriz de posiciones del disco duro
    
    int[] memBloquesDisponibles = new int[1]; //Bloques disponibles para almacenar los procesos en ram
    int[] disBloquesDisponibles = new int[1]; //Bloques disponibles para almacenar los procesos en disco
    
    int paginas; //Variable auxiliar para colocar las paginas necesarias para colocar el proceso que esta esperando
    int procesoActual;
    
    
    public Administrador(javax.swing.JTable tablaProcesos,javax.swing.JTable tablaMemoria,javax.swing.JTable tablaDisco, javax.swing.JLabel memUsada, javax.swing.JLabel memDisponible,javax.swing.JLabel disUsado, javax.swing.JLabel disDisponible)
    {
        //Inicializamos el hilo con el nombre que este llevará
        super("Main");
        
        //Seteamos las tablas que vienen del main.java
        this.tablaProcesos = tablaProcesos; 
        this.tablaMemoria = tablaMemoria;
        this.tablaDisco = tablaDisco;
        
        //Seteamos las etiquetas que vienen del main.java
        this.memDisponible = memDisponible;
        this.memUsada = memUsada;
        this.disDisponbile = disDisponible;
        this.disUsado = disUsado;
        
        //Inicializar la lista de procesos
        procesos = new ArrayList();
        
        //Inicializamos la memoria y el disco
        memBloquesDisponibles[0] = 80;
        disBloquesDisponibles[0] = 160;
        for(int i = 0; i<20; i++)
        {
            //El -1 significa que la posicion esta disponible, cualquier otro valor es porque esta ocupado
            memMatriz[i][0] =  memMatriz[i][1] = memMatriz[i][2] = memMatriz[i][3] = -1;
            disMatriz[i][0] =  disMatriz[i][1] = disMatriz[i][2] = disMatriz[i][3] =  disMatriz[i][4] =  disMatriz[i][5] = disMatriz[i][6] = disMatriz[i][7] = -1;
        }
        
        //Establecemos el proceso actual de la cola
        procesoActual = 0;
        
        //Inicializamos el generador de procesos
        generador = new Generador(procesos, tablaProcesos, tablaMemoria, tablaDisco, memBloquesDisponibles, disBloquesDisponibles, memMatriz, disMatriz); //Inicializamos el generador de procesos automatizado 3000
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
        while(true);       
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
