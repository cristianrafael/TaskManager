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
    
    private Proceso proceso;
    
    private javax.swing.JTable tabla;
    private int fila,tiempo,transcurrido,restante;
    private String estado = "Creado"; //Los estados que puede tener son: Creado, Ejecutandose, Pausado ,Terminado
    
    
    public Proceso(String nombre_proceso, javax.swing.JTable tabla, int fila,int tiempo, Proceso proceso){
       super(nombre_proceso);
       this.tabla = tabla;
       this.proceso = proceso;
       
       this.fila = fila;
       this.tiempo = this.restante = tiempo;
       this.transcurrido = 0;
    }
    @Override
    public void run() {
        estado = "Ejecutandose";
        tabla.setValueAt(estado,fila,2);
        
        while(restante>0)
        {
            try {
                sleep(1000);
                
                synchronized (this)
                {
                    while(pausado())
                        wait();
                    
                    if(terminado())
                        break;
                }
            } catch (InterruptedException ex) {
                System.out.println("Error en el hilo");
            }
            
            if(proceso != null)
            {
                if(proceso.terminado())
                {
                    transcurrido++;
                    restante = tiempo - transcurrido;
                    tabla.setValueAt("" + transcurrido +" seg",fila,3);
                    tabla.setValueAt("" + restante + " seg",fila,4);
                }
            }
            else
            {
                transcurrido++;
                restante = tiempo - transcurrido;
                tabla.setValueAt("" + transcurrido +" seg",fila,3);
                tabla.setValueAt("" + restante + " seg",fila,4);
            }
            System.out.println("Hilo corriendo");
        }
        estado = "Terminado";
        tabla.setValueAt(estado,fila,2);
    }
    public boolean terminado(){
        if(!estado.equals("Terminado"))
            return false;
        return true;
    }
    public boolean pausado(){
        if(!estado.equals("Pausado"))
            return false;
        return true;
    }
    public Proceso getProceso(){
        return proceso;
    }
    public void setProceso(Proceso proceso){
       this.proceso = proceso;
    }
    synchronized void pausar_reanudar(){
        if(!terminado())
        {
            estado = pausado() ? "Ejecutandose" : "Pausado";
            tabla.setValueAt(estado,fila,2);
            notify();
        }
    }
    synchronized void detener(){
        if(!terminado())
        {
            estado = "Terminado";
            tabla.setValueAt(estado,fila,2);
            notify();
        }
    }
}
