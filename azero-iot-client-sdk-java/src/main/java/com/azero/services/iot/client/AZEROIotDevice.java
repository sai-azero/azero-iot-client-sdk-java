package com.azero.services.iot.client;

import com.azero.services.iot.client.shadow.AbstractAzeroIotDevice;

public class AZEROIotDevice extends AbstractAzeroIotDevice {

    /**
     * Instantiates a new device instance.
     *
     * @param thingName
     *            the thing name
     */
    public AZEROIotDevice(String thingName,String accountPrefix) {
        super(thingName,accountPrefix);
    }

    /**
     * Gets the device report interval.
     *
     * @return the report interval in milliseconds.
     */
    @Override
    public long getReportInterval() {
        return super.getReportInterval();
    }

    /**
     * Sets the device report interval in milliseconds. This value must be set
     * before the device is attached to a client via the
     * {@link AZEROIotMqttClient#attach(AZEROIotDevice)} call. The default interval
     * is 3,000ms. Setting it to 0 will disable reporting.
     *
     * @param reportInterval
     *            the new report interval
     */
    @Override
    public void setReportInterval(long reportInterval) {
        super.setReportInterval(reportInterval);
    }

    /**
     * Checks if versioning is enabled for device updates.
     *
     * @return true, if versioning is enabled for device updates.
     */
    @Override
    public boolean isEnableVersioning() {
        return super.isEnableVersioning();
    }

    /**
     * Sets the device update versioning to be enabled or disabled. This value
     * must be set before the device is attached to a client via the
     * {@link AZEROIotMqttClient#attach(AZEROIotDevice)} call.
     *
     * @param enableVersioning
     *            true to enable device update versioning; false to disable.
     */
    @Override
    public void setEnableVersioning(boolean enableVersioning) {
        super.setEnableVersioning(enableVersioning);
    }

    /**
     * Gets the MQTT QoS level for publishing the device report. The default QoS
     * is QoS 0.
     *
     * @return the device report QoS
     */
    @Override
    public AZEROIotQos getDeviceReportQos() {
        return super.getDeviceReportQos();
    }

    /**
     * Sets the MQTT QoS level for publishing the device report. This value must
     * be set before the device is attached to a client via the
     * {@link AZEROIotMqttClient#attach(AZEROIotDevice)} call.
     *
     * @param deviceReportQos
     *            the new device report QoS
     */
    @Override
    public void setDeviceReportQos(AZEROIotQos deviceReportQos) {
        super.setDeviceReportQos(deviceReportQos);
    }

    /**
     * Gets the MQTT QoS level for subscribing to shadow updates. The default
     * QoS is QoS 0.
     *
     * @return the shadow update QoS
     */
    @Override
    public AZEROIotQos getShadowUpdateQos() {
        return super.getShadowUpdateQos();
    }

    /**
     * Sets the MQTT QoS level for subscribing to shadow updates. This value
     * must be set before the device is attached to a client via the
     * {@link AZEROIotMqttClient#attach(AZEROIotDevice)} call.
     *
     * @param shadowUpdateQos
     *            the new shadow update QoS
     */
    @Override
    public void setShadowUpdateQos(AZEROIotQos shadowUpdateQos) {
        super.setShadowUpdateQos(shadowUpdateQos);
    }

    /**
     * Gets the MQTT QoS level for sending the shadow methods, namely Get,
     * Update, and Delete. The default QoS is QoS 0.
     *
     * @return the QoS level for sending shadow methods.
     */
    @Override
    public AZEROIotQos getMethodQos() {
        return super.getMethodQos();
    }

    /**
     * Sets the MQTT QoS level for sending shadow methods. This value must be
     * set before the device is attached to a client via the
     * {@link AZEROIotMqttClient#attach(AZEROIotDevice)} call.
     *
     * @param methodQos
     *            the new QoS level for sending shadow methods.
     */
    @Override
    public void setMethodQos(AZEROIotQos methodQos) {
        super.setMethodQos(methodQos);
    }

    /**
     * Gets the MQTT QoS level for subscribing to acknowledgement messages of
     * shadow methods. The default QoS is QoS 0.
     *
     * @return the QoS level for subscribing to acknowledgement messages.
     */
    @Override
    public AZEROIotQos getMethodAckQos() {
        return super.getMethodAckQos();
    }

    /**
     * Sets the MQTT QoS level for subscribing to acknowledgement messages of
     * shadow methods. This value must be set before the device is attached to a
     * client via the {@link AZEROIotMqttClient#attach(AZEROIotDevice)} call.
     *
     * @param methodAckQos
     *            the new QoS level for subscribing to acknowledgement messages.
     */
    @Override
    public void setMethodAckQos(AZEROIotQos methodAckQos) {
        super.setMethodAckQos(methodAckQos);
    }

    /**
     * Retrieves the latest state stored in the thing shadow. This method
     * returns the full JSON document, including meta data. This is a blocking
     * call, so the calling thread will be blocked until the operation succeeded
     * or failed.
     * <p>
     * Note: Blocking API call without specifying a timeout, in very rare cases,
     * can block the calling thread indefinitely, if the server response is not
     * received or lost. Use the alternative APIs with timeout for applications
     * that expect responses within fixed duration.
     * </p>
     *
     * @return the JSON document of the device state
     * @throws AZEROIotException
     *             exception thrown if the operation fails
     */
    @Override
    public String get() throws AZEROIotException {
        return super.get();
    }

    /**
     * Retrieves the latest state stored in the thing shadow. This method
     * returns the full JSON document, including meta data. This is a blocking
     * call, so the calling thread will be blocked until the operation
     * succeeded, failed, or timed out.
     *
     * @param timeout
     *            the timeout in milliseconds that the calling thread will wait
     * @return the JSON document of the device state
     * @throws AZEROIotException
     *             exception thrown if the operation fails
     * @throws AZEROIotTimeoutException
     *             exception thrown if the operation times out
     */
    @Override
    public String get(long timeout) throws AZEROIotException, AZEROIotTimeoutException {
        return super.get(timeout);
    }

    /**
     * Retrieves the latest state stored in the thing shadow. This method
     * returns the full JSON document, including meta data. This is a
     * non-blocking call, so it immediately returns once is the operation has
     * been queued in the system. The result of the operation will be notified
     * through the callback functions, namely {@link AZEROIotMessage#onSuccess},
     * {@link AZEROIotMessage#onFailure}, and {@link AZEROIotMessage#onTimeout}, one
     * of which will be invoked after the operation succeeded, failed, or timed
     * out respectively.
     * 
     * @param message
     *            the message object contains callback functions; if the call is
     *            successful, the full JSON document of the device state will be
     *            stored in the {@code payload} field of {@code message}.
     * @param timeout
     *            the timeout in milliseconds for the operation to be considered
     *            timed out
     * @throws AZEROIotException
     *             exception thrown if the operation fails
     */
    @Override
    public void get(AZEROIotMessage message, long timeout) throws AZEROIotException {
        super.get(message, timeout);
    }

    /**
     * Updates the content of a thing shadow with the data provided in the
     * request. This is a blocking call, so the calling thread will be blocked
     * until the operation succeeded or failed.
     * <p>
     * Note: Blocking API call without specifying a timeout, in very rare cases,
     * can block the calling thread indefinitely, if the server response is not
     * received or lost. Use the alternative APIs with timeout for applications
     * that expect responses within fixed duration.
     * </p>
     *
     * @param jsonState
     *            the JSON document of the new device state
     * @throws AZEROIotException
     *             exception thrown if the operation fails
     */
    @Override
    public void update(String jsonState) throws AZEROIotException {
        super.update(jsonState);
    }

    /**
     * Updates the content of a thing shadow with the data provided in the
     * request. This is a blocking call, so the calling thread will be blocked
     * until the operation succeeded, failed, or timed out.
     *
     * @param jsonState
     *            the JSON document of the new device state
     * @param timeout
     *            the timeout in milliseconds that the calling thread will wait
     * @throws AZEROIotException
     *             exception thrown if the operation fails
     * @throws AZEROIotTimeoutException
     *             exception thrown if the operation times out
     */
    @Override
    public void update(String jsonState, long timeout) throws AZEROIotException, AZEROIotTimeoutException {
        super.update(jsonState, timeout);
    }

    /**
     * Updates the content of a thing shadow with the data provided in the
     * request. This is a non-blocking call, so it immediately returns once is
     * the operation has been queued in the system. The result of the operation
     * will be notified through the callback functions, namely
     * {@link AZEROIotMessage#onSuccess}, {@link AZEROIotMessage#onFailure}, and
     * {@link AZEROIotMessage#onTimeout}, one of which will be invoked after the
     * operation succeeded, failed, or timed out respectively.
     *
     * @param message
     *            the message object contains callback functions
     * @param timeout
     *            the timeout in milliseconds for the operation to be considered
     *            timed out
     * @throws AZEROIotException
     *             exception thrown if the operation fails
     */
    @Override
    public void update(AZEROIotMessage message, long timeout) throws AZEROIotException {
        super.update(message, timeout);
    }

    /**
     * Deletes the content of a thing shadow. This is a blocking call, so the
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
    public void delete() throws AZEROIotException {
        super.delete();
    }

    /**
     * Deletes the content of a thing shadow. This is a blocking call, so the
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
    public void delete(long timeout) throws AZEROIotException, AZEROIotTimeoutException {
        super.delete(timeout);
    }

    /**
     * Deletes the content of a thing shadow. This is a non-blocking call, so it
     * immediately returns once is the operation has been queued in the system.
     * The result of the operation will be notified through the callback
     * functions, namely {@link AZEROIotMessage#onSuccess},
     * {@link AZEROIotMessage#onFailure}, and {@link AZEROIotMessage#onTimeout}, one
     * of which will be invoked after the operation succeeded, failed, or timed
     * out respectively.
     *
     * @param message
     *            the message object contains callback functions
     * @param timeout
     *            the timeout in milliseconds for the operation to be considered
     *            timed out
     * @throws AZEROIotException
     *             exception thrown if the operation fails
     */
    @Override
    public void delete(AZEROIotMessage message, long timeout) throws AZEROIotException {
        super.delete(message, timeout);
    }

    /**
     * This function handles update messages received from the shadow. By
     * default, it invokes the setter methods provided for the annotated device
     * attributes. When there are multiple attribute changes received in one
     * shadow update, the order of invoking the setter methods are not defined.
     * One can override this function to provide their own implementation for
     * updating the device. The shadow update containing the delta (between the
     * 'desired' state and the 'reported' state) is passed in as an input
     * argument.
     *
     * @param jsonState
     *            the JSON document containing the delta between 'desired' and
     *            'reported' states
     */
    @Override
    public void onShadowUpdate(String jsonState) {
        super.onShadowUpdate(jsonState);
    }

    /**
     * This function handles collecting device data for reporting to the shadow.
     * By default, it invokes the getter methods provided for the annotated
     * device attributes. The data is serialized in a JSON document and reported
     * to the shadow. One could override this default implementation and provide
     * their own JSON document for reporting.
     *
     * @return the JSON document containing 'reported' state
     */
    @Override
    public String onDeviceReport() {
        return super.onDeviceReport();
    }

}
