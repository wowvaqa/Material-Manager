/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ControlCard;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer do koloraowania co drugiego wiersza w tabeli typów elastomerów oraz
 * tabeli rejestru łożysk.
 *
 * @author Łuaksz Wawrzyniak
 */
public class KKTableRenderer extends DefaultTableCellRenderer {

  static final Color FUNDO = Color.white;
  static final Color AZUL = new Color(240, 245, 255);
  //static final Color AZUL = new Color(225, 235, 255);

  @Override
  public Component getTableCellRendererComponent(
          JTable table,
          java.lang.Object value,
          boolean isSelected,
          boolean hasFocus,
          int row,
          int column) {

    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if (table.getModel().getValueAt(row, 0).equals("Pierrot le fou")) {
      setBackground(Color.yellow);
    } else {
      setForeground(Color.black);
      if (!isSelected) {
        if ((row % 2) == 0 && FUNDO.getRed() > 20 && FUNDO.getGreen() > 20 && FUNDO.getBlue() > 20) {
          setBackground(AZUL);
        } else {
          setBackground(FUNDO);
        }
      }
    }
    return this;
  }
}
