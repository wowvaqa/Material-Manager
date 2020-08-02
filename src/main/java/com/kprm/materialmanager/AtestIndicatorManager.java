package com.kprm.materialmanager;

import MyClasses.Atest;
import MyClasses.MmComparators;
import MyClasses.SortModes;
import java.awt.HeadlessException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Zarządza przyporządkowaniem atestów.
 *
 * @author Łukasz Wawrzyniak
 */
public class AtestIndicatorManager {

  // Tablica przechowująca atesty.
  //private Atest[] atesty;
  private ArrayList<Atest> atesty;

  public void sortCerts(JTable tblAtesty, SortModes mode) {
    DefaultTableModel model = (DefaultTableModel) tblAtesty.getModel();
    ((DefaultTableModel) tblAtesty.getModel()).setRowCount(0);

    if (atesty != null) {
      switch (mode) {
        case ATEST_NAZWA_UP:
          Collections.sort(atesty, MmComparators.atestNameUpComparator);
          break;
        case ATEST_NAZWA_DOWN:
          Collections.sort(atesty, MmComparators.atestNameDownComparator);
          break;
        case ATEST_NAZWA_MATERIALU_UP:
          Collections.sort(atesty, MmComparators.atestMaterialNameUpComparator);
          break;
        case ATEST_NAZWA_MATERIALU_DOWN:
          Collections.sort(atesty, MmComparators.atestMaterialNameDownComparator);
          break;
        case ATEST_DATA_UP:
          Collections.sort(atesty, MmComparators.atestDateUpComparator);
          break;
        case ATEST_DATA_DOWN:
          Collections.sort(atesty, MmComparators.atestDateDownComparator);
          break;
        case ATEST_NRZAM_UP:
          Collections.sort(atesty, MmComparators.atestNrZamowieniaUpComparator);
          break;
        case ATEST_NRZAM_DOWN:
          Collections.sort(atesty, MmComparators.atestNrZamowieniaDownComparator);
          break;
        case ATEST_WZ_UP:
          Collections.sort(atesty, MmComparators.atestWzUpComparator);
          break;
        case ATEST_WZ_DOWN:
          Collections.sort(atesty, MmComparators.atestWzDownComparator);
          break;
      }

      // Dane wiersza w tabeli
      Object rowData[] = new Object[5];

      int tableIndex = 0;

      for (Atest atest : atesty) {
        rowData[0] = atest.getNazwa();
        rowData[1] = atest.getNazwaMaterialu();
        rowData[2] = atest.getWz();
        rowData[3] = atest.getNr_zamowienia();
        rowData[4] = atest.getDate();
        atest.setPositionInTable(tableIndex);
        model.addRow(rowData);
        tableIndex += 1;
      }
    }
  }

  /**
   * Wyszukuje atestu wg nazwy i rodzaju materiału.
   *
   * @param nazwaAtestu Nazwa atestu.
   * @param nazwaMaterialu Nazwa materiału.
   * @param outputTable Tabela z wynikami.
   */
  public void searchCert(String nazwaAtestu, String nazwaMaterialu,
          JTable outputTable) {

    try {
      DefaultTableModel model = (DefaultTableModel) outputTable.getModel();
      model.setRowCount(0);

      /* Sprawdza czy wyszukanie ma nastąpić tylko po nazwie atestu. */
      if (nazwaAtestu.trim().length() > 0 && nazwaMaterialu.trim().length() < 1) {
        searchByNameOfCert(model, nazwaAtestu);
        /* Sprawdzenie czy wyszukanie ma nastąpić tylko po nazwie materiału. */
      } else if (nazwaAtestu.trim().length() < 1 && nazwaMaterialu.trim().length() > 0) {
        searchByNameOfMaterial(model, nazwaMaterialu);
      }
    } catch (Exception e) {
      Logger.getLogger(AtestIndicatorManager.class.getName()).log(Level.SEVERE, null, e);
      //JOptionPane.showMessageDialog(null, e);
    }
  }

  /**
   * Wyszukuje atest tylko po nazwie.
   *
   * @param model Tabela do której zapisane będą wyniki.
   * @param nazwaAtestu Nazwa atestu.
   */
  private void searchByNameOfCert(DefaultTableModel model, String nazwaAtestu) {
    ResultSet resultSet = DatabaseManager.getInstance().searchCerts(nazwaAtestu, null);

    try {
      int sizeOfResultSet = DatabaseManager.getInstance().getSizeOfResuleSet(resultSet);

      if (sizeOfResultSet > 0) {

        resultSet.first();

        atesty = new ArrayList<>();

        int tableIndex = 0;

        do {
          Object[] rowData = new Object[5];

          atesty.add(new Atest());
          atesty.get(tableIndex).setNazwa(resultSet.getString("nazwa"));
          atesty.get(tableIndex).setNazwaMaterialu(
                  DatabaseManager.getInstance().getNameOfMaterial(resultSet.getInt("id_materialu"))
          );
          atesty.get(tableIndex).setId(resultSet.getInt("id"));
          atesty.get(tableIndex).setIdMaterialu(resultSet.getInt("id_materialu"));
          atesty.get(tableIndex).setDate(resultSet.getString("data_dodania"));
          atesty.get(tableIndex).setWz(resultSet.getString("nr_wz"));
          atesty.get(tableIndex).setNr_zamowienia(resultSet.getString("nr_zamowienia"));
          atesty.get(tableIndex).setPositionInTable(tableIndex);

          rowData[0] = atesty.get(tableIndex).getNazwa();
          rowData[1] = atesty.get(tableIndex).getNazwaMaterialu();
          rowData[2] = atesty.get(tableIndex).getWz();
          rowData[3] = atesty.get(tableIndex).getNr_zamowienia();
          rowData[4] = atesty.get(tableIndex).getDate();

          model.addRow(rowData);
          tableIndex += 1;

        } while (resultSet.next());
      }

    } catch (SQLException ex) {
      Logger.getLogger(AtestIndicatorManager.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Wyszukanie atestów na podstawie nazwy materiału.
   *
   * @param model Tabela do której dodane zostaną wyniki
   * @param nazwaMaterialu Nazwa materiału którego atesty mają zostać odszukane.
   */
  private void searchByNameOfMaterial(DefaultTableModel model, String nazwaMaterialu) {

    /* RS zawierający pasujące do parametru materiały */
    ResultSet resultSet = DatabaseManager.getInstance().searchCerts(null, nazwaMaterialu);

    try {
      int sizeOfResultSet = DatabaseManager.getInstance().getSizeOfResuleSet(resultSet);

      if (sizeOfResultSet > 0) {
        resultSet.first();

        /* Tablica główna przechowująca znalezione atesty */
        //atesty = new Atest[0];
        atesty = new ArrayList<>();

        /* Dla każdego materiału z RS następuje wyszukanie wszystkich dostępnych atestów. */
        do {
          ResultSet resultSet1 = DatabaseManager.getInstance().searchCertByMaterialID(
                  resultSet.getInt("id"));

          int sizeOfResultSet1 = DatabaseManager.getInstance().getSizeOfResuleSet(resultSet1);

          try {
            if (sizeOfResultSet1 > 0) {

              resultSet1.first();

              /* Tymczasowa tablica zawierająca znalezione atesty wg
                            aktualnie zadanego materiału
               */
              //Atest[] atestyTmp = new Atest[sizeOfResultSet1];
              ArrayList<Atest> atestyTmp = new ArrayList<>();

              int tableIndex = 0;

              do {

                Object[] rowData = new Object[5];

                rowData[0] = resultSet1.getString("nazwa");
                rowData[1] = DatabaseManager.getInstance().getNameOfMaterial(resultSet1.getInt("id_materialu"));
                rowData[2] = resultSet1.getString("nr_wz");
                rowData[3] = resultSet1.getString("nr_zamowienia");
                rowData[4] = resultSet1.getString("data_dodania");

                model.addRow(rowData);

                atestyTmp.add(new Atest());
                atestyTmp.get(tableIndex).setNazwa(resultSet1.getString("nazwa"));
                atestyTmp.get(tableIndex).setId(resultSet1.getInt("id"));
                atestyTmp.get(tableIndex).setIdMaterialu(resultSet1.getInt("id_materialu"));
                atestyTmp.get(tableIndex).setWz(resultSet1.getString("nr_wz"));
                atestyTmp.get(tableIndex).setNr_zamowienia(resultSet1.getString("nr_zamowienia"));
                atestyTmp.get(tableIndex).setDate(resultSet1.getString("data_dodania"));
                atestyTmp.get(tableIndex).setNazwaMaterialu(DatabaseManager.getInstance().getNameOfMaterial(resultSet1.getInt("id_materialu")));
                atestyTmp.get(tableIndex).setPositionInTable(tableIndex);

                tableIndex += 1;

              } while (resultSet1.next());

              for (Atest atest : atestyTmp) {
                atesty.add(atest);
              }
            }

          } catch (SQLException ex) {
            Logger.getLogger(AtestIndicatorManager.class.getName()).log(Level.SEVERE, null, ex);
          }

        } while (resultSet.next());
      }

    } catch (SQLException ex) {
      //JOptionPane.showMessageDialog(null, ex);
      Logger.getLogger(AtestIndicatorManager.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Przyporządkowuje atest pod materiał z boma.
   *
   * @param tblOutput Tabela z wynikami poszukiwanych atestów.
   * @param tblAtests Tabela z atestami dla boma z zakładki materiały.
   * @param tblBom Tabela z bomem w zakładce materiały.
   */
  public void assignCert(JTable tblOutput, JTable tblAtests, JTable tblBom) {

    try {
      DefaultTableModel modelAtests = (DefaultTableModel) tblAtests.getModel();

      // Dodaje wybrany atest do tabeli z atestami w tabeli atestów w 
      // zakładce materiały.
      modelAtests.setRowCount(modelAtests.getRowCount() + 1);
      modelAtests.setValueAt(atesty, 0, 0);
      //modelAtests.setValueAt(atesty[tblOutput.getSelectedRow()].getNazwa(), modelAtests.getRowCount() - 1, 0);
      modelAtests.setValueAt(atesty.get(tblOutput.getSelectedRow()).getNazwa(), modelAtests.getRowCount() - 1, 0);

      // Zapisuje wybrany atest w bazie danych dla wybranego BOMA.
      DatabaseManager.getInstance().addCertIntoBom(atesty.get(tblOutput.getSelectedRow()).getId(),
              BomManager.getInstance().getBomMaterials()[BomManager.getInstance().getSelectedBomMaterial()].getId());
    } catch (Exception e) {
      Logger.getLogger(AtestIndicatorManager.class.getName()).log(Level.SEVERE, null, e);
    }
  }

  /**
   * Przyporządkowuje atest pod wiele materiałów.
   *
   * @param tblOutput Tabela z wynikami poszukiwanych atestów.
   * @param tblBom Tabela zawierająca materiały z bomów.
   */
  public void assignCert(JTable tblOutput, JTable tblBom) {

    for (int i = 0; i < tblBom.getRowCount(); i++) {
      if (tblBom.getValueAt(i, 2) != null) {
        if (tblBom.getValueAt(i, 2).equals(true)) {
          System.out.println("Zaznaczony numer wiersza: " + i);

          try {
            DatabaseManager.getInstance().addCertIntoBom(atesty.get(tblOutput.getSelectedRow()).getId(),
                    BomManager.getInstance().getCertToManyBomsFiltered().get(i).getId());

          } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(null, e);
            break;
          }
        }
      }
    }

    JOptionPane.showMessageDialog(null, "Dodawanie atestów zakończone powodzeniem.");
  }

  private AtestIndicatorManager() {
  }

  public static AtestIndicatorManager getInstance() {
    return AtestIndicatorManagerHolder.INSTANCE;
  }

  private static class AtestIndicatorManagerHolder {

    private static final AtestIndicatorManager INSTANCE = new AtestIndicatorManager();
  }
}
