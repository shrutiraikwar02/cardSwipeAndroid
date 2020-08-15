package com.cardswipe.webservices;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cardswipe.MyApplication;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private MyApplication app;
    private NetworkChangeInterface networkChangeInterface;

    @Override
    public void onReceive(Context context, Intent intent) {
        app = (MyApplication) context.getApplicationContext();
        Activity a = app.getCurrentActivity();
        try {
            this.networkChangeInterface = (NetworkChangeInterface) a;
        } catch (final ClassCastException e) {
            throw new ClassCastException(a.toString() + " must implement OnCompleteListener");
        }

        if (intent.getExtras() != null) {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                //Toast.makeText(context, "Si hay",1).show();
                // MyApp.getBus().post(new NetworkStateChanged(true));
                // there is Internet connection
                if (networkChangeInterface != null) {
                    networkChangeInterface.onConnect();
                }
            } else
                // MyApp.getBus().post(new NetworkStateChanged(false));
                // if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                // no Internet connection, send network state changed
                //   MyApp.getBus().post(new NetworkStateChanged(false));
                //EventBus.getDefault().post(new NetworkStateChanged(false));
                if (networkChangeInterface != null) {
                    networkChangeInterface.onDisConnect();
                }
            // }
        }
    }
}
