/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cristian
 */
public class Consumidor extends Thread{
    
    javax.swing.JProgressBar pilaConsumidor;
    javax.swing.JLabel imagenConsumidorPausado;
    int velocidad;                
    public Consumidor(javax.swing.JProgressBar pilaConsumidor,
                      javax.swing.JLabel imagenConsumidorPausado)
    {
        super("Consumidor");
        this.pilaConsumidor = pilaConsumidor;
        this.imagenConsumidorPausado = imagenConsumidorPausado;
        this.velocidad = 1000;
    }
    @Override
    public void run()
    {
        do{
            if(pilaConsumidor.getValue() > 0)
            {
                imagenConsumidorPausado.setVisible(false);
                pilaConsumidor.setValue(pilaConsumidor.getValue() - 1);
                try { sleep(velocidad); } catch (InterruptedException ex) { Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);}
            }
            else
                imagenConsumidorPausado.setVisible(true);
            
           System.out.println("Consumidor");

        }while(true);
    }
    public void setVelocidad(int velocidad)
    {
        this.velocidad = 1000/velocidad ;
    }
}
