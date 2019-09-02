/*
 * Esta clase es el HILO PRINCIPAL(PADRE)
 * El proposito es administrar los demas hilos que se van creando
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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Cristian
 */
public class Administrador extends Thread{
    
    javax.swing.JTable tabla; //La tabla donde se van a mostrar los estados de los procesos (La misma que se crea en Main.java)
    DefaultTableModel tablaModelo; //El modelo de la tabla (se usa para insertar datos)
    List<Proceso> procesos; //Arreglo de los procesos(hilos) que se van creando
    Random rand; //Variable que sirve para generar numeros aleatorios
    int proceso_actual;
    boolean terminado;
    //Variables para archivos
    File archivo; //Archivo para cargar los proceso
    FileReader lector; //Lector que recorre el archivo
    BufferedReader buffer; //buffer
    String linea,token; //La linea sirve para leer linea por linea el archivo, el token concatena los caracteres leidos para obtener una cadena
    int num_token; //El numero de token elige la posicion del arreglo para saber que token es.
    String[] fila = {"","","","",""};
    
    XYSeries series;
    XYSeriesCollection dataset;
    public Administrador(javax.swing.JTable tabla)
    {
        super("Main"); //Inicializamos el hilo con el nombre que este llevará
                
        this.tabla = tabla; //Seteamos la tabla que viene del main.java
        
        procesos = new ArrayList(); //Inicializamos el arreglo de procesos.
        
        tablaModelo = new DefaultTableModel(); //Aqui inicializamos el modelo de la tabla, seguido del nombre de sus columnas
        tablaModelo.addColumn("Nombre");
        tablaModelo.addColumn("PID");
        tablaModelo.addColumn("Estado");
        tablaModelo.addColumn("Tiempo transcurrido");
        tablaModelo.addColumn("Tiempo restante");
        tablaModelo.addColumn("Memoria");
        
        rand = new Random(); //Se inicializa la variable random
        tabla.setModel(tablaModelo); //Le asignamos a la tabla, el modelo que acabamos de inicializar.
        
        cargarArchivo();//Cargamos el archivo
        
        proceso_actual = 0;
        terminado = false;
        
        series = new XYSeries("Rendimiento");
        dataset = new XYSeriesCollection();
        
        series.add(1, 1);
        series.add(2, 6);
        series.add(3, 3);
        series.add(4, 10);
        
        dataset.addSeries(series);
        
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Ventas 2014", // Título
                "Tiempo (x)", // Etiqueta Coordenada X
                "Cantidad", // Etiqueta Coordenada Y
                dataset, // Datos
                PlotOrientation.VERTICAL,
                true, // Muestra la leyenda de los productos (Rendimiento)
                false,
                false
        );
        ChartFrame frame = new ChartFrame("Ejemplo Grafica Lineal", chart);
        frame.pack();
        frame.setVisible(true);
    }
    @Override
    public void run(){
        procesos.get(proceso_actual).start();
        do{
            if(procesos.get(proceso_actual).terminado() == true)
            {
                System.out.println(procesos.get(proceso_actual).terminado());
                if(proceso_actual < procesos.size())
                {
                    proceso_actual++;
                    procesos.get(proceso_actual).start();
                    
             
                    int numero = rand.nextInt(10) + 1;
                    int numero2 = rand.nextInt(15)+ 2;
                    int numero3 = rand.nextInt(10) +3;
                    String[] row = {"Usuario",""+numero2+"","En espera...", "0 seg",""+numero+" seg", ""+numero3+"KB"};
                    tablaModelo.addRow(row);
                    Proceso task = new Proceso("Usuario",tabla,tabla.getRowCount()-1, numero2);
                    procesos.add(task);
                }
                else
                    terminado = true;
            }
            series.add(rand.nextInt(10), rand.nextInt(10));
            
            System.out.println(procesos.get(proceso_actual).terminado());
        }while(!terminado);
    }
    private void cargarArchivo(){
        
        archivo = new File("procesos.txt");
        if(archivo != null)
            try {
                lector = new FileReader(archivo);
                buffer = new BufferedReader(lector);
                linea = buffer.readLine();
                while(linea != null) //Mientras la linea actual no sea nula
                {   
                    //System.out.println(linea);
                    num_token = 0; //Incializamos con el primer token
                    token = ""; //Vaciamos el token
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
                                
                    System.out.println(" " + fila[0] + " " + fila[1] + " " + fila[2] + " " + fila[3] + " " + fila[4]);
                    String[] row = {fila[0], fila[1], "En espera...", "0 seg", ""+fila[2]+" seg", ""+fila[3]+" "+fila[4]};
                    tablaModelo.addRow(row);
                    Proceso task = new Proceso(fila[0],tabla,tabla.getRowCount()-1, Integer.parseInt(fila[2]));
                    procesos.add(task);
                    //task.start();
                    linea = buffer.readLine();
                }                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
            }
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
