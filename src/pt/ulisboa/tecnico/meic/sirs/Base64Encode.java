package pt.ulisboa.tecnico.meic.sirs;

import javax.xml.bind.DatatypeConverter;

/**
 * Encode a file with Base64
 */
public class Base64Encode {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("This program encodes a file with base64.");
            System.out.println("Usage: base64-encode <inputFile> <outputFile>");
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
