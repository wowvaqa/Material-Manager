/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ControlCard;

import com.kprm.materialmanager.DatabaseManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author wakan
 */
public class ElastomerTypeManager {

  private ElastomerTypeManager() {
  }

  public static ElastomerTypeManager getInstance() {
    return ElastomerTypeManagerHolder.INSTANCE;
  }

  private static class ElastomerTypeManagerHolder {

    private static final ElastomerTypeManager INSTANCE = new ElastomerTypeManager();
  }

  /**
   * Dodaje nowy type elastomeru
   *
   * @param type Typ elastomeru (MN)
   * @param deDimension Średnica wkładu elastomerowego
   * @param teDimension Wysokość wkładu elastomerowego
   */
  public void addNewElastomerType(float type, float deDimension, float teDimension) {
    DatabaseManager.getInstance().addNewElastomerType(
            Float.toString(type),
            Float.toString(deDimension),
            Float.toString(teDimension));
  }

  /**
   * Odświeża tabelę typów wkłądów elastomerowych.
   *
   * @param tblElastomerTypes Tabela przchowująca typy elastomerów
   * @throws java.sql.SQLException
   */
  public void refreshElastomerTypeTable(JTable tblElastomerTypes) throws SQLException {

    DefaultTableModel model = (DefaultTableModel) tblElastomerTypes.getModel();
    model.setRowCount(0);

    // Dane wiersza w tabeli
    Object rowData[] = new Object[3];

    ResultSet resultSet = DatabaseManager.getInstance().getElastomerTypes();

    if (resultSet != null) {

      resultSet.first();

      do {
        rowData[0] = resultSet.getString("type");
        rowData[1] = resultSet.getString("de");
        rowData[2] = resultSet.getString("te");
        model.addRow(rowData);

      } while (resultSet.next());
    }
  }

  /**
   * Usuwa zadany typ wkładu elastomerowego
   *
   * @param tblElastomerInsertDimension Tabela z typami elastomerów
   */
  public void removeElastomerType(JTable tblElastomerInsertDimension) {
    try {
      String type = (String) tblElastomerInsertDimension.getValueAt(tblElastomerInsertDimension.getSelectedRow(), 0);
      DatabaseManager.getInstance().removeElastomerType(type);
    } catch (IndexOutOfBoundsException ex){
      JOptionPane.showMessageDialog(null, "Zaznacz type do usunięcia", "Błąd", JOptionPane.ERROR_MESSAGE);
    }
  }
}
