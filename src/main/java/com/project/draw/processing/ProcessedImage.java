package com.project.draw.processing;

import com.project.draw.ImageProcessing;
import com.project.draw.preprocessing.PreprocessedImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ProcessedImage implements ImageProcessing {
    private final File fileImage;
    private final int imageHeight;
    private final int imageWidth;
    private BufferedImage sourceImage;
    private PreprocessedImage processedImage;

    public ProcessedImage(String path) {
        fileImage = new File(path);
        initializeSourceImage();
        calculateProcessedImage();
        imageWidth = sourceImage.getWidth();
        imageHeight = sourceImage.getHeight();
    }

    private void initializeSourceImage() {
        try {
            sourceImage = ImageIO.read(fileImage);
        } catch (IOException e) {
            System.out.println("Error " + e.getMessage() + "\nReading from '" + fileImage.getPath() + "'");
        }
    }

    public BufferedImage getSourceImage() {
        return sourceImage;
    }

    public void calculateProcessedImage() {
        processedImage = new PreprocessedImage(sourceImage);
    }

    public PreprocessedImage getProcessedImage() {
        return processedImage;
    }

    public int getHeight() {
        return imageHeight;
    }

    public int getWidth() {
        return imageWidth;
    }

    @Override
    public int[][] getPixelsArray() {
        return processedImage.getPixelsArray();
    }

    /**
     * @param path path to save the image
     */
    public void createImageInDirectory(String path) {
        File modifiedImage = new File(path);
        if (!modifiedImage.exists()) {
            try {
                ImageIO.write(processedImage.getProcessedImage(), "png", modifiedImage);
            } catch (IOException e) {
                System.out.println("Error writing" + e.getMessage());
            }
        }
    }
}
