/*
 * Wszystkie prawa zastrzeżone.
 */
package com.kprm.materialmanager;

import Frames.FrmWaiting;
import Frames.FrmZpCreator;
import MyClasses.ExcelManager;
import MyClasses.ExcelRow;
import com.monitorjbl.xlsx.StreamingReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Klasa odpowiada za:
 * Import danych z pliku zawierającego rejestr łożysk.
 * Podliczenie ilości elastomerów zbrojonych w wskazanch przez użytkownika
 * łożyskach.
 * @author Łukasz Wawrzyniak
 */
public class ZpCreatorManager {  

  private ZpCreatorManager() {    
  }

  public static ZpCreatorManager getInstance() {
    return ZpCreatorManagerHolder.INSTANCE;
  }

  private static class ZpCreatorManagerHolder {

    private static final ZpCreatorManager INSTANCE = new ZpCreatorManager();
  }

  /**
   * Wczytuje plik Excela, wiersze z pliku dodaje do listy z wierszami
   * zawierającymi dane łożysk - nr kontraktu, obiekt i symbol łożyska.
   *
   * @param filePath Ścieżka pliku excela.
   * @param sheets Numery arkuszy z pliku excela z których mają zostać pobrane
   * dane
   * @throws java.io.FileNotFoundException
   */
  public void readExcel(String filePath, int[] sheets) throws FileNotFoundException, IOException {
    ExcelManager.getInstance().getExcelRows().clear();

    try (
             InputStream is = new FileInputStream(new File(filePath));  Workbook workbook = StreamingReader.builder().open(is);) {

      DataFormatter dataFormatter = new DataFormatter();

      for (int i : sheets) {
        Sheet sheet = workbook.getSheetAt(i);
        //System.out.println("Przetwarzam arkusz: " + sheet.getSheetName());
        for (Row row : sheet) {
          Cell cellContract = row.getCell(3);
          Cell cellSymbol = row.getCell(5);
          Cell cellObject = row.getCell(10);
          String valueContract = dataFormatter.formatCellValue(cellContract);
          String valueSymbol = dataFormatter.formatCellValue(cellSymbol);
          String valueObject = dataFormatter.formatCellValue(cellObject);

          if (!"".equals(valueContract)) {
            ExcelManager.getInstance().getExcelRows().add(
                    new ExcelRow(valueContract, valueObject, valueSymbol));
          }
        }
      }
      //System.out.println("liczba wierszy: " + excelRows.size());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex);
      Logger.getLogger(ZpCreatorManager.class.getName()).log(Level.SEVERE,
              null, ex);
    }
  }

  /**
   * Wypełnia tabelę danymi z listy łożysk wczytanych z pliku excela.
   *
   * @param tblKontrakty
   */
  public void fillTable(JTable tblKontrakty) {
    DefaultTableModel model = (DefaultTableModel) tblKontrakty.getModel();
    ((DefaultTableModel) tblKontrakty.getModel()).setRowCount(0);

    Object rowData[] = new Object[3];

    for (ExcelRow excelRow : ExcelManager.getInstance().getExcelRows()) {
      rowData[0] = excelRow.getContract();
      rowData[1] = excelRow.getContractObject();
      rowData[2] = excelRow.getBearingSymbol();
      model.addRow(rowData);
    }
  }

  /**
   * Filtruje zawartość tabeli wg zadanych parametrem filtrów.
   *
   * @param contractNubmer Numer kontraktu
   * @param objectNumber Numer obiektu
   * @param tblTable Tabela do przefiltrowania.
   */
  public void filterTable(String contractNubmer, String objectNumber, JTable tblTable) {
    DefaultTableModel model = (DefaultTableModel) tblTable.getModel();
    ((DefaultTableModel) tblTable.getModel()).setRowCount(0);

    Object rowData[] = new Object[3];

    for (ExcelRow excelRow : ExcelManager.getInstance().getExcelRows()) {
      if (excelRow.getContract().toLowerCase().contains(contractNubmer.toLowerCase())
              && excelRow.getContractObject().toLowerCase().contains(objectNumber.toLowerCase())) {
        rowData[0] = excelRow.getContract();
        rowData[1] = excelRow.getContractObject();
        rowData[2] = excelRow.getBearingSymbol();
        model.addRow(rowData);
      }
    }
  }

  /**
   * Otwiera plik excela i zapisuje dane pobrane z jego zawartości do listy
   * wierszy excela
   *
   * @param tblTable Tabela w której wyświetlone będą wyniki.
   * @param frmZpCreator Forma podrzędna, która zostanie dezaktywowana na czas
   * odczytu danych z pliku *.xlsx
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void openFileAndFillTable(JTable tblTable, FrmZpCreator frmZpCreator) throws FileNotFoundException, IOException {
    int[] sheets = {6, 7};

    // Dezaktywacja formy 
    frmZpCreator.setEnabled(false);

    // Utworzenie i wywołanie formy informującej o oczekiwaniu na odczyt
    // pliku *.xlsx
    FrmWaiting frm = new FrmWaiting();
    frm.setAlwaysOnTop(true);
    frm.show();

    /* Uruchomienie swing workera odpowiadającego za wykonanie odczytu
        pliku *.xlsx w tle. Brak blokowania działania GUI, dzięki temu możliwość
        wyświetlenia formy informującej o oczekiwaniu na odczyt z pliku.
     */
    SwingWorker sw = new SwingWorker() {

      /* Działania w tle swing workera */
      @Override
      protected Object doInBackground() throws Exception {
        readExcel(ExcelManager.getInstance().getBearingRegistryPath(), sheets);
        String res = "Finish!";
        return res;
      }

      /* Funkcja uruchamia się po zakończeniu działań w tle swing 
            workera */
      @Override
      protected void done() {
        ZpCreatorManager.getInstance().fillTable(tblTable);
        frmZpCreator.setEnabled(true);
        frm.dispose();
        super.done(); //To change body of generated methods, choose Tools | Templates.                
      }
    };
    sw.execute();
  }

  /**
   * Kopiuje zaznaczone łożyska z rejestru łożysk do tabeli ZP
   *
   * @param tblSource Tabela z rejestrem łożysk
   * @param tblZp Tabela zawierająca łożyska do wygenerowania ZP
   * @param tblFinalAmount Tabela zawierająca ostateczną ilość elastomerów
   */
  public void copyAmongTabels(JTable tblSource, JTable tblZp, JTable tblFinalAmount) {
    DefaultTableModel modelFrom;
    modelFrom = (DefaultTableModel) tblSource.getModel();

    DefaultTableModel modelTo;
    modelTo = (DefaultTableModel) tblZp.getModel();

    for (int i : tblSource.getSelectedRows()) {

      Object _rowData[] = new Object[4];

      _rowData[0] = modelFrom.getValueAt(i, 0);
      _rowData[1] = modelFrom.getValueAt(i, 1);
      _rowData[2] = modelFrom.getValueAt(i, 2);
      _rowData[3] = this.getElastomerDimension((String) modelFrom.getValueAt(i, 2));

      modelTo.addRow(_rowData);
    }

    /* Zlicza wszystkie elastomery i dodaje wynik do tabeli z ostateczną 
    ilością elastomerów
     */
    if (tblZp.getRowCount() > 0) {
      fillFinalAmountTable(tblZp, tblFinalAmount);
    }
  }

  /**
   * Zlicza wszystkie eastomery i wyświetla w tabeli ilości elastomerów
   *
   * @param tblZp Tabela z elastomerami
   * @param tblFinalAmount Tabela z ilościami elastomerów
   */
  public void fillFinalAmountTable(JTable tblZp, JTable tblFinalAmount) {
    DefaultTableModel modelZpTable;
    modelZpTable = (DefaultTableModel) tblZp.getModel();

    DefaultTableModel modelFinalTable;
    modelFinalTable = (DefaultTableModel) tblFinalAmount.getModel();

    modelFinalTable.setRowCount(0);

    for (int i = 0; i < tblZp.getRowCount(); i++) {
      if (checkElastomerPresence((String) modelZpTable.getValueAt(i, 3), modelFinalTable) == -1) {
        Object rowData[] = new Object[2];
        rowData[0] = modelZpTable.getValueAt(i, 3);
        rowData[1] = 1;
        modelFinalTable.addRow(rowData);
      } else {
        int finalTableIndex = checkElastomerPresence((String) modelZpTable.getValueAt(i, 3), modelFinalTable);
        modelFinalTable.setValueAt((int) modelFinalTable.getValueAt(finalTableIndex, 1) + 1, finalTableIndex, 1);
      }
    }
  }

  /**
   * Sprawdza czy zadany parametrem elastomer widnieje w tabeli elastomerów
   *
   * @param elastomer Elastomer do sprawdzenia
   * @param modelFinalTable Model tabeli elastomerów
   * @return indeks pod którym występuje zadany elastomer w tabeli, zwraca (-1)
   * jeżeli nie występuje.
   */
  private int checkElastomerPresence(String elastomer, DefaultTableModel modelFinalTable) {
    int indexOfPresence = -1;
    for (int i = 0; i < modelFinalTable.getRowCount(); i++) {
      if (elastomer.equals(modelFinalTable.getValueAt(i, 0))) {
        indexOfPresence = i;
      }
    }
    return indexOfPresence;
  }

  /**
   * Zwraca wymiar elastomeru zbrojonego
   *
   * @param bearingSymbol Symbol łożyska
   * @return wymiar elastomeru zbrojonego
   */
  private String getElastomerDimension(String bearingSymbol) {

    System.out.println(bearingSymbol.substring(4, 9));

    String symbol = bearingSymbol.substring(4, 9);

    String sideSymbolA = symbol.substring(0, 1);
    String sideSymbolB = symbol.substring(1, 2);
    String sideSymbolC = symbol.substring(2, 5);

    //System.out.println(sideStringA + " " + sideStringB + " " + sideStringC);
    int sideIntC;

    try {
      sideIntC = Integer.parseInt(sideSymbolC);
      System.out.println("C: " + sideIntC);
    } catch (NumberFormatException ex) {
      return "Nieznany symbol";
    }

    if (getSymbolDimension(sideSymbolA).equals("nieznany symbol")) {
      return "nieznany symbol";
    }
    if (getSymbolDimension(sideSymbolB).equals("nieznany symbol")) {
      return "nieznany symbol";
    }

    sideSymbolA = getSymbolDimension(sideSymbolA.toUpperCase());
    sideSymbolB = getSymbolDimension(sideSymbolB.toUpperCase());

    String elastomerDim = sideIntC + "x" + sideSymbolA + "x" + sideSymbolB;

    return elastomerDim;
  }

  /**
   * Zwraca wymiar boku elastomeru na podstawie zadanego symbolu
   *
   * @param symbol Symoble boku elastomeru - info T. Głogowski
   * @return Zwraca wymiar boku elastomeru w milimetrach.
   */
  private String getSymbolDimension(String symbol) {
    switch (symbol) {
      case "Z":
        return "100";
      case "A":
        return "150";
      case "B":
        return "200";
      case "C":
        return "250";
      case "D":
        return "300";
      case "E":
        return "350";
      case "F":
        return "400";
      case "G":
        return "450";
      case "H":
        return "500";
      case "I":
        return "550";
      case "J":
        return "600";
      case "K":
        return "650";
      case "L":
        return "700";
      case "M":
        return "750";
      case "N":
        return "800";
      default:
        return "nieznany symbol";
    }
  }
}
