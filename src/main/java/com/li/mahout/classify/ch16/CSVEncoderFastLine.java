package com.li.mahout.classify.ch16;

import org.apache.commons.io.FileUtils;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.vectorizer.encoders.ConstantValueEncoder;
import org.apache.mahout.vectorizer.encoders.FeatureVectorEncoder;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class CSVEncoderFastLine {

    private static final int FIELDS = 10;

    public static void main(String[] args) throws Exception {

        FeatureVectorEncoder[] encoder = new FeatureVectorEncoder[FIELDS];
        for (int i = 0; i < FIELDS; i++) {

            encoder[i] = new ConstantValueEncoder("v" + i);
        }

        long t0 = System.currentTimeMillis();
        Vector v = new DenseVector(1000);

        ByteBuffer buf = ByteBuffer.wrap(FileUtils.readFileToByteArray(new File(args[0])));
        FastLine line = FastLine.read(buf);
        while (line != null) {
            v.assign(0);
            for (int i = 0; i < FIELDS; i++) {
                encoder[i].addToVector((byte[]) null, line.getDouble(i), v);
            }
            line = FastLine.read(buf);
        }

        System.out.printf("\nElapsed time = %.3f s\n", (System.currentTimeMillis() - t0) / 1000.0);
    }
}
