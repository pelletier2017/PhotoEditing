package view;

import controller.App;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ControlView extends VBox {

    private App controller;
    
    public ControlView(App controller) {
        this.controller = controller;
        
        // rotate buttons
        Button rotateLeftBtn = new Button("\u21BA");
        Button rotateRightBtn = new Button("\u21BB");
        int buttonSize = 20;
        rotateLeftBtn.setMinSize(buttonSize, buttonSize);
        rotateRightBtn.setMinSize(buttonSize, buttonSize);
        rotateLeftBtn.setFont(new Font(buttonSize));
        rotateRightBtn.setFont(new Font(buttonSize));

        rotateLeftBtn.setOnAction(e -> controller.rotateLeft());
        rotateRightBtn.setOnAction(e -> controller.rotateRight());

        Slider brightnessSlider = new Slider(-1, 1, 0);
        brightnessSlider.setShowTickMarks(true);
        brightnessSlider.setShowTickLabels(true);
        brightnessSlider.setSnapToTicks(true);
        brightnessSlider.setMajorTickUnit(0.5);
        brightnessSlider.setBlockIncrement(0.1);
        brightnessSlider.valueProperty().addListener(e -> controller.handleBrightnessSlider(brightnessSlider.getValue()));
        
        setSpacing(5);
        setBackground(new Background(new BackgroundFill(Color.GREY, null, null)));
        setPadding(new Insets(10));
        getChildren().addAll(rotateLeftBtn, rotateRightBtn, new Label("Brightness"), brightnessSlider);
    }
    
    
    
}
