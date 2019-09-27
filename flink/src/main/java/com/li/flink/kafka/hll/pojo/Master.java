
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
public class Master {

    @SerializedName("actionTime")
    private Long mActionTime;
    @SerializedName("alertFlagLst")
    private String mAlertFlagLst;
    @SerializedName("areaName")
    private String mAreaName;
    @SerializedName("cardKey")
    private String mCardKey;
    @SerializedName("cardNo")
    private String mCardNo;
    @SerializedName("cardTransAfterRemark")
    private String mCardTransAfterRemark;
    @SerializedName("cardTransID")
    private String mCardTransID;
    @SerializedName("channelKey")
    private String mChannelKey;
    @SerializedName("channelName")
    private String mChannelName;
    @SerializedName("channelOrderKey")
    private String mChannelOrderKey;
    @SerializedName("channelOrderNo")
    private String mChannelOrderNo;
    @SerializedName("channelOrderTime")
    private String mChannelOrderTime;
    @SerializedName("channelUserID")
    private String mChannelUserID;
    @SerializedName("channelUserImage")
    private String mChannelUserImage;
    @SerializedName("channelUserKey")
    private String mChannelUserKey;
    @SerializedName("checkoutBy")
    private String mCheckoutBy;
    @SerializedName("checkoutTime")
    private Long mCheckoutTime;
    @SerializedName("createBy")
    private String mCreateBy;
    @SerializedName("customerName")
    private String mCustomerName;
    @SerializedName("dataSource")
    private Long mDataSource;
    @SerializedName("deviceCode")
    private String mDeviceCode;
    @SerializedName("deviceGroupID")
    private String mDeviceGroupID;
    @SerializedName("deviceGroupName")
    private String mDeviceGroupName;
    @SerializedName("deviceKey")
    private String mDeviceKey;
    @SerializedName("deviceName")
    private String mDeviceName;
    @SerializedName("deviceOrderNo")
    private String mDeviceOrderNo;
    @SerializedName("discountBy")
    private String mDiscountBy;
    @SerializedName("discountRange")
    private Long mDiscountRange;
    @SerializedName("discountRate")
    private Double mDiscountRate;
    @SerializedName("discountWayKey")
    private String mDiscountWayKey;
    @SerializedName("discountWayName")
    private String mDiscountWayName;
    @SerializedName("FJZCount")
    private Long mFJZCount;
    @SerializedName("foodAlert")
    private String mFoodAlert;
    @SerializedName("foodAmount")
    private Double mFoodAmount;
    @SerializedName("foodCount")
    private Long mFoodCount;
    @SerializedName("foodNameDetail")
    private String mFoodNameDetail;
    @SerializedName("groupID")
    private Long mGroupID;
    @SerializedName("invoiceAmount")
    private Double mInvoiceAmount;
    @SerializedName("invoiceBy")
    private String mInvoiceBy;
    @SerializedName("invoiceTaxAmount")
    private Double mInvoiceTaxAmount;
    @SerializedName("invoiceTaxRate")
    private Double mInvoiceTaxRate;
    @SerializedName("invoiceTaxpayerIdentCode")
    private String mInvoiceTaxpayerIdentCode;
    @SerializedName("invoiceTitle")
    private String mInvoiceTitle;
    @SerializedName("isCreatedByLoginUser")
    private Long mIsCreatedByLoginUser;
    @SerializedName("isTestOrder")
    private Long mIsTestOrder;
    @SerializedName("isVipPrice")
    private Long mIsVipPrice;
    @SerializedName("itemID")
    private Long mItemID;
    @SerializedName("lockedFlag")
    private String mLockedFlag;
    @SerializedName("modifyOrderLog")
    private String mModifyOrderLog;
    @SerializedName("moneyWipeZeroType")
    private Long mMoneyWipeZeroType;
    @SerializedName("netOrderTypeCode")
    private String mNetOrderTypeCode;
    @SerializedName("orderKey")
    private String mOrderKey;
    @SerializedName("orderMD5")
    private String mOrderMD5;
    @SerializedName("orderNo")
    private String mOrderNo;
    @SerializedName("orderRemark")
    private String mOrderRemark;
    @SerializedName("orderStatus")
    private Long mOrderStatus;
    @SerializedName("orderSubType")
    private Long mOrderSubType;
    @SerializedName("paidAmount")
    private Double mPaidAmount;
    @SerializedName("payAlert")
    private String mPayAlert;
    @SerializedName("person")
    private Long mPerson;
    @SerializedName("primaryDeviceType")
    private String mPrimaryDeviceType;
    @SerializedName("primaryDeviceVersion")
    private String mPrimaryDeviceVersion;
    @SerializedName("promotionAmount")
    private Double mPromotionAmount;
    @SerializedName("promotionDesc")
    private String mPromotionDesc;
    @SerializedName("reportDate")
    private Long mReportDate;
    @SerializedName("reviewBy")
    private String mReviewBy;
    @SerializedName("reviewTime")
    private Long mReviewTime;
    @SerializedName("sendCouponAmount")
    private Double mSendCouponAmount;
    @SerializedName("sendCouponRemark")
    private String mSendCouponRemark;
    @SerializedName("sendFoodAmount")
    private Double mSendFoodAmount;
    @SerializedName("serverMAC")
    private String mServerMAC;
    @SerializedName("shiftName")
    private String mShiftName;
    @SerializedName("shiftTime")
    private Long mShiftTime;
    @SerializedName("shopID")
    private Long mShopID;
    @SerializedName("shopName")
    private String mShopName;
    @SerializedName("specialStatAmount")
    private Double mSpecialStatAmount;
    @SerializedName("startTime")
    private Long mStartTime;
    @SerializedName("tableName")
    private String mTableName;
    @SerializedName("timeNameCheckout")
    private String mTimeNameCheckout;
    @SerializedName("timeNameStart")
    private String mTimeNameStart;
    @SerializedName("uploadTime")
    private Long mUploadTime;
    @SerializedName("userAddress")
    private String mUserAddress;
    @SerializedName("userMobile")
    private String mUserMobile;
    @SerializedName("userName")
    private String mUserName;
    @SerializedName("userSex")
    private Long mUserSex;
    @SerializedName("waiterBy")
    private String mWaiterBy;
    @SerializedName("YJZCount")
    private Long mYJZCount;

    public Long getActionTime() {
        return mActionTime;
    }

    public void setActionTime(Long actionTime) {
        mActionTime = actionTime;
    }

    public String getAlertFlagLst() {
        return mAlertFlagLst;
    }

    public void setAlertFlagLst(String alertFlagLst) {
        mAlertFlagLst = alertFlagLst;
    }

    public String getAreaName() {
        return mAreaName;
    }

    public void setAreaName(String areaName) {
        mAreaName = areaName;
    }

    public String getCardKey() {
        return mCardKey;
    }

    public void setCardKey(String cardKey) {
        mCardKey = cardKey;
    }

    public String getCardNo() {
        return mCardNo;
    }

    public void setCardNo(String cardNo) {
        mCardNo = cardNo;
    }

    public String getCardTransAfterRemark() {
        return mCardTransAfterRemark;
    }

    public void setCardTransAfterRemark(String cardTransAfterRemark) {
        mCardTransAfterRemark = cardTransAfterRemark;
    }

    public String getCardTransID() {
        return mCardTransID;
    }

    public void setCardTransID(String cardTransID) {
        mCardTransID = cardTransID;
    }

    public String getChannelKey() {
        return mChannelKey;
    }

    public void setChannelKey(String channelKey) {
        mChannelKey = channelKey;
    }

    public String getChannelName() {
        return mChannelName;
    }

    public void setChannelName(String channelName) {
        mChannelName = channelName;
    }

    public String getChannelOrderKey() {
        return mChannelOrderKey;
    }

    public void setChannelOrderKey(String channelOrderKey) {
        mChannelOrderKey = channelOrderKey;
    }

    public String getChannelOrderNo() {
        return mChannelOrderNo;
    }

    public void setChannelOrderNo(String channelOrderNo) {
        mChannelOrderNo = channelOrderNo;
    }

    public String getChannelOrderTime() {
        return mChannelOrderTime;
    }

    public void setChannelOrderTime(String channelOrderTime) {
        mChannelOrderTime = channelOrderTime;
    }

    public String getChannelUserID() {
        return mChannelUserID;
    }

    public void setChannelUserID(String channelUserID) {
        mChannelUserID = channelUserID;
    }

    public String getChannelUserImage() {
        return mChannelUserImage;
    }

    public void setChannelUserImage(String channelUserImage) {
        mChannelUserImage = channelUserImage;
    }

    public String getChannelUserKey() {
        return mChannelUserKey;
    }

    public void setChannelUserKey(String channelUserKey) {
        mChannelUserKey = channelUserKey;
    }

    public String getCheckoutBy() {
        return mCheckoutBy;
    }

    public void setCheckoutBy(String checkoutBy) {
        mCheckoutBy = checkoutBy;
    }

    public Long getCheckoutTime() {
        return mCheckoutTime;
    }

    public void setCheckoutTime(Long checkoutTime) {
        mCheckoutTime = checkoutTime;
    }

    public String getCreateBy() {
        return mCreateBy;
    }

    public void setCreateBy(String createBy) {
        mCreateBy = createBy;
    }

    public String getCustomerName() {
        return mCustomerName;
    }

    public void setCustomerName(String customerName) {
        mCustomerName = customerName;
    }

    public Long getDataSource() {
        return mDataSource;
    }

    public void setDataSource(Long dataSource) {
        mDataSource = dataSource;
    }

    public String getDeviceCode() {
        return mDeviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        mDeviceCode = deviceCode;
    }

    public String getDeviceGroupID() {
        return mDeviceGroupID;
    }

    public void setDeviceGroupID(String deviceGroupID) {
        mDeviceGroupID = deviceGroupID;
    }

    public String getDeviceGroupName() {
        return mDeviceGroupName;
    }

    public void setDeviceGroupName(String deviceGroupName) {
        mDeviceGroupName = deviceGroupName;
    }

    public String getDeviceKey() {
        return mDeviceKey;
    }

    public void setDeviceKey(String deviceKey) {
        mDeviceKey = deviceKey;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public void setDeviceName(String deviceName) {
        mDeviceName = deviceName;
    }

    public String getDeviceOrderNo() {
        return mDeviceOrderNo;
    }

    public void setDeviceOrderNo(String deviceOrderNo) {
        mDeviceOrderNo = deviceOrderNo;
    }

    public String getDiscountBy() {
        return mDiscountBy;
    }

    public void setDiscountBy(String discountBy) {
        mDiscountBy = discountBy;
    }

    public Long getDiscountRange() {
        return mDiscountRange;
    }

    public void setDiscountRange(Long discountRange) {
        mDiscountRange = discountRange;
    }

    public Double getDiscountRate() {
        return mDiscountRate;
    }

    public void setDiscountRate(Double discountRate) {
        mDiscountRate = discountRate;
    }

    public String getDiscountWayKey() {
        return mDiscountWayKey;
    }

    public void setDiscountWayKey(String discountWayKey) {
        mDiscountWayKey = discountWayKey;
    }

    public String getDiscountWayName() {
        return mDiscountWayName;
    }

    public void setDiscountWayName(String discountWayName) {
        mDiscountWayName = discountWayName;
    }

    public Long getFJZCount() {
        return mFJZCount;
    }

    public void setFJZCount(Long fJZCount) {
        mFJZCount = fJZCount;
    }

    public String getFoodAlert() {
        return mFoodAlert;
    }

    public void setFoodAlert(String foodAlert) {
        mFoodAlert = foodAlert;
    }

    public Double getFoodAmount() {
        return mFoodAmount;
    }

    public void setFoodAmount(Double foodAmount) {
        mFoodAmount = foodAmount;
    }

    public Long getFoodCount() {
        return mFoodCount;
    }

    public void setFoodCount(Long foodCount) {
        mFoodCount = foodCount;
    }

    public String getFoodNameDetail() {
        return mFoodNameDetail;
    }

    public void setFoodNameDetail(String foodNameDetail) {
        mFoodNameDetail = foodNameDetail;
    }

    public Long getGroupID() {
        return mGroupID;
    }

    public void setGroupID(Long groupID) {
        mGroupID = groupID;
    }

    public Double getInvoiceAmount() {
        return mInvoiceAmount;
    }

    public void setInvoiceAmount(Double invoiceAmount) {
        mInvoiceAmount = invoiceAmount;
    }

    public String getInvoiceBy() {
        return mInvoiceBy;
    }

    public void setInvoiceBy(String invoiceBy) {
        mInvoiceBy = invoiceBy;
    }

    public Double getInvoiceTaxAmount() {
        return mInvoiceTaxAmount;
    }

    public void setInvoiceTaxAmount(Double invoiceTaxAmount) {
        mInvoiceTaxAmount = invoiceTaxAmount;
    }

    public Double getInvoiceTaxRate() {
        return mInvoiceTaxRate;
    }

    public void setInvoiceTaxRate(Double invoiceTaxRate) {
        mInvoiceTaxRate = invoiceTaxRate;
    }

    public String getInvoiceTaxpayerIdentCode() {
        return mInvoiceTaxpayerIdentCode;
    }

    public void setInvoiceTaxpayerIdentCode(String invoiceTaxpayerIdentCode) {
        mInvoiceTaxpayerIdentCode = invoiceTaxpayerIdentCode;
    }

    public String getInvoiceTitle() {
        return mInvoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        mInvoiceTitle = invoiceTitle;
    }

    public Long getIsCreatedByLoginUser() {
        return mIsCreatedByLoginUser;
    }

    public void setIsCreatedByLoginUser(Long isCreatedByLoginUser) {
        mIsCreatedByLoginUser = isCreatedByLoginUser;
    }

    public Long getIsTestOrder() {
        return mIsTestOrder;
    }

    public void setIsTestOrder(Long isTestOrder) {
        mIsTestOrder = isTestOrder;
    }

    public Long getIsVipPrice() {
        return mIsVipPrice;
    }

    public void setIsVipPrice(Long isVipPrice) {
        mIsVipPrice = isVipPrice;
    }

    public Long getItemID() {
        return mItemID;
    }

    public void setItemID(Long itemID) {
        mItemID = itemID;
    }

    public String getLockedFlag() {
        return mLockedFlag;
    }

    public void setLockedFlag(String lockedFlag) {
        mLockedFlag = lockedFlag;
    }

    public String getModifyOrderLog() {
        return mModifyOrderLog;
    }

    public void setModifyOrderLog(String modifyOrderLog) {
        mModifyOrderLog = modifyOrderLog;
    }

    public Long getMoneyWipeZeroType() {
        return mMoneyWipeZeroType;
    }

    public void setMoneyWipeZeroType(Long moneyWipeZeroType) {
        mMoneyWipeZeroType = moneyWipeZeroType;
    }

    public String getNetOrderTypeCode() {
        return mNetOrderTypeCode;
    }

    public void setNetOrderTypeCode(String netOrderTypeCode) {
        mNetOrderTypeCode = netOrderTypeCode;
    }

    public String getOrderKey() {
        return mOrderKey;
    }

    public void setOrderKey(String orderKey) {
        mOrderKey = orderKey;
    }

    public String getOrderMD5() {
        return mOrderMD5;
    }

    public void setOrderMD5(String orderMD5) {
        mOrderMD5 = orderMD5;
    }

    public String getOrderNo() {
        return mOrderNo;
    }

    public void setOrderNo(String orderNo) {
        mOrderNo = orderNo;
    }

    public String getOrderRemark() {
        return mOrderRemark;
    }

    public void setOrderRemark(String orderRemark) {
        mOrderRemark = orderRemark;
    }

    public Long getOrderStatus() {
        return mOrderStatus;
    }

    public void setOrderStatus(Long orderStatus) {
        mOrderStatus = orderStatus;
    }

    public Long getOrderSubType() {
        return mOrderSubType;
    }

    public void setOrderSubType(Long orderSubType) {
        mOrderSubType = orderSubType;
    }

    public Double getPaidAmount() {
        return mPaidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        mPaidAmount = paidAmount;
    }

    public String getPayAlert() {
        return mPayAlert;
    }

    public void setPayAlert(String payAlert) {
        mPayAlert = payAlert;
    }

    public Long getPerson() {
        return mPerson;
    }

    public void setPerson(Long person) {
        mPerson = person;
    }

    public String getPrimaryDeviceType() {
        return mPrimaryDeviceType;
    }

    public void setPrimaryDeviceType(String primaryDeviceType) {
        mPrimaryDeviceType = primaryDeviceType;
    }

    public String getPrimaryDeviceVersion() {
        return mPrimaryDeviceVersion;
    }

    public void setPrimaryDeviceVersion(String primaryDeviceVersion) {
        mPrimaryDeviceVersion = primaryDeviceVersion;
    }

    public Double getPromotionAmount() {
        return mPromotionAmount;
    }

    public void setPromotionAmount(Double promotionAmount) {
        mPromotionAmount = promotionAmount;
    }

    public String getPromotionDesc() {
        return mPromotionDesc;
    }

    public void setPromotionDesc(String promotionDesc) {
        mPromotionDesc = promotionDesc;
    }

    public Long getReportDate() {
        return mReportDate;
    }

    public void setReportDate(Long reportDate) {
        mReportDate = reportDate;
    }

    public String getReviewBy() {
        return mReviewBy;
    }

    public void setReviewBy(String reviewBy) {
        mReviewBy = reviewBy;
    }

    public Long getReviewTime() {
        return mReviewTime;
    }

    public void setReviewTime(Long reviewTime) {
        mReviewTime = reviewTime;
    }

    public Double getSendCouponAmount() {
        return mSendCouponAmount;
    }

    public void setSendCouponAmount(Double sendCouponAmount) {
        mSendCouponAmount = sendCouponAmount;
    }

    public String getSendCouponRemark() {
        return mSendCouponRemark;
    }

    public void setSendCouponRemark(String sendCouponRemark) {
        mSendCouponRemark = sendCouponRemark;
    }

    public Double getSendFoodAmount() {
        return mSendFoodAmount;
    }

    public void setSendFoodAmount(Double sendFoodAmount) {
        mSendFoodAmount = sendFoodAmount;
    }

    public String getServerMAC() {
        return mServerMAC;
    }

    public void setServerMAC(String serverMAC) {
        mServerMAC = serverMAC;
    }

    public String getShiftName() {
        return mShiftName;
    }

    public void setShiftName(String shiftName) {
        mShiftName = shiftName;
    }

    public Long getShiftTime() {
        return mShiftTime;
    }

    public void setShiftTime(Long shiftTime) {
        mShiftTime = shiftTime;
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

    public Double getSpecialStatAmount() {
        return mSpecialStatAmount;
    }

    public void setSpecialStatAmount(Double specialStatAmount) {
        mSpecialStatAmount = specialStatAmount;
    }

    public Long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Long startTime) {
        mStartTime = startTime;
    }

    public String getTableName() {
        return mTableName;
    }

    public void setTableName(String tableName) {
        mTableName = tableName;
    }

    public String getTimeNameCheckout() {
        return mTimeNameCheckout;
    }

    public void setTimeNameCheckout(String timeNameCheckout) {
        mTimeNameCheckout = timeNameCheckout;
    }

    public String getTimeNameStart() {
        return mTimeNameStart;
    }

    public void setTimeNameStart(String timeNameStart) {
        mTimeNameStart = timeNameStart;
    }

    public Long getUploadTime() {
        return mUploadTime;
    }

    public void setUploadTime(Long uploadTime) {
        mUploadTime = uploadTime;
    }

    public String getUserAddress() {
        return mUserAddress;
    }

    public void setUserAddress(String userAddress) {
        mUserAddress = userAddress;
    }

    public String getUserMobile() {
        return mUserMobile;
    }

    public void setUserMobile(String userMobile) {
        mUserMobile = userMobile;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public Long getUserSex() {
        return mUserSex;
    }

    public void setUserSex(Long userSex) {
        mUserSex = userSex;
    }

    public String getWaiterBy() {
        return mWaiterBy;
    }

    public void setWaiterBy(String waiterBy) {
        mWaiterBy = waiterBy;
    }

    public Long getYJZCount() {
        return mYJZCount;
    }

    public void setYJZCount(Long yJZCount) {
        mYJZCount = yJZCount;
    }

}
