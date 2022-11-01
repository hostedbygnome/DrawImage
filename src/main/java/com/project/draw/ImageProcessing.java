package com.project.draw;


import com.project.draw.preprocessing.PreprocessedImage;

import java.awt.image.BufferedImage;

public interface ImageProcessing {
    int getHeight();
    int getWidth();
    int[][] getPixelsArray();
    BufferedImage getSourceImage();
    PreprocessedImage getProcessedImage();
}
