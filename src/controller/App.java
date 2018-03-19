package controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import view.ControlView;
import view.MenuView;

/**
 * Prototyping stage
 * 
 * @author Andrew Pelletier
 *
 */
@SuppressWarnings("restriction")
public class App extends Application {

    // view properties
    private int canvasSize = 900;
    
    // view components
    private Image img;
    private BorderPane borderPane;
    private Canvas canvas;
    private GraphicsContext gc;
    private Stage stage;
    
    // state of canvas
    private boolean clickedOnImage = false;
    private double clickX;
    private double clickY;
    private double oldOriginX;
    private double oldOriginY;
    
    // image properties
    private String imgPath = "resources/skye.jpg";
    private double imgSize = 600;
    private Double originX;
    private Double originY;      
    private int rotateDeg;
    private ColorAdjust colorAdjust = new ColorAdjust();
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        this.stage = stage;
        
        ControlView controlView = new ControlView(this);

        borderPane = new BorderPane();
        setBackground(borderPane, Color.BLACK);
        borderPane.setRight(controlView);
        
        borderPane.setOnScroll(e -> handleScrollZoom(e));

        canvas = new Canvas(canvasSize, canvasSize);
        
        setupCanvasMouseHandlers();
        
        
        borderPane.setCenter(canvas);
        borderPane.setTop(new MenuView(this));
        
        gc = canvas.getGraphicsContext2D();
        
        Scene root = new Scene(borderPane, canvasSize + 175, canvasSize);

        drawImage();
        setImageDefaults();
        
        stage.setTitle("PhotoStop");
        stage.setScene(root);
        stage.show();
    }

    private void setupCanvasMouseHandlers() {
        // event handlers
        
        canvas.setOnMouseDragged(e -> {
            if (clickedOnImage) {
                originX = oldOriginX + e.getX() - clickX;
                originY = oldOriginY + e.getY() - clickY;
                drawImage();
            }
        });
        
        canvas.setOnMousePressed(e -> {
            if (inImage(e.getX(), e.getY())) {
                clickedOnImage = true;
                clickX = e.getX();
                clickY = e.getY();
                oldOriginX = originX;
                oldOriginY = originY;
            }
        });
        
        canvas.setOnMouseReleased(e -> {
            clickedOnImage = false;
        });
    }
    
    private boolean inImage(double x, double y) {
        boolean xInImage = x >= originX && x < originX + img.getWidth();
        boolean yInImage = y >= originY && y < originY + img.getHeight();
        return xInImage && yInImage;
    }

    public void handleBrightnessSlider(double newVal) {
        double oldVal = colorAdjust.getBrightness();
        
        // only redraw if significant enough change
        if (Math.abs(oldVal - newVal) > 0.001) {
            colorAdjust.setBrightness(newVal);
            drawImage();
        }
    }

    public void saveToFileClicked(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File to Save");
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif"));
        fileChooser.setInitialDirectory(new File("resources"));
        
        File selectedFile = fileChooser.showSaveDialog(stage);
        
        // null if dialog box canceled
        if (selectedFile != null) {
            String fname = selectedFile.getName();
            String fileExtension = fname.substring(fname.lastIndexOf(".") + 1);
            BufferedImage bImage = SwingFXUtils.fromFXImage(getHighQualityImage(), null);
            if (fileExtension.equals("jpg")) {
                bImage = convertToJpeg(bImage);
            } 
            
            try {
                ImageIO.write(bImage, fileExtension, selectedFile);
            } catch (IOException e1) {
                System.out.println("Failed to save file");
                e1.printStackTrace();
            }
        } else {
            System.out.println("canceled");
        }

    }

    private BufferedImage convertToJpeg(BufferedImage bImage) {
        BufferedImage rgbImage = new BufferedImage(bImage.getWidth(), bImage.getHeight(), BufferedImage.OPAQUE);
        Graphics2D graphics = rgbImage.createGraphics();
        
        graphics.drawImage(bImage, 0, 0, null);
        bImage = rgbImage;
        return bImage;
    }

    public void loadFromFileClicked(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File");
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif"));
        fileChooser.setInitialDirectory(new File("resources/"));
        
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        // null if dialog box canceled
        if (selectedFile != null) {
            System.out.println(selectedFile.getPath());
            imgPath = selectedFile.getPath();
            drawImage();
        }
        
        
    }

    private void setImageDefaults() {
        originX = topLeftX();
        originY = topLeftY();
        colorAdjust = new ColorAdjust();
        rotateDeg = 0;
        imgSize = 600;
    }

    public void rotateRight() {
        rotateDeg = (rotateDeg + 90) % 360;
        drawImage();
    }

    public void rotateLeft() {
        rotateDeg = (rotateDeg - 90 + 360) % 360;
        drawImage();
    }

    private void handleScrollZoom(ScrollEvent event) {
        
        double scalingFactor = 0.12;
        if (event.getDeltaY() > 0) {
            imgSize *= 1 + scalingFactor;
        } else {
            imgSize /= 1 + scalingFactor;
        }
        
        // minimum size
        if (imgSize < 100) {
            imgSize = 100;
        }
        drawImage();
    }
    
    private Image getHighQualityImage() {
        
        
        SnapshotParameters params = new SnapshotParameters();
        params.setViewport(new Rectangle2D(topLeftX(), topLeftY(), img.getRequestedWidth(), img.getRequestedHeight()));
        
        Image img = new Image("file:" + imgPath);
        ImageView imgView = new ImageView(img);
        Canvas hqCanvas = new Canvas(img.getWidth(), img.getHeight());
        GraphicsContext hqGC = hqCanvas.getGraphicsContext2D();
        hqGC.drawImage(img, 0, 0);
        
        imgView.setRotate(rotateDeg);
        imgView.setEffect(colorAdjust);
        
        Image snapshot = imgView.snapshot(null, null);

        return snapshot;
    }

    private double topLeftX() {
        if (rotateDeg == 0 || rotateDeg == 180) {
            return (canvas.getWidth() / 2) - (img.getWidth() / 2);
        } else {
            return (canvas.getHeight() / 2) - (img.getHeight() / 2);
        }
    }
    
    private double topLeftY() {
        if (rotateDeg == 0 || rotateDeg == 180) {
            return (canvas.getHeight() / 2) - (img.getHeight() / 2);
        } else {
            return (canvas.getWidth() / 2) - (img.getWidth() / 2);
        }
    }
    
    private void drawImage() {
        
        img = new Image("file:" + imgPath, imgSize, imgSize, true, true);
        if (originX == null || originY == null) {
            originX = topLeftX();
            originY = topLeftY();
        }
        ImageView imgView = new ImageView(img);
        imgView.setRotate(rotateDeg);
        imgView.setEffect(colorAdjust);
        
        Image snapshot = imgView.snapshot(null, null);
        
        // later add drawing only a small square
        clearCanvas();
        gc.drawImage(snapshot, originX, originY);
        
    }

    private void clearCanvas() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    private void setBackground(Region region, Color color) {
        region.setBackground(new Background(new BackgroundFill(color, null, null)));
    }

}
