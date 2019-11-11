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
    
    Cargador cargador;
    Productor productor;
    Consumidor consumidor;
    
    javax.swing.JTable tabla;
    
    
    //List<Proceso> procesos;
    //Generador generador;  
    int proceso_actual;
    
    javax.swing.JLabel consumidorTam;
    javax.swing.JLabel productorTam;
    
    javax.swing.JProgressBar pilaConsumidor;
    javax.swing.JProgressBar pilaProductor;
    
    public Administrador(
                         javax.swing.JProgressBar pilaConsumidor,
                         javax.swing.JProgressBar pilaProductor,
                         javax.swing.JLabel imagenConsumidorPausado,
                         javax.swing.JLabel imagenProductorPausado,
                         javax.swing.JLabel imagenCargador,
                         javax.swing.JLabel imagenCajas,
                         javax.swing.JLabel consumidorTam,
                         javax.swing.JLabel productorTam)
    {
        super("Main");
        
        this.consumidor = new Consumidor(pilaConsumidor,imagenConsumidorPausado);
        this.cargador = new Cargador(pilaConsumidor,pilaProductor,imagenConsumidorPausado,imagenProductorPausado, imagenCargador,imagenCajas);
        this.productor = new Productor(pilaProductor,imagenProductorPausado,imagenCajas);
        
        this.pilaConsumidor = pilaConsumidor;
        this.pilaProductor = pilaProductor;
        
        this.consumidorTam = consumidorTam;
        this.productorTam = productorTam;
        
        //procesos = new ArrayList();
        //generador = new Generador(procesos,tabla);
        proceso_actual = 0;
    }
    @Override
    public void run(){
        productor.start();
        cargador.start();
        consumidor.start();
        
        
        //generador.start();
        do
        {   
            System.out.println("Administrador");
            consumidorTam.setText("Elementos: " + pilaConsumidor.getValue() + "/" + pilaConsumidor.getMaximum());
            productorTam.setText("Elementos: " +pilaProductor.getValue() + "/" + pilaProductor.getMaximum());
            
            /*try {
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
            }*/
        }
        while(true);
    }
    public void setVelocidadCargador(int velocidad)
    {
        cargador.setVelocidad(velocidad);
    }
    public void setVelocidadConsumidor(int velocidad)
    {
        consumidor.setVelocidad(velocidad);
    }
    public void setVelocidadProductor(int velocidad)
    {
        productor.setVelocidad(velocidad);
    }
    /*public void pausarReanudarProceso(){
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
    }*/
}
