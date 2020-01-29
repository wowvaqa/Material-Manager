/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyClasses;

import com.kprm.materialmanager.AtestManager;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Klasa nadpisuje renderer drzewka w celu zaznaczenia materiałów które nie 
 * mają pełnych danych w atestach.
 * @author wakan
 */
public class MyTreeRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus); //To change body of generated methods, choose Tools | Templates.

        if (AtestManager.getInstance().getMaterials() != null) {
            
            setForeground(Color.black);
            setBackground(Color.blue);
            
            /* Przeszukanie tablicy materiałów w celu wyznaczenia materiałów
                z brakami.
            */
            for (Material material: AtestManager.getInstance().getMaterials()){
                if (value.toString().equals(material.getName()) && material.getLack() > 0){
                    setForeground(Color.red);
                }
            }
        } else {

            setForeground(Color.black);
            setBackground(Color.blue);
        }

        if (sel) {
            setForeground(Color.white);
            setBackground(Color.blue);
        }

        return this;
    }

}
