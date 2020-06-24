package com.azero.services.iot.client.core;

import java.security.KeyStore;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocketFactory;

import com.azero.services.iot.client.AZEROIotConfig;
import com.azero.services.iot.client.AZEROIotConnectionStatus;
import com.azero.services.iot.client.AZEROIotDevice;
import com.azero.services.iot.client.AZEROIotException;
import com.azero.services.iot.client.AZEROIotMessage;
import com.azero.services.iot.client.AZEROIotQos;
import com.azero.services.iot.client.AZEROIotTimeoutException;
import com.azero.services.iot.client.AZEROIotTopic;
import com.azero.services.iot.client.shadow.AbstractAzeroIotDevice;

/**
 * The actual implementation of {@code AZEROIotMqttClient}.
 */
public abstract class AbstractAzeroIotClient implements AzeroIotConnectionCallback {
    private static final int DEFAULT_MQTT_PORT = 8883;
    private static final Logger LOGGER = Logger.getLogger(AbstractAzeroIotClient.class.getName());
    protected final String clientId;
    protected final String clientEndpoint;
    protected final boolean clientEnableMetrics;
    protected final AzeroIotConnectionType connectionType;
    protected int port = DEFAULT_MQTT_PORT;
    protected int numOfClientThreads = AZEROIotConfig.NUM_OF_CLIENT_THREADS;
    protected int connectionTimeout = AZEROIotConfig.CONNECTION_TIMEOUT;
    protected int serverAckTimeout = AZEROIotConfig.SERVER_ACK_TIMEOUT;
    protected int keepAliveInterval = AZEROIotConfig.KEEP_ALIVE_INTERVAL;
    protected int maxConnectionRetries = AZEROIotConfig.MAX_CONNECTION_RETRIES;
    protected int baseRetryDelay = AZEROIotConfig.CONNECTION_BASE_RETRY_DELAY;
    protected int maxRetryDelay = AZEROIotConfig.CONNECTION_MAX_RETRY_DELAY;
    protected int maxOfflineQueueSize = AZEROIotConfig.MAX_OFFLINE_QUEUE_SIZE;
    protected boolean cleanSession = AZEROIotConfig.CLEAN_SESSION;
    protected AZEROIotMessage willMessage;
    private final ConcurrentMap<String, AZEROIotTopic> subscriptions = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, AbstractAzeroIotDevice> devices = new ConcurrentHashMap<>();
    private final AzeroIotConnection connection;
    private ScheduledExecutorService executionService;

    protected AbstractAzeroIotClient(String clientEndpoint, String clientId, KeyStore keyStore, String keyPassword, boolean enableSdkMetrics) {
        this.clientEndpoint = clientEndpoint;
        this.clientId = clientId;
        this.connectionType = AzeroIotConnectionType.MQTT_OVER_TLS;
        this.clientEnableMetrics = enableSdkMetrics;
        try {
            connection = new AzeroIotTlsConnection(this, keyStore, keyPassword);
        } catch (AZEROIotException e) {
            throw new AzeroIotRuntimeException(e);
        }
    }

    protected AbstractAzeroIotClient(String clientEndpoint, String clientId, KeyStore keyStore, String keyPassword) {
        // Enable Metrics by default
        this(clientEndpoint, clientId, keyStore, keyPassword, true);
    }

    protected AbstractAzeroIotClient(String clientEndpoint, String clientId, String azeroAccessKeyId, String azeroSecretAccessKey, String sessionToken, boolean enableSdkMetrics) {
        //setting the region blank to ensure it's determined from the clientEndpoint
        this(clientEndpoint, clientId, azeroAccessKeyId, azeroSecretAccessKey, sessionToken, "", enableSdkMetrics);
    }

    protected AbstractAzeroIotClient(String clientEndpoint, String clientId, String azeroAccessKeyId, String azeroSecretAccessKey, String sessionToken, String region, boolean enableSdkMetrics) {
        this.clientEndpoint = clientEndpoint;
        this.clientId = clientId;
        this.connectionType = AzeroIotConnectionType.MQTT_OVER_WEBSOCKET;
        this.clientEnableMetrics = enableSdkMetrics;
        try {
            connection = new AzeroIotWebsocketConnection(this, azeroAccessKeyId, azeroSecretAccessKey, sessionToken, region);
        } catch (AZEROIotException e) {
            throw new AzeroIotRuntimeException(e);
        }
    }

    protected AbstractAzeroIotClient(String clientEndpoint, String clientId, String azeroAccessKeyId, String azeroSecretAccessKey, String sessionToken) {
        // Enable Metrics by default
        this(clientEndpoint, clientId, azeroAccessKeyId, azeroSecretAccessKey, sessionToken, true);
    }

    protected AbstractAzeroIotClient(String clientEndpoint, String clientId, String azeroAccessKeyId, String azeroSecretAccessKey, String sessionToken, String region) {
        // Enable Metrics by default
        this(clientEndpoint, clientId, azeroAccessKeyId, azeroSecretAccessKey, sessionToken, region, true);
    }

    AbstractAzeroIotClient(String clientEndpoint, String clientId, AzeroIotConnection connection, boolean enableSdkMetrics) {
        this.clientEndpoint = clientEndpoint;
        this.clientId = clientId;
        this.connection = connection;
        this.connectionType = null;
        this.clientEnableMetrics = enableSdkMetrics;
    }

    AbstractAzeroIotClient(String clientEndpoint, String clientId, AzeroIotConnection connection) {
        // Enable Metrics by default
        this(clientEndpoint, clientId, connection, true);
    }

    protected AbstractAzeroIotClient(String clientEndpoint, String clientId, SSLSocketFactory socketFactory, boolean enableSdkMetrics) {
        this.clientEndpoint = clientEndpoint;
        this.clientId = clientId;
        this.connectionType = null;
        this.clientEnableMetrics = enableSdkMetrics;
        try {
            this.connection = new AzeroIotTlsConnection(this, socketFactory);
        } catch (AZEROIotException e) {
            throw new AzeroIotRuntimeException(e);
        }
    }

    protected AbstractAzeroIotClient(String clientEndpoint, String clientId, SSLSocketFactory socketFactory) {
        this(clientEndpoint, clientId, socketFactory, true);
    }

    protected AbstractAzeroIotClient(String clientEndpoint, String clientId, SSLSocketFactory socketFactory, int port, boolean enableSdkMetrics) {
        this.clientEndpoint = clientEndpoint;
        this.clientId = clientId;
        this.connectionType = AzeroIotConnectionType.MQTT_OVER_TLS;
        this.port = port;
        this.clientEnableMetrics = enableSdkMetrics;
        try {
            this.connection = new AzeroIotTlsConnection(this, socketFactory);
        } catch (AZEROIotException e) {
            throw new AzeroIotRuntimeException(e);
        }
    }

    protected AbstractAzeroIotClient(String clientEndpoint, String clientId, SSLSocketFactory socketFactory, int port) {
        this(clientEndpoint, clientId, socketFactory, port, true);
    }

    public void updateCredentials(String azeroAccessKeyId, String azeroSecretAccessKey, String sessionToken) {
    }

    public void connect() throws AZEROIotException {
        try {
            connect(0, true);
        } catch (AZEROIotTimeoutException e) {
            // We shouldn't get timeout exception because timeout is 0
            throw new AzeroIotRuntimeException(e);
        }
    }

    public void connect(long timeout) throws AZEROIotException, AZEROIotTimeoutException {
        connect(timeout, true);
    }

    public void connect(long timeout, boolean blocking) throws AZEROIotException, AZEROIotTimeoutException {
        synchronized (this) {
            if (executionService == null) {
                executionService = Executors.newScheduledThreadPool(numOfClientThreads);
            }
        }
        AzeroIotCompletion completion = new AzeroIotCompletion(timeout, !blocking);
        connection.connect(completion);
        completion.get(this);
    }

    public void disconnect() throws AZEROIotException {
        try {
            disconnect(0, true);
        } catch (AZEROIotTimeoutException e) {
            // We shouldn't get timeout exception because timeout is 0
            throw new AzeroIotRuntimeException(e);
        }
    }

    public void disconnect(long timeout) throws AZEROIotException, AZEROIotTimeoutException {
        disconnect(timeout, true);
    }

    public void disconnect(long timeout, boolean blocking) throws AZEROIotException, AZEROIotTimeoutException {
        AzeroIotCompletion completion = new AzeroIotCompletion(timeout, !blocking);
        connection.disconnect(completion);
        completion.get(this);
    }

    public void publish(String topic, String payload) throws AZEROIotException {
        publish(topic, AZEROIotQos.QOS0, payload);
    }

    public void publish(String topic, String payload, long timeout) throws AZEROIotException, AZEROIotTimeoutException {
        publish(topic, AZEROIotQos.QOS0, payload, timeout);
    }

    public void publish(String topic, AZEROIotQos qos, String payload) throws AZEROIotException {
        try {
            publish(topic, qos, payload, 0);
        } catch (AZEROIotTimeoutException e) {
            // We shouldn't get timeout exception because timeout is 0
            throw new AzeroIotRuntimeException(e);
        }
    }

    public void publish(String topic, AZEROIotQos qos, String payload, long timeout) throws AZEROIotException, AZEROIotTimeoutException {
        AzeroIotCompletion completion = new AzeroIotCompletion(topic, qos, payload, timeout);
        connection.publish(completion);
        completion.get(this);
    }

    public void publish(String topic, byte[] payload) throws AZEROIotException {
        publish(topic, AZEROIotQos.QOS0, payload);
    }

    public void publish(String topic, byte[] payload, long timeout) throws AZEROIotException, AZEROIotTimeoutException {
        publish(topic, AZEROIotQos.QOS0, payload, timeout);
    }

    public void publish(String topic, AZEROIotQos qos, byte[] payload) throws AZEROIotException {
        try {
            publish(topic, qos, payload, 0);
        } catch (AZEROIotTimeoutException e) {
            // We shouldn't get timeout exception because timeout is 0
            throw new AzeroIotRuntimeException(e);
        }
    }

    public void publish(String topic, AZEROIotQos qos, byte[] payload, long timeout) throws AZEROIotException, AZEROIotTimeoutException {
        AzeroIotCompletion completion = new AzeroIotCompletion(topic, qos, payload, timeout);
        connection.publish(completion);
        completion.get(this);
    }

    public void publish(AZEROIotMessage message) throws AZEROIotException {
        publish(message, 0);
    }

    public void publish(AZEROIotMessage message, long timeout) throws AZEROIotException {
        AzeroIotCompletion completion = new AzeroIotCompletion(message, timeout, true);
        connection.publish(completion);
        try {
            completion.get(this);
        } catch (AZEROIotTimeoutException e) {
            // We shouldn't get timeout exception because it's asynchronous call
            throw new AzeroIotRuntimeException(e);
        }
    }

    public void subscribe(AZEROIotTopic topic, boolean blocking) throws AZEROIotException {
        try {
            _subscribe(topic, 0, !blocking);
        } catch (AZEROIotTimeoutException e) {
            // We shouldn't get timeout exception because timeout is 0
            throw new AzeroIotRuntimeException(e);
        }
    }

    public void subscribe(AZEROIotTopic topic, long timeout, boolean blocking) throws AZEROIotException, AZEROIotTimeoutException {
        _subscribe(topic, timeout, !blocking);
    }

    public void subscribe(AZEROIotTopic topic) throws AZEROIotException {
        subscribe(topic, 0);
    }

    public void subscribe(AZEROIotTopic topic, long timeout) throws AZEROIotException {
        try {
            _subscribe(topic, timeout, true);
        } catch (AZEROIotTimeoutException e) {
            // We shouldn't get timeout exception because it's asynchronous call
            throw new AzeroIotRuntimeException(e);
        }
    }

    private void _subscribe(AZEROIotTopic topic, long timeout, boolean async) throws AZEROIotException, AZEROIotTimeoutException {
        AzeroIotCompletion completion = new AzeroIotCompletion(topic, timeout, async);
        connection.subscribe(completion);
        completion.get(this);
        subscriptions.put(topic.getTopic(), topic);
    }

    public void unsubscribe(String topic) throws AZEROIotException {
        try {
            unsubscribe(topic, 0);
        } catch (AZEROIotTimeoutException e) {
            // We shouldn't get timeout exception because timeout is 0
            throw new AzeroIotRuntimeException(e);
        }
    }

    public void unsubscribe(String topic, long timeout) throws AZEROIotException, AZEROIotTimeoutException {
        if (subscriptions.remove(topic) == null) {
            return;
        }
        AzeroIotCompletion completion = new AzeroIotCompletion(topic, AZEROIotQos.QOS0, timeout);
        connection.unsubscribe(completion);
        completion.get(this);
    }

    public void unsubscribe(AZEROIotTopic topic) throws AZEROIotException {
        unsubscribe(topic, 0);
    }

    public void unsubscribe(AZEROIotTopic topic, long timeout) throws AZEROIotException {
        if (subscriptions.remove(topic.getTopic()) == null) {
            return;
        }
        AzeroIotCompletion completion = new AzeroIotCompletion(topic, timeout, true);
        connection.unsubscribe(completion);
        try {
            completion.get(this);
        } catch (AZEROIotTimeoutException e) {
            // We shouldn't get timeout exception because it's asynchronous call
            throw new AzeroIotRuntimeException(e);
        }
    }

    public boolean topicFilterMatch(String topicFilter, String topic) {
        if (topicFilter == null || topic == null) {
            return false;
        }
        String[] filterTokens = topicFilter.split("/");
        String[] topicTokens = topic.split("/");
        if (filterTokens.length > topicTokens.length) {
            return false;
        }
        for (int i = 0; i < filterTokens.length; i++) {
            if (filterTokens[i].equals("#")) {
                // '#' must be the last character
                return ((i + 1) == filterTokens.length);
            }
            if (!(filterTokens[i].equals(topicTokens[i]) || filterTokens[i].equals("+"))) {
                return false;
            }
        }
        return (filterTokens.length == topicTokens.length);
    }

    public void dispatch(final AZEROIotMessage message) {
        boolean matches = false;
        for (String topicFilter : subscriptions.keySet()) {
            if (topicFilterMatch(topicFilter, message.getTopic())) {
                final AZEROIotTopic topic = subscriptions.get(topicFilter);
                scheduleTask(new Runnable() {
                    @Override
                    public void run() {
                        topic.onMessage(message);
                    }
                });
                matches = true;
            }
        }
        if (!matches) {
            LOGGER.warning("Unexpected message received from topic " + message.getTopic());
        }
    }

    public void attach(AZEROIotDevice device) throws AZEROIotException {
        if (devices.putIfAbsent(device.getThingName(), device) != null) {
            return;
        }
        device.setClient(this);
        // start the shadow sync task if the connection is already established
        if (getConnectionStatus().equals(AZEROIotConnectionStatus.CONNECTED)) {
            device.activate();
        }
    }

    public void detach(AZEROIotDevice device) throws AZEROIotException {
        if (devices.remove(device.getThingName()) == null) {
            return;
        }
        device.deactivate();
    }

    public AZEROIotConnectionStatus getConnectionStatus() {
        if (connection != null) {
            return connection.getConnectionStatus();
        } else {
            return AZEROIotConnectionStatus.DISCONNECTED;
        }
    }

    @Override
    public void onConnectionSuccess() {
        LOGGER.info("Client connection active: " + clientId);
        try {
            // resubscribe all the subscriptions
            for (AZEROIotTopic topic : subscriptions.values()) {
                subscribe(topic, serverAckTimeout);
            }
            // start device sync
            for (AbstractAzeroIotDevice device : devices.values()) {
                device.activate();
            }
        } catch (AZEROIotException e) {
            // connection couldn't be fully recovered, disconnecting
            LOGGER.warning("Failed to complete subscriptions while client is active, will disconnect");
            try {
                connection.disconnect(null);
            } catch (AZEROIotException de) {
            }
        }
        // ignore disconnect errors
    }

    @Override
    public void onConnectionFailure() {
        LOGGER.info("Client connection lost: " + clientId);
        // stop device sync
        for (AbstractAzeroIotDevice device : devices.values()) {
            try {
                device.deactivate();
            } catch (AZEROIotException e) {
                // ignore errors from deactivate() as the connection is lost
                LOGGER.warning("Failed to deactive all the devices, ignoring the error");
            }
        }
    }

    @Override
    public void onConnectionClosed() {
        LOGGER.info("Client connection closed: " + clientId);
        // stop device sync
        for (AbstractAzeroIotDevice device : devices.values()) {
            try {
                device.deactivate();
            } catch (AZEROIotException e) {
                // ignore errors from deactivate() as the connection is lost
                LOGGER.warning("Failed to deactive all the devices, ignoring the error");
            }
        }
        subscriptions.clear();
        devices.clear();
        executionService.shutdown();
    }

    public Future<?> scheduleTask(Runnable runnable) {
        return scheduleTimeoutTask(runnable, 0);
    }

    public Future<?> scheduleTimeoutTask(Runnable runnable, long timeout) {
        if (executionService == null) {
            throw new AzeroIotRuntimeException("Client is not connected");
        }
        return executionService.schedule(runnable, timeout, TimeUnit.MILLISECONDS);
    }

    public Future<?> scheduleRoutineTask(Runnable runnable, long initialDelay, long period) {
        if (executionService == null) {
            throw new AzeroIotRuntimeException("Client is not connected");
        }
        return executionService.scheduleAtFixedRate(runnable, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    @java.lang.SuppressWarnings("all")
    public String getClientId() {
        return this.clientId;
    }

    @java.lang.SuppressWarnings("all")
    public String getClientEndpoint() {
        return this.clientEndpoint;
    }

    @java.lang.SuppressWarnings("all")
    public boolean isClientEnableMetrics() {
        return this.clientEnableMetrics;
    }

    @java.lang.SuppressWarnings("all")
    public AzeroIotConnectionType getConnectionType() {
        return this.connectionType;
    }

    @java.lang.SuppressWarnings("all")
    public int getPort() {
        return this.port;
    }

    @java.lang.SuppressWarnings("all")
    public int getNumOfClientThreads() {
        return this.numOfClientThreads;
    }

    @java.lang.SuppressWarnings("all")
    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    @java.lang.SuppressWarnings("all")
    public int getServerAckTimeout() {
        return this.serverAckTimeout;
    }

    @java.lang.SuppressWarnings("all")
    public int getKeepAliveInterval() {
        return this.keepAliveInterval;
    }

    @java.lang.SuppressWarnings("all")
    public int getMaxConnectionRetries() {
        return this.maxConnectionRetries;
    }

    @java.lang.SuppressWarnings("all")
    public int getBaseRetryDelay() {
        return this.baseRetryDelay;
    }

    @java.lang.SuppressWarnings("all")
    public int getMaxRetryDelay() {
        return this.maxRetryDelay;
    }

    @java.lang.SuppressWarnings("all")
    public int getMaxOfflineQueueSize() {
        return this.maxOfflineQueueSize;
    }

    @java.lang.SuppressWarnings("all")
    public boolean isCleanSession() {
        return this.cleanSession;
    }

    @java.lang.SuppressWarnings("all")
    public AZEROIotMessage getWillMessage() {
        return this.willMessage;
    }

    @java.lang.SuppressWarnings("all")
    public ConcurrentMap<String, AZEROIotTopic> getSubscriptions() {
        return this.subscriptions;
    }

    @java.lang.SuppressWarnings("all")
    public ConcurrentMap<String, AbstractAzeroIotDevice> getDevices() {
        return this.devices;
    }

    @java.lang.SuppressWarnings("all")
    public AzeroIotConnection getConnection() {
        return this.connection;
    }

    @java.lang.SuppressWarnings("all")
    public ScheduledExecutorService getExecutionService() {
        return this.executionService;
    }

    @java.lang.SuppressWarnings("all")
    public void setPort(final int port) {
        this.port = port;
    }

    @java.lang.SuppressWarnings("all")
    public void setNumOfClientThreads(final int numOfClientThreads) {
        this.numOfClientThreads = numOfClientThreads;
    }

    @java.lang.SuppressWarnings("all")
    public void setConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @java.lang.SuppressWarnings("all")
    public void setServerAckTimeout(final int serverAckTimeout) {
        this.serverAckTimeout = serverAckTimeout;
    }

    @java.lang.SuppressWarnings("all")
    public void setKeepAliveInterval(final int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    @java.lang.SuppressWarnings("all")
    public void setMaxConnectionRetries(final int maxConnectionRetries) {
        this.maxConnectionRetries = maxConnectionRetries;
    }

    @java.lang.SuppressWarnings("all")
    public void setBaseRetryDelay(final int baseRetryDelay) {
        this.baseRetryDelay = baseRetryDelay;
    }

    @java.lang.SuppressWarnings("all")
    public void setMaxRetryDelay(final int maxRetryDelay) {
        this.maxRetryDelay = maxRetryDelay;
    }

    @java.lang.SuppressWarnings("all")
    public void setMaxOfflineQueueSize(final int maxOfflineQueueSize) {
        this.maxOfflineQueueSize = maxOfflineQueueSize;
    }

    @java.lang.SuppressWarnings("all")
    public void setCleanSession(final boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    @java.lang.SuppressWarnings("all")
    public void setWillMessage(final AZEROIotMessage willMessage) {
        this.willMessage = willMessage;
    }

    @java.lang.SuppressWarnings("all")
    public void setExecutionService(final ScheduledExecutorService executionService) {
        this.executionService = executionService;
    }
}
