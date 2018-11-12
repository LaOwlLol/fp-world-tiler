package fauxpas.apps;

import fauxpas.entities.FilterableImage;
import fauxpas.filters.*;
import fauxpas.filters.noise.*;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.io.*;


public class Depth extends Application {

    Image lastImage;
    private ImageView blendViewer;
    private ImageView mainViewer;
    private FilterableImage main;
    private FilterableImage blend;

    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();

        main = new FilterableImage(1024, 576);
        blend = new FilterableImage( 1024, 576);

        mainViewer = new ImageView(main.getImage());
        mainViewer.setFitWidth(1024);
        mainViewer.setFitHeight(576);
        mainViewer.setPreserveRatio(true);
        root.getChildren().add(mainViewer);
        AnchorPane.setLeftAnchor(mainViewer, 1.0);
        AnchorPane.setTopAnchor(mainViewer, 1.0);

        blendViewer = new ImageView(blend.getImage());
        blendViewer.setFitWidth(480);
        blendViewer.setFitHeight(234);
        blendViewer.setPreserveRatio(true);
        root.getChildren().add(blendViewer);
        AnchorPane.setRightAnchor(blendViewer, 1.0);
        AnchorPane.setTopAnchor(blendViewer, 1.0);

        HBox buttonBar = new HBox();

        Button last = new Button("Undo");
        lastImage = main.getImage();
        last.setDisable(true);

        Button generate = new Button("Generate");
        generate.setOnMouseClicked((event) -> {
            Thread process = new Thread(() -> {
                buttonBar.setDisable(true);
                lastImage = main.getImage();
                last.setDisable(false);
                main.applyFilter(new ValueNoise(1.25f));
                mainViewer.setImage(main.getImage());
                buttonBar.setDisable(false);
            });
            process.start();
        });
        buttonBar.getChildren().add(generate);
        HBox.setMargin( generate, new Insets(5, 5, 5, 5));

        Button mixImages = new Button("Blend");
        mixImages.setOnMouseClicked((event) -> {
            Thread process = new Thread(() -> {
                buttonBar.setDisable(true);
                lastImage = main.getImage();
                last.setDisable(false);
                main.applyFilter( new BlendFilter().apply( image -> image, image -> blend.getImage() ) );
                mainViewer.setImage(main.getImage());
                buttonBar.setDisable(false);
            });
            process.start();
        });
        buttonBar.getChildren().add(mixImages);
        HBox.setMargin( mixImages, new Insets(5, 5, 5, 5));

        Button sumImages = new Button("Sum");
        sumImages.setOnMouseClicked((event) -> {
            Thread process = new Thread(() -> {
                buttonBar.setDisable(true);
                lastImage = main.getImage();
                last.setDisable(false);
                main.applyFilter( new SumFilter(0.5, 0.5).apply( image -> image, image -> blend.getImage() ));
                mainViewer.setImage(main.getImage());
                buttonBar.setDisable(false);
            });
            process.start();
        });
        buttonBar.getChildren().add(sumImages);
        HBox.setMargin( sumImages, new Insets(5, 5, 5, 5));

        Button gray = new Button("Grayscale");
        gray.setOnMouseClicked((event) -> {
            Thread process = new Thread(() -> {
                buttonBar.setDisable(true);
                lastImage = main.getImage();
                last.setDisable(false);
                main.applyFilter(new GrayscaleFilter());
                mainViewer.setImage(main.getImage());
                buttonBar.setDisable(false);
            });
            process.start();
        });
        buttonBar.getChildren().add(gray);
        HBox.setMargin( gray, new Insets(5, 5, 5, 5));

        Button cannyEdges = new Button("Canny Edges");
        cannyEdges.setOnMouseClicked((event) -> {
            Thread process = new Thread(() -> {
                buttonBar.setDisable(true);
                lastImage = main.getImage();
                last.setDisable(false);
                main.applyFilter(new SobelFilter(0.07, false, false));
                long t = System.currentTimeMillis();
                main.applyFilter(new CannyFilter(0.1, 0.45));
                System.out.println("Canny (only) processed in : " + (System.currentTimeMillis() - t) + " milliseconds.");
                mainViewer.setImage(main.getImage());
                buttonBar.setDisable(false);
            });
            process.start();
        });
        buttonBar.getChildren().add(cannyEdges);
        HBox.setMargin( cannyEdges, new Insets(5, 5, 5, 5));

        Button edge = new Button("Sobel Edges");
        edge.setOnMouseClicked((event) -> {
            Thread process = new Thread(() -> {
                buttonBar.setDisable(true);
                lastImage = main.getImage();
                last.setDisable(false);
                long t = System.currentTimeMillis();
                main.applyFilter(new SobelFilter(0.1, false, false));
                System.out.println("Sobel processed in : " + (System.currentTimeMillis() - t) + " milliseconds.");
                mainViewer.setImage(main.getImage());
                buttonBar.setDisable(false);
            });
            process.start();
        });
        buttonBar.getChildren().add(edge);
        HBox.setMargin( edge, new Insets(5, 5, 5, 5));

        Button blur = new Button("Blur");
        blur.setOnMouseClicked((event) -> {
            Thread process = new Thread(() -> {
                buttonBar.setDisable(true);
                lastImage = main.getImage();
                last.setDisable(false);
                long t = System.currentTimeMillis();
                main.applyFilter(new GaussianBlur(3, 10));
                System.out.println("Blur processed in : " + (System.currentTimeMillis() - t) + " milliseconds.");
                mainViewer.setImage(main.getImage());
                buttonBar.setDisable(false);
            });
            process.start();
        });
        buttonBar.getChildren().add(blur);
        HBox.setMargin( blur, new Insets(5, 5, 5, 5));

        Button redistribute = new Button("Sharpen");
        redistribute.setOnMouseClicked((event) -> {
            Thread process = new Thread(() -> {
                buttonBar.setDisable(true);
                lastImage = main.getImage();
                last.setDisable(false);
                main.applyFilter(new RedistributionFilter(1.2));
                mainViewer.setImage(main.getImage());
                buttonBar.setDisable(false);
            });
            process.start();
        });
        buttonBar.getChildren().add(redistribute);
        HBox.setMargin( redistribute, new Insets(5, 5, 5, 5));

        Button selectImage = new Button("Load Image");
        selectImage.setOnMouseClicked((event) -> {
            buttonBar.setDisable(true);
            lastImage = main.getImage();
            last.setDisable(false);
            FileChooser fileChooser = new FileChooser();
            File file =  fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                main.setImage(new Image(file.toURI().toString()));
                mainViewer.setImage(main.getImage());
                refreshBlendImage((int) main.getImage().getWidth(),(int) main.getImage().getHeight());
            }
            buttonBar.setDisable(false);
        });
        buttonBar.getChildren().add(selectImage);
        HBox.setMargin( selectImage, new Insets(5, 5, 5, 5));

        Button saveImage = new Button("Save Image");
        saveImage.setOnMouseClicked((event) -> {
            buttonBar.setDisable(true);
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image","*.png"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("bitmap Image, ","*.bmp"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG Image","*.jpg"));
            File file =  fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                writeImageToFile(main.getImage(), file);
            }
            else {
                System.out.println("File selection for save image returned null!");
            }
            buttonBar.setDisable(false);
        });
        buttonBar.getChildren().add(saveImage);
        HBox.setMargin( saveImage, new Insets(5, 5, 5, 5));

        last.setOnMouseClicked((event -> {
            Thread process = new Thread(() -> {
                buttonBar.setDisable(true);
                main.setImage(lastImage);
                mainViewer.setImage(lastImage);
                last.setDisable(true);
                buttonBar.setDisable(false);
            });
            process.start();
        }));
        buttonBar.getChildren().add(last);
        HBox.setMargin( last, new Insets(5, 5, 5, 5));

        buttonBar.setMaxSize( 1024 , 65);
        root.getChildren().add(buttonBar);
        AnchorPane.setBottomAnchor(buttonBar, 1.0);
        AnchorPane.setLeftAnchor(buttonBar, 1.0);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Depth visual test");
        primaryStage.setMinWidth(1024 + 480);
        primaryStage.setMaxWidth(1024 + 480);
        primaryStage.setMinHeight(576+65);
        primaryStage.setMaxHeight(576+65);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void writeImageToFile(Image img, File file) {
        String extension = FilenameUtils.getExtension(file.getPath());

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), extension, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshBlendImage(int width, int height) {
        this.blend = new FilterableImage(width, height);
        this.blendViewer.setImage(blend.getImage());
    }
}
