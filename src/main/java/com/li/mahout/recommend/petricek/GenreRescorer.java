package com.li.mahout.recommend.petricek;

import org.apache.mahout.cf.taste.recommender.IDRescorer;

public class GenreRescorer implements IDRescorer {

    @Override
    public double rescore(long id, double originalScore) {
        return 0;
    }

    @Override
    public boolean isFiltered(long id) {
        return false;
    }
}
