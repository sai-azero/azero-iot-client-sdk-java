package com.azero.services.iot.client;

import java.io.UnsupportedEncodingException;

import com.azero.services.iot.client.core.AzeroIotMessageCallback;
import com.azero.services.iot.client.core.AzeroIotRuntimeException;

/**
 * A common data structure that is used in a lot of non-blocking APIs in this
 * library.
 * <p>
 * It provides common data elements, such as {@link #topic}, {@link #qos}, and
 * {@link #payload}, used by the APIs.
 * </p>
 * <p>
 * It also contains callback functions that can be overridden to provide
 * customized handlers. The callback functions are invoked when a non-blocking
 * API call has completed successfully, unsuccessfully, or timed out.
 * Applications wish to have customized callback functions must extend this
 * class or its child classes, such as {@link AZEROIotTopic}.
 */
public class AZEROIotMessage implements AzeroIotMessageCallback {
    /**
     * The topic the message is received from or published to.
     */
    protected String topic;
    /**
     * The MQTT QoS level for the message.
     */
    protected AZEROIotQos qos;
    /**
     * The payload of the message.
     */
    protected byte[] payload;
    /**
     * Error code for shadow methods. It's only applicable to messages returned
     * by those shadow method APIs.
     */
    protected AZEROIotDeviceErrorCode errorCode;
    /**
     * Error message for shadow methods. It's only applicable to messages
     * returned by those shadow method APIs.
     */
    protected String errorMessage;

    /**
     * Instantiates a new message object.
     *
     * @param topic
     *            the topic of the message
     * @param qos
     *            the QoS level of the message
     */
    public AZEROIotMessage(String topic, AZEROIotQos qos) {
        this.topic = topic;
        this.qos = qos;
    }

    /**
     * Instantiates a new message object.
     *
     * @param topic
     *            the topic of the message
     * @param qos
     *            the QoS level of the message
     * @param payload
     *            the payload of the message
     */
    public AZEROIotMessage(String topic, AZEROIotQos qos, byte[] payload) {
        this.topic = topic;
        this.qos = qos;
        setPayload(payload);
    }

    /**
     * Instantiates a new message object.
     *
     * @param topic
     *            the topic of the message
     * @param qos
     *            the QoS level of the message
     * @param payload
     *            the payload of the message
     */
    public AZEROIotMessage(String topic, AZEROIotQos qos, String payload) {
        this.topic = topic;
        this.qos = qos;
        setStringPayload(payload);
    }

    /**
     * Gets the byte array payload.
     *
     * @return the byte array payload
     */
    public byte[] getPayload() {
        if (payload == null) {
            return null;
        }
        return payload.clone();
    }

    /**
     * Sets the byte array payload.
     *
     * @param payload
     *            the new byte array payload
     */
    public void setPayload(byte[] payload) {
        if (payload == null) {
            this.payload = null;
            return;
        }
        this.payload = payload.clone();
    }

    /**
     * Gets the string payload.
     *
     * @return the string payload
     */
    public String getStringPayload() {
        if (payload == null) {
            return null;
        }
        String str;
        try {
            str = new String(payload, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AzeroIotRuntimeException(e);
        }
        return str;
    }

    /**
     * Sets the string payload.
     *
     * @param payload
     *            the new string payload
     */
    public void setStringPayload(String payload) {
        if (payload == null) {
            this.payload = null;
            return;
        }
        try {
            this.payload = payload.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AzeroIotRuntimeException(e);
        }
    }

    /**
     * Callback function to be invoked a non-block API has completed
     * successfully.
     */
    @Override
    public void onSuccess() {
        // Default callback implementation is no-op
    }

    /**
     * Callback function to be invoked a non-block API has completed
     * unsuccessfully.
     */
    @Override
    public void onFailure() {
        // Default callback implementation is no-op
    }

    /**
     * Callback function to be invoked a non-block API has timed out.
     */
    @Override
    public void onTimeout() {
        // Default callback implementation is no-op
    }

    /**
     * The topic the message is received from or published to.
     *
     * @return the current topic of the message
     */
    @java.lang.SuppressWarnings("all")
    public String getTopic() {
        return this.topic;
    }

    /**
     * The topic the message is received from or published to.
     *
     * @param topic the new topic of the message
     */
    @java.lang.SuppressWarnings("all")
    public void setTopic(final String topic) {
        this.topic = topic;
    }

    /**
     * The MQTT QoS level for the message.
     *
     * @return the current QoS level
     */
    @java.lang.SuppressWarnings("all")
    public AZEROIotQos getQos() {
        return this.qos;
    }

    /**
     * The MQTT QoS level for the message.
     *
     * @param qos the new QoS level
     */
    @java.lang.SuppressWarnings("all")
    public void setQos(final AZEROIotQos qos) {
        this.qos = qos;
    }

    /**
     * Error code for shadow methods. It's only applicable to messages returned
     * by those shadow method APIs.
     *
     * @return the current error code of the shadow method
     */
    @java.lang.SuppressWarnings("all")
    public AZEROIotDeviceErrorCode getErrorCode() {
        return this.errorCode;
    }

    /**
     * Error code for shadow methods. It's only applicable to messages returned
     * by those shadow method APIs.
     *
     * @param errorCode the new error code for the shadow method
     */
    @java.lang.SuppressWarnings("all")
    public void setErrorCode(final AZEROIotDeviceErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Error message for shadow methods. It's only applicable to messages
     * returned by those shadow method APIs.
     *
     * @return the current error message of the shadow method
     */
    @java.lang.SuppressWarnings("all")
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Error message for shadow methods. It's only applicable to messages
     * returned by those shadow method APIs.
     *
     * @param errorMessage the new error message for the shadow method
     */
    @java.lang.SuppressWarnings("all")
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
