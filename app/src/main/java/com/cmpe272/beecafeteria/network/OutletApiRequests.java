package com.cmpe272.beecafeteria.network;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.cmpe272.beecafeteria.BuildConfig;
import com.cmpe272.beecafeteria.modelResponse.Outlet;
import com.cmpe272.beecafeteria.modelResponse.PostResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by Rushil on 12/1/2015.
 */
public class OutletApiRequests {

    private static final Gson gson = new GsonBuilder()
            .create();

    /**
     * Returns a GsonGetRequest<PostResponse>
     *
     * @param listener is the listener for the correct answer
     * @param errorListener is the listener for the error response
     *
     * @return {@link GsonGetRequest}
     */
    public static GsonGetRequest<ArrayList<Outlet>> getOutletRequest
    (
            @NonNull final Response.Listener<ArrayList<Outlet>> listener,
            @NonNull final Response.ErrorListener errorListener
    )
    {
        final String url = BuildConfig.apiDomainName + "/counter/fetch";

        return new GsonGetRequest<>
                (
                        url,
                        new TypeToken<ArrayList<Outlet>>() {}.getType(),
                        gson,
                        listener,
                        errorListener
                );
    }


    /**
     * An Order call that would send data and place users order
     *
     * @param listener is the listener for the success response
     * @param errorListener is the listener for the error response
     * @param params parameters tht will be send in post request
     *
     * @return {@link GsonPostRequest}
     */
    public static GsonPostRequest postPlaceOrder
    (
            @NonNull final Response.Listener<PostResponse> listener,
            @NonNull final Response.ErrorListener errorListener,
            String... params
    )
    {
        final String url = BuildConfig.apiDomainName + "/order/create";

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("counter_id", params[0]);
        jsonObject.addProperty("email", params[1]);
        jsonObject.addProperty("ddf", params[2]);
        jsonObject.addProperty("menu_items", params[3]);
        jsonObject.addProperty("amount", params[4]);
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
                        new TypeToken<PostResponse>() {}.getType(),
                        gson,
                        listener,
                        errorListener
                );

        gsonPostRequest.setShouldCache(false);

        return gsonPostRequest;
    }

    /**
     * An Request to server to send notification
     *
     * @param listener is the listener for the success response
     * @param errorListener is the listener for the error response
     * @param params parameters tht will be send in post request
     *
     * @return {@link GsonPostRequest}
     */
    public static GsonPostRequest postNotificationRequest
    (
            @NonNull final Response.Listener<PostResponse> listener,
            @NonNull final Response.ErrorListener errorListener,
            String... params
    )
    {
        final String url = BuildConfig.apiDomainName + "/menucontent/notification/"+params[0];

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", params[1]);
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
                        new TypeToken<PostResponse>() {}.getType(),
                        gson,
                        listener,
                        errorListener
                );

        gsonPostRequest.setShouldCache(false);

        return gsonPostRequest;
    }



}