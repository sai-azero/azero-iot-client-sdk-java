package com.azero.services.iot.client.core;

/**
 * This interface class defines functions called under different connection
 * events.
 */
public interface AzeroIotConnectionCallback {

    /**
     * On connection success.
     */
    void onConnectionSuccess();

    /**
     * On connection failure.
     */
    void onConnectionFailure();

    /**
     * On connection closed.
     */
    void onConnectionClosed();

}
