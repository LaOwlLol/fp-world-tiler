package fauxpas.components;

import fauxpas.collections.TileImageDirectory;
import fauxpas.entities.World;
import fauxpas.views.ScrollableWorldView;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;

public class TileEditor {

    private World world;
    private TileImageDirectory assets;
    private ScrollableWorldView view;
    private Canvas canvas;

    private AnimationTimer animTimer;
    private boolean drawing;

    public TileEditor(World world, TileImageDirectory assets, ScrollableWorldView view, Canvas canvas) {
        this.world = world;
        this.assets = assets;
        this.view = view;
        this.canvas = canvas;

        this.drawing = false;
    }

    public void startRender() {
        if (!drawing) {
            animTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    canvas.getGraphicsContext2D().clearRect(0,0,assets.getTileDimension()*view.getWidth(),
                          assets.getTileDimension()*view.getHeight());
                    view.render(canvas.getGraphicsContext2D());
                }
            };

            animTimer.start();
            drawing = true;
        }
    }

    public void stopRender() {
        animTimer.stop();
        drawing = false;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
