/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyClasses;

/**
 * Klasa do zarządzania ścieżkami atestów w tabeli.
 * @author Łukasz Wawrzyniak
 */
public class CertPath {
    
    // Ścieżka do pliku
    private String path;
    // Pozycja w tabeli
    private int tableInex;
    // Id w bazie danych
    private int id;
    // Id atestu w bazie
    private int certId;
    // Nazwa atestu.
    private String nazwa;

    /**
     * Zwraca ścieżkę do atestu.
     * @return ścieżka atestu.
     */
    public String getPath() {
        return path;
    }

    /**
     * Ustala ścieżkę atestu.
     * @param path Ścieżka atestu.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Zwraca indeks obiektu z tabeli.
     * @return Indeks w tabeli.
     */
    public int getTableInex() {
        return tableInex;
    }

    /**
     * Ustala indeks obiektu w tabeli.
     * @param tableInex indeks w tabeli.
     */
    public void setTableInex(int tableInex) {
        this.tableInex = tableInex;
    }

    /**
     * Zwraca ID z bazy danych
     * @return ID
     */
    public int getId() {
        return id;
    }

    /**
     * Ustala ID który obiekt ma w bazie danych
     * @param id ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Zwraca ID atestu którego dotyczy w bazie danych.
     * @return ID atestu.
     */
    public int getCertId() {
        return certId;
    }

    /**
     * Ustala ID atestu którego dotyczy w bazie danych.
     * @param certId ID atestu.
     */
    public void setCertId(int certId) {
        this.certId = certId;
    }

    /**
     * Nazwa atestu
     * @return Nazwa
     */
    public String getNazwa() {
        return nazwa;
    }

    /**
     * Ustala nazwę atestu
     * @param nazwa nazwa atestu
     */
    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }
}
