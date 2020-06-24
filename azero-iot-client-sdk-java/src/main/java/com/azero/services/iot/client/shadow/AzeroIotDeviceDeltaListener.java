package com.azero.services.iot.client.shadow;

import java.io.IOException;
import java.util.logging.Logger;

import com.azero.services.iot.client.AZEROIotMessage;
import com.azero.services.iot.client.AZEROIotQos;
import com.azero.services.iot.client.AZEROIotTopic;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class extends {@link AZEROIotTopic} to provide a callback function for
 * receiving the shadow delta updates.
 */
public class AzeroIotDeviceDeltaListener extends AZEROIotTopic {

    private static final Logger LOGGER = Logger.getLogger(AzeroIotDeviceDeltaListener.class.getName());

    private final AbstractAzeroIotDevice device;

    public AzeroIotDeviceDeltaListener(String topic, AZEROIotQos qos, AbstractAzeroIotDevice device) {
        super(topic, qos);
        this.device = device;
    }

    @Override
    public void onMessage(AZEROIotMessage message) {
        String payload = message.getStringPayload();
        if (payload == null) {
            LOGGER.warning("Received empty delta for device " + device.getThingName());
            return;
        }

        JsonNode rootNode;
        try {
            rootNode = device.getJsonObjectMapper().readTree(payload);
            if (!rootNode.isObject()) {
                throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Received invalid delta for device " + device.getThingName());
            return;
        }

        if (device.enableVersioning) {
            JsonNode node = rootNode.get("version");
            if (node == null) {
                LOGGER.warning("Missing version field in delta for device " + device.getThingName());
                return;
            }

            long receivedVersion = node.longValue();
            long localVersion = device.getLocalVersion().get();
            if (receivedVersion < localVersion) {
                LOGGER.warning("An old version of delta received for " + device.getThingName() + ", local "
                        + localVersion + ", received " + receivedVersion);
                return;
            }

            device.getLocalVersion().set(receivedVersion);
            LOGGER.info("Local version number updated to " + receivedVersion);
        }

        JsonNode node = rootNode.get("state");
        if (node == null) {
            LOGGER.warning("Missing state field in delta for device " + device.getThingName());
            return;
        }
        device.onShadowUpdate(node.toString());
    }

    @Override
    public void onSuccess() {
    }

    @Override
    public void onFailure() {
        LOGGER.warning("Failed to subscribe to device topic " + topic);
    }

    @Override
    public void onTimeout() {
        LOGGER.warning("Timeout when subscribing to device topic " + topic);
    }

}
