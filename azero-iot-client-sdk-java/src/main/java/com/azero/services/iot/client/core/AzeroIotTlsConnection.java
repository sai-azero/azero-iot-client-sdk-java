package com.azero.services.iot.client.core;

import java.security.KeyStore;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import com.azero.services.iot.client.AZEROIotException;
import com.azero.services.iot.client.mqtt.AzeroIotMqttConnection;
import com.azero.services.iot.client.util.AzeroIotTlsSocketFactory;

/**
 * This is a thin layer on top of {@link AzeroIotMqttConnection} that provides a
 * TLS v1.2 based communication channel to the MQTT implementation.
 */
public class AzeroIotTlsConnection extends AzeroIotMqttConnection {

    public AzeroIotTlsConnection(AbstractAzeroIotClient client, KeyStore keyStore, String keyPassword)
            throws AZEROIotException {
        super(client, new AzeroIotTlsSocketFactory(keyStore, keyPassword), "ssl://" + client.getClientEndpoint() + ":" + client.getPort());
    }

    public AzeroIotTlsConnection(AbstractAzeroIotClient client, SSLSocketFactory socketFactory) throws AZEROIotException {
        super(client, new AzeroIotTlsSocketFactory(socketFactory), "ssl://" + client.getClientEndpoint() + ":" + client.getPort());
    }
    
}
