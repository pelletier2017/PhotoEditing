package view;

import controller.App;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MenuView extends MenuBar {
    
    private App controller;
    
    public MenuView(App controller) {
        super();
        
        this.controller = controller;
        
        Menu fileMenu = new Menu("File");
        MenuItem save = new MenuItem("Save");
        MenuItem load = new MenuItem("Load");
        
        save.setOnAction(e -> controller.saveToFileClicked(e));
        load.setOnAction(e -> controller.loadFromFileClicked(e));
        
        fileMenu.getItems().addAll(save, load);
        
        Menu filterMenu = new Menu("Filter");
        MenuItem blur = new MenuItem("Blur");
        filterMenu.getItems().addAll(blur);
        
        getMenus().addAll(fileMenu, filterMenu);
        
    }
    
}
