package com.cardswipe.webservices;

import com.cardswipe.models.UserData;
import com.google.gson.JsonElement;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * The interface Rest api.
 */
public interface RestAPI {

    /**
     * Post api call.
     *
     * @param remainingURL the remaining url
     * @param fields       the fields
     * @return the call
     */
    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST
    Call<JsonElement> postApi(@Url String remainingURL, @FieldMap Map<String, Object> fields);

    /**
     * Gets api.
     *
     * @param remainingURL the remaining url
     * @return the api
     */
    @Headers({"Accept: application/json"})
    @GET
    Call<JsonElement> getApi(@Url String remainingURL);

    /**
     * Post multi part api call.
     *
     * @param remainingURL the remaining url
     * @param fields       the fields
     * @param image        the image
     * @return the call
     */
    @Multipart
    @POST
    Call<JsonElement> postMultiPartApi(@Url String remainingURL, @PartMap Map<String, Object> fields, @Part MultipartBody.Part image);

    /**
     * Post multi part api call.
     *
     * @param remainingURL the remaining url
     * @param fields       the fields
     * @param profile_pic  the image
     * @return the call
     */
    @Multipart
    @POST
    Call<JsonElement> postMultiPartImageApi(@Url String remainingURL, @PartMap Map<String, RequestBody> fields, @Part MultipartBody.Part profile_pic);


    /**
     * Post multi part api call.
     *
     * @param remainingURL the remaining url
     * @param fields       the fields
     * @return the call
     */
    @Multipart
    @POST
    Call<JsonElement> postMultiPartsApi(@Url String remainingURL, @PartMap Map<String, RequestBody> fields, @Part MultipartBody.Part[] imagelist);

    @GET
    Call<ResponseBody> download(@Url String fileUrl);

    @GET("/api/")
    Call<JsonElement> getResult(@Query("results")int results);

}
