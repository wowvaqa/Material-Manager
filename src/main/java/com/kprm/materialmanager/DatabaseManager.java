package com.kprm.materialmanager;

import MyClasses.Material;
import MyClasses.PkdNumber;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * @author Łukasz Wawrzyniak
 */
public class DatabaseManager {

    private String _login;
    private String _pass;
    private String _dbAdress;
    private String _dbName = null;
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    /**
     * Łączy z bazą danych
     *
     * @param login Login do bazy danych
     * @param password Hasło do bazy danych.
     * @param dbAdress Adres bazy danych.
     * @throws java.lang.ClassNotFoundException Błąd połączenia z bazą danych.
     */
    public void connectDatabase(String login, String password, String dbAdress) throws
            ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        //&serverTimezone=" + TimeZone.getDefault().getID()"
        try {
            //connect = DriverManager
            //        .getConnection("jdbc:mysql:" + dbAdress + "?useUnicode=yes&characterEncoding=UTF-8", login, password);
            connect = DriverManager
                    .getConnection("jdbc:mysql:" + dbAdress + "?autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8&serverTimezone=" + TimeZone.getDefault().getID(), login, password);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    /**
     * @return Zwraca nazwę bazy danych.
     */
    public String getNameOfDatabase() {
        try {
            return connect.getSchema();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean checkConnection() {
        try {
            if (connect.isValid(10)) {
                System.out.println("Połączenie jest ważne");
                return true;
            } else {
                System.out.println("Połączenie NIE jest ważne");
                try {
                    connectDatabase(_login, _pass, _dbAdress);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * @param nameFilter Filtr nazwy materiału.
     * @return Zwraca tablicę z materiałami
     */
    public ArrayList readMaterials(String nameFilter) {

        try {
            statement = connect.createStatement();

            if (nameFilter == null) {
                resultSet = statement.executeQuery("select * from " + DatabaseManager.getInstance().getDbName() + ".materialy");
            } else {
                resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".materialy WHERE nazwa LIKE '%" + nameFilter + "%';");
            }

            ArrayList<Material> materialy = new ArrayList();

            while (resultSet.next()) {
                //System.out.println(resultSet.getString("nazwa"));
                //materialy.add(resultSet.getString("nazwa"));

                Material material = new Material();
                material.setName(resultSet.getString("nazwa"));
                material.setLack(resultSet.getInt("braki"));

                materialy.add(material);
            }

            return materialy;

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Zwraca id podanego materiału
     *
     * @param nazwaMaterialu Nazwa materiału
     * @return Zwraca id materiału z bazy danych
     */
    public int getIdOfMaterial(String nazwaMaterialu) {
        try {
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT id FROM "
                    + "" + DatabaseManager.getInstance().getDbName() + ".materialy WHERE nazwa = '" + nazwaMaterialu
                    + "'");
            if (getSizeOfResuleSet(resultSet) > 0) {
                return resultSet.getInt("id");
            }

        } catch (SQLException ex) {
            //System.out.println("ERROR CODE: " + ex.getErrorCode());
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE,
                    null, ex);
        }

        return -1;
    }

    /**
     * Odszukuje nazwę materiału wg podanego ID
     *
     * @param materialId Id materiału
     * @return Nazwa materiału.
     */
    public String getNameOfMaterial(int materialId) {
        try {
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT nazwa FROM "
                    + "" + DatabaseManager.getInstance().getDbName() + ".materialy WHERE id = '" + materialId
                    + "'");
            if (getSizeOfResuleSet(resultSet) > 0) {
                return resultSet.getString("nazwa");
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return null;
    }

    /**
     * Dodaje nowy materiał do bazy danych.
     *
     * @param nazwaMaterialu Nazwa nowego materiału.
     */
    public void addNewMaterial(String nazwaMaterialu) {
        try {

            preparedStatement = connect.prepareStatement("INSERT INTO "
                    + "`" + DatabaseManager.getInstance().getDbName() + "`.`materialy` (`id`, `nazwa`)"
                    + " VALUES (NULL, '" + nazwaMaterialu + "')");
            preparedStatement.execute("SET NAMES 'UTF8'");
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    /**
     * Dodaje nowy atest do materiału.
     *
     * @param nazwaAtestu Nazwa atestu
     * @param nrZamowienia Zamówienie którego dotyczy atest
     * @param nrWZ Numer dok. WZ którego dotyczy atest.
     * @param file Plik z atestem.
     * @param zp Numer ZP
     * @param dostawca Nazwa dostawcy
     * @param idMaterialu Id materiału z bazy
     * @param dataDostawy Data dostawy materiału.
     * @param pkd Numer PKD
     * @return -1 jeżeli błąd
     */
    public int addNewCert(String nazwaAtestu, String nrZamowienia, String nrWZ,
            File file, String zp, String dostawca, String pkd, String dataDostawy, int idMaterialu) {

        Date dNow = new Date();
        SimpleDateFormat ft
                = new SimpleDateFormat("yyyy-MM-dd");
//        System.out.println("Current Date: " + ft.format(dNow));

//        int brak_dok = 0;
//        /* Sprawdza czy kolumny zawierające informacje dot. wz, zp są puste */
//        if (nrZamowienia.trim().length() < 1 || nrWZ.trim().length() < 1 
//                || zp.trim().length() < 1 || dostawca.trim().length() < 1 
//                || pkd.trim().length() < 1){
//            brak_dok = 1;
//        }
        if (dataDostawy.equals("    -  -  ")) {
            dataDostawy = "0000-00-00";
        }

        try {
            preparedStatement = connect.prepareStatement(
                    "INSERT INTO `" + DatabaseManager.getInstance().getDbName() + "`.`atesty` "
                    + "(`nazwa`, `nr_zamowienia`, `nr_wz`, `sciezka`, "
                    + "`data_dodania`, "
                    + "`id_materialu`, `pkd`, `zp`, `dostawca`, `braki_cert`) VALUES ('" + nazwaAtestu + "', '"
                    + nrZamowienia + "', '" + nrWZ + "', '"
                    + "" /*sciezka*/ + "', '" + dataDostawy + "', '"
                    + idMaterialu + "' , '" + pkd + "', '" + zp + "', '" + dostawca + "', '" + 1 + "');"
            );

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE,
                    null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Zwraca atesty wg podanej nazwy
     *
     * @param filter słowo do szukania
     * @param mode Tryb: 1 - nazwa atestu, 2 - Zamówienie, 3 - ZP, 4 - dostawca
     * @return Atesty wg podanej nazwy.
     */
    public ResultSet getAtestOfMaterial(String filter, int mode) {

        String statment;

        switch (mode) {
            case 1: // Nazwa atestu
                statment = "SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".atesty WHERE nazwa LIKE '%"
                        + filter + "%';";
                break;
            case 2: // Numer zamówienia
                statment = "SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".atesty WHERE nr_zamowienia LIKE '%"
                        + filter + "%';";
                break;
            case 3: // Numer ZP
                statment = "SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".atesty WHERE zp LIKE '%"
                        + filter + "%';";
                break;
            case 4: // Dostawca
                statment = "SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".atesty WHERE dostawca LIKE '%"
                        + filter + "%';";
                break;
            default:
                statment = "SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".atesty WHERE nazwa LIKE '%"
                        + filter + "%';";
        }

        try {
            resultSet = statement.executeQuery(statment);

            if (getSizeOfResuleSet(resultSet) > 0) {
                return resultSet;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Zwraca atesty wg podanego id materiału.
     *
     * @param idMaterialu Id materiału z bazy
     * @return Zwraca dane z bazy danych.
     */
    public ResultSet getAtestOfMaterial(int idMaterialu) {
        //checkConnection();
        try {
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".atesty"
                    + " WHERE id_materialu = '" + idMaterialu + "'");

            if (getSizeOfResuleSet(resultSet) > 0) {
                return resultSet;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE,
                    null, ex);
        }

        return null;
    }

    /**
     * Dodaje nowy węzeł do bazy danych.
     *
     * @param nazwaWezla Nazwa nowego węzła.
     * @param parentID Id rodzica z bazy danych.
     * @return zwraca -1 jeżeli dodawanie rekordu do bazy nie powiodło się
     */
    public int addNode(String nazwaWezla, int parentID) {
        try {
            preparedStatement = connect.prepareStatement(
                    "INSERT INTO `" + DatabaseManager.getInstance().getDbName() + "`.`wezel` (`nazwa`, `rodzic`) VALUES ('" + nazwaWezla + "', '" + parentID + "');"
            );
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(null, "Dodawanie węzła: " + nazwaWezla + " zakończone sukcesem.");

            return getLastAddedNodeId();

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    /**
     * Dodaje ścieżkę do pliku z atestem.
     *
     * @param file Plik atestu.
     * @param id_atestu ID atestu którego dotyczy ścieżka.
     * @param certPath Ścieżka do atestu.
     * @param opis Opis atestu jednoznacznie informujący jakiego materiału
     * dlotyczy atest.
     * @return -1 jeżeli błąd.
     */
    public int addCertFilePath(File file, int id_atestu, String certPath, String opis) {
        String sciezka;
        sciezka = file.getPath().replaceAll("\\\\", "/");

        String sciezka2 = sciezka;

        //System.out.println("Znaki do wyszukania: " + certPath);
        //System.out.println("Zmienna przed zmianą: " + sciezka2);
        //sciezka2.replaceAll(certPath, "");
        String certPathToUpdate = certPath.replaceAll("\\\\", "/");

        String sciezka3 = sciezka2.replaceAll(certPathToUpdate, "");

        //System.out.println("Zmienna po zamianie: " + sciezka3);
        try {
            preparedStatement = connect.prepareStatement(
                    "INSERT INTO `" + DatabaseManager.getInstance().getDbName() + "`.`atesty_pliki` (`sciezka`, `id_atest`, `nazwa`) VALUES ('" + sciezka3 + "', '" + id_atestu + "', '" + opis + "');"
            );
            preparedStatement.executeUpdate();

            lackClearFromCert(id_atestu);

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Czyści barak z atestu
     */
    private void lackClearFromCert(int id_atest) {
        try {
            preparedStatement = connect.prepareStatement(
                    "UPDATE `" + DatabaseManager.getInstance().getDbName() + "`.`atesty` SET `braki_cert` = '0' WHERE (`id` = '" + id_atest + "');"
            );
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Zapisuje informacje o braku pliku certyfikatu w ateście.
     *
     * @param id_atest Id atestu
     * @param lackStatus Status braku: 1 - jest brak, 0 - nie ma braku
     */
    public void lackAddToCert(int id_atest, int lackStatus) {
        try {
            preparedStatement = connect.prepareStatement(
                    "UPDATE `" + DatabaseManager.getInstance().getDbName() + "`.`atesty` SET `braki_cert` = '" + lackStatus + "' WHERE (`id` = '" + id_atest + "');"
            );
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Zapisuje informacje o brakach w atestach do materiału.
     *
     * @param id_materialu ID materiału.
     * @param lackStatus Status braku: 1 - jest brak, 0 - nie ma braku
     */
    public void lackAddToMaterial(int id_materialu, int lackStatus) {
        try {
            preparedStatement = connect.prepareStatement(
                    "UPDATE `" + DatabaseManager.getInstance().getDbName() + "`.`materialy` SET `braki` = '"
                    + lackStatus + "' WHERE (`id` = '" + id_materialu + "');"
            );
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Zwraca wyniki z nazwami dla zadanego węzła
     *
     * @param nodeId Id węzła
     * @return Wynik z nazwami węzła
     */
    public ResultSet getNodeName(int nodeId) {
        try {
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM "
                    + DatabaseManager.getInstance().getDbName()
                    + ".wezel WHERE id = '" + nodeId + "'");

            if (getSizeOfResuleSet(resultSet) > 0) {
                return resultSet;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Zwraca wszystkie rekordy z węzłami.
     *
     * @return Obiekt klasy ResultSet.
     */
    public ResultSet getNodes() {
        try {
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".wezel ");

            if (getSizeOfResuleSet(resultSet) > 0) {
                return resultSet;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Zwraca rekordy za ścieżkami do atestów dla zadanego atestu.
     *
     * @param atest_id ID atestu
     * @return Resultset
     */
    public ResultSet getCertsFilePaths(int atest_id) {
        try {
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".atesty_pliki WHERE id_atest = '" + atest_id + "'");

            if (getSizeOfResuleSet(resultSet) > 0) {
                return resultSet;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Dodaje materiał z boma do bazy danych
     *
     * @param nazwaMaterialu Nazwa materiału z Boma.
     * @param id_wezla Id wezła jakiego dotyczy materiał.
     */
    public void addMaterialIntoBom(String nazwaMaterialu, int id_wezla) {
        try {
            preparedStatement = connect.prepareStatement(
                    "INSERT INTO `" + DatabaseManager.getInstance().getDbName() + "`.`bom` (`material`, `wezel`) VALUES ('" + nazwaMaterialu + "', '" + id_wezla + "');"
            );
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Dodaje zależnoćś atest - boma do bazy danych
     *
     * @param idAtest Id atestu
     * @param idBom Id boma
     */
    public void addCertIntoBom(int idAtest, int idBom) {
        try {
            preparedStatement = connect.prepareStatement(
                    "INSERT INTO `" + DatabaseManager.getInstance().getDbName() + "`.`atest_bom` (`id_atest`, `id_bom`) VALUES ('" + idAtest + "', '" + idBom + "');"
            );
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Zapisuje ścieżkę do katalogu z atestami.
     *
     * @param path Ścieżka katalogu z atestami.
     * @return -1 jeżeli błąd.
     */
    public int saveCertDirectory(String path) {
        try {
            preparedStatement = connect.prepareStatement(
                    "UPDATE `" + DatabaseManager.getInstance().getDbName() + "`.`ustawienia` SET `string_value` = '" + path + "' WHERE (`id` = '1');"
            );
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Odszukuje atesty przynależne do zadanego boma
     *
     * @param idBom Id boma
     * @return Zwraca atesty przyporzadkowane do pozycji w bomie.
     */
    public ResultSet findAtestInBom(int idBom) {
        try {
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".atest_bom"
                    + " WHERE id_bom = '" + idBom + "'");

            if (getSizeOfResuleSet(resultSet) > 0) {
                return resultSet;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Importuje wszystkie bomy z bazy danych
     *
     * @return Null jeżeli bom jest pusty
     */
    public ResultSet importAllBomFromDB() {
        try {
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".bom");

            if (getSizeOfResuleSet(resultSet) > 0) {
                return resultSet;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }
        return null;
    }

    /**
     * Importuje materiały wg zadanego Boma
     *
     * @param id_wezla ID węzła
     * @return Return set
     */
    public ResultSet importBomFromDB(int id_wezla) {

        //_lblStatus.setText("Proszę czekać...");
        try {
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".bom"
                    + " WHERE wezel = '" + id_wezla + "'");

            if (getSizeOfResuleSet(resultSet) > 0) {
                return resultSet;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            //_lblStatus.setText("Status!");
        }
        return null;
    }

    /**
     * Wyszukuje wszystkie atesty w bazie danych.
     *
     * @return Atesty.
     */
    public ResultSet searchAllCerts() {
        try {
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".atesty;");

            if (getSizeOfResuleSet(resultSet) > 0) {
                return resultSet;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Pinguje bazę danych w celu odnowienia połączenia
     *
     * @return
     */
    public int pingDb() {
        try {
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT 'id' FROM " + DatabaseManager.getInstance().getDbName() + ".ustawienia;");
            return 1;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    /**
     * Odszukuje atesty w bazie wg zadanych kryteriów.
     *
     * @param nazwaAtestu Nazwa atestu.
     * @param nazwaMaterialu Nazwa materiału.
     * @return ResultSet z atestami.
     */
    public ResultSet searchCerts(String nazwaAtestu, String nazwaMaterialu) {

        /* Wyszukanie atestów wg nazw atestów. */
        if (nazwaMaterialu == null) {
            try {
                statement = connect.createStatement();
                resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".atesty WHERE nazwa LIKE '%" + nazwaAtestu + "%';");

//                System.out.println("Nazwa atestu: " + nazwaAtestu);
//                System.out.println("Rozmiar resultSeta: " + getSizeOfResuleSet(resultSet));
                if (getSizeOfResuleSet(resultSet) > 0) {
                    return resultSet;
                } else {
                    return null;
                }

            } catch (SQLException ex) {
                if (ex.getErrorCode() != 0) {
                    JOptionPane.showMessageDialog(null, ex);
                }
                Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (nazwaAtestu == null) {
            try {
                statement = connect.createStatement();
                resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".materialy WHERE nazwa LIKE '%" + nazwaMaterialu + "%';");

//                System.out.println("Nazwa atestu: " + nazwaMaterialu);
//                System.out.println("Rozmiar resultSeta: " + getSizeOfResuleSet(resultSet));
                if (getSizeOfResuleSet(resultSet) > 0) {
                    return resultSet;
                } else {
                    return null;
                }

            } catch (SQLException ex) {
                if (ex.getErrorCode() != 0) {
                    JOptionPane.showMessageDialog(null, ex);
                }
                Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     * Wyszukuje atest na podstawie id materiału.
     *
     * @param material_id Id materiału
     * @return ResultSet z atestami.
     */
    public ResultSet searchCertByMaterialID(int material_id) {
        try {
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".atesty WHERE id_materialu ='" + material_id + "';");

//                System.out.println("Nazwa atestu: " + nazwaMaterialu);
//                System.out.println("Rozmiar resultSeta: " + getSizeOfResuleSet(resultSet));
            if (getSizeOfResuleSet(resultSet) > 0) {
                return resultSet;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Odszukuje zadany parametrem atest w bazie.
     *
     * @param idAtestu Id atestu z bazy.
     * @return Zwraca resultSeta
     */
    public ResultSet searchCert(int idAtestu) {

        try {
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".atesty WHERE id = '" + idAtestu + "';");

            //System.out.println("Nazwa atestu: " + nazwaAtestu);
            //System.out.println("Rozmiar resultSeta: " + getSizeOfResuleSet(resultSet));
            if (getSizeOfResuleSet(resultSet) > 0) {
                return resultSet;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Zwraca numer PKD
     *
     * @return numer PKD
     */
    public PkdNumber readPkdNumber() {
        PkdNumber pkdNumber = new PkdNumber();

        try {
            statement = connect.createStatement();

            resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".ustawienia WHERE id = '3';");
            resultSet.first();
            pkdNumber.setActualPkdNumber(resultSet.getInt("int_value"));

            resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".ustawienia WHERE id = '4';");
            resultSet.first();
            pkdNumber.setActualPkdMonthNumber(resultSet.getInt("int_value"));

            resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".ustawienia WHERE id = '5';");
            resultSet.first();
            pkdNumber.setActualPkdYearNumber(resultSet.getInt("int_value"));

            return pkdNumber;

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Aktualizuje numer PKD w bazie danych.
     *
     * @param pkdNumber Numer pkd
     * @param mode Tryb pracy bazy danych; 0 - baza produkcyjna, 1 - baza
     * testowa
     * @return -1 jeżeli błąd.
     */
    public int renamePkdNumber(PkdNumber pkdNumber, int mode) {
        try {

            switch (mode) {
                case 0:
                    preparedStatement = connect.prepareStatement(
                            "UPDATE `aspekt_materials`.`ustawienia` SET `int_value` = '" + pkdNumber.getActualPkdNumber() + "' WHERE (`id` = '3');"
                    );
                    preparedStatement.executeUpdate();

                    preparedStatement = connect.prepareStatement(
                            "UPDATE `aspekt_materials`.`ustawienia` SET `int_value` = '" + pkdNumber.getActualPkdMonthNumber() + "' WHERE (`id` = '4');"
                    );
                    preparedStatement.executeUpdate();

                    preparedStatement = connect.prepareStatement(
                            "UPDATE `aspekt_materials`.`ustawienia` SET `int_value` = '" + pkdNumber.getActualPkdYearNumber() + "' WHERE (`id` = '5');"
                    );
                    preparedStatement.executeUpdate();
                    break;
                case 1:
                    preparedStatement = connect.prepareStatement(
                            "UPDATE `aspekt_materials_test`.`ustawienia` SET `int_value` = '" + pkdNumber.getActualPkdNumber() + "' WHERE (`id` = '3');"
                    );
                    preparedStatement.executeUpdate();

                    preparedStatement = connect.prepareStatement(
                            "UPDATE `aspekt_materials_test`.`ustawienia` SET `int_value` = '" + pkdNumber.getActualPkdMonthNumber() + "' WHERE (`id` = '4');"
                    );
                    preparedStatement.executeUpdate();

                    preparedStatement = connect.prepareStatement(
                            "UPDATE `aspekt_materials_test`.`ustawienia` SET `int_value` = '" + pkdNumber.getActualPkdYearNumber() + "' WHERE (`id` = '5');"
                    );
                    preparedStatement.executeUpdate();
                    break;
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Odczytuje ścieżkę do katalogu z atestami z bazy danych.
     *
     * @return Ścieżka do katalogu z atestami.
     */
    public String readCertPath() {

        String path = null;
        try {
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".ustawienia WHERE id = '1';");

            resultSet.first();
            path = resultSet.getString("string_value");

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return path;
    }

    /**
     * Wyszukuje atesty wg boma
     *
     * @param idBom Id boma
     * @return Atesty
     */
    public ResultSet searchCertInAtestBom(int idBom) {

        try {
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".atest_bom WHERE id_bom = '" + idBom + "';");

            //System.out.println("Nazwa atestu: " + nazwaAtestu);
            //System.out.println("Rozmiar resultSeta: " + getSizeOfResuleSet(resultSet));
            if (getSizeOfResuleSet(resultSet) > 0) {
                return resultSet;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Zmienia nazwę węzła w bazie danych.
     *
     * @param nazwa Nowa nazwa węzła
     * @param idWezla Id węzła z bazy danych, którego nazwa ma być zmieniona.
     * @return Zwraca -1 kiedy operacja zakończona niepowodzeniem.
     */
    public int renameNode(String nazwa, int idWezla) {
        try {
            preparedStatement = connect.prepareStatement(
                    "UPDATE `" + DatabaseManager.getInstance().getDbName() + "`.`wezel` SET `nazwa` = '" + nazwa + "' WHERE (`id` = '" + idWezla + "');"
            );
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Zmienia nazwę atestu.
     *
     * @param nazwa Nowa nazwa atestu.
     * @param id ID atestu w tabeli.
     * @return -1 jeżeli błąd.
     */
    public int renameCertName(String nazwa, int id) {
        try {
            preparedStatement = connect.prepareStatement(
                    "UPDATE `" + DatabaseManager.getInstance().getDbName() + "`.`atesty` SET `nazwa` = '" + nazwa + "' WHERE (`id` = '" + id + "');"
            );
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Zmienia numer zamówienia atestu.
     *
     * @param nazwa Nowy numer zamówienia.
     * @param id ID atestu w tabeli.
     * @return -1 jeżeli błąd.
     */
    public int renameCertOrder(String nazwa, int id) {
        try {
            preparedStatement = connect.prepareStatement(
                    "UPDATE `" + DatabaseManager.getInstance().getDbName() + "`.`atesty` SET `nr_zamowienia` = '" + nazwa + "' WHERE (`id` = '" + id + "');"
            );
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Zmienia numer WZ atestu.
     *
     * @param nazwa Nowy numer WZ.
     * @param id ID atestu w tabeli.
     * @return -1 jeżeli błąd.
     */
    public int renameCertWz(String nazwa, int id) {
        try {
            preparedStatement = connect.prepareStatement(
                    "UPDATE `" + DatabaseManager.getInstance().getDbName() + "`.`atesty` SET `nr_wz` = '" + nazwa + "' WHERE (`id` = '" + id + "');"
            );
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Zmienia numer Zp atestu
     *
     * @param nazwa Nowy numer Zp
     * @param id Identyfikator materiału
     * @return -1 jeżeli błąd
     */
    public int renameCertZp(String nazwa, int id) {
        try {
            preparedStatement = connect.prepareStatement(
                    "UPDATE `" + DatabaseManager.getInstance().getDbName() + "`.`atesty` SET `zp` = '" + nazwa + "' WHERE (`id` = '" + id + "');"
            );
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Zmienia datę dostawy atestu
     *
     * @param deliveryDate Data dostawy
     * @param id Identyfikator materiału
     * @return -1 jeżeli błąd
     */
    public int renameCertDeliveryDate(String deliveryDate, int id) {
        try {
            preparedStatement = connect.prepareStatement(
                    "UPDATE `" + DatabaseManager.getInstance().getDbName() + "`.`atesty` SET `data_dodania` = '" + deliveryDate + "' WHERE (`id` = '" + id + "');");
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Zmienia numer PKD atestu
     *
     * @param nazwa Nowy numer PKD
     * @param id Identyfikator atestu
     * @return -1 jeżeli błąd
     */
    public int renameCertPkd(String nazwa, int id) {
        try {
            preparedStatement = connect.prepareStatement(
                    "UPDATE `" + DatabaseManager.getInstance().getDbName() + "`.`atesty` SET `pkd` = '" + nazwa + "' WHERE (`id` = '" + id + "');"
            );
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Zmienia nazwę dostawcy
     *
     * @param nazwa Nowa nazwa dostawcy
     * @param id Identyfikator materiału
     * @return -1 jeżeli błąd
     */
    public int renameCertSupplier(String nazwa, int id) {
        try {
            preparedStatement = connect.prepareStatement(
                    "UPDATE `" + DatabaseManager.getInstance().getDbName() + "`.`atesty` SET `dostawca` = '" + nazwa + "' WHERE (`id` = '" + id + "');"
            );
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Zmienia opis pliku z atestem
     *
     * @param opis Opis
     * @param id Id atestu
     * @return -1 jeżeli błąd
     */
    public int renameCertFileDescription(String opis, int id) {
        try {
            preparedStatement = connect.prepareStatement(
                    "UPDATE `" + DatabaseManager.getInstance().getDbName() + "`.`atesty_pliki` SET `nazwa` = '" + opis + "' WHERE (`id` = '" + id + "');"
            );
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Zmienia nazwę materiału.
     *
     * @param nazwa Nowa nazwa materiału.
     * @param oldNazwa Stara nazwa materiału.
     * @return -1 jeżeli błąd.
     */
    public int renameMaterialNode(String nazwa, String oldNazwa) {
        try {
            preparedStatement = connect.prepareStatement(
                    "UPDATE `" + DatabaseManager.getInstance().getDbName() + "`.`materialy` SET `nazwa` = '" + nazwa + "' WHERE (`nazwa` = '" + oldNazwa + "');"
            );
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Zmienia id materiału do którego przynależy atest
     *
     * @param nazwaMaterialu Unikatowa nazwa materiału.
     * @return -1 jeżeli błąd
     */
    public int renameCertMaterialId(String nazwaMaterialu) {
        try {

            int idMaterialu = getIdOfMaterial(nazwaMaterialu);

            preparedStatement = connect.prepareStatement(
                    "UPDATE `" + DatabaseManager.getInstance().getDbName() + "`.`atesty` SET `id_materialu` = '"
                    + idMaterialu
                    + "' WHERE (`id` = '"
                    + AtestManager.getInstance().getClipboard().getId()
                    + "');"
            );
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Usuwa materiał z bazy danych.
     *
     * @param nazwa Nazwa materiału do usunięcia.
     * @return -1 jeżeli błąd.
     */
    public int removeMaterialNode(String nazwa) {
        try {
            preparedStatement = connect.prepareStatement(
                    "DELETE FROM `" + DatabaseManager.getInstance().getDbName() + "`.`materialy` WHERE (`nazwa` = '" + nazwa + "');"
            );
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, "Nie można usunąć materiału - najpierw usuń atesty.");
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Usuwa atest z bazy danych.
     *
     * @param id ID atestu do usunięcia
     * @return -1 jeżeli błąd.
     */
    public int removeCert(int id) {
        try {
            preparedStatement = connect.prepareStatement(
                    "DELETE FROM `" + DatabaseManager.getInstance().getDbName() + "`.`atesty` WHERE (`id` = '" + id + "');"
            );
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {

            if (ex.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(null, "Atest użyty w Bomie, nie można usunąć. ");
                return -1;
            }
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Usuwa ścieżkę do pliku atestu z bazy danych.
     *
     * @param id ID rekordu ścieżki do pliku.
     * @return -1 jeżeli błąd.
     */
    public int removeCertFile(int id) {
        try {
            preparedStatement = connect.prepareStatement(
                    "DELETE FROM `" + DatabaseManager.getInstance().getDbName() + "`.`atesty_pliki` WHERE (`id` = '" + id + "');"
            );
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Usuwa węzeł
     *
     * @param idNode Id węzła z bazy danych.
     * @return zwraca -1 jeżeli usuwanie zakończone niepowodzeniem.
     */
    public int removeNode(int idNode) {
        try {
            preparedStatement = connect.prepareStatement(
                    "DELETE FROM `" + DatabaseManager.getInstance().getDbName() + "`.`wezel` WHERE (`id` = '" + idNode + "');"
            );
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(null, "Węzeł zawiera podwęzły lub BOMA - nie można usunąć.");
                return -1;
            }

            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Usuwa zadany rekord z tabel atest_bom
     *
     * @param id id rekordu
     * @return Zwraca -1 jeżeli błąd.
     */
    public int removeCertFromBomTable(int id) {
        try {
            preparedStatement = connect.prepareStatement(
                    "DELETE FROM `" + DatabaseManager.getInstance().getDbName() + "`.`atest_bom` WHERE (`id` = '" + id + "');"
            );
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Zmienia nazwę materiału w zadanym bomie.
     *
     * @param name Nowa nazwa materiału.
     * @param id Id Materiału w bomie.
     * @return Zwraca -1 w przypadku niepowodzenia.
     */
    public int renameMaterialInBom(String name, int id) {
        try {
            preparedStatement = connect.prepareStatement(
                    "UPDATE `" + DatabaseManager.getInstance().getDbName() + "`.`bom` SET `material` = '"
                    + name + "' WHERE (`id` = '"
                    + id + "');"
            );
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, ex);
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Usuwa materiał z tabli bomów.
     *
     * @param id ID materiału z tabeli Bom
     * @return -1 kiedy błąd
     */
    public int removeMaterialFromBom(int id) {
        try {
            preparedStatement = connect.prepareStatement(
                    "DELETE FROM `" + DatabaseManager.getInstance().getDbName() + "`.`bom` WHERE (`id` = '" + id + "');"
            );
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, "Nie mogę usunąć materiału z przyporządkowanym atestem.");
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } 
        return 1;
    }

    /**
     * Usuwa wszystkie materiały z tabeli bomów wg węzła.
     *
     * @param idWezla ID węzła
     * @return -1 jeżeli błąd
     */
    public int removeAllMaterialsFromBom(int idWezla) {
        try {
            preparedStatement = connect.prepareStatement(
                    "DELETE FROM `" + DatabaseManager.getInstance().getDbName() + "`.`bom` WHERE (`wezel` = '" + idWezla + "');"
            );
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 0) {
                JOptionPane.showMessageDialog(null, "Nie można usunąć wszystkich materiałów. "
                        + "Sprawdź czy materiały w bomie nie mają przyporządkowanych atestów.");
            }
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    /**
     * Zwraca Id ostatniego dodanego do bazy danych węzła.
     *
     * @return Id węzła
     */
    private int getLastAddedNodeId() {

        try {
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getInstance().getDbName() + ".wezel");
            resultSet.last();
            System.out.println("Ostani id w tabeli wezly:" + resultSet.getInt("id"));
            return resultSet.getInt("id");

        } catch (SQLException ex) {

            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }

    /**
     * Zwraca rozmiar ResuleSet
     *
     * @param resultSet ResultSet którego rozmiar ma zostać sprawdzony.
     * @return Rozmia Result seta
     * @throws SQLException Błąd SQL
     */
    public int getSizeOfResuleSet(ResultSet resultSet) throws SQLException {

        int size = 0;
        if (resultSet != null) {
            resultSet.beforeFirst();
            resultSet.last();
            size = resultSet.getRow();
        }
        //System.out.println("Size of resulSet: " + size);

        return size;
    }

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        return DatabaseManagerHolder.INSTANCE;
    }

    private static class DatabaseManagerHolder {

        private static final DatabaseManager INSTANCE = new DatabaseManager();
    }

    /**
     * @return Zwraca nazwę bazy dancy.
     */
    public String getDbName() {
        return _dbName;
    }

    /**
     * Zapisuje nazwę bazy danych.
     *
     * @param dbName Nazwa bazy danych.
     */
    public void setDbName(String dbName) {
        this._dbName = dbName;
    }

    /**
     * @return Zwraca login do bazy danych.
     */
    public String getLogin() {
        return _login;
    }

    /**
     * @param _login Login do bazy danych.
     */
    public void setLogin(String _login) {
        this._login = _login;
    }

    /**
     * @return Hasło do bazy danych.
     */
    public String getPass() {
        return _pass;
    }

    /**
     * @param _pass Hasło do bazy danych.
     */
    public void setPass(String _pass) {
        this._pass = _pass;
    }

    /**
     * @return Adres bazy danych.
     */
    public String getDbAdress() {
        return _dbAdress;
    }

    /**
     * @param _dbAdress Adres bazy danych.
     */
    public void setDbAdress(String _dbAdress) {
        this._dbAdress = _dbAdress;
    }

}
