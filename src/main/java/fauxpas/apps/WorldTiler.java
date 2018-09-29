package fauxpas.apps;

import fauxpas.collections.TileImageDirectory;
import fauxpas.entities.Tile;
import fauxpas.entities.World;
import fauxpas.views.ScrollableWorldView;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.nio.file.Paths;


public class WorldTiler extends Application {

    private static final int VIEW_WIDTH_TILES = 4;
    private static final int VIEW_HEIGHT_TILES = 3;
    private static final int STATUS_HEIGHT = 70;

    private World world;
    private TileImageDirectory assets;
    private ScrollableWorldView view;
    private int tile_Dim;
    Tile blank;

    public WorldTiler() {

        tile_Dim = 150;

        //define some tiles.
        Tile blank = new Tile(1,0);
        Tile grass = new Tile (0, 0);

        //construct a new assets directory with dims matching local assets.
        this.assets = new TileImageDirectory(tile_Dim);

        //map tiles to image assets.
        this.assets.map(blank,
                new Image( Paths.get(System.getProperty("user.home"),
                      "WorldTiler", "water", "water_0.png" ).toUri().toString() )
        );
        this.assets.map(grass,
              new Image( Paths.get(System.getProperty("user.home"),
                    "WorldTiler", "grass", "grass_0.png" ).toUri().toString() )
        );

        //construct a world with blank tile.
        this.world = new World(10,10, blank);
        //set a tile to grass.
        this.world.setTile(1,0, grass);

        //construct a view on the world using the asset set.
        this.view = new ScrollableWorldView(0,0, VIEW_WIDTH_TILES, VIEW_HEIGHT_TILES, world, assets);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("World-Tiler");
        AnchorPane root = new AnchorPane();

        Canvas worldMap = new Canvas(tile_Dim*VIEW_WIDTH_TILES,tile_Dim*VIEW_HEIGHT_TILES + STATUS_HEIGHT);

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
                worldMap.getGraphicsContext2D().clearRect(0,0,tile_Dim*VIEW_WIDTH_TILES,tile_Dim*VIEW_HEIGHT_TILES + STATUS_HEIGHT);
                view.render(worldMap.getGraphicsContext2D());
                worldMap.getGraphicsContext2D().strokeText( Integer.toString(view.getX()) +
                      "(to "+ Integer.toString(view.getX()+VIEW_WIDTH_TILES)+")", 300,480);
                worldMap.getGraphicsContext2D().strokeText( ","+Integer.toString(view.getY()) +
                      "(to "+ Integer.toString(view.getY()+ VIEW_HEIGHT_TILES)+")", 345,480);
            }
        };

        timer.start();

        Scene scene = new Scene(root, 1024, 768);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
