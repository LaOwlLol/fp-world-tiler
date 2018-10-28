package fauxpas.apps;

import fauxpas.entities.FilterableImage;
import fauxpas.filters.*;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.io.*;


public class Depth extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        FilterableImage depth = new FilterableImage (800, 600);

        ImageView view = new ImageView(depth.getImage());
        view.setPreserveRatio(true);
        root.getChildren().add(view);

        HBox buttonBar = new HBox();

        //primaryStage.minWidthProperty().bind(view.fitWidthProperty());
        //primaryStage.minHeightProperty().bind(root.heightProperty());

        //primaryStage.maxWidthProperty().bind(view.fitWidthProperty());
        //primaryStage.maxHeightProperty().bind(root.heightProperty());

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

        Button gray = new Button("Grayscale");
        gray.setOnMouseClicked((event) -> {
            depth.applyFilter(new GrayscaleFilter());
            view.setImage(depth.getImage());

        });
        buttonBar.getChildren().add(gray);
        buttonBar.setMargin( gray, new Insets(5, 5, 5, 5));


        Button edge = new Button("Edges");
        edge.setOnMouseClicked((event) -> {
            depth.applyFilter(new SobelFilter());
            view.setImage(depth.getImage());

        });
        buttonBar.getChildren().add(edge);
        buttonBar.setMargin( edge, new Insets(5, 5, 5, 5));


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
                if (depth.getImage().getHeight() > Screen.getPrimary().getVisualBounds().getHeight()) {
                    view.setFitHeight(Screen.getPrimary().getVisualBounds().getHeight() - buttonBar.getHeight());

                }
                else {
                    view.setFitHeight(depth.getImage().getHeight() );

                }
                /*primaryStage.setWidth(Math.max( view.getFitWidth(), buttonBar.getWidth() ));
                primaryStage.setHeight(root.getHeight());
                primaryStage.sizeToScene();*/
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
            else {
                System.out.println("File selection for save image returned null!");
            }
        });
        buttonBar.getChildren().addAll(saveImage);
        buttonBar.setMargin( saveImage, new Insets(5, 5, 5, 5));


        root.getChildren().add(buttonBar);

        //primaryStage.minWidthProperty().bind(root.widthProperty());
        //primaryStage.minWidthProperty().bind(root.heightProperty());
        primaryStage.setTitle("Depth visual test");
        primaryStage.sizeToScene();
        primaryStage.show();

        buttonBar.setMaxSize(buttonBar.getWidth(), buttonBar.getHeight());
        buttonBar.setPrefSize(buttonBar.getWidth(), buttonBar.getHeight());
        buttonBar.setMinSize(buttonBar.getWidth(), buttonBar.getHeight());
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void writeImageToFile(Image img, File file) {
        String extension = FilenameUtils.getExtension(file.getPath());

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), extension, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
