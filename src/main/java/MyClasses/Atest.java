/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyClasses;

/**
 * 
 * @author Łukasz Wawrzyniak
 */
public class Atest {
    
    // Nazwa atestu
    private String nazwa;
    // Id atestu w bazie danych
    private int id;
    // Id materiału z bazy danych
    private int idMaterialu;
    // Pozycja w tabeli.
    private int positionInTable;
    // Ścieżka do pliku atestu.
    private String sciezka;
    // Id w tabeli atest_Bom
    private int id_atestBom;
    // Numer Zp atestu
    private String zp;
    // Dostawca atestu
    private String dostawca;
    // Numer wz
    private String wz;
    // PKD atestu
    private String pkd;
    // Numer zamówienie
    private String nr_zamowienia;
    // Data dodania
    private String date;
    // Nazwa materiału
    private String nazwaMaterialu;
    // Infromacja czy atest nie zawiera braków, 1 - tak, 0 - nie;
    private boolean redStatus;
            

    /**
     * @return Zwraca nazwę atestu.
     */
    public String getNazwa() {
        return nazwa;
    }

    /**
     * Ustala nazwę atestu.
     * @param nazwa 
     */
    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    /**
     * @return Zwraca Id materiału
     */
    public int getId() {
        return id;
    }

    /**
     * Ustala ID atestu
     * @param id 
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return Zwraca ID materiału którego atest dotyczy.
     */
    public int getIdMaterialu() {
        return idMaterialu;
    }

    /**
     * Ustala ID materiału którego atest dotyczy.
     * @param idMaterialu 
     */
    public void setIdMaterialu(int idMaterialu) {
        this.idMaterialu = idMaterialu;
    }

    /**
     * @return Zwraca pozycję atestu w tabeli w której jest wyświetlony.
     */
    public int getPositionInTable() {
        return positionInTable;
    }

    /**
     * Pozycja w tabeli wynikowej
     * @param positionInTable 
     */
    public void setPositionInTable(int positionInTable) {
        this.positionInTable = positionInTable;
    }

    /**
     * @return Zwraca ścieżkę do pliku z certyfikatem atestu.
     */
    public String getSciezka() {
        return sciezka;
    }

    /**
     * Ustala ścieżkę do pliku z certyfiaktem atestu.
     * @param sciezka 
     */
    public void setSciezka(String sciezka) {
        this.sciezka = sciezka;
    }

    /**
     * @return Zwraca Id boma którego dotyczy atest.
     */
    public int getId_atestBom() {
        return id_atestBom;
    }

    /**
     * Ustala numer ID boma którego dotyczy atest.
     * @param id_atestBom 
     */
    public void setId_atestBom(int id_atestBom) {
        this.id_atestBom = id_atestBom;
    }

    /**
     * @return Zwraca numer ZP materiału z atestu.
     */
    public String getZp() {
        return zp;
    }

    /**
     * Ustala numer ZP materiału z atestu.
     * @param zp 
     */
    public void setZp(String zp) {
        this.zp = zp;
    }

    /**
     * @return Zwraca dostawcę materiału z atestu.
     */
    public String getDostawca() {
        return dostawca;
    }

    /**
     * Ustala dostawcę materiału z atestu.
     * @param dostawca Dostawca
     */
    public void setDostawca(String dostawca) {
        this.dostawca = dostawca;
    }

    /**
     * @return Zwraca numer PKD materiału z atestu
     */
    public String getPkd() {
        return pkd;
    }

    /**
     * Ustala numer pkd materiału z atestu
     * @param pkd 
     */
    public void setPkd(String pkd) {
        this.pkd = pkd;
    }

    /**
     * @return Zwraca czy z materiałem jest coś nie tak (TRUE jeżeli brakuje 
     * np atestu).
     */
    public boolean isRedStatus() {
        return redStatus;
    }

    /**
     * Ustala status materiału, jeżeli True wtedy w tabelach będzie wyróżniony
     * kolorem czerwonym.
     * @param redStatus 
     */
    public void setRedStatus(boolean redStatus) {
        this.redStatus = redStatus;
    }

    /**
     * @return Zwraca numer WZ na którym przyjechał materiał podpięty pod atest.
     */
    public String getWz() {
        return wz;
    }

    /**
     * Ustala numer wz na której przyjechła materiał podpięty pod atest.
     * @param wz Numer WZ
     */
    public void setWz(String wz) {
        this.wz = wz;
    }

    /**
     * @return Zwraca numer zamówienia atestu
     */
    public String getNr_zamowienia() {
        return nr_zamowienia;
    }

    /**
     * Ustala numer zamówienia którego dotyczy atest.
     * @param nr_zamowienia 
     */
    public void setNr_zamowienia(String nr_zamowienia) {
        this.nr_zamowienia = nr_zamowienia;
    }

    /**
     * @return Zwraca datę dodania atestu.
     */
    public String getDate() {
        return date;
    }

    /**
     * Ustala datę dodania atestu.
     * @param date Data
     */
    public void setDate(String date) {
        this.date = date;
    }
    /** 
     * @return Zwraca nazwę materiału którego dotyczy atest.
     */
    public String getNazwaMaterialu() {
        return nazwaMaterialu;
    }

    /**
     * Ustala nazwę materiału którego dotyczy atest.
     * @param nazwaMaterialu Nazwa materiału.
     */
    public void setNazwaMaterialu(String nazwaMaterialu) {
        this.nazwaMaterialu = nazwaMaterialu;
    }
    
}
