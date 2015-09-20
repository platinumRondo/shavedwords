package com.github.platinumrondo.shavedwords;

/**
 * This exception will be raised whenever the server report a problem with the
 * command sent or cannot handle our requests for whatever reason.
 */
public class DictException extends RuntimeException {
    private final String serverMessage;

    public DictException(String result) {
        this.serverMessage = result;
    }

    @Override
    public String toString() {
        return serverMessage;
    }
}
