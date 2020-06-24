package com.azero.services.iot.client;

/**
 * The class provides default values for the library. All the values defined
 * here can be overridden at runtime through setter functions in
 * {@link AZEROIotMqttClient} and {@link AZEROIotDevice}.
 */
public class AZEROIotConfig {

    /**
     * The default value for number of client threads. See also
     * {@link AZEROIotMqttClient#getNumOfClientThreads()}.
     */
    public static final int NUM_OF_CLIENT_THREADS = 1;

    /**
     * The default value for client connection timeout (milliseconds). See also
     * {@link AZEROIotMqttClient#getConnectionTimeout()}.
     */
    public static final int CONNECTION_TIMEOUT = 30000;

    /**
     * The default value for service acknowledge timeout (milliseconds). See
     * also {@link AZEROIotMqttClient#getServerAckTimeout()}.
     */
    public static final int SERVER_ACK_TIMEOUT = 3000;

    /**
     * The default value for client keep-alive interval (milliseconds). See also
     * {@link AZEROIotMqttClient#getKeepAliveInterval()}.
     */
    public static final int KEEP_ALIVE_INTERVAL = 600000;

    /**
     * The default value for maximum connection retry times. See also
     * {@link AZEROIotMqttClient#getMaxConnectionRetries()}.
     */
    public static final int MAX_CONNECTION_RETRIES = 5;

    /**
     * The default value for connection base retry delay (milliseconds). See
     * also {@link AZEROIotMqttClient#getBaseRetryDelay()}.
     */
    public static final int CONNECTION_BASE_RETRY_DELAY = 3000;

    /**
     * The default value for connection maximum retry delay (milliseconds). See
     * also {@link AZEROIotMqttClient#getMaxRetryDelay()}.
     */
    public static final int CONNECTION_MAX_RETRY_DELAY = 30000;

    /**
     * The default value for clean session connections.
     * See also {@link AZEROIotMqttClient#isCleanSession()}.
     */
    public static final boolean CLEAN_SESSION = true;

    /**
     * The default value for maximum offline queue size. See also
     * {@link AZEROIotMqttClient#getMaxOfflineQueueSize()}.
     */
    public static final int MAX_OFFLINE_QUEUE_SIZE = 64;

    /**
     * The default value for device reporting interval (milliseconds). See also
     * {@link AZEROIotDevice#getReportInterval()}.
     */
    public static final int DEVICE_REPORT_INTERVAL = 3000;

    /**
     * The default value for enabling device update versioning. See also
     * {@link AZEROIotDevice#isEnableVersioning()}.
     */
    public static final boolean DEVICE_ENABLE_VERSIONING = false;

    /**
     * The default value for device reporting QoS level. See also
     * {@link AZEROIotDevice#getDeviceReportQos()}.
     */
    public static final int DEVICE_REPORT_QOS = 0;

    /**
     * The default value for the QoS level for subscribing to shadow updates.
     * See also {@link AZEROIotDevice#getShadowUpdateQos()}.
     */
    public static final int DEVICE_SHADOW_UPDATE_QOS = 0;

    /**
     * The default value for the QoS level for publishing shadow methods. See
     * also {@link AZEROIotDevice#getMethodQos()}.
     */
    public static final int DEVICE_METHOD_QOS = 0;

    /**
     * The default value for the QoS level for subscribing to shadow method
     * acknowledgement. See also {@link AZEROIotDevice#getMethodAckQos()}.
     */
    public static final int DEVICE_METHOD_ACK_QOS = 0;

}
