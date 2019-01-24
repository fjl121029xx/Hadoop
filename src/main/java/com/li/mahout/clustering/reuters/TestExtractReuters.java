package com.li.mahout.clustering.reuters;

import org.apache.lucene.benchmark.utils.ExtractReuters;

import java.io.File;
import java.io.IOException;


public class TestExtractReuters {

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        File inputFolder = new File("/home/cdw/reuters");
        File outputFolder = new File("/home/cdw/extracted");
        ExtractReuters extractor = new ExtractReuters(inputFolder, outputFolder);
        extractor.extract();
    }
}
