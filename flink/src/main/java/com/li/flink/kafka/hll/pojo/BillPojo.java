
package com.li.flink.kafka.hll.pojo;

import java.text.ParseException;
import java.util.List;
import javax.annotation.Generated;

import com.alibaba.fastjson.JSON;
import com.google.gson.annotations.SerializedName;
import lombok.*;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
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


    public static BillPojo fromString(String billStr) throws ParseException {

        return JSON.parseObject(billStr, BillPojo.class);
    }

}
