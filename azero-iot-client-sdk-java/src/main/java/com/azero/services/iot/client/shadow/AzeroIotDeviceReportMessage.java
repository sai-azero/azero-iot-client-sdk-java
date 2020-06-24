package com.azero.services.iot.client.shadow;

import java.util.logging.Logger;

import com.azero.services.iot.client.AZEROIotDeviceErrorCode;
import com.azero.services.iot.client.AZEROIotMessage;
import com.azero.services.iot.client.AZEROIotQos;

public class AzeroIotDeviceReportMessage extends AZEROIotMessage {

    private static final Logger LOGGER = Logger.getLogger(AzeroIotDeviceReportMessage.class.getName());

    private final AbstractAzeroIotDevice device;
    private final long reportVersion;

    public AzeroIotDeviceReportMessage(String topic, AZEROIotQos qos, long reportVersion, String jsonState,
            AbstractAzeroIotDevice device) {
        super(topic, qos, jsonState);
        this.device = device;
        this.reportVersion = reportVersion;
    }

    @Override
    public void onSuccess() {
        // increment local version only if it hasn't be updated
        device.getLocalVersion().compareAndSet(reportVersion, reportVersion + 1);
    }

    @Override
    public void onFailure() {
        if (AZEROIotDeviceErrorCode.CONFLICT.equals(errorCode)) {
            LOGGER.warning("Device version conflict, restart version synchronization");
            device.startVersionSync();
        } else {
            LOGGER.warning("Failed to publish device report: " + errorMessage);
        }
    }

}
