/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Cristian
 */
public class Colores extends DefaultTableCellRenderer {
    
    int[][] paleta = {{192,192,192},{128,128,128},{64,64,64},{255,0,0},{255,175,175},{255,200,0},{255,255,0},{0,255,0},{255,0,255},{0,255,255},{0,0,255}};
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean Selected, boolean hasFocus, int row, int col){
        super.getTableCellRendererComponent(table,value,Selected,hasFocus,row,col);
        
        String codigo = table.getValueAt(row, col).toString();
        if(!codigo.equals(""))
        {
            int c = Integer.parseInt(codigo) % 11;
            
            setBackground(new Color(paleta[c][0],paleta[c][1],paleta[c][2]));  
        }
        else
            setBackground(Color.WHITE);
        return this;
    }
}