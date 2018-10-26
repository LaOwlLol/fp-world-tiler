package fauxpas.apps;

import fauxpas.entities.DepthMap;
import fauxpas.filters.*;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;


public class Depth extends Application {

    @Override
    public void start(Stage primaryStage) {
        FlowPane root = new FlowPane();
        root.setOrientation(Orientation.VERTICAL);

        DepthMap depth = new DepthMap(800, 600);

        ImageView view = new ImageView(depth.getImage());
        root.getChildren().add(view);

        FlowPane buttonBar = new FlowPane();
        buttonBar.setOrientation(Orientation.HORIZONTAL);

        Button generate = new Button("Generate");
        generate.setOnMouseClicked((event) -> {
            depth.applyFilter(new PerlinNoise());
            view.setImage(depth.getImage());
        });
        buttonBar.getChildren().add(generate);

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

        Button blur = new Button("Blur");
        blur.setOnMouseClicked((event) -> {
            depth.applyFilter(new GaussianBlur(3, 3));
            view.setImage(depth.getImage());

        });
        buttonBar.getChildren().add(blur);

        Button redistribute = new Button("Smooth");
        redistribute.setOnMouseClicked((event) -> {
            depth.applyFilter(new RedistributionFilter(1.3 ));
            view.setImage(depth.getImage());
        });
        buttonBar.getChildren().add(redistribute);

        Button selectImage = new Button("Load Image");
        selectImage.setOnMouseClicked((event) -> {
            FileChooser fileChooser = new FileChooser();
            File file =  fileChooser.showOpenDialog(primaryStage);
            depth.setImage(new Image(file.toURI().toString()));
            view.setImage(depth.getImage());
        });
        buttonBar.getChildren().addAll(selectImage);

        root.getChildren().add(buttonBar);
        Scene scene = new Scene(root, depth.getImage().getWidth(), depth.getImage().getHeight()+35);
        primaryStage.setTitle("Depth visual test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
