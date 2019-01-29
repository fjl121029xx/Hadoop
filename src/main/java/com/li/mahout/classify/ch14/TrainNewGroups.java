package com.li.mahout.classify.ch14;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Iterables;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.apache.mahout.classifier.sgd.L1;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.vectorizer.encoders.ConstantValueEncoder;
import org.apache.mahout.vectorizer.encoders.Dictionary;
import org.apache.mahout.vectorizer.encoders.FeatureVectorEncoder;
import org.apache.mahout.vectorizer.encoders.StaticWordValueEncoder;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class TrainNewGroups {


    private static final int FEATURES = 10000;
    private static Multiset<String> overallCounts;


    public static void main(String[] args) throws Exception {

        File base = new File(args[0]);
        overallCounts = HashMultiset.create();

        /**
         * 1.建立向量编码器
         */
        Map<String, Set<Integer>> traceDictionary = new TreeMap<>();
        FeatureVectorEncoder encoder = new StaticWordValueEncoder("body");
        encoder.setProbes(2);
        encoder.setTraceDictionary(traceDictionary);
        FeatureVectorEncoder bias = new ConstantValueEncoder("Intercept");
        bias.setTraceDictionary(traceDictionary);
        FeatureVectorEncoder lines = new ConstantValueEncoder("Lines");
        lines.setTraceDictionary(traceDictionary);
        Dictionary newsGroups = new Dictionary();

        /**
         * 2.配置学习算法
         */
        try (OnlineLogisticRegression learningAlgorithm = new OnlineLogisticRegression(
                20, FEATURES, new L1()
        ).alpha(1).stepOffset(1000)
                .decayExponent(0.9)
                .lambda(3.0e-5)
                .learningRate(20)) {

            /**
             * 3.访问数据文件
             */
            List<File> files = new ArrayList<>();
            for (File newsgroup : base.listFiles()) {

                newsGroups.intern(newsgroup.getName());
                files.addAll(Arrays.asList(newsgroup.listFiles()));
            }

            Collections.shuffle(files);
            System.out.printf("%d training files\n");

            /**
             * 4.数据词条化前的预备工作
             */
            double averageLL = 0.0;
            double averageCorrect = 0.0;
            double averageLingCount = 0.0;

            int k = 0;
            double step = 0.0;
            int[] bumps = new int[]{1, 2, 5};
            double lineCount = 0;

            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);

            for (File file : files) {

                BufferedReader reader = new BufferedReader(new FileReader(file));
                String ng = file.getParentFile().getName();
                int actual = newsGroups.intern(ng);
                Multiset<String> words = ConcurrentHashMultiset.create();

                String line = reader.readLine();
                while (line != null && line.length() > 0) {

                    if (line.startsWith("Lines:")) {

//                        String count = Iterables.get(onColon.split(line),1);
                    }
                }


            }
        }


    }
}
