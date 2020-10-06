package Configuration;

import com.kprm.materialmanager.DatabaseManager;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

/**
 * Manager ustawień zapisywanych w bazie danych.
 *
 * @author Łukasz Wawrzyniak
 */
public class DatabaseConfigManager {

  /**
   * Uruchamia wyszukiwanie katalogu z atestami
   *
   * @param tfCertPath Pole gdzie zapisany zostanie wynik.
   */
  public void setupCertDirectory(JTextField tfCertPath) {
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
   *
   * @param directory Ścieżka do katalogu z atestami.
   */
  public void saveCertDirectory(String directory) {
    DatabaseManager.getInstance().saveCertDirectory(directory);
  }

  /**
   * Uruchamia odczyt z bazy danych ścieżki do pliku z atestami.
   *
   * @param tfCertPath
   */
  public void readCertDirectory(JTextField tfCertPath) {
    tfCertPath.setText(
            DatabaseManager.getInstance().readCertPath()
    );
  }

  /**
   * Ustawia w zadanym polu tekstowym wybraną przez użytkownika ścieżkę do
   * folderu zapisu kart kontroli.
   * @param tfCCSavePath Pole tekstowe zawierające ścieżkę do zapisu kart kontroli
   */
  public void setupControlCardSavePath(JTextField tfCCSavePath){
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setCurrentDirectory(new java.io.File("."));
    fileChooser.setDialogTitle("Wybierz folder zapisu kart kontroli");
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fileChooser.setAcceptAllFileFilterUsed(false);
    
    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      String sciezka;
      sciezka = fileChooser.getSelectedFile().toString().replaceAll("\\\\", "/");
      tfCCSavePath.setText(sciezka + "/");
    }    
  }

  private DatabaseConfigManager() {
  }

  public static DatabaseConfigManager getInstance() {
    return SettingsManagerHolder.INSTANCE;
  }

  private static class SettingsManagerHolder {

    private static final DatabaseConfigManager INSTANCE = new DatabaseConfigManager();
  }
}
