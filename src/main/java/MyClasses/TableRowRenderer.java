/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyClasses;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author wakan
 */
public class TableRowRenderer implements TableCellRenderer{
    
    public static final DefaultTableCellRenderer DEFAULT_RENDERER = 
            new DefaultTableCellRenderer();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        Component c = DEFAULT_RENDERER.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
        
        
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
