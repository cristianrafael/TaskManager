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
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Cristian
 */
public class Grafica extends Thread{
    java.awt.Panel panel;
    int numero = 1;
    private boolean terminado;
    
    int mem,pro,dis;
    int a,b,c;
    XYSeries series;
    XYSeriesCollection dataset;
    
    List<Proceso> procesos; //Arreglo de los procesos(hilos) que se van creando
    Random rand;
    public Grafica(List<Proceso> procesos, java.awt.Panel panel){
        super("Grafica");
        this.panel = panel;
        terminado = false;
        this.procesos = procesos;
        mem = pro = dis = 0;
        a = b = c = 0;
        rand = new Random();
        series = new XYSeries("Rendimiento");
        dataset = new XYSeriesCollection();
        
        series.add(0,0);
        dataset.addSeries(series);
        
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Rendimiento", // Título
                "Tiempo (milisegundos transcurridos)", // Etiqueta Coordenada X
                "Recursos de la PC (%)", // Etiqueta Coordenada Y
                dataset, // Datos
                PlotOrientation.VERTICAL,
                true, // Muestra la leyenda de los productos (Rendimiento)
                true,
                false
        );
        ChartPanel pan = new ChartPanel(chart);
        panel.setLayout(new java.awt.BorderLayout());
        panel.add(pan);
        panel.validate();
        
        
    }
    @Override
    public void run() {
        do{
                calcularMemoria();
            
        }while(!terminado);
       
    }
    public void calcularMemoria(){
        a = b = c = 0;
        for(int j=0; j<procesos.size(); j++)
        {
            if(procesos.get(j).getEstado().equals("Ejecutandose") || procesos.get(j).getEstado().equals("Pausado"))
            {
                a += procesos.get(j).getMemoria();
                b += procesos.get(j).getProcesador();
                c += procesos.get(j).getDisco();
            }
            //System.out.println("Memoria: "+mem+ ", pro: "+pro+", dis: "+dis);
        }
        mem = a;
        pro = b;
        dis = c;
        series.add(numero,(mem + pro + dis) / 3);
        numero++;
    }
    public int getMemoria(){
        return mem;
    }
    public int getProcesador(){
        return pro;
    }
    public int getDisco(){
        return dis;
    }
    public List<Proceso> getProcesos(){
        return procesos;
    }
}
