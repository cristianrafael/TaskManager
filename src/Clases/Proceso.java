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
    
    javax.swing.JTable tabla;
    int fila,tiempo,transcurrido,restante,peso;
    String estado = "Creado"; //Los estados que puede tener son: Creado, Ejecutandose, Pausado , Terminado, Esperando turno
    boolean turno,iniciado;
    boolean termino_forzado;
    
    public Proceso(String nombre_proceso, javax.swing.JTable tabla, int fila,int tiempo){
       super(nombre_proceso);
       this.tabla = tabla;
       this.fila = fila;
       this.tiempo = this.restante = tiempo;
       this.transcurrido = 0;
       turno = iniciado = false;
       this.termino_forzado = false;
    }
    @Override
    public void run() {
        estado = "Ejecutandose";
        tabla.setValueAt(estado,fila,2);
        
        while(restante>0)
        {
            try {
                sleep(500);
                
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
            
            transcurrido++;
            restante = tiempo - transcurrido;
            tabla.setValueAt("" + transcurrido,fila,3);
            tabla.setValueAt("" + restante,fila,4);
            
            System.out.println("Hilo corriendo");
        }
        estado = "Terminado";
        tabla.setValueAt(estado,fila,2);
    }
    public int getFila(){
        return fila;
    }
    public int getTiempo(){
        return tiempo;
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
    public boolean iniciado(){
        return iniciado;
    }
    public void iniciar()
    {
        iniciado = true;
        start();
    }
    public boolean getTerminoForzado()
    {
        return termino_forzado;
    }
    synchronized void pausar_reanudar(){
        if(!terminado())
        {
            if(!pausado())
                estado = "Pausado";
            else
                if(turno)
                   estado = "Ejecutandose";
                else
                   estado = "Esperando turno";
            
            tabla.setValueAt(estado,fila,2);
            notify();
        }
    }
    synchronized void detener(){
        if(!terminado())
        {
            estado = "Terminado";
            termino_forzado = iniciado = true;
            tabla.setValueAt(estado,fila,2);
            notify();
        }
    }
}
