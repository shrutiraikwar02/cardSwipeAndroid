package com.cardswipe.webservices;

import java.io.IOException;

import retrofit2.Response;


/**
 * The interface Service callback.
 *
 * @param <T> the type parameter
 */
public interface ServiceCallback<T> {
    /**
     * Called for [200, 300) responses.
     */
    // void success(Response<T> response);

    /**
     * Called for 401 responses.
     *
     * @param response the response
     */
    void unauthenticated(Response<?> response);

    /**
     * Called for [400, 500) responses, except 401.
     *
     * @param response the response
     */
    void clientError(Response<?> response);

    /**
     * Called for [500, 600) response.
     *
     * @param response the response
     */
    void serverError(Response<?> response);

    /**
     * Called for network errors while making the call.
     *
     * @param e the e
     */
    void networkError(IOException e);

    /**
     * Called for unexpected errors while making the call.
     *
     * @param t the t
     */
    void unexpectedError(Throwable t);
}
