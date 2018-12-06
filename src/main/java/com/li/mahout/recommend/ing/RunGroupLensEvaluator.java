package com.li.mahout.recommend.ing;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.similarity.precompute.example.GroupLensDataModel;

import java.io.File;
import java.io.IOException;

public class RunGroupLensEvaluator {

    public static void main(String[] args) throws IOException, TasteException {

        DataModel model = new GroupLensDataModel(new File("data/intro.csv"));

        RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();

        RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
            @Override
            public Recommender buildRecommender(DataModel dataModel) throws TasteException {

                UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
                /**
                 * 固定大小领域
                 */
//                UserNeighborhood neighborhood = new NearestNUserNeighborhood(500, similarity, model);
                /**
                 * 基于阈值的领域
                 */
                UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.75, similarity, model);
                return new GenericUserBasedRecommender(model, neighborhood, similarity);
            }
        };

        double score = evaluator.evaluate(recommenderBuilder, null, model, 0.90, 0.10);
        System.out.println(score);

    }
}
