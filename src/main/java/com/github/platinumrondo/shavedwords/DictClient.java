package com.github.platinumrondo.shavedwords;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the dict protocol, client side.
 * The SASLAUTH command is missing.
 * Except for the connect() method, every method match a protocol command.
 */
public class DictClient {

    private final String serverName;
    private final int serverPort;

    private Socket serverSocket;
    private BufferedReader serverIn;
    private BufferedWriter serverOut;

    public DictClient(String serverName, int port) {
        if (serverName == null || serverName.compareTo("") == 0)
            throw new IllegalArgumentException();
        if (port < 0 || port > 65535)
            throw new IndexOutOfBoundsException();
        this.serverName = serverName;
        this.serverPort = port;
    }

    public void connect() throws IOException {
        serverSocket = new Socket(serverName, serverPort);
        serverIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream(), StandardCharsets.UTF_8));
        serverOut = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream(), StandardCharsets.UTF_8));
        System.out.println(readStatusResponse());
    }

    public String[] define(String database, String word) throws IOException {
        if (database == null || word == null)
            throw new IllegalArgumentException();
        sendCommand("define", database, word);
        StatusResponse result = readStatusResponse();
        if (result.getCode() == 150) {
            return getDefinitions();
        } else if (result.getCode() == 552) {
            return new String[0];
        }
        //550: nonexistent db
        throw new DictException(result);
    }

    private String[] getDefinitions() throws IOException {
        List<String> l = new ArrayList<>();
        StatusResponse result = readStatusResponse();
        while (result.getCode() != 250) {
            //TODO get from status the db in use
            l.add(readTextualResponse());
            result = readStatusResponse();
        }
        return l.toArray(new String[l.size()]);
    }

    public String match(String database, String strategy, String word) throws IOException {
        sendCommand("match", database, strategy, word);
        StatusResponse result = readStatusResponse();
        if (result.getCode() == 152) {
            String matchedWords = readTextualResponse();
            readStatusResponse();
            return matchedWords;
        } else if (result.getCode() == 552) {
            return "";
        }
        //
        //550: wrong db
        //552: wrong strategy
        throw new DictException(result);
    }

    public String showDatabases() throws IOException {
        //TODO test if connected
        //TODO find better way to return dict with its own description
        sendCommand("show db");
        StatusResponse result = readStatusResponse();
        if (result.getCode() == 554)
            return "";
        if (result.getCode() == 110) {
            String dbs = readTextualResponse();
            readStatusResponse();
            return dbs;
        }
        throw new DictException(result);
    }

    public String showStrategies() throws IOException {
        //TODO find better way to return this
        sendCommand("show strat");
        StatusResponse result = readStatusResponse();
        if (result.getCode() == 111) {
            //return: name description
            String strats = readTextualResponse();
            readStatusResponse();
            return strats;
        }
        throw new DictException(result);
    }

    public String showInfo(String database) throws IOException {
        if (database == null || database.trim().compareTo("") == 0)
            throw new IllegalArgumentException();
        sendCommand("show info", database);
        StatusResponse result = readStatusResponse();
        if (result.getCode() == 112) {
            String info = readTextualResponse();
            readStatusResponse();
            return info;
        }
        //TODO 550 no database found
        throw new DictException(result);
    }

    public String showServer() throws IOException {
        sendCommand("show server");
        StatusResponse result = readStatusResponse();
        if (result.getCode() == 110)
            return readTextualResponse();
        throw new DictException(result);
    }

    public void client(String text) throws IOException {
        if (text == null || text.trim().compareTo("") == 0)
            throw new IllegalArgumentException();
        sendCommand("client", text);
        StatusResponse result = readStatusResponse();
        if (result.getCode() != 250)
            throw new DictException(result);
    }

    public String status() throws IOException {
        sendCommand("status");
        StatusResponse result = readStatusResponse();
        return result.getMessage();
    }

    public String help() throws IOException {
        sendCommand("help");
        StatusResponse result = readStatusResponse();
        if (result.getCode() == 113) {
            String help = readTextualResponse();
            readStatusResponse();
            return help;
        }
        throw new DictException(result);
    }

    public void auth(String username, String authstring) throws IOException {
        sendCommand("auth", username, authstring);
        StatusResponse result = readStatusResponse();
        if (result.getCode() == 230)
            return;
        throw new DictException(result);
    }

    public void quit() throws IOException {
        sendCommand("quit");
        StatusResponse result = readStatusResponse();
        System.out.println(result);
        serverSocket.close();
    }

    private void sendCommand(String cmd, String... params) throws IOException {
        StringBuilder buffer = new StringBuilder();
        buffer.append(cmd);
        for (String param : params) {
            buffer.append(" ");
            buffer.append(param);
        }
        buffer.append((char) 13);
        buffer.append((char) 10);
        serverOut.write(buffer.toString(), 0, buffer.length());
        serverOut.flush();
    }

    private StatusResponse readStatusResponse() throws IOException {
        return new StatusResponse(serverIn.readLine());
    }

    private String readTextualResponse() throws IOException {
        StringBuilder sb = new StringBuilder();
        String txt = serverIn.readLine();
        while (txt.compareTo(".") != 0) {
            txt = normalizeIncomingTextLine(txt);
            sb.append(txt);
            sb.append((char) 10);
            txt = serverIn.readLine();
        }
        return sb.toString();
    }

    private String normalizeIncomingTextLine(String str) {
        if (str.startsWith(".."))
            str = str.substring(1);
        return str;
    }
}
