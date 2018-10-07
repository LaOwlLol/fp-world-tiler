package fauxpas.apps;

import fauxpas.collections.TileImageDirectory;
import fauxpas.components.AssetPalette;
import fauxpas.components.EditorMiniMap;
import fauxpas.components.TileEditor;
import fauxpas.entities.Tile;
import fauxpas.entities.World;
import fauxpas.views.MiniMapWorldView;
import fauxpas.views.ScrollableWorldView;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Paths;


public class WorldTiler extends Application {

    private static final int VIEW_WIDTH_TILES = 8;
    private static final int VIEW_HEIGHT_TILES = 5;

    private TileEditor editor;
    private EditorMiniMap miniMap;
    private AssetPalette palette;

    private Tile blank;
    private TileImageDirectory assets;

    public WorldTiler() {

        int tile_Dim = 75;

        //define some tiles.
        this.blank = new Tile("water",
              Paths.get(System.getProperty("user.home"), "WorldTiler", "water", "water_0.png" ).toString());
        Tile grass = new Tile ("grass",
              Paths.get(System.getProperty("user.home"), "WorldTiler", "grass", "grass_0.png" ).toString());

        //construct a new assets directory with dims matching local assets.
        this.assets = TileImageDirectory.LoadFromFileSystem(Paths.get(System.getProperty("user.home"),
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

        World world;

        world = World.ReadFromFile(
              Paths.get(System.getProperty("user.home"), "WorldTiler", "world_1").toString()).orElseGet(
              () -> {

                  System.out.println("Failed to load world from file!");

                  //construct a world with blank tile.
                  World w = new World(300,300, this.blank);

                  //set a tile to grass.
                  w.setTile(1,0, grass);

                  for (int j = 15; j < 25; ++j) {
                      for (int i = 15; i < 25; ++i) {
                          w.setTile(i, j, grass);
                      }
                  }
                  return w;
              }
        );

        //World.WriteToFile(world,  Paths.get(System.getProperty("user.home"), "WorldTiler", "world_1").toString() );

        //construct a ScrollableWorldView for the TileEditor.
        ScrollableWorldView scrollableView = new ScrollableWorldView(0,0, VIEW_WIDTH_TILES, VIEW_HEIGHT_TILES, world, assets);

        //construct the Canvas that scrollableView will draw on.
        Canvas scrollCanvas = new Canvas(tile_Dim*VIEW_WIDTH_TILES,tile_Dim*VIEW_HEIGHT_TILES);

        //create the editor component
        this.editor = new TileEditor(world, assets, scrollableView, scrollCanvas);

        //construct a MiniMapWorldView for an over view of the world under construction.
        MiniMapWorldView miniMapView = new MiniMapWorldView(0, 0, VIEW_WIDTH_TILES, VIEW_HEIGHT_TILES, world, assets);

        //construct the Canvas that miniMapView will draw on.
        Canvas miniMapCanvas = new Canvas(600, 375);

        //register observer miniMapView to support in observable scrollableView.
        scrollableView.registerChangeListener(miniMapView);
        //turn on preview rendering of observed scrollView.
        miniMapView.setTrackScrollView(true);

        //construct the mini map component.
        this.miniMap = new EditorMiniMap(world, miniMapView, miniMapCanvas);

    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("World-Tiler");
        AnchorPane root = new AnchorPane();

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(5,5,5,5));
        this.palette = new AssetPalette(grid, this.assets, this.blank);
        this.palette.initLayout();
        this.palette.registerChangeListener(this.editor);

        root.getChildren().add(grid);
        root.setRightAnchor(grid, 10.0);
        root.setTopAnchor(grid, 10.0);

        root.getChildren().add(editor.getCanvas());
        root.setLeftAnchor(editor.getCanvas(), 10.0);

        this.editor.startRender();
        this.editor.initEventHandlers();

        root.getChildren().add(this.miniMap.getCanvas());
        root.setLeftAnchor(this.miniMap.getCanvas(), 10.0);
        root.setBottomAnchor(this.miniMap.getCanvas(), 10.0);

        this.miniMap.startRender();

        Scene scene = new Scene(root, 1024, 768);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
