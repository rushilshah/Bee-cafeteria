package com.cmpe272.beecafeteria.modelResponse;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Rushil on 11/20/2015.
 */
@Parcel
public class Outlet {

    @SerializedName("name")
    private String outletName;

    @SerializedName("primary_id")
    private String outletId;

    @SerializedName("rating")
    private String expenseRating;

    public String getOutletName() {
        return outletName;
    }

    public String getExpenseRating() {
        return expenseRating;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public void setExpenseRating(String expenseRating) {
        this.expenseRating = expenseRating;
    }

    public String getOutletId() {
        return outletId;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }
}
