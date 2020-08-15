package com.cardswipe;

import android.app.Activity;
import android.app.Application;

import com.cardswipe.database.DBHelper;
import com.cardswipe.webservices.APICallback;
import com.cardswipe.webservices.APICalling;

public class MyApplication extends Application {

    private static MyApplication instance;

    private APICallback apiCallback;

    public APICalling apiCalling;

    private DBHelper db;

    public static MyApplication getInstance() {
        return instance;
    }
    private Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        db = new DBHelper(instance);
    }

    public APICallback getApiCallback() {
        return apiCallback;
    }

    public void setApiCallback(APICallback apiCallback) {
        this.apiCallback = apiCallback;
    }

    public APICalling getApiCalling() {
        return apiCalling;
    }

    public void setApiCalling(APICalling apiCalling) {
        this.apiCalling = apiCalling;
    }

    public DBHelper getDb() {
        return db;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        try {
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if(db!=null){
            db.close();
        }
    }
}
