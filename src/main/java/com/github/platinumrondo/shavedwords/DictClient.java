package com.github.platinumrondo.shavedwords;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the dict protocol, client side. For more information see http://www.dict.org .
 * This implementation isn't thread-safe, it must the synchronized externally.
 * The SASLAUTH command is missing.
 * Except for the connect() method, every method match a protocol command.
 * If the server return an error, a DictException is raised.
 * If there's a connection problem, a IOException is raised.
 * @author platinum rondo
 */
public class DictClient {

    private final String serverName;
    private final int serverPort;

    private Socket serverSocket;
    private BufferedReader serverIn;
    private BufferedWriter serverOut;

    public DictClient(String serverName, int port) {
        this.serverName = serverName;
        this.serverPort = port;
    }

    /**
     * Connect to the server specified in the constructor.
     * Throw an IllegalStateException if we are already connected.
     * Throw a DictException if the server refuse the connection.
     * @throws IOException something happened with the connection.
     */
    public void connect() throws IOException {
        if (isConnected())
            throw new IllegalStateException();
        serverSocket = new Socket(serverName, serverPort);
        serverIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream(), StandardCharsets.UTF_8));
        serverOut = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream(), StandardCharsets.UTF_8));
        StatusResponse status = readStatusResponse();
        System.out.println(status);
        if (status.getCode() != 220) {
            serverSocket.close();
            throw new DictException(status);
        }
    }

    /**
     * Check if we are connected.
     * @return connection status.
     */
    public boolean isConnected() {
        return serverSocket != null && serverSocket.isConnected() && !serverSocket.isClosed();
    }

    private void checkAndConnect() throws IOException {
        if (!isConnected())
            connect();
    }

    /**
     * Ask the server for the definition of the word provided.
     * @param database the database in which the server should lookup.
     * @param word the word.
     * @return all the definitions returned; a empty array if no definition where found.
     * @throws IOException
     */
    public String[] define(String database, String word) throws IOException {
        if (database == null || word == null)
            throw new IllegalArgumentException();
        checkAndConnect();
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
        checkAndConnect();
        List<String> l = new ArrayList<>();
        StatusResponse result = readStatusResponse();
        while (result.getCode() != 250) {
            //TODO get from status the db in use
            l.add(readTextualResponse());
            result = readStatusResponse();
        }
        return l.toArray(new String[l.size()]);
    }

    /**
     * Find similar words to the one provided.
     * @param database the database where to look for.
     * @param strategy the strategy to use for the search.
     * @param word the word (or part of it, or regex)
     * @return a list of possible words, or an empty string if nothing is found.
     * @throws IOException
     */
    public String match(String database, String strategy, String word) throws IOException {
        if (database == null || strategy == null || word == null)
            throw new IllegalArgumentException();
        checkAndConnect();
        sendCommand("match", database, strategy, word);
        StatusResponse result = readStatusResponse();
        if (result.getCode() == 152) {
            String matchedWords = readTextualResponse();
            readStatusResponse();
            //TODO this is shit! separate the words from the dictionary they're in and the other results.
            return matchedWords;
        } else if (result.getCode() == 552) {
            return "";
        }
        //
        //550: wrong db
        //552: wrong strategy
        throw new DictException(result);
    }

    /**
     * Gives you the dictionary the server will let you use. Some server may have dictionary available only to
     * logged in users.
     * @return map providing the db name in the key, and its description as the value.
     * @throws IOException
     */
    public Map<String, String> showDatabases() throws IOException {
        checkAndConnect();
        sendCommand("show db");
        StatusResponse result = readStatusResponse();
        if (result.getCode() == 554)
            return new HashMap<>();
        if (result.getCode() == 110) {
            String dbs = readTextualResponse();
            readStatusResponse();
            return parseStringToMap(dbs);
        }
        throw new DictException(result);
    }

    /**
     * Gives you all the strategies supported by the server, to be used with the MATCH command.
     * @return map providing the strategy in the key and its description in the value.
     * @throws IOException
     */
    public Map<String, String> showStrategies() throws IOException {
        checkAndConnect();
        sendCommand("show strat");
        StatusResponse result = readStatusResponse();
        if (result.getCode() == 111) {
            //return: name description
            String strats = readTextualResponse();
            readStatusResponse();
            return parseStringToMap(strats);
        }
        throw new DictException(result);
    }

    private Map<String, String> parseStringToMap(String str) {
        String[] lines = str.split("\n");
        Map<String, String> map = new HashMap<>();
        for (String line : lines) {
            String[] parts = line.split(" ", 2);
            map.put(parts[0], removeDoubleQuotes(parts[1]));
        }
        return map;
    }

    private String removeDoubleQuotes(String s) {
        if (s.charAt(0) == '"')
            s = s.substring(1);
        if (s.charAt(s.length() - 1) == '"')
            s = s.substring(0, s.length() - 1);
        return s;
    }

    /**
     * Retrieve the information for the chosen database.
     * @param database the chosen one.
     * @return the info.
     * @throws IOException
     */
    public String showInfo(String database) throws IOException {
        if (database == null || database.trim().compareTo("") == 0)
            throw new IllegalArgumentException();
        checkAndConnect();
        sendCommand("show info", database);
        StatusResponse result = readStatusResponse();
        if (result.getCode() == 112) {
            String info = readTextualResponse();
            readStatusResponse();
            return info;
        }
        throw new DictException(result);
    }

    /**
     * Retrieve information on the server itself.
     * @return info.
     * @throws IOException
     */
    public String showServer() throws IOException {
        checkAndConnect();
        sendCommand("show server");
        StatusResponse result = readStatusResponse();
        if (result.getCode() == 110)
            return readTextualResponse();
        throw new DictException(result);
    }

    /**
     * Send to the server some information about the client, usually the name and the version.
     * @param text the text to send for identification.
     * @throws IOException
     */
    public void client(String text) throws IOException {
        if (text == null || text.trim().compareTo("") == 0)
            throw new IllegalArgumentException();
        checkAndConnect();
        sendCommand("client", text);
        StatusResponse result = readStatusResponse();
        if (result.getCode() != 250)
            throw new DictException(result);
    }

    /**
     * Usually used for debugging purposes, ask the server for its status.
     * The answer is server dependent.
     * @return server status.
     * @throws IOException
     */
    public String status() throws IOException {
        checkAndConnect();
        sendCommand("status");
        StatusResponse result = readStatusResponse();
        return result.getMessage();
    }

    /**
     * Retrieve the help guide of the server.
     * @return halp.
     * @throws IOException
     */
    public String help() throws IOException {
        checkAndConnect();
        sendCommand("help");
        StatusResponse result = readStatusResponse();
        if (result.getCode() == 113) {
            String help = readTextualResponse();
            readStatusResponse();
            return help;
        }
        throw new DictException(result);
    }

    /**
     * Authenticate the user. May unlock new dictionaries.
     * If the user is rejected, a DictException is raised.
     * @param username the username
     * @param authstring an autentication string, a password.
     * @throws IOException
     */
    public void auth(String username, String authstring) throws IOException {
        checkAndConnect();
        sendCommand("auth", username, authstring);
        StatusResponse result = readStatusResponse();
        if (result.getCode() == 230)
            return;
        throw new DictException(result);
    }

    /**
     * Tell the server we're leaving.
     * @throws IOException
     */
    public void quit() throws IOException {
        checkAndConnect();
        sendCommand("quit");
        StatusResponse result = readStatusResponse();
        System.out.println(result);
        serverSocket.close();
    }

    private void sendCommand(String cmd, String... params) throws IOException {
        serverOut.write(cmd);
        for (String param : params) {
            serverOut.write(" ");
            serverOut.write(escapeString(param));
        }
        serverOut.write(13);
        serverOut.write(10);
        serverOut.flush();
    }

    private String escapeString(String str) {
        if (str.contains(" "))
            return '"' + str + '"';
        return str;
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
