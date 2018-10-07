package com.tysovsky.charbo;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService implements NetworkResponseListener {
    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences.edit().putString("FIREBASE_TOKEN", refreshedToken).apply();

        NetworkManager networkManager = new NetworkManager();
        networkManager.UpdateFirebaseId(MainActivity.currentUserId, refreshedToken, this);
    }

    @Override
    public void OnNetworkResponse(String ResponseType, Object data) {

    }
}