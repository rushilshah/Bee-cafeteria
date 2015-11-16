package com.cmpe272.beecafeteria.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Rushil on 11/13/2015.
 */
@Parcel
public class PostResponse {

    String status;

    @SerializedName("status")
    public String getStatus() {
        return status;
    }
}
