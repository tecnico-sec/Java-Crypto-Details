package pt.ulisboa.tecnico.meic.sirs;

import javax.crypto.Cipher;
import java.io.IOException;

/**
 * Decrypts a file with the AES algorithm in multiple modes, with a given, appropriate AES key
 */
public class FileAESDecipher {
    public static void main(String[] args) throws IOException {

        if(args.length != 4) {
            System.err.println("This program decrypts a file with AES.");
            System.err.println("Usage: FileAESDecipher [inputFile] [AESKeyFile] [ECB|CBC|OFB] [outputFile]");
            return;
        }

        final String inputFile = args[0];
        final String keyFile = args[1];
        final String mode = args[2].toUpperCase();
        final String outputFile = args[3];

        if( !(mode.equals("ECB") || mode.equals("CBC") || mode.equals("OFB")) ) {
            System.err.println("The modes of operation must be ECB, CBC or OFB.");
            return;
        }

        AESCipherByteArrayMixer cipher = new AESCipherByteArrayMixer(Cipher.DECRYPT_MODE);
        cipher.setParameters(keyFile, mode);
        FileMixer.mix(inputFile, outputFile, cipher);

    }
}
