package pt.ulisboa.tecnico.meic.sirs;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class AESKeyGenerator {

    public static void main(String[] args) throws Exception {

        // check args
        if (args.length != 2) {
            System.err.println("Usage: AESKeyGenerator [r|w] <key-file>");
            return;
        }

        final String mode = args[0];
        final String keyPath = args[1];

        if (mode.toLowerCase().startsWith("w")) {
            System.out.println("Generate and save keys");
            write(keyPath);
        } else {
            System.out.println("Load keys");
            read(keyPath);
        }

        System.out.println("Done.");
    }

    public static void write(String keyPath) throws GeneralSecurityException, IOException {
        // get an AES private key
        System.out.println("Generating AES key ..." );
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        Key key = keyGen.generateKey();
        System.out.println( "Finish generating AES key" );
        byte[] encoded = key.getEncoded();
        System.out.println("Key:");
        System.out.println(printHexBinary(encoded));

        System.out.println("Writing key to '" + keyPath + "' ..." );

        FileOutputStream fos = new FileOutputStream(keyPath);
        fos.write(encoded);
        fos.close();
    }

    public static Key read(String keyPath) throws GeneralSecurityException, IOException {
        System.out.println("Reading key from file " + keyPath + " ...");
        FileInputStream fis = new FileInputStream(keyPath);
        byte[] encoded = new byte[fis.available()];
        fis.read(encoded);
        fis.close();

        return new SecretKeySpec(encoded, 0, 16, "AES");
    }

}
