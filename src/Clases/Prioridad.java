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
public class Prioridad extends Thread{
    
    private javax.swing.JTable tabla;
    private String estado = "Creado";
    int fila;
    int taza = 1;
    int tiempo_espera = 0;
    int tiempo_servicio = 0;
    
    public Prioridad(String nombre_proceso, javax.swing.JTable tabla, int fila,int tiempo_servicio){
        super(nombre_proceso + " prioridad");
        this.tabla = tabla;
        this.fila = fila;
        this.tiempo_servicio = tiempo_servicio;
    }
    @Override
    public void run(){
        while(!terminado())
        { 
            tabla.setValueAt(taza,fila,3);
            tabla.setValueAt(tiempo_espera + " s",fila,4);
            taza = (tiempo_espera + tiempo_servicio) / tiempo_servicio;
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
            tiempo_espera++;
            System.out.println("Hilo tiempo espera->" + taza);
        }
    }
    public void setTiempoEspera(int t)
    {
        this.tiempo_espera = t;
        tabla.setValueAt(tiempo_espera + " s",fila,6);
    }
    public int getPrioridad()
    {
        return taza;
    }
    synchronized void pausar_reanudar(){
        if(!terminado())
        {
            if(!pausado())
                estado = "Pausado";
            else
                estado = "Ejecutandose";
            notify();
        }
    }
    synchronized void detener(){
        if(!terminado())
        {
            estado = "Terminado";
            notify();
        }
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
}
