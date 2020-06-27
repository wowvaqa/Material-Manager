/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ControlCard;

import com.kprm.materialmanager.SettingsManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author wakan
 */
public class FrmKKCreator extends javax.swing.JFrame {

  /**
   * Creates new form FrmKKCreator
   */
  public FrmKKCreator() {
    initComponents();
    initPopups();

    tblElastomerInsertDimension.setAutoCreateRowSorter(true);

    try {
      KKCreatorManager.getInstance().openFileAndFillTable(tblKontrakty, this);
    } catch (IOException ex) {
      Logger.getLogger(KKCreatorManager.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    popTblElastomerType = new javax.swing.JPopupMenu();
    jTabbedPane1 = new javax.swing.JTabbedPane();
    jPanel3 = new javax.swing.JPanel();
    jLabel3 = new javax.swing.JLabel();
    jLabel1 = new javax.swing.JLabel();
    tfContractNumber = new javax.swing.JTextField();
    jLabel2 = new javax.swing.JLabel();
    tfObjectNumber = new javax.swing.JTextField();
    jScrollPane1 = new javax.swing.JScrollPane();
    tblKontrakty = new javax.swing.JTable();
    jPanel1 = new javax.swing.JPanel();
    cbBearingKind = new javax.swing.JComboBox<>();
    jLabel8 = new javax.swing.JLabel();
    jLabel4 = new javax.swing.JLabel();
    tfA = new javax.swing.JTextField();
    jLabel5 = new javax.swing.JLabel();
    tfH = new javax.swing.JTextField();
    jLabel6 = new javax.swing.JLabel();
    tfBottomPlate = new javax.swing.JTextField();
    jLabel9 = new javax.swing.JLabel();
    cbBearingType = new javax.swing.JComboBox<>();
    jPanel2 = new javax.swing.JPanel();
    jLabel7 = new javax.swing.JLabel();
    tfDestPath = new javax.swing.JTextField();
    btnGenerateCard = new javax.swing.JButton();
    btnChooseFolder = new javax.swing.JButton();
    cbGenMesure = new javax.swing.JCheckBox();
    jPanel4 = new javax.swing.JPanel();
    jScrollPane2 = new javax.swing.JScrollPane();
    tblElastomerInsertDimension = new javax.swing.JTable();
    jLabel10 = new javax.swing.JLabel();
    jPanel5 = new javax.swing.JPanel();
    jLabel11 = new javax.swing.JLabel();
    tfBearingType = new javax.swing.JTextField();
    jLabel12 = new javax.swing.JLabel();
    tfDimensionDe = new javax.swing.JTextField();
    jLabel13 = new javax.swing.JLabel();
    tfDimensionTe = new javax.swing.JTextField();
    btnAddNewElastomerType = new javax.swing.JButton();

    setTitle("Kreator Karty kontroli dla łożysk elastomerowych");
    setIconImage(new javax.swing.ImageIcon(getClass().getResource("/product.png")).getImage());

    jTabbedPane1.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N

    jPanel3.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        jPanel3ComponentShown(evt);
      }
    });

    jLabel3.setText("Filtr");

    jLabel1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jLabel1.setText("Kontrakt:");

    tfContractNumber.setBackground(new java.awt.Color(224, 255, 255));
    tfContractNumber.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N
    tfContractNumber.setName("tfKontrakt"); // NOI18N
    tfContractNumber.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(java.awt.event.KeyEvent evt) {
        tfContractNumberKeyReleased(evt);
      }
    });

    jLabel2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jLabel2.setText("Obiekt:");

    tfObjectNumber.setBackground(new java.awt.Color(224, 255, 255));
    tfObjectNumber.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N
    tfObjectNumber.setName("tfObiekt"); // NOI18N
    tfObjectNumber.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(java.awt.event.KeyEvent evt) {
        tfObjectNumberKeyReleased(evt);
      }
    });

    tblKontrakty.setBorder(new javax.swing.border.MatteBorder(null));
    tblKontrakty.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
    tblKontrakty.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "Kontrakt", "Numer seryjny", "Symbol", "Typ", "Rodzaj", "Obiekt", "Pozycja", "Nośność pionowa", "Zakres przesuwów"
      }
    ));
    tblKontrakty.setRowHeight(20);
    tblKontrakty.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    tblKontrakty.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        tblKontraktyMousePressed(evt);
      }
    });
    jScrollPane1.setViewportView(tblKontrakty);

    jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    cbBearingKind.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
    cbBearingKind.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Jednokierunkowe", "Stałe", "Wielokierunkowe", "Wielokierunkowe oblachowane", "(!) TYP NIEZNANY (!)" }));

    jLabel8.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jLabel8.setText("Typ łożyska:");

    jLabel4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jLabel4.setText("Szerokość (A):");

    tfA.setBackground(new java.awt.Color(224, 255, 255));
    tfA.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N

    jLabel5.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jLabel5.setText("Wysokość (H):");

    tfH.setBackground(new java.awt.Color(224, 255, 255));
    tfH.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N

    jLabel6.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jLabel6.setText("Wymiar płyty dolnej:");

    tfBottomPlate.setBackground(new java.awt.Color(224, 255, 255));
    tfBottomPlate.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N

    jLabel9.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jLabel9.setText("Rodzaj łożyska:");

    cbBearingType.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
    cbBearingType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Elastomerowe", "Garnkowe", "Soczewkowe", "(!) TYP NIEZNANY (!)" }));

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel4)
              .addComponent(jLabel8))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(cbBearingType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jLabel9)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(tfA, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfH, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfBottomPlate, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
              .addComponent(cbBearingKind, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel8)
          .addComponent(cbBearingType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(cbBearingKind, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel9))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(tfA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel5)
          .addComponent(tfH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel6)
          .addComponent(tfBottomPlate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    jLabel7.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jLabel7.setText("Ścieżka zapisu Kart:");

    tfDestPath.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N

    btnGenerateCard.setBackground(new java.awt.Color(0, 153, 0));
    btnGenerateCard.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    btnGenerateCard.setText("GENERUJ KARTĘ");
    btnGenerateCard.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnGenerateCardActionPerformed(evt);
      }
    });

    btnChooseFolder.setBackground(new java.awt.Color(0, 153, 0));
    btnChooseFolder.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    btnChooseFolder.setText("Wybierz folder");
    btnChooseFolder.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnChooseFolderActionPerformed(evt);
      }
    });

    cbGenMesure.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    cbGenMesure.setText("Generuj pomiary");

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel7)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(tfDestPath, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnChooseFolder)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(cbGenMesure)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnGenerateCard)
        .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(btnGenerateCard)
          .addComponent(tfDestPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel7)
          .addComponent(btnChooseFolder)
          .addComponent(cbGenMesure))
        .addContainerGap())
    );

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane1)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel3)
              .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfContractNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfObjectNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGap(0, 699, Short.MAX_VALUE))
          .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel3)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(tfContractNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel2)
          .addComponent(tfObjectNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );

    jTabbedPane1.addTab("Kreator Kart Kontroli", jPanel3);

    jPanel4.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        jPanel4ComponentShown(evt);
      }
    });

    tblElastomerInsertDimension.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
    tblElastomerInsertDimension.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "TYP ŁOŻYSKA", "WYMIAR De [mm]", "WYMIAR Te [mm]"
      }
    ));
    tblElastomerInsertDimension.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    tblElastomerInsertDimension.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    tblElastomerInsertDimension.setShowGrid(true);
    tblElastomerInsertDimension.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        tblElastomerInsertDimensionMousePressed(evt);
      }
    });
    tblElastomerInsertDimension.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        tblElastomerInsertDimensionComponentShown(evt);
      }
    });
    jScrollPane2.setViewportView(tblElastomerInsertDimension);

    jLabel10.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jLabel10.setText("Wymiary wkładów elastomerowych");

    jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "NOWY TYP ŁOŻYSKA", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14))); // NOI18N

    jLabel11.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jLabel11.setText("TYP:");

    tfBearingType.setBackground(new java.awt.Color(224, 255, 255));
    tfBearingType.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N

    jLabel12.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jLabel12.setText("WYMIAR De [mm]:");

    tfDimensionDe.setBackground(new java.awt.Color(224, 255, 255));
    tfDimensionDe.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N

    jLabel13.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    jLabel13.setText("WYMIAR Te [mm]:");

    tfDimensionTe.setBackground(new java.awt.Color(224, 255, 255));
    tfDimensionTe.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N

    btnAddNewElastomerType.setBackground(new java.awt.Color(0, 153, 0));
    btnAddNewElastomerType.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    btnAddNewElastomerType.setText("DODAJ");
    btnAddNewElastomerType.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnAddNewElastomerTypeActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
      jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel5Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel11)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(tfBearingType, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(48, 48, 48)
        .addComponent(jLabel12)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(tfDimensionDe, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(55, 55, 55)
        .addComponent(jLabel13)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(tfDimensionTe, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 92, Short.MAX_VALUE)
        .addComponent(btnAddNewElastomerType, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    jPanel5Layout.setVerticalGroup(
      jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel11)
          .addComponent(tfBearingType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel12)
          .addComponent(tfDimensionDe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel13)
          .addComponent(tfDimensionTe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(btnAddNewElastomerType))
        .addContainerGap())
    );

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
            .addComponent(jLabel10)
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );
    jPanel4Layout.setVerticalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addGap(8, 8, 8)
        .addComponent(jLabel10)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );

    jTabbedPane1.addTab("Wymiary", jPanel4);

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
        .addComponent(jTabbedPane1)
        .addContainerGap())
    );

    pack();
    setLocationRelativeTo(null);
  }// </editor-fold>//GEN-END:initComponents

  private void tfContractNumberKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfContractNumberKeyReleased
    // TODO add your handling code here:
    KKCreatorManager.getInstance().filterTable(tfContractNumber.getText(), tfObjectNumber.getText(), tblKontrakty);

  }//GEN-LAST:event_tfContractNumberKeyReleased

  private void tfObjectNumberKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfObjectNumberKeyReleased
    // TODO add your handling code here:
    KKCreatorManager.getInstance().filterTable(tfContractNumber.getText(), tfObjectNumber.getText(), tblKontrakty);
  }//GEN-LAST:event_tfObjectNumberKeyReleased

  private void btnGenerateCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateCardActionPerformed

    try {
      if (tblKontrakty.getSelectedRow() != -1) {
        switch (cbBearingType.getSelectedIndex()) {
          /* Typ łożyska - ELASTOMEROWE */
          case 0:
            switch (cbBearingKind.getSelectedIndex()) {
              case 0: // Łożysko jednokierunkowe
                KKCreatorManager.getInstance().modifyKKexcelOneWayBaring(
                        tblKontrakty, tfA.getText(), tfH.getText(),
                        tfBottomPlate.getText(), tfDestPath.getText(),
                        cbGenMesure.isSelected());
                break;
              case 1: // Łożysko stałe
                KKCreatorManager.getInstance().modifyKKexcelConstantBearing(
                        tblKontrakty, tfA.getText(), tfH.getText(),
                        tfDestPath.getText(), cbGenMesure.isSelected());
                break;
              case 2: // Łożysko wielokierunkowe
                KKCreatorManager.getInstance().modifyKKexcelManyWayBearing(
                        tblKontrakty, tfDestPath.getText(), cbGenMesure.isSelected());
                break;
              case 3: // Łożysko wielokierunkowe oblachowane
                KKCreatorManager.getInstance().modifyKKexcelConstantBearing(
                        tblKontrakty, tfA.getText(), tfH.getText(),
                        tfDestPath.getText(), cbGenMesure.isSelected());
                break;
              case 4: // Nieznany rodzaj łożyska
                JOptionPane.showMessageDialog(null, "Nieznany rodzaj łożyska", "Nie można wygenerować karty", JOptionPane.ERROR_MESSAGE);
                break;
            }
            break;
          /* Typ łożyska - GARNKOWE */
          case 1:
            switch (cbBearingKind.getSelectedIndex()) {
              case 0: // Łożysko jednokierunkowe
                KKCreatorManager.getInstance().modifyKKExcelPotOneWayBearing(
                        tblKontrakty, tfDestPath.getText());
                break;
              case 1: // Łożysko stałe
                KKCreatorManager.getInstance().modifyKKExcelPotConstantBearing(
                        tblKontrakty, tfDestPath.getText());                
                break;
              case 2: // Łożysko wielokierunkowe
                KKCreatorManager.getInstance().modifyKKExcelPotManyWayBearing(
                        tblKontrakty, tfDestPath.getText());  
                break;
              case 3: // Łożysko wielokierunkowe oblachowane
                KKCreatorManager.getInstance().modifyKKExcelPotManyWayBearing(
                        tblKontrakty, tfDestPath.getText());  
                break;
              case 4: // Nieznany rodzaj łożyska
                JOptionPane.showMessageDialog(null, "Nieznany rodzaj łożyska", "Nie można wygenerować karty", JOptionPane.ERROR_MESSAGE);
                break;
            }
            break;
          /* Typ łożyska - SOCZEWKOWE */
          case 2:
            JOptionPane.showMessageDialog(null, "Nie obsługiwany typ łożyska");
            break;
        }

      } else {
        JOptionPane.showMessageDialog(null, "Zaznacz łożysko w tabeli", "Błąd", JOptionPane.ERROR_MESSAGE);
      }
    } catch (IOException ex) {
      Logger.getLogger(FrmKKCreator.class.getName()).log(Level.SEVERE, null, ex);
      JOptionPane.showMessageDialog(null, ex, "Błąd", JOptionPane.ERROR_MESSAGE);
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(null, "Wprowadź poprawne wymiary A, H, Płyty dolnej", "Błąd", JOptionPane.ERROR_MESSAGE);
    }
  }//GEN-LAST:event_btnGenerateCardActionPerformed

  private void tblKontraktyMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKontraktyMousePressed
    // TODO add your handling code here:
    System.out.println(cbBearingKind.getSelectedItem().toString());

    DefaultTableModel model = (DefaultTableModel) tblKontrakty.getModel();

    switch ((model.getValueAt(tblKontrakty.getSelectedRow(), 3)).toString()) {
      case "1":
        cbBearingType.setSelectedIndex(2);
        break;
      case "2":
        cbBearingType.setSelectedIndex(1);
        break;
      case "3":
        cbBearingType.setSelectedIndex(0);
        break;
      default:
        cbBearingType.setSelectedIndex(3);
    }

    switch ((model.getValueAt(tblKontrakty.getSelectedRow(), 4)).toString()) {
      case "Jednokierunkowe":
        cbBearingKind.setSelectedIndex(0);
        break;
      case "Stałe":
        cbBearingKind.setSelectedIndex(1);
        break;
      case "Wielokierunkowe":
        cbBearingKind.setSelectedIndex(2);
        break;
      case "Wielokierunkowe oblachowane":
        cbBearingKind.setSelectedIndex(3);
        break;
      default:
        cbBearingKind.setSelectedIndex(4);

    }

    System.out.println("Rodzaj: " + model.getValueAt(tblKontrakty.getSelectedRow(), 4));

  }//GEN-LAST:event_tblKontraktyMousePressed

  private void btnChooseFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseFolderActionPerformed
    // TODO add your handling code here:
    SettingsManager.getInstance().setupControlCardSavePath(tfDestPath);
  }//GEN-LAST:event_btnChooseFolderActionPerformed

  // Dodanie nowego typu elstomeru.
  private void btnAddNewElastomerTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNewElastomerTypeActionPerformed
    // TODO add your handling code here:

    float type;
    float deDim;
    float teDim;

    try {
      type = Float.valueOf(tfBearingType.getText());
      deDim = Float.valueOf(tfDimensionDe.getText());
      teDim = Float.valueOf(tfDimensionTe.getText());

      ElastomerTypeManager.getInstance().addNewElastomerType(
              type, deDim, teDim);

      ElastomerTypeManager.getInstance().refreshElastomerTypeTable(tblElastomerInsertDimension);
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(null, "Nipoprawne dane", "Błąd",
              JOptionPane.ERROR_MESSAGE);
    } catch (SQLException ex) {
      Logger.getLogger(FrmKKCreator.class.getName()).log(Level.SEVERE, null, ex);
      JOptionPane.showMessageDialog(null, "Błąd odczytu typów wkładów elastomerowych z bazy danych: " + ex, "Błąd",
              JOptionPane.ERROR_MESSAGE);
    }
  }//GEN-LAST:event_btnAddNewElastomerTypeActionPerformed

  // Odświeżenie typów wkładów elastomerowych.
  private void tblElastomerInsertDimensionComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tblElastomerInsertDimensionComponentShown

  }//GEN-LAST:event_tblElastomerInsertDimensionComponentShown

  private void jPanel4ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel4ComponentShown
    // TODO add your handling code here:
    try {
      // TODO add your handling code here:
      ElastomerTypeManager.getInstance().refreshElastomerTypeTable(tblElastomerInsertDimension);
    } catch (SQLException ex) {
      JOptionPane.showMessageDialog(null, "Błąd odczytu bazy danych" + ex, "Błąd", JOptionPane.ERROR_MESSAGE);
      Logger.getLogger(FrmKKCreator.class.getName()).log(Level.SEVERE, null, ex);
    }
  }//GEN-LAST:event_jPanel4ComponentShown

  private void jPanel3ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel3ComponentShown
    // TODO add your handling code here:
    try {
      // TODO add your handling code here:
      ElastomerTypeManager.getInstance().refreshElastomerTypeTable(tblElastomerInsertDimension);
    } catch (SQLException ex) {
      JOptionPane.showMessageDialog(null, "Błąd odczytu bazy danych" + ex, "Błąd", JOptionPane.ERROR_MESSAGE);
      Logger.getLogger(FrmKKCreator.class.getName()).log(Level.SEVERE, null, ex);
    }
  }//GEN-LAST:event_jPanel3ComponentShown

  private void tblElastomerInsertDimensionMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElastomerInsertDimensionMousePressed
    // TODO add your handling code here:
    if (evt.getButton() == 3) {
      popTblElastomerType.show(tblElastomerInsertDimension, evt.getX(), evt.getY());
    }
  }//GEN-LAST:event_tblElastomerInsertDimensionMousePressed

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
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(FrmKKCreator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(FrmKKCreator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(FrmKKCreator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(FrmKKCreator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new FrmKKCreator().setVisible(true);

      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnAddNewElastomerType;
  private javax.swing.JButton btnChooseFolder;
  private javax.swing.JButton btnGenerateCard;
  private javax.swing.JComboBox<String> cbBearingKind;
  private javax.swing.JComboBox<String> cbBearingType;
  private javax.swing.JCheckBox cbGenMesure;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel10;
  private javax.swing.JLabel jLabel11;
  private javax.swing.JLabel jLabel12;
  private javax.swing.JLabel jLabel13;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JLabel jLabel9;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jPanel5;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JTabbedPane jTabbedPane1;
  private javax.swing.JPopupMenu popTblElastomerType;
  private javax.swing.JTable tblElastomerInsertDimension;
  private javax.swing.JTable tblKontrakty;
  private javax.swing.JTextField tfA;
  private javax.swing.JTextField tfBearingType;
  private javax.swing.JTextField tfBottomPlate;
  private javax.swing.JTextField tfContractNumber;
  private javax.swing.JTextField tfDestPath;
  private javax.swing.JTextField tfDimensionDe;
  private javax.swing.JTextField tfDimensionTe;
  private javax.swing.JTextField tfH;
  private javax.swing.JTextField tfObjectNumber;
  // End of variables declaration//GEN-END:variables

  private void initPopups() {
    JMenuItem itmTblElastoerType = new JMenuItem("Usuń");

    itmTblElastoerType.addActionListener((ActionEvent e) -> {
      ElastomerTypeManager.getInstance().removeElastomerType(tblElastomerInsertDimension);
      try {
        ElastomerTypeManager.getInstance().refreshElastomerTypeTable(tblElastomerInsertDimension);
      } catch (SQLException ex) {
        Logger.getLogger(FrmKKCreator.class.getName()).log(Level.SEVERE, null, ex);
      }
    });

    popTblElastomerType.add(itmTblElastoerType);

    tblElastomerInsertDimension.add(popTblElastomerType);
  }
}
