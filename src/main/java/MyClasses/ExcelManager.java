/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyClasses;

import java.util.Iterator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 * Klasa do zarządzania plikiem excela.
 *
 * @author Łukasz Wawrzyniak
 */
public class ExcelManager {

    /**
     * Zwraca ilość kolumn w zadanym parametrze arkuszu excela
     *
     * @param xssfSheet Arkusz excela
     * @return Liczba kolumn
     */
    public int getColumntCount(XSSFSheet xssfSheet) {
        //int result = 0;
        Iterator<Row> rowIterator = xssfSheet.iterator();
        
        int columnCount = 0;
        //int tempColumnCount = 0;
        
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();   
            //tempColumnCount = row.getLastCellNum() + 1;
            
            if (row.getLastCellNum() + 1 > columnCount)
                columnCount = row.getLastCellNum() + 1;
            
            //tempColumnCount = 0;
            
            //List<Cell> cells = new ArrayList<>();
//            Iterator<Cell> cellIterator = row.cellIterator();
//            while (cellIterator.hasNext()) {
//                //System.out.println("Dodaj komórkę: " + cellIterator.next().getRowIndex() + ", " + cellIterator.next().getColumnIndex());
//                //System.out.println("Typ komórki: " + cellIterator.next().getCellType().toString());
//                //cells.add(cellIterator.next());
//                tempColumnCount += 1;
//                if (tempColumnCount > columnCount )
//                    columnCount = tempColumnCount;
//            }
            //tempColumnCount = 0;
            
            //System.out.println("Liczba kolumn: " + columnCount);
            
//            for (int i = cells.size(); i >= 0; i--) {
//                System.out.println("cell.size: " + cells.size());
//                Cell cell = cells.get(i - 1);
//                if (cell.toString().trim().isEmpty()) {
//                    cells.remove(i - 1);
//                } else {
//                    result = cells.size() > result ? cells.size() : result;
//                    break;
//                }
//            }
        }
        return columnCount;
    }

    /**
     * Zwraca liczbę wierszy w zadanym parametrem arkuszu Excela
     * @param xssfSheet Arkusz Excela
     * @return Liczba wierszy w arkuszu.
     */
    public int getRowCount(XSSFSheet xssfSheet) {
        int rowTotal = xssfSheet.getLastRowNum();
        if ((rowTotal > 0) || (xssfSheet.getPhysicalNumberOfRows() > 0)) {
            rowTotal++;
        }
        return rowTotal;
    }

    private ExcelManager() {
    }

    public static ExcelManager getInstance() {
        return ExcelManagerHolder.INSTANCE;
    }

    private static class ExcelManagerHolder {

        private static final ExcelManager INSTANCE = new ExcelManager();
    }
}
