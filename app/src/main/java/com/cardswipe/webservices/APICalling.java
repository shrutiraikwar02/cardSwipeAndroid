package com.cardswipe.webservices;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.cardswipe.MyApplication;
import com.cardswipe.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * The type Api calling.
 */
// this is the class for calling API using retrofit

public class APICalling extends BaseRequestParser implements ServiceCallback<JsonElement> {

    /**
     * The constant BASE_URL.
     */
    private static final String BASE_URL = "https://randomuser.me";
    private static MyApplication myApp;
    private String TAG = "APICalling";
    private Activity activity;
    private Gson gson;
    private String result = "";

    public APICalling(Context context) {
        activity = (Activity) context;
        mProgressBarHandler = new ProgressBarHandler(context);
    }


    /**
     * Web service interface rest api.
     *
     * @return the rest api
     */
    public static RestAPI webServiceInterface() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES);
        // add your other interceptors …
        // add logging as last interceptor
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        //creating service for adapter with restAPI interface
        return retrofit.create(RestAPI.class);
    }


    public <T> Object getDataObject(JsonObject jsonObject, Class<T> t) {
        Gson gsm = new Gson();
        if (null != jsonObject && !TextUtils.isEmpty(jsonObject.toString())) {
            return gsm.fromJson(jsonObject.toString(), t);
        }
        return null;
    }

    public <T> List<T> getDataList(JsonObject jsonObject, String key, final Class<T> t) {
        Gson gsm = new Gson();
        List<T> list = null;
        list = new ArrayList<>();
        if (null != jsonObject) {
            JsonArray jsonArray = jsonObject.getAsJsonArray(key);
            for (int i = 0; i < jsonArray.size(); i++) {
                Object obj = gsm.fromJson(jsonArray.get(i), t);
                if (obj != null && t.isAssignableFrom(obj.getClass()))
                    list.add(t.cast(obj));
            }
        }
        return list;
    }

    public <T> List<T> castCollection(List srcList, Class<T> clas) {
        List<T> list = new ArrayList<T>();
        for (Object obj : srcList) {
            if (obj != null && clas.isAssignableFrom(obj.getClass()))
                list.add(clas.cast(obj));
        }
        return list;
    }

    /**
     * Call api.
     *
     * @param app  the app
     * @param call the call
     * @param from the from
     */
    public void callAPI(final MyApplication app, Call<JsonElement> call, final String from) {
        gson = new Gson();
        final APICallback apiCallback = app.getApiCallback();
        showLoader(activity);


        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                int statusCode = response.code();
                hideLoader(activity);
                try {
                    if (statusCode >= 200 && statusCode < 300 && response.isSuccessful()) {
                        JsonElement user1 = response.body();
                        result = gson.toJson(user1);
                        if (apiCallback != null) {
                            JsonElement element = gson.fromJson(result, JsonElement.class);
                            JsonObject jsonObj = element.getAsJsonObject();
                            apiCallback.apiCallback(jsonObj, from);
                        }
                    } else if (statusCode == 401) {
                        unauthenticated(response);
                    } else if (statusCode >= 400 && statusCode < 500) {
                        clientError(response);
                    } else if (statusCode >= 500 && statusCode < 600) {
                        serverError(response);
                    } else {
                        unexpectedError(new RuntimeException("Unexpected response " + response));
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Exception  " + e.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                hideLoader(activity);

                apiCallback.apiCallback(null, from);
                if (t instanceof IOException) {
                    networkError((IOException) t);
                } else {
                    unexpectedError(t);
                }
            }
        });
    }


    @Override
    public void unauthenticated(Response<?> response) {
        Log.d(TAG, activity.getResources().getString(R.string.unable_auth));
    }

    @Override
    public void clientError(Response<?> response) {
        Log.d(TAG, activity.getResources().getString(R.string.client_not_response));
    }

    @Override
    public void serverError(Response<?> response) {
        Log.d(TAG, activity.getResources().getString(R.string.server_not_response));
    }

    @Override
    public void networkError(IOException e) {
        Log.d(TAG, activity.getResources().getString(R.string.netwrk_conn));
    }

    @Override
    public void unexpectedError(Throwable t) {
        Log.d(TAG, activity.getResources().getString(R.string.something_wrong));
    }
}
