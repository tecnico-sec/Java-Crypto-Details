package pt.ulisboa.tecnico.meic.sirs;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

/**
 * Encode a file with Base64
 */
public class Base64Encode {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("This program encodes a file with base64.");
            System.out.println("Usage: Base64Encode <inputFile> <outputFile>");
            return;
        }

        FileMixer.mix(args[0], args[1], new ByteArrayMixer() {
            @Override
            public byte[] mix(byte[] byteArray1, byte[] byteArray2) {
                return DatatypeConverter.printBase64Binary(byteArray1).getBytes();
            }
        });
    }
}
