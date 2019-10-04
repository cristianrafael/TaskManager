/*
 * Esta clase es el HILO PRINCIPAL(PADRE)
 * El proposito es administrar los demas hilos que se van creando
 */
package Clases;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Cristian
 */
public class Administrador extends Thread{
    
    javax.swing.JTable tabla; //La tabla donde se van a mostrar los estados de los procesos (La misma que se crea en Main.java)
    javax.swing.JProgressBar progressBar;
    javax.swing.JLabel estado_barra;
    javax.swing.JLabel elementos;
    List<Proceso> consumidores; //Cola 1 cuantos 4
    List<Proceso> productores; //Cola 2 cuantos 2
    
    List<List> colas; //Lista
    
    
    Generador generador; //Generador de procesos en automatico
    
    boolean terminado; //Iteracion infinita hasta que se indique lo contrario
    
    boolean produciendo;
    
    int cola_contador;
    
    int consumidor_actual, productor_actual;
    
    int consumidores_restantes;
    int productores_restantes;
    
    public Administrador(javax.swing.JTable tabla , javax.swing.JProgressBar progressBar, javax.swing.JLabel estado_barra, javax.swing.JLabel elementos)
    {
        super("Main"); //Inicializamos el hilo con el nombre que este llevará
     
        this.tabla = tabla; //Seteamos la tabla que viene del main.java
        this.progressBar = progressBar;
        this.estado_barra = estado_barra;
        this.elementos = elementos;
        productores = new ArrayList();
        consumidores = new ArrayList();
        
        colas = new ArrayList();
        
        colas.add(productores);
        colas.add(consumidores);
        
        generador = new Generador(colas,tabla); //Inicializamos el generador de procesos automatizado 3000
        terminado = false;
        produciendo = true;
        consumidor_actual = productor_actual = cola_contador = 0;
        productores_restantes = 8;
        consumidores_restantes = 5;
        progressBar.setValue(0);
    }
    @Override
    public void run(){
        generador.start();
        do
        {   
            System.out.print("Estado -> ");
            if(produciendo)
            {
                System.out.println("Produciendo");
                estado_barra.setText("Produciendo..");
                if(progressBar.getValue()< 100 && productor_actual < productores.size())
                {
                    
                    if(productores.get(productor_actual).terminado())
                    {
                        if(!productores.get(productor_actual).getTerminoForzado())
                        {
                            productores_restantes--;
                            progressBar.setValue(progressBar.getValue() + 10);
                            elementos.setText("Elementos : " + progressBar.getValue() / 10);
                            if(productores_restantes == 0)
                            {
                                produciendo = false;
                                if(consumidores_restantes == 0)
                                    consumidores_restantes = 5;
                            }
                        }                            
                        productor_actual++;
                    }
                    else if(!productores.get(productor_actual).iniciado())
                    {
                        System.out.println("Produciendo dentro");
                        productores.get(productor_actual).iniciar();
                        System.out.println("Produciendo fuera");
                    }
                }
                else
                    produciendo = false;
            }
            else
            {
                System.out.println("Consumiendo");
                estado_barra.setText("Consumiendo..");
                if(progressBar.getValue()> 0 && consumidor_actual < productores.size())
                {
                    if(!consumidores.get(consumidor_actual).iniciado())
                    {
                        System.out.println("Consumiendo dentro");
                        consumidores.get(consumidor_actual).iniciar();
                        System.out.println("Consumidores fuera");
                    }
                    else if(consumidores.get(consumidor_actual).terminado())
                    {
                        if(!consumidores.get(consumidor_actual).getTerminoForzado())
                        {
                            consumidores_restantes--;
                            progressBar.setValue(progressBar.getValue() - 10);
                            elementos.setText("Elementos : " + progressBar.getValue() / 10);
                            if(consumidores_restantes == 0)
                            {
                                produciendo = true;
                                if(productores_restantes == 0)
                                    productores_restantes = 8;
                            }
                        }                        
                        consumidor_actual++;
                    }                      
                }
                else
                    produciendo = true;
            }
        }
        while(!terminado);       
    }
    public void pausarReanudarProceso(){
        int row = tabla.getSelectedRow();
        if(row != -1)
        {
            boolean encontrado = false;
            for(int i = 0 ; i< productores.size(); i++)
            {
                productores.get(row).pausar_reanudar();
                encontrado = true;
                break;
            }
            if(!encontrado)
            for(int i = 0 ; i< consumidores.size(); i++)
            {
                consumidores.get(row).pausar_reanudar();
                break;
            }    
            
        }
        
        else
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un proceso");
    }
    public void detenerProceso(){
        int row = tabla.getSelectedRow();
        if(row != -1)
        {
            boolean encontrado = false;
            for(int i = 0 ; i< productores.size(); i++)
            {
                if(productores.get(i).getFila() == row)
                {
                    productores.get(i).detener();
                    encontrado = true;
                    break;
                }
            }
            if(!encontrado)
            for(int i = 0 ; i< consumidores.size(); i++)
            {
                if(consumidores.get(i).getFila() == row)
                {
                    consumidores.get(row).detener();
                    break;
                }
            }   
        }
        else
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un proceso");
    }
}
