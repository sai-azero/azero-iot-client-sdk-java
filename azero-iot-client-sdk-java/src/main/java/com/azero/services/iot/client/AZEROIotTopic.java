package com.azero.services.iot.client;

import com.azero.services.iot.client.core.AzeroIotTopicCallback;

/**
 * This class is used for subscribing to a topic in the subscription APIs, such
 * as {@link AZEROIotMqttClient#subscribe(AZEROIotTopic topic)}.
 * <p>
 * In contains a callback function, {@link #onMessage}, that is invoked when a
 * subscribed message has arrived. In most cases, applications are expected to
 * override the default {@link #onMessage} method in order to access the message
 * payload.
 * </p>
 * <p>
 * This class extends {@link AZEROIotMessage}, therefore callback functions in
 * {@link AZEROIotMessage} can also be overridden if the application wishes to be
 * invoked for the outcomes of the subscription API. For more details, please
 * refer to {@link AZEROIotMessage}.
 * </p>
 */
public class AZEROIotTopic extends AZEROIotMessage implements AzeroIotTopicCallback {

    /**
     * Instantiates a new topic object.
     *
     * @param topic
     *            the topic to be subscribed to
     */
    public AZEROIotTopic(String topic) {
        super(topic, AZEROIotQos.QOS0);
    }

    /**
     * Instantiates a new topic object.
     *
     * @param topic
     *            the topic to be subscribed to
     * @param qos
     *            the MQTT QoS level for the subscription
     */
    public AZEROIotTopic(String topic, AZEROIotQos qos) {
        super(topic, qos);
    }

    /**
     * Callback function to be invoked upon the arrival of a subscribed message.
     *
     * @param message
     *            the message received
     */
    @Override
    public void onMessage(AZEROIotMessage message) {
        // Default callback implementation is no-op
    }

}
