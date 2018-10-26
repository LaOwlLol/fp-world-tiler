package fauxpas.apps;

import fauxpas.entities.DepthMap;
import fauxpas.filters.*;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;


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

        Button filter = new Button("Filter");
        filter.setOnMouseClicked((event) -> {
            depth.applyFilter(new GaussianBlur(3, 3));
            view.setImage(depth.getImage());

        });
        buttonBar.getChildren().add(filter);

        Button generate = new Button("Generate");
        generate.setOnMouseClicked((event) -> {
            depth.applyFilter(new PerlinNoise());
            view.setImage(depth.getImage());
        });
        buttonBar.getChildren().add(generate);

        Button blend = new Button("Blend");
        blend.setOnMouseClicked((event) -> {
            depth.applyFilter( new SumFilter(1.0, 0.25).apply(
                  new SumFilter(1, 0.5).apply(new RedistributionFilter(1.0), new SimplexNoise(2) ),
                  new WhiteNoise()
            ));
            depth.applyFilter(new RedistributionFilter(1.3 ));
            view.setImage(depth.getImage());
        });
        buttonBar.getChildren().add(blend);

        Button redistribute = new Button("Flatten");
        redistribute.setOnMouseClicked((event) -> {
            depth.applyFilter(new RedistributionFilter(1.3 ));
            view.setImage(depth.getImage());
        });
        buttonBar.getChildren().add(redistribute);

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
