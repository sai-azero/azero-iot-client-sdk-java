package com.azero.services.iot.client;

/**
 * QoS definitions. The AZERO IoT service supports QoS0 and QoS1 defined by the
 * MQTT protocol.
 */
public enum AZEROIotQos {

    /** The QoS0. */
    QOS0(0),

    /** The QoS1. */
    QOS1(1);

    /** The qos. */
    private final int qos;

    /**
     * Instantiates a QoS object.
     *
     * @param qos
     *            the QoS level
     */
    private AZEROIotQos(final int qos) {
        this.qos = qos;
    }

    /**
     * Gets the integer representation of the QoS
     *
     * @return the integer value of the QoS
     */
    public int getValue() {
        return this.qos;
    }

    /**
     * Gets the Enum representation of the QoS
     *
     * @param qos
     *            the integer value of the QoS
     * @return the Enum value of the QoS
     */
    public static AZEROIotQos valueOf(int qos) {
        if (qos == 0) {
            return QOS0;
        } else if (qos == 1) {
            return QOS1;
        } else {
            throw new IllegalArgumentException("QoS not supported");
        }
    }

}
