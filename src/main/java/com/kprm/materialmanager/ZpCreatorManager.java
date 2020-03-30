/*
 * Wszystkie prawa zastrzeżone.
 */
package com.kprm.materialmanager;

import Frames.FrmWaiting;
import Frames.FrmZpCreator;
import MyClasses.ExcelRow;
import com.monitorjbl.xlsx.StreamingReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
 * @author Łukasz Wawrzyniak
 */
public class ZpCreatorManager {

    /* Lista przechowująca wszystkie wiersze z wczytanego pliku Excela */
    private final ArrayList<ExcelRow> excelRows;

    private ZpCreatorManager() {
        excelRows = new ArrayList<>();
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
        ZpCreatorManager.getInstance().getExcelRows().clear();

        try (
                InputStream is = new FileInputStream(new File(filePath)); Workbook workbook = StreamingReader.builder().open(is);) {

            DataFormatter dataFormatter = new DataFormatter();

            for (int i : sheets) {
                Sheet sheet = workbook.getSheetAt(i);
                System.out.println("Przetwarzam arkusz: " + sheet.getSheetName());
                for (Row row : sheet) {
                    Cell cellContract = row.getCell(3);
                    Cell cellSymbol = row.getCell(5);
                    Cell cellObject = row.getCell(10);
                    String valueContract = dataFormatter.formatCellValue(cellContract);
                    String valueSymbol = dataFormatter.formatCellValue(cellSymbol);
                    String valueObject = dataFormatter.formatCellValue(cellObject);

                    if (!"".equals(valueContract)) {
                        ZpCreatorManager.getInstance().getExcelRows().add(new ExcelRow(valueContract, valueObject, valueSymbol));
                    }
                }
            }
            //System.out.println("liczba wierszy: " + excelRows.size());
        }
    }

    /**
     * Wypełnia tabelę danymi z listy łożysk wczytanych z pliku excela.
     * @param tblKontrakty
     */
    public void fillTable(JTable tblKontrakty) {
        DefaultTableModel model = (DefaultTableModel) tblKontrakty.getModel();
        ((DefaultTableModel) tblKontrakty.getModel()).setRowCount(0);

        Object rowData[] = new Object[3];

        for (ExcelRow excelRow : this.excelRows) {
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

        for (ExcelRow excelRow : this.excelRows) {
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
        int[] sheets = {5, 6, 7};
        
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
        SwingWorker sw = new SwingWorker(){
            
            /* Działania w tle swing workera */
            @Override            
            protected Object doInBackground() throws Exception {                
                readExcel("d:rl.xlsx", sheets);
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
     * Zwraca listę z wierszami załadowanymi z pliku excela.
     *
     * @return Lista wierszy łożysk.
     */
    public ArrayList<ExcelRow> getExcelRows() {
        return excelRows;
    }
}
