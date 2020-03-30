/*
 * Wszelkie prawa zastrzeżone
 */
package MyClasses;

/**
 * Klasa przechowuje dane z jednego wiersza Excel - rejestr łożysk.
 * @author l.wawrzyniak
 */
public class ExcelRow {
    // Numer kontraktu
    private String Contract;    

    /**
     * 
     * @return Numer kontraktu
     */
    public String getContract() {
        return Contract;
    }

    /**
     * Ustawienie numeru kontraktu
     * @param Contract 
     */
    public void setContract(String Contract) {
        this.Contract = Contract;
    }
}
