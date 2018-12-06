package com.li.mahout.recommend.data;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;

public class RunGenericBooleanPrefDataModel {

    public static void main(String[] args) throws IOException, TasteException {

        DataModel model = new GenericBooleanPrefDataModel(
                GenericBooleanPrefDataModel.toDataMap(new FileDataModel(new File("ml100K/ua.base"))));

        RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();

        RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
            @Override
            public Recommender buildRecommender(DataModel dataModel) throws TasteException {

                UserSimilarity similarity = new LogLikelihoodSimilarity(model);
                UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, model);
                return new GenericUserBasedRecommender(model, neighborhood, similarity);
            }
        };

        DataModelBuilder modelBuilder = new DataModelBuilder() {
            @Override
            public DataModel buildDataModel(FastByIDMap<PreferenceArray> trainingData) {

                return new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(trainingData));
            }
        };

        double score = evaluator.evaluate(recommenderBuilder,modelBuilder,model,0.9,1.0);

        System.out.println(score);

    }
}
