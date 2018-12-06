package com.li.mahout.recommend.system;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

import java.io.File;
import java.util.List;

public class RecommenderIntro {

    public static void main(String[] args) throws Exception {


        DataModel model = new FileDataModel(new File("data/ml100K/ratings.data"));

        PearsonCorrelationSimilarity similarity = new PearsonCorrelationSimilarity(model);

        NearestNUserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);

        Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

        List<RecommendedItem> recommendations = recommender.recommend(1, 10);

        for (RecommendedItem recommendation : recommendations) {

            System.out.println(recommendation);
        }
    }
}
