/*
 * Course: CSC 1120
 * Term: Spring 2024
 * Assignment: Lab 5
 * Name: Andrew Keenan
 * Created: 2-14-24
*/
package keenana;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Calculates the mean and median of the Images
 * plus reads incoming images and writes to files form Image objects
 */
public class MeanImageMedian {
    /**
     * Maximum color value
     */
    public static final int MAX_COLOR = 255;

    private static final keenana.Transform MEAN = x -> {
        int sum = 0;
        for (int i : x){
            sum += i;
        }
        return sum / x.length;
    };
    private static final keenana.Transform MEDIAN = x -> {
        Arrays.sort(x);
        int sum;
        if (x.length % 2 == 0){
            sum = x[x.length / 2] + x[(x.length / 2) - 1] / 2;
        } else {
            sum = x[x.length / 2];
        }
        return sum;
    };
    private static final keenana.Transform MIN = x -> {
        Arrays.sort(x);
        return x[0];
    };
    private static final keenana.Transform MAX = x -> {
        Arrays.sort(x);
        return x[x.length - 1];
    };
    private static final keenana.Transform RANDOM = x -> {
        int bound = x.length;
        int index = (int) (Math.random() * bound);
        return x[index];
    };
    /**
     * Calculates the median of all the images passed to the method.
     * <br />
     * Each pixel in the output image consists is calculated as the median
     * red, green, and blue components of the input images at the same location.
     * @param inputImages Images to be used as input
     * @return An image containing the median color value for each pixel in the input images
     *
     * @throws IllegalArgumentException Thrown if inputImages or any element of inputImages is null,
     * the length of the array is less than two, or  if any of the input images differ in size.
     *
     * @deprecated use {@link #generateImage(Image[], String)} instead
     */
    @Deprecated
    public static Image calculateMedianImage(Image[] inputImages) {
        int length = inputImages.length;
        double x = inputImages[0].getWidth();
        double y = inputImages[0].getHeight();
        boolean same = true;
        for (Image i : inputImages){
            if (i.getWidth() != x) {
                same = false;
            }
            if (i.getHeight() != y){
                same = false;
            }
        }
        WritableImage median = new WritableImage((int)x, (int)y);
        PixelWriter writer = median.getPixelWriter();
        if (same) {
            for (int i = 0; i < y; i++) {
                for (int j = 0; j < x; j++) {
                    int pixel;
                    int[] asum = new int[length];
                    int[] rsum = new int[length];
                    int[] gsum = new int[length];
                    int[] bsum = new int[length];
                    for (int k = 0; k < length; k++) {
                        PixelReader reader = inputImages[k].getPixelReader();
                        int argb = reader.getArgb(j, i);
                        asum[k] = argbToAlpha(argb);
                        rsum[k] = argbToRed(argb);
                        gsum[k] = argbToGreen(argb);
                        bsum[k] = argbToBlue(argb);
                    }
                    Arrays.sort(asum);
                    Arrays.sort(rsum);
                    Arrays.sort(gsum);
                    Arrays.sort(bsum);
                    int alpha;
                    int red;
                    int green;
                    int blue;

                    if (length % 2 == 0){
                        alpha = (asum[length / 2] + asum[(length / 2)- 1]) / 2;
                        red = (rsum[length / 2] + rsum[(length / 2)- 1]) / 2;
                        green = (gsum[length / 2] + gsum[(length / 2)- 1]) / 2;
                        blue = (bsum[length / 2] + bsum[(length / 2)- 1]) / 2;
                    } else {
                        int division = length / 2;
                        alpha = asum[division];
                        red = rsum[division];
                        green = gsum[division];
                        blue = bsum[division];
                    }
                    pixel = argbToInt(alpha, red, green, blue);
                    writer.setArgb(j, i, pixel);
                }
            }
        }
        return median;
    }

    /**
     * Calculates the mean of all the images passed to the method.
     * <br />
     * Each pixel in the output image consists is calculated as the average of the
     * red, green, and blue components of the input images at the same location.
     * @param inputImages Images to be used as input
     * @return An image containing the mean color value for each pixel in the input images
     *
     * @throws IllegalArgumentException Thrown if inputImages or any element of inputImages is null,
     * the length of the array is less than two, or  if any of the input images differ in size.
     *
     * @deprecated use {@link #generateImage(Image[], String)} instead
     */
    @Deprecated
    public static Image calculateMeanImage(Image[] inputImages) {
        int length = inputImages.length;
        double x = inputImages[0].getWidth();
        double y = inputImages[0].getHeight();
        boolean same = true;
        for (Image i : inputImages){
            if (i.getWidth() != x) {
                same = false;
            }
            if (i.getHeight() != y){
                same = false;
            }
        }
        WritableImage mean = new WritableImage((int)x, (int)y);
        PixelWriter writer = mean.getPixelWriter();
        if (same) {
            for (int i = 0; i < y; i++) {
                for (int j = 0; j < x; j++) {
                    int pixel;
                    int asum = 0;
                    int rsum = 0;
                    int gsum = 0;
                    int bsum = 0;
                    for (Image inputImage : inputImages) {
                        PixelReader reader = inputImage.getPixelReader();
                        int argb = reader.getArgb(j, i);
                        asum += argbToAlpha(argb);
                        rsum += argbToRed(argb);
                        gsum += argbToGreen(argb);
                        bsum += argbToBlue(argb);
                    }
                    asum = asum / length;
                    rsum = rsum / length;
                    gsum = gsum / length;
                    bsum = bsum / length;
                    pixel = argbToInt(asum, rsum, gsum, bsum);
                    writer.setArgb(j, i, pixel);
                }
            }
        }
        return mean;
    }

    /**
     * generates a new image based off of the parameters
     * @param images the input images as an array
     * @param operation the operation that needs ot be completed on the input images
     * @return the output images after the appled operation is done.
     */
    public static Image generateImage(Image[] images, String operation){
        return switch (operation) {
            case "mean" -> applyTransform(images, MEAN);
            case "median" -> applyTransform(images, MEDIAN);
            case "min" -> applyTransform(images, MIN);
            case "max" -> applyTransform(images, MAX);
            default -> applyTransform(images, RANDOM);
        };
    }

    /**
     * returns an image that the given transformation is applied to
     * @param images the array of input images
     * @param transformation the transformation that is wished to be applied
     * @return the image with the transformation completed on the input images and then merged
     */
    public static Image applyTransform(Image[] images, Transform transformation){
        int x = (int)images[0].getWidth();
        int y = (int)images[0].getHeight();
        boolean same = true;
        for (Image i : images){
            if (i.getWidth() != x) {
                same = false;
            }
            if (i.getHeight() != y){
                same = false;
            }
        }
        WritableImage out = new WritableImage(x, y);
        PixelWriter writer = out.getPixelWriter();
        if (same) {
            for (int i = 0; i < y; i++){
                for (int j = 0; j < x; j++){
                    int pixel;
                    int[] asum = new int[images.length];
                    int[] rsum = new int[images.length];
                    int[] gsum = new int[images.length];
                    int[] bsum = new int[images.length];
                    for (int k = 0; k < images.length; k++) {
                        PixelReader reader = images[k].getPixelReader();
                        int argb = reader.getArgb(j, i);
                        asum[k] = argbToAlpha(argb);
                        rsum[k] = argbToRed(argb);
                        gsum[k] = argbToGreen(argb);
                        bsum[k] = argbToBlue(argb);
                    }
                    int alpha = transformation.apply(asum);
                    int red = transformation.apply(rsum);
                    int green = transformation.apply(gsum);
                    int blue = transformation.apply(bsum);
                    pixel = argbToInt(alpha, red, green, blue);
                    writer.setArgb(j, i, pixel);
                }
            }
        }
        return out;
    }

    /**
     * Reads an image in PPM format. The method only supports the plain PPM
     * (P3) format with 24-bit color
     * and does not support comments in the image file.
     * @param imagePath the path to the image to be read
     * @return An image object containing the image read from the file.
     *
     * @throws IllegalArgumentException Thrown if imagePath is null.
     * @throws IOException Thrown if the image format is invalid or there was
     * trouble reading the file.
     */
    public static Image readImage(Path imagePath) throws IOException, IllegalArgumentException{
        File file = imagePath.toFile();
        FileInputStream input = new FileInputStream(file);
        Image copy;
        if(imagePath.toString().endsWith(".ppm")){
            copy = readPPMImage(imagePath);
        } else if (imagePath.toString().endsWith(".msoe")) {
            copy = readMSOEImage(imagePath);
        } else {
            copy = new Image(input);
        }
        input.close();
        return copy;
    }
    private static Image readPPMImage(Path imagePath) throws IOException, NoSuchElementException {
        Scanner in = new Scanner(imagePath);
        WritableImage copy;
        if (in.nextLine().equals("P3")) {
            int x = in.nextInt();
            int y = in.nextInt();
            copy = new WritableImage(x, y);
            PixelWriter writer = copy.getPixelWriter();
            int alpha = in.nextInt();
            if (alpha == MAX_COLOR){
                for (int i = 0; i < y; i++) {
                    for (int j = 0; j < x; j++) {
                        int red = in.nextInt();
                        int green = in.nextInt();
                        int blue = in.nextInt();
                        int pixel = argbToInt(alpha, red, green, blue);
                        writer.setArgb(j, i, pixel);
                    }
                }
            } else {
                throw new IOException();
            }
        } else {
            throw new IOException();
        }
        return copy;
    }

    private static Image readMSOEImage(Path imagePath) throws IOException {
        WritableImage copy;
        FileInputStream fInput = new FileInputStream(imagePath.toFile());
        DataInputStream input = new DataInputStream(fInput);
        final int check = 1297305413;
        if (input.readInt() == check){
            int x = input.readInt();
            int y = input.readInt();
            copy = new WritableImage(x, y);
            PixelWriter writer = copy.getPixelWriter();
            for (int i = 0; i < y; i++){
                for (int j = 0; j < x; j++){
                    writer.setArgb(j, i, input.readInt());
                }
            }
        } else {
            throw new IOException();
        }
        fInput.close();
        input.close();
        return copy;
    }
    /**
     * Writes an image in PPM format. The method only supports the plain PPM (P3)
     * format with 24-bit color
     * and does not support comments in the image file.
     * @param imagePath the path to where the file should be written
     * @param image the image containing the pixels to be written to the file
     *
     * @throws IllegalArgumentException Thrown if imagePath is null.
     * @throws IOException Thrown if the image format is invalid or there was trouble
     * reading the file.
     */
    public static void writeImage(Path imagePath, Image image) throws IOException,
            IllegalArgumentException {
        File file = imagePath.toFile();
        if (imagePath.toString().endsWith(".ppm")){
            writePPMImage(imagePath, image);
        } else if (imagePath.toString().endsWith(".png")){
            RenderedImage image2 = SwingFXUtils.fromFXImage(image, null);
            ImageIO.write(image2, "png", file);
        } else if (imagePath.toString().endsWith(".jpg")){
            RenderedImage image2 = SwingFXUtils.fromFXImage(image, null);
            ImageIO.write(image2, "jpg", file);
        } else if (imagePath.toString().endsWith(".msoe")){
            writeMSOEImage(imagePath, image);
        }
    }
    private static void writePPMImage(Path imagePath, Image image) throws IOException {
        PixelReader reader = image.getPixelReader();
        File output = imagePath.toFile();
        PrintWriter writer = new PrintWriter(output);
        double x = image.getWidth();
        double y = image.getHeight();
        writer.write("P3\n"+(int)x+" "+(int)y+"\n"+MAX_COLOR);
        for (int i = 0; i < y; i++){
            writer.write("\n");
            for (int j = 0; j < x; j++){
                int argb = reader.getArgb(j, i);
                int red = argbToRed(argb);
                int green = argbToGreen(argb);
                int blue = argbToBlue(argb);
                writer.write(red+" "+green+" "+blue+" ");
            }
        }
        writer.close();
    }
    private static void writeMSOEImage(Path imagePath, Image image) throws IOException{
        PixelReader reader = image.getPixelReader();
        File output = imagePath.toFile();
        FileOutputStream sOutput = new FileOutputStream(output);
        DataOutputStream dOutput = new DataOutputStream(sOutput);
        final int check = 1297305413;
        dOutput.writeInt(check);
        dOutput.writeInt((int)image.getWidth());
        dOutput.write((int)image.getHeight());
        for (int i = 0; i < image.getHeight(); i++){
            for (int j = 0; j < image.getWidth(); j++){
                dOutput.writeInt(reader.getArgb(j, i));
            }
        }
        sOutput.close();
        dOutput.close();
    }

    /**
     * Extract 8-bit Alpha value of color from 32-bit representation of the color in the format
     * described by the INT_ARGB PixelFormat type.
     * @param argb the 32-bit representation of the color
     * @return the 8-bit Alpha value of the color.
     */
    private static int argbToAlpha(int argb) {
        final int bitShift = 24;
        return argb >> bitShift;
    }

    /**
     * Extract 8-bit Red value of color from 32-bit representation of the color in the format
     * described by the INT_ARGB PixelFormat type.
     * @param argb the 32-bit representation of the color
     * @return the 8-bit Red value of the color.
     */
    private static int argbToRed(int argb) {
        final int bitShift = 16;
        final int mask = 0xff;
        return (argb >> bitShift) & mask;
    }

    /**
     * Extract 8-bit Green value of color from 32-bit representation of the color in the format
     * described by the INT_ARGB PixelFormat type.
     * @param argb the 32-bit representation of the color
     * @return the 8-bit Green value of the color.
     */
    private static int argbToGreen(int argb) {
        final int bitShift = 8;
        final int mask = 0xff;
        return (argb >> bitShift) & mask;
    }

    /**
     * Extract 8-bit Blue value of color from 32-bit representation of the color in the format
     * described by the INT_ARGB PixelFormat type.
     * @param argb the 32-bit representation of the color
     * @return the 8-bit Blue value of the color.
     */
    private static int argbToBlue(int argb) {
        final int bitShift = 0;
        final int mask = 0xff;
        return (argb >> bitShift) & mask;
    }

    /**
     * Converts argb components into a single int that represents the argb value of a color.
     * @param a the 8-bit Alpha channel value of the color
     * @param r the 8-bit Red channel value of the color
     * @param g the 8-bit Green channel value of the color
     * @param b the 8-bit Blue channel value of the color
     * @return a 32-bit representation of the color in the format described by the
     * INT_ARGB PixelFormat type.
     */
    private static int argbToInt(int a, int r, int g, int b) {
        final int alphaShift = 24;
        final int redShift = 16;
        final int greenShift = 8;
        final int mask = 0xff;
        return a << alphaShift | ((r & mask) << redShift) | (g & mask) << greenShift | b & mask;
    }
}
