package Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Klasa przechowuje ustawienia programu
 * @author ŁW
 */
public class MmConfig {
    
    @Getter
    @Setter
    /* Lokalizacja pliku rejestru łożysk */
    private String bearingRegistryFilePath;
    
}
