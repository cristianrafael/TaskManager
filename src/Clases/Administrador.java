/*
 * Esta clase es el HILO PRINCIPAL(PADRE)
 * El proposito es administrar los demas hilos que se van creando
 */
package Clases;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Cristian
 */
public class Administrador extends Thread{
    
    javax.swing.JTable tabla; //La tabla donde se van a mostrar los estados de los procesos (La misma que se crea en Main.java)
    List<Proceso> procesos; //Productores y Lectores
        
    Generador generador; //Generador de procesos en automatico
    boolean terminado; //Iteracion infinita hasta que se indique lo contrario
    
    int proceso_actual;
    int recurso; //0 para disponible, 1 para leyendo, 2 para escribiendo
    int [] lectores_activos = new int [1]; //Contador de lectores activos
    public Administrador(javax.swing.JTable tabla)
    {
        super("Main"); //Inicializamos el hilo con el nombre que este llevará
     
        this.tabla = tabla; //Seteamos la tabla que viene del main.java
        procesos = new ArrayList();
        lectores_activos[0] = 0;
        generador = new Generador(procesos,tabla,lectores_activos); //Inicializamos el generador de procesos automatizado 3000
        terminado = false;
        proceso_actual = recurso = 0;
    }
    @Override
    public void run(){
        generador.start();
        do
        {   
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.print("Estado -> ");
            if(procesos.size() > proceso_actual)
            {
                if(!procesos.get(proceso_actual).iniciado())
                {
                    boolean tipo = procesos.get(proceso_actual).getTipo();
                    switch(recurso)
                    {
                        case 0: //Disponible
                            procesos.get(proceso_actual).iniciar(); //Como esta desocupado activamos el proceso sin importar lo que sea
                            if(tipo) //Si es escritor
                                recurso = 2; //Indicamos que se esta escribiendo
                            else
                            {
                                recurso = 1; //Si es lector indicamos leyendo
                                proceso_actual++; //Pasamos al siguiente proceso para ver si es otro lector
                            }
                        break;
                        case 1: //Leyendo
                            if(!tipo)
                            {
                                procesos.get(proceso_actual).iniciar(); //Como esta desocupado activamos el proceso sin importar lo que sea
                                proceso_actual++; //Avanzamos al siguiente proceso
                            }
                            else
                            {
                                if(lectores_activos[0] >0) //Mientras siga habiendo lectores, el escritor no puede hacer otra cosa mas que esperar
                                    System.out.print("Esperando a lector(es)");
                                else
                                {
                                   procesos.get(proceso_actual).iniciar();
                                   recurso = 2; //Escribiendo en el recurso
                                }  
                            }
                        break;
                        case 2: //Escribiendo
                            //Te esperas wey, hasta que el escritor acabe (No me importa quien seas)
                            System.out.print("Esperando a escritor");
                        break;
                           
                    }
                }
                else if(procesos.get(proceso_actual).terminado())
                {
                    proceso_actual++;
                    recurso = 0;
                }
            }
            
        }
        while(!terminado);       
    }
    public void pausarReanudarProceso(){
        int row = tabla.getSelectedRow();
        if(row != -1)
            procesos.get(row).pausar_reanudar();
        else
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un proceso");
    }
    public void detenerProceso(){
        int row = tabla.getSelectedRow();
        if(row != -1)
            procesos.get(row).detener();
        else
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un proceso");
    }
}
