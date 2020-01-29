package com.kprm.materialmanager;

import MyClasses.MmComparators;
import MyClasses.MmMutableTreeNode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * Klasa do zarządzania kontraktami.
 *
 * @author Łukasz Wawrzyniak
 */
public class NodeManager {

    // Informuje o koniecznosci odświeżenia zawratości drzewa z kontraktami
    // w zakładce materiały.
    public static boolean treeKontraktyMaterialyRead = true;

    // Zmienna przechowująca ścieżkę węzła z metody getNodePath
    private String nodePath;

    private NodeManager() {
    }

    public static NodeManager getInstance() {
        return NodeManagerHolder.INSTANCE;
    }

    private static class NodeManagerHolder {

        private static final NodeManager INSTANCE = new NodeManager();
    }

    /**
     * Dodaje nowy węzeł do drzewa węzłów.
     *
     * @param tfNazwaWezla Pole do którego wpisana jest nazwa nowego węzła.
     * @param treeKontrakty Drzewo węzłów.
     */
    public void addNode(JTextField tfNazwaWezla, JTree treeKontrakty) {
        DefaultTreeModel model = (DefaultTreeModel) treeKontrakty.getModel();

        // Sprawdzenie czy zaznaczony węzeł w drzewku jest głównym węzłem.
        // Wymusza to stworzenie obiektu odpowiedniej klasy określającego 
        // Zaznaczony węzeł drzewa. Obiekt klasy DefaultMutableTreeNode nie 
        // posiada pól identyfikujących id oraz id_rodzica z bazy danych.
        if (((DefaultMutableTreeNode) treeKontrakty.getLastSelectedPathComponent()).isRoot()) {

            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeKontrakty.getLastSelectedPathComponent();

            // ID rodzica, w bazie danych jest to pierwszy rekord - rodzic wszystkich
            // późniejszych węzłów, musi się równać = 1.
            int parentID = 1;

            if (selectedNode != null) {
                if (!tfNazwaWezla.getText().trim().equals("")) {
                    MmMutableTreeNode newNode = new MmMutableTreeNode(tfNazwaWezla.getText(), parentID);
                    model.insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());

                    newNode.setId(DatabaseManager.getInstance().addNode(tfNazwaWezla.getText(), parentID));
                }
            }
        } else {

            // Obiekt tej klasy posiada pola umożliwiające zapisanie informacji
            // o id, id_rodzica wymagane do identyfikacji zaznaczonego węzła w
            // drzewku JTree.
            MmMutableTreeNode selectedNode = (MmMutableTreeNode) treeKontrakty.getLastSelectedPathComponent();

            // ID rodzica, w bazie danych jest to pierwszy rekord - rodzic wszystkich
            // późniejszych węzłów, musi się równać = 1.
            int parentID = selectedNode.getId();

            if (selectedNode != null) {
                if (!tfNazwaWezla.getText().trim().equals("")) {
                    MmMutableTreeNode newNode = new MmMutableTreeNode(tfNazwaWezla.getText(), parentID);
                    model.insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());

                    newNode.setId(DatabaseManager.getInstance().addNode(tfNazwaWezla.getText(), parentID));
                }
            }
        }
    }

    /**
     * Odświeża zawartość drzewa węzłów (kontraktów).
     *
     * @param treeKontrakty Drzewo węzłów.
     */
    public void refresthNodesTree(JTree treeKontrakty) {
        DefaultTreeModel model = (DefaultTreeModel) treeKontrakty.getModel();
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) model.getRoot();

        // Wyczyszczenie zawartości drzewka.
        selectedNode.removeAllChildren();
        model.reload();

        ResultSet resultSet = DatabaseManager.getInstance().getNodes();

        // Lista z weżłami których rodzicem jest węzeł główny.
        // Lista utworzona w celu posortowania węzłów.
        ArrayList<MmMutableTreeNode> mainNodes = new ArrayList<>();

        try {
            /*
             * W pierwszej kolejności wyszukane zostają wszystkie węzły, których
             * rodzicem jest węzeł 1 (kontrakty)
             */
            resultSet.first();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nazwa = resultSet.getString("nazwa");
                int rodzic = resultSet.getInt("rodzic");

                if (rodzic == 1) {
                    MmMutableTreeNode newNode = new MmMutableTreeNode(nazwa, rodzic, id);
                    mainNodes.add(newNode);

                }
            }

            // Sortowanie zawartości listy z głównymi węzłami.
            Collections.sort(mainNodes, MmComparators.treeNodeComparator);

            // Wypałnienie drzewka węzłąmi.
            for (MmMutableTreeNode node : mainNodes) {
                model.insertNodeInto(node, selectedNode, selectedNode.getChildCount());
            }

            /* Lista przechoująca węzły kontraktów */
            ArrayList<MmMutableTreeNode> contractsNodes = new ArrayList<>();

            /* Dla każdego głównego węzła (rodzicem jest węzeł 'KONTRAKTY')
                następuje odszukanie węzła którego rodzicem jest ów główny
                węzeł. Znaleziony węzeł dodawany jest do listy tymczasowej, 
                która po zakończeniu pętli jest sortowana. Posortowane węzły z
                listy tymczasowej dodawane są jako dzieci do odpowiadającego im 
                w drzewku głównego węzła.
                Dodatkowo znalezione węzły dodawane są do listy kontraktów
                w celu wyeliminowania ich z późniejszego szukania.
             */
            for (MmMutableTreeNode node : mainNodes) {
                ArrayList<MmMutableTreeNode> tmpNodes = new ArrayList<>();

                resultSet.first();

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String nazwa = resultSet.getString("nazwa");
                    int idRodzica = resultSet.getInt("rodzic");

                    if (idRodzica != 1) {
                        if (node.getId() == idRodzica) {
                            MmMutableTreeNode newNode = new MmMutableTreeNode(nazwa, idRodzica, id);
                            tmpNodes.add(newNode);
                            contractsNodes.add(newNode);
                        }
                    }
                }

                Collections.sort(tmpNodes, MmComparators.treeNodeComparator);

                for (MmMutableTreeNode node2 : tmpNodes) {
                    MmMutableTreeNode parentNode = getParentNode(model, node2.getParentId());
                    model.insertNodeInto(node2, parentNode, parentNode.getChildCount());
                }
            }

            /*
                Ponowne przeszukanie wyników i dodanie pozostałych węzłów które
                nie są węzłem 'KONTRAKTY', jednym z węzłów głównych, węzłem
                kontraktu do drzewka.
             */
            resultSet.first();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nazwa = resultSet.getString("nazwa");
                int idRodzica = resultSet.getInt("rodzic");

                if (idRodzica != 1) {
                    if (!isParentIdExist(contractsNodes, id)) {
                        MmMutableTreeNode newNode = new MmMutableTreeNode(nazwa, idRodzica, id);
                        MmMutableTreeNode parentNode = getParentNode(model, idRodzica);
                        model.insertNodeInto(newNode, parentNode, parentNode.getChildCount());
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(NodeManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sprawdza czy podany Id znajduje się w jednym z węzłów z listy.
     *
     * @param contractNodes Lista zawierająca węzły kontraktów
     * @param id Id węzła
     * @return Zwraca TRUE jeżeli podany ID węzła znajduje się w liście z
     * węzłami kontraktów, FALSE jeżeli nie.
     */
    private boolean isParentIdExist(ArrayList<MmMutableTreeNode> contractNodes, int id) {
        for (MmMutableTreeNode node : contractNodes) {
            if (node.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Funkcja zwraca węzeł rodzica.
     *
     * @param tree Drzewko JTree w którym ma nastąpić poszukiwanie węzła rodzica
     * @param parentId Numer ID rodzica z bazy danych.
     * @return Zwraca węzeł rodzica.
     */
    private MmMutableTreeNode getParentNode(DefaultTreeModel tree, int parentId) {

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getRoot();
        return printNode(root, parentId);
    }

    /**
     * Funkcja przeszukuje wszystkie węzły-dzieci w zadanym parametrem węźle.
     * Jeżeli ID aktualnie sprawdzanego węzła będzie takie samo jak ID rodzica
     * zadanego parametrem, funkcja zwróci sprawdzany węzeł jako węzeł-rodzic
     * Jeżeli sprawdzany węzeł będzie miał w sobie węzły podrzędne(dzieci)
     * funkcja wywoła samą siebie. Uwaga! Wywołanie niniejszej funkcji następuje
     * tylko dla pierwszego węzła tj. Kontrakty.
     *
     * @param node Węzeł do sprawdzenia
     * @param parentId Id rodzica
     * @return Zwraca węzła rodzica.
     */
    public MmMutableTreeNode printNode(DefaultMutableTreeNode node, int parentId) {

        int childCount = node.getChildCount();

        //System.out.println("PN1 -> " + " NODE_NAME: " + node.toString());
        for (int i = 0; i < childCount; i++) {
            MmMutableTreeNode childNode = (MmMutableTreeNode) node.getChildAt(i);
            if (childNode.getId() == parentId) {
                return childNode;
            } else if (childNode.getChildCount() > 0) {
                if (printNode(childNode, parentId) != null) {
                    return printNode(childNode, parentId);
                }
            }
        }
        return null;
    }

    /**
     * Funkcja przeszukuje wszystkie węzły-dzieci w zadanym parametrem węźle.
     * Jeżeli ID aktualnie sprawdzanego węzła będzie takie samo jak ID rodzica
     * zadanego parametrem, funkcja zwróci sprawdzany węzeł jako węzeł-rodzic
     * Jeżeli sprawdzany węzeł będzie miał w sobie węzły podrzędne(dzieci)
     * funkcja wywoła samą siebie.
     *
     * @param node Węzeł do sprawdzenia.
     * @param parentId Id rodzica.
     * @return Zwraca węzeł rodzica.
     */
    public MmMutableTreeNode printNode(MmMutableTreeNode node, int parentId) {

        int childCount = node.getChildCount();

        //System.out.println("PN2 -> " + "NODE_ID: " + node.getId() + " NODE_PARENT_ID: " + node.getParentId() + " NODE_NAME: " + node.toString());
        for (int i = 0; i < childCount; i++) {
            MmMutableTreeNode childNode = (MmMutableTreeNode) node.getChildAt(i);
            //System.out.println("PN2.1 -> " + "CHILD_NODE_ID " + childNode.getId() + " CHILD_NODE_PARENT_ID: " + childNode.getParentId() + " CHILD_NODE_NAME: " + childNode.toString());
            if (childNode.getId() == parentId) {
                //System.out.println("PN2.1.1 -> FOUND");
                return childNode;
            } else if (childNode.getChildCount() > 0) {
                if (printNode(childNode, parentId) != null) {
                    //System.out.println("PN2.1.2 -> FOUND");
                    //return childNode;
                    return printNode(childNode, parentId);
                }
            }
        }
        return null;
    }

    /**
     * Zmienia nazwę węzła.
     *
     * @param tree Drzewko węzłów
     */
    public void renameNode(JTree tree) {
        // Sprawdza czy zaznaczono jakikolwiek węzeł
        if (tree.getLastSelectedPathComponent() != null) {

            // Sprawdza czy węzeł jest węzłem głównym
            if (((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).isRoot()) {
                JOptionPane.showMessageDialog(null, "Nie można zmienić nazwy głównego węzła.");
            } else {
                MmMutableTreeNode selectedNode = (MmMutableTreeNode) tree.getLastSelectedPathComponent();

                String name = JOptionPane.showInputDialog(null, "Podaj nową nazwę węzła");

                if (name != null && !name.isEmpty()) {
                    if (DatabaseManager.getInstance().renameNode(name, selectedNode.getId()) == 1) {
                        selectedNode.setUserObject(name);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Zaznacz węzeł.");
        }
    }

    /**
     * Usuwa węzeł.
     *
     * @param tree Drzewko węzłów.
     */
    public void removeNode(JTree tree) {
        // Sprawdza czy zaznaczono jakikolwiek węzeł
        if (tree.getLastSelectedPathComponent() != null) {

            // Sprawdza czy węzeł jest węzłem głównym
            if (((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).isRoot()) {
                JOptionPane.showMessageDialog(null, "Nie można usunąć głównego węzła.");
            } else {

                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                MmMutableTreeNode selectedNode = (MmMutableTreeNode) tree.getLastSelectedPathComponent();

                if (DatabaseManager.getInstance().removeNode(selectedNode.getId()) == 1) {
                    TreePath[] paths = tree.getSelectionPaths();
                    if (paths != null) {
                        for (TreePath path : paths) {
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                            if (node.getParent() != null) {
                                model.removeNodeFromParent(node);
                            }
                        }
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Zaznacz węzeł.");
        }
    }

    /**
     * Zwraca pełną ścieżkę zadanego węzła.
     *
     * @param nodeId Id węzła
     * @return Nazwa węzła
     */
    public String getNodePath(int nodeId) {

        try {
            ResultSet rs = DatabaseManager.getInstance().getNodeName(nodeId);

            if (DatabaseManager.getInstance().getSizeOfResuleSet(rs) > 0) {
                rs.first();

                nodePath += rs.getString("nazwa") + " -> ";

                if (rs.getInt("rodzic") > 1) {
                    NodeManager.getInstance().getNodePath(rs.getInt("rodzic"));
                }

                return nodePath;
            }

        } catch (SQLException ex) {
            Logger.getLogger(NodeManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Czyści pełną ścieżke do węzła
     */
    public void clearNodePath(){
        nodePath = "";
    }
}
