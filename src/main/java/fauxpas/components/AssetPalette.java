package fauxpas.components;

import fauxpas.collections.TileImageDirectory;
import fauxpas.entities.Tile;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.atomic.AtomicInteger;

public class AssetPalette {

    private GridPane grid;
    private TileImageDirectory assets;

    private PropertyChangeSupport changeSupport;

    private Tile selectedTile;
    private Tile blankTile;

    public AssetPalette(GridPane grid, TileImageDirectory assets, Tile blank) {
        this.grid = grid;
        this.assets = assets;
        this.blankTile = blank;
        this.selectedTile = blank;
        this.changeSupport = new PropertyChangeSupport(this);
    }

    public void initLayout() {
        AtomicInteger i = new AtomicInteger();
        assets.getKeys().forEach( tile -> {
            assets.get(tile).ifPresent( img -> {
                ImageView imgView = new ImageView(img);
                imgView.setPreserveRatio(true);
                imgView.setFitWidth(25.0);
                imgView.setFitHeight(25.0);
                Button b = new Button("", imgView);
                b.setOnMouseClicked(event -> {
                    selectTileAsset(tile);
                });
                i.getAndIncrement();
                grid.add(b, i.get()%2, i.get()/2);
            });
        });
    }

    public GridPane getGrid() {
        return grid;
    }

    private void selectTileAsset(Tile tileSelection) {
        this.changeSupport.firePropertyChange("selectedTile", this.selectedTile, tileSelection);
        this.selectedTile = tileSelection;
    }

    /**
     * Wrapper for addPropertyChangeListener
     * @param pcl propertyChangeListener to add.
     */
    public void registerChangeListener(PropertyChangeListener pcl) {
        this.changeSupport.addPropertyChangeListener(pcl);
    }

    /**
     * Wrapper for removePropertyChangeListener
     * @param pcl propertyChangeListener to remove.
     */
    public void unregisterChangeListener(PropertyChangeListener pcl) {
        this.changeSupport.removePropertyChangeListener(pcl);
    }
}
