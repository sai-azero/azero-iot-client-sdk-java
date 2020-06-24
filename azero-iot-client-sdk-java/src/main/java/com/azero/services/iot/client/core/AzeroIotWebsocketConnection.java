package com.azero.services.iot.client.core;

import java.util.HashSet;
import java.util.Set;

import com.azero.services.iot.client.AZEROIotException;
import com.azero.services.iot.client.mqtt.AzeroIotMqttConnection;
import com.azero.services.iot.client.util.AzeroIotWebSocketUrlSigner;

/**
 * This is a thin layer on top of {@link AzeroIotMqttConnection} that provides a
 * WebSocket based communication channel to the MQTT implementation.
 */
public class AzeroIotWebsocketConnection extends AzeroIotMqttConnection {

    private AzeroIotWebSocketUrlSigner urlSigner;

    public AzeroIotWebsocketConnection(AbstractAzeroIotClient client, String azeroAccessKeyId, String azeroSecretAccessKey)
            throws AZEROIotException {
        this(client, azeroAccessKeyId, azeroSecretAccessKey, null);
    }

    public AzeroIotWebsocketConnection(AbstractAzeroIotClient client, String azeroAccessKeyId, String azeroSecretAccessKey,
                                     String sessionToken, String region) throws AZEROIotException {
        super(client, null, "wss://" + client.getClientEndpoint() + ":443");

        // Port number must be included in the endpoint for signing otherwise
        // the signature verification will fail. This is because the Paho client
        // library always includes port number in the host line of the
        // HTTP request header, e.g "Host: iot.soundai.com:443".
        urlSigner = new AzeroIotWebSocketUrlSigner(client.getClientEndpoint() + ":443", region);
        urlSigner.updateCredentials(azeroAccessKeyId, azeroSecretAccessKey, sessionToken);
    }

    public AzeroIotWebsocketConnection(AbstractAzeroIotClient client, String azeroAccessKeyId, String azeroSecretAccessKey,
            String sessionToken) throws AZEROIotException {
        //setting the region blank to ensure it's determined from the client Endpoint
        this(client, azeroAccessKeyId, azeroSecretAccessKey, sessionToken, "");
    }

    @Override
    public void updateCredentials(String azeroAccessKeyId, String azeroSecretAccessKey, String sessionToken) {
        urlSigner.updateCredentials(azeroAccessKeyId, azeroSecretAccessKey, sessionToken);
    }

    @Override
    public Set<String> getServerUris() {
        Set<String> uris = new HashSet<>();
        try {
            uris.add(urlSigner.getSignedUrl(null));
        } catch (AZEROIotException e) {
            throw new AzeroIotRuntimeException(e);
        }

        return uris;
    }

}
