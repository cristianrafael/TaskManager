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
    int fila,tiempo,transcurrido,restante;
    String estado = "Creado"; //Los estados que puede tener son: Creado, Ejecutandose, Pausado , Terminado, Esperando turno
    
    boolean iniciado;
    boolean termino_forzado;
    
    boolean tipo; //True escritor  o False para lector
    int [] lectores_activos = new int [1];
    public Proceso(String nombre_proceso, javax.swing.JTable tabla, int fila,int tiempo, boolean tipo, int[] lectores_activos){
       super(nombre_proceso);
       this.tabla = tabla;
       this.lectores_activos = lectores_activos;
       this.fila = fila;
       this.tiempo = this.restante = tiempo;
       this.transcurrido = 0;
       this.termino_forzado = false;
       this.tipo = tipo;
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
            
            transcurrido++;
            restante = tiempo - transcurrido;
            tabla.setValueAt("" + transcurrido +" seg",fila,4);
            tabla.setValueAt("" + restante + " seg",fila,5);
            
            System.out.println("Hilo corriendo");
        }
        estado = "Terminado";
        tabla.setValueAt(estado,fila,2);
        
        if(!tipo) //Si el proceso es lector entonces reducimos
            lectores_activos[0]--;
    }
    public int getFila(){
        return fila;
    }
    public boolean getTipo(){
        return tipo;
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
        if(!tipo) //Si el proceso es lector entonces sumamos el contador
            lectores_activos[0]++;
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
                estado = "Ejecutandose";
            
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
