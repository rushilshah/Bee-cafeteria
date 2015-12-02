package com.cmpe272.beecafeteria.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmpe272.beecafeteria.R;
import com.cmpe272.beecafeteria.base.App;
import com.cmpe272.beecafeteria.modelResponse.PostResponse;
import com.cmpe272.beecafeteria.network.GsonPostRequest;
import com.cmpe272.beecafeteria.network.RegisterApiRequests;
import com.cmpe272.beecafeteria.others.SessionManager;
import com.cmpe272.beecafeteria.others.Utils;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by Rushil on 11/13/2015.
 */
public class GCMRegistrationIntentService extends IntentService {

    // abbreviated tag name
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String GCM_TOKEN = "gcmToken";



    public GCMRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Make a call to Instance API
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = getResources().getString(R.string.gcm_defaultSenderId);
        try {
            // request token that will be used by the server to send push notifications
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            Log.d(TAG, "GCM Registration Token: " + token);

            // save token
            sharedPreferences.edit().putString(GCM_TOKEN, token).apply();
            // pass along this data
            sendRegistrationToServer(token);
            // Subscribe to topic channels
            subscribeTopics(token);
        } catch (IOException e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();

        }

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Utils.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String token) {
        // send network request

        String strUsername  =SessionManager.getUserDetails(this).get(SessionManager.KEY_EMAIL);

        final GsonPostRequest gsonPostRequest =
                RegisterApiRequests.postGCMRegistration
                        (
                                new Response.Listener<PostResponse>() {
                                    @Override
                                    public void onResponse(PostResponse dummyObject) {
                                        // Deal with the DummyObject here
                                        //mProgressBar.setVisibility(View.GONE);
                                        //mContent.setVisibility(View.VISIBLE);
                                        ///setData(dummyObject);
                                        // if registration sent was successful, store a boolean that indicates whether the generated token has been sent to server
                                        Log.d("Server GCM registration","Successful");
                                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(GCMRegistrationIntentService.this);
                                        sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();
                                    }
                                }
                                ,
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // Deal with the error here
                                        //mProgressBar.setVisibility(View.GONE);
                                        //mErrorView.setVisibility(View.VISIBLE);
                                        // if registration sent was successful, store a boolean that indicates whether the generated token has been sent to server
                                        Log.d("Server GCM registration","Failed");
                                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(GCMRegistrationIntentService.this);
                                        sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
                                    }
                                },
                                strUsername, token
                        );

        App.addRequest(gsonPostRequest, TAG);



    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}
