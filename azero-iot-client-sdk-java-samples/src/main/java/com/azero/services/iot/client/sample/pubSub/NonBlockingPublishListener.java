package com.azero.services.iot.client.sample.pubSub;

import com.azero.services.iot.client.AZEROIotMessage;
import com.azero.services.iot.client.AZEROIotQos;

/**
 * This class extends {@link AZEROIotMessage} to provide customized handlers for
 * non-blocking message publishing.
 */
public class NonBlockingPublishListener extends AZEROIotMessage {

    public NonBlockingPublishListener(String topic, AZEROIotQos qos, String payload) {
        super(topic, qos, payload);
    }

    @Override
    public void onSuccess() {
        System.out.println(System.currentTimeMillis() + ": >>> " + getStringPayload());
    }

    @Override
    public void onFailure() {
        System.out.println(System.currentTimeMillis() + ": publish failed for " + getStringPayload());
    }

    @Override
    public void onTimeout() {
        System.out.println(System.currentTimeMillis() + ": publish timeout for " + getStringPayload());
    }

}
