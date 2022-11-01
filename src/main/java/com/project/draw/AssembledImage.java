package com.project.draw;

import com.project.draw.processing.AgentPass;
import com.project.draw.processing.ProcessedImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AssembledImage {
    private final String pathToProcessedImage;
    private final String pathToCalculatedImage = "src/main/resources/calculatedImage.png";
    ProcessedImage processedImage;
    int[][] assembledPixelsArray;
    int[][] calculatedPixelsArray;

    public AssembledImage(String pathToSourceImage) {
        processedImage = new ProcessedImage(pathToSourceImage);
        assembledPixelsArray = processedImage.getPixelsArray();
        this.pathToProcessedImage = pathToSourceImage.substring(0, pathToSourceImage.indexOf('.')) +
                "Processed.png";
        calculatedPixelsArray = new int[processedImage.getHeight()][processedImage.getWidth()];
        agentLaunch();
    }

    public void createImageInDirectory() {
        processedImage.createImageInDirectory(pathToProcessedImage);
    }

    public void createImageInDirectory(String path) {
        if (!path.endsWith(".png") || !path.endsWith(".jpg")) {
            path = path.contains(".") ? path.substring(0, path.indexOf('.')) +
                    ".png" : path + ".png";
        }
        processedImage.createImageInDirectory(path);
    }

    public void agentLaunch() {
        AgentPass goToAgent = new AgentPass(assembledPixelsArray, calculatedPixelsArray);
        goToAgent.calculateWithThreads(4);
        calculatedPixelsArray = goToAgent.getCalculatedPixelsArray();
    }

    public void createCalculatedImage() {
        createCalculatedImage(pathToCalculatedImage);
    }

    public void createCalculatedImage(String path) {
        if (!path.endsWith(".png") || !path.endsWith(".jpg")) {
            path = path.contains(".") ? path.substring(0, path.indexOf('.')) +
                    ".png" : path + ".png";
        }
        BufferedImage calculatedImage = new BufferedImage(processedImage.getWidth(), processedImage.getHeight(),
                BufferedImage.TYPE_4BYTE_ABGR);
        File writeCalculatedImage = new File(path);
        if (calculatedPixelsArray != null) {
            for (int i = 0; i < calculatedPixelsArray.length; i++) {
                for (int j = 0; j < calculatedPixelsArray[0].length; j++) {
                    int currPixel = calculatedPixelsArray[i][j];
                    int p = (255 << 24) | (currPixel << 16) | (currPixel << 8) | currPixel;
                    calculatedImage.setRGB(j, i, p);
                }
            }

            try {
                ImageIO.write(calculatedImage, "png", writeCalculatedImage);
            } catch (IOException e) {
                System.out.println("Error " + e.getMessage() + "\nWriting to '" + writeCalculatedImage.getPath() + "'");
            }
        }

    }


    /**
     * @return String representation of the pixels image
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        int[][] pixelsArray = calculatedPixelsArray;
        int[][] pixelsArraySource = assembledPixelsArray;
        for (int i = 0; i < pixelsArray.length; i++) {
            for (int j = 0; j < pixelsArray.length; j++) {
                if (j == 0) s.append("[");
                else if (j != pixelsArray.length - 1) {
                    s.append(pixelsArray[i][j]);
                    s.append(", ");
                } else {
                    s.append(pixelsArray[i][j]);
                    s.append("]");
                }
            }
            s.append("\n");
        }
        return s.toString();
    }
}
