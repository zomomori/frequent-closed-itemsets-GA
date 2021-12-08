package GAFCI;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/**
 * Run the algorithm from here
 * input file is context.txt
 * output.txt will display the output
 * */

public class MainTestGAFCI {

    public static void main(String [] arg) throws IOException {

		String input = fileToPath("input.txt");
        String output = "output.txt";

        double minsup = 0.4; // means a minsup of 2 transaction (we used a relative count)

        // Applying the algorithm
       GACFI_algo algorithm = new GACFI_algo();

        algorithm.runAlgorithm(input, minsup, output);
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTestGAFCI.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}

