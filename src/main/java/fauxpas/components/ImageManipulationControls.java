package fauxpas.components;

import fauxpas.entities.FilterableImage;
import fauxpas.filters.*;
import fauxpas.filters.noise.ValueNoise;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ImageManipulationControls {

    private final Orientation orientation;
    private Image lastImage;
    private FilterableImage image;

    public ImageManipulationControls(FilterableImage image) {
        this(image, Orientation.HORIZONTAL);
    }

    public ImageManipulationControls(FilterableImage image, Orientation orientation) {
        this.image = image;
        this.orientation = orientation;
    }

    public void init(ImageView imageView, Pane buttonBar, Stage primaryStage) {

        Button last = new Button("Undo");
        lastImage = image.getImage();
        last.setDisable(true);

        Button generate = new Button("Generate");
        generate.setOnMouseClicked((event) -> {
            Thread process = new Thread(() -> {
                buttonBar.setDisable(true);
                lastImage = image.getImage();
                last.setDisable(false);
                image.applyFilter(new ValueNoise(1.25f));
                imageView.setImage(image.getImage());
                buttonBar.setDisable(false);
            });
            process.start();
        });
        buttonBar.getChildren().add(generate);
        if (this.orientation == Orientation.HORIZONTAL) {
            HBox.setMargin( generate, new Insets(5, 5, 5, 5));
        }
        else {
            VBox.setMargin( generate, new Insets(5, 5, 5, 5));
        }


        Button gray = new Button("Grayscale");
        gray.setOnMouseClicked((event) -> {
            Thread process = new Thread(() -> {
                buttonBar.setDisable(true);
                lastImage = image.getImage();
                last.setDisable(false);
                image.applyFilter(new GrayscaleFilter());
                imageView.setImage(image.getImage());
                buttonBar.setDisable(false);
            });
            process.start();
        });
        buttonBar.getChildren().add(gray);
        if (this.orientation == Orientation.HORIZONTAL) {
            HBox.setMargin( gray, new Insets(5, 5, 5, 5));
        }
        else {
            VBox.setMargin( gray, new Insets(5, 5, 5, 5));
        }

        Button cannyEdges = new Button("Canny");
        cannyEdges.setOnMouseClicked((event) -> {
            Thread process = new Thread(() -> {
                buttonBar.setDisable(true);
                lastImage = image.getImage();
                last.setDisable(false);
                image.applyFilter(new SobelFilter(0.07, false, false));
                long t = System.currentTimeMillis();
                image.applyFilter(new CannyFilter(0.1, 0.45));
                System.out.println("Canny (only) processed in : " + (System.currentTimeMillis() - t) + " milliseconds.");
                imageView.setImage(image.getImage());
                buttonBar.setDisable(false);
            });
            process.start();
        });
        buttonBar.getChildren().add(cannyEdges);
        if (this.orientation == Orientation.HORIZONTAL) {
            HBox.setMargin( cannyEdges, new Insets(5, 5, 5, 5));
        }
        else {
            VBox.setMargin( cannyEdges, new Insets(5, 5, 5, 5));
        }

        Button edge = new Button("Sobel");
        edge.setOnMouseClicked((event) -> {
            Thread process = new Thread(() -> {
                buttonBar.setDisable(true);
                lastImage = image.getImage();
                last.setDisable(false);
                long t = System.currentTimeMillis();
                image.applyFilter(new SobelFilter(0.1, false, false));
                System.out.println("Sobel processed in : " + (System.currentTimeMillis() - t) + " milliseconds.");
                imageView.setImage(image.getImage());
                buttonBar.setDisable(false);
            });
            process.start();
        });
        buttonBar.getChildren().add(edge);
        if (this.orientation == Orientation.HORIZONTAL) {
            HBox.setMargin( edge, new Insets(5, 5, 5, 5));
        }
        else {
            VBox.setMargin( edge, new Insets(5, 5, 5, 5));
        }

        Button blur = new Button("Blur");
        blur.setOnMouseClicked((event) -> {
            Thread process = new Thread(() -> {
                buttonBar.setDisable(true);
                lastImage = image.getImage();
                last.setDisable(false);
                long t = System.currentTimeMillis();
                image.applyFilter(new GaussianBlur(3, 10));
                System.out.println("Blur processed in : " + (System.currentTimeMillis() - t) + " milliseconds.");
                imageView.setImage(image.getImage());
                buttonBar.setDisable(false);
            });
            process.start();
        });
        buttonBar.getChildren().add(blur);
        if (this.orientation == Orientation.HORIZONTAL) {
            HBox.setMargin( blur, new Insets(5, 5, 5, 5));
        }
        else {
            VBox.setMargin( blur, new Insets(5, 5, 5, 5));
        }

        Button redistribute = new Button("Sharpen");
        redistribute.setOnMouseClicked((event) -> {
            Thread process = new Thread(() -> {
                buttonBar.setDisable(true);
                lastImage = image.getImage();
                last.setDisable(false);
                image.applyFilter(new RedistributionFilter(1.2));
                imageView.setImage(image.getImage());
                buttonBar.setDisable(false);
            });
            process.start();
        });
        buttonBar.getChildren().add(redistribute);
        if (this.orientation == Orientation.HORIZONTAL) {
            HBox.setMargin( redistribute, new Insets(5, 5, 5, 5));
        }
        else {
            VBox.setMargin( redistribute, new Insets(5, 5, 5, 5));
        }

        Button selectImage = new Button("Load Image");
        selectImage.setOnMouseClicked((event) -> {
            buttonBar.setDisable(true);
            lastImage = image.getImage();
            last.setDisable(false);
            FileChooser fileChooser = new FileChooser();
            File file =  fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                image.setImage(new Image(file.toURI().toString()));
                imageView.setImage(image.getImage());
                //refreshBlendImage((int) image.getImage().getWidth(),(int) image.getImage().getHeight());
            }
            buttonBar.setDisable(false);
        });
        buttonBar.getChildren().add(selectImage);
        if (this.orientation == Orientation.HORIZONTAL) {
            HBox.setMargin( selectImage, new Insets(5, 5, 5, 5));
        }
        else {
            VBox.setMargin( selectImage, new Insets(5, 5, 5, 5));
        }

        Button saveImage = new Button("Save Image");
        saveImage.setOnMouseClicked((event) -> {
            buttonBar.setDisable(true);
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image","*.png"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("bitmap Image, ","*.bmp"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG Image","*.jpg"));
            File file =  fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                writeImageToFile(image.getImage(), file);
            }
            else {
                System.out.println("File selection for save image returned null!");
            }
            buttonBar.setDisable(false);
        });
        buttonBar.getChildren().add(saveImage);
        if (this.orientation == Orientation.HORIZONTAL) {
            HBox.setMargin( saveImage, new Insets(5, 5, 5, 5));
        }
        else {
            VBox.setMargin( saveImage, new Insets(5, 5, 5, 5));
        }

        last.setOnMouseClicked((event -> {
            Thread process = new Thread(() -> {
                buttonBar.setDisable(true);
                image.setImage(lastImage);
                imageView.setImage(lastImage);
                last.setDisable(true);
                buttonBar.setDisable(false);
            });
            process.start();
        }));
        buttonBar.getChildren().add(last);
        if (this.orientation == Orientation.HORIZONTAL) {
            HBox.setMargin( last, new Insets(5, 5, 5, 5));
        }
        else {
            VBox.setMargin( last, new Insets(5, 5, 5, 5));
        }

    }

    public FilterableImage getImage() {
        return image;
    }

    public void setImage(FilterableImage image) {
        this.image = image;
    }

    public Image getLastImage() {
        return lastImage;
    }

    public void setLastImage(Image lastImage) {
        this.lastImage = lastImage;
    }

    private void writeImageToFile(Image img, File file) {
        String extension = FilenameUtils.getExtension(file.getPath());

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), extension, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
