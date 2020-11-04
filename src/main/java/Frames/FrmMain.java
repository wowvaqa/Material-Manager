/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Frames;

import Configuration.MmConfigManager;
import ZpCreator.FrmZpCreator;
import ControlCard.FrmKKCreator;
import MyClasses.CertOperations;
import MyClasses.MyTreeRenderer;
import MyClasses.PkdNumber;
import MyClasses.TableAtestyHeaderMouseListener;
import Configuration.DatabaseConfigManager;
import ControlCard.KKCreatorManager;
import com.kprm.materialmanager.AtestManager;
import com.kprm.materialmanager.BomManager;
import com.kprm.materialmanager.DatabaseManager;
import com.kprm.materialmanager.NodeManager;
import com.kprm.materialmanager.PkdMgr;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author wakan
 */
public class FrmMain extends javax.swing.JFrame {

    public static final DefaultTableCellRenderer DEFAULT_RENDERER
            = new DefaultTableCellRenderer();

    public static final DefaultTreeCellRenderer DEFAULT_TREE_RENDERER
            = new DefaultTreeCellRenderer();

    /**
     * Creates new form FrmMain
     */
    public FrmMain() {
        initComponents();
        initPopups();
        createTableHeadersClickListeners();
        setDatabase();

        this.setExtendedState(MAXIMIZED_BOTH);

        treeMaterialy.setCellRenderer(new MyTreeRenderer());

        tblAtesty.setDefaultRenderer(Object.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = DEFAULT_RENDERER.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (AtestManager.getInstance().getAtesty() != null) {
                    if (AtestManager.getInstance().getAtesty().get(row).isRedStatus()) /*if (AtestManager.getInstance().getAtesty()[row].isRedStatus())*/ {
                        c.setBackground(Color.red);
                        c.setForeground(Color.white);
                    } else {
                        c.setBackground(Color.white);
                        c.setForeground(Color.black);
                    }

                    if (table.isRowSelected(row)) {
                        c.setBackground(Color.blue);
                        c.setForeground(Color.white);
                    }
                }

                //c.setBackground(Color.red);
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                return c;
            }
        });

        /* Tworzy folder przechowujący pliki konfiguracyjne programu */
        MmConfigManager.createConfigurationDirectory();
        /* Ładuje ustawienia programu z pliku konfiguracyjnego */
        MmConfigManager.setMmConfig(MmConfigManager.loadConfigFile());

        if (MmConfigManager.getMmConfig() != null) {
            tfBearingRegistryPath.setText(
                    MmConfigManager.getMmConfig().getBearingRegistryFilePath());
        }

        try {
            connectDatabase();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Błąd połączenia z bazą danych, " + ex);
        }

        DatabaseConfigManager.getInstance().readCertDirectory(tfCertPath);
    }

    /**
     * Tworzy menu popup
     */
    private void initPopups() {
        initMaterialyPopups();
        initAtestyPopups();
        initKontraktyPopups();
    }

    /**
     * Tworzy nasłuch w którym program wykrywa kliknięcie w nagłówku tabeli
     * atestów.
     */
    private void createTableHeadersClickListeners() {
        JTableHeader headTblAtesty = tblAtesty.getTableHeader();
        headTblAtesty.addMouseListener(new TableAtestyHeaderMouseListener(tblAtesty));
    }

    /**
     * Tworzy Popupry dla atestów.
     */
    private void initAtestyPopups() {

        JMenuItem itmTblPathAtesty01 = new JMenuItem("Usuń");
        JMenuItem itmTblPathAtesty02 = new JMenuItem("Zmień Opis");
        JMenuItem itmTblPathAtesty03 = new JMenuItem("Pokaż ścieżkę");
        JMenuItem itmTblPathAtesty04 = new JMenuItem("Otwórz folder z atestm");

        // Usuwa ścieżke do pliku z atestem z tabeli oraz bazy danych.
        itmTblPathAtesty01.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Czy na pewno chcesz usunąć ścieżkę ?", "Ostrzeżenie!",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    AtestManager.getInstance().removeCertFile(tblPathAtesty);
                    AtestManager.getInstance().readCertsFiles(
                            tblAtesty, tblPathAtesty,
                            AtestManager.getInstance().getAtesty(),
                            tfCertPath.getText());
                }
            }
        });

        // Zmiana opisu pliku atestu.
        itmTblPathAtesty02.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (AtestManager.getInstance().editCertFileDiscription(tblPathAtesty) == 1) {
                    AtestManager.getInstance().readCertsFiles(tblAtesty, tblPathAtesty,
                            AtestManager.getInstance().getAtesty(), tfCertPath.getText());
                }
            }
        });

        // Otwiera okno z ścieżką do pliku.
        itmTblPathAtesty03.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AtestManager.getInstance().showCertPath(tblPathAtesty, tfCertPath.getText());
            }
        });

        // Otwiera folder z plikiem atestu.
        itmTblPathAtesty04.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AtestManager.getInstance().openFolder(tblPathAtesty, tfCertPath.getText());

            }
        });

        popTblPathAtesty.add(itmTblPathAtesty02);
        popTblPathAtesty.add(itmTblPathAtesty01);
        popTblPathAtesty.add(itmTblPathAtesty03);
        popTblPathAtesty.add(itmTblPathAtesty04);

        JMenuItem itmCertDelete = new JMenuItem("Usuń atest");
        JMenuItem itmCertRename = new JMenuItem("Zmień nazwę atestu");
        JMenuItem itmCertOrderNumberChange = new JMenuItem("Zmień numer zamówienia");
        JMenuItem itmCertWzChange = new JMenuItem("Zmień numer WZ");
        JMenuItem itmCertZpChange = new JMenuItem("Zmień numer ZP");
        JMenuItem itmCertPkdChange = new JMenuItem("Zmień numer PKD");
        JMenuItem itmCertSupplierChange = new JMenuItem("Zmień nazwę dostawcy");
        JMenuItem itmCertCopy = new JMenuItem("Kopiuj atest");
        JMenuItem itmCertCut = new JMenuItem("Wytnij atest");
        JMenuItem itmCertDeliveryDateChange = new JMenuItem("Zmień datę dostawy");

        // Usuwa atest z tabeli atestów.
        itmCertDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Czy na pewno chcesz usunąć atest ?", "Ostrzeżenie!",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                    //((DefaultTableModel) tblPathMaterialy.getModel()).setRowCount(0);
                    AtestManager.getInstance().removeCert(tblAtesty);
                    AtestManager.getInstance().readCerts(tblAtesty, treeMaterialy, 0, null);
                }
            }
        });

        // Zmienia nazwę atestu.
        itmCertRename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AtestManager.getInstance().editCertName(tblAtesty);
            }
        });

        // Zmienia numer zamówienia atestu.
        itmCertOrderNumberChange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AtestManager.getInstance().editCertOrder(tblAtesty);
            }
        });

        // Zmienia numer zamówienia WZ atestu.
        itmCertWzChange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AtestManager.getInstance().editCertWZ(tblAtesty);
            }
        });

        // Zmienia numer Zp atestu.
        itmCertZpChange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AtestManager.getInstance().editCertZp(tblAtesty);
            }
        });

        // Zmienia numer PKD atestu.
        itmCertPkdChange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AtestManager.getInstance().editCertPKD(tblAtesty);
            }
        });

        // Zmienia nazwę dostawcy atestu.
        itmCertSupplierChange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AtestManager.getInstance().editCertSupplier(tblAtesty);
            }
        });

        // Rozpoczyna proces kopiowania atestu
        itmCertCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AtestManager.getInstance().cutCopyCert(tblAtesty, CertOperations.COPY);
            }
        });

        // Rozpoczyna proces wycinania atestu
        itmCertCut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AtestManager.getInstance().cutCopyCert(tblAtesty, CertOperations.CUT);
            }
        });

        // Zmienia datę dostawy.
        itmCertDeliveryDateChange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AtestManager.getInstance().editCertDeliveryDate(tblAtesty);
            }
        });

        popTblAtesty.add(itmCertDelete);
        popTblAtesty.add(itmCertRename);
        popTblAtesty.add(itmCertOrderNumberChange);
        popTblAtesty.add(itmCertWzChange);
        popTblAtesty.add(itmCertZpChange);
        popTblAtesty.add(itmCertPkdChange);
        popTblAtesty.add(itmCertSupplierChange);
        popTblAtesty.add(itmCertDeliveryDateChange);
        popTblAtesty.add(itmCertCopy);
        popTblAtesty.add(itmCertCut);

        JMenuItem itmTreeMaterialy01 = new JMenuItem("Edytuj nazwę");
        JMenuItem itmTreeMaterialy02 = new JMenuItem("Usuń materiał");
        JMenuItem itmTreeMaterialy03 = new JMenuItem("Wklej atest");

        // Edycja nazwy materiału w drzewku materiałów.
        itmTreeMaterialy01.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AtestManager.getInstance().renameMaterialName(treeMaterialy);
            }
        });

        // Usunięcie materiału z drzewka materiałów.
        itmTreeMaterialy02.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Czy na pewno chcesz usunąć materiał ?", "Ostrzeżenie!",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    AtestManager.getInstance().removeMaterialNode(treeMaterialy);
                }
            }
        });

        // Usunięcie materiału z drzewka materiałów.
        itmTreeMaterialy03.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (AtestManager.getInstance().getClipboard() != null) {
                    if (JOptionPane.showConfirmDialog(null, "Czy na pewno chcesz wkleić atest "
                            + AtestManager.getInstance().getClipboard().getNazwa() + " ?", "Ostrzeżenie!",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        AtestManager.getInstance().pasteCert(
                                treeMaterialy, AtestManager.getInstance().getCertOperations());

                        AtestManager.getInstance().readCerts(tblAtesty, treeMaterialy, 0, null);
                        ((DefaultTableModel) tblPathAtesty.getModel()).setRowCount(0);
                        AtestManager.getInstance().setClipboard(null);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Schowek pusty");
                }
            }
        });

        popTreeMaterialy.add(itmTreeMaterialy01);
        popTreeMaterialy.add(itmTreeMaterialy02);
        popTreeMaterialy.add(itmTreeMaterialy03);

        JMenuItem itmTblBomImport01 = new JMenuItem("Wyczyść tabelę");
        itmTblBomImport01.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) tblBOMImport.getModel();
                model.setColumnCount(0);
                model.setRowCount(0);
            }
        });

        popTableBomImport.add(itmTblBomImport01);
    }

    /**
     * Tworzy Popupy dla zakładki materiały.
     */
    private void initMaterialyPopups() {

        /*
            Popupy dla tabeli z bomem.
         */
        JMenuItem itmTblBom01 = new JMenuItem("Usuń materiał");
        JMenuItem itmTblBom02 = new JMenuItem("Dodaj materiał");
        JMenuItem itmTblBom05 = new JMenuItem("Dodaj materiały pomocnicze");
        JMenuItem itmTblBom03 = new JMenuItem("Zmień nazwę materiału");
        JMenuItem itmTblBom04 = new JMenuItem("Usuń wszystkie materiały");

        // Usunięcie materiału z BOMA
        itmTblBom01.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Czy na pewno chcesz usunąć materiał?", "Ostrzeżenie!",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    BomManager.getInstance().removeMaterialFromBomTable(tblBom);

                    ((DefaultTableModel) tblAtest.getModel()).setRowCount(0);
                    ((DefaultTableModel) tblBom.getModel()).setRowCount(0);
                    ((DefaultTableModel) tblPathMaterialy.getModel()).setRowCount(0);

                    BomManager.getInstance().importBomFromDB(treeKontraktyMaterialy, tblBom);
                }
            }
        });

        // Dodaje nowy materiał do Boma
        itmTblBom02.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BomManager.getInstance().addMaterialIntoBomTable(tblBom, treeKontraktyMaterialy);
            }
        });

        // Dodaje materiały pomocnicze do Boma - ścierniwo, drut spawalniczy, drut do metalizacji, Farby.
        itmTblBom05.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BomManager.getInstance().addAuixilaryMaterialsIntoBomTable(tblBom, treeKontraktyMaterialy);
            }
        });

        // Zmiana nazwy materiału w BOMie
        itmTblBom03.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BomManager.getInstance().renameMaterialInBomTable(tblBom);
            }
        });

        // Usunięcie wszystkich materiałów z BOMA
        itmTblBom04.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Czy na pewno chcesz usunąć wszystkie materiały?", "Ostrzeżenie!",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    BomManager.getInstance().removeAllMaterialsFromBomTable(treeKontraktyMaterialy, tblBom);
                }
            }
        });

        popBomTable.add(itmTblBom01);
        popBomTable.add(itmTblBom04);
        popBomTable.add(itmTblBom02);
        popBomTable.add(itmTblBom05);
        popBomTable.add(itmTblBom03);
        tblBom.add(popBomTable);

        /*
            Popupy dla drzewka kontraktów
         */
        JMenuItem itmTreeKontraktyMaterialy01 = new JMenuItem("Zmień nazwę węzła");
        JMenuItem itmTreeKontraktyMaterialy02 = new JMenuItem("Usuń węzeł");
        JMenuItem itmTreeKontraktyMaterialy03 = new JMenuItem("Kopiuj BOM");
        JMenuItem itmTreeKontraktyMaterialy04 = new JMenuItem("Wklej BOM");

        itmTreeKontraktyMaterialy01.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NodeManager.getInstance().renameNode(treeKontraktyMaterialy);
            }
        });

        itmTreeKontraktyMaterialy02.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Czy na pewno chcesz usunąć węzeł?", "Ostrzeżenie!",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    NodeManager.getInstance().removeNode(treeKontraktyMaterialy);
                }
            }
        });

        itmTreeKontraktyMaterialy03.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BomManager.getInstance().copyBom(treeKontraktyMaterialy);
            }
        });

        itmTreeKontraktyMaterialy04.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BomManager.getInstance().psateBom(treeKontraktyMaterialy);
                BomManager.getInstance().importBomFromDB(treeKontraktyMaterialy, tblBom);
            }
        });
        popTreeKontraktyMaterialy.add(itmTreeKontraktyMaterialy01);
        popTreeKontraktyMaterialy.add(itmTreeKontraktyMaterialy02);
        popTreeKontraktyMaterialy.add(itmTreeKontraktyMaterialy03);
        popTreeKontraktyMaterialy.add(itmTreeKontraktyMaterialy04);
        treeKontraktyMaterialy.add(popTreeKontraktyMaterialy);

        /*
            Popupy dla tablie atestów boma.
         */
        JMenuItem itmTblAtest01 = new JMenuItem("Usuń");

        itmTblAtest01.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Czy na pewno chcesz usunąć atest?", "Ostrzeżenie!",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    BomManager.getInstance().removeAtestFromAtestTable(tblAtest);
                    BomManager.getInstance().refreshAtestTable(tblAtest,
                            BomManager.getInstance().getBomMaterials()[BomManager.getInstance().getSelectedBomMaterial()].getId());
                    BomManager.getInstance().importBomFromDB(treeKontraktyMaterialy, tblBom);
                }
            }
        });
        popTblAtest.add(itmTblAtest01);
        tblAtest.add(popTblAtest);
    }

    /**
     * Inicjalizacja popupów dla głównego drzewka kontraktów.
     */
    private void initKontraktyPopups() {
        /*
            Popupy dla drzewka kontraktów
         */
        JMenuItem itmTreeKontrakty01 = new JMenuItem("Zmień nazwę węzła");
        JMenuItem itmTreeKontrakty02 = new JMenuItem("Usuń węzeł");

        itmTreeKontrakty01.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NodeManager.getInstance().renameNode(treeKontrakty);
            }
        });

        itmTreeKontrakty02.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Czy na pewno chcesz usunąć węzeł?", "Ostrzeżenie!",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    NodeManager.getInstance().removeNode(treeKontrakty);
                }
            }
        });
        popTreeKontrakty.add(itmTreeKontrakty01);
        popTreeKontrakty.add(itmTreeKontrakty02);
        treeKontrakty.add(popTreeKontrakty);
    }

    private void connectDatabase() throws ClassNotFoundException, SQLException {
        DatabaseManager.getInstance().setDbAdress(tfDBadress.getText());
        DatabaseManager.getInstance().setDbName(tfDb.getText());
        DatabaseManager.getInstance().setLogin(tfLogin.getText());
        DatabaseManager.getInstance().setPass(tfPassword.getText());
        DatabaseManager.getInstance().connectDatabase(tfLogin.getText(), tfPassword.getText(), tfDBadress.getText() + tfDb.getText());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popTreeKontraktyMaterialy = new javax.swing.JPopupMenu();
        popTblAtest = new javax.swing.JPopupMenu();
        popBomTable = new javax.swing.JPopupMenu();
        popTreeMaterialy = new javax.swing.JPopupMenu();
        popTblAtesty = new javax.swing.JPopupMenu();
        popTblPathAtesty = new javax.swing.JPopupMenu();
        popTreeKontrakty = new javax.swing.JPopupMenu();
        popTableBomImport = new javax.swing.JPopupMenu();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelKontrakty = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        treeKontrakty = new javax.swing.JTree();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        tfNazwaWezla = new javax.swing.JTextField();
        jPanelAtesty = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        treeMaterialy = new javax.swing.JTree();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        tfNazwaMaterialu = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        tfFilter = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        tfNazwaAtestu = new javax.swing.JTextField();
        tfNrZamowienia = new javax.swing.JTextField();
        tfNrWZ = new javax.swing.JTextField();
        tfZp = new javax.swing.JTextField();
        tfDostawca = new javax.swing.JTextField();
        tfPkd = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        tfDataDostawy = new javax.swing.JFormattedTextField();
        cbClearFields = new javax.swing.JCheckBox();
        jPanel11 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        lbSciezka = new javax.swing.JLabel();
        jSplitPane5 = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAtesty = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        jScrollPane11 = new javax.swing.JScrollPane();
        tblPathAtesty = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        jPanel16 = new javax.swing.JPanel();
        cbAtestySzukaj = new javax.swing.JComboBox<>();
        tfAtestySzukaj = new javax.swing.JTextField();
        jButton11 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jPanelMaterialy = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        treeKontraktyMaterialy = new javax.swing.JTree();
        jPanel8 = new javax.swing.JPanel();
        jButton9 = new javax.swing.JButton();
        btnZapiszZmiany = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jSplitPane3 = new javax.swing.JSplitPane();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblBom = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        jSplitPane4 = new javax.swing.JSplitPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblAtest = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        jScrollPane10 = new javax.swing.JScrollPane();
        tblPathMaterialy = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        jPanel9 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        tfNazwaBoma = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblBOMImport = new javax.swing.JTable();
        jButton8 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jPanelPkd = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane9 = new javax.swing.JScrollPane();
        tblPdkResult = new javax.swing.JTable();
        jScrollPane12 = new javax.swing.JScrollPane();
        tblPkd = new javax.swing.JTable();
        jPanel18 = new javax.swing.JPanel();
        cbPkdMode = new javax.swing.JComboBox<>();
        tfPkdFilter = new javax.swing.JTextField();
        jButton12 = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        tfSprawdzajacy = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        tfPkdData = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        tfPkdKontrakt = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jScrollPane13 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel22 = new javax.swing.JLabel();
        jButton22 = new javax.swing.JButton();
        tfPkdNr = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        tfPkdMonthNr = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        tfPkdYearNr = new javax.swing.JTextField();
        jPanelUstawienia = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        tfLogin = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        tfDBadress = new javax.swing.JTextField();
        cbDb = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        tfDb = new javax.swing.JTextField();
        tfPassword = new javax.swing.JPasswordField();
        jPanel15 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        tfCertPath = new javax.swing.JTextField();
        jButton15 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jPanel20 = new javax.swing.JPanel();
        tfBearingRegistryPath = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        jButton10 = new javax.swing.JButton();
        jPanel22 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel23 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        treeMaterialy1 = new javax.swing.JTree();
        tfContractNumber6 = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel36 = new javax.swing.JLabel();
        tfContractNumber8 = new javax.swing.JTextField();
        btnGenerateCard2 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel24 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        tfContractNumber = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        tfContractNumber1 = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        tfContractNumber2 = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        tfContractNumber3 = new javax.swing.JTextField();
        tfContractNumber4 = new javax.swing.JTextField();
        tfContractNumber5 = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        tfDataDostawy1 = new javax.swing.JFormattedTextField();
        btnGenerateCard = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel35 = new javax.swing.JLabel();
        tfContractNumber7 = new javax.swing.JTextField();
        btnGenerateCard1 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel19 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Material Manager v. 0.0.6.5");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/product.png")).getImage());

        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTabbedPane1.setName(""); // NOI18N

        jPanelKontrakty.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanelKontraktyComponentShown(evt);
            }
        });

        jLabel1.setText("Kontrakty:");

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Kontrakty");
        treeKontrakty.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeKontrakty.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treeKontraktyMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(treeKontrakty);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Edycja węzłów"));

        jButton1.setText("Dodaj Węzeł");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Nazwa:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(tfNazwaWezla, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 655, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tfNazwaWezla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 143, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelKontraktyLayout = new javax.swing.GroupLayout(jPanelKontrakty);
        jPanelKontrakty.setLayout(jPanelKontraktyLayout);
        jPanelKontraktyLayout.setHorizontalGroup(
            jPanelKontraktyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelKontraktyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelKontraktyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelKontraktyLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelKontraktyLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelKontraktyLayout.setVerticalGroup(
            jPanelKontraktyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelKontraktyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelKontraktyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                    .addGroup(jPanelKontraktyLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 284, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("KONTRAKTY", jPanelKontrakty);

        jPanelAtesty.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanelAtestyComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Materiały"));

        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Materiały");
        treeMaterialy.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeMaterialy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treeMaterialyMousePressed(evt);
            }
        });
        jScrollPane3.setViewportView(treeMaterialy);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Nowy materiał"));

        jLabel5.setText("Nazwa materiału:");

        jButton4.setText("Dodaj materiał");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tfNazwaMaterialu, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton4)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(tfNazwaMaterialu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtruj"));

        tfFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfFilterKeyReleased(evt);
            }
        });

        jLabel16.setText("Nazwa:");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfFilter)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Atesty"));

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Nowy atest"));

        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        jPanel10.setForeground(new java.awt.Color(102, 102, 102));

        jLabel6.setText("Nazwa atestu:");

        jLabel7.setText("Numer zamówienia:");

        jLabel8.setText("Numer WZ:");

        jLabel13.setText("Numer ZP:");

        jLabel14.setText("Dostawca:");

        jLabel15.setText("PKD:");

        jButton6.setText("Dodaj nowy atest");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel23.setText("Data dostawy:");

        tfDataDostawy.setColumns(10);
        try {
            tfDataDostawy.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####-##-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        tfDataDostawy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfDataDostawyActionPerformed(evt);
            }
        });

        cbClearFields.setSelected(true);
        cbClearFields.setText("Wyczyść pola");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbClearFields))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(tfDostawca, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfNrWZ, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfNazwaAtestu, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel13)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfPkd, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfZp, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(tfNrZamowienia, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfDataDostawy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(tfNazwaAtestu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(tfNrWZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(8, 8, 8))
                            .addComponent(tfDostawca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(tfNrZamowienia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23)
                            .addComponent(tfDataDostawy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(tfZp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(tfPkd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6)
                    .addComponent(cbClearFields))
                .addContainerGap())
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        jLabel9.setText("Lokalizacja pliku z atestem:");

        jButton5.setText("Dodaj plik z atestem");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        lbSciezka.setText("...");
        lbSciezka.setMaximumSize(new java.awt.Dimension(1, 1));
        lbSciezka.setMinimumSize(new java.awt.Dimension(1, 1));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbSciezka, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jButton5)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addComponent(lbSciezka, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSplitPane5.setDividerLocation(700);
        jSplitPane5.setDividerSize(8);

        tblAtesty.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nazwa", "Nr zamówienia", "Nr WZ", "Data dostawy", "Nr ZP", "Dostawca", "PKD"
            }
        ));
        tblAtesty.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblAtesty.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblAtestyMousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblAtesty);

        jSplitPane5.setLeftComponent(jScrollPane2);

        tblPathAtesty.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Opis"
            }
        ));
        tblPathAtesty.setMaximumSize(new java.awt.Dimension(500, 0));
        tblPathAtesty.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblPathAtesty.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblPathAtestyMousePressed(evt);
            }
        });
        jScrollPane11.setViewportView(tblPathAtesty);

        jSplitPane5.setRightComponent(jScrollPane11);

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("Szukaj"));

        cbAtestySzukaj.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nazwa", "Zamówienie", "ZP", "Dostawca" }));

        tfAtestySzukaj.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfAtestySzukajKeyReleased(evt);
            }
        });

        jButton11.setText("Szukaj");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton14.setText("Otwórz plik atestu");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tfAtestySzukaj, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbAtestySzukaj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton14)
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbAtestySzukaj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton11)
                        .addComponent(jButton14))
                    .addComponent(tfAtestySzukaj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSplitPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jSplitPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelAtestyLayout = new javax.swing.GroupLayout(jPanelAtesty);
        jPanelAtesty.setLayout(jPanelAtestyLayout);
        jPanelAtestyLayout.setHorizontalGroup(
            jPanelAtestyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAtestyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelAtestyLayout.setVerticalGroup(
            jPanelAtestyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelAtestyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelAtestyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("ATESTY", jPanelAtesty);

        jPanelMaterialy.setPreferredSize(new java.awt.Dimension(1200, 785));
        jPanelMaterialy.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanelMaterialyComponentShown(evt);
            }
        });

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Kontrakty"));

        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Kontrakty");
        treeKontraktyMaterialy.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeKontraktyMaterialy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treeKontraktyMaterialyMousePressed(evt);
            }
        });
        jScrollPane4.setViewportView(treeKontraktyMaterialy);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("BOM"));

        jButton9.setText("Przyporządkuj atest");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        btnZapiszZmiany.setText("Zapisz BOM");
        btnZapiszZmiany.setActionCommand("Dodaj do BOM");
        btnZapiszZmiany.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZapiszZmianyActionPerformed(evt);
            }
        });

        jButton16.setText("Otwórz atest");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jSplitPane3.setDividerLocation(400);
        jSplitPane3.setDividerSize(8);

        tblBom.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Materiał:", "Ilość atestów:"
            }
        ));
        tblBom.setMaximumSize(new java.awt.Dimension(300, 0));
        tblBom.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblBom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblBomMousePressed(evt);
            }
        });
        jScrollPane7.setViewportView(tblBom);

        jSplitPane3.setLeftComponent(jScrollPane7);

        jSplitPane4.setDividerLocation(200);
        jSplitPane4.setDividerSize(8);

        tblAtest.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Atest:"
            }
        ));
        tblAtest.setMaximumSize(new java.awt.Dimension(300, 0));
        tblAtest.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblAtest.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblAtestMousePressed(evt);
            }
        });
        jScrollPane5.setViewportView(tblAtest);

        jSplitPane4.setLeftComponent(jScrollPane5);

        tblPathMaterialy.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Opis"
            }
        ));
        tblPathMaterialy.setMaximumSize(new java.awt.Dimension(500, 0));
        tblPathMaterialy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblPathMaterialyMousePressed(evt);
            }
        });
        jScrollPane10.setViewportView(tblPathMaterialy);

        jSplitPane4.setRightComponent(jScrollPane10);

        jSplitPane3.setRightComponent(jSplitPane4);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(btnZapiszZmiany)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton16)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9)
                    .addComponent(btnZapiszZmiany)
                    .addComponent(jButton16))
                .addContainerGap())
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Import"));

        jLabel10.setText("Nazwa węzła:");

        tblBOMImport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblBOMImport.setCellSelectionEnabled(true);
        tblBOMImport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblBOMImportMousePressed(evt);
            }
        });
        jScrollPane6.setViewportView(tblBOMImport);

        jButton8.setText("Dodaj Węzeł");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton7.setText("Importuj BOM z pliku Excela");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton13.setText("Dodaj do BOMA");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel9Layout.createSequentialGroup()
                            .addComponent(jLabel10)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(tfNazwaBoma, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jButton13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jButton8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 754, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(tfNazwaBoma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7)
                .addContainerGap())
            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelMaterialyLayout = new javax.swing.GroupLayout(jPanelMaterialy);
        jPanelMaterialy.setLayout(jPanelMaterialyLayout);
        jPanelMaterialyLayout.setHorizontalGroup(
            jPanelMaterialyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMaterialyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMaterialyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelMaterialyLayout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelMaterialyLayout.setVerticalGroup(
            jPanelMaterialyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMaterialyLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanelMaterialyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("MATERIAŁY", jPanelMaterialy);

        jPanelPkd.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanelPkdComponentShown(evt);
            }
        });

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder("Utwórz PKD"));

        jSplitPane1.setDividerLocation(600);

        tblPdkResult.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Materiał", "Nazwa", "Zamówienie", "WZ", "ZP", "Dostawca"
            }
        ));
        tblPdkResult.setDragEnabled(true);
        jScrollPane9.setViewportView(tblPdkResult);

        jSplitPane1.setLeftComponent(jScrollPane9);

        tblPkd.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Materiał", "Nazwa", "Zamówienie", "WZ", "ZP", "Dostawca", "Ilość", "Wymiar", "OK", "NOK", "Uwagi"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane12.setViewportView(tblPkd);

        jSplitPane1.setRightComponent(jScrollPane12);

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtruj"));

        cbPkdMode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nazwa", "Zamówienie", "ZP", "Dostawca" }));

        tfPkdFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfPkdFilterKeyReleased(evt);
            }
        });

        jButton12.setText("Szukaj");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbPkdMode, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfPkdFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton12)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbPkdMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfPkdFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton12))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Operacje"));

        jButton18.setText("dodaj do PKD");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton19.setText("wyczyść");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton20.setText("dodaj wiersz");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton21.setText("usuń wiersz");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton19)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton18)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton19)
                    .addComponent(jButton20)
                    .addComponent(jButton21))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("PKD"));

        jLabel17.setText("Numer PKD:");

        jLabel18.setText("Sprawdzający:");

        jLabel19.setText("Kontrakt:");

        jLabel20.setText("Data:");

        jLabel21.setText("Dokument:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Dopuszczam", "Nie dopuszczam", "Dopuszczam pod warunkiem" }));

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jTextArea1.setRows(5);
        jScrollPane13.setViewportView(jTextArea1);

        jLabel22.setText("Warunek:");

        jButton22.setText("Generuj i Zapisz PKD");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        tfPkdNr.setText("99");
        tfPkdNr.setMinimumSize(new java.awt.Dimension(30, 24));
        tfPkdNr.setPreferredSize(new java.awt.Dimension(30, 24));

        jLabel25.setText(".");

        tfPkdMonthNr.setText("99");
        tfPkdMonthNr.setMinimumSize(new java.awt.Dimension(30, 24));
        tfPkdMonthNr.setPreferredSize(new java.awt.Dimension(30, 24));

        jLabel26.setText(".");

        tfPkdYearNr.setText("9999");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane13)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addComponent(jLabel21)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addComponent(jLabel17)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfPkdNr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel25)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfPkdMonthNr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel26)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfPkdYearNr, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(17, 17, 17)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfSprawdzajacy, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfPkdKontrakt, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfPkdData, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel22))
                        .addGap(0, 32, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton22)))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(tfSprawdzajacy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(tfPkdData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(tfPkdKontrakt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfPkdNr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(tfPkdMonthNr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(tfPkdYearNr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton22)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1036, Short.MAX_VALUE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelPkdLayout = new javax.swing.GroupLayout(jPanelPkd);
        jPanelPkd.setLayout(jPanelPkdLayout);
        jPanelPkdLayout.setHorizontalGroup(
            jPanelPkdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPkdLayout.createSequentialGroup()
                .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelPkdLayout.setVerticalGroup(
            jPanelPkdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPkdLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("PKD", jPanelPkd);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Baza Danych"));

        tfLogin.setText("root");
        tfLogin.setToolTipText("");

        jLabel3.setText("Nazwa użytkownika:");

        jLabel4.setText("Hasło:");

        jButton2.setText("Połącz");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel12.setText("Adres bazy danych");

        tfDBadress.setText("//localhost/");
        tfDBadress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfDBadressActionPerformed(evt);
            }
        });

        cbDb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "KPRM", "KPRM_test", "local", "Italy" }));
        cbDb.setSelectedIndex(2);
        cbDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDbActionPerformed(evt);
            }
        });

        jLabel24.setText("Nazwa bazy danych");

        tfDb.setText("materials");

        tfPassword.setText("tere7-67CS2");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(tfDb, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(tfLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfPassword))
                            .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfDBadress, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbDb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)))
                .addContainerGap(695, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfDBadress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfDb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Atesty"));

        jLabel11.setText("Lokalizacja katalogu z atestami:");

        tfCertPath.setText("D:\\atesty");

        jButton15.setLabel("Zapisz");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton17.setLabel("Wskaż folder");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(tfCertPath, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton17)
                        .addGap(5, 5, 5)
                        .addComponent(jButton15)))
                .addContainerGap(434, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfCertPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton15)
                    .addComponent(jButton17))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder("Lokalizacja pliku rejestru łożysk"));

        tfBearingRegistryPath.setText("C:\\rl.xlsx");

        jButton3.setText("Wskaż plik rejestru łożysk");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tfBearingRegistryPath, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfBearingRegistryPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton10.setText("jButton10");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton10)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton10)
                .addContainerGap(66, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelUstawieniaLayout = new javax.swing.GroupLayout(jPanelUstawienia);
        jPanelUstawienia.setLayout(jPanelUstawieniaLayout);
        jPanelUstawieniaLayout.setHorizontalGroup(
            jPanelUstawieniaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelUstawieniaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelUstawieniaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelUstawieniaLayout.setVerticalGroup(
            jPanelUstawieniaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelUstawieniaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(61, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("USTAWIENIA", jPanelUstawienia);

        jSplitPane2.setDividerLocation(300);

        treeMaterialy1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Materiały");
        treeMaterialy1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeMaterialy1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treeMaterialy1MousePressed(evt);
            }
        });
        jScrollPane8.setViewportView(treeMaterialy1);

        tfContractNumber6.setBackground(new java.awt.Color(224, 255, 255));
        tfContractNumber6.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N
        tfContractNumber6.setName("tfKontrakt"); // NOI18N

        jLabel34.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel34.setText("Filtr:");

        jLabel36.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel36.setText("Nowy materiał");

        tfContractNumber8.setBackground(new java.awt.Color(224, 255, 255));
        tfContractNumber8.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N
        tfContractNumber8.setName("tfKontrakt"); // NOI18N

        btnGenerateCard2.setBackground(new java.awt.Color(0, 153, 0));
        btnGenerateCard2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnGenerateCard2.setText("+");
        btnGenerateCard2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateCard2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane8)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfContractNumber6))
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jLabel36)
                        .addGap(0, 182, Short.MAX_VALUE))
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(tfContractNumber8)
                        .addGap(18, 18, 18)
                        .addComponent(btnGenerateCard2)))
                .addContainerGap())
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(tfContractNumber6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfContractNumber8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGenerateCard2))
                .addContainerGap())
        );

        jSplitPane2.setLeftComponent(jPanel23);

        jLabel27.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel27.setText("Nazwa atestu:");

        tfContractNumber.setBackground(new java.awt.Color(224, 255, 255));
        tfContractNumber.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N
        tfContractNumber.setName("tfKontrakt"); // NOI18N

        jLabel28.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel28.setText("Numer zamówienia:");

        tfContractNumber1.setBackground(new java.awt.Color(224, 255, 255));
        tfContractNumber1.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N
        tfContractNumber1.setName("tfKontrakt"); // NOI18N

        jLabel29.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel29.setText("Numer WZ:");

        tfContractNumber2.setBackground(new java.awt.Color(224, 255, 255));
        tfContractNumber2.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N
        tfContractNumber2.setName("tfKontrakt"); // NOI18N

        jLabel30.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel30.setText("Numer ZP:");

        jLabel31.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel31.setText("Dostawca:");

        jLabel32.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel32.setText("PKD");

        tfContractNumber3.setBackground(new java.awt.Color(224, 255, 255));
        tfContractNumber3.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N
        tfContractNumber3.setName("tfKontrakt"); // NOI18N

        tfContractNumber4.setBackground(new java.awt.Color(224, 255, 255));
        tfContractNumber4.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N
        tfContractNumber4.setName("tfKontrakt"); // NOI18N

        tfContractNumber5.setBackground(new java.awt.Color(224, 255, 255));
        tfContractNumber5.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N
        tfContractNumber5.setName("tfKontrakt"); // NOI18N

        jLabel33.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel33.setText("Data dostawy:");

        tfDataDostawy1.setBackground(new java.awt.Color(224, 255, 255));
        tfDataDostawy1.setColumns(10);
        try {
            tfDataDostawy1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####-##-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        tfDataDostawy1.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N

        btnGenerateCard.setBackground(new java.awt.Color(0, 153, 0));
        btnGenerateCard.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnGenerateCard.setText("ZAPISZ");
        btnGenerateCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateCardActionPerformed(evt);
            }
        });

        jCheckBox1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jCheckBox1.setSelected(true);
        jCheckBox1.setText("Wyczyść pola");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel35.setText("Plik atestu:");

        tfContractNumber7.setBackground(new java.awt.Color(224, 255, 255));
        tfContractNumber7.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N
        tfContractNumber7.setName("tfKontrakt"); // NOI18N

        btnGenerateCard1.setBackground(new java.awt.Color(0, 153, 0));
        btnGenerateCard1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnGenerateCard1.setText("+");
        btnGenerateCard1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateCard1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 444, Short.MAX_VALUE)
                        .addComponent(btnGenerateCard, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28)
                            .addComponent(jLabel27)
                            .addComponent(jLabel29)
                            .addComponent(jLabel30)
                            .addComponent(jLabel31)
                            .addComponent(jLabel32)
                            .addComponent(jLabel33)
                            .addComponent(jLabel35))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel24Layout.createSequentialGroup()
                                .addComponent(tfContractNumber7)
                                .addGap(18, 18, 18)
                                .addComponent(btnGenerateCard1))
                            .addComponent(tfContractNumber2)
                            .addComponent(tfContractNumber)
                            .addComponent(tfContractNumber1)
                            .addComponent(tfContractNumber3)
                            .addComponent(tfContractNumber4)
                            .addComponent(tfContractNumber5)
                            .addComponent(tfDataDostawy1))))
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfContractNumber1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfContractNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(tfContractNumber2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(tfContractNumber3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(tfContractNumber4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(tfContractNumber5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(tfDataDostawy1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfContractNumber7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGenerateCard1)
                    .addComponent(jLabel35))
                .addGap(30, 30, 30)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 158, Short.MAX_VALUE)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGenerateCard)
                    .addComponent(jCheckBox1))
                .addContainerGap())
        );

        jSplitPane2.setRightComponent(jPanel24);

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane2)
                .addContainerGap())
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane2)
                .addContainerGap())
        );

        jTabbedPane1.addTab("NOWY ATEST", jPanel22);

        jPanel19.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblStatus.setText("Status");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblStatus))
        );

        jMenu1.setText("Plik");

        jMenuItem1.setText(" Zakończ");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu3.setText("Narzędzia");

        jMenuItem4.setText("Raporty");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem4);

        jMenuItem5.setText("Atest do wielu materiałów");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem5);

        jMenuItem3.setText("Kreator ZP");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem3);

        jMenuItem6.setText("Kreator Protokołu Przekazania Łożysk");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem6);

        jMenuItem7.setText("Kreator Kart Kontroli");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem7);

        jMenuBar1.add(jMenu3);

        jMenu2.setText("Pomoc");

        jMenuItem2.setText("O programie");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1066, Short.MAX_VALUE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("");

        getAccessibleContext().setAccessibleName("Material Manager v. 0.0.5.3");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        FrmAbout frmAbout = new FrmAbout();
        frmAbout.show();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        // TODO add your handling code here:
        DatabaseConfigManager.getInstance().setupCertDirectory(tfCertPath);
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        // TODO add your handling code here:
        DatabaseConfigManager.getInstance().saveCertDirectory(tfCertPath.getText());
    }//GEN-LAST:event_jButton15ActionPerformed

    private void cbDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDbActionPerformed
        // TODO add your handling code here:
        setDatabase();
    }//GEN-LAST:event_cbDbActionPerformed

    private void tfDBadressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfDBadressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfDBadressActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            // TODO add your handling code here:
            DatabaseManager.getInstance().setDbAdress(tfDBadress.getText());
            DatabaseManager.getInstance().setDbName(tfDb.getText());
            DatabaseManager.getInstance().setLogin(tfLogin.getText());
            DatabaseManager.getInstance().setPass(tfPassword.getText());

            DatabaseManager.getInstance().connectDatabase(tfLogin.getText(),
                    tfPassword.getText(), tfDBadress.getText() + tfDb.getText());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jPanelMaterialyComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanelMaterialyComponentShown
        // TODO add your handling code here:
        if (NodeManager.treeKontraktyMaterialyRead) {
            NodeManager.getInstance().refresthNodesTree(treeKontraktyMaterialy);
            NodeManager.treeKontraktyMaterialyRead = false;
        }
    }//GEN-LAST:event_jPanelMaterialyComponentShown

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:
        BomManager.getInstance().copySelectedDataToBomTable(tblBOMImport, tblBom);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        BomManager.getInstance().importBomFromExcel(tblBOMImport);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:

        if (treeKontraktyMaterialy.getSelectionCount() > 0) {
            if (tfNazwaBoma.getText().trim().length() > 0) {
                NodeManager.getInstance().addNode(tfNazwaBoma, treeKontraktyMaterialy);
            } else {
                JOptionPane.showMessageDialog(null, "Wpisz nazwę BOMa");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Zaznacz węzeł");
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void tblPathMaterialyMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPathMaterialyMousePressed
        // TODO add your handling code here:

        if (evt.getClickCount() == 2) {
            if (tblPathMaterialy.getSelectedRowCount() > 0) {
                AtestManager.getInstance().openCertFile(tblPathMaterialy, tfCertPath.getText());
//                AtestManager.getInstance().openCertFile(
//                        tblPathMaterialy.getValueAt(tblPathMaterialy.getSelectedRow(), 1).toString());
            } else {
                JOptionPane.showMessageDialog(null, "Zaznacz atest.");
            }
        }
    }//GEN-LAST:event_tblPathMaterialyMousePressed

    private void tblAtestMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAtestMousePressed
        // TODO add your handling code here:
        if (evt.getButton() == 3) {
            popTblAtest.show(tblAtest, evt.getX(), evt.getY());
        }

        AtestManager.getInstance().readCertsFiles(tblAtest, tblPathMaterialy,
                BomManager.getInstance().getAtesty(), tfCertPath.getText());
    }//GEN-LAST:event_tblAtestMousePressed

    private void tblBomMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBomMousePressed

        ((DefaultTableModel) tblPathMaterialy.getModel()).setRowCount(0);

        if (tblBom.getSelectedRowCount() > 0) {
            BomManager.getInstance().setSelectedBomMaterial(tblBom.getSelectedRow());
            DefaultTableModel model = (DefaultTableModel) tblAtest.getModel();
            model.setRowCount(0);

            BomManager.getInstance().refreshAtestTable(tblAtest,
                    BomManager.getInstance().getBomMaterials()[BomManager.getInstance().getSelectedBomMaterial()].getId());

            if (evt.getButton() == 3) {
                popBomTable.show(tblBom, evt.getX(), evt.getY());
            }
        }
    }//GEN-LAST:event_tblBomMousePressed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        // TODO add your handling code here:
        //        if (tblAtest.getSelectedRowCount() > 0) {
        //            AtestManager.getInstance().openCertFile(
        //                    tblAtest.getValueAt(tblAtest.getSelectedRow(), 1).toString());
        //        } else {
        //            JOptionPane.showMessageDialog(null, "Zaznacz atest.");
        //        }

        if (tblPathMaterialy.getSelectedRowCount() > 0) {

            AtestManager.getInstance().openCertFile(tblPathMaterialy, tfCertPath.getText());
//            AtestManager.getInstance().openCertFile(
//                    tblPathMaterialy.getValueAt(tblPathMaterialy.getSelectedRow(), 1).toString());
        } else {
            JOptionPane.showMessageDialog(null, "Zaznacz atest.");
        }
    }//GEN-LAST:event_jButton16ActionPerformed

    private void btnZapiszZmianyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZapiszZmianyActionPerformed
        // TODO add your handling code here:

        BomManager.getInstance().addBomIntoDB(tblBom, treeKontraktyMaterialy);
        BomManager.getInstance().importBomFromDB(treeKontraktyMaterialy, tblBom);
    }//GEN-LAST:event_btnZapiszZmianyActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        if (tblBom.getSelectedRowCount() > 0) {
            FrmAtestIndicator frmAtestIndicator = new FrmAtestIndicator();
            frmAtestIndicator.show(tblBom, tblAtest, treeKontraktyMaterialy);
        } else {
            JOptionPane.showMessageDialog(null, "Zaznacz materiał");
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void treeKontraktyMaterialyMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeKontraktyMaterialyMousePressed
        if (evt.getButton() == 3) {
            popTreeKontraktyMaterialy.show(treeKontraktyMaterialy, evt.getX(), evt.getY());
        }

        ((DefaultTableModel) tblAtest.getModel()).setRowCount(0);
        ((DefaultTableModel) tblBom.getModel()).setRowCount(0);
        ((DefaultTableModel) tblPathMaterialy.getModel()).setRowCount(0);

        BomManager.getInstance().importBomFromDB(treeKontraktyMaterialy, tblBom);


    }//GEN-LAST:event_treeKontraktyMaterialyMousePressed

    private void jPanelAtestyComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanelAtestyComponentShown

        jScrollPane2.setVerticalScrollBarPolicy(jScrollPane1.VERTICAL_SCROLLBAR_ALWAYS);

        try {
            AtestManager.getInstance().readMaterialsFromDB(treeMaterialy, null);
        } catch (SQLException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jPanelAtestyComponentShown

    private void tblPathAtestyMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPathAtestyMousePressed
        // TODO add your handling code here:
        if (evt.getButton() == 3 && tblPathAtesty.getSelectedRowCount() > 0) {
            popTblPathAtesty.show(tblPathAtesty, evt.getX(), evt.getY());
        }

        if (evt.getClickCount() == 2 && evt.getButton() == 1) {
            if (tblPathAtesty.getSelectedRowCount() > 0) {
                AtestManager.getInstance().openCertFile(tblPathAtesty, tfCertPath.getText());
//                AtestManager.getInstance().openCertFile(
//                        tblPathAtesty.getValueAt(tblPathAtesty.getSelectedRow(), 1).toString());
            } else {
                JOptionPane.showMessageDialog(null, "Zaznacz atest.");
            }
        }
    }//GEN-LAST:event_tblPathAtestyMousePressed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        if (tblAtesty.getSelectedRowCount() > 0) {
            AtestManager.getInstance().addPath(tfCertPath.getText(), lbSciezka);
            AtestManager.getInstance().addFilePathIntoTable(tblAtesty,
                    tblPathAtesty, tfCertPath.getText());
            AtestManager.getInstance().readCertsFiles(tblAtesty, tblPathAtesty,
                    AtestManager.getInstance().getAtesty(), tfCertPath.getText());
        } else {
            JOptionPane.showMessageDialog(null, "Wybierz atest");
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    /**
     * Uruchamia dodawanie nowego atestu.
     *
     * @param evt
     */
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        AtestManager.getInstance().addNewCert(
                tfNazwaAtestu.getText(), tfNrZamowienia.getText(),
                tfNrWZ.getText(), lbSciezka.getText(), tfZp.getText(),
                tfDostawca.getText(), tfPkd.getText(),
                tfDataDostawy.getText(), treeMaterialy);

        if (cbClearFields.isSelected()) {
            tfNazwaAtestu.setText("");
            tfNrZamowienia.setText("");
            tfNrWZ.setText("");
            tfZp.setText("");
            tfDostawca.setText("");
            tfPkd.setText("");
            tfDataDostawy.setText("");
        }

        if (treeMaterialy.getSelectionCount() > 0) {
            AtestManager.getInstance().readCerts(tblAtesty, treeMaterialy, 0, null);
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:

        if (tblPathAtesty.getSelectedRowCount() > 0) {
            AtestManager.getInstance().openCertFile(tblPathAtesty, tfCertPath.getText());
//            AtestManager.getInstance().openCertFile(
//                    tblPathAtesty.getValueAt(tblPathAtesty.getSelectedRow(), 1).toString());
        } else {
            JOptionPane.showMessageDialog(null, "Zaznacz atest.");
        }
    }//GEN-LAST:event_jButton14ActionPerformed

    private void tfFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfFilterKeyReleased
        ((DefaultTableModel) tblAtesty.getModel()).setRowCount(0);
        try {
            // TODO add your handling code here:
            AtestManager.getInstance().readMaterialsFromDB(treeMaterialy, tfFilter.getText().trim());
        } catch (SQLException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tfFilterKeyReleased

    /**
     * Uruchamia dodawanie nowego materiału
     *
     * @param evt
     */
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        AtestManager.getInstance().addNewMaterial(tfNazwaMaterialu.getText(),
                treeMaterialy);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void treeMaterialyMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeMaterialyMousePressed
        AtestManager.getInstance().readCerts(tblAtesty, treeMaterialy, 0, null);

        if (evt.getButton() == 3) {
            popTreeMaterialy.show(treeMaterialy, evt.getX(), evt.getY());
        }

        ((DefaultTableModel) tblPathAtesty.getModel()).setRowCount(0);
    }//GEN-LAST:event_treeMaterialyMousePressed

    private void jPanelKontraktyComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanelKontraktyComponentShown
        // TODO add your handling code here:
        //JOptionPane.showMessageDialog(null, "Pokazanie panelu Kontrakty.");

        NodeManager.getInstance().refresthNodesTree(treeKontrakty);
    }//GEN-LAST:event_jPanelKontraktyComponentShown

    /**
     * Dodanie nowego węzła do drzewa kontraktów.
     *
     * @param evt
     */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //NodeManager.getInstance().addNode(tfNazwaWezla, treeKontrakty);
        NodeManager.getInstance().addNode(tfNazwaWezla, treeKontrakty);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void treeKontraktyMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeKontraktyMousePressed
        if (evt.getButton() == 3) {
            popTreeKontrakty.show(treeKontrakty, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_treeKontraktyMousePressed

    private void tfAtestySzukajKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfAtestySzukajKeyReleased
        // TODO add your handling code here:
        /* 1 - nazwa, 2 - zamówienie, 3 - zp, 4 - dostawca*/

        int key = evt.getKeyCode();
        if (evt.getSource() == tfAtestySzukaj) {
            if (key == KeyEvent.VK_ENTER) {
                int indeks = cbAtestySzukaj.getSelectedIndex() + 1;
                ((DefaultTableModel) tblPathAtesty.getModel()).setRowCount(0);
                AtestManager.getInstance().readCerts(tblAtesty, treeMaterialy,
                        indeks, tfAtestySzukaj.getText().trim());
            }
        }

//        int indeks = cbAtestySzukaj.getSelectedIndex() + 1;
//
//        ((DefaultTableModel) tblPathAtesty.getModel()).setRowCount(0);
//        
//        AtestManager.getInstance().readCerts(tblAtesty, treeMaterialy,
//                indeks, tfAtestySzukaj.getText().trim());
    }//GEN-LAST:event_tfAtestySzukajKeyReleased

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:

        // Okdreśla tryb wg którego mają być wyszukiwane atesty.
        int mode = cbAtestySzukaj.getSelectedIndex() + 1;

        ((DefaultTableModel) tblPathAtesty.getModel()).setRowCount(0);

        AtestManager.getInstance().readCerts(tblAtesty, treeMaterialy,
                mode, tfAtestySzukaj.getText().trim());
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jPanelPkdComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanelPkdComponentShown
        // TODO add your handling code here:

        DatabaseManager.getInstance().pingDb();

        /* Wyciągnięcie numeru pkd z bazy i wrzucenie go w odpowiednie
         * text fieldy. */
        PkdNumber pkdNumber = DatabaseManager.getInstance().readPkdNumber();

        /* Sprawdzenie roku i miesiąca - aktualizacja numeru PKD (miesiąc, rok)
           jeżeli ów numery są inne niżeli w numerze PKD z bazy danych */
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();

        if (month != pkdNumber.getActualPkdMonthNumber()) {
            pkdNumber.setActualPkdMonthNumber(month);
            pkdNumber.setActualPkdNumber(1);
        }

        if (year != pkdNumber.getActualPkdYearNumber()) {
            pkdNumber.setActualPkdYearNumber(year);
            pkdNumber.setActualPkdNumber(1);
        }

        tfPkdNr.setText(String.valueOf(pkdNumber.getActualPkdNumber()));
        tfPkdMonthNr.setText(String.valueOf(pkdNumber.getActualPkdMonthNumber()));
        tfPkdYearNr.setText(String.valueOf(pkdNumber.getActualPkdYearNumber()));
    }//GEN-LAST:event_jPanelPkdComponentShown

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:

        int _indeks = cbPkdMode.getSelectedIndex() + 1;

        PkdMgr.getInstance().fillPkdResultTable(tblPdkResult,
                tfPkdFilter.getText().trim(), _indeks);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        // TODO add your handling code here:

        if (tblPdkResult.getSelectedRows().length > 0) {
            PkdMgr.getInstance().moveData(tblPdkResult, tblPkd);
        }
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        // TODO add your handling code here:
        PkdMgr.getInstance().clearCertInToPkdTable();
        ((DefaultTableModel) tblPkd.getModel()).setRowCount(0);
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        // TODO add your handling code here:
        ((DefaultTableModel) tblPkd.getModel()).addRow(new Object[11]);
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        // TODO add your handling code here:        
        PkdMgr.getInstance().removeCertFromToPkdTable(tblPkd.getSelectedRow());
        ((DefaultTableModel) tblPkd.getModel()).removeRow(tblPkd.getSelectedRow());
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        // TODO add your handling code here:

        /* Zmienna przechowująca cały numer pkd */
        String stringPkdNumber
                = tfPkdNr.getText() + "."
                + tfPkdMonthNr.getText() + "."
                + tfPkdYearNr.getText();

        /* instancja klasy przechowującej numer pkd do aktualizacji numeru 
           Pkd w bazie danych oraz do zwiększenia numeru */
        PkdNumber pkdNumber = new PkdNumber();
        pkdNumber.setActualPkdNumber(Integer.valueOf(tfPkdNr.getText()));
        pkdNumber.setActualPkdMonthNumber(Integer.valueOf(tfPkdMonthNr.getText()));
        pkdNumber.setActualPkdYearNumber(Integer.valueOf(tfPkdYearNr.getText()));

        try {
//            if (tfPkdNumber.getText().trim().length() < 1) {
//                JOptionPane.showMessageDialog(null, "Podaj numer PKD");
            if (stringPkdNumber.trim().length() < 1) {
                JOptionPane.showMessageDialog(null, "Podaj numer PKD");
            } else {

                PkdMgr.getInstance().readDocx();

                PkdMgr._nrPkd = stringPkdNumber;
                PkdMgr._sprawdzajacy = tfSprawdzajacy.getText();
                PkdMgr._kontrakt = tfPkdKontrakt.getText();
                PkdMgr._data = tfPkdData.getText();
                PkdMgr._tablePkd = tblPkd;

                PkdMgr.getInstance().findAndReplaceInDocX(this);

                PkdMgr.getInstance().updatePkdNumbers();

                /* Zwiększenie numeru PKD */
                pkdNumber.setActualPkdNumber(pkdNumber.getActualPkdNumber() + 1);
                tfPkdNr.setText(String.valueOf(pkdNumber.getActualPkdNumber()));

                DatabaseManager.getInstance().renamePkdNumber(pkdNumber, cbDb.getSelectedIndex());
            }

        } catch (IOException | InvalidFormatException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Błąd: " + ex);
        }
    }//GEN-LAST:event_jButton22ActionPerformed

    private void tfPkdFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfPkdFilterKeyReleased
        // TODO add your handling code here:
        int key = evt.getKeyCode();
        if (evt.getSource() == tfPkdFilter) {
            if (key == KeyEvent.VK_ENTER) {
                int _indeks = cbPkdMode.getSelectedIndex() + 1;
                PkdMgr.getInstance().fillPkdResultTable(tblPdkResult,
                        tfPkdFilter.getText().trim(), _indeks);
            }
        }

    }//GEN-LAST:event_tfPkdFilterKeyReleased

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        Raports raports = new Raports();
        raports.show();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void tblBOMImportMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBOMImportMousePressed
        // TODO add your handling code here:
        if (evt.getButton() == 3) {
            popTableBomImport.show(tblBOMImport, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tblBOMImportMousePressed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // TODO add your handling code here:
        FrmCertToMany _frmCertToMany = new FrmCertToMany();
        _frmCertToMany.show();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void tfDataDostawyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfDataDostawyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfDataDostawyActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        if (MmConfigManager.getMmConfig().getBearingRegistryFilePath() != null) {
            FrmZpCreator frmZpCreator = new FrmZpCreator();
            frmZpCreator.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Nie znaleziono pliku rejestru łożysk. Wskaż lokalizację pliku w zakładce Ustawienia");
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        if (MmConfigManager.getMmConfig().getBearingRegistryFilePath() != null) {
            FrmTransProtocolCreator frmTransProtocolCreator = new FrmTransProtocolCreator();
            frmTransProtocolCreator.show();
        } else {
            JOptionPane.showMessageDialog(null, "Nie znaleziono pliku rejestru łożysk. Wskaż lokalizację pliku w zakładce Ustawienia");
        }
    }//GEN-LAST:event_jMenuItem6ActionPerformed

  private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
      // TODO add your handling code here:
      if (MmConfigManager.getMmConfig().getBearingRegistryFilePath() != null) {
          FrmKKCreator frmKKCreator = new FrmKKCreator();
          frmKKCreator.show();
      } else {
          JOptionPane.showMessageDialog(null, "Nie znaleziono pliku rejestru łożysk. Wskaż lokalizację pliku w zakładce Ustawienia");
      }

  }//GEN-LAST:event_jMenuItem7ActionPerformed

    /**
     * Ustawia ścieżkę pliku rejestru łożysk.
     *
     * @param evt Zdarzenie kliknięcia
     */
  private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
      MmConfigManager.saveBearingRegistryPath(tfBearingRegistryPath);

  }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton10ActionPerformed

    private void tblAtestyMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAtestyMousePressed
        // TODO add your handling code here:
        if (evt.getButton() == 3) {
            popTblAtesty.show(tblAtesty, evt.getX(), evt.getY());
        }

        AtestManager.getInstance().readCertsFiles(tblAtesty, tblPathAtesty,
            AtestManager.getInstance().getAtesty(), tfCertPath.getText());
    }//GEN-LAST:event_tblAtestyMousePressed

    private void btnGenerateCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateCardActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGenerateCardActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void treeMaterialy1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeMaterialy1MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_treeMaterialy1MousePressed

    private void btnGenerateCard1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateCard1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGenerateCard1ActionPerformed

    private void btnGenerateCard2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateCard2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGenerateCard2ActionPerformed

    private void setDatabase() {
        switch (cbDb.getSelectedIndex()) {
            case 2:
                tfDBadress.setText("//localhost/");
                DatabaseManager.getInstance().setDbName("aspekt_materials");
                tfDb.setText("aspekt_materials");
                tfLogin.setText("root");
                tfPassword.setText("rasengan");
                break;
            case 3:
                tfDBadress.setText("//80.211.31.33/");
                DatabaseManager.getInstance().setDbName("materials");
                tfDb.setText("materials");
                tfLogin.setText("kprmuser01");
                tfPassword.setText("386Sx20");
                break;
            case 0:
                tfDBadress.setText("//195.114.1.195/");
                DatabaseManager.getInstance().setDbName("aspekt_materials");
                tfDb.setText("aspekt_materials");
                tfLogin.setText("aspekt_sqladmin");
                tfPassword.setText("tere7-67CS2");
                break;
            case 1:
                tfDBadress.setText("//195.114.1.195/");
                DatabaseManager.getInstance().setDbName("aspekt_materials_test");
                tfDb.setText("aspekt_materials_test");
                tfLogin.setText("aspekt_sqladmin");
                tfPassword.setText("tere7-67CS2");
                break;
            default:
                break;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new FrmMain().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenerateCard;
    private javax.swing.JButton btnGenerateCard1;
    private javax.swing.JButton btnGenerateCard2;
    private javax.swing.JButton btnZapiszZmiany;
    private javax.swing.JComboBox<String> cbAtestySzukaj;
    private javax.swing.JCheckBox cbClearFields;
    private javax.swing.JComboBox<String> cbDb;
    private javax.swing.JComboBox<String> cbPkdMode;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelAtesty;
    private javax.swing.JPanel jPanelKontrakty;
    private javax.swing.JPanel jPanelMaterialy;
    private javax.swing.JPanel jPanelPkd;
    private javax.swing.JPanel jPanelUstawienia;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JSplitPane jSplitPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lbSciezka;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPopupMenu popBomTable;
    private javax.swing.JPopupMenu popTableBomImport;
    private javax.swing.JPopupMenu popTblAtest;
    private javax.swing.JPopupMenu popTblAtesty;
    private javax.swing.JPopupMenu popTblPathAtesty;
    private javax.swing.JPopupMenu popTreeKontrakty;
    private javax.swing.JPopupMenu popTreeKontraktyMaterialy;
    private javax.swing.JPopupMenu popTreeMaterialy;
    private javax.swing.JTable tblAtest;
    private javax.swing.JTable tblAtesty;
    private javax.swing.JTable tblBOMImport;
    private javax.swing.JTable tblBom;
    private javax.swing.JTable tblPathAtesty;
    private javax.swing.JTable tblPathMaterialy;
    private javax.swing.JTable tblPdkResult;
    private javax.swing.JTable tblPkd;
    private javax.swing.JTextField tfAtestySzukaj;
    private javax.swing.JTextField tfBearingRegistryPath;
    private javax.swing.JTextField tfCertPath;
    private javax.swing.JTextField tfContractNumber;
    private javax.swing.JTextField tfContractNumber1;
    private javax.swing.JTextField tfContractNumber2;
    private javax.swing.JTextField tfContractNumber3;
    private javax.swing.JTextField tfContractNumber4;
    private javax.swing.JTextField tfContractNumber5;
    private javax.swing.JTextField tfContractNumber6;
    private javax.swing.JTextField tfContractNumber7;
    private javax.swing.JTextField tfContractNumber8;
    private javax.swing.JTextField tfDBadress;
    private javax.swing.JFormattedTextField tfDataDostawy;
    private javax.swing.JFormattedTextField tfDataDostawy1;
    private javax.swing.JTextField tfDb;
    private javax.swing.JTextField tfDostawca;
    private javax.swing.JTextField tfFilter;
    private javax.swing.JTextField tfLogin;
    private javax.swing.JTextField tfNazwaAtestu;
    private javax.swing.JTextField tfNazwaBoma;
    private javax.swing.JTextField tfNazwaMaterialu;
    private javax.swing.JTextField tfNazwaWezla;
    private javax.swing.JTextField tfNrWZ;
    private javax.swing.JTextField tfNrZamowienia;
    private javax.swing.JPasswordField tfPassword;
    private javax.swing.JTextField tfPkd;
    private javax.swing.JTextField tfPkdData;
    private javax.swing.JTextField tfPkdFilter;
    private javax.swing.JTextField tfPkdKontrakt;
    private javax.swing.JTextField tfPkdMonthNr;
    private javax.swing.JTextField tfPkdNr;
    private javax.swing.JTextField tfPkdYearNr;
    private javax.swing.JTextField tfSprawdzajacy;
    private javax.swing.JTextField tfZp;
    private javax.swing.JTree treeKontrakty;
    private javax.swing.JTree treeKontraktyMaterialy;
    private javax.swing.JTree treeMaterialy;
    private javax.swing.JTree treeMaterialy1;
    // End of variables declaration//GEN-END:variables
}
