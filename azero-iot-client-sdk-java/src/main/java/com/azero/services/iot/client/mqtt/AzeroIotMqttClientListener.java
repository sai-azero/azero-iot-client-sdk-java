package com.azero.services.iot.client.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.azero.services.iot.client.AZEROIotMessage;
import com.azero.services.iot.client.AZEROIotQos;
import com.azero.services.iot.client.core.AbstractAzeroIotClient;

/**
 * This class implements listener functions for client related events from the
 * Paho MQTT library.
 */
public class AzeroIotMqttClientListener implements MqttCallback {

    private AbstractAzeroIotClient client;

    public AzeroIotMqttClientListener(AbstractAzeroIotClient client) {
        this.client = client;
    }

    @Override
    public void connectionLost(Throwable arg0) {
        client.scheduleTask(new Runnable() {
            @Override
            public void run() {
                client.getConnection().onConnectionFailure();
            }
        });
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {
        // Callback is not used
    }

    @Override
    public void messageArrived(String topic, MqttMessage arg1) throws Exception {
        AZEROIotMessage message = new AZEROIotMessage(topic, AZEROIotQos.valueOf(arg1.getQos()), arg1.getPayload());
        client.dispatch(message);
    }

}
