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
    private String contract;
    // Obiekt
    private String contractObject;    
    // Symbol łożyska (np. ESO-EG0-057-S00-A00)
    private String bearingSymbol;
    // Podpora łożyska
    private String pillar;

    /**
     * Tworzy instancje wiersza z akusza Excela
     * @param contract Numer kontrakut
     * @param contractObject Obiekt kontraktu
     * @param bearingSymbol Symbol łożyska
     */
    public ExcelRow(String contract, String contractObject, String bearingSymbol) {
        this.contract = contract;
        this.contractObject = contractObject;
        this.bearingSymbol = bearingSymbol;
    }   

    /**
     * Zwraca numer kontraktu
     * @return Numer kontraktu
     */
    public String getContract() {
        return contract;
    }

    /**
     * Zapisuje numer kontraktu
     * @param contract Numer kontraktu
     */
    public void setContract(String contract) {
        this.contract = contract;
    }
    
    /**
     * Zwraca obiekt kotraktu.
     * @return Obiekt kontraktu
     */
    public String getContractObject() {
        return contractObject;
    }

    /**'
     * Ustala obiekt kontraktu
     * @param contractObject Obiekt kontraktu
     */
    public void setContractObject(String contractObject) {
        this.contractObject = contractObject;
    }

    /**
     * Zwraca symbol łożyska
     * @return Symbol łożyska
     */
    public String getBearingSymbol() {
        return bearingSymbol;
    }

    /**
     * Zapisuje symbol łożyska
     * @param bearingSymbol symbol łożyska
     */
    public void setBearingSymbol(String bearingSymbol) {
        this.bearingSymbol = bearingSymbol;
    }

    /**
     * Zwraca podporę łożyska
     * @return Podpora łożyska
     */
    public String getPillar() {
        return pillar;
    }

    /**
     * Zapisuje podporę łożyska
     * @param pillar Podpora łożyska.
     */
    public void setPillar(String pillar) {
        this.pillar = pillar;
    }
}
