package fauxpas.apps;

import fauxpas.entities.DepthMap;
import fauxpas.filters.GaussianBlur;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class Depth extends Application {

    @Override
    public void start(Stage primaryStage) {
        FlowPane root = new FlowPane();

        root.setOrientation(Orientation.VERTICAL);

        DepthMap depth = new DepthMap(new Image(Paths.get(System.getProperty("user.home"),
              "WorldTiler", "Height.png").toUri().toString()));

        ImageView view = new ImageView(depth.getImage());
        root.getChildren().add(view);

        Button filter = new Button("filter");
        filter.setOnMouseClicked((event) -> {
            depth.applyFilter(new GaussianBlur(3, 5));
            view.setImage(depth.getImage());
        });
        root.getChildren().add(filter);

        Scene scene = new Scene(root, depth.getImage().getWidth(), depth.getImage().getHeight()+35);
        primaryStage.setTitle("Depth visual test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
