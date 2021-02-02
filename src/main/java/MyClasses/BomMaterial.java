package MyClasses;

import java.util.ArrayList;

/**
 * 
 * @author Łuksz Wawrzyniak
 */
public class BomMaterial {
    
    private String nazwaMaterialu;
    // Pozycja w tabeli
    private int tabelaPozycja;
    // Id materiału w bazie danych.
    private int id;
    // Id węzła do którego przynależy BOM
    private int id_wezla;
    // Przechowuje listę atestów.
    private ArrayList<Atest> listOfCerts = new ArrayList<>();

    /**
     * Zwraca nazwę materiału
     * @return Nazwa materiału.
     */
    public String getNazwaMaterialu() {
        return nazwaMaterialu;
    }

    /**
     * Ustala nazwę matriału
     * @param nazwaMaterialu Nazwa materiału
     */
    public void setNazwaMaterialu(String nazwaMaterialu) {
        this.nazwaMaterialu = nazwaMaterialu;
    }

    /**
     * Zwraca indeks w tabeli boma.
     * @return indeks w tabeli
     */
    public int getTabelaPozycja() {
        return tabelaPozycja;
    }

    /**
     * Ustala indeks w tabeli boma
     * @param tabelaPozycja Indeks w tabeli
     */
    public void setTabelaPozycja(int tabelaPozycja) {
        this.tabelaPozycja = tabelaPozycja;
    }

    /**
     * Zwraca id z bazy danych boma
     * @return id z bazy
     */
    public int getId() {
        return id;
    }

    /**
     * Ustala Id w bazie danych.
     * @param id 
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Zwraca id węzła do którego przynależy bom
     * @return id wezła z bazy
     */
    public int getId_wezla() {
        return id_wezla;
    }

    /**
     * Ustala id wezła dla boma.
     * @param id_wezla id węzła w bazie danych
     */
    public void setId_wezla(int id_wezla) {
        this.id_wezla = id_wezla;
    }

    /**
     * Zwraca listę atestów 
     * @return Lista atestów
     */
    public ArrayList<Atest> getListOfCerts() {
        return listOfCerts;
    }

    /**
     * Zapisuje liste atestów.
     * @param listOfCerts Lista atestów
     */
    public void setListOfCerts(ArrayList<Atest> listOfCerts) {
        this.listOfCerts = listOfCerts;
    }
    
    
}
