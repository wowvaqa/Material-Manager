/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kprm.materialmanager;

import Configuration.MmConfigManager;
import Frames.FrmTransProtocolCreator;
import Frames.FrmWaiting;
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
 *
 * @author l.wawrzyniak
 */
public class TransProtocolCreatorManager {

    private TransProtocolCreatorManager() {
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
        int[] sheets = {7};

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
                readExcel(MmConfigManager.getMmConfig().getBearingRegistryFilePath(), sheets);
                String res = "Finish!";
                return res;
            }

            /* Funkcja uruchamia się po zakończeniu działań w tle swing 
            workera */
            @Override
            protected void done() {
                TransProtocolCreatorManager.getInstance().fillTable(tblTable);
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
        ExcelManager.getInstance().getExcelRows().clear();

        try (                
                InputStream is = new FileInputStream(new File(filePath)); Workbook workbook = StreamingReader.builder().open(is);) {

            DataFormatter dataFormatter = new DataFormatter();

            for (int i : sheets) {
                Sheet sheet = workbook.getSheetAt(i);
                //System.out.println("Przetwarzam arkusz: " + sheet.getSheetName());
                for (Row row : sheet) {
                    Cell cellContract = row.getCell(3);
                    Cell cellSymbol = row.getCell(5);
                    Cell cellObject = row.getCell(10);
                    Cell cellPillar = row.getCell(11);
                    Cell cellSerialNumber = row.getCell(0);
                    Cell cellBearingType = row.getCell(1);
                    Cell cellBearingKind = row.getCell(2);
                    String valueContract = dataFormatter.formatCellValue(cellContract);
                    String valueSymbol = dataFormatter.formatCellValue(cellSymbol);
                    String valueObject = dataFormatter.formatCellValue(cellObject);
                    String valuePillar = dataFormatter.formatCellValue(cellPillar);
                    String valueSerialNumber = dataFormatter.formatCellValue(cellSerialNumber);
                    String valueBearingType = dataFormatter.formatCellValue(cellBearingType);
                    String valueBearingKind = dataFormatter.formatCellValue(cellBearingKind);

                    if (!"".equals(valueContract)) {
                        ExcelManager.getInstance().getExcelRows().add(
                                new ExcelRow(valueContract, valueObject, 
                                        valuePillar, valueSymbol, 
                                        valueSerialNumber, valueBearingType, 
                                        valueBearingKind));
                    }
                }
            }
            //System.out.println("liczba wierszy: " + excelRows.size());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
            Logger.getLogger(TransProtocolCreatorManager.class.getName()).log(Level.SEVERE,
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

        Object rowData[] = new Object[6];

        for (ExcelRow excelRow : ExcelManager.getInstance().getExcelRows()) {
            System.out.println("Wypełniam wiersz");
            rowData[0] = excelRow.getContract();
            rowData[1] = excelRow.getContractObject();
            rowData[2] = excelRow.getObjectPillar();
            rowData[3] = excelRow.getSerialNumber();
            rowData[4] = excelRow.getBearingType();
            rowData[5] = excelRow.getBearingKind();
            model.addRow(rowData);
        }
    }
    
    /**
   * Kopiuje zaznaczone łożyska z rejestru łożysk do tabeli ZP
   *
   * @param tblSource Tabela z rejestrem łożysk
   * @param tblProtocol Tabela protokołu łożysk
   */
  public void copyAmongTabels(JTable tblSource, JTable tblProtocol) {
    DefaultTableModel modelFrom;
    modelFrom = (DefaultTableModel) tblSource.getModel();

    DefaultTableModel modelTo;
    modelTo = (DefaultTableModel) tblProtocol.getModel();

    for (int i : tblSource.getSelectedRows()) {

      Object _rowData[] = new Object[4];

      _rowData[0] = modelFrom.getValueAt(i, 0);
      _rowData[1] = modelFrom.getValueAt(i, 1);
      _rowData[2] = modelFrom.getValueAt(i, 2);

      modelTo.addRow(_rowData);
    }
  }
}
