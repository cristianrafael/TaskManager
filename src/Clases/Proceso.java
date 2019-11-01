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
    javax.swing.JTable tablaDisco; //Tabla que muestra la memoria que consume el proceso
    
    int fila;  //Fila asignada para la tabla de los procesos (Saber cual es el renglon(o row) que tenemos que editar
    int tiempo; //Define la cantidad de tiempo que tardara el proceso en terminar
    int transcurrido; //Contador que indica el tiempo transcurrido del proceso
    int restante; //Contador que indica el tiempo que falta para que finalice el proceso
    
    boolean iniciado; //Bandera que indica si el proceso ya ejecuto su metodo start
    boolean termino_forzado; //Bandera que indica si el proceso se termino debido a una detencion forzada o manual ( mediante el boton)
    
    int paginas; //Son las paginas/bloques/celdas que consume el proceso
    int paginasRam; //Son las paginas que si estan en RAM, el resto estan en el disco
    
    int pid; //Identificador del proceso (OJO!! se puede repetir)
    
    List<int[]> memCoordenadas; //Lista de las coordenadas de cada una de las partes que compone el proceso en memoria
    List<int[]> disCoordenadas; //Lista de las coordenadas de cada una de las partes que compone el proceso en disco
    
    int[] memBloquesDisponibles;
    int[] disBloquesDisponibles;
    int[][] memMatriz;
    int[][] disMatriz;
    
    public Proceso(String nombre_proceso, javax.swing.JTable tablaProcesos, javax.swing.JTable tablaMemoria, javax.swing.JTable tablaDisco, int[] memBloquesDisponibles, int[] disBloquesDisponibles, int[][] memMatriz, int[][] disMatriz, int fila, int pid, int tiempo, int paginas, int paginasRam){
   
       super(nombre_proceso);
       
       this.tablaProcesos = tablaProcesos; 
       this.tablaMemoria = tablaMemoria;
       this.tablaDisco = tablaDisco;
       
       this.memBloquesDisponibles = memBloquesDisponibles;
       this.disBloquesDisponibles = disBloquesDisponibles;
       
       this.memMatriz = memMatriz;
       this.disMatriz = disMatriz;
       
       this.fila = fila;
       this.pid = pid;
       this.tiempo = this.restante = tiempo;
       this.paginas = paginas;
       this.paginasRam = paginasRam;
       
       this.transcurrido = 0; 
       this.iniciado = false;
       this.termino_forzado = false;
       
       memCoordenadas = new ArrayList(); 
       disCoordenadas = new ArrayList(); 
      
    }
    @Override
    public void run() {
        estado = "Ejecutandose";
        tablaProcesos.setValueAt(estado,fila,2);
        
        for(int i = 0; i<memCoordenadas.size(); i++)
        {
            int[] c = memCoordenadas.get(i);
            memMatriz[c[0]][c[1]] = pid;
            tablaMemoria.setValueAt(""+pid+"",c[0],c[1]);
        }
        for(int i = 0; i<disCoordenadas.size(); i++)
        {
            int[] c = disCoordenadas.get(i);
            disMatriz[c[0]][c[1]] = pid;
            tablaDisco.setValueAt(""+pid+"",c[0],c[1]);
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
            System.out.println("Paginas en Ram ->"+ paginasRam);
            System.out.println("Paginas en Disco ->"+ (paginas-paginasRam));
            System.out.println("Coordenadas en ram ->"+ memCoordenadas.size());
            System.out.println("Coordenadas en disco ->"+ disCoordenadas.size());
        }
        estado = "Terminado";
        tablaProcesos.setValueAt(estado,fila,2);
        
        
        for(int i = 0; i<memCoordenadas.size(); i++)
        {
            int[] c = memCoordenadas.get(i);
            memMatriz[c[0]][c[1]] = -1;
            tablaMemoria.setValueAt("",c[0],c[1]);
        }
        for(int i = 0; i<disCoordenadas.size() ; i++)
        {
            int[] c = disCoordenadas.get(i);
            disMatriz[c[0]][c[1]] = -1;
            tablaDisco.setValueAt("", c[0],c[1]);
        }
        memBloquesDisponibles[0] += paginasRam;
        disBloquesDisponibles[0] += (paginas - paginasRam);
    }
    //Getters
    public int getFila(){
        return fila;
    }
    public int getPaginas(){
        return paginas;
    }
    public int getPaginasRam(){
        return paginasRam;
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
    public void addCoordenada(int x, int y ,boolean tipo)
    {
        int[] coordenada = new int[2];
        coordenada[0] = x;
        coordenada[1] = y;
        if(tipo)
            memCoordenadas.add(coordenada);
        else
            disCoordenadas.add(coordenada);
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
