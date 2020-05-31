package com.kprm.materialmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Manager ustawień.
 *
 * @author Łukasz Wawrzyniak
 */
public class SettingsManager {

  /* Flaga informująca czy jest możliwość odczytu i zapisu pliku 
    konfiguracyjnego GUI */
  public boolean guiConfig = false;

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
   * Tworzy folder przechowujący konfigurację programu w katalogu użytkownika.
   */
  public void createMmConfigFolder() {
    String homePath = System.getProperty("user.home");
    System.out.println("Home Path: " + homePath);

    File dir = new File(homePath + "\\materialManager");

    if (dir.exists()) {
      System.out.println("Katalog konfiguracyjny istnieje");
    } else {
      try {
        dir.mkdir();
        System.out.println("Utworzono katalog konfiguracyjny programu.");
      } catch (Exception e) {
        System.out.println("Nie można utworzyć katalogu konfiguracji programu, " + e);
      }
    }
  }

  /**
   * Tworzy plik konfiguracyjny INI dla GUI programu
   *
   * @return 1 jeżeli plik istnieje, 0 jeżeli plik został utworzony, -1 jeżeli
   * wystąpił błąd podczas tworzenia pliku konfiguracyjnego.
   */
  public int createConfigFile() {
    String configPath = System.getProperty("user.home") + "\\materialManager";

    File configFile = new File(configPath + "\\config.xml");

    if (configFile.exists()) {
      System.out.println("Plik konfiguracyjny istnieje");
      return 1;
    } else {
      try {
        configFile.createNewFile();
        guiConfig = true;
        return 0;
      } catch (IOException ex) {
        System.out.println("Nie można utworzyć pliku konfiguracji, ");
        Logger.getLogger(SettingsManager.class.getName()).log(Level.SEVERE, null, ex);
        return -1;
      }
    }
  }

  /**
   * Odczytuje z pliku konfigruacyjnego ścieżkę do pliku z rejestrem łożysk.
   *
   * @param tfBearingRegistryPath
   * @throws FileNotFoundException
   * @throws XMLStreamException
   */
  public void readBearingRegistryPath(JTextField tfBearingRegistryPath) throws
          FileNotFoundException, XMLStreamException {

    String configFile = System.getProperty("user.home") + "\\materialManager\\config.xml";

    // First, create a new XMLInputFactory
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    // Setup a new eventReader
    InputStream in = new FileInputStream(configFile);
    XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

    while (eventReader.hasNext()) {
      XMLEvent event = eventReader.nextEvent();

      if (event.isStartElement()) {
        if (event.asStartElement().getName().getLocalPart()
                .equals("bearingRegistry")) {
          event = eventReader.nextEvent();
          tfBearingRegistryPath.setText(event.asCharacters().getData());
          System.out.println("Ścieżka do pliku: " + event.asCharacters().getData());
        }
      }
    }
  }

  /**
   * Ustawia ścieżkę do pliku rejestru łożysk.
   *
   * @param tfBearingRegistryPath Pole w ustawieniach z ścieżką do pliku
   * rejestru łożysk
   */
  public void setupBearingRegistryPath(JTextField tfBearingRegistryPath) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setCurrentDirectory(new java.io.File("."));
    fileChooser.setDialogTitle("Wybierz folder");
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setAcceptAllFileFilterUsed(false);

    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      String sciezka;
      sciezka = fileChooser.getSelectedFile().toString().replaceAll("\\\\", "/");
      tfBearingRegistryPath.setText(sciezka);
    }
  }

  /**
   * Zapisuje w pliku konfigruacyjnym ścieżkę do pliku z rejestrem łożysk
   *
   * @param tfBearingRegistryPath Pole zawierające ścieżke do pliku z rejestrem.
   * @throws XMLStreamException
   * @throws FileNotFoundException
   */
  public void saveBearingRegistryPath(JTextField tfBearingRegistryPath) throws
          XMLStreamException, FileNotFoundException {

    String configFile = System.getProperty("user.home") + "\\materialManager\\config.xml";

    // create an XMLOutputFactory
    XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
    // create XMLEventWriter
    XMLEventWriter eventWriter;
    eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream(configFile));

    // create an EventFactory
    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    XMLEvent end = eventFactory.createDTD("\n");
    // create and write Start Tag
    StartDocument startDocument = eventFactory.createStartDocument();
    eventWriter.add(startDocument);

    // create config open tag
    StartElement configStartElement = eventFactory.createStartElement("",
            "", "config");
    eventWriter.add(configStartElement);
    eventWriter.add(end);
    // Write the different nodes
    createNode(eventWriter, "bearingRegistry", tfBearingRegistryPath.getText());

    eventWriter.add(eventFactory.createEndElement("", "", "config"));
    eventWriter.add(end);
    eventWriter.add(eventFactory.createEndDocument());
    eventWriter.close();
  }

  /**
   * Tworzy węzeł w pliku XML
   *
   * @param eventWriter Interfejs do zapisu XML
   * @param name Nazwa węzła
   * @param value Wartość węzła
   * @throws XMLStreamException
   */
  private void createNode(XMLEventWriter eventWriter, String name,
          String value) throws XMLStreamException {

    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    XMLEvent end = eventFactory.createDTD("\n");
    XMLEvent tab = eventFactory.createDTD("\t");
    // create Start node
    StartElement sElement = eventFactory.createStartElement("", "", name);
    eventWriter.add(tab);
    eventWriter.add(sElement);
    // create Content
    Characters characters = eventFactory.createCharacters(value);
    eventWriter.add(characters);
    // create End node
    EndElement eElement = eventFactory.createEndElement("", "", name);
    eventWriter.add(eElement);
    eventWriter.add(end);

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
