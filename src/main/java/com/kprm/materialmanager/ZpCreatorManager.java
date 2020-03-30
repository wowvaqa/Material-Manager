/*
 * Wszystkie prawa zastrzeżone.
 */
package com.kprm.materialmanager;

import com.monitorjbl.xlsx.StreamingReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    private ZpCreatorManager() {
    }

    public static ZpCreatorManager getInstance() {
        return ZpCreatorManagerHolder.INSTANCE;
    }

    private static class ZpCreatorManagerHolder {

        private static final ZpCreatorManager INSTANCE = new ZpCreatorManager();
    }

    public void testOtwarciaExcela2(JTable tblKontrakty) throws FileNotFoundException, IOException {
        try (
                InputStream is = new FileInputStream(new File("d:rl.xlsx"));
                Workbook workbook = StreamingReader.builder().open(is);) {
            DataFormatter dataFormatter = new DataFormatter();
            
            Sheet sheet = workbook.getSheetAt(7);
            
            //for (Sheet sheet : workbook) {
                System.out.println("Processing sheet: " + sheet.getSheetName());
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        String value = dataFormatter.formatCellValue(cell);
                        System.out.print(" Value: " + value + " ");
                    }
                }
            //}
        }
    }

    public void testOtwarciaExcela(JTable tblKontrakty) throws FileNotFoundException, IOException {

        /* Ustawienie modelu tabeli, wyczyszczenie tabeli */
        DefaultTableModel model = (DefaultTableModel) tblKontrakty.getModel();
        ((DefaultTableModel) tblKontrakty.getModel()).setRowCount(0);

        try {
            FileInputStream file = new FileInputStream(new File("d:rl.xlsx"));

            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(7);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();

            Object rowData[] = new Object[2];

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                System.out.println("--> ROW NUMBER --> " + row.getRowNum());

                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();

//                while (cellIterator.hasNext()) 
//                {
//                    Cell cell = cellIterator.next();
//                    //Check the cell type and format accordingly
//                    System.out.print("CN-> " + cell.getColumnIndex() + " ");
//                    switch (cell.getCellType()) 
//                    {
//                        case NUMERIC:                            
//                            System.out.print(cell.getNumericCellValue() + " ");
//                            break;
//                        case STRING:
//                            
//                            if (cell.getColumnIndex() == 3){
//                                rowData[0] = cell.getStringCellValue();
//                                rowData[1] = true;
//                                //model.addRow(rowData);
//                            }                         
//                            
//                            System.out.print(cell.getStringCellValue() + " ");
//                            break;
//                    }
//                }                
                System.out.println("");
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
