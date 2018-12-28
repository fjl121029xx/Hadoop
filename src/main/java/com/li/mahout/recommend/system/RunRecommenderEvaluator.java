package com.li.mahout.recommend.system;


import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

import java.io.File;
import java.io.IOException;

/**
 * 配置并评估一个推荐程序
 */
public class RunRecommenderEvaluator {

    public static void main(String[] args) throws Exception {

        RandomUtils.useTestSeed();

        DataModel dataModel = new FileDataModel(new File("doc/mahout/example/intro.csv"));

        RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();

        RecommenderBuilder builder = model -> {

            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            NearestNUserNeighborhood neighborhood = new NearestNUserNeighborhood(3, similarity, model);

            return new GenericUserBasedRecommender(model, neighborhood, similarity);
        };

        double score = evaluator.evaluate(builder, null, dataModel, 0.7, 1);
        System.out.println(score);

    }
}
