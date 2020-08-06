/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyClasses;

import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;

/**
 * Klasa zawierająca metody używane wielokrotnie w innych klasach.
 *
 * @author Łukasz Wawrzyniak
 */
public class Utilities {

  /**
   * Zwraca listę wszystkich węzłów z zadanym drzewku węzłów
   *
   * @param tree Drzewko węzłów
   * @return Lista nazw węzłów (String)
   */
  public static ArrayList<String> visitAllNodes(JTree tree) {
    TreeNode root = (TreeNode) tree.getModel().getRoot();
    ArrayList<String> nodeList = new ArrayList<>();
    nodeList = visitAllNodes(root, nodeList);
    return nodeList;
  }

  public static ArrayList<String> visitAllNodes(TreeNode node, ArrayList<String> nodeList) {
    //System.out.println(node);
    if (node.getChildCount() >= 0) {
      for (Enumeration e = node.children(); e.hasMoreElements();) {
        TreeNode n = (TreeNode) e.nextElement();
        nodeList.add(n.toString());
        visitAllNodes(n, nodeList);
      }
    }
    return nodeList;
  }

  // Function to remove duplicates from an ArrayList 
  public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {

    // Create a new ArrayList 
    ArrayList<T> newList = new ArrayList<T>();

    // Traverse through the first list 
    for (T element : list) {

      // If this element is not present in newList 
      // then add it 
      if (!newList.contains(element)) {

        newList.add(element);
      }
    }

    // return the new list 
    return newList;
  }
}
