package com.cmpe272.beecafeteria.modelResponse;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Rushil on 11/20/2015.
 */
@Parcel
public class Order {

    @SerializedName("name")
    String outlateName;

    @SerializedName("menu_items")
    String orderDescription;

    @SerializedName("order_status")
    String status;

    @SerializedName("amount")
    String total;

    @SerializedName("created_at")
    String orderDate;

    public String getOutlateName() {
        return outlateName;
    }

    public void setOutlateName(String outlateName) {
        this.outlateName = outlateName;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
}
