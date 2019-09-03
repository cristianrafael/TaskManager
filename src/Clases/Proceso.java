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
    //private boolean pausado, detenido;
    private String estado = "En espera"; //0 ['En espera','Ejecutandose','Pausado',Terminado']
    private int memoria,procesador,disco;
    
    public Proceso(String nombre_proceso, javax.swing.JTable tabla, int fila, int tiempo, int memoria,int procesador, int disco){
       super(nombre_proceso);
       this.tabla = tabla;
       this.fila = fila;
       this.tiempo = this.restante = tiempo;
       this.transcurrido = 0;
       //this.pausado = this.detenido = false;
       
       this.memoria = memoria;
       this.procesador = procesador;
       this.disco = disco;
    }
    @Override
    public void run() {
        estado = "Ejecutandose";
        if(restante>0)
        {
            tabla.setValueAt(estado,fila,2);
        }
        
        while(restante>0)
        {
            try {
                sleep(1000);
                synchronized (this)
                {
                    while(estado.equals("Pausado"))
                        wait();
                    if(estado.equals("Terminado"))
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
        estado = "Terminado";
        tabla.setValueAt(estado,fila,2);
    }
    public String getEstado(){
        return estado;
    }
    public int getMemoria(){
        return memoria;
    }
    public int getProcesador(){
        return procesador;
    }
    public int getDisco(){
        return disco;
    }
    
    synchronized void pausar_reanudar(){
        if(!estado.equals("Terminado")) //Si el proceso no esta detenido
        {
            if(estado.equals("Pausado"))
                estado = "Ejecutandose";
            else
                estado = "Pausado";
            tabla.setValueAt(estado,fila,2);
            notify();
        }
    }
    synchronized void detener(){
        if(!estado.equals("Terminado"))
        {
            estado = "Terminado";
            
            if(restante>0)
                tabla.setValueAt(estado,fila,2);
            
            restante = 0;
            notify();
        }
    }
}
