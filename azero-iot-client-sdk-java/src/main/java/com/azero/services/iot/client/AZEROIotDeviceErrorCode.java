package com.azero.services.iot.client;

/**
 * These error codes are used by the server in acknowledgement message for the
 * shadow methods, namely Get, Update, and Delete.
 */
public enum AZEROIotDeviceErrorCode {

    /** The bad request. */
    BAD_REQUEST(400),
    /** The Unauthorized. */
    UNAUTHORIZED(401),
    /** The Forbidden. */
    FORBIDDEN(403),
    /** The Not found. */
    NOT_FOUND(404),
    /** The Conflict. */
    CONFLICT(409),
    /** The Payload too large. */
    PAYLOAD_TOO_LARGE(413),
    /** The Unsupported media type. */
    UNSUPPORTED_MEDIA_TYPE(415),
    /** The Too many requests. */
    TOO_MANY_REQUESTS(429),
    /** The Internal service failure. */
    INTERNAL_SERVICE_FAILURE(429);

    /** The error code. */
    private final long errorCode;

    /**
     * Instantiates a new device error code object.
     *
     * @param errorCode
     *            the error code
     */
    private AZEROIotDeviceErrorCode(final long errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Gets the error code value.
     *
     * @return the error code value
     */
    public long getValue() {
        return this.errorCode;
    }

    /**
     * Returns the Enum representation of the error code value
     *
     * @param code
     *            the error code value
     * @return the Enum representation of the error code, or null if the error
     *         code is unknown
     */
    public static AZEROIotDeviceErrorCode valueOf(long code) {
        for (AZEROIotDeviceErrorCode errorCode : AZEROIotDeviceErrorCode.values()) {
            if (errorCode.errorCode == code) {
                return errorCode;
            }
        }

        return null;
    }

}
