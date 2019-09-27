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
    javax.swing.JLabel cpu; //Etiqueta
            
    List<Proceso> procesos_criticos; //Cola 1 cuantos 4
    List<Proceso> procesos_usuario; //Cola 2 cuantos 2
    List<Proceso> procesos_demonios; //Cola 3 cuantos 1
    
    List<List> colas; //Lista
    
    
    Generador generador; //Generador de procesos en automatico
    
    boolean terminado; //Iteracion infinita hasta que se indique lo contrario
    
    //Variables para la cola en general
    int cola_actual = 0;
    int cola_contador = 0;
    
    //Variables para la cola de procesos criticos
    int criticos_actual = 0;
    int criticos_contador = 0;
    int criticos_ultimo = 0;
    
    //Variables para la cola de procesos de usuario
    int usuario_actual = 0;
    int usuario_contador = 0;
    int usuario_ultimo = 0;
    
    //Variables para la cola de procesos demonios
    int demonios_actual = 0;
    int demonios_contador = 0;
    int demonios_ultimo = 0;
    
    public Administrador(javax.swing.JTable tabla , javax.swing.JLabel cpu)
    {
        super("Main"); //Inicializamos el hilo con el nombre que este llevará
     
        this.tabla = tabla; //Seteamos la tabla que viene del main.java
        this.cpu = cpu;
        
        procesos_criticos = new ArrayList();
        procesos_usuario = new ArrayList();
        procesos_demonios = new ArrayList();
        
        colas = new ArrayList();
        
        colas.add(procesos_criticos);
        colas.add(procesos_usuario);
        colas.add(procesos_demonios);
        
        generador = new Generador(colas,tabla); //Inicializamos el generador de procesos automatizado 3000
        terminado = false;
    }
    @Override
    public void run(){
        generador.start();
        do
        {
            System.out.println("Hilo corriendo el contador es ->" + cola_contador);
            switch(cola_actual)
            {
                case 0:
                    if(cola_contador >= 40000)
                    {
                        cpu.setText("Cola activa -> Procesos de usuario");
                        cola_actual = 1;
                        cola_contador = criticos_contador = 0;
                        
                        if(procesos_criticos.size() > 0)
                            procesos_criticos.get(criticos_actual).setTurno(false); //Ahora ponemos el siguiente proceso en marcha (Si no esta inciado se inicia y se empieza a ejecutar)
                    }
                    else if(procesos_criticos.size() > 0)
                    {
                        System.out.println("Cola criticos Proceso actual ->"+ criticos_actual + " Contador->"+criticos_contador);
                        if(criticos_actual < procesos_criticos.size())
                        {
                            
                            if(cola_contador == 0 && criticos_contador == 0)
                                procesos_criticos.get(criticos_actual).setTurno(true); //Ahora ponemos el siguiente proceso en marcha (Si no esta inciado se inicia y se empieza a ejecutar)
                        
                            if(criticos_contador >= 1000) //Cuando el tiempo asignado se rebasa
                            {
                                criticos_contador = 0; //Vaciamos el contador
                                procesos_criticos.get(criticos_actual).setTurno(false); //Ponemos el proceso actual en espera
                                siguienteProcesoCriticos();
                                procesos_criticos.get(criticos_actual).setTurno(true); //Ahora ponemos el siguiente proceso en marcha (Si no esta inciado se inicia y se empieza a ejecutar)

                            }
                            else if(procesos_criticos.get(criticos_actual).terminado()) //Si el tiempo no se ha concretado entonces vamos a checar si el proceso actual esta terminado
                            {
                                if(criticos_actual == criticos_ultimo)
                                    criticos_ultimo++;

                                criticos_contador = 0; //Vaciamos el contador
                                siguienteProcesoCriticos(); //Aqui simplemente cambiamos el proceso actual por el siguiente
                                procesos_criticos.get(criticos_actual).setTurno(true);//Ahora iniciamos el siguiente proceso
                            }
                            else if(procesos_criticos.get(criticos_actual).pausado()) //Si el proceso aun no termina, pero esta pausado. Entonces solo cambiamos al que sigue.
                            {
                                criticos_contador = 0; //Vaciamos el contador
                                siguienteProcesoCriticos(); //Aqui simplemente cambiamos el proceso actual por el siguiente
                                procesos_criticos.get(criticos_actual).setTurno(true);//Ahora iniciamos el siguiente proceso
                            }
                            else
                                criticos_contador ++;
                        }
                    }
                break;
                    
                case 1:
                    if(cola_contador >= 20000)
                    {
                        cpu.setText("Cola activa -> Procesos demonios");
                        cola_actual = 2;
                        cola_contador = 1;
                        
                        if(procesos_usuario.size() > 0)
                            procesos_usuario.get(usuario_actual).setTurno(false); //Ahora ponemos el siguiente proceso en marcha (Si no esta inciado se inicia y se empieza a ejecutar)
                    }
                    else if(procesos_usuario.size() > 0)
                    {
                        System.out.println("Cola usuario Proceso actual ->"+ usuario_actual + " Contador->"+usuario_contador);
                        if(usuario_actual < procesos_usuario.size())
                        {
                            
                            if(cola_contador == 0 && usuario_contador == 0)
                                procesos_usuario.get(usuario_actual).setTurno(true); //Ahora ponemos el siguiente proceso en marcha (Si no esta inciado se inicia y se empieza a ejecutar)
                            
                            if(usuario_contador >= 1000) //Cuando el tiempo asignado se rebasa
                            {
                                usuario_contador = 0; //Vaciamos el contador
                                procesos_usuario.get(usuario_actual).setTurno(false); //Ponemos el proceso actual en espera
                                siguienteProcesoUsuario();
                                procesos_usuario.get(usuario_actual).setTurno(true); //Ahora ponemos el siguiente proceso en marcha (Si no esta inciado se inicia y se empieza a ejecutar)

                            }
                            else if(procesos_usuario.get(usuario_actual).terminado()) //Si el tiempo no se ha concretado entonces vamos a checar si el proceso actual esta terminado
                            {
                                if(usuario_actual == usuario_ultimo)
                                    usuario_ultimo++;

                                usuario_contador = 0; //Vaciamos el contador
                                siguienteProcesoUsuario(); //Aqui simplemente cambiamos el proceso actual por el siguiente
                                procesos_usuario.get(usuario_actual).setTurno(true);//Ahora iniciamos el siguiente proceso
                            }
                            else if(procesos_usuario.get(usuario_actual).pausado()) //Si el proceso aun no termina, pero esta pausado. Entonces solo cambiamos al que sigue.
                            {
                                usuario_contador = 0; //Vaciamos el contador
                                siguienteProcesoUsuario(); //Aqui simplemente cambiamos el proceso actual por el siguiente
                                procesos_usuario.get(usuario_actual).setTurno(true);//Ahora iniciamos el siguiente proceso
                            }
                            else
                                usuario_contador ++;
                        }
                    }
                break;
                    
                case 2:
                    if(cola_contador >= 10000)
                    {
                        cpu.setText("Cola activa -> Procesos criticos");
                        cola_actual = cola_contador = 0;
                        if(procesos_demonios.size() > 0)
                            procesos_demonios.get(demonios_actual).setTurno(false); //Ahora ponemos el siguiente proceso en marcha (Si no esta inciado se inicia y se empieza a ejecutar)
                    }
                    else if(procesos_demonios.size() > 0)
                    {
                        System.out.println("Cola demonios Proceso actual ->"+ demonios_actual + " Contador->"+demonios_contador);
                        if(demonios_actual < procesos_demonios.size())
                        {
                            
                            if(cola_contador == 0 && demonios_contador == 0)
                                procesos_demonios.get(demonios_actual).setTurno(true); //Ahora ponemos el siguiente proceso en marcha (Si no esta inciado se inicia y se empieza a ejecutar)
                            
                            if(demonios_contador >= 1000) //Cuando el tiempo asignado se rebasa
                            {
                                demonios_contador = 0; //Vaciamos el contador
                                procesos_demonios.get(demonios_actual).setTurno(false); //Ponemos el proceso actual en espera
                                siguienteProcesoDemonios();
                                procesos_demonios.get(demonios_actual).setTurno(true); //Ahora ponemos el siguiente proceso en marcha (Si no esta inciado se inicia y se empieza a ejecutar)

                            }
                            else if(procesos_demonios.get(demonios_actual).terminado()) //Si el tiempo no se ha concretado entonces vamos a checar si el proceso actual esta terminado
                            {
                                if(demonios_actual == demonios_ultimo)
                                    demonios_ultimo++;

                                demonios_contador = 0; //Vaciamos el contador
                                siguienteProcesoDemonios(); //Aqui simplemente cambiamos el proceso actual por el siguiente
                                procesos_demonios.get(demonios_actual).setTurno(true);//Ahora iniciamos el siguiente proceso
                            }
                            else if(procesos_demonios.get(demonios_actual).pausado()) //Si el proceso aun no termina, pero esta pausado. Entonces solo cambiamos al que sigue.
                            {
                                demonios_contador = 0; //Vaciamos el contador
                                siguienteProcesoDemonios(); //Aqui simplemente cambiamos el proceso actual por el siguiente
                                procesos_demonios.get(demonios_actual).setTurno(true);//Ahora iniciamos el siguiente proceso
                            }
                            else
                                demonios_contador ++;
                        }
                    }
                break;
                    
                default:
                break;
            }
            cola_contador++;
        }
        while(!terminado);       
    }
    public void siguienteProcesoCriticos(){
        if(criticos_actual != procesos_criticos.size()-1)
            criticos_actual++;
        else
            criticos_actual = criticos_ultimo;
    }
    public void siguienteProcesoUsuario(){
        if(usuario_actual != procesos_usuario.size()-1)
            usuario_actual++;
        else
            usuario_actual = usuario_ultimo;
    }
    public void siguienteProcesoDemonios(){
        if(demonios_actual != procesos_demonios.size()-1)
            demonios_actual++;
        else
            demonios_actual = demonios_ultimo;
    }
    public void pausarReanudarProceso(){
        /*int row = tabla.getSelectedRow();
        if(row != -1)
            procesos.get(row).pausar_reanudar();
        else
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un proceso");*/
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
            //procesos.get(row).detener();
        }
        else
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un proceso");
    }
}
