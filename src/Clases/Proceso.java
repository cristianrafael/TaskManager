/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;


/**
 *
 * @author Cristian
 */
public class Proceso extends Thread{
    
    private javax.swing.JTable tabla;
    private int fila,tiempo,transcurrido,restante;
    private boolean pausado, detenido;
    
    public Proceso(String nombre_proceso, javax.swing.JTable tabla, int fila,int tiempo){
       super(nombre_proceso);
       this.tabla = tabla;
       this.fila = fila;
       this.tiempo = this.restante = tiempo;
       this.transcurrido = 0;
       this.pausado = this.detenido = false;
    }
    @Override
    public void run() {
        
        if(restante>0){
            tabla.setValueAt("Ejecutandose",fila,2);
        }
        
        while(restante>0)
        {
            try {
                sleep(1000);
                synchronized (this)
                {
                    while(pausado)
                        wait();
                    if(detenido)
                        break;
                }
            } catch (InterruptedException ex) {
                System.out.println("Error en el hilo");
            }
            
            transcurrido++;
            restante = tiempo - transcurrido;
            tabla.setValueAt("" + transcurrido +" seg",fila,3);
            tabla.setValueAt("" + restante + " seg",fila,4);
        }
        tabla.setValueAt("Terminado",fila,2);
        detenido = true;
    }
    public boolean terminado(){
        return detenido;
    }
    synchronized void pausar_reanudar(){
        if(!detenido)
        {
            if(pausado)
            {
                pausado = false; 
                tabla.setValueAt("Ejecutandose",fila,2);
            }
            else
            {
                pausado = true;
                tabla.setValueAt("Pausado",fila,2);
            }
            notify();
        }
    }
    synchronized void detener(){
        if(!detenido)
        {
            pausado = false;
            detenido = true;
            
            if(restante>0)
                tabla.setValueAt("Terminado",fila,2);
            
            restante = 0;
            notify();
        }
    }
}
