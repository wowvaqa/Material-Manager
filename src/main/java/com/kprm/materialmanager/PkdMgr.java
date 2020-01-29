/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kprm.materialmanager;

import MyClasses.Atest;
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/**
 * Klasa do zarządzania dokumentami PKD
 *
 * @author wakan
 */
public class PkdMgr {

    XWPFDocument _wordDoc;
    XWPFDocument _wordDocToSave;

    public static String _nrPkd;
    public static String _sprawdzajacy;
    public static String _kontrakt;
    public static String _data;
    public static String _dostawca;
    public static JTable _tablePkd;

    /**
     * Lista zapamiętuje wszystkie znalezione PKD. *
     */
    private ArrayList<Atest> findedCerts;
    /**
     * Lista przechowuje wybrane atesty dla których ma zostać nadany numer PKD. *
     */
    private ArrayList<Atest> toPkdCerts;

    /**
     * Otwiera plik
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void readDocx() throws FileNotFoundException, IOException {

        File dirFrom = new File("./data/data.docx");
        //File dirTo = new File("./data/wzor.docx");
        File dirTo = new File(System.getProperty("java.io.tmpdir") + "wzor.docx");

        System.out.println(System.getProperty("java.io.tmpdir"));

        try {
            Files.deleteIfExists(dirTo.toPath());
            copyFile(dirFrom, dirTo);
        } catch (IOException ex) {

            Logger.getLogger(PkdMgr.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    /**
     * Kopiuje plik
     *
     * @param from plik do skopiowania
     * @param to Nowy plik
     * @throws IOException
     */
    public static void copyFile(File from, File to) throws IOException {
        Files.copy(from.toPath(), to.toPath());
    }

    /**
     * Zapełnia tabelę rezultatów wyszukiwania atestów.
     *
     * @param tblPkdResult Tabela do której mają zostać wrzucone wyniki
     * wyszukiwania
     * @param filter Filter wg którego ma nastąpić wyszukanie.
     * @param mode Tryb wyszukiwania 0 - wg materiału, 1 - wg kryteriów, 2 -
     * Zamówienia, 3 - ZP, 4 - Dostawca
     * @return -1 jeżeli błąd
     */
    public int fillPkdResultTable(JTable tblPkdResult, String filter, int mode) {

        findedCerts = new ArrayList<>();

        if (filter.length() < 1) {
            return -1;
        }

        DefaultTableModel _model = (DefaultTableModel) tblPkdResult.getModel();
        _model.setRowCount(0);

        ResultSet _resultSet;
        _resultSet = DatabaseManager.getInstance().getAtestOfMaterial(
                filter, mode);

        // Dane wiersza w tabeli
        Object _rowData[] = new Object[6];

        if (_resultSet != null) {
            try {
                _resultSet.first();

                do {
                    String _nameOfMaterial = DatabaseManager.getInstance().getNameOfMaterial(
                            _resultSet.getInt("id_materialu")
                    );

                    if (_nameOfMaterial == null) {
                        _nameOfMaterial = "błąd odzytu nazwy materiału";
                    }

                    Atest atest = new Atest();

                    atest.setId(_resultSet.getInt("id"));
                    atest.setNazwa(_resultSet.getString("nazwa"));
                    atest.setWz(_resultSet.getString("nr_wz"));
                    findedCerts.add(atest);

                    _rowData[0] = _nameOfMaterial;
                    _rowData[1] = _resultSet.getString("nazwa");
                    _rowData[2] = _resultSet.getString("nr_zamowienia");
                    _rowData[3] = _resultSet.getString("nr_wz");
                    _rowData[4] = _resultSet.getString("zp");
                    _rowData[5] = _resultSet.getString("dostawca");
                    _model.addRow(_rowData);

                } while (_resultSet.next());

            } catch (SQLException ex) {
                Logger.getLogger(PkdMgr.class.getName()).log(
                        Level.SEVERE, null, ex);
                return -1;
            }
        }
        return 0;
    }

    /**
     * Przenosi zaznaczone wiersze z tabeli rezultatów do tabeli PKD.
     *
     * @param tblFrom Tabela z której będą kopiowane dane.
     * @param tblTo Tabela do której będą kopiowane dane.
     * @return -1 jeżeli błąd.
     */
    public int moveData(JTable tblFrom, JTable tblTo) {

        DefaultTableModel _modelFrom;
        _modelFrom = (DefaultTableModel) tblFrom.getModel();

        DefaultTableModel _modelTo;
        _modelTo = (DefaultTableModel) tblTo.getModel();

        //toPkdCerts = new ArrayList<>();
        for (int i : tblFrom.getSelectedRows()) {

            toPkdCerts.add(findedCerts.get(i));

            Object _rowData[] = new Object[9];

            _rowData[0] = _modelFrom.getValueAt(i, 0);
            _rowData[1] = _modelFrom.getValueAt(i, 1);
            _rowData[2] = _modelFrom.getValueAt(i, 2);
            _rowData[3] = _modelFrom.getValueAt(i, 3);
            _rowData[4] = _modelFrom.getValueAt(i, 4);
            _rowData[5] = _modelFrom.getValueAt(i, 5);
            _rowData[8] = true;
            _modelTo.addRow(_rowData);
        }

        return -1;
    }

    /**
     * Usuwa zadany indeksem certyfikat z listy toPKD
     *
     * @param index Indeks atestu na liście toPKD do usunięcia
     */
    public void removeCertFromToPkdTable(int index) {
        toPkdCerts.remove(index);
    }

    /**
     * Usuwa wszystkie certyfikaty z listy certyfiaktów do zapisania nr PKD
     */
    public void clearCertInToPkdTable() {
        toPkdCerts.clear();
    }

    /**
     * Nadpisuje numery PKD wg tabeli atestów PKD
     *
     * @return -1 jeżeli błąd
     */
    public int updatePkdNumbers() {

        for (Atest atest : toPkdCerts) {
            DatabaseManager.getInstance().renameCertPkd(_nrPkd, atest.getId());
        }
        return -1;
    }

    private void debugInfo() {

        System.out.println(" Rozmiar tablicy: " + toPkdCerts.size());

        for (Atest atest : toPkdCerts) {
            System.out.print(" Id Atestu: " + atest.getId() + " <---> ");
            System.out.print(" WZ Atestu: " + atest.getWz() + " <---> ");
            System.out.println(" Nazwa Atestu: " + atest.getNazwa());
        }
    }

    /**
     * Tworzy nowy dokument PKD
     */
    public void createPkd() {
        //System.out.println("Paragrafy: " + _wordDoc.getParagraphs().size());

        //System.out.println("Tabele: " + _wordDoc.getTables().size());
        //System.out.println("Body elements: " + _wordDoc.getBodyElements().size());
        /**
         * Przeszukanie paragrafów *
         */
        for (XWPFParagraph par : _wordDoc.getParagraphs()) {
            //System.out.println("Paragraf: " + par.toString() + " TEXT: " + par.getText());
            if (par.getText().contains("@nr_pkd")) {
                //System.out.println("Znaleziono numer PKD");
                //par.getText().replace("@nr_pkd", "Witam");

                String text;
                text = par.getText();

                String newText;
                newText = text.replace("@nr_pkd", "Witam");

                XWPFRun run = par.createRun();
                run.setText(newText);

            }
        }

        for (XWPFTable tab : _wordDoc.getTables()) {

            //System.out.println("rzędy tabeli " + tab.toString() + ": " + tab.getRows().size());
            for (XWPFTableRow row : tab.getRows()) {
                //System.out.println("Rząd " + row.toString() + " : " + row.getTableICells().size());

                for (XWPFTableCell cell : row.getTableCells()) {

                    //System.out.println("Cell text: " + cell.getText());
//                    if (cell.getText().contains("@_k_001")){
//                        System.out.println("Znaleziono numer kontraktu");
//                    }
                }

            }

        }

        try {
            try (FileOutputStream fos = new FileOutputStream(new File("./docs/final.docx"))) {
                _wordDoc.write(fos);
            }
            _wordDoc.close();

            //XWPFParagraph par = wordDoc.getLastParagraph();
//        for (XWPFParagraph par: wordDoc.getParagraphs()){
//            System.out.println("Paragraf: " + par.getText());
//        }
        } catch (IOException ex) {
            Logger.getLogger(PkdMgr.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void findAndReplaceInDocX(Component component) throws IOException,
            InvalidFormatException,
            org.apache.poi.openxml4j.exceptions.InvalidFormatException {
        try {
            try (
                    //XWPFDocument doc = new XWPFDocument(
                    //        OPCPackage.open("./data/wzor.docx"))) {

                    XWPFDocument doc = new XWPFDocument(
                            OPCPackage.open(System.getProperty("java.io.tmpdir") + "wzor.docx"))) {

                for (XWPFTable tab : doc.getTables()) {
                    System.out.println("-----> TABELA: " + tab.toString());
                    for (XWPFTableRow tabRow : tab.getRows()) {
                        for (XWPFTableCell tabCell : tabRow.getTableCells()) {
                            for (XWPFParagraph p : tabCell.getParagraphs()) {
                                List<XWPFRun> runsTmp = p.getRuns();
                                if (runsTmp != null) {
                                    System.out.println("--> RUNS:" + runsTmp.toString());
                                    for (XWPFRun r : runsTmp) {
                                        replaceSprawdzajacy(r);
                                        replaceKontrakt(r);
                                        replaceData(r);
                                        replaceZp(r);
                                        replaceZamowienie(r);
                                        replaceWz(r);
                                        replaceDostawca(r);
                                        replaceDataInTable(r);
                                    }
                                }
                            }
                        }
                    }
                }

                for (XWPFParagraph p : doc.getParagraphs()) {
                    List<XWPFRun> runs = p.getRuns();
                    if (runs != null) {
                        for (XWPFRun r : runs) {
                            replacePkdNumber(r);
                        }
                    }
                }

                int rowsToDelete = 15 - PkdMgr._tablePkd.getRowCount();
                if (rowsToDelete < 0) {
                    rowsToDelete = 0;
                }
                if (rowsToDelete > 15) {
                    rowsToDelete = 15;
                }

                for (int i = 0; i < rowsToDelete; i++) {
                    doc.getTables().get(0).removeRow(doc.getTables().get(0).getRows().size() - 1);
                }

                //System.out.println("Ilość wierszy w tabeli: " +doc.getTables().get(0).getRows().size());
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Zapisz PKD");
                fileChooser.setSelectedFile(new File("C:\\"
                        + PkdMgr._nrPkd + " Protokół kontroli jakosci dostawy "
                        + PkdMgr._data + ", "
                        + PkdMgr._dostawca /*+ ", " */
                        + PkdMgr._kontrakt + ".docx"
                ));

                int userSelection = fileChooser.showSaveDialog(component);

                String filePath = "./pkd/";

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    filePath = fileToSave.getAbsolutePath() + "/";
                    System.out.println("Save as file: " + filePath);
                }

                File fileToSave = new File(filePath);
                //File fileToSave;

                String path = filePath;
//                        + PkdMgr._nrPkd + " Protokół kontroli jakosci dostawy "
//                        + PkdMgr._data + ", "
//                        + PkdMgr._dostawca /*+ ", " */
//                        + PkdMgr._kontrakt + ".docx";

                doc.write(new FileOutputStream(path));
                doc.close();
            }
        } finally {

        }
    }

    /**
     * Zastępuje numer PKD w dokumencie
     *
     * @param r Obszar tekstowy do przeszukania
     */
    private void replacePkdNumber(XWPFRun r) {
        String text = r.getText(0);
        if (text != null && text.contains("iiiiii")) {
            text = text.replace("iiiiii", "" + PkdMgr._nrPkd + "");
            r.setText(text, 0);
        }
    }

    /**
     * Zapisuje do dokumentu sprawdzającego
     *
     * @param r Obszar tekstowy do przszukania.
     */
    private void replaceSprawdzajacy(XWPFRun r) {
        String text = r.getText(0);
        if (text != null && text.contains("ssssss")) {
            text = text.replace("ssssss", "" + PkdMgr._sprawdzajacy + "");
            r.setText(text, 0);
        }
    }

    /**
     * Zapisuje do dokumentu kontrakt.
     *
     * @param r Obszar tekstowy do przeszukania.
     */
    private void replaceKontrakt(XWPFRun r) {
        String text = r.getText(0);
        if (text != null && text.contains("kkkkkk")) {
            text = text.replace("kkkkkk", "" + PkdMgr._kontrakt + "");
            r.setText(text, 0);
        }
    }

    /**
     * Zapisuje do dokumentu datę
     *
     * @param r Obszar tekstowy do przeszukania.
     */
    private void replaceData(XWPFRun r) {
        String text = r.getText(0);
        if (text != null && text.contains("dddddd")) {
            text = text.replace("dddddd", "" + PkdMgr._data + "");
            r.setText(text, 0);
        }
    }

    /**
     * Zapisuje do dokumentu numery ZP
     *
     * @param r Obszar tekstowy dokumentu do przeszukania.
     */
    private void replaceZp(XWPFRun r) {
        String text = r.getText(0);
        if (text != null && text.contains("pppppp")) {
            text = text.replace("pppppp", "" + getDataFromPkdTable(1) + "");
            r.setText(text, 0);
        }
    }

    /**
     * Zapisuje do dokumentu numer zamówienia
     *
     * @param r Obszar tekstowy dokumentu do przeszukania.
     */
    private void replaceZamowienie(XWPFRun r) {
        String text = r.getText(0);
        if (text != null && text.contains("mmmmmm")) {
            text = text.replace("mmmmmm", "" + getDataFromPkdTable(2) + "");
            r.setText(text, 0);
        }
    }

    /**
     * Zapisuje do dokumentu numer WZ
     *
     * @param r Obszar tekstowy dokumentu do przeszukania.
     */
    private void replaceWz(XWPFRun r) {
        String text = r.getText(0);
        if (text != null && text.contains("zzzzzz")) {
            text = text.replace("zzzzzz", "" + getDataFromPkdTable(4) + "");
            r.setText(text, 0);
        }
    }

    /**
     * Zapisuje do dokumentu dostawcę
     *
     * @param r Obszar tekstowy dokumentu do przeszukania.
     */
    private void replaceDostawca(XWPFRun r) {
        String text = r.getText(0);
        if (text != null && text.contains("wwwwww")) {
            PkdMgr._dostawca = getDataFromPkdTable(3);
            text = text.replace("wwwwww", "" + PkdMgr._dostawca + "");
            r.setText(text, 0);
        }
    }

    private void replaceDataInTable(XWPFRun r) {
        DefaultTableModel model = (DefaultTableModel) PkdMgr._tablePkd.getModel();

        String text = r.getText(0);

        for (int i = 0; i < 15; i++) {
            if (text != null) {
                if (text.contains("xx" + i + "xx") && model.getRowCount() > i) {
                    text = text.replace("xx" + i + "xx", model.getValueAt(i, 0).toString());
                    r.setText(text, 0);
                }

                if (text.contains("cc" + i + "cc") && model.getRowCount() > i) {
                    String textToReplace = " ";
                    if (model.getValueAt(i, 6) != null) {
                        textToReplace = model.getValueAt(i, 6).toString();
                    }
                    text = text.replace("cc" + i + "cc", textToReplace);
                    r.setText(text, 0);
                }
                
                if (text.contains("vv" + i + "vv") && model.getRowCount() > i) {
                    String textToReplace = " ";
                    if (model.getValueAt(i, 7) != null) {
                        textToReplace = model.getValueAt(i, 7).toString();
                    }
                    text = text.replace("vv" + i + "vv", textToReplace);
                    r.setText(text, 0);
                }

                if (text.contains("gg" + i + "gg") && model.getRowCount() > i) {
                    String textToReplace = " ";
                    if (model.getValueAt(i, 10) != null) {
                        textToReplace = model.getValueAt(i, 10).toString();
                    }
                    text = text.replace("gg" + i + "gg", textToReplace);
                    r.setText(text, 0);
                }

                if (text.contains("bb" + i + "bb") && model.getRowCount() > i) {
                    if (model.getValueAt(i, 8).equals(true)) {
                        text = text.replace("bb" + i + "bb", " X");
                        r.setText(text, 0);
                    } else {
                        text = text.replace("bb" + i + "bb", " ");
                        r.setText(text, 0);
                    }
                }

                if (text.contains("nn" + i + "nn") && model.getRowCount() > i) {
                    if (model.getValueAt(i, 9) != null) {
                        if (model.getValueAt(i, 9).equals(true)) {
                            text = text.replace("nn" + i + "nn", " X");
                            r.setText(text, 0);
                        }
                    } else {
                        text = text.replace("nn" + i + "nn", " ");
                        r.setText(text, 0);

                    }
                }
            }
        }

//        if (text != null) {
//            if (text.contains("_wyrob0_") && model.getRowCount() > 0){
//                text = text.replace("_wyrob0_", model.getValueAt(0, 0).toString());
//                r.setText(text, 0);
//            } 
//            
//            if (text.contains("_wyrob1_") && model.getRowCount() > 1){
//                text = text.replace("_wyrob1_", model.getValueAt(1, 0).toString());
//                r.setText(text, 0);
//            }
//            
//            if (text.contains("_wyrob2_") && model.getRowCount() > 2){
//                text = text.replace("_wyrob2_", model.getValueAt(2, 0).toString());
//                r.setText(text, 0);
//            }
//            
//            if (text.contains("_wyrob3_") && model.getRowCount() > 3){
//                text = text.replace("_wyrob3_", model.getValueAt(3, 0).toString());
//                r.setText(text, 0);
//            }
//            
//            if (text.contains("_wyrob4_") && model.getRowCount() > 4){
//                text = text.replace("_wyrob4_", model.getValueAt(4, 0).toString());
//                r.setText(text, 0);
//            }
//        }
    }

    /**
     * Wyciąga z tabeli odpowiednie wartości w zależności od trybu
     *
     * @param mode Tryby 1-ZP, 2-Zamówienie, 3-Dostawca, 4-WZ
     * @return Łańcuch znaków z odpowienimi danymi.
     */
    private String getDataFromPkdTable(int mode) {
        /* Kolumna w tabeli PKD, 4 - ZP, 2 - Zamówienie, 3 - WZ, 5 - Dostawca */
        int column = 4;

        switch (mode) {
            case 1:
                column = 4;
                break;
            case 2:
                column = 2;
                break;
            case 3:
                column = 5;
                break;
            case 4:
                column = 3;
                break;
        }

        String data = "";
        DefaultTableModel model = (DefaultTableModel) PkdMgr._tablePkd.getModel();

        /* Tablica z odczytanymi już numerami ZP */
        ArrayList<String> dubles = new ArrayList<>();

        for (int i = 0; i < PkdMgr._tablePkd.getRowCount(); i++) {
            String pretendent = model.getValueAt(i, column) + ", ";
            boolean dubled = false;
            for (String checkDuble : dubles) {
                /* Sprawdzenie czy aktualnie wyciągnięty numer ZP jest już
                 * w tablicy zdublowanych.*/
                if (pretendent.equals(checkDuble)) {
                    dubled = true;
                }
            }

            /* Jeżeli numer ZP nie występował */
            if (!dubled) {
                data = data + pretendent;
                dubles.add(pretendent);
            }
        }
        return data;
    }

    /**
     * Zapisuje podany numer PKD pod wybranymi atestami w bazie danych.
     *
     * @return -1 jeżeli błąd.
     */
    public int savePkdNumberInDB() {
        return -1;
    }

    private PkdMgr() {
        toPkdCerts = new ArrayList<Atest>();
    }

    public static PkdMgr getInstance() {
        return PkdMgrHolder.INSTANCE;

    }

    private static class PkdMgrHolder {

        private static final PkdMgr INSTANCE = new PkdMgr();
    }
}
