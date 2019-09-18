package com.li.mahout.recommend.ing;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.eval.LoadEvaluator;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.similarity.precompute.example.GroupLensDataModel;

import java.io.File;
import java.io.IOException;

public class RunGroupLensDataModel {

    public static void main(String[] args) throws IOException, TasteException {


        DataModel model = new GroupLensDataModel(new File("H:/workspaces/ml-10m/ml-10M100K/ratings.dat"));

        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);

        UserNeighborhood neighborhood = new NearestNUserNeighborhood(100, similarity, model);

        Recommender recommender =new GenericUserBasedRecommender(model,neighborhood,similarity);
        LoadEvaluator.runLoad(recommender);
    }
}
