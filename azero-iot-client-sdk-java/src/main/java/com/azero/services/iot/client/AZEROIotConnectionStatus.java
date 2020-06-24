package com.azero.services.iot.client;

/**
 * Connection status that can be retrieved through
 * {@link AZEROIotMqttClient#getConnectionStatus()}.
 */
public enum AZEROIotConnectionStatus {

    /** Client successfully connected. */
    CONNECTED,

    /** Not connected. */
    DISCONNECTED,

    /** Automatically reconnecting after connection loss. */
    RECONNECTING

}
