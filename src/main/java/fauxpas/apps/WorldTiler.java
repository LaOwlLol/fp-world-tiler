package fauxpas.apps;

import fauxpas.collections.TileImageDirectory;
import fauxpas.entities.Tile;
import fauxpas.entities.World;
import fauxpas.views.ScrollableWorldView;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.nio.file.Paths;


public class WorldTiler extends Application {

    private World world;
    private TileImageDirectory assets;
    private ScrollableWorldView view;
    Tile blank;

    public WorldTiler() {
        Tile blank = new Tile(1,3);
        this.assets = new TileImageDirectory(50);

        this.assets.addTile(blank,
                new Image( Paths.get(System.getProperty("user.home"), "WorldTiler", "temp1.png" ).toUri().toString() )
        );


        this.view = new ScrollableWorldView(0,0, 6,5);
        this.world = new World(10,10, blank);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("World-Tiler");
        AnchorPane root = new AnchorPane();

        Canvas worldMap = new Canvas(300, 250);

        Button sl = new Button("<-");
        sl.setOnAction( e -> {
            view.scrollX( -1 );
        } );

        Button sr = new Button("->");
        sr.setOnAction( e -> {
            view.scrollX( +1 );
        } );

        root.getChildren().add(worldMap);
        root.getChildren().add(sl);
        root.getChildren().add(sr);
        root.setLeftAnchor(worldMap, 10.0);
        root.setRightAnchor(sl, 10.0);
        root.setTopAnchor(sl, 10.0);
        root.setRightAnchor(sr, 10.0);
        root.setBottomAnchor(sr, 10.0);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                worldMap.getGraphicsContext2D().clearRect(0,0,300,250);
                view.render(worldMap.getGraphicsContext2D(), world, assets);
            }
        };

        timer.start();

        Scene scene = new Scene(root, 420, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
