package pt.ulisboa.tecnico.meic.sirs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileMixer {

    /**
     * Applies a function passed as an argument to the byte[] representations of two files
     * @param file1Path filesystem location of the first file
     * @param file2Path filesystem location of the second file
     * @param outputFilePath filesystem location of the output file
     * @param manipulationFunction an object implementing the manipulationFunction method, to be applied to the files
     * @throws IOException
     */
    public static void mix(String file1Path, String file2Path, String outputFilePath, ByteArrayMixer manipulationFunction) throws IOException {
        // get the bytes from file 1
        byte[] file1 = Files.readAllBytes(new File(file1Path).toPath());

        // get the bytes from image 2
        byte[] file2 = Files.readAllBytes(new File(file2Path).toPath());

        // apply the manipulationFunction to the byte arrays
        byte[] outputBytes = manipulationFunction.mix(file1, file2);

        // write the output byte array to disk
        Files.write(new File(outputFilePath).toPath(), outputBytes);
    }
    /**
     * Applies a function passed as an argument to the byte[] representations of one image file
     * @param filePath filesystem location of the image file
     * @param outputFilePath filesystem location of the output image file
     * @param manipulationFunction an object implementing the manipulationFunction method, to be applied to the image
     * @throws IOException
     */
    public static void mix(String filePath, String outputFilePath, ByteArrayMixer manipulationFunction) throws IOException {
        // get the bytes from the file
        byte[] file1 = Files.readAllBytes(new File(filePath).toPath());

        // apply the manipulationFunction to the byte array
        byte[] outputBytes = manipulationFunction.mix(file1, null);

        // write the output byte array to disk
        Files.write(new File(outputFilePath).toPath(), outputBytes);
    }

}
