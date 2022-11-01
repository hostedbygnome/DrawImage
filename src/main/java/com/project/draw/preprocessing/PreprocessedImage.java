package com.project.draw.preprocessing;

import java.awt.image.BufferedImage;


public class PreprocessedImage {
    private final BufferedImage processedImage;
    private final int[][] pixelsArray;

    public PreprocessedImage(BufferedImage processedImage) {
        this.processedImage = processedImage;
        this.pixelsArray = calculateArrayPixels(this.processedImage);
    }

    /**
     * @return Array of pixels image
     */
    public int[][] getPixelsArray() {
        return pixelsArray;
    }

    /**
     * @return Modified of source image
     */
    public BufferedImage getProcessedImage() {
        return processedImage;
    }

    /**
     * @param image Source image
     * @return pixels array of the modified image (b/w image)
     */
    private int[][] calculateArrayPixels(BufferedImage image) {
        int widthImage = image.getWidth();
        int heightImage = image.getHeight();
        int[][] pixelsArray = new int[widthImage][heightImage];
        for (int i = 0; i < pixelsArray.length; i++) {
            for (int j = 0; j < pixelsArray[i].length; j++) {
                int p = image.getRGB(j, i);
                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;
                int avg = (r + g + b) / 3;
                p = (a << 24) | (avg << 16) | (avg << 8) | avg;
                image.setRGB(j, i, p);
                pixelsArray[i][j] = avg;
            }
        }
        return pixelsArray;
    }

    /**
     * @return height image
     */
    public int getImageHeight() {
        return processedImage.getHeight();
    }

    /**
     * @return width image
     */
    public int getImageWidth() {
        return processedImage.getWidth();
    }
}
