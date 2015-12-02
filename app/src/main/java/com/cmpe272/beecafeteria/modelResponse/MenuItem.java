package com.cmpe272.beecafeteria.modelResponse;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Rushil on 11/26/2015.
 */
@Parcel
public class MenuItem {

    @SerializedName("primary_id")
    private String itemId;

    @SerializedName("item_name")
    private String itemName;

    @SerializedName("price")
    private String price;

    private boolean selected;

    private String quantity = "0";

    private String messgae;


    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setMessgae(String messgae) {
        this.messgae = messgae;
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getMessgae() {
        return messgae;
    }
}
