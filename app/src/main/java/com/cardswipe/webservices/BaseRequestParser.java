package com.cardswipe.webservices;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class BaseRequestParser {

    public boolean runInBackground = false;
    public boolean hideKeyBoard = true;
    public boolean cacheEnabled = false;
    public ProgressBarHandler mProgressBarHandler;
    public View loaderView = null;

    public boolean isRunInBackground() {
        return runInBackground;
    }

    public void setRunInBackground(boolean runInBackground) {
        this.runInBackground = runInBackground;
    }

    public boolean isHideKeyBoard() {
        return hideKeyBoard;
    }

    public void setHideKeyBoard(boolean hideKeyBoard) {
        this.hideKeyBoard = hideKeyBoard;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public void showLoader(Activity activity) {
        if (activity != null && !((Activity) activity).isDestroyed()) {
            if (!runInBackground) {
                if (null != loaderView) {
                    loaderView.setVisibility(View.VISIBLE);
                } else if (null != mProgressBarHandler) {
                    mProgressBarHandler.show();
                }
            }
        }
    }

    public void hideLoader(Activity activity) {
        if (activity != null && !((Activity) activity).isDestroyed()) {
            if (!runInBackground) {
                if (null != loaderView) {
                    loaderView.setVisibility(View.GONE);
                } else if (null != mProgressBarHandler) {
                    mProgressBarHandler.hide();
                }
            }
        }
    }

    public String serverMessage = "Something going wrong please try again later.";
    public String mResponseCode = "0";
    public String app_version = "";

    private JSONObject mRespJSONObject = null;

    public JSONObject getMainResponseInJSON() {
        return mRespJSONObject;
    }

    public boolean parseJson(String json, final Context mContext) {
        if (!TextUtils.isEmpty(json)) {
            try {
                mRespJSONObject = new JSONObject(json);
                if (null != mRespJSONObject) {
                    mResponseCode = mRespJSONObject.optString("ResponseCode", "");
                    serverMessage = mRespJSONObject.optString("Message", serverMessage);
                    app_version = mRespJSONObject.optString("app_version", "");
                    if (TextUtils.isEmpty(mResponseCode)) {
                        mResponseCode = mRespJSONObject.optString("Status", "");
                    }
                    if (mResponseCode.equalsIgnoreCase("200") || mResponseCode.equalsIgnoreCase("201")) {
                        if (!TextUtils.isEmpty(app_version)) {
                            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext
                                            .getPackageName(),0);
                            String currentVersionName = pInfo.versionName;
                            if (app_version.contains(",")) {
                                String versions[] = app_version.split(",");
                                boolean isAvail = false;
                                for (String serverVersion : versions) {
                                    if (serverVersion.equals(currentVersionName)) {
                                        isAvail = true;
                                        break;
                                    }
                                }
                                if (!isAvail) {
                                    // showUpdateDialog(mContext);
                                }
                            } else {
                                if (!app_version.equals(currentVersionName)) {
                                    //showUpdateDialog(mContext);
                                }
                            }
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public JSONArray getDataArray() {
        if (null == mRespJSONObject) {
            return null;
        }
        try {
            return mRespJSONObject.optJSONArray("Data");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getDataObject() {
        if (null == mRespJSONObject) {
            return null;
        }
        try {
            return mRespJSONObject.optJSONObject("Data");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public HashMap<String, Object> getHashMapObject(Object... nameValuePair) {
        HashMap<String, Object> HashMap = null;
        if (null != nameValuePair && nameValuePair.length % 2 == 0) {
            HashMap = new HashMap<>();
            int i = 0;
            while (i < nameValuePair.length) {
                HashMap.put(nameValuePair[i].toString(), nameValuePair[i + 1]);
                i += 2;
            }
        } else {
            HashMap = new HashMap<>();
        }
        return HashMap;
    }


    public HashMap<String, RequestBody> getHashMapObjectPart(Object... nameValuePair) {
        HashMap<String, RequestBody> HashMap = null;
        if (null != nameValuePair && nameValuePair.length % 2 == 0) {
            HashMap = new HashMap<>();
            int i = 0;
            while (i < nameValuePair.length) {
                HashMap.put(nameValuePair[i].toString(), createPartFromString(nameValuePair[i + 1].toString()));
                i += 2;
            }
        } else {
            HashMap = new HashMap<>();
        }
        return HashMap;
    }

    private RequestBody createPartFromString(String partString) {
        return RequestBody.create(MultipartBody.FORM, partString);
    }
}