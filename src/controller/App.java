package controller;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Prototyping stage
 * @author Andrew Pelletier
 *
 */
@SuppressWarnings("restriction")
public class App extends Application {

    private double imgSize = 300;
    private int rotate = 0;
    private String imgPath = "file:resources/skye.jpg";
    
    private ImageView imgView;
    private BorderPane borderPane;
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        ImageView imgView = new ImageView(new Image(imgPath, imgSize, imgSize, true, true));
        
        Button rotateLeftBtn = new Button("\u21BA");
        Button rotateRightBtn = new Button("\u21BB");
        rotateLeftBtn.setMinSize(30, 30);
        rotateRightBtn.setMinSize(30, 30);
        rotateLeftBtn.setFont(new Font(30));
        rotateRightBtn.setFont(new Font(30));
        
        rotateLeftBtn.setOnAction(e -> rotateLeft());
        rotateRightBtn.setOnAction(e -> rotateRight());
        
        
        VBox buttonVbox = new VBox(5);
        buttonVbox.getChildren().addAll(rotateLeftBtn, rotateRightBtn);
        
        borderPane = new BorderPane();
        borderPane.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        borderPane.setCenter(imgView);
        borderPane.setRight(buttonVbox);
        
        borderPane.setOnScroll(e -> handleScrollZoom(e));
        
        Scene root = new Scene(borderPane, 300, 300);
        
        stage.setScene(root);
        stage.show();
    }

    private void rotateRight() {
        rotate = (rotate + 90) % 360;
        drawImg();
    }
    
    private void rotateLeft() {
        rotate = (rotate - 90 + 360) % 360;
        drawImg();
    }

    private void handleScrollZoom(ScrollEvent event) {
        double scalingFactor = 0.12;
        if (event.getDeltaY() > 0) {
            imgSize *= 1 + scalingFactor;
        } else {
            imgSize *= 1 - scalingFactor;
        }
        drawImg();
    }
    
    private void drawImg() {
        Image img = new Image(imgPath, imgSize, imgSize, true, true);
        ImageView imgView = new ImageView(img);
        imgView.setRotate(rotate);
        
        borderPane.setCenter(imgView);
    }

}
