/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class Grafica extends Thread{
    int numero = 1;
    private boolean terminado;
    private int total;
    XYSeries series;
    XYSeriesCollection dataset;
    
    List<Proceso> procesos; //Arreglo de los procesos(hilos) que se van creando
    Random rand;
    public Grafica(List<Proceso> procesos){
        super("Grafica");
        terminado = false;
        this.procesos = procesos;
        total = 0;
        rand = new Random();
        series = new XYSeries("Rendimiento");
        dataset = new XYSeriesCollection();
        
        series.add(0,0);
        dataset.addSeries(series);
        
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Memoria RAM", // Título
                "Tiempo", // Etiqueta Coordenada X
                "Memoria", // Etiqueta Coordenada Y
                dataset, // Datos
                PlotOrientation.VERTICAL,
                true, // Muestra la leyenda de los productos (Rendimiento)
                true,
                false
        );
        ChartFrame frame = new ChartFrame("Ejemplo Grafica Lineal", chart);
        frame.pack();
        frame.setVisible(true);
        
    }
    @Override
    public void run() {
        do{
            try {
                calcularMemoria();
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Grafica.class.getName()).log(Level.SEVERE, null, ex);
            }
        }while(!terminado);
       
    }
    public void calcularMemoria(){
        total = 0;
        for(int i=0; i<procesos.size(); i++)
        {
            if(procesos.get(i).getEstado().equals("Ejecutandose"))
                total += procesos.get(i).getMemoria();
        }  
        series.add(numero,total);
        numero++;
    }
}
