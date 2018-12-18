package com.li.mahout.recommend.petricek;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import java.util.Collection;

public class GenderItemSimilarity implements ItemSimilarity {


    @Override
    public double itemSimilarity(long itemID1, long itemID2) throws TasteException {
        return 0;
    }

    @Override
    public double[] itemSimilarities(long itemID1, long[] itemID2s) throws TasteException {
        return new double[0];
    }

    @Override
    public long[] allSimilarItemIDs(long itemID) throws TasteException {
        return new long[0];
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {

    }
}
