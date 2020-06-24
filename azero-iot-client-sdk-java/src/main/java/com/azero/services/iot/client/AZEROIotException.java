package com.azero.services.iot.client;

/**
 * This is a generic exception that can be thrown in most of the APIs, blocking
 * and non-blocking, by the library.
 */
public class AZEROIotException extends Exception {
    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Error code for shadow methods. It's only applicable to exceptions thrown
     * by those shadow method APIs.
     */
    private AZEROIotDeviceErrorCode errorCode;

    /**
     * Instantiates a new exception object.
     *
     * @param message
     *            the error message
     */
    public AZEROIotException(String message) {
        super(message);
    }

    /**
     * Instantiates a new exception object.
     *
     * @param errorCode
     *            the error code
     * @param message
     *            the error message
     */
    public AZEROIotException(AZEROIotDeviceErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Instantiates a new exception object.
     *
     * @param cause
     *            the cause. A null value is permitted, and indicates that the
     *            cause is nonexistent or unknown.
     */
    public AZEROIotException(Throwable cause) {
        super(cause);
    }

    /**
     * Error code for shadow methods. It's only applicable to exceptions thrown
     * by those shadow method APIs.
     *
     * @return the error code of the shadow method exception
     */
    @java.lang.SuppressWarnings("all")
    public AZEROIotDeviceErrorCode getErrorCode() {
        return this.errorCode;
    }

    /**
     * Error code for shadow methods. It's only applicable to exceptions thrown
     * by those shadow method APIs.
     *
     * @param errorCode the new error code for the shadow method exception
     */
    @java.lang.SuppressWarnings("all")
    public void setErrorCode(final AZEROIotDeviceErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
