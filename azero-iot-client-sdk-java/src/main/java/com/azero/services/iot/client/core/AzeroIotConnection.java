package com.azero.services.iot.client.core;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.azero.services.iot.client.AZEROIotConnectionStatus;
import com.azero.services.iot.client.AZEROIotException;
import com.azero.services.iot.client.AZEROIotMessage;

/**
 * This class provides an abstract layer for the library to communicate with the
 * AZERO IoT service without having to directly interact with the actual MQTT
 * implementation. The abstraction layer also provides connection retry logic as
 * well as offline message queuing.
 */
public abstract class AzeroIotConnection implements AzeroIotConnectionCallback {
    private static final Logger LOGGER = Logger.getLogger(AzeroIotConnection.class.getName());
    /**
     * The client the connection is associated with.
     */
    protected AbstractAzeroIotClient client;
    /**
     * The connection status.
     *
     *            the new connection status
     */
    protected AZEROIotConnectionStatus connectionStatus = AZEROIotConnectionStatus.DISCONNECTED;
    /**
     * The future object holding the retry task.
     */
    private Future<?> retryTask;
    /**
     * The retry times.
     */
    private int retryTimes;
    /**
     * The callback functions for the connect request.
     */
    private AzeroIotMessageCallback connectCallback;
    /**
     * Flag to indicate user disconnect is in progress.
     */
    private boolean userDisconnect;
    /**
     * The offline publish queue holding messages while the connection is being
     * established.
     */
    private ConcurrentLinkedQueue<AZEROIotMessage> publishQueue = new ConcurrentLinkedQueue<>();
    /**
     * The offline subscribe request queue holding messages while the connection
     * is being established.
     */
    private ConcurrentLinkedQueue<AZEROIotMessage> subscribeQueue = new ConcurrentLinkedQueue<>();
    /**
     * The offline unsubscribe request queue holding messages while the
     * connection is being established.
     */
    private ConcurrentLinkedQueue<AZEROIotMessage> unsubscribeQueue = new ConcurrentLinkedQueue<>();

    /**
     * Instantiates a new connection object.
     *
     * @param client
     *            the client
     */
    public AzeroIotConnection(AbstractAzeroIotClient client) {
        this.client = client;
    }

    /**
     * Abstract method which is called to establish an underneath connection.
     *
     * @param callback
     *            connection callback functions
     * @throws AZEROIotException
     *             this exception is thrown when the request is failed to be
     *             sent
     */
    protected abstract void openConnection(AzeroIotMessageCallback callback) throws AZEROIotException;

    /**
     * Abstract method which is called to terminate an underneath connection.
     *
     * @param callback
     *            connection callback functions
     * @throws AZEROIotException
     *             this exception is thrown when the request is failed to be
     *             sent
     */
    protected abstract void closeConnection(AzeroIotMessageCallback callback) throws AZEROIotException;

    /**
     * Abstract method which is called to publish a message.
     *
     * @param message
     *            the message to be published
     * @throws AZEROIotException
     *             this exception is thrown when there's an unrecoverable error
     *             happened while processing the request
     * @throws AzeroIotRetryableException
     *             this exception is thrown when the request is failed to be
     *             sent, which will be queued and retried
     */
    protected abstract void publishMessage(AZEROIotMessage message) throws AZEROIotException, AzeroIotRetryableException;

    /**
     * Abstract method which is called to subscribe to a topic.
     *
     * @param message
     *            the topic to be subscribed to
     * @throws AZEROIotException
     *             this exception is thrown when there's an unrecoverable error
     *             happened while processing the request
     * @throws AzeroIotRetryableException
     *             this exception is thrown when the request is failed to be
     *             sent, which will be queued and retried
     */
    protected abstract void subscribeTopic(AZEROIotMessage message) throws AZEROIotException, AzeroIotRetryableException;

    /**
     * Abstract method which is called to unsubscribe to a topic.
     *
     * @param message
     *            the topic to be unsubscribed to
     * @throws AZEROIotException
     *             this exception is thrown when there's an unrecoverable error
     *             happened while processing the request
     * @throws AzeroIotRetryableException
     *             this exception is thrown when the request is failed to be
     *             sent, which will be queued and retried
     */
    protected abstract void unsubscribeTopic(AZEROIotMessage message) throws AZEROIotException, AzeroIotRetryableException;

    /**
     * The actual publish method exposed by this class.
     *
     * @param message
     *            the message to be published
     * @throws AZEROIotException
     *             this exception is thrown when the underneath failed to
     *             process the request
     */
    public void publish(AZEROIotMessage message) throws AZEROIotException {
        try {
            publishMessage(message);
        } catch (AzeroIotRetryableException e) {
            if (client.getMaxOfflineQueueSize() > 0 && publishQueue.size() < client.getMaxOfflineQueueSize()) {
                publishQueue.add(message);
            } else {
                LOGGER.info("Failed to publish message to " + message.getTopic());
                throw new AZEROIotException(e);
            }
        }
    }

    /**
     * Updates credentials for the connection, which will be used for new
     * connections.
     *
     * @param azeroAccessKeyId
     *            the AZERO access key id
     * @param azeroSecretAccessKey
     *            the AZERO secret access key
     * @param sessionToken
     *            Session token received along with the temporary credentials
     *            from services like STS server, AssumeRole, or Azero Cognito.
     */
    public void updateCredentials(String azeroAccessKeyId, String azeroSecretAccessKey, String sessionToken) {
        // default implementation does nothing
    }

    /**
     * The actual subscribe method exposed by this class.
     *
     * @param message
     *            the topic to be subscribed to
     * @throws AZEROIotException
     *             this exception is thrown when the underneath failed to
     *             process the request
     */
    public void subscribe(AZEROIotMessage message) throws AZEROIotException {
        try {
            subscribeTopic(message);
        } catch (AzeroIotRetryableException e) {
            if (client.getMaxOfflineQueueSize() > 0 && subscribeQueue.size() < client.getMaxOfflineQueueSize()) {
                subscribeQueue.add(message);
            } else {
                LOGGER.info("Failed to subscribe to " + message.getTopic());
                throw new AZEROIotException(e);
            }
        }
    }

    /**
     * The actual unsubscribe method exposed by this class.
     *
     * @param message
     *            the topic to be unsubscribed to
     * @throws AZEROIotException
     *             this exception is thrown when the underneath failed to
     *             process the request
     */
    public void unsubscribe(AZEROIotMessage message) throws AZEROIotException {
        try {
            unsubscribeTopic(message);
        } catch (AzeroIotRetryableException e) {
            if (client.getMaxOfflineQueueSize() > 0 && unsubscribeQueue.size() < client.getMaxOfflineQueueSize()) {
                unsubscribeQueue.add(message);
            } else {
                LOGGER.info("Failed to unsubscribe to " + message.getTopic());
                throw new AZEROIotException(e);
            }
        }
    }

    /**
     * The actual connect method exposed by this class.
     *
     * @param callback
     *            user callback functions
     * @throws AZEROIotException
     *             this exception is thrown when the underneath layer failed to
     *             process the request
     */
    public void connect(AzeroIotMessageCallback callback) throws AZEROIotException {
        cancelRetry();
        retryTimes = 0;
        userDisconnect = false;
        connectCallback = callback;
        openConnection(null);
    }

    /**
     * The actual disconnect method exposed by this class.
     * 
     * @param callback
     *            user callback functions
     * @throws AZEROIotException
     *             this exception is thrown when the underneath layer failed to
     *             process the request
     */
    public void disconnect(AzeroIotMessageCallback callback) throws AZEROIotException {
        cancelRetry();
        retryTimes = 0;
        userDisconnect = true;
        connectCallback = null;
        closeConnection(callback);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.azero.services.iot.client.core.AzeroIotConnectionCallback#
     * onConnectionSuccess()
     */
    @Override
    public void onConnectionSuccess() {
        LOGGER.info("Connection successfully established");
        connectionStatus = AZEROIotConnectionStatus.CONNECTED;
        retryTimes = 0;
        cancelRetry();
        // process offline messages
        try {
            while (subscribeQueue.size() > 0) {
                AZEROIotMessage message = subscribeQueue.poll();
                subscribeTopic(message);
            }
            while (unsubscribeQueue.size() > 0) {
                AZEROIotMessage message = unsubscribeQueue.poll();
                unsubscribeTopic(message);
            }
            while (publishQueue.size() > 0) {
                AZEROIotMessage message = publishQueue.poll();
                publishMessage(message);
            }
        } catch (AZEROIotException | AzeroIotRetryableException e) {
            // should close the connection if we can't send message when
            // connection is good
            LOGGER.log(Level.WARNING, "Failed to send queued messages, will disconnect", e);
            try {
                closeConnection(null);
            } catch (AZEROIotException ie) {
                LOGGER.log(Level.WARNING, "Failed to disconnect", ie);
            }
        }
        client.onConnectionSuccess();
        if (connectCallback != null) {
            connectCallback.onSuccess();
            connectCallback = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.azero.services.iot.client.core.AzeroIotConnectionCallback#
     * onConnectionFailure()
     */
    @Override
    public void onConnectionFailure() {
        LOGGER.info("Connection temporarily lost");
        connectionStatus = AZEROIotConnectionStatus.DISCONNECTED;
        cancelRetry();
        if (shouldRetry()) {
            retryConnection();
            client.onConnectionFailure();
        } else {
            // permanent failure, notify the client and no more retries
            LOGGER.info("Connection retry cancelled or exceeded maximum retries");
            if (connectCallback != null) {
                connectCallback.onFailure();
                connectCallback = null;
            }
            client.onConnectionClosed();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.azero.services.iot.client.core.AzeroIotConnectionCallback#
     * onConnectionClosed()
     */
    @Override
    public void onConnectionClosed() {
        LOGGER.info("Connection permanently closed");
        connectionStatus = AZEROIotConnectionStatus.DISCONNECTED;
        cancelRetry();
        if (connectCallback != null) {
            connectCallback.onFailure();
            connectCallback = null;
        }
        client.onConnectionClosed();
    }

    /**
     * Whether or not to reestablish the connection.
     *
     * @return true, if successful
     */
    private boolean shouldRetry() {
        return (!userDisconnect && (client.getMaxConnectionRetries() > 0 && retryTimes < client.getMaxConnectionRetries()));
    }

    /**
     * Cancel any pending retry request.
     */
    private void cancelRetry() {
        if (retryTask != null) {
            retryTask.cancel(false);
            retryTask = null;
        }
    }

    /**
     * Gets the exponentially back-off retry delay based on the number of times
     * the connection has been retried.
     *
     * @return the retry delay
     */
    long getRetryDelay() {
        double delay = Math.pow(2.0, retryTimes) * client.getBaseRetryDelay();
        delay = Math.min(delay, (double) client.getMaxRetryDelay());
        delay = Math.max(delay, 0.0);
        return (long) delay;
    }

    /**
     * Schedule retry task so the connection can be retried after the timeout
     */
    private void retryConnection() {
        if (retryTask != null) {
            LOGGER.warning("Connection retry already in progress");
            // retry task already scheduled, do nothing
            return;
        }
        retryTask = client.scheduleTimeoutTask(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Connection is being retried");
                connectionStatus = AZEROIotConnectionStatus.RECONNECTING;
                retryTimes++;
                try {
                    openConnection(null);
                } catch (AZEROIotException e) {
                    // permanent failure, notify the client and no more retries
                    client.onConnectionClosed();
                }
            }
        }, getRetryDelay());
    }

    /**
     * The client the connection is associated with.
     *
     * @return the current client
     */
    @java.lang.SuppressWarnings("all")
    public AbstractAzeroIotClient getClient() {
        return this.client;
    }

    /**
     * The connection status.
     *
     *            the new connection status
     * @return the current connection status
     */
    @java.lang.SuppressWarnings("all")
    public AZEROIotConnectionStatus getConnectionStatus() {
        return this.connectionStatus;
    }

    /**
     * The connection status.
     * 
     * @param connectionStatus
     *            the new connection status
     */
    @java.lang.SuppressWarnings("all")
    public void setConnectionStatus(final AZEROIotConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    /**
     * The future object holding the retry task.
     * 
     * @return the current retry task
     */
    @java.lang.SuppressWarnings("all")
    public Future<?> getRetryTask() {
        return this.retryTask;
    }

    /**
     * The retry times.
     * 
     * @return the current retry times
     */
    @java.lang.SuppressWarnings("all")
    public int getRetryTimes() {
        return this.retryTimes;
    }

    /**
     * The callback functions for the connect request.
     *
     * @return the current connect callback
     */
    @java.lang.SuppressWarnings("all")
    public AzeroIotMessageCallback getConnectCallback() {
        return this.connectCallback;
    }

    /**
     * Flag to indicate user disconnect is in progress.
     *
     * @return the current user disconnect flag
     */
    @java.lang.SuppressWarnings("all")
    public boolean isUserDisconnect() {
        return this.userDisconnect;
    }

    /**
     * The offline publish queue holding messages while the connection is being
     * established.
     * 
     * @return the current offline publish queue
     */
    @java.lang.SuppressWarnings("all")
    public ConcurrentLinkedQueue<AZEROIotMessage> getPublishQueue() {
        return this.publishQueue;
    }

    /**
     * The offline subscribe request queue holding messages while the connection
     * is being established.
     * 
     * @return the current offline subscribe request queue
     */
    @java.lang.SuppressWarnings("all")
    public ConcurrentLinkedQueue<AZEROIotMessage> getSubscribeQueue() {
        return this.subscribeQueue;
    }

    /**
     * The offline unsubscribe request queue holding messages while the
     * connection is being established.
     * 
     * @return the current offline unsubscribe request queue
     */
    @java.lang.SuppressWarnings("all")
    public ConcurrentLinkedQueue<AZEROIotMessage> getUnsubscribeQueue() {
        return this.unsubscribeQueue;
    }
}
