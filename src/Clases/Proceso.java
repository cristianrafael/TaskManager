/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Cristian
 */
public class Proceso extends Thread{

    String estado = "Creado"; //Los estados que puede tener son: Creado, Ejecutandose, Pausado , Terminado, Esperando turno
    
    javax.swing.JTable tablaProcesos; //Tabla que muestra el estado del proceso
    javax.swing.JTable tablaMemoria; //Tabla que muestra la memoria que consume el proceso
    
    int fila;  //Fila asignada para la tabla de los procesos (Saber cual es el renglon(o row) que tenemos que editar
    int tiempo; //Define la cantidad de tiempo que tardara el proceso en terminar
    int transcurrido; //Contador que indica el tiempo transcurrido del proceso
    int restante; //Contador que indica el tiempo que falta para que finalice el proceso
    
    boolean iniciado; //Bandera que indica si el proceso ya ejecuto su metodo start
    boolean termino_forzado; //Bandera que indica si el proceso se termino debido a una detencion forzada o manual ( mediante el boton)
    
    int paginas; //Son las paginas/bloques/celdas que consume el proceso
    int pid; //Identificador del proceso (OJO!! se puede repetir)
    
    List<int[]> coordenadas; //Lista de las coordenadas de cada una de las partes que compone el proceso en memoria
    int[] bloquesDisponibles;
    int[][] matriz;
    public Proceso(String nombre_proceso, javax.swing.JTable tablaProcesos, javax.swing.JTable tablaMemoria, int fila, int tiempo, int paginas, int pid, int[] bloquesDisponibles, int[][] matriz){
        
       super(nombre_proceso);
       
       this.tablaProcesos = tablaProcesos; 
       this.tablaMemoria = tablaMemoria;
       
       this.fila = fila;
       this.paginas = paginas;
       this.pid = pid;
              
       this.tiempo = this.restante = tiempo;
       this.transcurrido = 0;
       
       this.iniciado = false;
       this.termino_forzado = false;
       
       coordenadas = new ArrayList(); //Arreglo de cadenas para generar procesos aleatorios(proviene del archivo procesos.txt)
       
       this.bloquesDisponibles = bloquesDisponibles;
       this.matriz = matriz;
    }
    @Override
    public void run() {
        estado = "Ejecutandose";
        tablaProcesos.setValueAt(estado,fila,2);
        for(int i = 0; i<coordenadas.size(); i++)
        {
            int[] c = coordenadas.get(i);
            matriz[c[0]][c[1]] = pid;
            tablaMemoria.setValueAt(""+pid+"",c[0],c[1]);
        }
        
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
            tablaProcesos.setValueAt("" + transcurrido +" seg",fila,3);
            tablaProcesos.setValueAt("" + restante + " seg",fila,4);
            
            System.out.println("Paginas ->"+ paginas);
        }
        estado = "Terminado";
        tablaProcesos.setValueAt(estado,fila,2);
        
        for(int i = 0; i<coordenadas.size(); i++)
        {
            int[] c = coordenadas.get(i);
            matriz[c[0]][c[1]] = -1;
            tablaMemoria.setValueAt("",c[0],c[1]);
        }
        bloquesDisponibles[0] += paginas;
    }
    //Getters
    public int getFila(){
        return fila;
    }
    public int getPaginas(){
        return paginas;
    }
    public boolean getTerminoForzado(){
        return termino_forzado;
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
    
    //Setters
    public void addCoordenada(int x, int y)
    {
        int[] coordenada = new int[2];
        coordenada[0] = x;
        coordenada[1] = y;
        coordenadas.add(coordenada);
    }
    public void iniciar()
    {
        if(!terminado())
        {
            iniciado = true;
            start();
        }
    }
    
    synchronized void pausar_reanudar(){
        if(!terminado())
        {
            if(!pausado())
                estado = "Pausado";
            else
                estado = "Esperando turno";
            
            tablaProcesos.setValueAt(estado,fila,2);
            notify();
        }
    }
    synchronized void detener(){
        if(!terminado())
        {
            estado = "Terminado";
            termino_forzado = iniciado = true;
            tablaProcesos.setValueAt(estado,fila,2);
            notify();
        }
    }
}
