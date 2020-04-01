/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Frames;

import MyClasses.TableAtestyHeaderMouseListener;
import MyClasses.TableOutputHeaderMouseListener;
import com.kprm.materialmanager.AtestIndicatorManager;
import com.kprm.materialmanager.BomManager;
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.JTableHeader;

/**
 *
 * @author wakan
 */
public class FrmAtestIndicator extends javax.swing.JFrame {

    /**
     * Tryb pracy: 0 - przyporządkowanie atestu z poziomu materiału w głównym
     * oknie 1 - przyporządkowanie atestu do wielu materiałów z poziomu
     * CertToMany
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
        createTableHeadersClickListeners();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOutput = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tfAtest = new javax.swing.JTextField();
        tfMaterial = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        setTitle("Przyporządkuj atest");
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/product.png")).getImage());

        tblOutput.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nazwa", "Materiał", "Nr WZ", "Nr Zamówienia", "Data dodania"
            }
        ));
        jScrollPane1.setViewportView(tblOutput);

        jButton1.setText("Wybierz atest");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Szukaj"));

        jLabel1.setText("Nazwa atestu:");

        tfAtest.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfAtestKeyReleased(evt);
            }
        });

        tfMaterial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfMaterialKeyReleased(evt);
            }
        });

        jLabel2.setText("Materiał:");

        jButton2.setText("szukaj");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfAtest, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tfMaterial, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addContainerGap(234, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfAtest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfMaterial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 844, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Tworzy nasłuch w którym program wykrywa kliknięcie w nagłówku tabeli
     * wyników poszukiwania atestów.
     */
    private void createTableHeadersClickListeners(){
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
        super.show();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblOutput;
    private javax.swing.JTextField tfAtest;
    private javax.swing.JTextField tfMaterial;
    // End of variables declaration//GEN-END:variables
}
