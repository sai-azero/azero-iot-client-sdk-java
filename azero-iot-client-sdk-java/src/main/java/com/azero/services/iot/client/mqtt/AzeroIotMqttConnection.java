package com.azero.services.iot.client.mqtt;

import java.util.HashSet;
import java.util.Set;
import javax.net.SocketFactory;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.azero.services.iot.client.AZEROIotException;
import com.azero.services.iot.client.AZEROIotMessage;
import com.azero.services.iot.client.core.AbstractAzeroIotClient;
import com.azero.services.iot.client.core.AzeroIotConnection;
import com.azero.services.iot.client.core.AzeroIotMessageCallback;
import com.azero.services.iot.client.core.AzeroIotRetryableException;

/**
 * This class extends {@link AzeroIotConnection} to provide the basic MQTT pub/sub
 * functionalities using the Paho MQTT library.
 */
public class AzeroIotMqttConnection extends AzeroIotConnection {
    private static final String USERNAME_METRIC_STRING = "?SDK=Java&Version=1.3.7";
    private final SocketFactory socketFactory;
    private MqttAsyncClient mqttClient;
    private AzeroIotMqttMessageListener messageListener;
    private AzeroIotMqttClientListener clientListener;

    public AzeroIotMqttConnection(AbstractAzeroIotClient client, SocketFactory socketFactory, String serverUri) throws AZEROIotException {
        super(client);
        this.socketFactory = socketFactory;
        messageListener = new AzeroIotMqttMessageListener(client);
        clientListener = new AzeroIotMqttClientListener(client);
        try {
            mqttClient = new MqttAsyncClient(serverUri, client.getClientId(), new MemoryPersistence());
            mqttClient.setCallback(clientListener);
        } catch (MqttException e) {
            throw new AZEROIotException(e);
        }
    }

    AzeroIotMqttConnection(AbstractAzeroIotClient client, MqttAsyncClient mqttClient) throws AZEROIotException {
        super(client);
        this.mqttClient = mqttClient;
        this.socketFactory = null;
    }

    public void openConnection(AzeroIotMessageCallback callback) throws AZEROIotException {
        try {
            AzeroIotMqttConnectionListener connectionListener = new AzeroIotMqttConnectionListener(client, true, callback);
            MqttConnectOptions options = buildMqttConnectOptions(client, socketFactory);
            mqttClient.connect(options, null, connectionListener);
        } catch (MqttException e) {
            throw new AZEROIotException(e);
        }
    }

    public void closeConnection(AzeroIotMessageCallback callback) throws AZEROIotException {
        try {
            AzeroIotMqttConnectionListener connectionListener = new AzeroIotMqttConnectionListener(client, false, callback);
            mqttClient.disconnect(0, null, connectionListener);
        } catch (MqttException e) {
            throw new AZEROIotException(e);
        }
    }

    @Override
    public void publishMessage(AZEROIotMessage message) throws AZEROIotException, AzeroIotRetryableException {
        String topic = message.getTopic();
        MqttMessage mqttMessage = new MqttMessage(message.getPayload());
        mqttMessage.setQos(message.getQos().getValue());
        try {
            mqttClient.publish(topic, mqttMessage, message, messageListener);
        } catch (MqttException e) {
            if (e.getReasonCode() == MqttException.REASON_CODE_CLIENT_NOT_CONNECTED) {
                throw new AzeroIotRetryableException(e);
            } else {
                throw new AZEROIotException(e);
            }
        }
    }

    @Override
    public void subscribeTopic(AZEROIotMessage message) throws AZEROIotException, AzeroIotRetryableException {
        try {
            mqttClient.subscribe(message.getTopic(), message.getQos().getValue(), message, messageListener);
        } catch (MqttException e) {
            if (e.getReasonCode() == MqttException.REASON_CODE_CLIENT_NOT_CONNECTED) {
                throw new AzeroIotRetryableException(e);
            } else {
                throw new AZEROIotException(e);
            }
        }
    }

    @Override
    public void unsubscribeTopic(AZEROIotMessage message) throws AZEROIotException, AzeroIotRetryableException {
        try {
            mqttClient.unsubscribe(message.getTopic(), message, messageListener);
        } catch (MqttException e) {
            if (e.getReasonCode() == MqttException.REASON_CODE_CLIENT_NOT_CONNECTED) {
                throw new AzeroIotRetryableException(e);
            } else {
                throw new AZEROIotException(e);
            }
        }
    }

    public Set<String> getServerUris() {
        return new HashSet<>();
    }

    private MqttConnectOptions buildMqttConnectOptions(AbstractAzeroIotClient client, SocketFactory socketFactory) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setSocketFactory(socketFactory);
        options.setCleanSession(client.isCleanSession());
        options.setConnectionTimeout(client.getConnectionTimeout() / 1000);
        options.setKeepAliveInterval(client.getKeepAliveInterval() / 1000);
        if (client.isClientEnableMetrics()) {
            options.setUserName(USERNAME_METRIC_STRING);
        }
        Set<String> serverUris = getServerUris();
        if (serverUris != null && !serverUris.isEmpty()) {
            String[] uriArray = new String[serverUris.size()];
            serverUris.toArray(uriArray);
            options.setServerURIs(uriArray);
        }
        if (client.getWillMessage() != null) {
            AZEROIotMessage message = client.getWillMessage();
            options.setWill(message.getTopic(), message.getPayload(), message.getQos().getValue(), false);
        }
        return options;
    }

    @java.lang.SuppressWarnings("all")
    public SocketFactory getSocketFactory() {
        return this.socketFactory;
    }

    @java.lang.SuppressWarnings("all")
    public MqttAsyncClient getMqttClient() {
        return this.mqttClient;
    }

    @java.lang.SuppressWarnings("all")
    public AzeroIotMqttMessageListener getMessageListener() {
        return this.messageListener;
    }

    @java.lang.SuppressWarnings("all")
    public AzeroIotMqttClientListener getClientListener() {
        return this.clientListener;
    }

    @java.lang.SuppressWarnings("all")
    public void setMqttClient(final MqttAsyncClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    @java.lang.SuppressWarnings("all")
    public void setMessageListener(final AzeroIotMqttMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    @java.lang.SuppressWarnings("all")
    public void setClientListener(final AzeroIotMqttClientListener clientListener) {
        this.clientListener = clientListener;
    }
}
