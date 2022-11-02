package com.project.draw;

public class Application {
    private static final String pathToSourceImage = "src/main/resources/sourceImages/sourceImage3.png";
    public static void main(String[] args) {
        AssembledImage image = new AssembledImage(pathToSourceImage);
        image.createImageInDirectory("src/main/resources/assembledImages/assembledImage3.png");
        //System.out.println(image);
        image.createCalculatedImage("src/main/resources/calculatedImages/calculatedImage3.png");
    }
}