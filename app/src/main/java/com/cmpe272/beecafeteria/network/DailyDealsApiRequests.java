package com.cmpe272.beecafeteria.network;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.cmpe272.beecafeteria.BuildConfig;
import com.cmpe272.beecafeteria.modelResponse.DailyDeals;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by Rushil on 11/28/2015.
 */
public class DailyDealsApiRequests {

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
    public static GsonGetRequest<ArrayList<DailyDeals>> getDailyDealsRequest
    (
            @NonNull final Response.Listener<ArrayList<DailyDeals>> listener,
            @NonNull final Response.ErrorListener errorListener
    )
    {
        final String url = BuildConfig.apiDomainName + "/dailydeals/fetch";

        return new GsonGetRequest<>
                (
                        url,
                        new TypeToken<ArrayList<DailyDeals>>() {}.getType(),
                        gson,
                        listener,
                        errorListener
                );
    }

}
