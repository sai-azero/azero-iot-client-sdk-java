package com.azero.services.iot.client.mqtt;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import com.azero.services.iot.client.core.AbstractAzeroIotClient;
import com.azero.services.iot.client.core.AzeroIotMessageCallback;

/**
 * This class implements listener functions for the connection events from the
 * Paho MQTT library.
 */
public class AzeroIotMqttConnectionListener implements IMqttActionListener {

    private static final Logger LOGGER = Logger.getLogger(AzeroIotMqttConnectionListener.class.getName());

    private final AbstractAzeroIotClient client;
    private final boolean isConnect;
    private final AzeroIotMessageCallback userCallback;

    public AzeroIotMqttConnectionListener(AbstractAzeroIotClient client, boolean isConnect,
            AzeroIotMessageCallback userCallback) {
        this.client = client;
        this.isConnect = isConnect;
        this.userCallback = userCallback;
    }

    @Override
    public void onSuccess(IMqttToken arg0) {
        client.scheduleTask(new Runnable() {
            @Override
            public void run() {
                if (isConnect) {
                    client.getConnection().onConnectionSuccess();
                } else {
                    client.getConnection().onConnectionClosed();
                }
                if (userCallback != null) {
                    userCallback.onSuccess();
                }
            }
        });
    }

    @Override
    public void onFailure(IMqttToken arg0, Throwable arg1) {
        LOGGER.log(Level.WARNING, (isConnect ? "Connect" : "Disconnect") + " request failure", arg1);

        client.scheduleTask(new Runnable() {
            @Override
            public void run() {
                if (isConnect) {
                    client.getConnection().onConnectionFailure();
                } else {
                    client.getConnection().onConnectionClosed();
                }
                if (userCallback != null) {
                    userCallback.onFailure();
                }
            }
        });
    }

}
