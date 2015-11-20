package com.cmpe272.beecafeteria.network;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.cmpe272.beecafeteria.BuildConfig;
import com.cmpe272.beecafeteria.modelResponse.PostResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * Created by Rushil on 11/20/2015.
 */
public class RegisterApiRequests {

    private static final Gson gson = new GsonBuilder()
            .create();
    /**

    /**
     * An REgister call that would send data and store user through volley
     *
     * @param listener is the listener for the success response
     * @param errorListener is the listener for the error response
     * @param params parameters tht will be send in post request
     *
     * @return {@link GsonPostRequest}
     */
    public static GsonPostRequest postRegisterrequest
    (
            @NonNull final Response.Listener<PostResponse> listener,
            @NonNull final Response.ErrorListener errorListener,
            String... params
    )
    {
        final String url = BuildConfig.apiDomainName + "/register";

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", params[0]);
        jsonObject.addProperty("pass", params[1]);
        jsonObject.addProperty("email", params[2]);
        jsonObject.addProperty("phone", params[3]);

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
