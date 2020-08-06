/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Frames;

import ControlCard.KKTableRenderer;
import MyClasses.TableOutputHeaderMouseListener;
import MyClasses.Utilities;
import com.kprm.materialmanager.AtestIndicatorManager;
import com.kprm.materialmanager.AtestManager;
import com.kprm.materialmanager.BomManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.eclipse.wst.xml.xpath2.processor.internal.ast.SequenceType;

/**
 *
 * @author wakan
 */
public class FrmAtestIndicator extends javax.swing.JFrame {

  /**
   * Tryb pracy: 0 - przyporządkowanie atestu z poziomu materiału w głównym
   * oknie 1 - przyporządkowanie atestu do wielu materiałów z poziomu CertToMany
   */
  private int mode = 0;
  private JTable tblAtests;
  private JTable tblBom;
  private JTree treeKontrakty;

  /**
   * Creates new form FrmAtestIndicator
   */
  public FrmAtestIndicator() {
    initComponents();
    initPopups();
    createTableHeadersClickListeners();

    DefaultTableCellRenderer renderer = new KKTableRenderer();

    tblOutput.setDefaultRenderer(Object.class, renderer);
  }

  // Tworzy menu popUp
  private void initPopups() {
    JMenuItem itmMaterialTypesAdd = new JMenuItem("Dodaj");
    JMenuItem itmMaterialTypesRemove = new JMenuItem("Usuń");
    JMenuItem itmMaterialTypesRename = new JMenuItem("Zmień");

    // Dodanie typu materiału.
    itmMaterialTypesAdd.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        String materialType = JOptionPane.showInputDialog(
                null, "Podaj typ materiału", "Wpisz wartość",
                JOptionPane.QUESTION_MESSAGE);

        AtestIndicatorManager.getInstance().addMaterialType(materialType);
        AtestIndicatorManager.getInstance().refreshMaterialTypes(lstMaterialTypes);
      }
    });

    // Usunięcie typu materiału
    itmMaterialTypesRemove.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!lstMaterialTypes.isSelectionEmpty()) {
          int question = JOptionPane.showConfirmDialog(
                  null, "Czy na pewno usunąć?", "Usunięcie typu materiału",
                  JOptionPane.YES_OPTION, SequenceType.QUESTION);

          if (question == JOptionPane.YES_OPTION) {
            AtestIndicatorManager.getInstance().removeMaterialType(
                    lstMaterialTypes.getSelectedValue().toUpperCase());
            AtestIndicatorManager.getInstance().refreshMaterialTypes(lstMaterialTypes);
          }
        } else {
          JOptionPane.showMessageDialog(null, "Zaznacz typ materiału", "BŁĄD", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    // Zmiana nazwy typu materiału
    itmMaterialTypesRename.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!lstMaterialTypes.isSelectionEmpty()) {
          String newMaterialType = JOptionPane.showInputDialog(
                  null, "Podaj typ materiału", "Wpisz wartość", JOptionPane.QUESTION_MESSAGE);

          AtestIndicatorManager.getInstance().renameMaterialType(
                  lstMaterialTypes.getSelectedValue(), newMaterialType);
          AtestIndicatorManager.getInstance().refreshMaterialTypes(lstMaterialTypes);
        } else {
          JOptionPane.showMessageDialog(null, "Zaznacz typ materiału", "BŁĄD", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    popMaterialTypes.add(itmMaterialTypesAdd);
    popMaterialTypes.add(itmMaterialTypesRemove);
    popMaterialTypes.add(itmMaterialTypesRename);
    lstMaterialTypes.add(popMaterialTypes);

    JMenuItem itmMaterialTypesKeyWordsAdd = new JMenuItem("Dodaj");
    JMenuItem itmMaterialTypesKeyWordsRemove = new JMenuItem("Usuń");

    // Dodanie typu materiału.
    itmMaterialTypesKeyWordsAdd.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (!lstMaterialTypes.isSelectionEmpty()) {
          // Wyświetlenie InputBoxa pytającego o słowo kluczowe
          String keyWord = JOptionPane.showInputDialog(
                  null, "Podaj słowo kluczowe dla: "
                  + lstMaterialTypes.getSelectedValue(),
                  "Wpisz wartość",
                  JOptionPane.QUESTION_MESSAGE);

          // Dodanie słowa kluczowego
          AtestIndicatorManager.getInstance().addMaterialTypeKeyWord(
                  keyWord, lstMaterialTypes.getSelectedValue());

          // Odświeżenie zawartości listy słów kluczowych
          AtestIndicatorManager.getInstance().refreshMaterialTypeKeyWord(
                  lstMaterialTypes.getSelectedValue(), lstMaterialTypesKeyWords);

        } else {
          JOptionPane.showMessageDialog(null, "Zaznacz typ materiału", "BŁĄD", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    // Usunięcie typu materiału
    itmMaterialTypesKeyWordsRemove.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!lstMaterialTypes.isSelectionEmpty()
                || !lstMaterialTypesKeyWords.isSelectionEmpty()) {

          int question = JOptionPane.showConfirmDialog(
                  null, "Czy na pewno usunąć?", "Usunięcie słowa kluczowego",
                  JOptionPane.YES_OPTION, SequenceType.QUESTION);

          if (question == JOptionPane.YES_OPTION) {
            AtestIndicatorManager.getInstance().removeMaterialTypeKeyWord(
                    lstMaterialTypes.getSelectedValue(),
                    lstMaterialTypesKeyWords.getSelectedValue());

            AtestIndicatorManager.getInstance().refreshMaterialTypeKeyWord(
                    lstMaterialTypes.getSelectedValue(),
                    lstMaterialTypesKeyWords);
          }
        } else {
          JOptionPane.showMessageDialog(null, "Zaznacz typ materiału/słowo kluczowe", "BŁĄD", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    popMaterialTypesKeyWords.add(itmMaterialTypesKeyWordsAdd);
    popMaterialTypesKeyWords.add(itmMaterialTypesKeyWordsRemove);
    lstMaterialTypesKeyWords.add(popMaterialTypesKeyWords);
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    popMaterialTypes = new javax.swing.JPopupMenu();
    popMaterialTypesKeyWords = new javax.swing.JPopupMenu();
    jTabbedPane1 = new javax.swing.JTabbedPane();
    jPanel1 = new javax.swing.JPanel();
    jSplitPane1 = new javax.swing.JSplitPane();
    jPanel6 = new javax.swing.JPanel();
    jPanel3 = new javax.swing.JPanel();
    jLabel3 = new javax.swing.JLabel();
    tfFilter = new javax.swing.JTextField();
    jScrollPane3 = new javax.swing.JScrollPane();
    treeMaterialy = new javax.swing.JTree();
    jPanel5 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tblOutput = new javax.swing.JTable(){
      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
      }
    };
    jPanel2 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    tfAtest = new javax.swing.JTextField();
    tfMaterial = new javax.swing.JTextField();
    jLabel2 = new javax.swing.JLabel();
    jButton2 = new javax.swing.JButton();
    btnAutoAssign = new javax.swing.JButton();
    jButton1 = new javax.swing.JButton();
    jPanel4 = new javax.swing.JPanel();
    lblBomMaterial = new javax.swing.JLabel();
    jPanel7 = new javax.swing.JPanel();
    jSplitPane2 = new javax.swing.JSplitPane();
    jPanel8 = new javax.swing.JPanel();
    jScrollPane2 = new javax.swing.JScrollPane();
    lstMaterialTypes = new javax.swing.JList<>();
    jPanel9 = new javax.swing.JPanel();
    jScrollPane4 = new javax.swing.JScrollPane();
    lstMaterialTypesKeyWords = new javax.swing.JList<>();

    setTitle("Przyporządkuj atest");
    setIconImage(new javax.swing.ImageIcon(getClass().getResource("/product.png")).getImage());
    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });

    jTabbedPane1.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N

    jSplitPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
    jSplitPane1.setDividerLocation(340);

    jPanel6.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        jPanel6ComponentShown(evt);
      }
    });

    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filtr", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14))); // NOI18N

    jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jLabel3.setText("Nazwa:");

    tfFilter.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N
    tfFilter.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(java.awt.event.KeyEvent evt) {
        tfFilterKeyReleased(evt);
      }
    });

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel3)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(tfFilter)
        .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(tfFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel3))
        .addContainerGap())
    );

    treeMaterialy.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
    javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Materiały");
    treeMaterialy.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
    treeMaterialy.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        treeMaterialyMousePressed(evt);
      }
    });
    jScrollPane3.setViewportView(treeMaterialy);

    javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
    jPanel6.setLayout(jPanel6Layout);
    jPanel6Layout.setHorizontalGroup(
      jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel6Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane3)
          .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    jPanel6Layout.setVerticalGroup(
      jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );

    jSplitPane1.setLeftComponent(jPanel6);

    tblOutput.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
    tblOutput.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "Nazwa", "Materiał", "Nr WZ", "Nr Zamówienia", "Data dodania"
      }
    ));
    jScrollPane1.setViewportView(tblOutput);
    if (tblOutput.getColumnModel().getColumnCount() > 0) {
      tblOutput.getColumnModel().getColumn(3).setMinWidth(100);
      tblOutput.getColumnModel().getColumn(3).setMaxWidth(300);
      tblOutput.getColumnModel().getColumn(4).setMinWidth(100);
      tblOutput.getColumnModel().getColumn(4).setMaxWidth(300);
    }

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Szukaj", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14))); // NOI18N

    jLabel1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jLabel1.setText("Nazwa atestu:");

    tfAtest.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N
    tfAtest.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(java.awt.event.KeyEvent evt) {
        tfAtestKeyReleased(evt);
      }
    });

    tfMaterial.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
    tfMaterial.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(java.awt.event.KeyEvent evt) {
        tfMaterialKeyReleased(evt);
      }
    });

    jLabel2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jLabel2.setText("Materiał:");

    jButton2.setBackground(new java.awt.Color(0, 153, 0));
    jButton2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jButton2.setText("SZUKAJ");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton2ActionPerformed(evt);
      }
    });

    btnAutoAssign.setBackground(new java.awt.Color(0, 153, 0));
    btnAutoAssign.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    btnAutoAssign.setText("AUTO ATEST");
    btnAutoAssign.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnAutoAssignActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(tfAtest, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel2)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(tfMaterial, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jButton2)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnAutoAssign)
        .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(tfAtest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel2)
          .addComponent(tfMaterial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jButton2)
          .addComponent(btnAutoAssign))
        .addContainerGap(16, Short.MAX_VALUE))
    );

    jButton1.setBackground(new java.awt.Color(0, 153, 0));
    jButton1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jButton1.setText("ZATWIERDŹ ATEST");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
      jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jScrollPane1)
          .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(jPanel5Layout.createSequentialGroup()
            .addGap(0, 0, Short.MAX_VALUE)
            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    jPanel5Layout.setVerticalGroup(
      jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel5Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jButton1)
        .addContainerGap())
    );

    jSplitPane1.setRightComponent(jPanel5);

    jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

    lblBomMaterial.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    lblBomMaterial.setText("Materiał BOM");

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(lblBomMaterial, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel4Layout.setVerticalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(lblBomMaterial)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1185, Short.MAX_VALUE)
          .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jSplitPane1)
        .addContainerGap())
    );

    jTabbedPane1.addTab("Atesty", jPanel1);

    jSplitPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    jSplitPane2.setDividerLocation(300);

    jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Typ materiału", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 13))); // NOI18N

    lstMaterialTypes.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N
    lstMaterialTypes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    lstMaterialTypes.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        lstMaterialTypesMousePressed(evt);
      }
    });
    jScrollPane2.setViewportView(lstMaterialTypes);

    javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
    jPanel8.setLayout(jPanel8Layout);
    jPanel8Layout.setHorizontalGroup(
      jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel8Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
        .addContainerGap())
    );
    jPanel8Layout.setVerticalGroup(
      jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel8Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
        .addContainerGap())
    );

    jSplitPane2.setLeftComponent(jPanel8);

    jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Słowa kluczowe", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 13))); // NOI18N

    lstMaterialTypesKeyWords.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N
    lstMaterialTypesKeyWords.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    lstMaterialTypesKeyWords.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        lstMaterialTypesKeyWordsMousePressed(evt);
      }
    });
    jScrollPane4.setViewportView(lstMaterialTypesKeyWords);

    javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
    jPanel9.setLayout(jPanel9Layout);
    jPanel9Layout.setHorizontalGroup(
      jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel9Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1051, Short.MAX_VALUE)
        .addContainerGap())
    );
    jPanel9Layout.setVerticalGroup(
      jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel9Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
        .addContainerGap())
    );

    jSplitPane2.setRightComponent(jPanel9);

    javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
    jPanel7.setLayout(jPanel7Layout);
    jPanel7Layout.setHorizontalGroup(
      jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel7Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jSplitPane2)
        .addContainerGap())
    );
    jPanel7Layout.setVerticalGroup(
      jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel7Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jSplitPane2)
        .addContainerGap())
    );

    jTabbedPane1.addTab("Konfiguracja", jPanel7);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jTabbedPane1)
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jTabbedPane1)
        .addContainerGap())
    );

    pack();
    setLocationRelativeTo(null);
  }// </editor-fold>//GEN-END:initComponents

  /**
   * Tworzy nasłuch w którym program wykrywa kliknięcie w nagłówku tabeli
   * wyników poszukiwania atestów.
   */
  private void createTableHeadersClickListeners() {
    JTableHeader headTblAtesty = tblOutput.getTableHeader();
    headTblAtesty.addMouseListener(new TableOutputHeaderMouseListener(tblOutput));
  }

    private void tfAtestKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfAtestKeyReleased
      // TODO add your handling code here:

      int key = evt.getKeyCode();
      if (evt.getSource() == tfAtest) {
        if (key == KeyEvent.VK_ENTER) {
          AtestIndicatorManager.getInstance().searchCert(tfAtest.getText(),
                  tfMaterial.getText(), tblOutput);
        }
      }

//        AtestIndicatorManager.getInstance().searchCert(tfAtest.getText(),
//                tfMaterial.getText(), tblOutput);
    }//GEN-LAST:event_tfAtestKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
      // TODO add your handling code here:
      if (this.mode == 0) {
        AtestIndicatorManager.getInstance().assignCert(tblOutput, tblAtests, tblBom);

        BomManager.getInstance().refreshAtestTable(
                tblAtests, BomManager.getInstance().getBomMaterials()[BomManager.getInstance().getSelectedBomMaterial()].getId());
        BomManager.getInstance().importBomFromDB(treeKontrakty, tblBom);

        this.setVisible(false);
      } else {
        AtestIndicatorManager.getInstance().assignCert(tblOutput, tblBom);
        this.setVisible(false);
      }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tfMaterialKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfMaterialKeyReleased
      // TODO add your handling code here:
      int key = evt.getKeyCode();
      if (evt.getSource() == tfMaterial) {
        if (key == KeyEvent.VK_ENTER) {
          AtestIndicatorManager.getInstance().searchCert(tfAtest.getText(),
                  tfMaterial.getText(), tblOutput);
        }
      }
//        AtestIndicatorManager.getInstance().searchCert(tfAtest.getText(),
//                tfMaterial.getText(), tblOutput);
    }//GEN-LAST:event_tfMaterialKeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
      // TODO add your handling code here:
      AtestIndicatorManager.getInstance().searchCert(tfAtest.getText(),
              tfMaterial.getText(), tblOutput);
    }//GEN-LAST:event_jButton2ActionPerformed

  private void treeMaterialyMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeMaterialyMousePressed
    //AtestManager.getInstance().readCerts(tblAtesty, treeMaterialy, 0, null);

    DefaultMutableTreeNode selectedNode
            = (DefaultMutableTreeNode) treeMaterialy.getLastSelectedPathComponent();

    AtestIndicatorManager.getInstance().searchCert("",
            selectedNode.toString(), tblOutput);
  }//GEN-LAST:event_treeMaterialyMousePressed

  private void jPanel6ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel6ComponentShown
    // TODO add your handling code here:
    try {
      AtestManager.getInstance().readMaterialsFromDB(treeMaterialy, null);
    } catch (SQLException ex) {
      Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
    }
  }//GEN-LAST:event_jPanel6ComponentShown

  private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
    // TODO add your handling code here:
    try {
      AtestManager.getInstance().readMaterialsFromDB(treeMaterialy, null);
    } catch (SQLException ex) {
      Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
    }

    AtestIndicatorManager.getInstance().refreshMaterialTypes(lstMaterialTypes);
    this.matchMaterial();
  }//GEN-LAST:event_formComponentShown

  private void tfFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfFilterKeyReleased
    // TODO add your handling code here:
    //((DefaultTableModel) tblAtesty.getModel()).setRowCount(0);
    try {
      // TODO add your handling code here:
      AtestManager.getInstance().readMaterialsFromDB(treeMaterialy, tfFilter.getText().trim());
    } catch (SQLException ex) {
      Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
    }
  }//GEN-LAST:event_tfFilterKeyReleased

  
  private void btnAutoAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAutoAssignActionPerformed
    
  }//GEN-LAST:event_btnAutoAssignActionPerformed

  /**
   * Odświeża drzewko materiałów wg zadanej listy z materiałami.
   * @param materialList Lista materiałów
   */
  private void refreshTreeMaterial(ArrayList<String> materialList) {
    DefaultTreeModel model = (DefaultTreeModel) treeMaterialy.getModel();
    DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();

    //System.out.println("Dzieci: " + model.getChildCount(rootNode));
    rootNode.removeAllChildren();

    //ArrayList<Material> materialy = DatabaseManager.getInstance().readMaterials();
    //this.materials = DatabaseManager.getInstance().readMaterials(nameFilter);
    //Collections.sort(materials, MmComparators.atestNameComparator);
    for (String material : materialList) {
      DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(material);
      model.insertNodeInto(newNode, rootNode, 0);
    }

    model.reload();
  }

  /**
   * 
   */
  public void matchMaterial() {
    //AtestIndicatorManager.getInstance().autoMaterial(lblBomMaterial.getText(), treeMaterialy);
    String material = AtestIndicatorManager.getInstance().matchCert(
            lblBomMaterial.getText().toUpperCase());

    if (material != null) {
      tfFilter.setText(material);
      try {
        // TODO add your handling code here:
        AtestManager.getInstance().readMaterialsFromDB(treeMaterialy, tfFilter.getText().trim());
      } catch (SQLException ex) {
        Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    
    // TODO add your handling code here:
    ArrayList<String> digits = AtestIndicatorManager.getInstance().extractDigits(lblBomMaterial.getText());

    ArrayList<String> materialList = Utilities.visitAllNodes(treeMaterialy);
    System.out.println("SIZE: " + materialList.size());

    ArrayList<String> foundedMaterials = new ArrayList();

    for (String materailFromMaterialList : materialList) {
      System.out.println("---MATERIAL FROM LIST: " + materailFromMaterialList);
      for (String digit : digits) {
        System.out.println("------DIGIT: " + digit);
        if (materailFromMaterialList.contains(digit)) {
          System.out.println("--------- FOUND: " + materailFromMaterialList);
          foundedMaterials.add(materailFromMaterialList);
        }
      }
    }

    System.out.println("SIZE OF FOUNDED MATERIALS BEFORE REMOVING DUPS: " + foundedMaterials.size());

    ArrayList<String> afterRemove = Utilities.removeDuplicates(foundedMaterials);

    System.out.println("SIZE OF FOUNDED MATERIALS AFTER REMOVING DUPS: " + afterRemove.size());

//    for (String digit : digits) {
//      System.out.println("---DIGIT: " + digit);
//      for (String materailFromMaterialList : materialList) {
//        System.out.println("------MATERIAL FROM LIST: " + materailFromMaterialList);
//        if (materailFromMaterialList.contains(digit)) {
//          System.out.println("--------- FOUND: " + materailFromMaterialList);
//          foundedMaterials.add(materailFromMaterialList);
//        }
//      }
//      materialList = new ArrayList<>();
//      
//      for (String foundMaterial: foundedMaterials){
//        String item = foundMaterial;
//        materialList.add(item);
//      }
//      
//      foundedMaterials.clear();
//    }
//    for (String digit: digits){
//      for (String materailFromMaterialList : materialList) {
//        if (!materailFromMaterialList.contains(digit)){
//          materialList.remove(materailFromMaterialList);
//        }
//      }
//    }

    if (afterRemove.size() > 0){
      refreshTreeMaterial(afterRemove);
    }

//    if (materialList.size() > 0) {
//      tfFilter.setText(materialList.get(0));
//    }

  }

  private void lstMaterialTypesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstMaterialTypesMousePressed
    // TODO add your handling code here:
    // Wyświetlenie POPUP
    if (evt.getButton() == 3) {
      popMaterialTypes.show(lstMaterialTypes, evt.getX(), evt.getY());
    } else {

      // Wyczyszczenie listy słów kluczowych
      DefaultListModel model = new DefaultListModel();
      model.clear();
      lstMaterialTypesKeyWords.setModel(model);

      // Odczyt słów kluczowych    
      AtestIndicatorManager.getInstance().refreshMaterialTypeKeyWord(
              lstMaterialTypes.getSelectedValue(), lstMaterialTypesKeyWords);
    }
  }//GEN-LAST:event_lstMaterialTypesMousePressed

  private void lstMaterialTypesKeyWordsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstMaterialTypesKeyWordsMousePressed
    // TODO add your handling code here:
    if (evt.getButton() == 3) {
      popMaterialTypesKeyWords.show(lstMaterialTypesKeyWords, evt.getX(), evt.getY());
    }
  }//GEN-LAST:event_lstMaterialTypesKeyWordsMousePressed

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
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(FrmAtestIndicator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    //</editor-fold>

    //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(() -> {
      new FrmAtestIndicator().setVisible(true);
    });
  }

  /**
   *
   * @param tblBom Tabela z bomami.
   * @param tblAtest Tabela w której znajdują się atesty w zakładce materiały.
   * @param treeKontrakty Drzewko kontraktów.
   */
  public void show(JTable tblBom, JTable tblAtest, JTree treeKontrakty) {
    this.mode = 0;
    this.tblAtests = tblAtest;
    this.tblBom = tblBom;
    this.treeKontrakty = treeKontrakty;

    lblBomMaterial.setText((String) tblBom.getModel().getValueAt(
            tblBom.getSelectedRow(), 0));
    super.show(); //To change body of generated methods, choose Tools | Templates.
  }

  /**
   * Uruchomiony z poziomu przyporządkowania atestu do wielu materiałów.
   *
   * @param tblBomMaterials Tabela z materiałami z bomów
   * @param treeKontrakty Drzewko z kontraktami.
   */
  public void show(JTable tblBomMaterials, JTree treeKontrakty) {
    this.mode = 1;
    this.tblBom = tblBomMaterials;
    this.treeKontrakty = treeKontrakty;

    lblBomMaterial.setText((String) tblBom.getModel().getValueAt(
            tblBom.getSelectedRow(), 0));
    super.show();
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnAutoAssign;
  private javax.swing.JButton jButton1;
  private javax.swing.JButton jButton2;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jPanel5;
  private javax.swing.JPanel jPanel6;
  private javax.swing.JPanel jPanel7;
  private javax.swing.JPanel jPanel8;
  private javax.swing.JPanel jPanel9;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JScrollPane jScrollPane3;
  private javax.swing.JScrollPane jScrollPane4;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.JSplitPane jSplitPane2;
  private javax.swing.JTabbedPane jTabbedPane1;
  private javax.swing.JLabel lblBomMaterial;
  private javax.swing.JList<String> lstMaterialTypes;
  private javax.swing.JList<String> lstMaterialTypesKeyWords;
  private javax.swing.JPopupMenu popMaterialTypes;
  private javax.swing.JPopupMenu popMaterialTypesKeyWords;
  private javax.swing.JTable tblOutput;
  private javax.swing.JTextField tfAtest;
  private javax.swing.JTextField tfFilter;
  private javax.swing.JTextField tfMaterial;
  private javax.swing.JTree treeMaterialy;
  // End of variables declaration//GEN-END:variables
}
