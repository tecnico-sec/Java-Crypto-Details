package pt.ulisboa.tecnico.meic.sirs;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;

/**
 * Implementation of the AES cipher as a ByteArrayMixer
 */
public class AESCipherByteArrayMixer implements ByteArrayMixer {

    private String keyFile;
    private String mode;
    private int opmode;

    public void setParameters(String keyFile, String mode) {
        this.keyFile = keyFile;
        this.mode = mode;
    }

    public AESCipherByteArrayMixer(int opmode) {
        this.opmode = opmode;
    }
    @Override
    public byte[] mix(byte[] byteArray, byte[] byteArray2) {

        try {

            Key key = AESKeyGenerator.read(keyFile);

            // get a DES cipher object and print the provider
            Cipher cipher = Cipher.getInstance("AES/" + mode + "/PKCS5Padding");
            System.out.println(cipher.getProvider().getInfo());


            System.out.println("Ciphering ...");
            if(!mode.equals("ECB")) {
                // look! A null IV!
                cipher.init(this.opmode, key, new IvParameterSpec(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }));
            } else {
                cipher.init(this.opmode, key);
            }

            return cipher.doFinal(byteArray);

        } catch (Exception e) {
            // Pokemon exception handling!
            e.printStackTrace();
        }

        return null;

    }
}
