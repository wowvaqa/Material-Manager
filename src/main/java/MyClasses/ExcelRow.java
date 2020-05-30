/*
 * Wszelkie prawa zastrzeżone
 */
package MyClasses;

/**
 * Klasa przechowuje dane z jednego wiersza Excel - rejestr łożysk.
 *
 * @author l.wawrzyniak
 */
public class ExcelRow {
  // Numer kontraktu

  private String contract;
  // Obiekt
  private String contractObject;
  // Podpora łożyska
  private String objectPillar;
  // Symbol łożyska (np. ESO-EG0-057-S00-A00)
  private String bearingSymbol;
  // Numer seryjny łożyska
  private String serialNumber;
  // Typ łożyska
  private String bearingType;
  // Rodzaj łożyska
  private String bearingKind;
  // Nośność łożyska
  private String bearingCapacity;
  // Przesów łożyska
  private String bearingPass;

  /**
   * Tworzy instancje wiersza z akusza Excela
   *
   * @param contract Numer kontrakut
   * @param contractObject Obiekt kontraktu
   * @param bearingSymbol Symbol łożyska
   */
  public ExcelRow(String contract, String contractObject, String bearingSymbol) {
    this.contract = contract;
    this.contractObject = contractObject;
    this.bearingSymbol = bearingSymbol;
  }

  public ExcelRow(String contract, String contractObject, String objectPillar,
          String bearingSymbol, String serialNumber, String bearingType,
          String bearingKind) {
    this.contract = contract;
    this.contractObject = contractObject;
    this.objectPillar = objectPillar;
    this.bearingSymbol = bearingSymbol;
    this.serialNumber = serialNumber;
    this.bearingType = bearingType;
    this.bearingKind = bearingKind;
  }

  /**
   * 
   * @param valueContract
   * @param valueObject
   * @param valuePillar
   * @param valueSymbol
   * @param valueSerialNumber
   * @param valueBearingType
   * @param valueBearingKind
   * @param valueCapacity
   * @param valuePass 
   */
  public ExcelRow(String valueContract, String valueObject, String valuePillar, 
          String valueSymbol, String valueSerialNumber, String valueBearingType, 
          String valueBearingKind, String valueCapacity, String valuePass) {
    
    this.contract = valueContract;
    this.contractObject = valueObject;
    this.objectPillar = valuePillar;
    this.bearingSymbol = valueSymbol;
    this.serialNumber = valueSerialNumber;
    this.bearingType = valueBearingType;
    this.bearingKind = valueBearingKind;
    this.bearingCapacity = valueCapacity;
    this.bearingPass = valuePass;
  }

  /**
   * Zwraca numer kontraktu
   *
   * @return Numer kontraktu
   */
  public String getContract() {
    return contract;
  }

  /**
   * Zapisuje numer kontraktu
   *
   * @param contract Numer kontraktu
   */
  public void setContract(String contract) {
    this.contract = contract;
  }

  /**
   * Zwraca obiekt kotraktu.
   *
   * @return Obiekt kontraktu
   */
  public String getContractObject() {
    return contractObject;
  }

  /**
   * '
   * Ustala obiekt kontraktu
   *
   * @param contractObject Obiekt kontraktu
   */
  public void setContractObject(String contractObject) {
    this.contractObject = contractObject;
  }

  /**
   * Zwraca symbol łożyska
   *
   * @return Symbol łożyska
   */
  public String getBearingSymbol() {
    return bearingSymbol;
  }

  /**
   * Zapisuje symbol łożyska
   *
   * @param bearingSymbol symbol łożyska
   */
  public void setBearingSymbol(String bearingSymbol) {
    this.bearingSymbol = bearingSymbol;
  }

  /**
   * Zwraca podporę łożyska
   *
   * @return Podpora łożyska
   */
  public String getObjectPillar() {
    return objectPillar;
  }

  /**
   * Zapisuje podporę łożyska
   *
   * @param pillar Podpora łożyska.
   */
  public void setObjectPillar(String pillar) {
    this.objectPillar = pillar;
  }

  public String getSerialNumber() {
    return serialNumber;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public String getBearingType() {
    return bearingType;
  }

  public void setBearingType(String bearingType) {
    this.bearingType = bearingType;
  }

  public String getBearingKind() {
    return bearingKind;
  }

  public void setBearingKind(String bearingKind) {
    this.bearingKind = bearingKind;
  }

  public String getBearingCapacity() {
    return bearingCapacity;
  }

  public void setBearingCapacity(String bearingCapacity) {
    this.bearingCapacity = bearingCapacity;
  }

  public String getBearingPass() {
    return bearingPass;
  }

  public void setBearingPass(String bearingPass) {
    this.bearingPass = bearingPass;
  }
  
  
}
