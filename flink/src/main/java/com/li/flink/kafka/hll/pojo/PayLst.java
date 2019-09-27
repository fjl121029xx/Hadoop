
package com.li.flink.kafka.hll.pojo;

import javax.annotation.Generated;

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
public class PayLst {

    @SerializedName("actionTime")
    private Long mActionTime;
    @SerializedName("checkoutBy")
    private String mCheckoutBy;
    @SerializedName("createBy")
    private String mCreateBy;
    @SerializedName("createTime")
    private Long mCreateTime;
    @SerializedName("creditAmount")
    private Double mCreditAmount;
    @SerializedName("dataSource")
    private Long mDataSource;
    @SerializedName("debitAmount")
    private Double mDebitAmount;
    @SerializedName("debitAmountGiftTotal")
    private Double mDebitAmountGiftTotal;
    @SerializedName("deviceName")
    private String mDeviceName;
    @SerializedName("giftItemNoLst")
    private String mGiftItemNoLst;
    @SerializedName("groupID")
    private Long mGroupID;
    @SerializedName("isFeeJoinReceived")
    private Long mIsFeeJoinReceived;
    @SerializedName("isIncludeScore")
    private Long mIsIncludeScore;
    @SerializedName("isJoinReceived")
    private Long mIsJoinReceived;
    @SerializedName("isPhysicalEvidence")
    private Long mIsPhysicalEvidence;
    @SerializedName("itemID")
    private Long mItemID;
    @SerializedName("memberCardID")
    private String mMemberCardID;
    @SerializedName("orderKey")
    private String mOrderKey;
    @SerializedName("orderStatus")
    private Long mOrderStatus;
    @SerializedName("payRemark")
    private String mPayRemark;
    @SerializedName("paySubjectAllDiscountAmount")
    private Double mPaySubjectAllDiscountAmount;
    @SerializedName("paySubjectCode")
    private String mPaySubjectCode;
    @SerializedName("paySubjectCount")
    private Long mPaySubjectCount;
    @SerializedName("paySubjectDiscountAmount")
    private Double mPaySubjectDiscountAmount;
    @SerializedName("paySubjectFeeAmount")
    private Double mPaySubjectFeeAmount;
    @SerializedName("paySubjectGroupName")
    private String mPaySubjectGroupName;
    @SerializedName("paySubjectKey")
    private String mPaySubjectKey;
    @SerializedName("paySubjectName")
    private String mPaySubjectName;
    @SerializedName("paySubjectRate")
    private Double mPaySubjectRate;
    @SerializedName("paySubjectRealAmount")
    private Double mPaySubjectRealAmount;
    @SerializedName("paySubjectReceivedAmount")
    private Double mPaySubjectReceivedAmount;
    @SerializedName("payTransNo")
    private String mPayTransNo;
    @SerializedName("programType")
    private Long mProgramType;
    @SerializedName("promotionID")
    private String mPromotionID;
    @SerializedName("reportDate")
    private Long mReportDate;
    @SerializedName("serverMAC")
    private String mServerMAC;
    @SerializedName("shopID")
    private Long mShopID;
    @SerializedName("shopName")
    private String mShopName;

    public Long getActionTime() {
        return mActionTime;
    }

    public void setActionTime(Long actionTime) {
        mActionTime = actionTime;
    }

    public String getCheckoutBy() {
        return mCheckoutBy;
    }

    public void setCheckoutBy(String checkoutBy) {
        mCheckoutBy = checkoutBy;
    }

    public String getCreateBy() {
        return mCreateBy;
    }

    public void setCreateBy(String createBy) {
        mCreateBy = createBy;
    }

    public Long getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(Long createTime) {
        mCreateTime = createTime;
    }

    public Double getCreditAmount() {
        return mCreditAmount;
    }

    public void setCreditAmount(Double creditAmount) {
        mCreditAmount = creditAmount;
    }

    public Long getDataSource() {
        return mDataSource;
    }

    public void setDataSource(Long dataSource) {
        mDataSource = dataSource;
    }

    public Double getDebitAmount() {
        return mDebitAmount;
    }

    public void setDebitAmount(Double debitAmount) {
        mDebitAmount = debitAmount;
    }

    public Double getDebitAmountGiftTotal() {
        return mDebitAmountGiftTotal;
    }

    public void setDebitAmountGiftTotal(Double debitAmountGiftTotal) {
        mDebitAmountGiftTotal = debitAmountGiftTotal;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public void setDeviceName(String deviceName) {
        mDeviceName = deviceName;
    }

    public String getGiftItemNoLst() {
        return mGiftItemNoLst;
    }

    public void setGiftItemNoLst(String giftItemNoLst) {
        mGiftItemNoLst = giftItemNoLst;
    }

    public Long getGroupID() {
        return mGroupID;
    }

    public void setGroupID(Long groupID) {
        mGroupID = groupID;
    }

    public Long getIsFeeJoinReceived() {
        return mIsFeeJoinReceived;
    }

    public void setIsFeeJoinReceived(Long isFeeJoinReceived) {
        mIsFeeJoinReceived = isFeeJoinReceived;
    }

    public Long getIsIncludeScore() {
        return mIsIncludeScore;
    }

    public void setIsIncludeScore(Long isIncludeScore) {
        mIsIncludeScore = isIncludeScore;
    }

    public Long getIsJoinReceived() {
        return mIsJoinReceived;
    }

    public void setIsJoinReceived(Long isJoinReceived) {
        mIsJoinReceived = isJoinReceived;
    }

    public Long getIsPhysicalEvidence() {
        return mIsPhysicalEvidence;
    }

    public void setIsPhysicalEvidence(Long isPhysicalEvidence) {
        mIsPhysicalEvidence = isPhysicalEvidence;
    }

    public Long getItemID() {
        return mItemID;
    }

    public void setItemID(Long itemID) {
        mItemID = itemID;
    }

    public String getMemberCardID() {
        return mMemberCardID;
    }

    public void setMemberCardID(String memberCardID) {
        mMemberCardID = memberCardID;
    }

    public String getOrderKey() {
        return mOrderKey;
    }

    public void setOrderKey(String orderKey) {
        mOrderKey = orderKey;
    }

    public Long getOrderStatus() {
        return mOrderStatus;
    }

    public void setOrderStatus(Long orderStatus) {
        mOrderStatus = orderStatus;
    }

    public String getPayRemark() {
        return mPayRemark;
    }

    public void setPayRemark(String payRemark) {
        mPayRemark = payRemark;
    }

    public Double getPaySubjectAllDiscountAmount() {
        return mPaySubjectAllDiscountAmount;
    }

    public void setPaySubjectAllDiscountAmount(Double paySubjectAllDiscountAmount) {
        mPaySubjectAllDiscountAmount = paySubjectAllDiscountAmount;
    }

    public String getPaySubjectCode() {
        return mPaySubjectCode;
    }

    public void setPaySubjectCode(String paySubjectCode) {
        mPaySubjectCode = paySubjectCode;
    }

    public Long getPaySubjectCount() {
        return mPaySubjectCount;
    }

    public void setPaySubjectCount(Long paySubjectCount) {
        mPaySubjectCount = paySubjectCount;
    }

    public Double getPaySubjectDiscountAmount() {
        return mPaySubjectDiscountAmount;
    }

    public void setPaySubjectDiscountAmount(Double paySubjectDiscountAmount) {
        mPaySubjectDiscountAmount = paySubjectDiscountAmount;
    }

    public Double getPaySubjectFeeAmount() {
        return mPaySubjectFeeAmount;
    }

    public void setPaySubjectFeeAmount(Double paySubjectFeeAmount) {
        mPaySubjectFeeAmount = paySubjectFeeAmount;
    }

    public String getPaySubjectGroupName() {
        return mPaySubjectGroupName;
    }

    public void setPaySubjectGroupName(String paySubjectGroupName) {
        mPaySubjectGroupName = paySubjectGroupName;
    }

    public String getPaySubjectKey() {
        return mPaySubjectKey;
    }

    public void setPaySubjectKey(String paySubjectKey) {
        mPaySubjectKey = paySubjectKey;
    }

    public String getPaySubjectName() {
        return mPaySubjectName;
    }

    public void setPaySubjectName(String paySubjectName) {
        mPaySubjectName = paySubjectName;
    }

    public Double getPaySubjectRate() {
        return mPaySubjectRate;
    }

    public void setPaySubjectRate(Double paySubjectRate) {
        mPaySubjectRate = paySubjectRate;
    }

    public Double getPaySubjectRealAmount() {
        return mPaySubjectRealAmount;
    }

    public void setPaySubjectRealAmount(Double paySubjectRealAmount) {
        mPaySubjectRealAmount = paySubjectRealAmount;
    }

    public Double getPaySubjectReceivedAmount() {
        return mPaySubjectReceivedAmount;
    }

    public void setPaySubjectReceivedAmount(Double paySubjectReceivedAmount) {
        mPaySubjectReceivedAmount = paySubjectReceivedAmount;
    }

    public String getPayTransNo() {
        return mPayTransNo;
    }

    public void setPayTransNo(String payTransNo) {
        mPayTransNo = payTransNo;
    }

    public Long getProgramType() {
        return mProgramType;
    }

    public void setProgramType(Long programType) {
        mProgramType = programType;
    }

    public String getPromotionID() {
        return mPromotionID;
    }

    public void setPromotionID(String promotionID) {
        mPromotionID = promotionID;
    }

    public Long getReportDate() {
        return mReportDate;
    }

    public void setReportDate(Long reportDate) {
        mReportDate = reportDate;
    }

    public String getServerMAC() {
        return mServerMAC;
    }

    public void setServerMAC(String serverMAC) {
        mServerMAC = serverMAC;
    }

    public Long getShopID() {
        return mShopID;
    }

    public void setShopID(Long shopID) {
        mShopID = shopID;
    }

    public String getShopName() {
        return mShopName;
    }

    public void setShopName(String shopName) {
        mShopName = shopName;
    }

}
