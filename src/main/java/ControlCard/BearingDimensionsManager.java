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

        int intHt, intG1, intG2, intL, intH;

        try {
            intHt = Integer.parseInt(ht);
        } catch (NumberFormatException ex) {
            intHt = -1;
        }

        try {
            intG1 = Integer.parseInt(g1);
        } catch (NumberFormatException ex) {
            intG1 = -1;
        }

        try {
            intG2 = Integer.parseInt(g2);
        } catch (NumberFormatException ex) {
            intG2 = -1;
        }

        try {
            intL = Integer.parseInt(l);
        } catch (NumberFormatException ex) {
            intL = -1;
        }

        try {
            intH = Integer.parseInt(h);
        } catch (NumberFormatException ex) {
            intH = -1;
        }

        DatabaseManager.getInstance().addBearingDimensions(bearingType,
                intHt, intG1, intG2, intL, intH);
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

                if ("-1".equals(resultSet.getString("Ht"))) {
                    rowData[1] = "";
                } else {
                    rowData[1] = resultSet.getString("Ht");
                }

                if ("-1".equals(resultSet.getString("G1"))) {
                    rowData[2] = "";
                } else {
                    rowData[2] = resultSet.getString("G1");
                }

                if ("-1".equals(resultSet.getString("G2"))) {
                    rowData[3] = "";
                } else {
                    rowData[3] = resultSet.getString("G2");
                }

                if ("-1".equals(resultSet.getString("L"))) {
                    rowData[4] = "";
                } else {
                    rowData[4] = resultSet.getString("L");
                }

                if ("-1".equals(resultSet.getString("H"))) {
                    rowData[5] = "";
                } else {
                    rowData[5] = resultSet.getString("H");
                }

                model.addRow(rowData);

            } while (resultSet.next());
        }
    }

    /**
     * Zwraca wymiar Ht łożyska
     *
     * @param bearingType Nazwa łożyska (PS 01.0-S00-A00)
     * @return Wymiar Ht łożyska
     * @throws java.sql.SQLException
     */
    public String getDimension_Ht(String bearingType) throws SQLException {
        ResultSet resultSet = DatabaseManager.getInstance().getBearingDimensions();

        if (null != resultSet) {
            resultSet.first();
            do {
                if (resultSet.getString("bearing_type").equals(bearingType)) {
                    return resultSet.getString("Ht");
                }
            } while (resultSet.next());
        }
        return null;
    }
    
    /**
     * Zwraca wymiar Ht łożyska
     *
     * @param bearingType Nazwa łożyska (PS 01.0-S00-A00)
     * @return Wymiar Ht łożyska
     * @throws java.sql.SQLException
     */
    public String getDimension_G1(String bearingType) throws SQLException {
        ResultSet resultSet = DatabaseManager.getInstance().getBearingDimensions();

        if (null != resultSet) {
            resultSet.first();
            do {
                if (resultSet.getString("bearing_type").equals(bearingType)) {
                    return resultSet.getString("G1");
                }
            } while (resultSet.next());
        }
        return null;
    }
    
    /**
     * Zwraca wymiar Ht łożyska
     *
     * @param bearingType Nazwa łożyska (PS 01.0-S00-A00)
     * @return Wymiar Ht łożyska
     * @throws java.sql.SQLException
     */
    public String getDimension_G2(String bearingType) throws SQLException {
        ResultSet resultSet = DatabaseManager.getInstance().getBearingDimensions();

        if (null != resultSet) {
            resultSet.first();
            do {
                if (resultSet.getString("bearing_type").equals(bearingType)) {
                    return resultSet.getString("G2");
                }
            } while (resultSet.next());
        }
        return null;
    }
    
    /**
     * Zwraca wymiar Ht łożyska
     *
     * @param bearingType Nazwa łożyska (PS 01.0-S00-A00)
     * @return Wymiar Ht łożyska
     * @throws java.sql.SQLException
     */
    public String getDimension_L(String bearingType) throws SQLException {
        ResultSet resultSet = DatabaseManager.getInstance().getBearingDimensions();

        if (null != resultSet) {
            resultSet.first();
            do {
                if (resultSet.getString("bearing_type").equals(bearingType)) {
                    return resultSet.getString("L");
                }
            } while (resultSet.next());
        }
        return null;
    }
    
    /**
     * Zwraca wymiar Ht łożyska
     *
     * @param bearingType Nazwa łożyska (PS 01.0-S00-A00)
     * @return Wymiar Ht łożyska
     * @throws java.sql.SQLException
     */
    public String getDimension_H(String bearingType) throws SQLException {
        ResultSet resultSet = DatabaseManager.getInstance().getBearingDimensions();

        if (null != resultSet) {
            resultSet.first();
            do {
                if (resultSet.getString("bearing_type").equals(bearingType)) {
                    return resultSet.getString("H");
                }
            } while (resultSet.next());
        }
        return null;
    }

    /**
     * Usuwa z tabeli wymirów elementów łożysk garnkowych zaznaczony zakres
     * wymiarów
     *
     * @param tblBearingElements Tabela zawierająca wymiary elementów łożysk
     */
    public void removeBearingElementsDimensions(JTable tblBearingElements) {
        try {
            String bearingType = (String) tblBearingElements.getValueAt(tblBearingElements.getSelectedRow(), 0);
            DatabaseManager.getInstance().removeBearingElemetsDimensions(bearingType);
        } catch (IndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(null, "Zaznacz typ do usunięcia", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
}
