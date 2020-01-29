package com.kprm.materialmanager;

import MyClasses.Atest;
import MyClasses.BomMaterial;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Klasa do generowania raportów.
 *
 * @author Łukasz Wawrzyniak.
 */
public class RaportsManager {

    private ArrayList<Atest> atesty;
    private ArrayList<BomMaterial> bomy;

    /**
     * Wyszukuje atesty bez fizycznego pliku z certyfikatem.
     *
     * @param tblCertLack Tabela do wyświetlenia wyniku.
     * @param showOnlyDeliveredMaterial Jeżeli TRUE ma pokazać tylko dostarczony
     * materiał
     */
    public void lackOfCerts(JTable tblCertLack, boolean showOnlyDeliveredMaterial) {

        DefaultTableModel model = (DefaultTableModel) tblCertLack.getModel();
        model.setRowCount(0);

        ResultSet restultSet;
        restultSet = DatabaseManager.getInstance().searchAllCerts();

        int sizeOfResultSet;

        /* Następuje wyszukanie wszystkich atestów i zapisanie ich do tablicy.*/
        try {
            sizeOfResultSet = DatabaseManager.getInstance().getSizeOfResuleSet(restultSet);

            if (sizeOfResultSet > 0) {
                atesty = new ArrayList<>();
                restultSet.first();
                int tableIndex = 0;

                do {
                    if (showOnlyDeliveredMaterial == true
                            && !"0000-00-00".equals(restultSet.getString("data_dodania"))) {
                        atesty.add(new Atest());
                        atesty.get(tableIndex).setId(restultSet.getInt("id"));
                        atesty.get(tableIndex).setIdMaterialu(restultSet.getInt("id_materialu"));
                        atesty.get(tableIndex).setDostawca(restultSet.getString("dostawca"));
                        atesty.get(tableIndex).setZp(restultSet.getString("zp"));
                        atesty.get(tableIndex).setWz(restultSet.getString("nr_wz"));
                        atesty.get(tableIndex).setNr_zamowienia(restultSet.getString("nr_zamowienia"));
                        atesty.get(tableIndex).setDate(restultSet.getString("data_dodania"));
                        tableIndex += 1;
                    } else if (showOnlyDeliveredMaterial == false) {
                        atesty.add(new Atest());
                        atesty.get(tableIndex).setId(restultSet.getInt("id"));
                        atesty.get(tableIndex).setIdMaterialu(restultSet.getInt("id_materialu"));
                        atesty.get(tableIndex).setDostawca(restultSet.getString("dostawca"));
                        atesty.get(tableIndex).setZp(restultSet.getString("zp"));
                        atesty.get(tableIndex).setWz(restultSet.getString("nr_wz"));
                        atesty.get(tableIndex).setNr_zamowienia(restultSet.getString("nr_zamowienia"));
                        atesty.get(tableIndex).setDate(restultSet.getString("data_dodania"));
                        tableIndex += 1;
                    }
                } while (restultSet.next());

                restultSet.close();
                ResultSet resultSet1;

                /* 
                    Następuje sprawdzenie czy dla każdego atestu z tablicy 
                    są dostępne pliki z fizycznymi certyfikatami. 
                    Jeżeli takowe pliki nie występują wtedy taki atest wraz 
                    z materiałem którego dotyczy dodawany jest do tabeli 
                    rezultatów.
                 */
                for (Atest atest : atesty) {

                    resultSet1 = DatabaseManager.getInstance().getCertsFilePaths(atest.getId());
                    int sizeOfResultSet1 = DatabaseManager.getInstance().getSizeOfResuleSet(resultSet1);

                    if (sizeOfResultSet1 < 1) {
                        Object[] rowData = new Object[6];
                        rowData[0] = DatabaseManager.getInstance().getNameOfMaterial(atest.getIdMaterialu());
                        rowData[1] = atest.getDostawca();
                        rowData[2] = atest.getZp();
                        rowData[3] = atest.getWz();
                        rowData[4] = atest.getNr_zamowienia();
                        rowData[5] = atest.getDate();
                        model.addRow(rowData);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RaportsManager.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex, "Błąd generowania raportu", 0);
        }
    }

    /**
     * Tworzy raport w zadanej tabeli z brakującymi atestami w bomach.
     *
     * @param tblLack Tabela wyników.
     */
    public void lackOfCertsInBom(JTable tblLack) {

        DefaultTableModel model = (DefaultTableModel) tblLack.getModel();
        model.setRowCount(0);

        ResultSet resultSet;
        resultSet = DatabaseManager.getInstance().importAllBomFromDB();

        int sizeOfResultSet;

        /* Następuje wyszukanie wszystkich atestów i zapisanie ich do tablicy.*/
        try {
            sizeOfResultSet = DatabaseManager.getInstance().getSizeOfResuleSet(resultSet);

            if (sizeOfResultSet > 0) {
                bomy = new ArrayList<>();
                resultSet.first();
                int tableIndex = 0;

                do {
                    bomy.add(new BomMaterial());
                    bomy.get(tableIndex).setId(resultSet.getInt("id"));
                    bomy.get(tableIndex).setNazwaMaterialu(resultSet.getString("material"));
                    bomy.get(tableIndex).setId_wezla(resultSet.getInt("wezel"));
                    tableIndex += 1;
                } while (resultSet.next());

                resultSet.close();
                ResultSet resultSet1;

                /* 
                    Następuje sprawdzenie czy dla każdego materiału z boma.
                    
                 */
                for (BomMaterial bomMaterial : bomy) {
                    
                    resultSet1 = DatabaseManager.getInstance().findAtestInBom(bomMaterial.getId());

                    int sizeOfResultSet1 = DatabaseManager.getInstance().getSizeOfResuleSet(resultSet1);

                    if (sizeOfResultSet1 < 1) {
                        Object[] rowData = new Object[2];
                        rowData[0] = bomMaterial.getNazwaMaterialu();
                        NodeManager.getInstance().clearNodePath();
                        rowData[1] = NodeManager.getInstance().getNodePath(bomMaterial.getId_wezla());
                        model.addRow(rowData);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RaportsManager.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex, "Błąd generowania raportu", 0);
        }

    }
    
    /**
     * Tworzy plik z brakami w bomach.
     * @param tblResult Tabela w której znajdują się braki w bomach.
     */
    public void createCertBomLackRaport(JTable tblResult) {
        
        DefaultTableModel model = (DefaultTableModel) tblResult.getModel();

        //Create Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("Raport");
        XSSFRow row;

        //This data needs to be written (Object[])
        Map< String, Object[]> empinfo = new TreeMap<>();
        empinfo.put("0", new Object[]{"MATERIAŁ", "SCIEŻKA"});

        for (int i = 0; i < model.getRowCount(); i++) {

            String _material = model.getValueAt(i, 0).toString();
            String _sciezka = model.getValueAt(i, 1).toString();
            

            empinfo.put("" + i + 1, new Object[]{_material, _sciezka});
        }

        //Iterate over data and write to sheet
        Set< String> keyid = empinfo.keySet();
        int rowid = 0;

        for (String key : keyid) {
            row = spreadsheet.createRow(rowid++);
            Object[] objectArr = empinfo.get(key);
            int cellid = 0;

            for (Object obj : objectArr) {
                Cell cell = row.createCell(cellid++);
                cell.setCellValue((String) obj);
            }
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Zapisz raport");

        int userSelection = fileChooser.showSaveDialog(tblResult.getParent());

        File fileToSave = new File("./raporty/BrakiBomy.xlsx");
        //File fileToSave;

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            fileToSave = fileChooser.getSelectedFile();
            System.out.println("Save as file: " + fileToSave.getAbsolutePath());
        }

        //Create file system using specific name
        FileOutputStream out;
        try {
            out = new FileOutputStream(new File(fileToSave.getAbsolutePath() + ".xlsx"));
            //out = new FileOutputStream(new File("./raporty/BrakiAtestow.xlsx"));
            workbook.write(out);
            out.close();
            JOptionPane.showMessageDialog(null, "Zapis do pliku zakończony powodzeniem", "Zapis", 1);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RaportsManager.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex, "Błąd zapisu pliku", 0);
            JOptionPane.showMessageDialog(null, "Nazwa materiału niepoprawna");
        } catch (IOException ex) {
            Logger.getLogger(RaportsManager.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex, "Błąd zapisu pliku", 0);
        }
    }

    /**
     * Tworzy plik z raportujący braki w atestach.
     *
     * @param tblResult Tabela brakami atestów.
     */
    public void createCertLackRaport(JTable tblResult) {

        DefaultTableModel model = (DefaultTableModel) tblResult.getModel();

        //Create Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("Raport");
        XSSFRow row;

        //This data needs to be written (Object[])
        Map< String, Object[]> empinfo = new TreeMap<>();
        empinfo.put("0", new Object[]{"MATERIAŁ", "DOSTAWCA", "ZP", "WZ", "ZAMÓWIENIE", "DATA DOSTAWY"});

        for (int i = 0; i < model.getRowCount(); i++) {

            String _material = model.getValueAt(i, 0).toString();
            String _dostawca = model.getValueAt(i, 1).toString();
            String _zp = model.getValueAt(i, 2).toString();
            String _wz = model.getValueAt(i, 3).toString();
            String _zam = model.getValueAt(i, 4).toString();
            String _dataDostawy = model.getValueAt(i, 5).toString();

            empinfo.put("" + i + 1, new Object[]{_material, _dostawca, _zp, _wz, _zam, _dataDostawy});
        }

        //Iterate over data and write to sheet
        Set< String> keyid = empinfo.keySet();
        int rowid = 0;

        for (String key : keyid) {
            row = spreadsheet.createRow(rowid++);
            Object[] objectArr = empinfo.get(key);
            int cellid = 0;

            for (Object obj : objectArr) {
                Cell cell = row.createCell(cellid++);
                cell.setCellValue((String) obj);
            }
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Zapisz raport");

        int userSelection = fileChooser.showSaveDialog(tblResult.getParent());

        File fileToSave = new File("./raporty/BrakiAtestow.xlsx");
        //File fileToSave;

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            fileToSave = fileChooser.getSelectedFile();
            System.out.println("Save as file: " + fileToSave.getAbsolutePath());
        }

        //Create file system using specific name
        FileOutputStream out;
        try {
            out = new FileOutputStream(new File(fileToSave.getAbsolutePath() + ".xlsx"));
            //out = new FileOutputStream(new File("./raporty/BrakiAtestow.xlsx"));
            workbook.write(out);
            out.close();
            JOptionPane.showMessageDialog(null, "Zapis do pliku zakończony powodzeniem", "Zapis", 1);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RaportsManager.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex, "Błąd zapisu pliku", 0);
            JOptionPane.showMessageDialog(null, "Nazwa materiału niepoprawna");
        } catch (IOException ex) {
            Logger.getLogger(RaportsManager.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex, "Błąd zapisu pliku", 0);
        }
    }

    private RaportsManager() {
    }

    public static RaportsManager getInstance() {
        return RaportsManagerHolder.INSTANCE;
    }

    private static class RaportsManagerHolder {

        private static final RaportsManager INSTANCE = new RaportsManager();
    }
}
