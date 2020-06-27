package MyClasses;

/**
 * Klasa przechowująca ustawienia.
 * @author Łukasz Wawrzyniak
 */
public class Settings {
  
  /**
   * Ścieżka dla wzoru dokumentu karty konroli łożyska jednokierunkowego,
   * stałego, wielokierunkowego oblachowanego
   */
  public static final String ELASTOMER_BEARING_ONE_WAY_PATH = 
          ".\\data\\wzor_00.xlsx";
  
  /* Ścieżka dla wzoru dokumentu karty konroli łożyska wielokierunkowego */
  public static final String ELASTOMER_BEARING_MANY_WAY_PATH = 
          ".\\data\\wzor_01.xlsx";
  
  /* Ścieżki do plików zawierających wzory KK dla łożysk garnkowych */
  public static final String POT_BEARING_MANY_WAY_PATH = ".\\data\\wzor_PS.xlsx";
  public static final String POT_BEARING_ONE_WAY_PATH = ".\\data\\wzor_PG.xlsx";
  public static final String POT_BEARING_CONSTANT_PATH = ".\\data\\wzor_PF.xlsx";
  
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
