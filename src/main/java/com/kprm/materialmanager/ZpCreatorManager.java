/*
 * Wszystkie prawa zastrzeżone.
 */
package com.kprm.materialmanager;

import MyClasses.ExcelRow;
import com.monitorjbl.xlsx.StreamingReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
        this.excelRows.clear();

        try (
                 InputStream is = new FileInputStream(new File("e:rl.xlsx"));  Workbook workbook = StreamingReader.builder().open(is);) {

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
                        this.excelRows.add(new ExcelRow(valueContract, valueObject, valueSymbol));
                    }
                }
            }

            System.out.println("liczba wierszy: " + excelRows.size());
        }
    }

    /**
     * Wypełnia tabelę danymi z listy łożysk wczytanych z pliku excela.
     *
     *
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
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void openFileAndFillTable() throws FileNotFoundException, IOException {
        int[] sheets = {5, 6, 7};
        readExcel("e:rl.xlsx", sheets);
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
