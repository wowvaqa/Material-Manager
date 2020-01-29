/*
 *
 */
package MyClasses;

import com.kprm.materialmanager.AtestManager;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;

/**
 * Klasa służy do wykrywania kliknięcia w nagłówek kolumny w tabeli.
 *
 * @author Łukasz Wawrzyniak
 */
public class TableAtestyHeaderMouseListener extends MouseAdapter {

    private JTable _table;

    public TableAtestyHeaderMouseListener(JTable table) {
        this._table = table;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        Point point = e.getPoint();
        int column = _table.columnAtPoint(point);

        switch (column) {
            case 0:
                /* Nazwa materiału */
                if (!MmComparators.atestNazwa) {
                    AtestManager.getInstance().sortCerts(_table, SortModes.ATEST_NAZWA_UP);
                    MmComparators.atestNazwa = true;
                } else {
                    AtestManager.getInstance().sortCerts(_table, SortModes.ATEST_NAZWA_DOWN);
                    MmComparators.atestNazwa = false;
                }
                break;
            case 1:
                /* Numer zamówienia */
                if (!MmComparators.atestNrZamowienia) {
                    AtestManager.getInstance().sortCerts(_table, SortModes.ATEST_NRZAM_UP);
                    MmComparators.atestNrZamowienia = true;
                } else {
                    AtestManager.getInstance().sortCerts(_table, SortModes.ATEST_NRZAM_DOWN);
                    MmComparators.atestNrZamowienia = false;
                }
                break;

            case 2:
                /* Numer WZ */
                if (!MmComparators.atestWZ) {
                    AtestManager.getInstance().sortCerts(_table, SortModes.ATEST_WZ_UP);
                    MmComparators.atestWZ = true;
                } else {
                    AtestManager.getInstance().sortCerts(_table, SortModes.ATEST_WZ_DOWN);
                    MmComparators.atestWZ = false;
                }
                break;
            case 3:
                /* Data dodania */
                if (!MmComparators.atestData) {
                    AtestManager.getInstance().sortCerts(_table, SortModes.ATEST_DATA_UP);
                    MmComparators.atestData = true;
                } else {
                    AtestManager.getInstance().sortCerts(_table, SortModes.ATEST_DATA_DOWN);
                    MmComparators.atestData = false;
                }
                break;
            case 4:
                /* Numer ZP */
                if (!MmComparators.atestZP) {
                    AtestManager.getInstance().sortCerts(_table, SortModes.ATEST_ZP_UP);
                    MmComparators.atestZP = true;
                } else {
                    AtestManager.getInstance().sortCerts(_table, SortModes.ATEST_ZP_DOWN);
                    MmComparators.atestZP = false;
                }
                break;
            case 5:
                /* Dostawca */
                if (!MmComparators.atestDostawca) {
                    AtestManager.getInstance().sortCerts(_table, SortModes.ATEST_DOSTAWCA_UP);
                    MmComparators.atestDostawca = true;
                } else {
                    AtestManager.getInstance().sortCerts(_table, SortModes.ATEST_DOSTAWCA_DOWN);
                    MmComparators.atestDostawca = false;
                }
                break;    
            case 6:
                /* Dostawca */
                if (!MmComparators.atestPKD) {
                    AtestManager.getInstance().sortCerts(_table, SortModes.ATEST_PKD_UP);
                    MmComparators.atestPKD = true;
                } else {
                    AtestManager.getInstance().sortCerts(_table, SortModes.ATEST_PKD_DOWN);
                    MmComparators.atestPKD = false;
                }
                break;    
             
        }
    }
}
