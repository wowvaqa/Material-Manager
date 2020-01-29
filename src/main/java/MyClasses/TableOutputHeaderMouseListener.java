/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyClasses;

import com.kprm.materialmanager.AtestIndicatorManager;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;

/**
 *
 * @author Łukasz Wawrzyniak
 */
public class TableOutputHeaderMouseListener extends MouseAdapter {

    private JTable _table;

    public TableOutputHeaderMouseListener(JTable table) {
        _table = table;
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
                    AtestIndicatorManager.getInstance().sortCerts(_table, SortModes.ATEST_NAZWA_UP);
                    MmComparators.atestNazwa = true;
                } else {
                    AtestIndicatorManager.getInstance().sortCerts(_table, SortModes.ATEST_NAZWA_DOWN);
                    MmComparators.atestNazwa = false;
                }
                break;

            case 1:
                /* Nazwa materiału */
                if (!MmComparators.atestNazwaMaterialu) {
                    AtestIndicatorManager.getInstance().sortCerts(_table, SortModes.ATEST_NAZWA_MATERIALU_UP);
                    MmComparators.atestNazwaMaterialu = true;
                } else {
                    AtestIndicatorManager.getInstance().sortCerts(_table, SortModes.ATEST_NAZWA_MATERIALU_DOWN);
                    MmComparators.atestNazwaMaterialu = false;
                }
                break;

            case 2:
                /* Numer WZ */
                if (!MmComparators.atestWZ) {
                    AtestIndicatorManager.getInstance().sortCerts(_table, SortModes.ATEST_WZ_UP);
                    MmComparators.atestWZ = true;
                } else {
                    AtestIndicatorManager.getInstance().sortCerts(_table, SortModes.ATEST_WZ_DOWN);
                    MmComparators.atestWZ = false;
                }
                break;

            case 3:
                /* Numer zamówienia */
                if (!MmComparators.atestNrZamowienia) {
                    AtestIndicatorManager.getInstance().sortCerts(_table, SortModes.ATEST_NRZAM_UP);
                    MmComparators.atestNrZamowienia = true;
                } else {
                    AtestIndicatorManager.getInstance().sortCerts(_table, SortModes.ATEST_NRZAM_DOWN);
                    MmComparators.atestNrZamowienia = false;
                }
                break;

            case 4:
                /* Data dodania */
                if (!MmComparators.atestData) {
                    AtestIndicatorManager.getInstance().sortCerts(_table, SortModes.ATEST_DATA_UP);
                    MmComparators.atestData = true;
                } else {
                    AtestIndicatorManager.getInstance().sortCerts(_table, SortModes.ATEST_DATA_DOWN);
                    MmComparators.atestData = false;
                }
                break;

        }
    }
}
