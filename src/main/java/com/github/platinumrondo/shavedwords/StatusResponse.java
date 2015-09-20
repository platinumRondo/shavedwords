package com.github.platinumrondo.shavedwords;

/**
 * Created by rondo on 20/09/15.
 */
public class StatusResponse {
    private final int code;
    private final String message;

    public StatusResponse(String str) {
        code = Integer.decode(str.substring(0, 3));
        message = str.substring(4);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(code);
        sb.append(" ");
        sb.append(message);
        return sb.toString();
    }
}
