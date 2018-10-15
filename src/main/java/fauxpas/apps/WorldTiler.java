package fauxpas.apps;

import fauxpas.collections.TileImageDirectory;
import fauxpas.components.AssetPalette;
import fauxpas.components.EditorMiniMap;
import fauxpas.components.TileEditor;
import fauxpas.entities.Tile;
import fauxpas.entities.World;
import fauxpas.views.MiniMapWorldView;
import fauxpas.views.ScrollableWorldView;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;


public class WorldTiler extends Application {

    private static final int VIEW_WIDTH_TILES = 8;
    private static final int VIEW_HEIGHT_TILES = 5;
    private static final double MINIMAP_HEIGHT_PIXELS = 375.0;
    private static final double MINIMAP_WIDTH_PIXALS = 600.0;

    private TileEditor editor;
    private EditorMiniMap miniMap;
    private AssetPalette palette;

    private Tile primaryTile;
    private Tile secondaryTile;
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
        InitWorldTiler(primaryStage);
    }

    private void InitWorldTiler(Stage primaryStage) {
        primaryStage.setTitle("World-Tiler");
        AnchorPane root = new AnchorPane();

        Optional<String> dirResult = CollectInitialParameters(primaryStage);

        dirResult.ifPresent( (worldData) -> {

            if (Files.exists(Paths.get(worldData.split(":")[0], "water", "water_0.png")) &&
                  Files.exists(Paths.get(worldData.split(":")[0], "grass", "grass_0.png"))) {

                setupAssets(Integer.parseInt(worldData.split(":")[1]),
                      worldData.split(":")[0],
                      Paths.get(worldData.split(":")[0], "water", "water_0.png").toString(),
                      Paths.get(worldData.split(":")[0], "grass", "grass_0.png").toString());


            }
            else {
                Optional<String> tileResults = CollectTileDefaults(primaryStage);

                tileResults.ifPresent( tileData -> setupAssets(Integer.parseInt(worldData.split(":")[1]),
                      worldData.split(":")[0],
                      tileData.split(":")[0],
                      tileData.split(":")[1]));
            }

            if (this.assets != null && this.primaryTile != null && this.secondaryTile != null) {

                constructWorld( worldData.split(":")[2] );

                setupEditor(root);
                setupMiniMap(root);
                setupPalette(root);

                registerMiniMapViewToScrollViewUpdates();
                registerScrollViewToPaletteUpdates();

                Scene scene = new Scene(root, 1024, 768);
                primaryStage.setScene(scene);
                primaryStage.show();
            }
        });
    }

    private Optional<String> CollectInitialParameters(Stage primaryStage) {
        Dialog<String> setupInfoCollector = new Dialog<>();
        setupInfoCollector.setTitle("Setup Assets...");
        GridPane infoCollectGrid = new GridPane();
        infoCollectGrid.setHgap(10);
        infoCollectGrid.setVgap(4);
        infoCollectGrid.setPadding(new Insets(0, 10, 0, 10));

        AtomicBoolean dirSelected = new AtomicBoolean(false);
        Label assetsLabel = new Label("Assets Directory: ");
        Button directorySelect = new Button("None");
        directorySelect.setOnMouseClicked((event) -> {
            DirectoryChooser assetsSelector = new DirectoryChooser();
            assetsSelector.setTitle("Choose asset directory");
            File assetsDir = assetsSelector.showDialog(primaryStage);
            directorySelect.setText(assetsDir.toPath().toString());
            dirSelected.set(true);
        });

        infoCollectGrid.add(assetsLabel, 1, 1);
        GridPane.setHalignment(assetsLabel, HPos.LEFT);
        infoCollectGrid.add(directorySelect, 2, 1, 18, 1);
        GridPane.setHalignment(directorySelect, HPos.RIGHT);

        Label worldLabel = new Label("World File: ");
        Button worldSelect = new Button("None");
        worldSelect.setOnMouseClicked((event) -> {
            FileChooser worldSelector = new FileChooser();
            worldSelector.setTitle("Choose a world file");
            File assetsDir = worldSelector.showOpenDialog(primaryStage);
            worldSelect.setText(assetsDir.toPath().toString());
        });

        infoCollectGrid.add(worldLabel, 1, 3);
        GridPane.setHalignment(worldLabel, HPos.LEFT);
        infoCollectGrid.add(worldSelect, 2, 3, 18, 1);
        GridPane.setHalignment(worldSelect, HPos.RIGHT);

        AtomicBoolean dimSelected = new AtomicBoolean(false);
        Label assetDims = new Label("Tile dimension: ");
        ChoiceBox<Integer> dimOptions = new ChoiceBox<>();
        IntStream.range(20, 76).filter(i ->  i%5 == 0 ).forEach(dimOptions.getItems()::add);
        dimOptions.getSelectionModel().selectedItemProperty().addListener(
              (observable, oldVal, newVal) -> dimSelected.set(true));

        infoCollectGrid.add(assetDims, 1, 4);
        infoCollectGrid.add(dimOptions, 3, 4 );

        ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        setupInfoCollector.getDialogPane().getButtonTypes().add(buttonTypeOk);
        setupInfoCollector.setResultConverter( buttonType -> {
            if (buttonType == buttonTypeOk && dimSelected.get() && dirSelected.get()) {
                return directorySelect.getText()+":"+
                      dimOptions.getValue().toString()+":"+
                      worldSelect.getText();
            }
            else {
                return null;
            }
        });

        infoCollectGrid.autosize();
        setupInfoCollector.getDialogPane().setContent(infoCollectGrid);
        return setupInfoCollector.showAndWait();
    }

    private Optional<String> CollectTileDefaults(Stage primaryStage) {
        Dialog<String> setupInfoCollector = new Dialog<>();
        setupInfoCollector.setTitle("Select default tiles...");
        GridPane infoCollectGrid = new GridPane();
        infoCollectGrid.setHgap(10);
        infoCollectGrid.setVgap(4);
        infoCollectGrid.setPadding(new Insets(0, 10, 0, 10));

        AtomicBoolean waterSelected = new AtomicBoolean(false);
        Label waterLabel = new Label("Water tile: ");
        Button waterSelect = new Button("None");
        waterSelect.setOnMouseClicked((event) -> {
            FileChooser assetsSelector = new FileChooser();
            assetsSelector.setTitle("Choose water tile");
            File assetsDir = assetsSelector.showOpenDialog(primaryStage);
            waterSelect.setText(assetsDir.toPath().toString());
            waterSelected.set(true);
        });

        infoCollectGrid.add(waterLabel, 1, 1);
        GridPane.setHalignment(waterLabel, HPos.LEFT);
        infoCollectGrid.add(waterSelect, 2, 1, 18, 1);
        GridPane.setHalignment(waterSelect, HPos.RIGHT);

        AtomicBoolean grassSelected = new AtomicBoolean(false);
        Label grassLabel = new Label("Grass tile: ");
        Button grassSelect = new Button("None");
        grassSelect.setOnMouseClicked((event) -> {
            FileChooser assetsSelector = new FileChooser();
            assetsSelector.setTitle("Choose grass tile");
            File asset = assetsSelector.showOpenDialog(primaryStage);
            grassSelect.setText(asset.toPath().toString());
            grassSelected.set(true);
        });

        infoCollectGrid.add(grassLabel, 1, 3);
        GridPane.setHalignment(grassLabel, HPos.LEFT);
        infoCollectGrid.add(grassSelect, 2, 3, 18, 1);
        GridPane.setHalignment(grassSelect, HPos.RIGHT);

        ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        setupInfoCollector.getDialogPane().getButtonTypes().add(buttonTypeOk);
        setupInfoCollector.setResultConverter( buttonType -> {
            if (buttonType == buttonTypeOk && waterSelected.get() && grassSelected.get()) {
                return waterSelect.getText()+":"+grassSelect.getText();
            }
            else {
                return null;
            }
        });

        infoCollectGrid.autosize();
        setupInfoCollector.getDialogPane().setContent(infoCollectGrid);
        return setupInfoCollector.showAndWait();
    }

    private void setupPalette(AnchorPane root) {
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(5,5,5,5));

        this.palette = new AssetPalette(grid, this.assets, this.primaryTile);
        this.palette.initLayout();

        root.getChildren().add(grid);
        AnchorPane.setRightAnchor(grid, 10.0);
        AnchorPane.setTopAnchor(grid, 10.0);
    }

    private void setupAssets(int assetsDim, String assetsPath, String primaryTilePath, String secondaryTilePath) {
        this.tile_dim = assetsDim;

        //construct a new assets directory with dims matching local assets.
        this.assets = TileImageDirectory.LoadFromFileSystem(assetsPath, tile_dim, true);

        //define some tiles.
        this.primaryTile = new Tile("water", primaryTilePath);
        this.secondaryTile = new Tile( "grass", secondaryTilePath);
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
        AnchorPane.setLeftAnchor(this.editor.getCanvas(), 10.0);
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
        AnchorPane.setLeftAnchor(this.miniMap.getCanvas(), 10.0);
        AnchorPane.setBottomAnchor(this.miniMap.getCanvas(), 10.0);
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

    private void constructWorld(String worldPath) {
        //construct a by world loading from file or with primaryTile tile.

        this.world = World.ReadFromFile(
              Paths.get(worldPath).toString()).orElseGet(() -> {

                  System.out.println("Generating new World!");

                  //construct a world with primaryTile tile.
                  //TODO we need a default map to load or a default title to use.
                  World w = new World(300,300, this.primaryTile);

                  //make and island of grass.

                  for (int j = 15; j < 25; ++j) {
                      for (int i = 15; i < 25; ++i) {
                          //TODO we need a default map to load or a default title to use.
                          w.setTile(i, j, this.secondaryTile);
                      }
                  }
                  return w;
              }
        );

    }

    private void saveWorld(String path) {
        World.WriteToFile(this.world,  Paths.get(path).toString() );
    }

}
