package ControlCard;

import javax.swing.JTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 * Klasa reprezentująca dane nagłóweka dokumentu karty kontrli
 *
 * @author Łukasz Wawrzyniak
 */
public class KKExcelHeader {

  private String contract;
  private String serialNumber;
  private String symbol;
  private String type;
  private String kind;
  private String object;
  private String pillar;
  private String capacity;
  private String pass;

  /**
   * Pobiera dane do nagłówka tabeli excela z tabeli wyników.
   * @param table Tabela wyników przechowująca dane z rejestru łożysk
   */
  public void getExcelHeader(JTable table) {
    
    int row = table.getSelectedRow();
    
    this.contract = table.getModel().getValueAt(row, 0).toString();
    this.serialNumber = table.getModel().getValueAt(row, 1).toString();
    this.symbol = table.getModel().getValueAt(row, 2).toString();
    this.type = "Łożysko elastomerowe";
    this.kind = table.getModel().getValueAt(row, 4).toString();
    if ("Wielokierunkowe oblachowane".equals(this.kind)) this.kind = "Wielokierunkowe";
    this.object = table.getModel().getValueAt(row, 5).toString();
    this.pillar = table.getModel().getValueAt(row, 6).toString();
    this.capacity = table.getModel().getValueAt(row, 7).toString();
    this.pass = table.getModel().getValueAt(row, 8).toString();
  }
  
  /**
   * Modyfikuje nagłówek arkusza karty kontrloi łożysk
   * @param spreadsheet Arkusz Excela do modyfikacji nagłówka
   */
  public void modifyKKSpreadsheetHeader(XSSFSheet spreadsheet){
    spreadsheet.getRow(3).getCell(0).setCellValue(this.contract);
    spreadsheet.getRow(3).getCell(1).setCellValue(this.serialNumber);
    spreadsheet.getRow(3).getCell(2).setCellValue(this.symbol);
    spreadsheet.getRow(3).getCell(3).setCellValue(this.type);
    spreadsheet.getRow(3).getCell(4).setCellValue(this.kind);
    spreadsheet.getRow(3).getCell(5).setCellValue(this.object);
    spreadsheet.getRow(3).getCell(6).setCellValue(this.pillar);
    spreadsheet.getRow(3).getCell(7).setCellValue(this.capacity);
    spreadsheet.getRow(3).getCell(8).setCellValue(this.pass);
  }

  /**
   * Zwraca symbol łożska
   * @return Symbol
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * Zwraca rodzaj łożyska
   * @return Rodzaj
   */
  public String getKind() {
    return kind;
  }

  /**
   * Zwraca podporę (położenie) łożyska
   * @return Podpora
   */
  public String getPillar() {
    return pillar;
  }
  
  
}
