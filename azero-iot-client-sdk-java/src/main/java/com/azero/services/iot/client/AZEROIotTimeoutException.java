package com.azero.services.iot.client;

/**
 * This timeout exception can be thrown by the blocking APIs in this library
 * when expected time has elapsed.
 */
public class AZEROIotTimeoutException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new exception object.
     *
     * @param message
     *            the error message
     */
    public AZEROIotTimeoutException(String message) {
        super(message);
    }

    /**
     * Instantiates a new exception object.
     *
     * @param cause
     *            the cause. A null value is permitted, and indicates that the
     *            cause is nonexistent or unknown.
     */
    public AZEROIotTimeoutException(Throwable cause) {
        super(cause);
    }

}
