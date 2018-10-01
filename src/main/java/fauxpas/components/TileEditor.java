package fauxpas.components;

import fauxpas.collections.TileImageDirectory;
import fauxpas.entities.World;
import fauxpas.views.ScrollableWorldView;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;


public class TileEditor {

    private static final int EDGE_SCROLL_INSET = 25;

    private World world;
    private TileImageDirectory assets;
    private ScrollableWorldView view;
    private Canvas canvas;

    private AnimationTimer animTimer;
    private boolean drawing;

    private int browsedTileX;
    private int browsedTileY;

    public TileEditor(World world, TileImageDirectory assets, ScrollableWorldView view, Canvas canvas) {
        this.world = world;
        this.assets = assets;
        this.view = view;
        this.canvas = canvas;

        this.drawing = false;
        browsedTileX = -1;
        browsedTileY = -1;
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
        drawing = false;
    }

    public void initEventHandlers() {
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, (EventHandler<MouseEvent>) event -> {
            ScrollMouse(event);
        });

        canvas.addEventHandler(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) event ->{
            browsedTileX = -1;
            browsedTileY = -1;
        });

    }

    private void ScrollMouse(MouseEvent event) {
        if (event.getX() < EDGE_SCROLL_INSET) {
            view.scrollX(-1);
        }
        else if (event.getX() > ((assets.getTileDimension()*view.getWidth())-EDGE_SCROLL_INSET)) {
            view.scrollX(1);
        }
        if (event.getY() < EDGE_SCROLL_INSET) {
            view.scrollY(-1);
        }
        else if (event.getY() > ((assets.getTileDimension()*view.getHeight())-EDGE_SCROLL_INSET)) {
            view.scrollY(1);
        }

        browsedTileX = (int) (event.getX() / assets.getTileDimension());
        browsedTileY = (int) (event.getY() / assets.getTileDimension());
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
