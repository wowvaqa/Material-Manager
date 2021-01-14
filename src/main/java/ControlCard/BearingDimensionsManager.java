/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ControlCard;

import com.kprm.materialmanager.DatabaseManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Klasa zarządza wymiarami elementów składowych łożyska garnkowego. Dane do
 * generowania kart kontroli jakości dla dz. jakości.
 *
 * @author l.wawrzyniak
 */
public class BearingDimensionsManager {

    private BearingDimensionsManager() {
    }

    public static BearingDimensionsManager getInstance() {
        return BearingDimensionsManagerHolder.INSTANCE;
    }

    private static class BearingDimensionsManagerHolder {

        private static final BearingDimensionsManager INSTANCE = new BearingDimensionsManager();
    }

    /**
     * Dodaje do tabeli nowy zakres wymiarów dla zadanego typu łożyska
     *
     * @param bearingType Typ łożyska (PS 01.0, PF 03.0 itp.)
     * @param ht Wymiar Ht
     * @param g1 Wymiar G1
     * @param g2 Wymair G2
     * @param l Wymiar L
     * @param h Wymiar H
     */
    public void addBearingDimensions(String bearingType, String ht, String g1,
            String g2, String l, String h) {
        try {
            DatabaseManager.getInstance().addBearingDimensions(bearingType,
                    Integer.parseInt(ht), Integer.parseInt(g1), Integer.parseInt(g2),
                    Integer.parseInt(l), Integer.parseInt(h));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Nieoprawne dane");
        }
    }

    /**
     * Odświeża zawartość tabli wymiarów elementów łożysk
     *
     * @param tblBearingElements Tabela wymiarów elementów łożysk
     * @throws java.sql.SQLException
     */
    public void refreshBearingDimensionsTable(JTable tblBearingElements) throws SQLException {
        DefaultTableModel model = (DefaultTableModel) tblBearingElements.getModel();
        model.setRowCount(0);

        // Dane wiersza w tabeli
        Object rowData[] = new Object[6];

        ResultSet resultSet = DatabaseManager.getInstance().getBearingDimensions();

        if (resultSet != null) {
            resultSet.first();
            do {
                rowData[0] = resultSet.getString("bearing_type");
                rowData[1] = resultSet.getString("Ht");
                rowData[2] = resultSet.getString("G1");
                rowData[3] = resultSet.getString("G2");
                rowData[4] = resultSet.getString("L");
                rowData[5] = resultSet.getString("H");
                model.addRow(rowData);

            } while (resultSet.next());
        }
    }
}
