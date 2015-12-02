package com.cmpe272.beecafeteria.network;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.cmpe272.beecafeteria.BuildConfig;
import com.cmpe272.beecafeteria.modelResponse.MenuItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by Rushil on 11/28/2015.
 */
public class MenuApiReqests {

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
    public static GsonGetRequest<ArrayList<MenuItem>> getMenuFromOutletRequest
    (

            @NonNull final Response.Listener<ArrayList<MenuItem>> listener,
            @NonNull final Response.ErrorListener errorListener,
            String outletName
    )
    {
        final String url = BuildConfig.apiDomainName + "/menucontent/read/"+outletName;

        return new GsonGetRequest<>
                (
                        url,
                        new TypeToken<ArrayList<MenuItem>>() {}.getType(),
                        gson,
                        listener,
                        errorListener
                );
    }
}
