package com.kprm.materialmanager;

import Frames.FrmMain;
import MyClasses.Atest;
import MyClasses.CertOperations;
import MyClasses.CertPath;
import MyClasses.Material;
import MyClasses.MmComparators;
import MyClasses.SortModes;
import com.toedter.calendar.JDateChooser;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * Manager atestów.
 *
 * @author Łukasz Wawrzyniak
 */
public class AtestManager {

    private ArrayList<Atest> atesty;

    // Tablica z atestami.
    //private Atest[] atesty;
    // Tablica z ścieżkami do plików.
    private CertPath[] certPaths;
    // Plik z fizycznym atestem.
    private File certFile;
    // Tablica z plikami.
    private File[] files;
    // Tablica z materiałami
    private ArrayList<Material> materials;
    // Schowek przechowujący wycięty atest
    private Atest clipboard;
    // Typ operacji która ma zostać przeprowadzona na ateście - wycinanie/kopiowanie
    private CertOperations certOperations;

    /**
     * Odczytuje materiały z bazy danych i wrzuca je do drzewa materiałów.
     *
     * @param treeMaterialy Drzewko z materiałami.
     * @param nameFilter Filtr nazwy materiału.
     * @throws SQLException Błąd bazy danych
     */
    public void readMaterialsFromDB(JTree treeMaterialy, String nameFilter) throws SQLException {
        DefaultTreeModel model = (DefaultTreeModel) treeMaterialy.getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();

        //System.out.println("Dzieci: " + model.getChildCount(rootNode));
        rootNode.removeAllChildren();

        //ArrayList<Material> materialy = DatabaseManager.getInstance().readMaterials();
        this.materials = DatabaseManager.getInstance().readMaterials(nameFilter);

        Collections.sort(materials, MmComparators.atestNameComparator);

        for (Material material : this.materials) {
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(material.getName());
            model.insertNodeInto(newNode, rootNode, 0);
        }

        model.reload();
    }

    /**
     * Dodaje nowy materiał do bazy danych.
     *
     * @param nazwaMaterialu Nazwa materiału
     * @param treeMaterialy Drzewo z materiałami
     */
    public void addNewMaterial(String nazwaMaterialu, JTree treeMaterialy) {

        /* Sprawdzenie czy wpisano cokolwiek */
        if (nazwaMaterialu.trim().length() > 0 && nazwaMaterialu.length() < 64) {

            /* zmiana liter na wielkie */
            nazwaMaterialu = nazwaMaterialu.toUpperCase();
            DatabaseManager.getInstance().addNewMaterial(nazwaMaterialu);

            try {
                readMaterialsFromDB(treeMaterialy, null);
            } catch (SQLException ex) {
                Logger.getLogger(AtestManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nazwa materiału niepoprawna");
        }
    }

    /**
     * Uruchamia dodawanie nowego atestu.
     *
     * @param nazwaAtestu Nazwa atestu
     * @param nrZamowienia Numer zamówienia.
     * @param nrWZ Numer wztki
     * @param sciezka Ścieżka do pliku pdf z atestem
     * @param zp Numer zp
     * @param dostawca Nazwa dostawcy
     * @param treeMaterialy Drzewko z materiałami
     * @param dataDostawy Data dostawy materiału
     * @param pkd Protokół kontroli dostawy
     * @return -1 jeżeli błąd
     */
    public int addNewCert(String nazwaAtestu, String nrZamowienia, String nrWZ,
            String sciezka, String zp, String dostawca, String pkd,
            String dataDostawy, JTree treeMaterialy) {

        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeMaterialy.getLastSelectedPathComponent();

        if (selectedNode != null && !"Materiały".equals(selectedNode.toString())) {

            int idMaterialu = DatabaseManager.getInstance().getIdOfMaterial(
                    selectedNode.toString());

            if (DatabaseManager.getInstance().addNewCert(nazwaAtestu, nrZamowienia,
                    nrWZ, this.certFile, zp, dostawca, pkd, dataDostawy, idMaterialu) != -1) {
                return 1;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Zaznacz poprawny węzeł");
        }
        return -1;
    }

    /**
     * Zapełnia tabelę atestami.
     *
     * @param tblAtesty Tabela z atestami
     * @param treeMaterialy Drzewo z materiałami
     * @param mode Tryb wyszukiwania 0 - wg materiału, 1 - wg kryteriów, 2 -
     * Zamówienia, 3 - ZP, 4 - Dostawca
     * @param filter Filter
     */
    public void readCerts(JTable tblAtesty, JTree treeMaterialy, int mode, String filter) {

        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeMaterialy.getLastSelectedPathComponent();
        DefaultTableModel model = (DefaultTableModel) tblAtesty.getModel();

        ((DefaultTableModel) tblAtesty.getModel()).setRowCount(0);

        int idMaterialu = 0;

        ResultSet resultSet;

        if (mode != 0 && filter.length() > 0) {
            resultSet = DatabaseManager.getInstance().getAtestOfMaterial(filter, mode);
        } else {
            idMaterialu = DatabaseManager.getInstance().getIdOfMaterial(
                    selectedNode.toString());

            resultSet = DatabaseManager.getInstance().getAtestOfMaterial(idMaterialu);
            //resultSet = DatabaseManager.getInstance().getAtestOfMaterial2(
            //        selectedNode.toString());
        }

        // Dane wiersza w tabeli
        Object rowData[] = new Object[7];

        if (resultSet != null) {
            try {
                atesty = new ArrayList<>();

                //atesty = new Atest[DatabaseManager.getInstance().getSizeOfResuleSet(resultSet)];
                int tableIndex = 0;
                resultSet.first();
                do {

                    atesty.add(new Atest());
                    atesty.get(tableIndex).setId(resultSet.getInt("id"));
                    atesty.get(tableIndex).setNazwa(resultSet.getString("nazwa"));
                    atesty.get(tableIndex).setNr_zamowienia(resultSet.getString("nr_zamowienia"));
                    atesty.get(tableIndex).setWz(resultSet.getString("nr_wz"));
                    atesty.get(tableIndex).setDate(resultSet.getString("data_dodania"));
                    atesty.get(tableIndex).setZp(resultSet.getString("zp"));
                    atesty.get(tableIndex).setDostawca(resultSet.getString("dostawca"));
                    atesty.get(tableIndex).setPkd(resultSet.getString("pkd"));
                    atesty.get(tableIndex).setPositionInTable(tableIndex);

                    //atesty[tableIndex] = new Atest();
                    //atesty[tableIndex].setId(resultSet.getInt("id"));
                    //atesty[tableIndex].setPositionInTable(tableIndex);
                    rowData[0] = resultSet.getString("nazwa");
                    rowData[1] = resultSet.getString("nr_zamowienia");
                    rowData[2] = resultSet.getString("nr_wz");
                    rowData[3] = resultSet.getString("data_dodania");
                    rowData[4] = resultSet.getString("zp");
                    rowData[5] = resultSet.getString("dostawca");
                    rowData[6] = resultSet.getString("pkd");
                    model.addRow(rowData);

                    /* 
                        Sprawdzenie atestów w tablicy atestów i oznaczenie atestu
                        jako czerownego (TRUE) jeżeli w polu brakuje danych lub 
                        atest nie zawiera pliku certyfikatu.
                     */
                    for (Object data : rowData) {
                        /* Sprawdza czy kolumny są kompletne oraz czy atest zawiera certyfikat */
                        if (data == null || data.hashCode() == 0 || resultSet.getInt("braki_cert") > 0) {
                            atesty.get(tableIndex).setRedStatus(true);
                            //atesty[tableIndex].setRedStatus(true);
                            DatabaseManager.getInstance().lackAddToMaterial(idMaterialu, 1);
                        } else {
                            DatabaseManager.getInstance().lackAddToMaterial(idMaterialu, 0);
                        }
                    }

                    tableIndex += 1;

                } while (resultSet.next());
            } catch (SQLException ex) {
                Logger.getLogger(AtestManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Odczytuje ścieżki do plików dla zadanego atestu.
     *
     * @param tblAtesty Tabela z atestami.
     * @param tblPathAtesty Tabela z ścieżkami do plików.
     * @param atesty Tablica z atestami.
     * @param certPath Ścieżka do katalogu z atestami.
     */
    public void readCertsFiles(JTable tblAtesty, JTable tblPathAtesty,
            ArrayList<Atest> atesty, String certPath) {

        if (tblAtesty.getSelectedRowCount() > 0) {

            ResultSet resultSet = DatabaseManager.getInstance()
                    .getCertsFilePaths(atesty.get(tblAtesty.getSelectedRow()).getId());

            DefaultTableModel model = (DefaultTableModel) tblPathAtesty.getModel();
            model.setRowCount(0);

            Object[] rowData = new Object[1];

            try {
                if (DatabaseManager.getInstance().getSizeOfResuleSet(resultSet)
                        > 0) {

                    certPaths = new CertPath[DatabaseManager.getInstance().getSizeOfResuleSet(
                            resultSet)];

                    resultSet.first();
                    int tableIndex = 0;

                    do {
                        certPaths[tableIndex] = new CertPath();
                        certPaths[tableIndex].setId(resultSet.getInt("id"));
                        certPaths[tableIndex].setCertId(resultSet.getInt("id_atest"));
                        certPaths[tableIndex].setPath(resultSet.getString("sciezka"));
                        certPaths[tableIndex].setNazwa(resultSet.getString("nazwa"));
                        certPaths[tableIndex].setTableInex(tableIndex);

                        rowData[0] = resultSet.getString("nazwa");
                        //rowData[1] = certPath + resultSet.getString("sciezka");

                        model.addRow(rowData);
                        tableIndex += 1;

                    } while (resultSet.next());
                } else {
                    /* W przypadku nie wykrycia plików z certyfikatami do bazy
                        danych zapisana zostaje informacja o ich braku.
                     */
                    DatabaseManager.getInstance().lackAddToCert(
                            atesty.get(tblAtesty.getSelectedRow()).getId(), 1);
                }

            } catch (SQLException ex) {
                Logger.getLogger(AtestManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Wczytuje do tablicy ścieżki do plików zaznaczonego atestu.
     *
     * @param tblAtesty Tablica z atestami.
     * @return -1 jeżeli błąd
     */
    public int copyCertsFiels(JTable tblAtesty) {

        certPaths = null;

        if (tblAtesty.getSelectedRowCount() > 0) {
            ResultSet resultSet = DatabaseManager.getInstance()
                    .getCertsFilePaths(atesty.get(tblAtesty.getSelectedRow()).getId());

            try {
                if (DatabaseManager.getInstance().getSizeOfResuleSet(resultSet)
                        > 0) {

                    certPaths = new CertPath[DatabaseManager.getInstance().getSizeOfResuleSet(
                            resultSet)];

                    resultSet.first();
                    int tableIndex = 0;

                    do {
                        certPaths[tableIndex] = new CertPath();
                        certPaths[tableIndex].setId(resultSet.getInt("id"));
                        certPaths[tableIndex].setCertId(resultSet.getInt("id_atest"));
                        certPaths[tableIndex].setPath(resultSet.getString("sciezka"));
                        certPaths[tableIndex].setNazwa(resultSet.getString("nazwa"));
                        certPaths[tableIndex].setTableInex(tableIndex);

                        tableIndex += 1;

                    } while (resultSet.next());
                } else {
                    /* W przypadku nie wykrycia plików z certyfikatami do bazy
                        danych zapisana zostaje informacja o ich braku.
                     */
                    DatabaseManager.getInstance().lackAddToCert(
                            atesty.get(tblAtesty.getSelectedRow()).getId(), 1);
                }

                return 1;
            } catch (SQLException ex) {
                Logger.getLogger(AtestManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return -1;
    }

    /**
     * Kopiuje ścieżki do plików z certyfikatami do zaznaczonego w tabeli atestu
     *
     * @param atestId Id atestu do którego skopiowanę będą ścieżki do plików.
     * @return -1 jeżeli błąd.
     */
    public int pasteCertsFiles(int atestId) {
        if (certPaths != null) {
            for (CertPath certPath : certPaths) {
                DatabaseManager.getInstance().addCertFilePath(atestId, certPath.getPath(),
                        certPath.getNazwa());
            }
            return 1;
        }
        return -1;
    }

    /**
     * Pokazuje okno dialogowe z ścieżką do atestu.
     *
     * @param tblPathAtesty Tabela z plikami atestów.
     * @param mainPath
     */
    public void showCertPath(JTable tblPathAtesty, String mainPath) {
        //certPaths[tblPathAtesty.getSelectedRow()].getPath();
        JOptionPane.showMessageDialog(null, mainPath + certPaths[tblPathAtesty.getSelectedRow()].getPath());
    }

    /**
     * Otwiera folder z atestem
     *
     * @param tblPathAtesty Tabela z plikami atestów
     * @param mainPath Główna ścieżka w której znajdują się wszystkie atesty.
     */
    public void openFolder(JTable tblPathAtesty, String mainPath) {
        try {
            Runtime.getRuntime().exec("explorer " + mainPath + certPaths[tblPathAtesty.getSelectedRow()].getPath());
        } catch (IOException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Ustala ścieżkę do fizycznego pliku z atestem.
     *
     * @param certDirectory Lokalizacja folderu z atestami.
     * @param lblSciezka Etykieta wyświetlająca ścieżkę.
     */
    public void addPath(String certDirectory, JLabel lblSciezka) {

        JFileChooser fileChooser = new JFileChooser();

        File directory = new File(certDirectory);

        fileChooser.setCurrentDirectory(directory);

        fileChooser.setMultiSelectionEnabled(true);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

            File file = fileChooser.getSelectedFile();
            File[] readedFiles = fileChooser.getSelectedFiles();
            this.certFile = file;
            this.files = readedFiles;

//            for (File x: this.files){
//                System.out.println("ścieżka do pliku: " + x.toString());
//            }
            //System.out.println("Ścieżka do pliku: " + file.getPath());
            //System.out.println("Ścieżka(A) do pliku: " + file.getAbsolutePath());
            lblSciezka.setText(file.getPath());
        }
    }

    /**
     * Dodaje wybrane ścieżki do plików z atestami do tabeli w zakładce nowego
     * atestu.
     *
     * @param certDirectory Lokalizacja folderu z atestami
     * @param tblNewCertFiles Tabela przechowująca dane dot. lokalizacji plików
     * z atestami.
     */
    public void addPathIntoNewCertTable(String certDirectory, JTable tblNewCertFiles) {
        
        DefaultTableModel tableModel = (DefaultTableModel) tblNewCertFiles.getModel();
        
        JFileChooser fileChooser = new JFileChooser();
        File directory = new File(certDirectory);
        fileChooser.setCurrentDirectory(directory);
        fileChooser.setMultiSelectionEnabled(true);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            File[] readedFiles = fileChooser.getSelectedFiles();
            this.certFile = file;
            this.files = readedFiles;
            
            Object rowData[] = new Object[2];
            
            for (File checkedFile: readedFiles){
                String name = JOptionPane.showInputDialog(null, "Podaj opis dla: " + checkedFile.getName());
                rowData[0] = name;
                rowData[1] = checkedFile.getPath();
                tableModel.addRow(rowData);
                
                
            }
        }
    }

    /**
     * Dodaje do bazy danych oraz tabeli ścieżkę do pliku z atestem
     *
     * @param tblAtesty Tabela atestów.
     * @param tblPathAtesty Tabela ścieżki do pliku z atestem.
     * @param CertPath Ścieżka do katalogu z atestami.
     */
    public void addFilePathIntoTable(JTable tblAtesty, JTable tblPathAtesty, String CertPath) {

        if (this.files.length > 0) {
            for (File file : files) {

                String name = JOptionPane.showInputDialog(null, "Podaj opis dla: " + file.getName());

                DatabaseManager.getInstance().addCertFilePath(file,
                        atesty.get(tblAtesty.getSelectedRow()).getId() /*atesty[tblAtesty.getSelectedRow()].getId()*/,
                        CertPath,
                        name);
            }
            this.files = null;
        } else {
            JOptionPane.showMessageDialog(null, "Nie wybrano pliku z atestem.");
        }
    }

    /**
     * Otwiera fizyczny plik z atestem.
     *
     * @param tblPathAtesty Tabela z plikami atestów.
     * @param sciezka Ścieżka główna do katalogu z atestami..
     */
    public void openCertFile(JTable tblPathAtesty, String sciezka) {

        String finalPath = sciezka + certPaths[tblPathAtesty.getSelectedRow()].getPath();

        System.out.println(finalPath);

        File file = new File(finalPath);
        if (file.toString().endsWith(".jpeg") || file.toString().endsWith(".pdf")) {
            try {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + file);
            } catch (IOException ex) {
                Logger.getLogger(AtestManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(file);
            } catch (IOException ex) {
                Logger.getLogger(AtestManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Zmienia nazwę materiału.
     *
     * @param treeMaterialy Drzewko z materiałami.
     * @return zwraca -1 jeżeli błąd.
     */
    public int renameMaterialName(JTree treeMaterialy) {

        if (treeMaterialy.getLastSelectedPathComponent() != null) {

            // Sprawdza czy węzeł jest węzłem głównym
            if (((DefaultMutableTreeNode) treeMaterialy.getLastSelectedPathComponent()).isRoot()) {
                JOptionPane.showMessageDialog(null, "Nie można zmienić nazwy głównego węzła.");
            } else {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeMaterialy.getLastSelectedPathComponent();

                String name = JOptionPane.showInputDialog(null, "Podaj nową nazwę węzła");

                if (name != null && !name.isEmpty() && name.length() < 64) {

                    //System.out.println(selectedNode.toString());
                    name = name.toUpperCase();

                    if (DatabaseManager.getInstance().renameMaterialNode(name, selectedNode.toString()) == 1) {
                        selectedNode.setUserObject(name);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Zaznacz węzeł.");
        }

        return -1;
    }

    /**
     * Usuwa węzeł materiału z drzewka materiałów
     *
     * @param treeMaterialy Drzewko materiałów.
     * @return -1 jeżeli błąd.
     */
    public int removeMaterialNode(JTree treeMaterialy) {
        if (treeMaterialy.getLastSelectedPathComponent() != null) {

            // Sprawdza czy węzeł jest węzłem głównym
            if (((DefaultMutableTreeNode) treeMaterialy.getLastSelectedPathComponent()).isRoot()) {
                JOptionPane.showMessageDialog(null, "Nie można usunąć głównego węzła.");
                return -1;
            } else {

                DefaultTreeModel model = (DefaultTreeModel) treeMaterialy.getModel();
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeMaterialy.getLastSelectedPathComponent();

                if (DatabaseManager.getInstance().removeMaterialNode(selectedNode.toString()) == 1) {
                    TreePath[] paths = treeMaterialy.getSelectionPaths();
                    if (paths != null) {
                        for (TreePath path : paths) {
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                            if (node.getParent() != null) {
                                model.removeNodeFromParent(node);
                            }
                        }
                    }
                    return 1;
                } else {
                    return -1;
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Zaznacz węzeł.");
            return -1;
        }
    }

    /**
     * Usuwa atest z tabeli atestów oraz bazy danych.
     *
     * @param tblAtesty Tabela atestów.
     * @return -1 jeżeli błąd.
     */
    public int removeCert(JTable tblAtesty) {
        if (DatabaseManager.getInstance().removeCert(
                atesty.get(tblAtesty.getSelectedRow()).getId()
        /*atesty[tblAtesty.getSelectedRow()].getId()*/) == 1) {

            DefaultTableModel model = (DefaultTableModel) tblAtesty.getModel();
            model.removeRow(tblAtesty.getSelectedRow());
            return 1;
        }
        return -1;
    }

    /**
     * Usuwa zaznaczoną ścieżkę z tabeli oraz uruchamia usuwanie ścieżki z bazy
     * danych.
     *
     * @param tblPathAtesty Tabela przechowująca ścieżki do plików z atestami.
     * @return -1 jeżeli błąd.
     */
    public int removeCertFile(JTable tblPathAtesty) {
        if (DatabaseManager.getInstance().removeCertFile(
                certPaths[tblPathAtesty.getSelectedRow()].getId()) == 1) {

            DefaultTableModel model = (DefaultTableModel) tblPathAtesty.getModel();
            model.removeRow(tblPathAtesty.getSelectedRow());
            return 1;
        }
        return -1;
    }

    /**
     * Zmienia nazwę atestu.
     *
     * @param tblAtesty Tabela z atestami.
     * @return zwraca -1 jeżeli błąd.
     */
    public int editCertName(JTable tblAtesty) {
        String name = JOptionPane.showInputDialog(null, "Podaj nazwę materiału:");

        if (tblAtesty.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Zaznacz atest");
            return -1;
        }

        if (name != null && !name.isEmpty()) {
            if (DatabaseManager.getInstance().renameCertName(name,
                    atesty.get(tblAtesty.getSelectedRow()).getId()
            /*atesty[tblAtesty.getSelectedRow()].getId()*/) == 1) {
                DefaultTableModel model = (DefaultTableModel) tblAtesty.getModel();
                model.setValueAt(name, tblAtesty.getSelectedRow(), 0);
                atesty.get(tblAtesty.getSelectedRow()).setNazwa(name);
                return 1;
            }
        }
        return -1;
    }

    /**
     * Zmienia nazwę atestu.
     *
     * @param tblAtesty Tabela z atestami.
     * @return zwraca -1 jeżeli błąd.
     */
    public int editCertOrder(JTable tblAtesty) {
        String name = JOptionPane.showInputDialog(null, "Podaj numer zamówienia:");

        if (tblAtesty.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Zaznacz atest");
            return -1;
        }

        if (name != null && !name.isEmpty()) {
            if (DatabaseManager.getInstance().renameCertOrder(name,
                    atesty.get(tblAtesty.getSelectedRow()).getId()
            /*atesty[tblAtesty.getSelectedRow()].getId()*/) == 1) {
                DefaultTableModel model = (DefaultTableModel) tblAtesty.getModel();
                model.setValueAt(name, tblAtesty.getSelectedRow(), 1);
                atesty.get(tblAtesty.getSelectedRow()).setNr_zamowienia(name);
                return 1;
            }
        }
        return -1;
    }

    /**
     * Zmienia numer WZ.
     *
     * @param tblAtesty Tabela z atestami.
     * @return zwraca -1 jeżeli błąd.
     */
    public int editCertWZ(JTable tblAtesty) {
        String name = JOptionPane.showInputDialog(null, "Podaj numer WZ:");

        if (tblAtesty.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Zaznacz atest");
            return -1;
        }

        if (name != null && !name.isEmpty()) {
            if (DatabaseManager.getInstance().renameCertWz(name,
                    atesty.get(tblAtesty.getSelectedRow()).getId()
            /*atesty[tblAtesty.getSelectedRow()].getId()*/) == 1) {
                DefaultTableModel model = (DefaultTableModel) tblAtesty.getModel();
                model.setValueAt(name, tblAtesty.getSelectedRow(), 2);
                atesty.get(tblAtesty.getSelectedRow()).setWz(name);
                return 1;
            }
        }
        return -1;
    }

    /**
     * Zmiana numeru dostawcy
     *
     * @param tblAtesty Tabela z atestami
     * @return -1 jeżeli błąd
     */
    public int editCertSupplier(JTable tblAtesty) {
        String name = JOptionPane.showInputDialog(null, "Podaj nazwę dostawcy:");

        if (tblAtesty.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Zaznacz atest");
            return -1;
        }

        if (name != null && !name.isEmpty()) {
            if (DatabaseManager.getInstance().renameCertSupplier(name,
                    atesty.get(tblAtesty.getSelectedRow()).getId()
            /*atesty[tblAtesty.getSelectedRow()].getId()*/) == 1) {
                DefaultTableModel model = (DefaultTableModel) tblAtesty.getModel();
                model.setValueAt(name, tblAtesty.getSelectedRow(), 5);
                atesty.get(tblAtesty.getSelectedRow()).setDostawca(name);
                return 1;
            }
        }
        return -1;
    }

    /**
     * Zmiana numeru PKD
     *
     * @param tblAtesty Tabela z atestami
     * @return -1 jeżeli błąd
     */
    public int editCertPKD(JTable tblAtesty) {
        String name = JOptionPane.showInputDialog(null, "Podaj numer PKD:");

        if (tblAtesty.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Zaznacz atest");
            return -1;
        }

        if (name != null && !name.isEmpty()) {
            if (DatabaseManager.getInstance().renameCertPkd(name,
                    atesty.get(tblAtesty.getSelectedRow()).getId()
            /*atesty[tblAtesty.getSelectedRow()].getId()*/) == 1) {
                DefaultTableModel model = (DefaultTableModel) tblAtesty.getModel();
                model.setValueAt(name, tblAtesty.getSelectedRow(), 6);
                atesty.get(tblAtesty.getSelectedRow()).setPkd(name);
                return 1;
            }
        }
        return -1;
    }

    /**
     * Zmiana Numeru ZP
     *
     * @param tblAtesty Tabela z atestami
     * @return -1 jeżeli błąd
     */
    public int editCertZp(JTable tblAtesty) {
        String name = JOptionPane.showInputDialog(null, "Podaj numer ZP:");

        if (tblAtesty.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Zaznacz atest");
            return -1;
        }

        if (name != null && !name.isEmpty()) {
            if (DatabaseManager.getInstance().renameCertZp(name,
                    atesty.get(tblAtesty.getSelectedRow()).getId()
            /*atesty[tblAtesty.getSelectedRow()].getId()*/) == 1) {
                DefaultTableModel model = (DefaultTableModel) tblAtesty.getModel();
                model.setValueAt(name, tblAtesty.getSelectedRow(), 4);
                atesty.get(tblAtesty.getSelectedRow()).setZp(name);
                return 1;
            }
        }
        return -1;
    }

    /**
     * Zmienia datę dostawy
     *
     * @param tblAtesty Tabela z atestami
     * @return -1 jeżeli błąd.
     */
    public int editCertDeliveryDate(JTable tblAtesty) {

        if (tblAtesty.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Zaznacz atest");
            return -1;
        }

        JDateChooser jd = new JDateChooser();
        String message = "Wybierz datę dostawy:\n";
        Object[] params = {message, jd};
        JOptionPane.showConfirmDialog(null, params, "Start date", JOptionPane.PLAIN_MESSAGE);

        String s = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            s = sdf.format(((JDateChooser) params[1]).getDate());

            if (s != null) {
                if (DatabaseManager.getInstance().renameCertDeliveryDate(s,
                        atesty.get(tblAtesty.getSelectedRow()).getId()
                /*atesty[tblAtesty.getSelectedRow()].getId()*/) == 1) {
                    DefaultTableModel model = (DefaultTableModel) tblAtesty.getModel();
                    model.setValueAt(s, tblAtesty.getSelectedRow(), 3);
                    atesty.get(tblAtesty.getSelectedRow()).setDate(s);
                    return 1;
                }
            }
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "Nie wybrano daty.");
        }
        return -1;
    }

    /**
     * Zwraca plik z certyfikatem.
     *
     * @return Plik
     */
    public File getCertFile() {
        return certFile;
    }

    /**
     * Zmienia opis pliku z atestem
     *
     * @param tablePathCerts Tabela z plikami atestów
     * @return -1 jeżeli błąd
     */
    public int editCertFileDiscription(JTable tablePathCerts) {
        String name = JOptionPane.showInputDialog(null, "Podaj nowy opis certyfikatu");

        if (name != null && !name.isEmpty()) {
            if (DatabaseManager.getInstance().renameCertFileDescription(name,
                    certPaths[tablePathCerts.getSelectedRow()].getId()) == 1) {
                return 1;
            }
        }
        return -1;
    }

    /**
     * Sortowanie
     *
     * @param tblAtesty Tabela atestów.
     * @param mode Według której kolumny ma nastąpić sortowanie,
     */
    public void sortCerts(JTable tblAtesty, SortModes mode) {

        DefaultTableModel model = (DefaultTableModel) tblAtesty.getModel();
        ((DefaultTableModel) tblAtesty.getModel()).setRowCount(0);

        if (atesty != null) {
            switch (mode) {
                case ATEST_NAZWA_UP:
                    Collections.sort(atesty, MmComparators.atestNameUpComparator);
                    break;
                case ATEST_NAZWA_DOWN:
                    Collections.sort(atesty, MmComparators.atestNameDownComparator);
                    break;
                case ATEST_DATA_UP:
                    Collections.sort(atesty, MmComparators.atestDateUpComparator);
                    break;
                case ATEST_DATA_DOWN:
                    Collections.sort(atesty, MmComparators.atestDateDownComparator);
                    break;
                case ATEST_NRZAM_UP:
                    Collections.sort(atesty, MmComparators.atestNrZamowieniaUpComparator);
                    break;
                case ATEST_NRZAM_DOWN:
                    Collections.sort(atesty, MmComparators.atestNrZamowieniaDownComparator);
                    break;
                case ATEST_WZ_UP:
                    Collections.sort(atesty, MmComparators.atestWzUpComparator);
                    break;
                case ATEST_WZ_DOWN:
                    Collections.sort(atesty, MmComparators.atestWzDownComparator);
                    break;
                case ATEST_ZP_UP:
                    Collections.sort(atesty, MmComparators.atestZpUpComparator);
                    break;
                case ATEST_ZP_DOWN:
                    Collections.sort(atesty, MmComparators.atestZpDownComparator);
                    break;
                case ATEST_DOSTAWCA_UP:
                    Collections.sort(atesty, MmComparators.atestDostawcaUpComparator);
                    break;
                case ATEST_DOSTAWCA_DOWN:
                    Collections.sort(atesty, MmComparators.atestDostawcaDownComparator);
                    break;
                case ATEST_PKD_UP:
                    Collections.sort(atesty, MmComparators.atestPkdUpComparator);
                    break;
                case ATEST_PKD_DOWN:
                    Collections.sort(atesty, MmComparators.atestPkdDownComparator);
                    break;
            }

            // Dane wiersza w tabeli
            Object rowData[] = new Object[7];

            int tableIndex = 0;

            for (Atest atest : atesty) {
                rowData[0] = atest.getNazwa();
                rowData[1] = atest.getNr_zamowienia();
                rowData[2] = atest.getWz();
                rowData[3] = atest.getDate();
                rowData[4] = atest.getZp();
                rowData[5] = atest.getDostawca();
                rowData[6] = atest.getPkd();
                atest.setPositionInTable(tableIndex);
                model.addRow(rowData);
                tableIndex += 1;
            }
        }
    }

    /**
     * Zapisuje wybrany z tabeli atestów atest w schowku.
     *
     * @param tblAtesty Tabela z atestami.
     * @param certOperations Typ operacji - kopiowanie lub wycinanie atestu.
     * @return -1 jeżeli błąd.
     */
    public int cutCopyCert(JTable tblAtesty, CertOperations certOperations) {
        if (tblAtesty.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Zaznacz atest");
            return -1;
        }
        AtestManager.getInstance().setClipboard(atesty.get(
                tblAtesty.getSelectedRow()));
        AtestManager.getInstance().certOperations = certOperations;
        // Skopiowanie ścieżek plików certyfikatów do tablicy
        copyCertsFiels(tblAtesty);

        return 1;
    }

//  public int copyCert(JTable tblAtesty) {
//    if (tblAtesty.getSelectedRow() == -1) {
//      JOptionPane.showMessageDialog(null, "Zaznacz atest");
//      return -1;
//    }
//    return -1;
//  }
    /**
     * Zmienia materiał do którego przynależy atest w schowku.
     *
     * @param treeMaterialy Drzewko z materiałami
     * @param certOperations Typ operacji która zostanie przeprowadzona na
     * ateście. Kopiowanie lub wklejanie po uprzednim wycięciu z materiału.
     * @return -1 jeżeli błąd.
     */
    public int pasteCert(JTree treeMaterialy, CertOperations certOperations) {
        if (treeMaterialy.getLastSelectedPathComponent() != null) {

            // Sprawdza czy węzeł jest węzłem głównym
            if (((DefaultMutableTreeNode) treeMaterialy.getLastSelectedPathComponent()).isRoot()) {
                JOptionPane.showMessageDialog(null, "Nie można wkleić do głównego węzła.");
            } else {
                switch (certOperations) {
                    case CUT:
                        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeMaterialy.getLastSelectedPathComponent();
                        DatabaseManager.getInstance().renameCertMaterialId(selectedNode.toString());
                        break;
                    case COPY:
                        this.addNewCert(clipboard.getNazwa(), clipboard.getNr_zamowienia(),
                                clipboard.getWz(), clipboard.getSciezka(), clipboard.getZp(),
                                clipboard.getDostawca(), clipboard.getPkd(), clipboard.getDate(),
                                treeMaterialy);
                        //System.out.println("ID OSTATNIO DODANEGO ATESTU: " + DatabaseManager.getInstance().getLastInsertIdInAtests());
                        int idAtestu = Integer.parseInt(DatabaseManager.getInstance().getLastInsertIdInAtests());
                        this.pasteCertsFiles(idAtestu);
                        this.certPaths = null;
                        break;
                }
            }

        } else {
            JOptionPane.showMessageDialog(null, "Zaznacz węzeł.");
        }

        return -1;
    }

    private AtestManager() {
    }

    public static AtestManager getInstance() {
        return AtestManagerHolder.INSTANCE;
    }

    private static class AtestManagerHolder {

        private static final AtestManager INSTANCE = new AtestManager();
    }

    /**
     * Zwraca tablicę z atestami.
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
     * Tablica z materiałami, konieczna do odczytu ewentualnych braków.
     *
     * @return materiały
     */
    public ArrayList<Material> getMaterials() {
        return materials;
    }

    /**
     * @return Zwraca atest przechowywany w schowku
     */
    public Atest getClipboard() {
        return clipboard;
    }

    /**
     * Zapisuje wycięty z tabeli atestów atest do schowka.
     *
     * @param clipboard Atest.
     */
    public void setClipboard(Atest clipboard) {
        this.clipboard = clipboard;
    }

    /**
     * Zwraca typ operacji przeprowadzanej na ateście
     *
     * @return Typ operacji
     */
    public CertOperations getCertOperations() {
        return certOperations;
    }

    public File[] getFiles() {
        return files;
    }
}
