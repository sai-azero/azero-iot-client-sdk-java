package com.azero.services.iot.client.core;

/**
 * This exception class is used internally in the library to track retryable
 * events.
 */
public class AzeroIotRetryableException extends Exception {

    private static final long serialVersionUID = 1L;

    public AzeroIotRetryableException(String message) {
        super(message);
    }

    public AzeroIotRetryableException(Throwable e) {
        super(e);
    }

}
