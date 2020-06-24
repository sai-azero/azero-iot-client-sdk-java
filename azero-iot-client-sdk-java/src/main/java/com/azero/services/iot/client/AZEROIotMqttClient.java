package com.azero.services.iot.client;

import javax.net.ssl.SSLSocketFactory;

import com.azero.services.iot.client.core.AbstractAzeroIotClient;

import java.security.KeyStore;

public class AZEROIotMqttClient extends AbstractAzeroIotClient {

    /**
     * Instantiates a new client using TLS 1.2 mutual authentication. Client
     * certificate and private key are passed in through the {@link KeyStore}
     * argument. The key password protecting the private key in the
     * {@link KeyStore} is also required.
     *
     * @param clientEndpoint
     *            the client endpoint in the form of {@code <account-specific
     *            prefix>.iot.soundai.cn}. The account-specific
     *            prefix can be found on the AZERO IoT console or by using the
     *            {@code describe-endpoint} command through the AZERO command line
     *            interface.
     * @param clientId
     *            the client ID uniquely identify a MQTT connection. Two clients
     *            with the same client ID are not allowed to be connected
     *            concurrently to a same endpoint.
     * @param keyStore
     *            the key store containing the client X.509 certificate and
     *            private key. The {@link KeyStore} object can be constructed
     *            using X.509 certificate file and private key file created on
     *            the AZERO IoT console. For more details, please refer to the
     *            README file of this SDK.
     * @param keyPassword
     *            the key password protecting the private key in the
     *            {@code keyStore} argument.
     */
    public AZEROIotMqttClient(String clientEndpoint, String clientId, KeyStore keyStore, String keyPassword) {
        super(clientEndpoint, clientId, keyStore, keyPassword);
    }

    /**
     * Instantiates a new client using TLS 1.2 mutual authentication. Client
     * certificate and private key should be used to initialize the KeyManager
     * of the socketFactory.
     *
     * @param clientEndpoint
     *            the client endpoint in the form of {@code <account-specific
     *            prefix>.iot.soundai.cn}. The account-specific
     *            prefix can be found on the AZERO IoT console or by using the
     *            {@code describe-endpoint} command through the AZERO command line
     *            interface.
     * @param clientId
     *            the client ID uniquely identify a MQTT connection. Two clients
     *            with the same client ID are not allowed to be connected
     *            concurrently to a same endpoint.
     * @param socketFactory
     *            A socketFactory instantiated with a Keystore containing the client X.509
     *            certificate and private key, and a Truststore containing trusted
     *            Certificate Authorities(CAs).
     */
    public AZEROIotMqttClient(String clientEndpoint, String clientId, SSLSocketFactory socketFactory) {
        super(clientEndpoint, clientId, socketFactory);
    }

    /**
     * Instantiates a new client using TLS 1.2 mutual authentication. Client
     * certificate and private key should be used to initialize the KeyManager
     * of the socketFactory.
     *
     * @param clientEndpoint
     *            the client endpoint in the form of {@code <account-specific
     *            prefix>.iot.soundai.cn}. The account-specific
     *            prefix can be found on the AZERO IoT console or by using the
     *            {@code describe-endpoint} command through the AZERO command line
     *            interface.
     * @param clientId
     *            the client ID uniquely identify a MQTT connection. Two clients
     *            with the same client ID are not allowed to be connected
     *            concurrently to a same endpoint.
     * @param socketFactory
     *            A socketFactory instantiated with a Keystore containing the client X.509
     *            certificate and private key, and a Truststore containing trusted
     *            Certificate Authorities(CAs).
     * @param port
     *            The socket port to use.
     */
    public AZEROIotMqttClient(String clientEndpoint, String clientId, SSLSocketFactory socketFactory, int port) {
        super(clientEndpoint, clientId, socketFactory, port);
    }

    
    /**
     * Instantiates a new client using Secure WebSocket and AZERO SigV4
     * authentication. AZERO IAM credentials, including the access key ID and
     * secret access key, are required for signing the request. Credentials can
     * be permanent ones associated with IAM users or temporary ones generated
     * via the AZERO Cognito service.
     *
     * @param clientEndpoint
     *            the client endpoint in the form of
     *            {@literal <account-specific-prefix>.iot.soundai.cn}
     *            . The account-specific prefix can be found on the AZERO IoT
     *            console or by using the {@code describe-endpoint} command
     *            through the AZERO command line interface.
     * @param clientId
     *            the client ID uniquely identify a MQTT connection. Two clients
     *            with the same client ID are not allowed to be connected
     *            concurrently to a same endpoint.
     * @param azeroAccessKeyId
     *            the AZERO access key id
     * @param azeroSecretAccessKey
     *            the AZERO secret access key
     */
    public AZEROIotMqttClient(String clientEndpoint, String clientId, String azeroAccessKeyId, String azeroSecretAccessKey) {
        super(clientEndpoint, clientId, azeroAccessKeyId, azeroSecretAccessKey, null);
    }

    /**
     * Instantiates a new client using Secure WebSocket and AZERO SigV4
     * authentication. AZERO IAM credentials, including the access key ID and
     * secret access key, are required for signing the request. Credentials can
     * be permanent ones associated with IAM users or temporary ones generated
     * via the AZERO Cognito service.
     *
     * @param clientEndpoint
     *            the client endpoint in the form of
     *            {@literal <account-specific-prefix>.iot.soundai.cn}
     *            . The account-specific prefix can be found on the AZERO IoT
     *            console or by using the {@code describe-endpoint} command
     *            through the AZERO command line interface.
     * @param clientId
     *            the client ID uniquely identify a MQTT connection. Two clients
     *            with the same client ID are not allowed to be connected
     *            concurrently to a same endpoint.
     * @param azeroAccessKeyId
     *            the AZERO access key id
     * @param azeroSecretAccessKey
     *            the AZERO secret access key
     * @param sessionToken
     *            Session token received along with the temporary credentials
     *            from services like STS server, AssumeRole, or Amazon Cognito.
     */
    public AZEROIotMqttClient(String clientEndpoint, String clientId, String azeroAccessKeyId, String azeroSecretAccessKey,
            String sessionToken) {
        super(clientEndpoint, clientId, azeroAccessKeyId, azeroSecretAccessKey, sessionToken);
    }

    /**
     * Instantiates a new client using Secure WebSocket and AZERO SigV4
     * authentication. AZERO IAM credentials, including the access key ID and
     * secret access key, are required for signing the request. Credentials can
     * be permanent ones associated with IAM users or temporary ones generated
     * via the AZERO Cognito service.
     *
     * @param clientEndpoint
     *            the client endpoint in the form of
     *            {@literal <account-specific-prefix>.iot.soundai.cn}
     *            . The account-specific prefix can be found on the AZERO IoT
     *            console or by using the {@code describe-endpoint} command
     *            through the AZERO command line interface.
     * @param clientId
     *            the client ID uniquely identify a MQTT connection. Two clients
     *            with the same client ID are not allowed to be connected
     *            concurrently to a same endpoint.
     * @param azeroAccessKeyId
     *            the AZERO access key id
     * @param azeroSecretAccessKey
     *            the AZERO secret access key
     * @param sessionToken
     *            Session token received along with the temporary credentials
     *            from services like STS server, AssumeRole, or Amazon Cognito.
     * @param region
     *            the AZERO region
     */
    public AZEROIotMqttClient(String clientEndpoint, String clientId, String azeroAccessKeyId, String azeroSecretAccessKey,
                            String sessionToken, String region) {
        super(clientEndpoint, clientId, azeroAccessKeyId, azeroSecretAccessKey, sessionToken, region);
    }

    /**
     * Updates credentials used for signing Secure WebSocket URLs. When temporary
     * credentails used for the WebSocket connection are expired, newer
     * credentails can be supplied through this API to allow new connections to
     * be reestablished using the new credentails.
     *
     * @param azeroAccessKeyId
     *            the AZERO access key id
     * @param azeroSecretAccessKey
     *            the AZERO secret access key
     * @param sessionToken
     *            Session token received along with the temporary credentials
     *            from services like STS server, AssumeRole, or Amazon Cognito.
     */
    @Override
    public void updateCredentials(String azeroAccessKeyId, String azeroSecretAccessKey, String sessionToken) {
        super.updateCredentials(azeroAccessKeyId, azeroSecretAccessKey, sessionToken);
    }

    /**
     * Gets the number of client threads currently configured. Each client has
     * their own thread pool, which is used to execute user callback functions
     * as well as any timeout callback functions requested. By default, the
     * thread pool is configured with one execution thread.
     *
     * @return the number of client threads
     */
    @Override
    public int getNumOfClientThreads() {
        return super.getNumOfClientThreads();
    }

    /**
     * Sets a new value for the number of client threads. This value must be set
     * before {@link #connect()} is called.
     *
     * @param numOfClientThreads
     *            the new number of client threads. The default value is 1.
     */
    @Override
    public void setNumOfClientThreads(int numOfClientThreads) {
        super.setNumOfClientThreads(numOfClientThreads);
    }

    /**
     * Gets the connection timeout in milliseconds currently configured.
     * Connection timeout specifies how long the client should wait for the
     * connection to be established with the server. By default, it's 30,000ms.
     *
     * @return the connection timeout
     */
    @Override
    public int getConnectionTimeout() {
        return super.getConnectionTimeout();
    }

    /**
     * Sets a new value in milliseconds for the connection timeout. This value
     * must be set before {@link #connect()} is called.
     *
     * @param connectionTimeout
     *            the new connection timeout. The default value is 30,000ms.
     */
    @Override
    public void setConnectionTimeout(int connectionTimeout) {
        super.setConnectionTimeout(connectionTimeout);
    }

    /**
     * Gets the maximum number of connection retries currently configured.
     * Connections will be automatically retried for the configured maximum
     * times when failing to be established or lost. User disconnect, requested
     * via {@link #disconnect()} will not be retried. By default, it's 5 times.
     * Setting it to 0 will disable the connection retry function.
     *
     * @return the max connection retries
     */
    @Override
    public int getMaxConnectionRetries() {
        return super.getMaxConnectionRetries();
    }

    /**
     * Sets a new value for the maximum connection retries. This value must be
     * set before {@link #connect()} is called. Setting it to 0 will disable the
     * connection retry function.
     *
     * @param maxConnectionRetries
     *            the new max connection retries. The default value is 5.
     */
    @Override
    public void setMaxConnectionRetries(int maxConnectionRetries) {
        super.setMaxConnectionRetries(maxConnectionRetries);
    }

    /**
     * Gets the base retry delay in milliseconds currently configured. For each
     * connection failure, a brief delay has to elapse before the connection is
     * retried. The retry delay is calculated using this simple formula
     * {@code delay = min(baseRetryDelay * pow(2, numRetries), maxRetryDelay)}.
     * By default, the base retry delay is 3,000ms.
     *
     * @return the base retry delay
     */
    @Override
    public int getBaseRetryDelay() {
        return super.getBaseRetryDelay();
    }

    /**
     * Sets a new value in milliseconds for the base retry delay. This value
     * must be set before {@link #connect()} is called.
     *
     * @param baseRetryDelay
     *            the new base retry delay. The default value is 3,000ms.
     */
    @Override
    public void setBaseRetryDelay(int baseRetryDelay) {
        super.setBaseRetryDelay(baseRetryDelay);
    }

    /**
     * Gets the maximum retry delay in milliseconds currently configured. For
     * each connection failure, a brief delay has to elapse before the
     * connection is retried. The retry delay is calculated using this simple
     * formula
     * {@code delay = min(baseRetryDelay * pow(2, numRetries), maxRetryDelay)}.
     * By default, the maximum retry delay is 30,000ms.
     *
     * @return the maximum retry delay
     */
    @Override
    public int getMaxRetryDelay() {
        return super.getMaxRetryDelay();
    }

    /**
     * Sets a new value in milliseconds for the maximum retry delay. This value
     * must be set before {@link #connect()} is called.
     *
     * @param maxRetryDelay
     *            the new max retry delay. The default value is 30,000ms.
     */
    @Override
    public void setMaxRetryDelay(int maxRetryDelay) {
        super.setMaxRetryDelay(maxRetryDelay);
    }

    /**
     * Gets the server acknowledge timeout in milliseconds currently configured.
     * This timeout is used internally by the SDK when subscribing to shadow
     * confirmation topics for get, update, and delete requests. It's also used
     * for re-subscribing to user topics when the connection is retried. For
     * most of the APIs provided in the SDK, the user can specify the timeout as
     * an argument. By default, the server acknowledge timeout is 3,000ms.
     *
     * @return the server acknowledge timeout
     */
    @Override
    public int getServerAckTimeout() {
        return super.getServerAckTimeout();
    }

    /**
     * Sets a new value in milliseconds for the default server acknowledge
     * timeout. This value must be set before {@link #connect()} is called.
     *
     * @param serverAckTimeout
     *            the new server acknowledge timeout. The default value is
     *            3,000ms.
     */
    @Override
    public void setServerAckTimeout(int serverAckTimeout) {
        super.setServerAckTimeout(serverAckTimeout);
    }

    /**
     * Gets the keep-alive interval for the MQTT connection in milliseconds
     * currently configured. Setting this value to 0 will disable the keep-alive
     * function for the connection. The default keep alive interval is 30,000ms.
     *
     * @return the keep alive interval
     */
    @Override
    public int getKeepAliveInterval() {
        return super.getKeepAliveInterval();
    }

    /**
     * Sets a new value in milliseconds for the connection keep-alive interval.
     * This value must be set before {@link #connect()} is called. Setting this
     * value to 0 will disable the keep-alive function.
     *
     * @param keepAliveInterval
     *            the new keep alive interval. The default value is 30,000ms.
     */
    @Override
    public void setKeepAliveInterval(int keepAliveInterval) {
        super.setKeepAliveInterval(keepAliveInterval);
    }

    /**
     * Gets the maximum offline queue size current configured. The offline
     * queues are used for temporarily holding outgoing requests while the
     * connection is being established or retried. When the connection is
     * established, offline queue messages will be sent out as usual. They can
     * be useful for dealing with transient connection failures by allowing the
     * application to continuously send requests while the connection is being
     * established. Each type of request, namely publish, subscribe, and
     * unsubscribe, has their own offline queue. The default offline queue size
     * is 64. Setting it to 0 will disable the offline queues.
     *
     * @return the max offline queue size
     */
    @Override
    public int getMaxOfflineQueueSize() {
        return super.getMaxOfflineQueueSize();
    }

    /**
     * Sets a new value for the maximum offline queue size. This value must be
     * set before {@link #connect()} is called. Setting it to 0 will disable the
     * offline queues.
     *
     * @param maxOfflineQueueSize
     *            the new maximum offline queue size. The default value is 64.
     */
    @Override
    public void setMaxOfflineQueueSize(int maxOfflineQueueSize) {
        super.setMaxOfflineQueueSize(maxOfflineQueueSize);
    }

    /**
     * Gets the Last Will and Testament message currently configured. The Last
     * Will and Testament message with configured payload will be published when
     * the client connection is lost or terminated ungracefully, i.e. not
     * through {@link #disconnect()}.
     *
     * @return the will message
     */
    @Override
    public AZEROIotMessage getWillMessage() {
        return super.getWillMessage();
    }

    /**
     * Sets whether the client and server should establish a clean session on each connection.
     * If false, the server should attempt to persist the client's state between connections.
     * This must be set before {@link #connect()} is called.
     *
     * @param cleanSession
     *            If true, the server starts a clean session with the client on each connection.
     *            If false, the server should persist the client's state between connections.
     */
    @Override
    public void setCleanSession(boolean cleanSession) { super.setCleanSession(cleanSession); }

    /**
     * Gets whether each connection is a clean session. See also {@link #setCleanSession(boolean)}.
     * @return whether each connection to the server should be a clean session.
     */
    @Override
    public boolean isCleanSession() { return super.isCleanSession(); }

    /**
     * Sets a new Last Will and Testament message. The message must be set
     * before {@link #connect()} is called. By default, Last Will and Testament
     * message is not sent.
     *
     * @param willMessage
     *            the new Last Will and Testament message message. The default
     *            value is {@code null}.
     */
    @Override
    public void setWillMessage(AZEROIotMessage willMessage) {
        super.setWillMessage(willMessage);
    }

    /**
     * Connect the client to the server. This is a blocking call, so the calling
     * thread will be blocked until the operation succeeded or failed.
     * <p>
     * Note: Blocking API call without specifying a timeout, in very rare cases,
     * can block the calling thread indefinitely, if the server response is not
     * received or lost. Use the alternative APIs with timeout for applications
     * that expect responses within fixed duration.
     * </p>
     *
     * @throws AZEROIotException
     *             exception thrown if the connection operation fails
     */
    @Override
    public void connect() throws AZEROIotException {
        super.connect();
    }

    /**
     * Connect the client to the server. This is a blocking call, so the calling
     * thread will be blocked until the operation succeeded, failed, or timed
     * out.
     *
     * @param timeout
     *            the timeout in milliseconds that the calling thread will wait
     * @throws AZEROIotException
     *             exception thrown if the operation fails
     * @throws AZEROIotTimeoutException
     *             exception thrown if the operation times out
     */
    @Override
    public void connect(long timeout) throws AZEROIotException, AZEROIotTimeoutException {
        super.connect(timeout);
    }

    /**
     * Connect the client to the server. This call can be either blocking or
     * non-blocking specified by the {@code blocking} argument. For blocking
     * calls, the calling thread is blocked until the operation completed,
     * failed, or timed out; for non-blocking calls, the calling thread will not
     * be blocked while the connection is being established.
     *
     * @param timeout
     *            the timeout in milliseconds that the calling thread will wait
     * @param blocking
     *            whether the call should be blocking or non-blocking
     * @throws AZEROIotException
     *             exception thrown if the operation fails
     * @throws AZEROIotTimeoutException
     *             exception thrown if the operation times out
     */
    @Override
    public void connect(long timeout, boolean blocking) throws AZEROIotException, AZEROIotTimeoutException {
        super.connect(timeout, blocking);
    }

    /**
     * Disconnect the client from the server. This is a blocking call, so the
     * calling thread will be blocked until the operation succeeded or failed.
     * <p>
     * Note: Blocking API call without specifying a timeout, in very rare cases,
     * can block the calling thread indefinitely, if the server response is not
     * received or lost. Use the alternative APIs with timeout for applications
     * that expect responses within fixed duration.
     * </p>
     *
     * @throws AZEROIotException
     *             exception thrown if the operation fails
     */
    @Override
    public void disconnect() throws AZEROIotException {
        super.disconnect();
    }

    /**
     * Disconnect the client from the server. This is a blocking call, so the
     * calling thread will be blocked until the operation succeeded, failed, or
     * timed out.
     *
     * @param timeout
     *            the timeout in milliseconds that the calling thread will wait
     * @throws AZEROIotException
     *             exception thrown if the operation fails
     * @throws AZEROIotTimeoutException
     *             exception thrown if the operation times out
     */
    @Override
    public void disconnect(long timeout) throws AZEROIotException, AZEROIotTimeoutException {
        super.disconnect(timeout);
    }

    /**
     * Disconnect the client from the server. This call can be either blocking
     * or non-blocking specified by the {@code blocking} argument. For blocking
     * calls, the calling thread is blocked until the operation completed,
     * failed, or timed out; for non-blocking calls, the calling thread will not
     * be blocked while the connection is being terminated.
     *
     * @param timeout
     *            the timeout in milliseconds that the calling thread will wait
     * @param blocking
     *            whether the call should be blocking or non-blocking
     * @throws AZEROIotException
     *             exception thrown if the operation fails
     * @throws AZEROIotTimeoutException
     *             exception thrown if the operation times out
     */
    @Override
    public void disconnect(long timeout, boolean blocking) throws AZEROIotException, AZEROIotTimeoutException {
        super.disconnect(timeout, blocking);
    }

    /**
     * Publishes the payload to a given topic. This is a blocking call so the
     * calling thread is blocked until the publish operation succeeded or
     * failed. MQTT QoS0 is used for publishing the payload.
     * <p>
     * Note: Blocking API call without specifying a timeout, in very rare cases,
     * can block the calling thread indefinitely, if the server response is not
     * received or lost. Use the alternative APIs with timeout for applications
     * that expect responses within fixed duration.
     * </p>
     *
     * @param topic
     *            the topic to be published to
     * @param payload
     *            the payload to be published
     * @throws AZEROIotException
     *             exception thrown if the publish operation fails
     */
    @Override
    public void publish(String topic, String payload) throws AZEROIotException {
        super.publish(topic, payload);
    }

    /**
     * Publishes the payload to a given topic. This is a blocking call so the
     * calling thread is blocked until the publish operation succeeded, failed,
     * or the specified timeout has elapsed. MQTT QoS0 is used for publishing
     * the payload.
     *
     * @param topic
     *            the topic to be published to
     * @param payload
     *            the payload to be published
     * @param timeout
     *            the timeout in milliseconds that the calling thread will wait
     * @throws AZEROIotException
     *             the exception thrown if the publish operation fails
     * @throws AZEROIotTimeoutException
     *             the exception thrown if the publish operation times out
     */
    @Override
    public void publish(String topic, String payload, long timeout) throws AZEROIotException, AZEROIotTimeoutException {
        super.publish(topic, payload, timeout);
    }

    /**
     * Publishes the payload to a given topic. This is a blocking call so the
     * calling thread is blocked until the publish operation succeeded or
     * failed.
     * <p>
     * Note: Blocking API call without specifying a timeout, in very rare cases,
     * can block the calling thread indefinitely, if the server response is not
     * received or lost. Use the alternative APIs with timeout for applications
     * that expect responses within fixed duration.
     * </p>
     *
     * @param topic
     *            the topic to be published to
     * @param qos
     *            the MQTT QoS used for publishing
     * @param payload
     *            the payload to be published
     * @throws AZEROIotException
     *             the exception thrown if the publish operation fails
     */
    @Override
    public void publish(String topic, AZEROIotQos qos, String payload) throws AZEROIotException {
        super.publish(topic, qos, payload);
    }

    /**
     * Publishes the payload to a given topic. This is a blocking call so the
     * calling thread is blocked until the publish operation succeeded, failed,
     * or the specified timeout has elapsed.
     *
     * @param topic
     *            the topic to be published to
     * @param qos
     *            the MQTT QoS used for publishing
     * @param payload
     *            the payload to be published
     * @param timeout
     *            the timeout in milliseconds that the calling thread will wait
     * @throws AZEROIotException
     *             the exception thrown if the publish operation fails
     * @throws AZEROIotTimeoutException
     *             the exception thrown if the publish operation times out
     */
    @Override
    public void publish(String topic, AZEROIotQos qos, String payload, long timeout) throws AZEROIotException,
            AZEROIotTimeoutException {
        super.publish(topic, qos, payload, timeout);
    }

    /**
     * Publishes the raw payload to a given topic. This is a blocking call so
     * the calling thread is blocked until the publish operation succeeded or
     * failed. MQTT QoS0 is used for publishing the payload.
     * <p>
     * Note: Blocking API call without specifying a timeout, in very rare cases,
     * can block the calling thread indefinitely, if the server response is not
     * received or lost. Use the alternative APIs with timeout for applications
     * that expect responses within fixed duration.
     * </p>
     *
     * @param topic
     *            the topic to be published to
     * @param payload
     *            the payload to be published
     * @throws AZEROIotException
     *             the exception thrown if the publish operation fails
     */
    @Override
    public void publish(String topic, byte[] payload) throws AZEROIotException {
        super.publish(topic, payload);
    }

    /**
     * Publishes the raw payload to a given topic. This is a blocking call so
     * the calling thread is blocked until the publish operation succeeded,
     * failed, or the specified timeout has elapsed. MQTT QoS0 is used for
     * publishing the payload.
     *
     * @param topic
     *            the topic to be published to
     * @param payload
     *            the payload to be published
     * @param timeout
     *            the timeout in milliseconds that the calling thread will wait
     * @throws AZEROIotException
     *             the exception thrown if the publish operation fails
     * @throws AZEROIotTimeoutException
     *             the exception thrown if the publish operation times out
     */
    @Override
    public void publish(String topic, byte[] payload, long timeout) throws AZEROIotException, AZEROIotTimeoutException {
        super.publish(topic, payload, timeout);
    }

    /**
     * Publishes the raw payload to a given topic. This is a blocking call so
     * the calling thread is blocked until the publish operation is succeeded or
     * failed.
     * <p>
     * Note: Blocking API call without specifying a timeout, in very rare cases,
     * can block the calling thread indefinitely, if the server response is not
     * received or lost. Use the alternative APIs with timeout for applications
     * that expect responses within fixed duration.
     * </p>
     *
     * @param topic
     *            the topic to be published to
     * @param qos
     *            the MQTT QoS used for publishing
     * @param payload
     *            the payload to be published
     * @throws AZEROIotException
     *             the exception thrown if the publish operation fails
     */
    @Override
    public void publish(String topic, AZEROIotQos qos, byte[] payload) throws AZEROIotException {
        super.publish(topic, qos, payload);
    }

    /**
     * Publishes the raw payload to a given topic. This is a blocking call so
     * the calling thread is blocked until the publish operation is succeeded,
     * failed, or the specified timeout has elapsed.
     *
     * @param topic
     *            the topic to be published to
     * @param qos
     *            the MQTT QoS used for publishing
     * @param payload
     *            the payload to be published
     * @param timeout
     *            the timeout in milliseconds that the calling thread will wait
     * @throws AZEROIotException
     *             the exception thrown if the publish operation fails
     * @throws AZEROIotTimeoutException
     *             the exception thrown if the publish operation times out
     */
    @Override
    public void publish(String topic, AZEROIotQos qos, byte[] payload, long timeout) throws AZEROIotException,
            AZEROIotTimeoutException {
        super.publish(topic, qos, payload, timeout);
    }

    /**
     * Publishes the payload to a given topic. Topic, MQTT QoS, and payload are
     * given in the {@code message} argument. This is a non-blocking call so it
     * immediately returns once the operation has been queued in the system. The
     * result of the operation will be notified through the callback functions,
     * namely {@link AZEROIotMessage#onSuccess} and
     * {@link AZEROIotMessage#onFailure}, one of which will be invoked after the
     * operation succeeded or failed respectively. The default implementation
     * for the callback functions in {@link AZEROIotMessage} does nothing. The
     * user could override one or more of these functions through subclassing.
     *
     * @param message
     *            the message, including the topic, MQTT QoS, and payload, to be
     *            published
     * @throws AZEROIotException
     *             the exception thrown if the publish operation fails to be
     *             queued
     */
    @Override
    public void publish(AZEROIotMessage message) throws AZEROIotException {
        super.publish(message);
    }

    /**
     * Publishes the payload to a given topic. Topic, MQTT QoS, and payload are
     * given in the {@code message} argument. This is a non-blocking call so it
     * immediately returns once the operation has been queued in the system. The
     * result of the operation will be notified through the callback functions,
     * namely {@link AZEROIotMessage#onSuccess}, {@link AZEROIotMessage#onFailure},
     * and {@link AZEROIotMessage#onTimeout}, one of which will be invoked after
     * the operation succeeded, failed, or timed out respectively. The user
     * could override one or more of these functions through subclassing.
     *
     * @param message
     *            the message, including the topic, MQTT QoS, and payload, to be
     *            published
     * @param timeout
     *            the timeout in milliseconds for the operation to be considered
     *            timed out
     * @throws AZEROIotException
     *             the exception thrown if the publish operation fails to be
     *             queued
     */
    @Override
    public void publish(AZEROIotMessage message, long timeout) throws AZEROIotException {
        super.publish(message, timeout);
    }

    /**
     * Subscribes to a given topic. Topic and MQTT QoS are given in the
     * {@code topic} argument. This call can be either blocking or non-blocking
     * specified by the {@code blocking} argument. For blocking calls, the
     * calling thread is blocked until the subscribe operation completed or
     * failed; for non-blocking calls, the result of the operation will be
     * notified through the callback functions, namely
     * {@link AZEROIotTopic#onSuccess} and {@link AZEROIotTopic#onFailure}, one of
     * which will be invoked after the operation succeeded or failed
     * respectively. For both blocking and non-blocking calls, callback function
     * {@link AZEROIotTopic#onMessage} is invoked when subscribed message arrives.
     * The default implementation for the callback functions in
     * {@link AZEROIotTopic} does nothing. The user could override one or more of
     * these functions through subclassing.
     * <p>
     * Note: Blocking API call without specifying a timeout, in very rare cases,
     * can block the calling thread indefinitely, if the server response is not
     * received or lost. Use the alternative APIs with timeout for applications
     * that expect responses within fixed duration.
     * </p>
     *
     * @param topic
     *            the topic to subscribe to
     * @param blocking
     *            whether the call should be blocking or non-blocking
     * @throws AZEROIotException
     *             the exception thrown if the subscribe operation fails
     *             (blocking) or fails to be queued (non-blocking)
     */
    @Override
    public void subscribe(AZEROIotTopic topic, boolean blocking) throws AZEROIotException {
        super.subscribe(topic, blocking);
    }

    /**
     * Subscribes to a given topic. Topic and MQTT QoS are given in the
     * {@code topic} argument. This call can be either blocking or non-blocking
     * specified by the {@code blocking} argument. For blocking call, the
     * calling thread is blocked until the subscribe operation completed,
     * failed, or timed out; for non-blocking call, the result of the operation
     * will be notified through the callback functions, namely
     * {@link AZEROIotTopic#onSuccess}, {@link AZEROIotTopic#onFailure} and
     * {@link AZEROIotTopic#onTimeout}, one of which will be invoked after the
     * operation succeeded, failed, or timed out respectively. For both blocking
     * and non-blocking calls, callback function {@link AZEROIotTopic#onMessage}
     * is invoked when subscribed message arrives. The default implementation
     * for the callback functions in {@link AZEROIotTopic} does nothing. The user
     * could override one or more of these functions through subclassing.
     *
     * @param topic
     *            the topic to subscribe to
     * @param timeout
     *            the timeout in milliseconds for the operation to be considered
     *            timed out
     * @param blocking
     *            whether the call should be blocking or non-blocking
     * @throws AZEROIotException
     *             the exception thrown if the subscribe operation fails
     *             (blocking) or fails to be queued (non-blocking)
     * @throws AZEROIotTimeoutException
     *             the exception thrown if the subscribe operation times out.
     *             This exception is not thrown if the call is non-blocking;
     *             {@link AZEROIotTopic#onTimeout} will be invoked instead if
     *             timeout happens.
     */
    @Override
    public void subscribe(AZEROIotTopic topic, long timeout, boolean blocking) throws AZEROIotException,
            AZEROIotTimeoutException {
        super.subscribe(topic, timeout, blocking);
    }

    /**
     * Subscribes to a given topic. Topic and MQTT QoS are given in the
     * {@code topic} argument. This is a non-blocking call so it immediately
     * returns once is the operation has been queued in the system. The result
     * of the operation will be notified through the callback functions, namely
     * {@link AZEROIotTopic#onSuccess} and {@link AZEROIotTopic#onFailure}, one of
     * which will be invoked after the operation succeeded or failed
     * respectively. Another callback function, {@link AZEROIotTopic#onMessage},
     * is invoked when subscribed message arrives. The default implementation
     * for the callback functions in {@link AZEROIotTopic} does nothing. The user
     * could override one or more of these functions through sub-classing.
     *
     * @param topic
     *            the topic to subscribe to
     * @throws AZEROIotException
     *             the exception thrown if the subscribe operation fails to be
     *             queued
     */
    @Override
    public void subscribe(AZEROIotTopic topic) throws AZEROIotException {
        super.subscribe(topic);
    }

    /**
     * Subscribes to a given topic. Topic and MQTT QoS are given in the
     * {@code topic} argument. This is a non-blocking call so it immediately
     * returns once is the operation has been queued in the system. The result
     * of the operation will be notified through the callback functions, namely
     * {@link AZEROIotTopic#onSuccess}, {@link AZEROIotTopic#onFailure}, and
     * {@link AZEROIotTopic#onTimeout}, one of which will be invoked after the
     * operation succeeded, failed, or timed out respectively. Another callback
     * function, {@link AZEROIotTopic#onMessage}, is invoked when subscribed
     * message arrives. The default implementation for the callback functions in
     * {@link AZEROIotTopic} does nothing. The user could override one or more of
     * these functions through sub-classing.
     *
     * @param topic
     *            the topic to subscribe to
     * @param timeout
     *            the timeout in milliseconds for the operation to be considered
     *            timed out
     * @throws AZEROIotException
     *             the exception thrown if the subscribe operation fails to be
     *             queued
     */
    @Override
    public void subscribe(AZEROIotTopic topic, long timeout) throws AZEROIotException {
        super.subscribe(topic, timeout);
    }

    /**
     * Unsubscribes to a given topic. This is a blocking call, so the calling
     * thread is blocked until the unsubscribe operation completed or failed.
     * <p>
     * Note: Blocking API call without specifying a timeout, in very rare cases,
     * can block the calling thread indefinitely, if the server response is not
     * received or lost. Use the alternative APIs with timeout for applications
     * that expect responses within fixed duration.
     * </p>
     *
     * @param topic
     *            the topic to unsubscribe to
     * @throws AZEROIotException
     *             the exception thrown if the unsubscribe operation fails
     */
    @Override
    public void unsubscribe(String topic) throws AZEROIotException {
        super.unsubscribe(topic);
    }

    /**
     * Unsubscribes to a given topic. This is a blocking call, so the calling
     * thread is blocked until the unsubscribe operation completed, failed, or
     * the specified timeout has elapsed.
     *
     * @param topic
     *            the topic to unsubscribe to
     * @param timeout
     *            the timeout in milliseconds that the calling thread will wait
     * @throws AZEROIotException
     *             the exception thrown if the unsubscribe operation fails
     * @throws AZEROIotTimeoutException
     *             the exception thrown if the unsubscribe operation times out
     */
    @Override
    public void unsubscribe(String topic, long timeout) throws AZEROIotException, AZEROIotTimeoutException {
        super.unsubscribe(topic, timeout);
    }

    /**
     * Unsubscribes to a given topic. This is a non-blocking call so it
     * immediately returns once the operation has been queued in the system. The
     * result of the operation will be notified through the callback functions,
     * namely {@link AZEROIotTopic#onSuccess} and {@link AZEROIotTopic#onFailure},
     * one of which will be invoked after the operation succeeded or failed
     * respectively. The default implementation for the callback functions in
     * {@link AZEROIotTopic} does nothing. The user could override one or more of
     * these functions through subclassing.
     * 
     * @param topic
     *            the topic to unsubscribe to
     * @throws AZEROIotException
     *             the exception thrown if the unsubscribe operation fails to be
     *             queued
     */
    @Override
    public void unsubscribe(AZEROIotTopic topic) throws AZEROIotException {
        super.unsubscribe(topic);
    }

    /**
     * Unsubscribes to a given topic. This is a non-blocking call so it
     * immediately returns once the operation has been queued in the system. The
     * result of the operation will be notified through the callback functions,
     * namely {@link AZEROIotTopic#onSuccess}, {@link AZEROIotTopic#onFailure}, and
     * {@link AZEROIotTopic#onTimeout}, one of which will be invoked after the
     * operation succeeded, failed, or timed out respectively. The default
     * implementation for the callback functions in {@link AZEROIotTopic} does
     * nothing. The user could override one or more of these functions through
     * subclassing.
     *
     * @param topic
     *            the topic to unsubscribe to
     * @param timeout
     *            the timeout in milliseconds for the operation to be considered
     *            timed out
     * @throws AZEROIotException
     *             the exception thrown if the unsubscribe operation fails to be
     *             queued
     */
    @Override
    public void unsubscribe(AZEROIotTopic topic, long timeout) throws AZEROIotException {
        super.unsubscribe(topic, timeout);
    }

    /**
     * Attach a shadow device to the client. Once attached, the device, if
     * configured, will be automatically synchronized with the AZERO Thing shadow
     * using this client and its connection. For more details about how to
     * configure and use a device, please refer to {@link AZEROIotDevice}.
     *
     * @param device
     *            the device to be attached to the client
     * @throws AZEROIotException
     *             the exception thrown if the attach operation fails
     */
    @Override
    public void attach(AZEROIotDevice device) throws AZEROIotException {
        super.attach(device);
    }

    /**
     * Detach the given device from the client. Device and shadow
     * synchronization will be stopped after the device is detached from the
     * client.
     *
     * @param device
     *            the device to be detached from the client
     * @throws AZEROIotException
     *             the exception thrown if the detach operation fails
     */
    @Override
    public void detach(AZEROIotDevice device) throws AZEROIotException {
        super.detach(device);
    }

    /**
     * Gets the connection status of the connection used by the client.
     *
     * @return the connection status
     */
    @Override
    public AZEROIotConnectionStatus getConnectionStatus() {
        return super.getConnectionStatus();
    }

    /**
     * This callback function is called when the connection used by the client
     * is successfully established. The user could supply a different callback
     * function via subclassing, however the default implementation should
     * always be called in the override function in order for the connection
     * retry as well as device synchronization to work properly.
     */
    @Override
    public void onConnectionSuccess() {
        super.onConnectionSuccess();
    }

    /**
     * This callback function is called when the connection used by the client
     * is temporarily lost. The user could supply a different callback function
     * via subclassing, however the default implementation should always be
     * called in the override function in order for the connection retry as well
     * as device synchronization to work properly.
     */
    @Override
    public void onConnectionFailure() {
        super.onConnectionFailure();
    }

    /**
     * This callback function is called when the connection used by the client
     * is permanently closed. The user could supply a different callback
     * function via subclassing, however the default implementation should
     * always be called in the override function in order for the connection
     * retry as well as device synchronization to work properly.
     */
    @Override
    public void onConnectionClosed() {
        super.onConnectionClosed();
    }

}
