/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyClasses;

import java.util.Comparator;

/**
 * Porównywarki do sortowania.
 *
 * @author Łukasz Wawrzyniak.
 */
public class MmComparators {

    public static boolean atestNazwa;
    public static boolean atestData;
    public static boolean atestNrZamowienia;
    public static boolean atestWZ;
    public static boolean atestZP;
    public static boolean atestDostawca;
    public static boolean atestPKD;
    public static boolean atestNazwaMaterialu;

    /**
     * Sortowanie materiałów.
     */
    public static Comparator<Material> atestNameComparator = new Comparator<Material>() {
        @Override
        public int compare(Material o1, Material o2) {
            return (int) (o2.getName().compareTo(o1.getName()));
        }
    };

    /**
     * Sortowanie atestów wg nazwy, tryb 1.
     */
    public static Comparator<Atest> atestNameUpComparator = new Comparator<Atest>() {
        @Override
        public int compare(Atest o1, Atest o2) {
            return (int) (o2.getNazwa().compareTo(o1.getNazwa()));
        }
    };

    /**
     * Sortowanie atestów wg nazwy, tryb 2.
     */
    public static Comparator<Atest> atestNameDownComparator = new Comparator<Atest>() {
        @Override
        public int compare(Atest o1, Atest o2) {
            return (int) (o1.getNazwa().compareTo(o2.getNazwa()));
        }
    };

    /**
     * Sortowanie atestów wg daty, tryb 1.
     */
    public static Comparator<Atest> atestDateUpComparator = new Comparator<Atest>() {
        @Override
        public int compare(Atest o1, Atest o2) {
            return (int) (o2.getDate().compareTo(o1.getDate()));
        }
    };

    /**
     * Sortowanie atestów wg daty, tryb 2.
     */
    public static Comparator<Atest> atestDateDownComparator = new Comparator<Atest>() {
        @Override
        public int compare(Atest o1, Atest o2) {
            return (int) (o1.getDate().compareTo(o2.getDate()));
        }
    };

    public static Comparator<Atest> atestWzUpComparator = new Comparator<Atest>() {
        @Override
        public int compare(Atest o1, Atest o2) {
            return (int) (o1.getWz().compareTo(o2.getWz()));
        }
    };

    public static Comparator<Atest> atestWzDownComparator = new Comparator<Atest>() {
        @Override
        public int compare(Atest o1, Atest o2) {
            return (int) (o2.getWz().compareTo(o1.getWz()));
        }
    };

    public static Comparator<Atest> atestZpUpComparator = new Comparator<Atest>() {
        @Override
        public int compare(Atest o1, Atest o2) {
            return (int) (o1.getZp().compareTo(o2.getZp()));
        }
    };

    public static Comparator<Atest> atestZpDownComparator = new Comparator<Atest>() {
        @Override
        public int compare(Atest o1, Atest o2) {
            return (int) (o2.getZp().compareTo(o1.getZp()));
        }
    };

    public static Comparator<Atest> atestNrZamowieniaUpComparator = new Comparator<Atest>() {
        @Override
        public int compare(Atest o1, Atest o2) {
            return (int) (o1.getNr_zamowienia().compareTo(o2.getNr_zamowienia()));
        }
    };

    public static Comparator<Atest> atestNrZamowieniaDownComparator = new Comparator<Atest>() {
        @Override
        public int compare(Atest o1, Atest o2) {
            return (int) (o2.getNr_zamowienia().compareTo(o1.getNr_zamowienia()));
        }
    };

    public static Comparator<Atest> atestDostawcaUpComparator = new Comparator<Atest>() {
        @Override
        public int compare(Atest o1, Atest o2) {
            return (int) (o1.getDostawca().compareTo(o2.getDostawca()));
        }
    };

    public static Comparator<Atest> atestDostawcaDownComparator = new Comparator<Atest>() {
        @Override
        public int compare(Atest o1, Atest o2) {
            return (int) (o2.getDostawca().compareTo(o1.getDostawca()));
        }
    };

    public static Comparator<Atest> atestPkdUpComparator = new Comparator<Atest>() {
        @Override
        public int compare(Atest o1, Atest o2) {
            return (int) (o1.getPkd().compareTo(o2.getPkd()));
        }
    };

    public static Comparator<Atest> atestPkdDownComparator = new Comparator<Atest>() {
        @Override
        public int compare(Atest o1, Atest o2) {
            return (int) (o2.getPkd().compareTo(o1.getPkd()));
        }
    };

    public static Comparator<Atest> atestMaterialNameUpComparator = new Comparator<Atest>() {
        @Override
        public int compare(Atest o1, Atest o2) {
            return (int) (o1.getNazwaMaterialu().compareTo(o2.getNazwaMaterialu()));
        }
    };

    public static Comparator<Atest> atestMaterialNameDownComparator = new Comparator<Atest>() {
        @Override
        public int compare(Atest o1, Atest o2) {
            return (int) (o2.getNazwaMaterialu().compareTo(o1.getNazwaMaterialu()));
        }
    };

    public static Comparator<MmMutableTreeNode> treeNodeComparator = new Comparator<MmMutableTreeNode>() {
        @Override
        public int compare(MmMutableTreeNode o1, MmMutableTreeNode o2) {
            return (int) (o2.getNazwa().compareToIgnoreCase(o1.getNazwa()));
        }
    };
}
