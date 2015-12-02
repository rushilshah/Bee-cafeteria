package com.cmpe272.beecafeteria.modelResponse;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Rushil on 11/20/2015.
 */
@Parcel
public class DailyDeals {

    @SerializedName("counter_id")
    private String providerName;

    @SerializedName("deal_name")
    private String dailyDealTitle;

    @SerializedName("deal_conditions")
    private String dailyDealDescription;

    @SerializedName("start_date")
    private String dealStartTime;

    @SerializedName("end_date")
    private String dealEndTime;

    @SerializedName("price")
    private String price;

    public String getDailyDealTitle() {
        return dailyDealTitle;
    }

    public void setDailyDealTitle(String dailyDealTitle) {
        this.dailyDealTitle = dailyDealTitle;
    }

    public String getDailyDealDescription() {
        return dailyDealDescription;
    }

    public void setDailyDealDescription(String dailyDealDescription) {
        this.dailyDealDescription = dailyDealDescription;
    }


    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getDealStartTime() {
        return dealStartTime;
    }

    public void setDealStartTime(String dealStartTime) {
        this.dealStartTime = dealStartTime;
    }

    public String getDealEndTime() {
        return dealEndTime;
    }

    public void setDealEndTime(String dealEndTime) {
        this.dealEndTime = dealEndTime;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
