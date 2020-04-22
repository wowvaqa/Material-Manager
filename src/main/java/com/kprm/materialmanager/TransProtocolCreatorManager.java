/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kprm.materialmanager;

import Frames.FrmTransProtocolCreator;
import Frames.FrmWaiting;
import MyClasses.ExcelRow;
import com.monitorjbl.xlsx.StreamingReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author l.wawrzyniak
 */
public class TransProtocolCreatorManager {
    
    private final ArrayList<ExcelRow> excelRows;
    private String bearingRegistryPath;
    
    private TransProtocolCreatorManager() {
        excelRows = new ArrayList<>();
        bearingRegistryPath = null;        
    }
    
    public static TransProtocolCreatorManager getInstance() {
        return TransProtocolCreatorManagerHolder.INSTANCE;
    }
    
    private static class TransProtocolCreatorManagerHolder {

        private static final TransProtocolCreatorManager INSTANCE = new TransProtocolCreatorManager();
    }
    
    /**
   * Otwiera plik excela i zapisuje dane pobrane z jego zawartości do listy
   * wierszy excela
   *
   * @param tblTable Tabela w której wyświetlone będą wyniki.
   * @param frmTransProtocolCreator Formatka
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void openFileAndFillTable(JTable tblTable, FrmTransProtocolCreator frmTransProtocolCreator) throws FileNotFoundException, IOException {
    int[] sheets = {5, 6, 7};

    // Dezaktywacja formy 
    frmTransProtocolCreator.setEnabled(false);

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
        readExcel(ZpCreatorManager.getInstance().getBearingRegistryPath(), sheets);
        String res = "Finish!";
        return res;
      }

      /* Funkcja uruchamia się po zakończeniu działań w tle swing 
            workera */
      @Override
      protected void done() {
        ZpCreatorManager.getInstance().fillTable(tblTable);
        frmTransProtocolCreator.setEnabled(true);
        frm.dispose();
        super.done(); //To change body of generated methods, choose Tools | Templates.                
      }
    };
    sw.execute();
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
    ZpCreatorManager.getInstance().getExcelRows().clear();

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
            ZpCreatorManager.getInstance().getExcelRows().add(
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
}
