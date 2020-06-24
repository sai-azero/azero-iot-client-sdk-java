package com.azero.services.iot.client.mqtt;

import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttSuback;

import com.azero.services.iot.client.AZEROIotMessage;
import com.azero.services.iot.client.core.AbstractAzeroIotClient;

/**
 * This class implements listener functions for the message events from the Paho
 * MQTT library.
 */
public class AzeroIotMqttMessageListener implements IMqttActionListener {

    private static final Logger LOGGER = Logger.getLogger(AzeroIotMqttMessageListener.class.getName());

    private static final int SUB_ACK_RETURN_CODE_FAILURE = 0x80;

    private AbstractAzeroIotClient client;

    public AzeroIotMqttMessageListener(AbstractAzeroIotClient client) {
        this.client = client;
    }

    @Override
    public void onSuccess(IMqttToken token) {
        final AZEROIotMessage message = (AZEROIotMessage) token.getUserContext();
        if (message == null) {
            return;
        }

        boolean forceFailure = false;
        if (token.getResponse() instanceof MqttSuback) {
            MqttSuback subAck = (MqttSuback) token.getResponse();
            int qos[] = subAck.getGrantedQos();
            for (int i = 0; i < qos.length; i++) {
                if (qos[i] == SUB_ACK_RETURN_CODE_FAILURE) {
                    LOGGER.warning("Request failed: likely due to too many subscriptions or policy violations");
                    forceFailure = true;
                    break;
                }
            }
        }

        final boolean isSuccess = !forceFailure;
        client.scheduleTask(new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    message.onSuccess();
                } else {
                    message.onFailure();
                }
            }
        });
    }

    @Override
    public void onFailure(IMqttToken token, Throwable cause) {
        final AZEROIotMessage message = (AZEROIotMessage) token.getUserContext();
        if (message == null) {
            LOGGER.warning("Request failed: " + token.getException());
            return;
        }

        LOGGER.warning("Request failed for topic " + message.getTopic() + ": " + token.getException());
        client.scheduleTask(new Runnable() {
            @Override
            public void run() {
                message.onFailure();
            }
        });
    }

}
