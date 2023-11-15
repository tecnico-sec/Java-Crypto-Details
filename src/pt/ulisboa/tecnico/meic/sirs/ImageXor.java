package pt.ulisboa.tecnico.meic.sirs;

/**
 * XORs two images
 */
public class ImageXor {

    public static void main(String[] args) throws Exception {

        if (args.length < 3) {
            System.out.println("This program XORs two b/w image files.");
            System.out.println("Usage: image-xor <inputFile1.png> <inputFile2.png> <outputFile.png>");
            return;
        }

        ImageMixer.mix(args[0], args[1], args[2], new ByteArrayMixer() {
            @Override
            public byte[] mix(byte[] byteArray1, byte[] byteArray2) {
                byte[] outputBytes = new byte[byteArray1.length];
                for (int i = 0; i < outputBytes.length; i++) {
                    outputBytes[i] = (byte) (byteArray1[i] ^ byteArray2[i]);
                }
                return outputBytes;
            }
        });

    }
}
