/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyClasses;

/**
 *
 * @author wakan
 */
public class PopupManager {
    
    
    
        
    private PopupManager() {        
        
    }
    
    public static PopupManager getInstance() {
        return PopupManagerHolder.INSTANCE;
    }
    
    private static class PopupManagerHolder {

        private static final PopupManager INSTANCE = new PopupManager();
    }
}
