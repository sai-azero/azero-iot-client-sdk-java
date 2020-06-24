package com.azero.services.iot.client.core;

import com.azero.services.iot.client.AZEROIotMessage;

/**
 * This interface class defines the function called when subscribed message has
 * arrived.
 */
public interface AzeroIotTopicCallback {

    void onMessage(AZEROIotMessage message);

}
