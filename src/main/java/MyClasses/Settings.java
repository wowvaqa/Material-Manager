package MyClasses;

/**
 * Klasa przechowująca ustawienia.
 * @author Łukasz Wawrzyniak
 */
public class Settings {
  
  /* Ścieżka do pliku z rejestrem łożysk. */
  private String bearingRegistryPath;
  

  /**
   * Zwraca ścieżkę do pliku z rejesterm łożysk.
   * @return ścieżka do pliku
   */
  public String getBearingRegistryPath() {
    return bearingRegistryPath;
  }

  /**
   * Ustawia ścieżkę do pliku z rejestrem łożysk.
   * @param bearingRegistryPath  ścieżka do pliku
   */
  public void setBearingRegistryPath(String bearingRegistryPath) {
    this.bearingRegistryPath = bearingRegistryPath;
  }
  
  private Settings() {
  }
  
  public static Settings getInstance() {
    return SettingsHolder.INSTANCE;
  }
  
  private static class SettingsHolder {

    private static final Settings INSTANCE = new Settings();
  }
}
