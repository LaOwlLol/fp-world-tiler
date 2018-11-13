package fauxpas.apps;

import fauxpas.components.ImageManipulationControls;
import fauxpas.entities.FilterableImage;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Depth extends Application {

    private Image lastImage;
    private ImageView secondaryViewer;
    private ImageView mainViewer;
    private FilterableImage main;
    private FilterableImage seconary;

    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();

        main = new FilterableImage(1024, 576);
        seconary = new FilterableImage( 1024, 576);

        VBox mainPane = new VBox();
        mainViewer = new ImageView(main.getImage());
        mainViewer.setFitWidth(1024);
        mainViewer.setFitHeight(576);
        mainViewer.setPreserveRatio(true);
        mainPane.getChildren().add(mainViewer);

        HBox mainBar = new HBox();
        ImageManipulationControls mainControls = new ImageManipulationControls(main);
        mainControls.init(mainViewer, mainBar, primaryStage);
        mainPane.getChildren().add(mainBar);

        /*VBox secondaryPane = new VBox();
        secondaryViewer = new ImageView(seconary.getImage());
        secondaryViewer.setFitWidth(480);
        secondaryViewer.setFitHeight(234);
        secondaryViewer.setPreserveRatio(true);
        secondaryPane.getChildren().add(secondaryViewer);

        VBox secondaryBar = new VBox();
        ImageManipulationControls secondaryControls = new ImageManipulationControls(seconary, Orientation.VERTICAL);
        secondaryControls.init(secondaryViewer, secondaryBar, primaryStage);
        secondaryPane.getChildren().add(secondaryBar);*/

        root.getChildren().add(mainPane);
        AnchorPane.setLeftAnchor(mainPane, 0.0);
        /*root.getChildren().add(secondaryPane);
        AnchorPane.setRightAnchor(secondaryPane, 0.0);*/

        /*Button mixImages = new Button("Blend");
        mixImages.setOnMouseClicked((event) -> {
            Thread process = new Thread(() -> {
                buttonBar.setDisable(true);
                lastImage = main.getImage();
                last.setDisable(false);
                main.applyFilter( new BlendFilter().apply( image -> image, image -> seconary.getImage() ) );
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
                main.applyFilter( new SumFilter(0.5, 0.5).apply( image -> image, image -> seconary.getImage() ));
                mainViewer.setImage(main.getImage());
                buttonBar.setDisable(false);
            });
            process.start();
        });
        buttonBar.getChildren().add(sumImages);
        HBox.setMargin( sumImages, new Insets(5, 5, 5, 5));*/

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

    private void refreshBlendImage(int width, int height) {
        this.seconary = new FilterableImage(width, height);
        this.secondaryViewer.setImage(seconary.getImage());
    }
}
