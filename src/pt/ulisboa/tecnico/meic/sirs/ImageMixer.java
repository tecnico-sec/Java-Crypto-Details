package pt.ulisboa.tecnico.meic.sirs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

public class ImageMixer {

    /**
     * Converts a BufferedImage into a byte[]
     * @param image the image to convert
     * @return a byte array with the pixels from the image (one pixel is one bit)
     */
    private static byte[] imageToByteArray(BufferedImage image) {
        DataBuffer imageDataBuffer = image.getRaster().getDataBuffer();
        return ((DataBufferByte)imageDataBuffer).getData();
    }

    /**
     * Applies a function passed as an argument to the byte[] representations of two image files
     * @param image1FilePath filesystem location of the first image file
     * @param image2FilePath filesystem location of the second image file
     * @param outputImageFilePath filesystem location of the output image file
     * @param manipulationFunction an object implementing the manipulationFunction method, to be applied to the images
     * @throws IOException
     */
    public static void mix(String image1FilePath, String image2FilePath, String outputImageFilePath, ByteArrayMixer manipulationFunction) throws IOException {
        // get the bytes from image 1
        BufferedImage image1 = ImageIO.read(new File(image1FilePath));
        byte[] image1Bytes = imageToByteArray(image1);

        // get the bytes from image 2
        BufferedImage image2 = ImageIO.read(new File(image2FilePath));
        byte[] image2Bytes = imageToByteArray(image2);

        // apply the manipulationFunction to the byte arrays
        byte[] outputBytes = manipulationFunction.mix(image1Bytes, image2Bytes);

        // convert the output byte array into an image and write it to disk
        BufferedImage outputImage = getImageFromArray(outputBytes, image1.getWidth(), image1.getHeight());
        writeImageToFile(outputImage, outputImageFilePath);
    }
    /**
     * Applies a function passed as an argument to the byte[] representations of one image file
     * @param imageFilePath filesystem location of the image file
     * @param outputImageFilePath filesystem location of the output image file
     * @param manipulationFunction an object implementing the manipulationFunction method, to be applied to the image
     * @throws IOException
     */
    public static void mix(String imageFilePath, String outputImageFilePath, ByteArrayMixer manipulationFunction) throws IOException {
        // get the bytes from the image
        BufferedImage image = ImageIO.read(new File(imageFilePath));
        byte[] imageBytes = imageToByteArray(image);

        // apply the manipulationFunction to the byte array of the image
        byte[] outputBytes = manipulationFunction.mix(imageBytes, null);

        // convert the output byte array into an image and write it to disk
        BufferedImage outputImage = getImageFromArray(outputBytes, image.getWidth(), image.getHeight());
        writeImageToFile(outputImage, outputImageFilePath);
    }

    /**
     * Creates an image file with randomized pixels
     * @param imageFilePath filesystem location of the image file
     */
    public static void createRandomImage(String imageFilePath, int width, int height) {
        byte[] imageArray = new byte[width*height];

        // generate random pixels on array
        // (we multiply by 2^8=256 because one byte equals 8 pixels)
        for (int p = 0; p < width*height; p++) {
            imageArray[p] = (byte)(Math.random() * 256);
        }

        // convert the output byte array into an image and write it to disk
        BufferedImage image = getImageFromArray(imageArray, width, height);
        writeImageToFile(image, imageFilePath);
    }

    /**
     * Takes a flat array of width*height pixels, and turns it into a BufferedImage.
     * This is only ready to deal with images with a colormap of 1 bit. Proceed at your own risk.
     * @param pixels the input array with the pixels
     * @param width the width of the image to generate
     * @param height the height of the image to generate
     * @return the BufferedImage corresponding to the input array
     */
    private static BufferedImage getImageFromArray(byte[] pixels, int width, int height) {
        // create a binary color model, with a b/w colormap, where white (index 0) is the transparent color
        // I sure hope there's an easier way of accomplishing this...
        byte[] colorMap = { (byte) 0xff, (byte) 0x00 };
        IndexColorModel colorModel = new IndexColorModel(1, 2, colorMap, colorMap, colorMap, 0);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY, colorModel);
        DataBuffer dataBuffer = new DataBufferByte(pixels, pixels.length);
        SampleModel sampleModel = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE, width, height, 1);
        WritableRaster raster = Raster.createWritableRaster(sampleModel, dataBuffer, new Point(0, 0));
        image.setData(raster);
        return image;
    }

    /**
     * Write a BufferedImage to a PNG file.
     * @param image the BufferedImage to write to disk
     * @param file the filesystem location of the image file
     */
    private static void writeImageToFile(BufferedImage image, String file) {
        try {
            File outputFile = new File(file);
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            System.out.println("Error while writing image file: " + e);
        }
    }
}
