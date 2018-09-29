package fauxpas.apps;

import fauxpas.collections.TileImageDirectory;
import fauxpas.components.TileEditor;
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

    private TileEditor editor;


    public WorldTiler() {

        int tile_Dim = 150;

        //define some tiles.
        Tile blank = new Tile(0,0);
        Tile grass = new Tile (1, 0);

        //construct a new assets directory with dims matching local assets.
        TileImageDirectory assets = new TileImageDirectory(tile_Dim);

        //map tiles to image assets.
        assets.map(blank,
                new Image( Paths.get(System.getProperty("user.home"),
                      "WorldTiler", "water", "water_0.png" ).toUri().toString() )
        );
        assets.map(grass,
              new Image( Paths.get(System.getProperty("user.home"),
                    "WorldTiler", "grass", "grass_0.png" ).toUri().toString() )
        );

        //construct a world with blank tile.
        World world = new World(10,10, blank);

        //set a tile to grass.
        world.setTile(1,0, grass);

        //construct a view on the world using the asset set.
        ScrollableWorldView view = new ScrollableWorldView(0,0, VIEW_WIDTH_TILES, VIEW_HEIGHT_TILES, world, assets);

        //construct the canvas view will draw on.
        Canvas canvas = new Canvas(tile_Dim*VIEW_WIDTH_TILES,tile_Dim*VIEW_HEIGHT_TILES);

        this.editor = new TileEditor(world, assets, view, canvas);

    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("World-Tiler");
        AnchorPane root = new AnchorPane();

        root.getChildren().add(editor.getCanvas());
        root.setLeftAnchor(editor.getCanvas(), 10.0);

        this.editor.startRender();

        Scene scene = new Scene(root, 1024, 768);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
