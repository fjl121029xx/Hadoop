package com.li.mahout.recommend.data;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

public class RunGenericDataModel {

    public static void main(String[] args) {

        FastByIDMap<PreferenceArray> preferences = new FastByIDMap<>();


        PreferenceArray user1Prefs = new GenericUserPreferenceArray(2);

        user1Prefs.setUserID(0, 1L);

        user1Prefs.setItemID(0, 101L);
        user1Prefs.setValue(0, 2.0f);
        user1Prefs.setItemID(1, 102L);
        user1Prefs.setValue(1, 3.0f);

        preferences.put(1, user1Prefs);

        DataModel model = new GenericDataModel(preferences);
        System.out.println(model);

    }
}
