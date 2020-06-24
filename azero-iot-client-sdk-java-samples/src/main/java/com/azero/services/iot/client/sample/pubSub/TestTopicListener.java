package com.azero.services.iot.client.sample.pubSub;

import com.azero.services.iot.client.AZEROIotMessage;
import com.azero.services.iot.client.AZEROIotQos;
import com.azero.services.iot.client.AZEROIotTopic;

/**
 * This class extends {@link AZEROIotTopic} to receive messages from a subscribed
 * topic.
 */
public class TestTopicListener extends AZEROIotTopic {

    public TestTopicListener(String topic, AZEROIotQos qos) {
        super(topic, qos);
    }

    @Override
    public void onMessage(AZEROIotMessage message) {
        System.out.println(System.currentTimeMillis() + ": <<< " + message.getStringPayload());
    }

}
