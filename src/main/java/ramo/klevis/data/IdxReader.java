package ramo.klevis.data;

import org.apache.spark.sql.catalyst.expressions.In;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

public class IdxReader {

    public java.util.List<LabeledImage> loadData() throws IOException {
        FileInputStream inImage = null;
        FileInputStream inLabel = null;

        String inputImagePath = "src/main/resources/train-images.idx3-ubyte";
        String inputLabelPath = "src/main/resources/train-labels.idx1-ubyte";

        String outputPath = "src/main/resources/";

        HashMap<Integer, Integer> labelMap = new HashMap<>();

        try {
            inImage = new FileInputStream(inputImagePath);
            inLabel = new FileInputStream(inputLabelPath);

            int magicNumberImages = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfImages = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfRows = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfColumns = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());

            int magicNumberLabels = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());
            int numberOfLabels = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());

            BufferedImage image = new BufferedImage(numberOfColumns, numberOfRows, BufferedImage.TYPE_INT_ARGB);
            int numberOfPixels = numberOfRows * numberOfColumns;
            double[] imgPixels = new double[numberOfPixels];
            java.util.List<LabeledImage> all = new ArrayList();

            long start = System.currentTimeMillis();
            int currentLabel = 0;
            for (int i = 0; i < 500; i++) {

                if (i % 1000 == 0) {
                    System.out.println("Number of images extracted: " + i);
                }

                for (int p = 0; p < numberOfPixels; p++) {
                    int gray = 255 - inImage.read();
                    imgPixels[p] = 0xFF000000 | (gray << 16) | (gray << 8) | gray;
                }

//                image.setRGB(0, 0, numberOfColumns, numberOfRows, imgPixels, 0, numberOfColumns);

                int label = inLabel.read();
                Integer labelID = labelMap.get(label);
                if (labelID == null) {
                    labelID = currentLabel;
                    labelMap.put(label, currentLabel++);
                }

                all.add(new LabeledImage(label, imgPixels));
//                File outputfile = new File(outputPath + label + "_0" + hashMap[label] + ".png");

//                ImageIO.write(image, "png", outputfile);
            }
            System.out.println("Time in seconds" + ((System.currentTimeMillis() - start) / 1000d));
            return all;

        } finally {
            if (inImage != null) {
                inImage.close();
            }
            if (inLabel != null) {
                inLabel.close();
            }
        }
    }

}