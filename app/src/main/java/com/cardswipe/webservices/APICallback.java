package com.cardswipe.webservices;


import com.google.gson.JsonObject;

/**
 * The interface Api callback.
 */
public interface APICallback {

    /**
     * Api callback.
     *
     * @param json the json
     * @param from the from
     */
    void apiCallback(JsonObject json, String from);

}
