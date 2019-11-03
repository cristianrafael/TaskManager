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
    List<Proceso> procesos;
    Generador generador; //Generador de procesos en automatico
    int ultimo_proceso,proceso_actual;    
    public Administrador(javax.swing.JTable tabla)
    {
        super("Main"); //Inicializamos el hilo con el nombre que este llevará
        this.tabla = tabla; //Seteamos la tabla que viene del main.java
        procesos = new ArrayList();   
        generador = new Generador(procesos,tabla); //Inicializamos el generador de procesos automatizado 3000
        ultimo_proceso = 0;
        proceso_actual = 0;
    }
    @Override
    public void run(){
        generador.start();
        do
        {   
            System.out.print("Corriendo Hilo principal");
            if(ultimo_proceso < procesos.size())
            {
               if(procesos.get(proceso_actual).terminado())
               {
                    if(ultimo_proceso == proceso_actual)
                    {
                       if(ultimo_proceso < procesos.size() -1)
                           ultimo_proceso++;
                    }
                    else
                    {
                        int numero = procesos.size();
                        proceso_actual++;
                        for(int i = ultimo_proceso; i< numero; i++)
                        {
                            if((!procesos.get(i).terminado()) && (procesos.get(i).getTiempo() < procesos.get(proceso_actual).getTiempo()))
                                proceso_actual = i;
                        }
                    }
               }
               else if(!procesos.get(proceso_actual).iniciado())
               {
                   procesos.get(proceso_actual).iniciar();
               }
                   
                             
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
