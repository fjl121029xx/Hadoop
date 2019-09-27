
package com.li.flink.kafka.hll.pojo;

import java.text.ParseException;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class BillPojo {

    @SerializedName("foodLst")
    private List<FoodLst> mFoodLst;
    @SerializedName("foodPromotionList")
    private List<Object> mFoodPromotionList;
    @SerializedName("master")
    private Master mMaster;
    @SerializedName("payLst")
    private List<PayLst> mPayLst;

    public List<FoodLst> getFoodLst() {
        return mFoodLst;
    }

    public void setFoodLst(List<FoodLst> foodLst) {
        mFoodLst = foodLst;
    }

    public List<Object> getFoodPromotionList() {
        return mFoodPromotionList;
    }

    public void setFoodPromotionList(List<Object> foodPromotionList) {
        mFoodPromotionList = foodPromotionList;
    }

    public Master getMaster() {
        return mMaster;
    }

    public void setMaster(Master master) {
        mMaster = master;
    }

    public List<PayLst> getPayLst() {
        return mPayLst;
    }

    public void setPayLst(List<PayLst> payLst) {
        mPayLst = payLst;
    }


    public static BillPojo fromString(String eventStr) throws ParseException {
//
//        String[] split = eventStr.split("=");
//
//        String userId = split[2];
//        int userPlayTime = Integer.parseInt(JSON.parseObject(split[3]).get("userPlayTime").toString());
//
//
//        return new BillPojo(userId, userPlayTime, sdf.parse(split[4]).getTime());
//        return new KafkaEvent(split[0],Integer.parseInt(split[1]),Long.parseLong(split[2]));

        return null;
    }

}
