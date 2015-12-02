package com.cmpe272.beecafeteria.network;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.cmpe272.beecafeteria.BuildConfig;
import com.cmpe272.beecafeteria.modelResponse.Order;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by Rushil on 12/1/2015.
 */
public class OrderApiRequests {
    private static final Gson gson = new GsonBuilder()
            .create();
    /**
     * An Login call that would send data and authenticate user through volley
     *
     * @param listener is the listener for the success response
     * @param errorListener is the listener for the error response
     * @param params parameters tht will be send in post request
     *
     * @return {@link GsonPostRequest}
     */
    public static GsonPostRequest getOrdersRequests
    (
            @NonNull final Response.Listener<ArrayList<Order>> listener,
            @NonNull final Response.ErrorListener errorListener,
            String... params
    )
    {
        final String url = BuildConfig.apiDomainName + "/order/read";

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", params[0]);

        /*final JsonArray squareGuys = new JsonArray();
        final JsonObject dev1 = new JsonObject();
        final JsonObject dev2 = new JsonObject();
        dev1.addProperty("name", "Jake Wharton");
        dev2.addProperty("name", "Jesse Wilson");
        squareGuys.add(dev1);
        squareGuys.add(dev2);

        jsonObject.add("squareGuys", squareGuys);*/

        final GsonPostRequest gsonPostRequest = new GsonPostRequest<>
                (
                        url,
                        jsonObject.toString(),
                        new TypeToken<ArrayList<Order>>() {}.getType(),
                        gson,
                        listener,
                        errorListener
                );

        gsonPostRequest.setShouldCache(false);

        return gsonPostRequest;
    }

}
