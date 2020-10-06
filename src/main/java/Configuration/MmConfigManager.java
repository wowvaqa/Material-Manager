package Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import lombok.Getter;
import lombok.Setter;

/**
 * Klasa zarządzająca konfiguracją programu.
 *
 * @author ŁW
 */
public class MmConfigManager {

    /* Nazwa katalogu przechowującego pliki konfiguracyjne */
    public static final String DIR_NAME = "//.materialManager";
    /* Nazwa pliku konfiguracyjnego */
    public static final String CONFIG_FILE_NAME = "config.json";

    /**
     * Ścieżka dla wzoru dokumentu karty konroli łożyska jednokierunkowego,
     * stałego, wielokierunkowego oblachowanego
     */
    public static final String ELASTOMER_BEARING_ONE_WAY_PATH
            = ".\\data\\wzor_00.xlsx";

    /* Ścieżka dla wzoru dokumentu karty konroli łożyska wielokierunkowego */
    public static final String ELASTOMER_BEARING_MANY_WAY_PATH
            = ".\\data\\wzor_01.xlsx";

    /* Ścieżki do plików zawierających wzory KK dla łożysk garnkowych */
    public static final String POT_BEARING_MANY_WAY_PATH = ".\\data\\wzor_PS.xlsx";
    public static final String POT_BEARING_ONE_WAY_PATH = ".\\data\\wzor_PG.xlsx";
    public static final String POT_BEARING_CONSTANT_PATH = ".\\data\\wzor_PF.xlsx";

    /* Arkusze które program wczyta z rejestru łożysk 7 - 2020, 6 - 2019, 5 - 2018 */
    public static final int[] BEARING_REGISTRY_SHEETS = {5, 6, 7};

    @Getter
    @Setter
    /* Obiekt przechowujący ustawienia programu */
    private static MmConfig mmConfig = new MmConfig();

    /**
     * Tworzy katalog przechowujący pliki konfiguracyjne programu
     *
     * @return True jeżeli katalog został utworzony
     */
    public static boolean createConfigurationDirectory() {

        boolean dirCreated = false;
        String userHomeDir = System.getProperty("user.home");

        File configDir = new File(System.getProperty("user.home") + DIR_NAME);
        if (!configDir.exists()) {
            dirCreated = configDir.mkdir();
            System.out.format("%n Folder: %s, Utworzony", userHomeDir + DIR_NAME);
            return dirCreated;
        } else {
            System.out.format("%n Katalog: %s istnieje", configDir.toString());
        }

        return dirCreated;
    }

    /**
     * Ładuje plik konfiguracyjny
     *
     * @return Obiekt klasy przechowującej ustawienia programu.
     */
    public static MmConfig loadConfigFile() {

        mmConfig = new MmConfig();
        ObjectMapper objectMapper = new ObjectMapper();

        File configFile = new File(MmConfigManager.getConfigFilePath());

        if (configFile.exists()) {
            try {
                mmConfig = objectMapper.readValue(configFile, MmConfig.class);
            } catch (IOException ex) {
                Logger.getLogger(MmConfigManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.format("%n Plik konfiguracyjny nie istnieje");
        }
        return mmConfig;
    }

    /**
     * Zwraca pełną ścieżkę do pliku konfiguracyjnego programu.
     *
     * @return Ścieżka do pliku konfiguracyjnego programu.
     */
    public static String getConfigFilePath() {
        String userHomeDir = System.getProperty("user.home");
        return userHomeDir + DIR_NAME + "//" + CONFIG_FILE_NAME;
    }

    /**
     * Ustawia ścieżkę do pliku rejestru łożysk.
     *
     * @param tfBearingRegistryPath Pole w ustawieniach z ścieżką do pliku
     * rejestru łożysk
     */
    public static void saveBearingRegistryPath(JTextField tfBearingRegistryPath) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setDialogTitle("Wybierz plik rejestru łożysk");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            String configFilePath;
            configFilePath = fileChooser.getSelectedFile().toString().replaceAll("\\\\", "/");
            tfBearingRegistryPath.setText(configFilePath);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

            mmConfig.setBearingRegistryFilePath(configFilePath);

            try {
                objectMapper.writeValue(new File(MmConfigManager.getConfigFilePath()), mmConfig);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Błąd zapisu pliku konfiguracyjnego. " + e);
            }
        }
    }
}
