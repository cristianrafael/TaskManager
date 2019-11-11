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
public class Productor extends Thread
{
    javax.swing.JProgressBar pilaProductor;
    javax.swing.JLabel imagenProductorPausado;
    javax.swing.JLabel imagenCajas;
    
    int velocidad;
    public Productor(javax.swing.JProgressBar pilaProductor,
                     javax.swing.JLabel imagenProductorPausado,
                     javax.swing.JLabel imagenCajas)
    {
        super("Productor");
        this.pilaProductor = pilaProductor;
        this.imagenProductorPausado = imagenProductorPausado;
        this.imagenCajas = imagenCajas;
        this.velocidad = 1000;
    }
    @Override
    public void run()
    {
        do{
            if(pilaProductor.getValue() < pilaProductor.getMaximum())
            {
                imagenProductorPausado.setVisible(false);
                try { sleep(velocidad); } catch (InterruptedException ex) { Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);}
                pilaProductor.setValue(pilaProductor.getValue() + 1);
                imagenCajas.setVisible(true);
            }
            else
                imagenProductorPausado.setVisible(true);
            
            System.out.println("Productor");
        }while(true);
    }
    public void setVelocidad(int velocidad)
    {
        this.velocidad = 1000/velocidad ;
    }
}
