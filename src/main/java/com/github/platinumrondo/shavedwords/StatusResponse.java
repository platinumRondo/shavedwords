package com.github.platinumrondo.shavedwords;

/**
 * Helper class that split the response code from whatever follow that.
 */
public class StatusResponse {
    private final int code;
    private final String message;

    public StatusResponse(String str) {
        code = Integer.decode(str.substring(0, 3));
        if (str.length() > 3)
            message = str.substring(4);
        else
            message = "";
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.valueOf(code) + " " + message;
    }
}
