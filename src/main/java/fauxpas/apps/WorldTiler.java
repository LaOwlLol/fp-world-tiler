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
    private static final double MINIMAP_HEIGHT_PIXELS = 375.0;
    private static final double MINIMAP_WIDTH_PIXALS = 600.0;

    private TileEditor editor;
    private EditorMiniMap miniMap;
    private AssetPalette palette;

    private Tile blank;
    private TileImageDirectory assets;
    private int tile_dim;
    private ScrollableWorldView scrollableView;
    private Canvas scrollCanvas;
    private World world;
    private MiniMapWorldView miniMapView;
    private Canvas miniMapCanvas;

    public WorldTiler() {

    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("World-Tiler");
        AnchorPane root = new AnchorPane();

        setupAssets();
        constructWorld();

        setupEditor(root);
        setupMiniMap(root);
        setupPalette(root);

        registerMiniMapViewToScrollViewUpdates();
        registerScrollViewToPaletteUpdates();

        Scene scene = new Scene(root, 1024, 768);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupPalette(AnchorPane root) {
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(5,5,5,5));

        this.palette = new AssetPalette(grid, this.assets, this.blank);
        this.palette.initLayout();

        root.getChildren().add(grid);
        root.setRightAnchor(grid, 10.0);
        root.setTopAnchor(grid, 10.0);
    }

    private void setupAssets() {
        this.tile_dim = 75;

        //construct a new assets directory with dims matching local assets.
        this.assets = TileImageDirectory.LoadFromFileSystem(Paths.get(System.getProperty("user.home"),
              "WorldTiler").toString(), tile_dim, true);

        //define some tiles.
        this.blank = new Tile("water",
              Paths.get(System.getProperty("user.home"), "WorldTiler", "water", "water_0.png" ).toString());
    }

    private void setupEditor(AnchorPane root) {
        //construct a ScrollableWorldView for the TileEditor.
        this.scrollableView = new ScrollableWorldView(0,0, VIEW_WIDTH_TILES, VIEW_HEIGHT_TILES, this.world, this.assets);

        //construct the Canvas that scrollableView will draw on.
        this.scrollCanvas = new Canvas(this.tile_dim *VIEW_WIDTH_TILES, this.tile_dim *VIEW_HEIGHT_TILES);

        //create the editor component
        this.editor = new TileEditor(this.world, this.assets, this.scrollableView, this.scrollCanvas);

        //define handlers.
        this.editor.initEventHandlers();

        //start animation
        this.editor.startRender();

        root.getChildren().add(this.editor.getCanvas());
        root.setLeftAnchor(this.editor.getCanvas(), 10.0);
    }


    private void setupMiniMap(AnchorPane root) {
        //construct a MiniMapWorldView for an over view of the world under construction.
        this.miniMapView = new MiniMapWorldView(0, 0, VIEW_WIDTH_TILES, VIEW_HEIGHT_TILES, this.world, this.assets);

        //construct the Canvas that miniMapView will draw on.
        this.miniMapCanvas = new Canvas(MINIMAP_WIDTH_PIXALS, MINIMAP_HEIGHT_PIXELS);

        //construct the mini map component.
        this.miniMap = new EditorMiniMap(this.world, this.miniMapView, this.miniMapCanvas);

        //start animation
        this.miniMap.startRender();

        root.getChildren().add(this.miniMap.getCanvas());
        root.setLeftAnchor(this.miniMap.getCanvas(), 10.0);
        root.setBottomAnchor(this.miniMap.getCanvas(), 10.0);
    }

    private void registerMiniMapViewToScrollViewUpdates() {
        //register observer miniMapView to support in observable scrollableView.
        this.scrollableView.registerChangeListener(this.miniMapView);
        //turn on preview rendering of observed scrollView.
        this.miniMapView.setTrackScrollView(true);
    }

    private void registerScrollViewToPaletteUpdates() {
        //register observer editor to support observable palette
        this.palette.registerChangeListener(this.editor);
    }


    private void constructWorld() {
        //construct a by world loading from file or with blank tile.
        this.world = World.ReadFromFile(
              Paths.get(System.getProperty("user.home"), "WorldTiler", "world_1").toString()).orElseGet(() -> {

                  System.out.println("Generating new World!");

                  //construct a world with blank tile.
                  //TODO we need a default map to load or a default title to use.
                  World w = new World(300,300, this.blank);

                  //make and island of grass.

                  for (int j = 15; j < 25; ++j) {
                      for (int i = 15; i < 25; ++i) {
                          //TODO we need a default map to load or a default title to use.
                          w.setTile(i, j,
                                new Tile("grass", Paths.get(System.getProperty("user.home"),
                                      "WorldTiler", "grass", "grass_0.png" ).toString()));
                      }
                  }
                  return w;
              }
        );

    }

    private void saveWorld(String name) {
        World.WriteToFile(this.world,  Paths.get(System.getProperty("user.home"), "WorldTiler", name).toString() );
    }

}
