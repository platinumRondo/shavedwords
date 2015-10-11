package com.github.platinumrondo.shavedwords;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class that split the response code from whatever follow that.
 */
public class StatusResponse {
    private final int code;
    private final String message;

    /**
     * Constructor.
     *
     * @param str the entire string provided by the server.
     */
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

    /**
     * The message following the may include one or more params.
     * This function simply split the message in its parts and return the one
     * requested. It throws IndexOutOfBoundsException if the param does not
     * exist.
     *
     * @param pos Starting with 0, the position of the param to retrieve.
     * @return the parameter requested.
     */
    public String getParam(int pos) {
        List<String> paramList = new ArrayList<>();
        String tmp = message.trim();
        while (tmp.length() > 0) {
            if (tmp.charAt(0) == '"') {
                int quote = tmp.indexOf('"', 1);
                paramList.add(tmp.substring(1, quote));
                tmp = tmp.substring(quote + 1).trim();
            } else {
                String[] tmpsplit = tmp.split(" ", 2);
                paramList.add(tmpsplit[0]);
                tmp = tmpsplit[1].trim();
            }
        }
        return paramList.get(pos);
    }

    @Override
    public String toString() {
        return String.valueOf(code) + " " + message;
    }
}
