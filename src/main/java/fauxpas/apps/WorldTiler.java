package fauxpas.apps;

import fauxpas.collections.TileImageDirectory;
import fauxpas.components.TileEditor;
import fauxpas.entities.Tile;
import fauxpas.entities.World;
import fauxpas.views.MiniMapWorldView;
import fauxpas.views.ScrollableWorldView;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.nio.file.Paths;


public class WorldTiler extends Application {

    private static final int VIEW_WIDTH_TILES = 8;
    private static final int VIEW_HEIGHT_TILES = 5;

    private TileEditor editor;
    private MiniMapWorldView miniMap;

    public WorldTiler() {

        int tile_Dim = 75;

        //define some tiles.
        Tile blank = new Tile("water",
              Paths.get(System.getProperty("user.home"), "WorldTiler", "water", "water_0.png" ).toString());
        Tile grass = new Tile ("grass",
              Paths.get(System.getProperty("user.home"), "WorldTiler", "grass", "grass_0.png" ).toString());

        //construct a new assets directory with dims matching local assets.
        TileImageDirectory assets = TileImageDirectory.LoadFromFileSystem(Paths.get(System.getProperty("user.home"),
              "WorldTiler").toString(), tile_Dim, true);

        //map tiles to image assets.
        /*assets.map(blank,
                new Image( Paths.get(System.getProperty("user.home"),
                      "WorldTiler", "water", "water_0.png" ).toUri().toString() )
        );
        assets.map(grass,
              new Image( Paths.get(System.getProperty("user.home"),
                    "WorldTiler", "grass", "grass_0.png" ).toUri().toString() )
        );*/

        //construct a world with blank tile.
        World world = new World(30,30, blank);

        //set a tile to grass.
        world.setTile(1,0, grass);

        for (int j = 15; j < 25; ++j) {
            for (int i = 15; i < 25; ++i) {
                world.setTile(i, j, grass);
            }
        }

        //construct a view on the world using the asset set.
        ScrollableWorldView view = new ScrollableWorldView(0,0, VIEW_WIDTH_TILES, VIEW_HEIGHT_TILES, world, assets);

        //construct the canvas view will draw on.
        Canvas canvas = new Canvas(tile_Dim*VIEW_WIDTH_TILES,tile_Dim*VIEW_HEIGHT_TILES);

        this.editor = new TileEditor(world, assets, view, canvas);

        this.miniMap = new MiniMapWorldView(0, 0, 200, 125, world, assets);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("World-Tiler");
        AnchorPane root = new AnchorPane();

        root.getChildren().add(editor.getCanvas());
        root.setLeftAnchor(editor.getCanvas(), 10.0);

        this.editor.startRender();
        this.editor.initEventHandlers();

        Canvas miniCanvas = new Canvas(200, 125);

        root.getChildren().add(miniCanvas);
        root.setLeftAnchor(miniCanvas, 10.0);
        root.setBottomAnchor(miniCanvas, 10.0);

        AnimationTimer animTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                miniCanvas.getGraphicsContext2D().clearRect(0,0,200, 125);
                miniMap.render(miniCanvas.getGraphicsContext2D());
            }
        };

        animTimer.start();

        Scene scene = new Scene(root, 1024, 768);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
