/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyClasses;

/**
 *
 * @author wakan
 */
public class Material {

    private String name;
    private int lack;

    /**
     * Nazwa materiału
     * @return nazwę
     */
    public String getName() {
        return name;
    }

    /**
     * Ustala nazwę materiału
     * @param name Nazwa materiału
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Zwraca czy materiał posiada niekompletny atest
     * @return brak w ateście.
     */
    public int getLack() {
        return lack;
    }

    /**
     * Zapisuje informacje o brakach w ateście
     * @param lack Brak
     */
    public void setLack(int lack) {
        this.lack = lack;
    }

}
