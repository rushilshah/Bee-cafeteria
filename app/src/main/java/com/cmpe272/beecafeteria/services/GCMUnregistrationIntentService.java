package com.cmpe272.beecafeteria.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cmpe272.beecafeteria.R;
import com.cmpe272.beecafeteria.others.Utils;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by Rushil on 11/22/2015.
 */
public class GCMUnregistrationIntentService extends IntentService {

    // abbreviated tag name
    private static final String TAG = "UnRegIntentService";
    private static final String[] TOPICS = {"global"};

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String GCM_TOKEN = "gcmToken";


    public GCMUnregistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Make a call to Instance API
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = getResources().getString(R.string.gcm_defaultSenderId);
        try {

            // Subscribe to topic channels
            unsubscribeTopics(PreferenceManager.getDefaultSharedPreferences(this).getString(GCM_TOKEN, null));

            // request token that will be used by the server to send push notifications
            instanceID.deleteToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            Log.d(TAG, "GCM token unregistered");

            // save token
            sharedPreferences.edit().remove(GCM_TOKEN).apply();
            // pass along this data
            sendUnregistrationToServer();

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

    private void sendUnregistrationToServer() {
        // send network request

        // if registration sent was successful, store a boolean that indicates whether the generated token has been sent to server
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();

    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void unsubscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        if (token != null) {
            for (String topic : TOPICS) {
                pubSub.unsubscribe(token, "/topics/" + topic);
            }
        }
    }
    // [END subscribe_topics]

}