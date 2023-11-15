package pt.ulisboa.tecnico.meic.sirs;

/**
 * Generates a random b/w 1-bit image
 */
public class RandomImageGenerator {

    public static void main(String args[]) {

        if (args.length < 3) {
            System.out.println("This program generates a 1-bit image file with randomized pixels");
            System.out.println("Usage: random-image-gen <file.png> <height> <width>");
            return;
        }

        ImageMixer.createRandomImage(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }

}
