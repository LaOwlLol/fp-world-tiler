package fauxpas.components;

import fauxpas.collections.TileImageDirectory;
import fauxpas.entities.World;
import fauxpas.views.MiniMapWorldView;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public class EditorMiniMap {

    private World world;
    private MiniMapWorldView view;
    private Canvas canvas;

    private AnimationTimer animTimer;

    private boolean drawing;


    public EditorMiniMap(World world, MiniMapWorldView view, Canvas canvas) {
        this.world = world;
        this.view = view;
        this.canvas = canvas;

        this.drawing = false;
    }


    public void startRender() {
        if (!drawing) {

            animTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    canvas.getGraphicsContext2D().clearRect(0,0,canvas.getWidth(), canvas.getWidth());
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
