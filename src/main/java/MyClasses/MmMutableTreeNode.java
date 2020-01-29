/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyClasses;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Rozwinięcie klasy DefaultMutableTreeNode.
 * Dodano pola umożliwiające identyfikację węzła w drzewku węzłów w celu 
 * odpowiedniego manipulowania węzłami na podstawie danych z bazy danych.
 * @author Łukasz Wawrzyniak
 */
public class MmMutableTreeNode extends DefaultMutableTreeNode{
    
    // Id rodzica węzła w bazie danych.
    private int parentId;
    // Id węzła w bazie danych.
    private int id;
    // Nazwa węzła
    private String nazwa;

    /**
     * Konstruktor nowego węzła w drzewku.
     * @param userObject Nazwa węzła - w przypadku tego programu.
     * @param parentId Id rodzica węzła
     */
    public MmMutableTreeNode(Object userObject, int parentId) {
        super(userObject);
        
        this.parentId = parentId;
    }

    /**
     * Konstruktor nowego węzła w drzewku.
     * @param userObject Nazwa węzła - w przypadku tego programu.
     * @param parentId Id rodzica węzła.
     * @param id Id węzła.
     */
    public MmMutableTreeNode(Object userObject, int parentId, int id) {
        super(userObject);
        this.parentId = parentId;
        this.id = id;
        this.nazwa = userObject.toString();
    }

    /**
     * Id węzła w bazie danych.
     * @return Zwraca Id z węzła w bazie danych.
     */
    public int getId() {
        return id;
    }

    /**
     * Zapisuje Id węzła z bazy danych.
     * @param id Id węzła w bazie danych.
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Id rodzica węzła w bazie danych.
     * @return Zwraca Id rodzica węzła w bazie danych.
     */
    public int getParentId() {
        return parentId;
    }

    /**
     * Id rodzica węzła w bazie danych.
     * @param parentId Id rodzica węzła w bazie danych.
     */
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    /**
     * @return Zwraca nazwę węzła
     */
    public String getNazwa() {
        return nazwa;
    }

    /**
     * Ustala nazwę węzła
     * @param nazwa Nazwa węzła
     */
    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }
    
    
    
}
