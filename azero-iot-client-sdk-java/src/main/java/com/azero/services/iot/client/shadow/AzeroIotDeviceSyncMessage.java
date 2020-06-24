package com.azero.services.iot.client.shadow;

import java.io.IOException;
import java.util.logging.Logger;

import com.azero.services.iot.client.AZEROIotDeviceErrorCode;
import com.azero.services.iot.client.AZEROIotMessage;
import com.azero.services.iot.client.AZEROIotQos;

public class AzeroIotDeviceSyncMessage extends AZEROIotMessage {

    private static final Logger LOGGER = Logger.getLogger(AzeroIotDeviceSyncMessage.class.getName());

    private final AbstractAzeroIotDevice device;

    public AzeroIotDeviceSyncMessage(String topic, AZEROIotQos qos, AbstractAzeroIotDevice device) {
        super(topic, qos);
        this.device = device;
    }

    @Override
    public void onSuccess() {
        if (payload != null) {
            try {
                long version = AzeroIotJsonDeserializer.deserializeVersion(device, getStringPayload());
                if (version > 0) {
                    LOGGER.info("Received shadow version number: " + version);

                    boolean updated = device.getLocalVersion().compareAndSet(-1, version);
                    if (!updated) {
                        LOGGER.warning(
                                "Local version not updated likely because newer version recieved from shadow update");
                    }
                }
            } catch (IOException e) {
                LOGGER.warning("Device update error: " + e.getMessage());
            }
        }
    }

    @Override
    public void onFailure() {
        if (AZEROIotDeviceErrorCode.NOT_FOUND.equals(errorCode)) {
            LOGGER.info("No shadow document found, reset local version to 0");
            device.getLocalVersion().set(0);
        } else {
            LOGGER.warning("Failed to get shadow version: " + errorMessage);
        }
    }
    
}
