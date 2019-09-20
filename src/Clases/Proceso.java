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
    
    private Prioridad prioridad;
    private javax.swing.JTable tabla;
    private int fila,tiempo,transcurrido,restante;
    private String estado = "Creado"; //Los estados que puede tener son: Creado, Ejecutandose, Pausado , Terminado, Esperando turno
    private boolean turno,iniciado;
    
    public Proceso(String nombre_proceso, javax.swing.JTable tabla, int fila,int tiempo){
       super(nombre_proceso);
       this.tabla = tabla;
       this.fila = fila;
       this.tiempo = this.restante = tiempo;
       this.transcurrido = 0;
       this.turno = iniciado = false;
       this.prioridad = new Prioridad(nombre_proceso,tabla,fila,tiempo); 
       this.prioridad.start();
    }
    @Override
    public void run() {
        iniciado = true;
        estado = "Ejecutandose";
        tabla.setValueAt(estado + " ------------------->",fila,2);
        
        prioridad.pausar_reanudar();
        prioridad.setTiempoEspera(0);
        while(restante>0)
        {
            try {
                sleep(1000);

                synchronized (this)
                {
                    while(pausado() || esperando_turno())
                        wait();

                    if(terminado())
                        break;
                }
            } catch (InterruptedException ex) {
                System.out.println("Error en el hilo");
            }

            transcurrido++;
            restante = tiempo - transcurrido;
            tabla.setValueAt("" + transcurrido +" seg",fila,5);
            tabla.setValueAt("" + restante + " seg",fila,6);

            System.out.println("Hilo corriendo");
        }
        estado = "Terminado";
        prioridad.detener();
        tabla.setValueAt(estado + " //////////////////////",fila,2);
    }
    public int getPrioridad()
    {
        return prioridad.getPrioridad();
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
    public boolean esperando_turno(){
        if(!estado.equals("Esperando turno"))
            return false;
        return true;
    }
    synchronized void setTurno(boolean turno)
    {
        if(!terminado() && !pausado())
        {
            this.turno = turno;
            if(!iniciado)
                start();
            else
            {
                if(turno)
                {
                    estado = "Ejecutandose";
                    //prioridad.setTiempoEspera(0);
                    tabla.setValueAt(estado + " ------------------->",fila,2);
                }
                else{
                    estado = "Esperando turno"; 
                    tabla.setValueAt(estado,fila,2);
                }
                prioridad.pausar_reanudar();
                notify();
            }
        }
    }
    synchronized void pausar_reanudar(){
        if(!terminado())
        {
            if(!pausado())
            {
                estado = "Pausado";
                turno = false;
            }
            else
                if(turno)
                   estado = "Ejecutandose";
                else
                {
                   estado = "Esperando turno";
                   prioridad.pausar_reanudar();
                }
            
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
