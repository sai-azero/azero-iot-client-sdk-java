package com.azero.services.iot.client.shadow;

import java.util.logging.Logger;

import com.azero.services.iot.client.AZEROIotMessage;
import com.azero.services.iot.client.AZEROIotQos;
import com.azero.services.iot.client.AZEROIotTopic;

/**
 * This class extends {@link AZEROIotTopic} to provide customized callback
 * functions for the subscription requests of the shadow commands.
 */
public class AzeroIotDeviceCommandAckListener extends AZEROIotTopic {

    private static final Logger LOGGER = Logger.getLogger(AzeroIotDeviceCommandAckListener.class.getName());

    private final AbstractAzeroIotDevice device;

    public AzeroIotDeviceCommandAckListener(String topic, AZEROIotQos qos, AbstractAzeroIotDevice device) {
        super(topic, qos);
        this.device = device;
    }

    @Override
    public void onMessage(AZEROIotMessage message) {
        device.onCommandAck(message);
    }

    @Override
    public void onSuccess() {
        device.onSubscriptionAck(topic, true);
    }

    @Override
    public void onFailure() {
        LOGGER.warning("Failed to subscribe to device topic " + topic);
        device.onSubscriptionAck(topic, false);
    }

    @Override
    public void onTimeout() {
        LOGGER.warning("Timeout when subscribing to device topic " + topic);
        device.onSubscriptionAck(topic, false);
    }

}
