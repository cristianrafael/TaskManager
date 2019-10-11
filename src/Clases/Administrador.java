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
    
    javax.swing.JTable tabla; //La tabla donde se van a mostrar los estados de los procesos (La misma que se crea en Main.java)
    javax.swing.JProgressBar progressBar;
    javax.swing.JLabel memoria;
    
    List<Proceso> procesos; //Cola 1 cuantos 4
    
    
    Generador generador; //Generador de procesos en automatico
    
    boolean terminado; //Iteracion infinita hasta que se indique lo contrario
    
    int proceso_actual;
    
    public Administrador(javax.swing.JTable tabla , javax.swing.JProgressBar progressBar, javax.swing.JLabel memoria)
    {
        super("Main"); //Inicializamos el hilo con el nombre que este llevará
     
        this.tabla = tabla; //Seteamos la tabla que viene del main.java
        this.progressBar = progressBar;
        this.memoria = memoria;
        
        procesos = new ArrayList();
        generador = new Generador(procesos,tabla, progressBar); //Inicializamos el generador de procesos automatizado 3000
        terminado = false;
        proceso_actual = 0;
        progressBar.setValue(0);
    }
    @Override
    public void run(){
        generador.start();
        do
        {   
            System.out.print("Estado -> ");
            memoria.setText("Memoria: "+ progressBar.getValue()+"%");
            try {
                sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(progressBar.getValue()< 100 && proceso_actual < procesos.size()- 1)
            {
                if(!procesos.get(proceso_actual).terminado())
                {
                   proceso_actual++;
                   procesos.get(proceso_actual).iniciar();
                }
            }
        }
        while(!terminado);       
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
