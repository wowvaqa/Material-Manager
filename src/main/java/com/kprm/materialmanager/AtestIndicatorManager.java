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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;

/**
 * Zarządza przyporządkowaniem atestów.
 *
 * @author Łukasz Wawrzyniak
 */
public class AtestIndicatorManager {

  // Tablica przechowująca atesty.
  private ArrayList<Atest> atesty;

  // Zmienna przechowuje materiał z BOM do automatycznego przypożądkowania atestu.
  private String bomMaterial;

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

  /**
   * Odszukuje typ materiału w zadaym materiale z BOMa.
   *
   * @param bomMaterial
   * @return Typ materiału
   */
  public String matchCert(String bomMaterial) {

    // Lista z zgodnymi typami materiałów
    ArrayList<String> matchedMaterialTypes = new ArrayList<>();

    // Wczytanie typów materiału z bazy danych
    ResultSet resultSetMaterialTypes = DatabaseManager.getInstance().getMaterialTypes();
    ArrayList<String> materialTypes = getAllStringsFromResultSet(resultSetMaterialTypes, "material_type");

    System.out.println("-" + bomMaterial + "");

    for (String materialType : materialTypes) {
      System.out.println("--- SPRAWDZAM TYP MATERIAŁU: " + materialType);
      ResultSet resultSetKeyWords = DatabaseManager.getInstance().getMaterialTypesKeyWords(materialType);

      if (resultSetKeyWords != null) {

        // Sprawdzenie czy słowo kluczowe występuje w liście słów kluczowych
        // typu materiału.
        ArrayList<String> materialTypesKeyWords = getAllStringsFromResultSet(resultSetKeyWords, "key_word");
        for (String materialTypeKeyWord : materialTypesKeyWords) {
          System.out.println("------ SPRAWDZAM SŁOWO KLUCZOWE: " + materialTypeKeyWord);

          if (bomMaterial.contains(materialTypeKeyWord)) {
            matchedMaterialTypes.add(materialType);
            System.out.println("--------- Słowo kluczowe znalezione w materiale z boma! ROZMIAR LISTY: " + matchedMaterialTypes.size());
            break;
            //return materialType;
          }
        }
      }
    }

//    ArrayList<String> findedDigits = extractDigits(bomMaterial);
//    String dimension = null;
//    if (findedDigits.size() > 1) {
//      dimension = findedDigits.get(0) + "X" + findedDigits.get(1);
//    } else if (findedDigits.size() == 1) {
//      dimension = findedDigits.get(0);
//    }
    if (matchedMaterialTypes.size() > 0) {
      return matchedMaterialTypes.get(0);
    }

    return null;
  }

  private void matchDimension(String bomMaterial) {
    extractDigits(bomMaterial);
  }

  private ArrayList<String> extractDigits(String bomMaterial) {

    ArrayList<String> findedNumbers = new ArrayList<>();

    //Regular expression to match digits in a string
    String regex = "\\d+";
    //Creating a pattern object
    Pattern pattern = Pattern.compile(regex);
    //Creating a Matcher object
    Matcher matcher = pattern.matcher(bomMaterial);
    System.out.println("Digits in the given string are: ");
    int counter = 1;
    while (matcher.find()) {
      findedNumbers.add(matcher.group());
      System.out.print(" " + counter + ": " + matcher.group());
      counter += 1;
    }
    return findedNumbers;
  }

  /**
   * Odszukuje materiał w drzewie wg podanego materiału z bom
   *
   * @param bomMaterial Materiał z boma
   * @param treeMaterialy Drzewo z materiałami
   */
  public void autoMaterial(String bomMaterial, JTree treeMaterialy) {
    //ArrayList<String> bomMaterialWords = new ArrayList<>();

    String[] words = bomMaterial.split(" ");
    for (String i : words) {
      System.out.println("Word: " + i);
    }
  }

  /**
   * Odczytuje typy materiałów i wyświetla je na liście.
   *
   * @param lstMaterialTypes Lista typów materiałów.
   */
  public void refreshMaterialTypes(JList lstMaterialTypes) {
    ResultSet resultSet = DatabaseManager.getInstance().getMaterialTypes();
    if (resultSet != null) {
      DefaultListModel<String> model = new DefaultListModel<>();

      getAllStringsFromResultSet(resultSet, "material_type").forEach((materialType) -> {
        model.addElement(materialType);
      });
      lstMaterialTypes.setModel(model);
    }
  }

  /**
   * Zwraca listę Stringów z zadanego ResultSeta
   *
   * @param resultSet ResultSet z bazy danych
   * @param dbColumnName Nazwa kolumny tabeli bazy danych z której mają zostać
   * odczytane Stringi.
   * @return Lista stringów w postaci listy
   */
  private ArrayList<String> getAllStringsFromResultSet(ResultSet resultSet, String dbColumnName) {
    ArrayList<String> stringList = new ArrayList<>();
    try {
      resultSet.first();
      do {
        String string;
        string = resultSet.getString(dbColumnName);
        stringList.add(string);
      } while (resultSet.next());
    } catch (SQLException ex) {
      Logger.getLogger(AtestIndicatorManager.class.getName()).log(Level.SEVERE, null, ex);
    }
    return stringList;
  }

  /**
   * Odczytuje słowa kluczowe zadanego typu materiału
   *
   * @param materialType Typ materiału
   * @param lstMaterialTypesKeyWords Lista słów kluczowych
   */
  public void refreshMaterialTypeKeyWord(String materialType, JList lstMaterialTypesKeyWords) {
    ResultSet resultSet = DatabaseManager.getInstance().getMaterialTypesKeyWords(materialType);

    if (resultSet != null) {
      DefaultListModel<String> model = new DefaultListModel<>();

      getAllStringsFromResultSet(resultSet, "key_word").forEach((materialTypeKeyWord) -> {
        model.addElement(materialTypeKeyWord);
      });

      lstMaterialTypesKeyWords.setModel(model);
    }
  }

  /**
   * Dodaje nowy typ materiału
   *
   * @param materialType typ materiału
   */
  public void addMaterialType(String materialType) {
    DatabaseManager.getInstance().addNewMaterialType(materialType.toUpperCase());
  }

  /**
   * Usuwa zadany typ materiału
   *
   * @param materialType Nazwa typu materiału
   */
  public void removeMaterialType(String materialType) {
    DatabaseManager.getInstance().removeMaterialType(materialType);
  }

  /**
   * Zmienia nazwę typu materiału
   *
   * @param materialType Nazwa typu materiału który ma zostać zmieniony
   * @param newMaterialType Nowa nazwa typu materiału
   */
  public void renameMaterialType(String materialType, String newMaterialType) {
    DatabaseManager.getInstance().renameMaterialType(
            materialType.toUpperCase(), newMaterialType.toUpperCase());
  }

  /**
   * Dodaje nowe słowo kluczowe do zadanego typu materiału
   *
   * @param keyWord Słowo kluczowe
   * @param materialType Typ materiału
   */
  public void addMaterialTypeKeyWord(String keyWord, String materialType) {
    if (!keyWord.isBlank()) {
      DatabaseManager.getInstance().addNewMaterialTypeKeyWord(
              keyWord.toUpperCase(), materialType.toUpperCase());
    }
  }

  /**
   * Usuwa zadane słowo kluczowe z listy typu materiału
   *
   * @param materialType Typ materiału dla którego ma zostać usunięte słowo
   * kluczowe
   * @param materialTypeKeyWord Słowo kluczowe do usunięcia
   */
  public void removeMaterialTypeKeyWord(String materialType, String materialTypeKeyWord) {
    DatabaseManager.getInstance().removeMaterialTypeKeyWord(materialType, materialTypeKeyWord);
  }

  private AtestIndicatorManager() {
  }

  public static AtestIndicatorManager getInstance() {
    return AtestIndicatorManagerHolder.INSTANCE;
  }

  private static class AtestIndicatorManagerHolder {

    private static final AtestIndicatorManager INSTANCE = new AtestIndicatorManager();
  }

  public String getBomMaterial() {
    return bomMaterial;
  }

  public void setBomMaterial(String bomMaterial) {
    this.bomMaterial = bomMaterial;
  }
}
