package com.kprm.materialmanager;

import MyClasses.Atest;
import MyClasses.BomMaterial;
import MyClasses.ExcelManager;
import MyClasses.MmMutableTreeNode;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Manager Bomów
 *
 * @author Łukasz Wawrzyniak
 */
public class BomManager {

    // Przechowuje aktualnego Boma.
    private BomMaterial[] _bomMaterials;
    // Lista przechowuje wiele bomów z formatki CertToMany.
    private ArrayList<BomMaterial> _certToManyBoms;
    // Lista przechowuje przefiltorwane wiele bomów z formatki CertToMany.
    private ArrayList<BomMaterial> _certToManyBomsFiltered;
    // Zaznaczony wiersz materiału w bomie    
    private int selectedBomMaterial;
    // Przechowuje atesty które są w tabli Atest.
    //private Atest[] atesty;
    private ArrayList<Atest> atesty;
    // Lista przechowuje materiały boma który ma zostać skopiowany.
    private ArrayList<BomMaterial> _toCopy_BomMaterial;

    /**
     * Importue BOM z pliku Excela
     *
     * @param importBomTable Tabela w której mają być wyświetlone wyniki.
     */
    public void importBomFromExcel(JTable importBomTable) {

        JFileChooser fileChooser = new JFileChooser();

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

            File file = fileChooser.getSelectedFile();
            //System.out.println("Nazwa pliku: " + file.getName());

            try {
                XSSFWorkbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheet = workbook.getSheetAt(0);

                DefaultTableModel model = (DefaultTableModel) importBomTable.getModel();

                // Ustalnie liczby kolumn i wierszy tabeli wyników.
                int columnsCount = ExcelManager.getInstance().getColumntCount(sheet);
                int rowCount = ExcelManager.getInstance().getRowCount(sheet);

                //int columnsCount = 10;
                //int rowCount = 25;
                model.setColumnCount(columnsCount);
                model.setRowCount(rowCount);

                // Iteracja arkusza i przyporządkowanie wartości komórek arkusza
                // pod odpowiadające im komórki tabeli.
                for (int i = 0; i < rowCount; i++) {
                    for (int j = 0; j < columnsCount; j++) {

                        if (sheet.getRow(i) != null) {
                            if (sheet.getRow(i).getCell(j) != null) {

                                Cell cell = sheet.getRow(i).getCell(j);

                                switch (cell.getCellType()) {
                                    case NUMERIC:
                                        model.setValueAt(cell.getNumericCellValue(), i, j);
                                        break;

                                    case STRING:
                                        model.setValueAt(cell.getStringCellValue(), i, j);
                                        break;
                                }
                            }
                        }
                    }
                }
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(null, ex);
                Logger.getLogger(BomManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | InvalidFormatException ex) {
                JOptionPane.showMessageDialog(null, ex);
                Logger.getLogger(BomManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Kopiuje zaznaczone komórki z tabeli importu do tabeli BOM
     *
     * @param bomImportTable Tabela importu z Excela
     * @param bomTable Tabel Boma
     * @return zwraca -1 jeżeli operacja zakończona niepowowdzeniem.
     */
    public int copySelectedDataToBomTable(JTable bomImportTable, JTable bomTable) {

        if (bomImportTable.getRowCount() < 1) {
            JOptionPane.showMessageDialog(null, "Brak danych do skopiowania.");
            return -1;
        }

        DefaultTableModel modelBomImport = (DefaultTableModel) bomImportTable.getModel();
        DefaultTableModel modelBomTable = (DefaultTableModel) bomTable.getModel();

        // Tablice z zaznaczonymi wierszami i kolumnami w tabeli importu.
        int[] selectedRows;
        int[] selectedColumns;
        selectedRows = bomImportTable.getSelectedRows();
        selectedColumns = bomImportTable.getSelectedColumns();

        //modelBomTable.setRowCount(selectedRows.length /*+ modelBomTable.getRowCount() +1*/);
        // Indeks określający wiersz w którym będą zapisywane dane z tabeli 
        // importu.
        int tableIndex = 0;
        if (modelBomTable.getRowCount() > 0) {
            tableIndex = modelBomTable.getRowCount();
        }

        modelBomTable.setColumnCount(2);
        modelBomTable.setRowCount(modelBomTable.getRowCount() + bomImportTable.getSelectedRowCount());

        // Iteracja tablic z koordynatami do zaznaczeń w tabel importu.
        // Kopiowanie danych do tabeli Bom-u.
        for (int x : selectedRows) {
            for (int y : selectedColumns) {
                //System.out.println("X: " + x + ",Y: " + y);
                modelBomTable.setValueAt(modelBomImport.getValueAt(x, y), tableIndex, 0);
                //System.out.println("Dodaje do X: " + tableIndex + ", Y: 0 Wartość:" + modelBomImport.getValueAt(x, y));
                tableIndex += 1;
            }
        }
        return -1;
    }

    /**
     * Uruchamia dodawanie tabeli bomów do bazy danych.
     *
     * @param bomTable Tabela z materiałówką
     * @param wezly Dzrewo z węzłami
     * @return Zwraca -1 kiedy jest błąd
     */
    public int addBomIntoDB(JTable bomTable, JTree wezly) {
        DefaultTableModel model = (DefaultTableModel) bomTable.getModel();

        if (wezly.getSelectionCount() < 1) {
            JOptionPane.showMessageDialog(null, "Nie zaznaczono węzła");
            return -1;
        }

        if (wezly.getLastSelectedPathComponent().getClass() != MmMutableTreeNode.class) {
            JOptionPane.showMessageDialog(null, "Nie można dodać materiałówki do głównego węzła.");
            return -1;
        }

        if (model.getRowCount() < 1) {
            JOptionPane.showMessageDialog(null, "Brak materiałów w tabeli BOM");
            return -1;
        }

        int rowCount = model.getRowCount();

        int idWezla = ((MmMutableTreeNode) wezly.getLastSelectedPathComponent()).getId();

        for (int i = 0; i < rowCount; i++) {
            System.out.println(model.getValueAt(i, 0));
            if (model.getValueAt(i, 0) != null) {
                DatabaseManager.getInstance().addMaterialIntoBom(model.getValueAt(i, 0).toString(), idWezla);
            }
        }

        return -1;
    }

    /**
     * Zwraca ile rekordów (materiałów) zawiera kliknięty w drzewku BOM
     *
     * @param wezly Drzewko z kontraktami.
     * @return Ilość materiałów w Bomie.
     */
    public int getBomRecordsCount(JTree wezly) {
        /* Sprawdzenie czy zaznaczono węzeł, czy węzeł jest głównym */
        if (wezly.getLastSelectedPathComponent() != null) {
            if (wezly.getLastSelectedPathComponent().getClass() != MmMutableTreeNode.class) {
                return 0;
            }
        } else {
            return 0;
        }

        int id_wezla = ((MmMutableTreeNode) wezly.getLastSelectedPathComponent()).getId();

        int _records;

        try {
            ResultSet resultSet = DatabaseManager.getInstance().importBomFromDB(id_wezla);
            _records = DatabaseManager.getInstance().getSizeOfResuleSet(resultSet);
            return _records;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
            Logger.getLogger(BomManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    /**
     * Importuje materiały z zadanego w drzewku boma do tabeli
     *
     * @param wezly Drzewko z kontraktami.
     * @param tblBoms Tabela z materiałami bomów
     * @return -1 jeżeli błąd.
     */
    public int importMaterialsFromBom(JTree wezly, JTable tblBoms) {

        /* Sprawdzenie czy zaznaczono węzeł, czy węzeł jest głównym */
        if (wezly.getLastSelectedPathComponent() != null) {
            if (wezly.getLastSelectedPathComponent().getClass() != MmMutableTreeNode.class) {
                return -1;
            }
        } else {
            return -1;
        }

        /* Odczytanie numeru id węzła z drzewka kontrków */
        int id_wezla = ((MmMutableTreeNode) wezly.getLastSelectedPathComponent()).getId();

        /* Wczytnie zawartości Boma z bazy danych */
        ResultSet resultSet = DatabaseManager.getInstance().importBomFromDB(id_wezla);

        /* Przygotowanie wirtualnej tabeli */
        DefaultTableModel model = (DefaultTableModel) tblBoms.getModel();

        /* Ustawienie indeksu do wrzucania nowych danych do tabeli */
        int tableIndex = 0;
        if (model.getRowCount() > 0) {
            tableIndex = model.getRowCount();
        }

        try {
            int sizeOfResultSet = DatabaseManager.getInstance().getSizeOfResuleSet(resultSet);

            if (sizeOfResultSet > 0) {
                model.setRowCount(model.getRowCount() + sizeOfResultSet);

                /**
                 * Jeżeli tabela z materiałami jest pusta następuje
                 * wyczyszczenie listy z materiałami.
                 */
                if (tableIndex < 1) {
                    _certToManyBoms = new ArrayList<>();
                    _certToManyBomsFiltered = new ArrayList<>();
                }

                resultSet.first();

                do {
                    model.setValueAt(resultSet.getString("material"), tableIndex, 0);                    

                    BomMaterial bomMaterial = new BomMaterial();
                    bomMaterial.setId(resultSet.getInt("id"));
                    bomMaterial.setNazwaMaterialu(resultSet.getString("material"));
                    bomMaterial.setId_wezla(resultSet.getInt("wezel"));
                    bomMaterial.setTabelaPozycja(tableIndex);

                    _certToManyBoms.add(bomMaterial);
                    _certToManyBomsFiltered.add(bomMaterial);

                    ResultSet resultSet1 = DatabaseManager.getInstance().findAtestInBom(
                            BomManager.getInstance().getCertToManyBoms().get(tableIndex).getId()
                    );

                    model.setValueAt(DatabaseManager.getInstance().getSizeOfResuleSet(resultSet1), tableIndex, 1);
                    tableIndex += 1;
                } while (resultSet.next());
                
                //System.out.println("Ilość pozycji w _cerrt: " + _certToManyBoms.size());
            } else {
                return -1;
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
            Logger.getLogger(BomManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        return -1;
    }
    
    /**
     * Filtruje tabelę bomów wg kryterów z tbFilter (materiał)
     * @param filter Tekst do wyszukania.
     * @param tblBoms Tabela z bomai
     * @return -1 jeżeli błąd
     */
    public int filterMaterialsInBomsTable(String filter, JTable tblBoms){
        
        // Wyczyszczenie tabeli.
        DefaultTableModel model = (DefaultTableModel) tblBoms.getModel();
        model.setRowCount(0);
        
        // Tekst którego będziemy szukać w tabeli z bomami.
        String textToFind = filter.trim().toLowerCase();
        String material;
        
        _certToManyBomsFiltered = new ArrayList<>();
        
        int tableIndex = 0;
        
        for (BomMaterial bomMaterial: _certToManyBoms){
            //System.out.println("Nazwa materiału: " + bomMaterial.getNazwaMaterialu());
            
            material = bomMaterial.getNazwaMaterialu().trim().toLowerCase();
            
            if (material.contains(textToFind)){
                //System.out.println("Znaleziono szukany tekst");
                _certToManyBomsFiltered.add(bomMaterial);
                bomMaterial.setTabelaPozycja(tableIndex);
                
                ResultSet resultSet = DatabaseManager.getInstance().findAtestInBom(bomMaterial.getId());
                
                Vector tableRow = new Vector();
                tableRow.add(bomMaterial.getNazwaMaterialu());
                
                try {
                    tableRow.add(DatabaseManager.getInstance().getSizeOfResuleSet(resultSet));
                } catch (SQLException ex) {
                    Logger.getLogger(BomManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                model.addRow(tableRow);                
                
                tableIndex += 1;
            }
            
        }
        
        return -1;
    }

    /**
     * Kopiuje bom, zapisuje go do 'schowka'
     *
     * @param wezly Drzewko z kontraktami
     * @return -1 jeżeli błąd.
     */
    public int copyBom(JTree wezly) {
        /* Sprawdzenie czy zaznaczono węzeł, czy węzeł jest głównym */
        if (wezly.getLastSelectedPathComponent() != null) {
            if (wezly.getLastSelectedPathComponent().getClass() != MmMutableTreeNode.class) {
                JOptionPane.showMessageDialog(null, "Nie można kopiować z węzła głównego.");
                _toCopy_BomMaterial = null;
                return -1;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Zaznacz węzeł.");
            _toCopy_BomMaterial = null;
            return -1;
        }

        int id_wezla = ((MmMutableTreeNode) wezly.getLastSelectedPathComponent()).getId();
        ResultSet resultSet = DatabaseManager.getInstance().importBomFromDB(id_wezla);

        try {
            int sizeOfResultSet = DatabaseManager.getInstance().getSizeOfResuleSet(resultSet);

            _toCopy_BomMaterial = null;

            if (sizeOfResultSet > 0) {
                _toCopy_BomMaterial = new ArrayList<>();
                resultSet.first();

                do {
                    BomMaterial bomMaterial = new BomMaterial();
                    bomMaterial.setId(resultSet.getInt("id"));
                    bomMaterial.setNazwaMaterialu(resultSet.getString("material"));

                    _toCopy_BomMaterial.add(bomMaterial);
                } while (resultSet.next());
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
            Logger.getLogger(BomManager.class.getName()).log(Level.SEVERE, null, ex);
            _toCopy_BomMaterial = null;
            return -1;
        }

        return 0;
    }

    /**
     * Wkleja materiały z boma z schowka do zaznaczonego w drzewku węzła
     *
     * @param wezly Drzewko z kontraktami.
     * @return -1 jeżeli błąd.
     */
    public int psateBom(JTree wezly) {

        if (wezly.getLastSelectedPathComponent() != null) {
            if (wezly.getLastSelectedPathComponent().getClass() != MmMutableTreeNode.class) {
                JOptionPane.showMessageDialog(null, "Nie można wklejać do węzła głównego.");
                return -1;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Zaznacz węzeł.");
            return -1;
        }

        int id_wezla = ((MmMutableTreeNode) wezly.getLastSelectedPathComponent()).getId();

        if (_toCopy_BomMaterial != null) {
            for (BomMaterial bomMaterial : _toCopy_BomMaterial) {
                DatabaseManager.getInstance().addMaterialIntoBom(bomMaterial.getNazwaMaterialu(), id_wezla);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Schowek jest pusty. Skopiuj BOM");
            return -1;
        }

        return 0;
    }

    /**
     * Importuje dane Boma z bazy danych i dodaje je do tabeli Boma.
     *
     * @param wezly Drzewko z węzłami
     * @param bomTable Tabela z zawartością boma
     * @return Zwraca -1 jeżeli błąd, 0 jeżeli bez błędów.
     */
    public int importBomFromDB(JTree wezly, JTable bomTable) {

        /* Sprawdzenie czy zaznaczono węzeł, czy węzeł jest głównym */
        if (wezly.getLastSelectedPathComponent() != null) {
            if (wezly.getLastSelectedPathComponent().getClass() != MmMutableTreeNode.class) {
                return -1;
            }
        } else {
            return -1;
        }

        int id_wezla = ((MmMutableTreeNode) wezly.getLastSelectedPathComponent()).getId();

        ResultSet resultSet = DatabaseManager.getInstance().importBomFromDB(id_wezla);

        DefaultTableModel model = (DefaultTableModel) bomTable.getModel();
        model.setRowCount(0);

        try {
            int sizeOfResultSet = DatabaseManager.getInstance().getSizeOfResuleSet(resultSet);

            if (sizeOfResultSet > 0) {
                model.setRowCount(sizeOfResultSet);

                _bomMaterials = new BomMaterial[sizeOfResultSet];

                resultSet.first();
                int tableIndex = 0;

                do {
                    model.setValueAt(resultSet.getString("material"), tableIndex, 0);

                    _bomMaterials[tableIndex] = new BomMaterial();
                    _bomMaterials[tableIndex].setId(resultSet.getInt("id"));
                    _bomMaterials[tableIndex].setNazwaMaterialu(resultSet.getString("material"));
                    _bomMaterials[tableIndex].setId_wezla(resultSet.getInt("wezel"));
                    _bomMaterials[tableIndex].setTabelaPozycja(tableIndex);

                    ResultSet resultSet1 = DatabaseManager.getInstance().findAtestInBom(_bomMaterials[tableIndex].getId());

                    model.setValueAt(DatabaseManager.getInstance().getSizeOfResuleSet(resultSet1), tableIndex, 1);

                    tableIndex += 1;
                } while (resultSet.next());
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
            Logger.getLogger(BomManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 0;
    }

    /**
     * Odświeża tabelę z atestami dla zaznaczonego boma
     *
     * @param tblAtest Tabela z atestami dla boma.
     * @param idBom Id boma z bazy danych.
     */
    public void refreshAtestTable(JTable tblAtest, int idBom) {
        DefaultTableModel model = (DefaultTableModel) tblAtest.getModel();
        model.setRowCount(0);

        ResultSet resultSet = DatabaseManager.getInstance().searchCertInAtestBom(idBom);

        int sizeOfResultSet;

        try {

            sizeOfResultSet = DatabaseManager.getInstance().getSizeOfResuleSet(resultSet);

            int tableIndex = 0;

            if (sizeOfResultSet > 0) {

                //atesty = new Atest[sizeOfResultSet];
                atesty = new ArrayList<>();

                model.setRowCount(sizeOfResultSet);
                resultSet.first();

                do {

                    ResultSet resultSet1 = DatabaseManager.getInstance().searchCert(
                            resultSet.getInt("id_atest"));

                    model.setValueAt(
                            resultSet1.getString("nazwa"),
                            tableIndex, 0);
//                    model.setValueAt(
//                            resultSet1.getString("sciezka"),
//                            tableIndex, 1);

//                    atesty[tableIndex] = new Atest();
//                    atesty[tableIndex].setId(resultSet1.getInt("id"));
//                    atesty[tableIndex].setNazwa(resultSet1.getString("nazwa"));
//                    atesty[tableIndex].setPositionInTable(tableIndex);
//                    atesty[tableIndex].setId_atestBom(resultSet.getInt("id"));
                    atesty.add(new Atest());
                    atesty.get(tableIndex).setId(resultSet1.getInt("id"));
                    atesty.get(tableIndex).setNazwa(resultSet1.getString("nazwa"));
                    atesty.get(tableIndex).setPositionInTable(tableIndex);
                    atesty.get(tableIndex).setId_atestBom(resultSet.getInt("id"));

                    tableIndex += 1;

                } while (resultSet.next());

//                for (Atest atesty1 : atesty) {
//                    System.out.println("Nazwa atestu: " + atesty1.getNazwa() + " ID atestu: " + atesty1.getId() + " Pozycja w tabeli: " + atesty1.getPositionInTable()
//                    + " ID boma: " + atesty1.getId_atestBom());
//                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(BomManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Usuwa wybrany atest z tabeli atestów dla wybranego materiału w Bomie.
     *
     * @param tblAtest Tabela atestów.
     * @return Zwraca -1 jeżeli operacja zakończona niepowodzeniem.
     */
    public int removeAtestFromAtestTable(JTable tblAtest) {

        if (DatabaseManager.getInstance().removeCertFromBomTable(
                atesty.get(tblAtest.getSelectedRow()).getId_atestBom()) == 1 /*atesty[tblAtest.getSelectedRow()].getId_atestBom()) == 1*/) {

            DefaultTableModel model = (DefaultTableModel) tblAtest.getModel();
            model.removeRow(tblAtest.getSelectedRow());
            return 1;
        }

        return -1;
    }

    /**
     * Usuwa zaznaczony materiał z tabeli BOM
     *
     * @param bomTable Tabela Bom
     * @return -1 jeżeli błąd.
     */
    public int removeMaterialFromBomTable(JTable bomTable) {

        if (bomTable.getSelectedRowCount() > 0) {

            if (DatabaseManager.getInstance().removeMaterialFromBom(
                    BomManager.getInstance().getBomMaterials()[BomManager.getInstance().getSelectedBomMaterial()].getId()
            ) == 1) {
                DefaultTableModel model = (DefaultTableModel) bomTable.getModel();
                model.removeRow(bomTable.getSelectedRow());
                return 1;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Zaznacz materiał");
            return -1;
        }
        return -1;
    }

    /**
     * Usuwa wszystkie materiały z Boma
     *
     * @param tree Drzewko z kontraktami i bomami.
     * @param bomTable Tabela z materiałami boma
     * @return -1 jeżeli błąd.
     */
    public int removeAllMaterialsFromBomTable(JTree tree, JTable bomTable) {

        // Sprawdza czy zaznaczono jakikolwiek węzeł
        if (tree.getLastSelectedPathComponent() != null) {

            // Sprawdza czy węzeł jest węzłem głównym
            if (((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).isRoot()) {
                JOptionPane.showMessageDialog(null, "Nie można usunąć głównego węzła.");
            } else {
                //DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                MmMutableTreeNode selectedNode = (MmMutableTreeNode) tree.getLastSelectedPathComponent();
                if (DatabaseManager.getInstance().removeAllMaterialsFromBom(selectedNode.getId()) != -1) {
                    ((DefaultTableModel) bomTable.getModel()).setRowCount(0);
                }
                //System.out.println("ID węzła: " + selectedNode.getId());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Zaznacz węzeł.");
        }
        return -1;
    }

    /**
     * Dodaje materiał do Boma
     *
     * @param tblBom Tabela BOM z materiałami.
     * @param treKontraktyMaterialy Drzewko z kontraktami.
     * @return -1 jeżeli błąd
     */
    public int addMaterialIntoBomTable(JTable tblBom, JTree treKontraktyMaterialy) {
        String name = JOptionPane.showInputDialog(null, "Podaj materiał");

        if (treKontraktyMaterialy.getLastSelectedPathComponent() != null) {

            // Sprawdza czy węzeł jest węzłem głównym
            if (((DefaultMutableTreeNode) treKontraktyMaterialy.getLastSelectedPathComponent()).isRoot()) {
                JOptionPane.showMessageDialog(null, "Nie można usunąć głównego węzła.");
            } else {
                MmMutableTreeNode selectedNode = (MmMutableTreeNode) treKontraktyMaterialy.getLastSelectedPathComponent();
                DatabaseManager.getInstance().addMaterialIntoBom(name, selectedNode.getId());
                BomManager.getInstance().importBomFromDB(treKontraktyMaterialy, tblBom);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Zaznacz węzeł.");
        }
        return -1;
    }
    
    /**
     * Dodaje materiały pomocnicze do boma.
     *
     * @param tblBom Tabela BOM z materiałami.
     * @param treKontraktyMaterialy Drzewko z kontraktami.
     * @return -1 jeżeli błąd
     */
    public int addAuixilaryMaterialsIntoBomTable(JTable tblBom, JTree treKontraktyMaterialy) {
        //String name = JOptionPane.showInputDialog(null, "Podaj materiał");

        if (treKontraktyMaterialy.getLastSelectedPathComponent() != null) {

            // Sprawdza czy węzeł jest węzłem głównym
            if (((DefaultMutableTreeNode) treKontraktyMaterialy.getLastSelectedPathComponent()).isRoot()) {
                JOptionPane.showMessageDialog(null, "Nie można usunąć głównego węzła.");
            } else {
                MmMutableTreeNode selectedNode = (MmMutableTreeNode) treKontraktyMaterialy.getLastSelectedPathComponent();
                DatabaseManager.getInstance().addMaterialIntoBom("Farby", selectedNode.getId());
                DatabaseManager.getInstance().addMaterialIntoBom("Drut spawalniczy", selectedNode.getId());
                DatabaseManager.getInstance().addMaterialIntoBom("Drut do metalizacji", selectedNode.getId());
                DatabaseManager.getInstance().addMaterialIntoBom("Ścierniwo", selectedNode.getId());
                BomManager.getInstance().importBomFromDB(treKontraktyMaterialy, tblBom);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Zaznacz węzeł.");
        }
        return -1;
    }

    /**
     * Zmienia nazwę materiału w bomie.
     *
     * @param bomTable Tabela z Bomem.
     * @return Zwraca -1 jeżeli niepowodzenie.
     */
    public int renameMaterialInBomTable(JTable bomTable) {

        String name = JOptionPane.showInputDialog(null, "Podaj nazwę materiału:");

        if (name != null && !name.isEmpty()) {

            if (DatabaseManager.getInstance().renameMaterialInBom(name,
                    BomManager.getInstance().getBomMaterials()[BomManager.getInstance().getSelectedBomMaterial()].getId()) == 1) {
                DefaultTableModel model = (DefaultTableModel) bomTable.getModel();
                model.setValueAt(name, bomTable.getSelectedRow(), 0);

                return 1;
            }
        }
        return -1;
    }

    private BomManager() {
    }

    public static BomManager getInstance() {
        return BomManagerHolder.INSTANCE;
    }

    private static class BomManagerHolder {

        private static final BomManager INSTANCE = new BomManager();
    }

    /**
     * Zwraca aktualnego BOMa
     *
     * @return BOM
     */
    public BomMaterial[] getBomMaterial() {
        return _bomMaterials;
    }

    /**
     * Ustala Boma
     *
     * @param bomMaterial Tablica z bomami.
     */
    public void setBomMaterial(BomMaterial[] bomMaterial) {
        this._bomMaterials = bomMaterial;
    }

    /**
     * Zwraca indeks zaznaczanego materiału w bomie
     *
     * @return indeks
     */
    public int getSelectedBomMaterial() {
        return selectedBomMaterial;
    }

    /**
     * Ustala indeks zaznaczonego materiału w bomie.
     *
     * @param selectedBomMaterial indeks materiału w bomie.
     */
    public void setSelectedBomMaterial(int selectedBomMaterial) {
        this.selectedBomMaterial = selectedBomMaterial;
    }

    /**
     * Zwraca tablicę z materiałami w bomie
     *
     * @return tablica z materiałami.
     */
    public BomMaterial[] getBomMaterials() {
        return _bomMaterials;
    }

    /**
     * Ustala tablice z materiałami w bomie.
     *
     * @param bomMaterials Tablica z bomami.
     */
    public void setBomMaterials(BomMaterial[] bomMaterials) {
        this._bomMaterials = bomMaterials;
    }

    /**
     * Zwraca tablicę z atestami
     *
     * @return Tablica z atestami.
     */
//    public Atest[] getAtesty() {
//        return atesty;
//    }
    public ArrayList<Atest> getAtesty() {
        return atesty;
    }

    /**
     * Zwraca listę z bomami
     *
     * @return lista z Bomami.
     */
    public ArrayList<BomMaterial> getCertToManyBoms() {
        return _certToManyBoms;
    }

    /**
     * Zwraca listę z perzefiltrowanymi Bomami.
     * @return ArrayList 
     */
    public ArrayList<BomMaterial> getCertToManyBomsFiltered() {
        return _certToManyBomsFiltered;
    }

    /**
     * Lista zawierająca atesty z wielu materiałów.
     * @param _certToManyBoms Arraylist
     */
    public void setCertToManyBoms(ArrayList<BomMaterial> _certToManyBoms) {
        this._certToManyBoms = _certToManyBoms;
    }

    /**
     * Lista zawierająca przefiltrowaną listę z wieloma materiałami.
     * @param _certToManyBomsFiltered ArrayList
     */
    public void setCertToManyBomsFiltered(ArrayList<BomMaterial> _certToManyBomsFiltered) {
        this._certToManyBomsFiltered = _certToManyBomsFiltered;
    }
    
    
}
