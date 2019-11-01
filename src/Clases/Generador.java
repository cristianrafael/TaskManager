/*
 * Esta clase se creo con el proposito de crear procesos de manera automatica.
 */
package Clases;

import Main.Inicio;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Cristian
 */
public class Generador extends Thread{
    
    javax.swing.JTable tablaProcesos, tablaMemoria, tablaDisco; //Tablas
    DefaultTableModel modeloProcesos, modeloMemoria, modeloDisco; //Modelos
    
    int[][] memMatriz,disMatriz;//Matrices de los marcos de memoria
    int[] memBloquesDisponibles,disBloquesDisponibles; //Bloques disponibles para almacenar los procesos en ram o disco
    
    
    List<Proceso> procesos; //Arreglo de los procesos(hilos) que se van creando
    boolean terminado; //Bandera que termina el while del run
    
    //Variables para archivos
    List<String[]> arreglo;
    String[] unidad = {"","","","",""};
    Random rand; //Variable que sirve para generar numeros aleatorios
    
    Colores colores;
    public Generador(List<Proceso> procesos, javax.swing.JTable tablaProcesos, javax.swing.JTable tablaMemoria, javax.swing.JTable tablaDisco, int[] memBloquesDisponibles, int[] disBloquesDisponibles, int[][] memMatriz, int[][] disMatriz)
    {
        this.procesos = procesos;
        this.tablaProcesos = tablaProcesos;
        this.tablaMemoria = tablaMemoria;
        this.tablaDisco = tablaDisco;
        this.memBloquesDisponibles = memBloquesDisponibles;
        this.disBloquesDisponibles = disBloquesDisponibles;
        this.memMatriz = memMatriz;
        this.disMatriz = disMatriz;
               
        modeloProcesos = new DefaultTableModel(); //Aqui inicializamos el modelo de la tabla, seguido del nombre de sus columnas
        modeloProcesos.addColumn("Nombre");
        modeloProcesos.addColumn("PID");
        modeloProcesos.addColumn("Estado");
        modeloProcesos.addColumn("Tiempo transcurrido");
        modeloProcesos.addColumn("Tiempo restante");
        modeloProcesos.addColumn("Memoria");
        tablaProcesos.setModel(modeloProcesos); //Le asignamos a la tabla, el modelo que acabamos de inicializar.
        
        modeloMemoria = new DefaultTableModel(); //Aqui inicializamos el modelo de la tabla, seguid del nombre de sus columnas
        modeloMemoria.addColumn(" ");
        modeloMemoria.addColumn(" ");
        modeloMemoria.addColumn(" ");
        modeloMemoria.addColumn(" ");
        tablaMemoria.setModel(modeloMemoria); //Le asignamos a la tabla, el modelo que acabamos de inicializar
        
        modeloDisco = new DefaultTableModel();
        modeloDisco.addColumn(" ");
        modeloDisco.addColumn(" ");
        modeloDisco.addColumn(" ");
        modeloDisco.addColumn(" ");
        modeloDisco.addColumn(" ");
        modeloDisco.addColumn(" ");
        modeloDisco.addColumn(" ");
        modeloDisco.addColumn(" ");
        tablaDisco.setModel(modeloDisco);
        
        //Colocamos la instancia de colores a las dos tablas
        colores = new Colores();
        tablaMemoria.setDefaultRenderer(Object.class, colores);
        tablaDisco.setDefaultRenderer(Object.class, colores);
        
        //Seteamos las tablas en blanco
        for(int i = 0; i<20 ; i++)
        {
            String[] memRow = {"","","",""};
            modeloMemoria.addRow(memRow);
            
            String[] disRow = {"","","","","","","",""};
            modeloDisco.addRow(disRow);
        }
        
        rand = new Random(); //Se inicializa la variable random
        arreglo = new ArrayList(); //Arreglo de cadenas para generar procesos aleatorios(proviene del archivo procesos.txt)
        cargarArchivo();//Cargamos el archivo
    }
    @Override
    public void run()
    {
        do{
            //Agregamos un proceso aleatorio del arreglo de procesos
            unidad = arreglo.get(rand.nextInt(arreglo.size())); 

            //Estructuramos nuestra fila para la tabla con ese proceso aleatorio
            String[] row = {unidad[0],unidad[1],"Creado","0 seg",""+ unidad[2] +" seg", ""+ unidad[3] + " " + unidad[4]};

            //Agregamos la nueva fila a la tabla de procesos
            modeloProcesos.addRow(row);
            
            int pags = Integer.parseInt(unidad[3])/100;
            
            int pagsRam = pags;
            switch(pags)
            {
                case 1: pagsRam = 1; break;
                case 2: pagsRam = 1; break;
                case 3: pagsRam = 1; break;
                case 4: pagsRam = 2; break;
                case 5: pagsRam = 2; break;
                case 6: pagsRam = 2; break;
                case 7: pagsRam = 2; break;
                case 8: pagsRam = 2; break;
                case 9: pagsRam = 2; break;
                case 10:pagsRam = 2; break;
            }
            
            
            
            
            
            //Ahora vamos a crear el proceso como tal con la misma informacion de arriba
            Proceso task = new Proceso(unidad[0], //Nombre del proceso
                                       tablaProcesos, //Tabla de los estados del proceso
                                       tablaMemoria, //Tabla de la memoria RAM
                                       tablaDisco, //Tabla del disco duro
                                       
                                       //Arreglo de valores para los bloques que siguen desocupados
                                       memBloquesDisponibles,
                                       disBloquesDisponibles,
                                       
                                       //Matrices de los valores
                                       memMatriz,
                                       disMatriz,
                    
                                       tablaProcesos.getRowCount()-1, //Fila correspondiente a la tabla
                                       Integer.parseInt(unidad[1]), //PID o identificador del proceso
                                       Integer.parseInt(unidad[2]), //Tiempo que el proceso tiene que permanecer activo para terminarse
                                       pags, //Paginas o marcos que abarcará el proceso cuando se ejecute
                                       pagsRam
                                    );

            //Agregamos el proceso creado a la lista de procesos
            procesos.add(task);
            
            //Dormimos el hilo por un tiempo para crear el siguiente proceso en la lista
            try{sleep(200);}
            catch (InterruptedException ex) { Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);}
            
            System.out.println("Hilo corriendo");
            
        }while(!terminado);
    }
    private void cargarArchivo(){
        
        int num_token; //El numero de token elige la posicion del arreglo para saber que token es.
        String token; //el token concatena los caracteres leidos para obtener una cadena
        File archivo = new File("procesos.txt");//Archivo para cargar los proceso
        
        try {
            FileReader lector = new FileReader(archivo); //Lector que recorre el archivo
            BufferedReader buffer = new BufferedReader(lector); //buffer
            String linea = buffer.readLine(); //La linea sirve para leer linea por linea el archivo, 
            while(linea != null) //Mientras la linea actual no sea nula
            {   
                num_token = 0; //Incializamos con el primer token
                token = ""; //Vaciamos el token

                String[] fila = {"","","","",""};

                for(int i=0; i< linea.length(); i++)
                {
                    if(linea.charAt(i) == ' ')//Si el caracter leido es un espacio en blanco
                    {
                        if(!token.equals(""))//Si el token contiene algo
                        {
                            fila[num_token] = token;//Verificamos que numero de token es para saber que parametro estamos concatenando
                            token = "";
                            num_token++;
                        }
                    } 
                    else
                        token += linea.charAt(i);
                }
                fila[num_token] = token;//Agregamos el ultimo token ( Como no hay espacio tiene que ser el ultimo)

                arreglo.add(fila);

                linea = buffer.readLine();
            }                
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
