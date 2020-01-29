package MyClasses;

/**
 * Klasa zarządza numeracją PKD
 * @author Wow Vaqa
 */
public class PkdNumber {
    
    /* Aktualny numer PKD */
    private int actualPkdNumber;
    /* Aktualny numer miesiąca dla PKD */
    private int actualPkdMonthNumber;
    /* Aktualny numer roku dla PKD */
    private int actualPkdYearNumber;

    /**
     * Zwraca aktualny numer PKD
     * @return numer PKD
     */
    public int getActualPkdNumber() {
        return actualPkdNumber;
    }

    /**
     * Ustala numer PKD
     * @param actualPkdNumber numer pkd
     */
    public void setActualPkdNumber(int actualPkdNumber) {
        this.actualPkdNumber = actualPkdNumber;
    }

    /**
     * Zwraca numer miesiąca z PKD
     * @return numer miesiąca
     */
    public int getActualPkdMonthNumber() {
        return actualPkdMonthNumber;
    }

    /**
     * Ustala numer miesiąca w PKD
     * @param actualPkdMonthNumber numer miesiąca
     */
    public void setActualPkdMonthNumber(int actualPkdMonthNumber) {
        this.actualPkdMonthNumber = actualPkdMonthNumber;
    }

    /**
     * Zwraca numer roku z PKD
     * @return numer roku
     */
    public int getActualPkdYearNumber() {
        return actualPkdYearNumber;
    }

    /**
     * Ustala numer roku w PKD
     * @param actualPkdYearNumber numer roku.
     */
    public void setActualPkdYearNumber(int actualPkdYearNumber) {
        this.actualPkdYearNumber = actualPkdYearNumber;
    }
}
