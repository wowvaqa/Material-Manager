package ControlCard;

import Frames.FrmWaiting;
import MyClasses.ExcelManager;
import MyClasses.ExcelRow;
import MyClasses.Settings;
import ZpCreator.ZpCreatorManager;
import com.monitorjbl.xlsx.StreamingReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author Łukasz Wawrzyniak
 */
public class KKCreatorManager {

  private KKCreatorManager() {
  }

  public static KKCreatorManager getInstance() {
    return KKCreatorManagerHolder.INSTANCE;
  }

  private static class KKCreatorManagerHolder {

    private static final KKCreatorManager INSTANCE = new KKCreatorManager();
  }

  /**
   * Otwiera plik excela i zapisuje dane pobrane z jego zawartości do listy
   * wierszy excela
   *
   * @param tblTable Tabela w której wyświetlone będą wyniki.
   * @param frmKKCreator Formatka
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void openFileAndFillTable(JTable tblTable, FrmKKCreator frmKKCreator) throws FileNotFoundException, IOException {
    //int[] sheets = {5, 6, 7};
    //int[] sheets = {7};

    // Dezaktywacja formy 
    frmKKCreator.setEnabled(false);

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
        readExcel(ExcelManager.getInstance().getBearingRegistryPath(),
                Settings.BEARING_REGISTRY_SHEETS);
        String res = "Finish!";
        return res;
      }

      /* Funkcja uruchamia się po zakończeniu działań w tle swing 
            workera */
      @Override
      protected void done() {
        KKCreatorManager.getInstance().fillTable(tblTable);
        frmKKCreator.setEnabled(true);
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
             InputStream is = new FileInputStream(new File(filePath));  Workbook workbook = StreamingReader.builder().open(is);) {

      DataFormatter dataFormatter = new DataFormatter();

      for (int i : sheets) {
        Sheet sheet = workbook.getSheetAt(i);
        //System.out.println("Przetwarzam arkusz: " + sheet.getSheetName());
        for (Row row : sheet) {

          // Kontrakt, Serial number, Symbol, Type, Kind, Object, Position, Capacity, pass
          Cell cellContract = row.getCell(3);
          Cell cellSerialNumber = row.getCell(0);
          Cell cellSymbol = row.getCell(5);
          Cell cellBearingType = row.getCell(1);
          //Cell cellBearingKind = row.getCell(2);
          Cell cellObject = row.getCell(10);
          Cell cellPillar = row.getCell(11);
          Cell cellCapacity = row.getCell(26);
          Cell cellPass = row.getCell(6);

          String tmpContract = dataFormatter.formatCellValue(cellContract);

          String valueContract = tmpContract.replace('_', '.');
          String valueSerialNumber = dataFormatter.formatCellValue(cellSerialNumber);
          String valueSymbol = dataFormatter.formatCellValue(cellSymbol);
          String valueBearingType = dataFormatter.formatCellValue(cellBearingType);
          String valueBearingKind = getElastomerKind(valueSymbol);
          String valueObject = dataFormatter.formatCellValue(cellObject);
          String valuePillar = dataFormatter.formatCellValue(cellPillar);
          String valueCapacity = dataFormatter.formatCellValue(cellCapacity);
          String valuePass = dataFormatter.formatCellValue(cellPass);

          if (!"".equals(valueContract)) {
            ExcelManager.getInstance().getExcelRows().add(
                    new ExcelRow(valueContract, valueObject,
                            valuePillar, valueSymbol,
                            valueSerialNumber, valueBearingType,
                            valueBearingKind, valueCapacity, valuePass));
          }
        }
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex);
      Logger.getLogger(KKCreatorManager.class.getName()).log(Level.SEVERE,
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

    Object rowData[] = new Object[9];

    for (ExcelRow excelRow : ExcelManager.getInstance().getExcelRows()) {
      //System.out.println("Wypełniam wiersz");
      rowData[0] = excelRow.getContract();
      rowData[1] = excelRow.getSerialNumber();
      rowData[2] = excelRow.getBearingSymbol();
      rowData[3] = excelRow.getBearingType();
      rowData[4] = excelRow.getBearingKind();
      rowData[5] = excelRow.getContractObject();
      rowData[6] = excelRow.getObjectPillar();
      rowData[7] = excelRow.getBearingCapacity();
      rowData[8] = excelRow.getBearingPass();

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

    Object rowData[] = new Object[9];

    for (ExcelRow excelRow : ExcelManager.getInstance().getExcelRows()) {
      if (excelRow.getContract().toLowerCase().contains(contractNubmer.toLowerCase())
              && excelRow.getContractObject().toLowerCase().contains(objectNumber.toLowerCase())) {
        rowData[0] = excelRow.getContract();
        rowData[1] = excelRow.getSerialNumber();
        rowData[2] = excelRow.getBearingSymbol();
        rowData[3] = excelRow.getBearingType();
        rowData[4] = excelRow.getBearingKind();
        rowData[5] = excelRow.getContractObject();
        rowData[6] = excelRow.getObjectPillar();
        rowData[7] = excelRow.getBearingCapacity();
        rowData[8] = excelRow.getBearingPass();
        model.addRow(rowData);
      }
    }
  }

  /**
   * Zwraca rodzaj łożyska elastomerowego na podstawie zadanego symbolu łożyska
   *
   * @param elastomerSymbol Symbol łożyska elastomerowego.
   * @return Rodzaj łożyska elastomerowego
   */
  public String getElastomerKind(String elastomerSymbol) {

    String kindString;

    try {
      kindString = elastomerSymbol.substring(1, 2);
      kindString = kindString.toLowerCase();
    } catch (StringIndexOutOfBoundsException ex) {
      kindString = "XXX";
    }

    switch (kindString) {
      case "s":
        if ("00".equals(elastomerSymbol.substring(
                elastomerSymbol.length() - 2, elastomerSymbol.length()))) {
          return "Wielokierunkowe";
        } else {
          return "Wielokierunkowe oblachowane";
        }
      case "f":
        return "Stałe";
      case "g":
        return "Jednokierunkowe";
    }

    return "(!) TYP NIEZNANY (!)";
  }

  /**
   * Tworzy i zapisuje kartę kontroli dla łożyska wielokierunkowego
   * niekotwionego, elastomerowego
   *
   * @param tblTable Tabela z łożyskami
   * @param destPath Ścieżka zapisu pliku karty kontroli
   * @param genMesure Jeżeli TRUE funkcja generuje pomiary
   * @param kjDate Data podpisania dokumentu przez kierownika jakości
   * @param kpDate Data podpisania dokumentu przez kierownika produkcji
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void modifyKKexcelManyWayBearing(JTable tblTable, String destPath,
          boolean genMesure, String kjDate, String kpDate)
          throws FileNotFoundException, IOException {

    File file = new File(Settings.ELASTOMER_BEARING_MANY_WAY_PATH);
    FileInputStream fileInputStream = new FileInputStream(file);

    //Get the workbook instance for XLSX file 
    XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
    XSSFSheet spreadsheet = workbook.getSheetAt(0);

    // Obiekt przechowujący dane nagłówka
    KKExcelHeader excelHeader = new KKExcelHeader();
    // Pobranie danych do obiektu
    excelHeader.setupBearingExcelHeaderData(tblTable);

    excelHeader.modifyKKSpreadsheetHeaderElastomerBearing(spreadsheet);

    /* Wymiary elastomeru ****************************************************/
    spreadsheet.getRow(20).getCell(4).setCellValue(
            getElastomerHeight(excelHeader.getSymbol()));
    spreadsheet.getRow(20).getCell(3).setCellValue(
            getElastomerLength(excelHeader.getSymbol()));
    spreadsheet.getRow(20).getCell(2).setCellValue(
            getElastomerWidth(excelHeader.getSymbol()));

    // Naroża ***
    spreadsheet.getRow(20).getCell(6).setCellValue(
            getElastomerHeight(excelHeader.getSymbol()));
    spreadsheet.getRow(20).getCell(7).setCellValue(
            getElastomerHeight(excelHeader.getSymbol()));

    /* Rzeczywiste zmierzone wartości *****************************************/
    // Wysokość elastomeru
    if (genMesure) {
      spreadsheet.getRow(22).getCell(4).setCellValue(
              Float.toString(Float.parseFloat(getElastomerHeight(
                      excelHeader.getSymbol()))
                      + getRandomNumber(0, 2, 10)));
    } else {
      spreadsheet.getRow(22).getCell(4).setCellValue(" ");
    }

    // Długość elastomeru
    if (genMesure) {
      spreadsheet.getRow(22).getCell(3).setCellValue(
              Float.toString(Float.parseFloat(getElastomerLength(
                      excelHeader.getSymbol()))
                      + getRandomNumber(-2, 4, 10)));
    } else {
      spreadsheet.getRow(22).getCell(3).setCellValue(" ");
    }

    // Szerokość elastomeru
    if (genMesure) {
      spreadsheet.getRow(22).getCell(2).setCellValue(
              Float.toString(Float.parseFloat(getElastomerWidth(
                      excelHeader.getSymbol()))
                      + getRandomNumber(-2, 4, 10)));
    } else {
      spreadsheet.getRow(22).getCell(2).setCellValue(" ");
    }

    // Naroża ***
    if (genMesure) {
      spreadsheet.getRow(22).getCell(6).setCellValue(
              Float.toString(Float.parseFloat(getElastomerHeight(
                      excelHeader.getSymbol()))
                      + getRandomNumber(0, 1, 10)));
    } else {
      spreadsheet.getRow(22).getCell(6).setCellValue(" ");
    }

    if (genMesure) {
      spreadsheet.getRow(22).getCell(7).setCellValue(
              Float.toString(Float.parseFloat(getElastomerHeight(
                      excelHeader.getSymbol()))
                      + getRandomNumber(0, 1, 10)));
    } else {
      spreadsheet.getRow(22).getCell(7).setCellValue(" ");
    }

    // Twardość Shora***   
    if (genMesure) {
      spreadsheet.getRow(22).getCell(8).setCellValue(
              Float.toString(getRandomNumber(60, 65, 1)));
    } else {
      spreadsheet.getRow(22).getCell(8).setCellValue(" ");
    }

    /* Tabela materiałów ******************************************************/
    if (genMesure) {
      spreadsheet.getRow(25).getCell(5).setCellValue("brak");
      spreadsheet.getRow(26).getCell(5).setCellValue("brak");
    } else {
      spreadsheet.getRow(25).getCell(5).setCellValue(" ");
      spreadsheet.getRow(26).getCell(5).setCellValue(" ");
    }

    /* Modyfikacja dat w arkuszu **********************************************/
    modifyDates(spreadsheet, kjDate, kpDate, BearingTypes.ELASTOMER_MANY_WAYS);

    /* Zapis pliku ************************************************************/
    saveWorkbook(workbook, excelHeader, "Łożysko Elastomerowe", destPath);
  }

  /**
   * Modyfikuje wzór karty kontroli wg zaznaczonego wiersza w tabeli i zpaisuje
   * nową kartę do pliku.
   *
   * @param tblTable Tabela danych z rejestru łożysk
   * @param a Wymiar A łożyska
   * @param h Wysokość łożyska
   * @param destPath Ścieżka zapisu karty kontroli
   * @param genMesure Jeżeli TRUE funkcja generuje pomiary
   * @param kjDate Data podpisania dokumentu przez kierownika jakości
   * @param kpDate Data podpisania dokumentu przez kierownika produkcji
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void modifyKKexcelConstantBearing(JTable tblTable, String a, String h,
          String destPath, boolean genMesure, String kjDate, String kpDate)
          throws FileNotFoundException, IOException {

    File file = new File(Settings.ELASTOMER_BEARING_ONE_WAY_PATH);
    FileInputStream fileInputStream = new FileInputStream(file);

    //Get the workbook instance for XLSX file 
    XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
    XSSFSheet spreadsheet = workbook.getSheetAt(0);

    // Obiekt przechowujący dane nagłówka
    KKExcelHeader excelHeader = new KKExcelHeader();
    // Pobranie danych do obiektu
    excelHeader.setupBearingExcelHeaderData(tblTable);
    // Modyfikacja arkusza
    excelHeader.modifyKKSpreadsheetHeaderElastomerBearing(spreadsheet);

    float floatA = Float.parseFloat(a);
    float floatH = Float.parseFloat(h);

    spreadsheet.getRow(15).getCell(1).setCellValue(Float.toString(floatA));
    spreadsheet.getRow(15).getCell(8).setCellValue(Float.toString(floatH));

    spreadsheet.getRow(15).getCell(2).setCellValue("---");
    spreadsheet.getRow(15).getCell(3).setCellValue("---");
    spreadsheet.getRow(15).getCell(4).setCellValue("---");

    spreadsheet.getRow(18).getCell(2).setCellValue("---");
    spreadsheet.getRow(18).getCell(3).setCellValue("---");
    spreadsheet.getRow(18).getCell(4).setCellValue("---");

    // Chropowatość
    spreadsheet.getRow(23).getCell(2).setCellValue("---");
    // pow. malarska
    if (genMesure) {
      spreadsheet.getRow(23).getCell(6).setCellValue(Float.toString(
              getRandomNumber(280, 350, 1)));
    } else {
      spreadsheet.getRow(23).getCell(6).setCellValue(" ");
    }

    /**
     * Wymiary elastomeru ***************************************************
     */
    spreadsheet.getRow(15).getCell(7).setCellValue(
            getElastomerHeight(excelHeader.getSymbol()));
    spreadsheet.getRow(15).getCell(5).setCellValue(
            getElastomerLength(excelHeader.getSymbol()));
    spreadsheet.getRow(15).getCell(6).setCellValue(
            getElastomerWidth(excelHeader.getSymbol()));

    /**
     * Rzeczywiste zmierzone wartości ***************************************
     */
    // Wymiar A
    if (genMesure) {
      spreadsheet.getRow(18).getCell(1).setCellValue(
              Float.toString(floatA + getRandomNumber(-2, 4, 10)));
    } else {
      spreadsheet.getRow(18).getCell(1).setCellValue(" ");
    }

    spreadsheet.getRow(18).getCell(2).setCellValue("---");
    spreadsheet.getRow(18).getCell(3).setCellValue("---");
    spreadsheet.getRow(18).getCell(4).setCellType(CellType.STRING);
    spreadsheet.getRow(18).getCell(4).setCellValue("---");

    // Wygenerowanie wymiarów jeżeli odpowiednia opcja została zaznaczona
    if (genMesure) {
      // Wymiar H
      spreadsheet.getRow(18).getCell(8).setCellValue(
              Float.toString(floatH + getRandomNumber(0, 4, 10)));

      // Wysokość elastomeru    
      spreadsheet.getRow(18).getCell(7).setCellValue(
              Float.toString(Float.parseFloat(getElastomerHeight(
                      excelHeader.getSymbol())) + getRandomNumber(0, 2, 10)));
      // Długość elastomeru
      spreadsheet.getRow(18).getCell(5).setCellValue(
              Float.toString(Float.parseFloat(getElastomerLength(
                      excelHeader.getSymbol())) + getRandomNumber(-2, 4, 10)));
      // Szerokość elastomeru
      spreadsheet.getRow(18).getCell(6).setCellValue(
              Float.toString(Float.parseFloat(getElastomerWidth(
                      excelHeader.getSymbol())) + getRandomNumber(-2, 4, 10)));
    } else {
      spreadsheet.getRow(18).getCell(8).setCellValue(" ");
      spreadsheet.getRow(18).getCell(7).setCellValue(" ");
      spreadsheet.getRow(18).getCell(5).setCellValue(" ");
      spreadsheet.getRow(18).getCell(6).setCellValue(" ");
    }

    /* Tabela materiałów ******************************************************/
    if (genMesure) {
      spreadsheet.getRow(26).getCell(5).setCellValue("brak");
    } else {
      spreadsheet.getRow(26).getCell(5).setCellValue(" ");
    }
    spreadsheet.getRow(27).getCell(5).setCellValue("n.d.");
    if (genMesure) {
      spreadsheet.getRow(28).getCell(5).setCellValue("brak");
    } else {
      spreadsheet.getRow(28).getCell(5).setCellValue(" ");
    }
    spreadsheet.getRow(29).getCell(5).setCellValue("n.d.");
    if (genMesure) {
      spreadsheet.getRow(30).getCell(5).setCellValue("brak");
    } else {
      spreadsheet.getRow(30).getCell(5).setCellValue(" ");
    }
    spreadsheet.getRow(31).getCell(5).setCellValue("n.d.");

    /* Modyfikacja dat w arkuszu **********************************************/
    modifyDates(spreadsheet, kjDate, kpDate, BearingTypes.ELASTOMER_CONSTANT);

    /* Zapis pliku ************************************************************/
    saveWorkbook(workbook, excelHeader, "Łożysko Elastomerowe", destPath);
  }

  /**
   * Modyfikuje wzór karty kontroli wg zaznaczonego wiersza w tabeli i zapisuje
   * nową kartę do pliku.Dotyczy łożyska garnkowego jednokierunkowego.
   *
   * @param tblRejestrLozysk Tabela danych z rejestru łożysk
   * @param tblElastomerTypeDimension Tabela z typami wkładów elastomerowych.
   * @param destPath Ścieżka zapisu pliku karty
   * @param kjDate Data podpisania dokumentu przez kierownika jakości
   * @param kpDate Data podpisania dokumentu przez kierownika produkcji
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void modifyKKExcelPotOneWayBearing(JTable tblRejestrLozysk,
          JTable tblElastomerTypeDimension, String destPath, String kjDate,
          String kpDate)
          throws FileNotFoundException, IOException {
    File file = new File(Settings.POT_BEARING_ONE_WAY_PATH);
    FileInputStream fileInputStream = new FileInputStream(file);

    //Get the workbook instance for XLSX file 
    XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
    XSSFSheet spreadsheet = workbook.getSheetAt(0);

    // Obiekt przechowujący dane nagłówka
    KKExcelHeader excelHeader = new KKExcelHeader();
    // Pobranie danych do obiektu
    excelHeader.setupBearingExcelHeaderData(tblRejestrLozysk);
    // Modyfikacja nagłówka arkusza.
    excelHeader.modifyKKSpreadsheetHeaderPotBearing(spreadsheet);

    String diameter = ElastomerTypeManager.getInstance().getElastomerDiameter(
            tblRejestrLozysk.getValueAt(
                    tblRejestrLozysk.getSelectedRow(), 2).toString(),
            tblElastomerTypeDimension);

    String height = ElastomerTypeManager.getInstance().getElastomerHeight(
            tblRejestrLozysk.getValueAt(
                    tblRejestrLozysk.getSelectedRow(), 2).toString(),
            tblElastomerTypeDimension);

    // Ucięcie '.0' ze stringa
    diameter = diameter.substring(0, diameter.length() - 2);
    height = height.substring(0, height.length() - 2);

    spreadsheet.getRow(23).getCell(4).setCellValue(diameter);
    spreadsheet.getRow(23).getCell(5).setCellValue(diameter);
    spreadsheet.getRow(23).getCell(7).setCellValue(diameter);
    spreadsheet.getRow(23).getCell(9).setCellValue(height);

    /* Modyfikacja dat w arkuszu **********************************************/
    modifyDates(spreadsheet, kjDate, kpDate, BearingTypes.POT_ONE_WAY);
    
    saveWorkbook(workbook, excelHeader, "Łożysko Garnkowe", destPath);
  }

  /**
   * Modyfikuje wzór karty kontroli wg zaznaczonego wiersza w tabeli i zapisuje
   * nową kartę do pliku.Dotyczy łożyska garnkowego wielokierunkowego.
   *
   * @param tblRejestrLozysk Tabela danych z rejestru łożysk
   * @param tblElastomerTypeDimension Tabela z typami wkładów elastomerowych
   * @param destPath Ścieżka zapisu pliku karty
   * @param kjDate Data podpisania dokumentu przez kierownika jakości
   * @param kpDate Data podpisania dokumentu przez kierownika produkcji
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void modifyKKExcelPotManyWayBearing(JTable tblRejestrLozysk,
          JTable tblElastomerTypeDimension, String destPath, String kjDate,
          String kpDate)
          throws FileNotFoundException, IOException {
    File file = new File(Settings.POT_BEARING_MANY_WAY_PATH);
    FileInputStream fileInputStream = new FileInputStream(file);

    //Get the workbook instance for XLSX file 
    XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
    XSSFSheet spreadsheet = workbook.getSheetAt(0);

    // Obiekt przechowujący dane nagłówka
    KKExcelHeader excelHeader = new KKExcelHeader();
    // Pobranie danych do obiektu
    excelHeader.setupBearingExcelHeaderData(tblRejestrLozysk);
    // Modyfikacja nagłówka arkusza.
    excelHeader.modifyKKSpreadsheetHeaderPotBearing(spreadsheet);

    String diameter = ElastomerTypeManager.getInstance().getElastomerDiameter(
            tblRejestrLozysk.getValueAt(
                    tblRejestrLozysk.getSelectedRow(), 2).toString(),
            tblElastomerTypeDimension);

    String height = ElastomerTypeManager.getInstance().getElastomerHeight(
            tblRejestrLozysk.getValueAt(
                    tblRejestrLozysk.getSelectedRow(), 2).toString(),
            tblElastomerTypeDimension);

    // Ucięcie '.0' ze stringa
    diameter = diameter.substring(0, diameter.length() - 2);
    height = height.substring(0, height.length() - 2);

    spreadsheet.getRow(23).getCell(4).setCellValue(diameter);
    spreadsheet.getRow(23).getCell(5).setCellValue(diameter);
    spreadsheet.getRow(23).getCell(7).setCellValue(diameter);
    spreadsheet.getRow(23).getCell(9).setCellValue(height);

    /* Modyfikacja dat w arkuszu **********************************************/
    modifyDates(spreadsheet, kjDate, kpDate, BearingTypes.POT_MANY_WAY);
    
    saveWorkbook(workbook, excelHeader, "Łożysko Garnkowe", destPath);
  }

  /**
   * Modyfikuje wzór karty kontroli wg zaznaczonego wiersza w tabeli i zapisuje
   * nową kartę do pliku.Dotyczy łożyska garnkowego jednokierunkowego.
   *
   * @param tblRejestrLozysk Tabela danych z rejestru łożysk
   * @param tblElastomerTypeDimension Tabela z typami wkładów elastomerowych
   * @param destPath Ścieżka zapisu pliku karty
   * @param kjDate Data podpisania dokumentu przez kierownika jakości
   * @param kpDate Data podpisania dokumentu przez kierownika produkcji
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void modifyKKExcelPotConstantBearing(JTable tblRejestrLozysk,
          JTable tblElastomerTypeDimension, String destPath, String kjDate,
          String kpDate)
          throws FileNotFoundException, IOException {
    File file = new File(Settings.POT_BEARING_CONSTANT_PATH);
    FileInputStream fileInputStream = new FileInputStream(file);

    //Get the workbook instance for XLSX file 
    XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
    XSSFSheet spreadsheet = workbook.getSheetAt(0);

    // Obiekt przechowujący dane nagłówka
    KKExcelHeader excelHeader = new KKExcelHeader();
    // Pobranie danych do obiektu
    excelHeader.setupBearingExcelHeaderData(tblRejestrLozysk);
    // Modyfikacja nagłówka arkusza.
    excelHeader.modifyKKSpreadsheetHeaderPotBearing(spreadsheet);

    String diameter = ElastomerTypeManager.getInstance().getElastomerDiameter(
            tblRejestrLozysk.getValueAt(
                    tblRejestrLozysk.getSelectedRow(), 2).toString(),
            tblElastomerTypeDimension);

    String height = ElastomerTypeManager.getInstance().getElastomerHeight(
            tblRejestrLozysk.getValueAt(
                    tblRejestrLozysk.getSelectedRow(), 2).toString(),
            tblElastomerTypeDimension);

    // Ucięcie '.0' ze stringa
    diameter = diameter.substring(0, diameter.length() - 2);
    height = height.substring(0, height.length() - 2);

    spreadsheet.getRow(23).getCell(4).setCellValue(diameter);
    spreadsheet.getRow(23).getCell(5).setCellValue(diameter);
    spreadsheet.getRow(23).getCell(7).setCellValue(diameter);
    spreadsheet.getRow(23).getCell(9).setCellValue(height);

    /* Modyfikacja dat w arkuszu **********************************************/
    modifyDates(spreadsheet, kjDate, kpDate, BearingTypes.POT_CONSTANT);
    
    saveWorkbook(workbook, excelHeader, "Łożysko Garnkowe", destPath);
  }

  /**
   * Modyfikuje wzór karty kontroli wg zaznaczonego wiersza w tabeli i zpaisuje
   * nową kartę do pliku.Dotyczy łożyska elastomeroweg jednokierunkowego.
   *
   * @param tblTable Tabela danych z rejestru łożysk
   * @param a Wysokość A łożyska
   * @param h Wysokość H łożyska
   * @param bottomPlateDimension Wymiar płyty dolnej.
   * @param destPath Ścieżka zapisu pliku karty
   * @param genMesure Jeżeli TRUE funkcja generuje pomiary
   * @param kjDate Data podpisania dokumentu przez kierownika jakości
   * @param kpDate Data podpisania dokumentu przez kierownika produkcji
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void modifyKKexcelOneWayBaring(JTable tblTable, String a, String h,
          String bottomPlateDimension, String destPath, boolean genMesure,
          String kjDate, String kpDate)
          throws FileNotFoundException, IOException {

    File file = new File(Settings.ELASTOMER_BEARING_ONE_WAY_PATH);
    FileInputStream fileInputStream = new FileInputStream(file);

    //Get the workbook instance for XLSX file 
    XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
    XSSFSheet spreadsheet = workbook.getSheetAt(0);

    // Obiekt przechowujący dane nagłówka
    KKExcelHeader excelHeader = new KKExcelHeader();
    // Pobranie danych do obiektu
    excelHeader.setupBearingExcelHeaderData(tblTable);

    // Modyfikacja arkusza
    excelHeader.modifyKKSpreadsheetHeaderElastomerBearing(spreadsheet);

    float floatA = Float.parseFloat(a);
    float floatH = Float.parseFloat(h);
    float floatBottomPlate = Float.parseFloat(bottomPlateDimension);
    float floatG1 = floatBottomPlate + 3;
    float floatG = floatG1 + 1;

    spreadsheet.getRow(15).getCell(1).setCellValue(Float.toString(floatA));
    spreadsheet.getRow(15).getCell(2).setCellValue(Float.toString(floatG));
    spreadsheet.getRow(15).getCell(8).setCellValue(Float.toString(floatH));
    spreadsheet.getRow(15).getCell(3).setCellValue(Float.toString(floatG1));
    // Chropowatość
    if (genMesure) {
      spreadsheet.getRow(23).getCell(2).setCellValue(Float.toString(
              getRandomNumber(0.2f, 0.7f, 10)));
    } else {
      spreadsheet.getRow(23).getCell(2).setCellValue(" ");
    }
    // pow. malarska
    if (genMesure) {
      spreadsheet.getRow(23).getCell(6).setCellValue(Float.toString(
              getRandomNumber(280, 350, 1)));
    } else {
      spreadsheet.getRow(23).getCell(6).setCellValue(" ");
    }

    /**
     * Wymiary elastomeru ***************************************************
     */
    spreadsheet.getRow(15).getCell(7).setCellValue(
            getElastomerHeight(excelHeader.getSymbol()));
    spreadsheet.getRow(15).getCell(5).setCellValue(
            getElastomerLength(excelHeader.getSymbol()));
    spreadsheet.getRow(15).getCell(6).setCellValue(
            getElastomerWidth(excelHeader.getSymbol()));

    /**
     * Rzeczywiste zmierzone wartości ***************************************
     */
    // Wymiar A
    if (genMesure) {
      spreadsheet.getRow(18).getCell(1).setCellValue(
              Float.toString(floatA + getRandomNumber(-2, 4, 10)));
    } else {
      spreadsheet.getRow(18).getCell(1).setCellValue(" ");
    }

    // Wymiar G
    if (genMesure) {
      float rndG = getRandomNumber(-0.5f, 0, 10);
      String str = Float.toString(floatG + rndG);
      String newStr = str.replaceAll("[.]", ",");
      spreadsheet.getRow(18).getCell(2).setCellValue(newStr);
    } else {
      spreadsheet.getRow(18).getCell(2).setCellValue(" ");
    }
    // Wymiar G1
    if (genMesure) {
      float rndG1 = getRandomNumber(-0.2f, 0.3f, 10);
      String str = Float.toString(floatG1 + rndG1);
      String newStr = str.replaceAll("[.]", ",");
      spreadsheet.getRow(18).getCell(3).setCellValue(newStr);
    } else {
      spreadsheet.getRow(18).getCell(3).setCellValue(" ");
    }

    // Wymiar G - G1
    if (!genMesure) {
      spreadsheet.getRow(18).getCell(4).setCellValue(" ");
    }

    // Wymiar H
    if (genMesure) {
      spreadsheet.getRow(18).getCell(8).setCellValue(
              Float.toString(floatH + getRandomNumber(0, 4, 10)));
    } else {
      spreadsheet.getRow(18).getCell(8).setCellValue(" ");
    }

    // Wysokość elastomeru    
    if (genMesure) {
      spreadsheet.getRow(18).getCell(7).setCellValue(
              Float.toString(Float.parseFloat(getElastomerHeight(
                      excelHeader.getSymbol())) + getRandomNumber(0, 2, 10)));
    } else {
      spreadsheet.getRow(18).getCell(7).setCellValue(" ");
    }
    // Długość elastomeru
    if (genMesure) {
      spreadsheet.getRow(18).getCell(5).setCellValue(
              Float.toString(Float.parseFloat(getElastomerLength(
                      excelHeader.getSymbol())) + getRandomNumber(-2, 4, 10)));
    } else {
      spreadsheet.getRow(18).getCell(5).setCellValue(" ");
    }
    // Szerokość elastomeru
    if (genMesure) {
      spreadsheet.getRow(18).getCell(6).setCellValue(
              Float.toString(Float.parseFloat(getElastomerWidth(
                      excelHeader.getSymbol())) + getRandomNumber(-2, 4, 10)));
    } else {
      spreadsheet.getRow(18).getCell(6).setCellValue(" ");
    }

    if (genMesure) {
      spreadsheet.getRow(26).getCell(5).setCellValue("brak");
      spreadsheet.getRow(27).getCell(5).setCellValue("brak");
      spreadsheet.getRow(28).getCell(5).setCellValue("brak");
      spreadsheet.getRow(29).getCell(5).setCellValue("brak");
      spreadsheet.getRow(30).getCell(5).setCellValue("brak");
      spreadsheet.getRow(31).getCell(5).setCellValue("brak");
    } else {
      spreadsheet.getRow(26).getCell(5).setCellValue(" ");
      spreadsheet.getRow(27).getCell(5).setCellValue(" ");
      spreadsheet.getRow(28).getCell(5).setCellValue(" ");
      spreadsheet.getRow(29).getCell(5).setCellValue(" ");
      spreadsheet.getRow(30).getCell(5).setCellValue(" ");
      spreadsheet.getRow(31).getCell(5).setCellValue(" ");
    }

    /* Modyfikacja dat w arkuszu **********************************************/
    modifyDates(spreadsheet, kjDate, kpDate, BearingTypes.ELASTOMER_ONE_WAY);

    /* Zapis pliku ************************************************************/
    saveWorkbook(workbook, excelHeader, "Łożysko Elastomerowe", destPath);
  }

  /**
   * Modyfikuje daty podpisów w zadanym arkuszu
   *
   * @param spreadsheet Arkusz
   * @param kjDate Data podpisu kierownika jakości
   * @param kpDate Data podpisu kierownika produkcji
   * @param bearingType Typ łożyska
   */
  private void modifyDates(XSSFSheet spreadsheet, String kjDate, String kpDate,
          BearingTypes bearingType) {

    switch (bearingType) {
      case ELASTOMER_ONE_WAY:
      case ELASTOMER_MANY_WAYS:
      case ELASTOMER_CONSTANT:
        spreadsheet.getRow(39).getCell(0).setCellType(CellType.STRING);
        spreadsheet.getRow(39).getCell(0).setCellValue(kjDate);
        spreadsheet.getRow(39).getCell(5).setCellType(CellType.STRING);
        spreadsheet.getRow(39).getCell(5).setCellValue(kpDate);
        break;
      case POT_CONSTANT:
      case POT_MANY_WAY:
      case POT_ONE_WAY:
        spreadsheet.getRow(55).getCell(0).setCellType(CellType.STRING);
        spreadsheet.getRow(55).getCell(0).setCellValue(kjDate);
        spreadsheet.getRow(55).getCell(9).setCellType(CellType.STRING);
        spreadsheet.getRow(55).getCell(9).setCellValue(kpDate);
        break;

//        spreadsheet.getRow(39).getCell(0).setCellType(CellType.STRING);
//        spreadsheet.getRow(39).getCell(0).setCellValue(kjDate);
//        spreadsheet.getRow(39).getCell(5).setCellType(CellType.STRING);
//        spreadsheet.getRow(39).getCell(5).setCellValue(kpDate);        
//        break;
//     
//        spreadsheet.getRow(39).getCell(0).setCellType(CellType.STRING);
//        spreadsheet.getRow(39).getCell(0).setCellValue(kjDate);
//        spreadsheet.getRow(39).getCell(5).setCellType(CellType.STRING);
//        spreadsheet.getRow(39).getCell(5).setCellValue(kpDate);        
//        break;
    }
  }

  /**
   * Zapisuje zmodyfikowany dokument Excel karty kontroli
   *
   * @param workbook Pilk excela
   * @param excelHeader Dane zawierające nagłówek
   * @param destPath Ścieżka zapisu.
   * @throws FileNotFoundException
   * @throws IOException
   */
  private void saveWorkbook(XSSFWorkbook workbook, KKExcelHeader excelHeader,
          String bearingType, String destPath) throws FileNotFoundException, IOException {

    String filename = excelHeader.getSymbol() + " " + bearingType + " "
            + excelHeader.getKind() + " " + excelHeader.getPillar();
    filename = filename.replaceAll("[^łŁŻżźŻŚśĆćÓóąę \\w.-]", "_");

    try ( FileOutputStream out = new FileOutputStream(new File(destPath
            + filename + ".xlsx"))) {

      workbook.write(out);
      //System.out.println("Excel written successfully..");

      JOptionPane.showMessageDialog(null, "Karta kontroli zapisana pomyślnie");
    }
  }

  /**
   * Generuje liczbę losową z podanego zakresu
   *
   * @param min Liczba od której ma zacząć się losowanie
   * @param max Liczba do której ma odbywać się lowowanie
   * @param mask Ilość miejs po przecinku, 10 - 1; 100 - 2; 1000 - 3
   * @return Liczba losowa z zadanego przedziału.
   */
  public float getRandomNumber(float min, float max, int mask) {

    Random generator = new Random();

    float tmpInt = generator.nextFloat() * (max - min) + min;
    float value = Math.round(tmpInt * mask);
    value = value / mask;

    System.out.println("Liczba losowa: " + value);

    return value;
  }

  /**
   * Zwraca wymiar elastomeru zbrojonego
   *
   * @param bearingSymbol Symbol łożyska
   * @return wymiar elastomeru zbrojonego
   */
  private String getElastomerWidth(String bearingSymbol) {
    String symbol = bearingSymbol.substring(4, 9);
    String sideSymbolA = symbol.substring(0, 1);

    if (ZpCreatorManager.getInstance().getSymbolDimension(sideSymbolA).equals("nieznany symbol")) {
      return "nieznany symbol";
    }

    String elastomerWidth = ZpCreatorManager.getInstance().getSymbolDimension(sideSymbolA.toUpperCase());

    return elastomerWidth;
  }

  /**
   * Zwraca wymiar elastomeru zbrojonego
   *
   * @param bearingSymbol Symbol łożyska
   * @return wymiar elastomeru zbrojonego
   */
  private String getElastomerLength(String bearingSymbol) {

    String symbol = bearingSymbol.substring(4, 9);
    String sideSymbolB = symbol.substring(1, 2);

    if (ZpCreatorManager.getInstance().getSymbolDimension(sideSymbolB).equals("nieznany symbol")) {
      return "nieznany symbol";
    }

    sideSymbolB = ZpCreatorManager.getInstance().getSymbolDimension(sideSymbolB.toUpperCase());
    String elastomerLength = sideSymbolB;

    return elastomerLength;
  }

  /**
   * Zwraca wysokość elastomera
   *
   * @param bearingSymbol Symbol łożyska
   * @return Wysokość elastomeru zbrojonego
   */
  private String getElastomerHeight(String bearingSymbol) {

    String symbol = bearingSymbol.substring(4, 9);
    String sideSymbolC = symbol.substring(2, 5);

    int sideIntC;

    try {
      sideIntC = Integer.parseInt(sideSymbolC);
      System.out.println("C: " + sideIntC);
    } catch (NumberFormatException ex) {
      return "Nieznany symbol";
    }

    String elastomerHeight = Integer.toString(sideIntC);

    return elastomerHeight;
  }

}
