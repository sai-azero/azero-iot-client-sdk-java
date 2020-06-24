package com.azero.services.iot.client.core;

/**
 * This interface class defines functions called under different message related
 * events.
 */
public interface AzeroIotMessageCallback {

    /**
     * On success.
     */
    void onSuccess();

    /**
     * On failure.
     */
    void onFailure();

    /**
     * On timeout.
     */
    void onTimeout();

}
