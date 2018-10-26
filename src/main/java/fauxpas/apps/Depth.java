package fauxpas.apps;

import fauxpas.entities.DepthMap;
import fauxpas.filters.*;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.*;


public class Depth extends Application {

    @Override
    public void start(Stage primaryStage) {
        FlowPane root = new FlowPane();
        root.setOrientation(Orientation.HORIZONTAL);

        DepthMap depth = new DepthMap(800, 600);

        ImageView view = new ImageView(depth.getImage());
        root.getChildren().add(view);


        HBox buttonBar = new HBox();

        Button generate = new Button("Generate");
        generate.setOnMouseClicked((event) -> {
            depth.applyFilter(new PerlinNoise());
            view.setImage(depth.getImage());
        });
        buttonBar.getChildren().add(generate);
        buttonBar.setMargin( generate, new Insets(5, 5, 5, 5));


        Button blendWithNoise = new Button("Blend Noise");
        blendWithNoise.setOnMouseClicked((event) -> {
            depth.applyFilter( new SumFilter(1.0, 0.1).apply(
                    new BlendFilter().apply(new RedistributionFilter(1.0), new SimplexNoise(2) ),
                    new WhiteNoise()
            ));
            depth.applyFilter(new RedistributionFilter(1.3 ));
            view.setImage(depth.getImage());
        });
        buttonBar.getChildren().add(blendWithNoise);
        buttonBar.setMargin( blendWithNoise, new Insets(5, 5, 5, 5));

        Button sumWithNoise = new Button("Sum Noise");
        sumWithNoise.setOnMouseClicked((event) -> {
            depth.applyFilter( new SumFilter(1.0, 0.25).apply(
                  new SumFilter(1, 0.5).apply(new RedistributionFilter(1.0), new SimplexNoise(2) ),
                  new WhiteNoise()
            ));
            depth.applyFilter(new RedistributionFilter(1.3 ));
            view.setImage(depth.getImage());
        });
        buttonBar.getChildren().add(sumWithNoise);
        buttonBar.setMargin( sumWithNoise, new Insets(5, 5, 5, 5));

        Button blur = new Button("Blur");
        blur.setOnMouseClicked((event) -> {
            depth.applyFilter(new GaussianBlur(3, 3));
            view.setImage(depth.getImage());

        });
        buttonBar.getChildren().add(blur);
        buttonBar.setMargin( blur, new Insets(5, 5, 5, 5));

        Button redistribute = new Button("Smooth");
        redistribute.setOnMouseClicked((event) -> {
            depth.applyFilter(new RedistributionFilter(1.3 ));
            view.setImage(depth.getImage());
        });
        buttonBar.getChildren().add(redistribute);
        buttonBar.setMargin( redistribute, new Insets(5, 5, 5, 5));

        Button selectImage = new Button("Load Image");
        selectImage.setOnMouseClicked((event) -> {
            FileChooser fileChooser = new FileChooser();
            File file =  fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                depth.setImage(new Image(file.toURI().toString()));
                view.setImage(depth.getImage());
            }
        });
        buttonBar.getChildren().addAll(selectImage);
        buttonBar.setMargin( selectImage, new Insets(5, 5, 5, 5));

        Button saveImage = new Button("Save Image");
        saveImage.setOnMouseClicked((event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image","*.png"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("bitmap Image, ","*.bmp"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG Image","*.jpg"));
            File file =  fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                writeImageToFile(depth.getImage(), file);
            }
        });
        buttonBar.getChildren().addAll(saveImage);
        buttonBar.setMargin( saveImage, new Insets(5, 5, 5, 5));

        root.getChildren().add(buttonBar);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.minWidthProperty().bind(root.widthProperty());
        primaryStage.minWidthProperty().bind(root.heightProperty());
        primaryStage.setTitle("Depth visual test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void writeImageToFile(Image img, File file) {
        String extension = "";

        int i = file.getName().lastIndexOf('.');
        int p = Math.max(file.getName().lastIndexOf('/'), file.getName().lastIndexOf('\\'));

        if (i > p) {
            extension = file.getName().substring(i+1);
        }

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), extension, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
