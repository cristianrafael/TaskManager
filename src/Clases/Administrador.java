/*
 * Esta clase es el HILO PRINCIPAL(PADRE)
 * El proposito es administrar los demas hilos que se van creando
 */
package Clases;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Cristian
 */
public class Administrador extends Thread{
    
    javax.swing.JTable tabla; //La tabla donde se van a mostrar los estados de los procesos (La misma que se crea en Main.java)
    List<Proceso> procesos; //Arreglo de los procesos(hilos) que se van creando
    
    Generador generador; //Generador de procesos en automatico
    
    int proceso_actual,ultimo_proceso,cuanto,contador;
    boolean terminado;
    
    
    public Administrador(javax.swing.JTable tabla)
    {
        super("Main"); //Inicializamos el hilo con el nombre que este llevará
     
        this.tabla = tabla; //Seteamos la tabla que viene del main.java
        
        procesos = new ArrayList(); //Inicializamos el arreglo de procesos.
        
        generador = new Generador(procesos,tabla); //Inicializamos el generador de procesos automatizado 3000
        ultimo_proceso = proceso_actual = contador =  0;
        terminado = false;
        cuanto = 500000; //Tiempo entre conmutacion de procesos
    }
    @Override
    public void run(){
        generador.start();
        do{
            if(proceso_actual < procesos.size())
            {
                if(contador >= cuanto) //Cuando el tiempo asignado se rebasa
                {
                    System.out.println("Conmutacion");
                    contador = 0; //Vaciamos el contador
                    procesos.get(proceso_actual).setTurno(false); //Ponemos el proceso actual en espera
                    siguienteProceso();
                    //System.out.println("Hilo corriendo contador->" + proceso_actual+ " el size en este momento es ->"+ procesos.size());
                    procesos.get(proceso_actual).setTurno(true); //Ahora ponemos el siguiente proceso en marcha (Si no esta inciado se inicia y se empieza a ejecutar)

                }
                else if(procesos.get(proceso_actual).terminado()) //Si el tiempo no se ha concretado entonces vamos a checar si el proceso actual esta terminado
                {
                    if(proceso_actual == ultimo_proceso)
                            ultimo_proceso++;

                    contador = 0; //Vaciamos el contador
                    siguienteProceso(); //Aqui simplemente cambiamos el proceso actual por el siguiente
                    procesos.get(proceso_actual).setTurno(true);//Ahora iniciamos el siguiente proceso
                }
                else if(procesos.get(proceso_actual).pausado()) //Si el proceso aun no termina, pero esta pausado. Entonces solo cambiamos al que sigue.
                {
                    contador = 0; //Vaciamos el contador
                    siguienteProceso(); //Aqui simplemente cambiamos el proceso actual por el siguiente
                    procesos.get(proceso_actual).setTurno(true);//Ahora iniciamos el siguiente proceso
                }
                else
                    contador ++;
                
                
            }
            System.out.println("Hilo corriendo el contador es ->" + contador + " elementos -> " + procesos.size());
        }while(!terminado);     
    }
    public void siguienteProceso()
    {
        if(ultimo_proceso != procesos.size())
        {
            int proceso_prioritario = proceso_actual;
            int prioridad = 0;

            int tamaño = procesos.size();

            for(int i = ultimo_proceso ; i<tamaño; i++)
            {
                /*if(proceso_actual != tamaño -1)
                    proceso_actual++;
                else
                    proceso_actual = ultimo_proceso;*/

                if(!procesos.get(i).terminado() && i != proceso_actual)
                {
                    if(procesos.get(i).getPrioridad() > prioridad)
                    {
                        prioridad = procesos.get(i).getPrioridad();
                        proceso_prioritario = i;
                    }
                }
            }
            proceso_actual = proceso_prioritario;
        }
        System.out.println("Proceso actual->" + proceso_actual);
        
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
        if(row >= 0)
        {
            /*if(!procesos.get(row).terminado()) //Si el proceso que se desea terminar ya esta terminado pues para que hacer lo de adentro
            {
                if(row == 0) //Primer caso, si detenemos el primero
                {
                    if(procesos.size()>1) //Primer caso opcion A (Que exista mas de un proceso)
                        procesos.get(1).setProceso(null);
                    //Primer caso opcion B (Que no exista otro proceso ademas de ese, entonces no hacemos nada)
                }
                else if(row == (procesos.size()-1)) //Segundo caso, estamos terminando el ultimo proceso
                {
                    //Aqui practicamente no hariamos nada, puesto que no hay mas procesos
                }
                else //Tercer caso, el proceso tiene papa e hijo (ancestro y descendiente)
                {
                    //Practicamente vamos a heredarle el proceso ancestro al sucesor.
                    procesos.get(row+1).setProceso(procesos.get(row).getProceso());
                }
                
            }*/
            procesos.get(row).detener();
        }
        else
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un proceso");
    }
}
