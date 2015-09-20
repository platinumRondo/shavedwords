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
            throw new IllegalArgumentException();
        this.serverName = serverName;
        this.serverPort = port;
    }

    public void connect() throws IOException {
        serverSocket = new Socket(serverName, serverPort);
        serverIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream(), StandardCharsets.UTF_8));
        serverOut = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream(), StandardCharsets.UTF_8));
        System.out.println(serverIn.readLine());
    }

    public String[] define(String database, String word) throws IOException {
        sendCommand("define", database, word);
        String result = readStatusResponse();
        if (result.startsWith("150")) {
            return getDefinitions();
        } else if (result.startsWith("552")){
            return new String[0];
        }
        //550: nonexistent db
        throw new DictException(result);
    }

    private String[] getDefinitions() throws IOException {
        List<String> l = new ArrayList<>();
        String status = readStatusResponse();
        while (! status.startsWith("250")) {
            //TODO get from status the db in use
            l.add(readTextualResponse());
            status = readStatusResponse();
        }
        return l.toArray(new String[l.size()]);
    }

    public String match(String database, String strategy, String word) throws IOException {
        sendCommand("match", database, strategy, word);
        String result = readStatusResponse();
        if (result.startsWith("152")) {
            String matchedWords = readTextualResponse();
            readStatusResponse();
            return matchedWords;
        } else if (result.startsWith("552")) {
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
        String result = readStatusResponse();
        if (result.startsWith("554"))
            return "";
        if (result.startsWith("110")) {
            String dbs = readTextualResponse();
            readStatusResponse();
            return dbs;
        }
        throw new DictException(result);
    }

    public String showStrategies() throws IOException {
        //TODO find better way to return this
        sendCommand("show strat");
        String in = readStatusResponse();
        if (in.startsWith("111")) {
            //return: name description
            String strats = readTextualResponse();
            readStatusResponse();
            return strats;
        }
        throw new DictException(in);
    }

    public String showInfo(String database) throws IOException {
        if (database == null || database.trim().compareTo("") == 0)
            throw new IllegalArgumentException();
        sendCommand("show info", database);
        String in = readStatusResponse();
        if (in.startsWith("112")) {
            String info = readTextualResponse();
            readStatusResponse();
            return info;
        }
        //TODO 550 no database found
        throw new DictException(in);
    }

    public String showServer() throws IOException {
        sendCommand("show server");
        String in = readStatusResponse();
        if (in.startsWith("110"))
            return readTextualResponse();
        throw new DictException(in);
    }

    public void client(String text) throws IOException {
        if (text == null || text.trim().compareTo("") == 0)
            throw new IllegalArgumentException();
        sendCommand("client", text);
        String in = readStatusResponse();
        if (! in.startsWith("250"))
            throw new DictException(in);
    }

    public String status() throws IOException {
        sendCommand("status");
        String in = readStatusResponse();
        return in.substring(4);
    }

    public String help() throws IOException {
        sendCommand("help");
        String in = readStatusResponse();
        if (in.startsWith("113")) {
            String help = readTextualResponse();
            readStatusResponse();
            return help;
        }
        throw new DictException(in);
    }

    public void auth(String username, String authstring) throws IOException {
        sendCommand("auth", username, authstring);
        String result = readStatusResponse();
        if (result.startsWith("230"))
            return;
        throw new DictException(result);
    }

    public void quit() throws IOException {
        sendCommand("quit");
        String in = readStatusResponse();
        System.out.println(in);
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

    private String readStatusResponse() throws IOException {
        return serverIn.readLine().trim();
    }

    private String readTextualResponse() throws IOException {
        //TODO bad code! too many tabs (or what they are called...)
        StringBuilder sb = new StringBuilder();
        String txt = serverIn.readLine();
        while (txt.trim().compareTo(".") != 0) {
            if (txt.startsWith(".."))
                sb.append(txt.substring(1));
            else
                sb.append(txt);
            sb.append((char) 10);
            txt = serverIn.readLine();
        }
        return sb.toString();
    }
}
