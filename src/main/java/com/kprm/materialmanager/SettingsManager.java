/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kprm.materialmanager;

import com.kprm.materialmanager.DatabaseManager;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

/**
 * Manager ustawień.
 * @author Łukasz Wawrzyniak
 */
public class SettingsManager {
    
    /**
     * Uruchamia wyszukiwanie katalogu z atestami
     * @param tfCertPath Pole gdzie zapisany zostanie wynik.
     */
    public void setupCertDirectory(JTextField tfCertPath){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setDialogTitle("Wybierz folder");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            
            String sciezka;
            sciezka = fileChooser.getSelectedFile().toString().replaceAll("\\\\", "/");
            tfCertPath.setText(sciezka);
        }
    }
    
    /**
     * Uruchamia zapisa katalogu z atestami do bazy danych.
     * @param directory Ścieżka do katalogu z atestami.
     */
    public void saveCertDirectory(String directory){
        DatabaseManager.getInstance().saveCertDirectory(directory);
    }
    
    /**
     * Uruchamia odczyt z bazy danych ścieżki do pliku z atestami.
     * @param tfCertPath 
     */
    public void readCertDirectory(JTextField tfCertPath){
        tfCertPath.setText(
                DatabaseManager.getInstance().readCertPath()
        );
    }
    
    private SettingsManager() {
    }
    
    public static SettingsManager getInstance() {
        return SettingsManagerHolder.INSTANCE;
    }
    
    private static class SettingsManagerHolder {

        private static final SettingsManager INSTANCE = new SettingsManager();
    }
}
