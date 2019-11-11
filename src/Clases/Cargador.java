/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Cristian
 */
public class Cargador extends Thread{
    
    javax.swing.JProgressBar pilaConsumidor;
    javax.swing.JProgressBar pilaProductor;
    javax.swing.JLabel imagenConsumidorPausado;
    javax.swing.JLabel imagenProductorPausado;
    javax.swing.JLabel imagenCargador;
    javax.swing.JLabel imagenCajas;
    
    int derecha,izquierda;
    int velocidad;
    public Cargador(javax.swing.JProgressBar pilaConsumidor,
                    javax.swing.JProgressBar pilaProductor,
                    javax.swing.JLabel imagenConsumidorPausado,
                    javax.swing.JLabel imagenProductorPausado,
                    javax.swing.JLabel imagenCargador,
                    javax.swing.JLabel imagenCajas)
    {
        
       super("Cargador");
       this.pilaConsumidor = pilaConsumidor;
       this.pilaProductor = pilaProductor;
       this.imagenConsumidorPausado = imagenConsumidorPausado;
       this.imagenProductorPausado = imagenProductorPausado;
       this.imagenCargador = imagenCargador;
       this.imagenCajas = imagenCajas;
       
       this.derecha = this.imagenCargador.getLocation().x;
       this.izquierda = this.imagenCargador.getLocation().x - 400;
       this.velocidad = 1;
    }
    @Override
    public void run() {
        
        do{
            if(pilaProductor.getValue() > 0 && pilaConsumidor.getValue() < pilaConsumidor.getMaximum())
            {
                imagenCargador.setVisible(true);
                pilaProductor.setValue(pilaProductor.getValue() - 1);
                
                if(pilaProductor.getValue() == 0)
                    imagenCajas.setVisible(false);
                
                for(int n = derecha; n > izquierda; n-= velocidad)
                {
                    imagenCargador.setLocation(n,imagenCargador.getLocation().y);
                    try {
                        sleep(10);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Cargador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                pilaConsumidor.setValue(pilaConsumidor.getValue() + 1);
            }
            else
                imagenCargador.setVisible(false);
            System.out.println("Cargador");
            
        }
        while(true);        
    }
    public void setVelocidad(int velocidad){
        this.velocidad = velocidad;
    }
}
