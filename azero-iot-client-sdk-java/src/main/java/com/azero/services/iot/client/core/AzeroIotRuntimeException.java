package com.azero.services.iot.client.core;

/**
 * This exception class is used internally in the library for runtime errors.
 */
public class AzeroIotRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AzeroIotRuntimeException(Throwable e) {
        super(e);
    }

    public AzeroIotRuntimeException(String message) {
        super(message);
    }

}
