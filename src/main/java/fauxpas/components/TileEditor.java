package fauxpas.components;

import fauxpas.collections.TileImageDirectory;
import fauxpas.entities.Tile;
import fauxpas.entities.World;
import fauxpas.views.ScrollableWorldView;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicInteger;


public class TileEditor implements PropertyChangeListener {

    private static final long TENTH_SECOND_IN_NANO = 100000000;

    private World world;
    private TileImageDirectory assets;
    private ScrollableWorldView view;
    private Canvas canvas;

    private AnimationTimer animTimer;
    private AnimationTimer scrollTimer;
    private boolean drawing;

    private int browsedTileX;
    private int browsedTileY;
    private int scrollModX;
    private int scrollModY;

    private double smoothScrollFactor;
    private int edgeInsetForScrollDetection;

    private AtomicInteger frame;
    private Tile tileBrush;

    public TileEditor(World world, TileImageDirectory assets, ScrollableWorldView view, Canvas canvas) {
        this.world = world;
        this.assets = assets;
        this.view = view;
        this.canvas = canvas;

        this.drawing = false;
        browsedTileX = -1;
        browsedTileY = -1;
        scrollModX = 0;
        scrollModY = 0;

        smoothScrollFactor = 2.5;
        edgeInsetForScrollDetection = Math.max(25, assets.getTileDimension()/3);

        frame = new AtomicInteger(0);
    }

    public void startRender() {
        if (!drawing) {

            animTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    canvas.getGraphicsContext2D().clearRect(0,0,assets.getTileDimension()*view.getWidth(),
                          assets.getTileDimension()*view.getHeight());
                    view.render(canvas.getGraphicsContext2D());
                    canvas.getGraphicsContext2D().setStroke(Color.IVORY);
                    DrawBrowsedTileFocus();
                    frame.getAndIncrement();
                }
            };

            animTimer.start();
            drawing = true;
        }
    }

    private void DrawBrowsedTileFocus() {
        if (browsedTileX != -1 && browsedTileY != -1) {
            canvas.getGraphicsContext2D().strokeRect(browsedTileX * assets.getTileDimension(),
                  browsedTileY * assets.getTileDimension(), assets.getTileDimension(), assets.getTileDimension());
            canvas.getGraphicsContext2D().setStroke(Color.BLACK);
        }
    }

    public void stopRender() {
        animTimer.stop();
        scrollTimer.stop();
        drawing = false;
    }

    public void initEventHandlers() {
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {

            UpdateFocusedTile(event);
            UpdateViewScroll(event);

        });

        canvas.addEventHandler(MouseEvent.MOUSE_EXITED, event ->{
            browsedTileX = -1;
            browsedTileY = -1;
        });

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->{
            if (this.tileBrush != null) {
                world.setTile(this.view.getX()+browsedTileX, this.view.getY()+browsedTileY, this.tileBrush);
            }
        });

        scrollTimer = new AnimationTimer() {
            long last;

            @Override
            public void handle(long now) {
                if ( ((now-last) > (smoothScrollFactor*TENTH_SECOND_IN_NANO)) && browsedTileX != -1 && browsedTileY != -1 ) {
                    view.scroll(scrollModX, scrollModY);
                    last = now;
                }
            }
        };
        scrollTimer.start();

    }

    private void UpdateViewScroll(MouseEvent event) {

        if (event.getX() < edgeInsetForScrollDetection) {
            scrollModX = -1;
        } else if (event.getX() > ((assets.getTileDimension() * view.getWidth()) - edgeInsetForScrollDetection)) {
            scrollModX = 1;
        }
        else {
            scrollModX = 0;
        }

        if (event.getY() < edgeInsetForScrollDetection) {
            scrollModY = -1;
        } else if (event.getY() > ((assets.getTileDimension() * view.getHeight()) - edgeInsetForScrollDetection)) {
            scrollModY = 1;
        }
        else {
            scrollModY = 0;
        }

    }

    private void UpdateFocusedTile(MouseEvent event) {
        browsedTileX = (int) (event.getX() / assets.getTileDimension());
        browsedTileY = (int) (event.getY() / assets.getTileDimension());
    }

    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equalsIgnoreCase("selectedTile")) {
            this.tileBrush = (Tile) event.getNewValue();
        }
    }
}
