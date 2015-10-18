package com.github.platinumrondo.shavedwords.storage;

import com.github.platinumrondo.shavedwords.DictClient;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Facility to save and retrieve databases and strategies.
 *
 * @author Platinum Rondo
 */
public final class Storage {
    private final static String DATABASE_NODE = "databases";
    private final static String STRATEGY_NODE = "strategies";

    private Storage() {
    }

    public static void saveDatabases(String server, Map<String, String> dbs) {
        saveKeys(server, DATABASE_NODE, dbs);
    }

    public static void saveStrategies(String server, Map<String, String> strs) {
        saveKeys(server, STRATEGY_NODE, strs);
    }

    private static void saveKeys(String server, String node,
                                 Map<String, String> content) {
        Preferences root = Preferences.userNodeForPackage(DictClient.class);
        Preferences dictNode = root.node(server);
        Preferences dbNode = dictNode.node(node);
        for (String key : content.keySet()) {
            dbNode.put(key, content.get(key));
        }
    }

    public static Map<String, String> retrieveDatabases(String server)
            throws BackingStoreException {
        try {
            return extractKeys(server, DATABASE_NODE);
        } catch (BackingStoreException e) {
            System.err.format("There was an error retrieving db keys for %s\n",
                    server);
        }
        return new HashMap<>();
    }

    public static Map<String, String> retrieveStrategies(String server)
            throws BackingStoreException {
        try {
            return extractKeys(server, STRATEGY_NODE);
        } catch (BackingStoreException e) {
            System.err.format("There was an error retrieving strategies keys " +
                    "for %s\n", server);
        }
        return new HashMap<>();
    }

    private static Map<String, String> extractKeys(String server, String node)
            throws BackingStoreException {
        Preferences root = Preferences.userNodeForPackage(DictClient.class);
        Preferences dictNode = root.node(server);
        Preferences dbNode = dictNode.node(node);
        Map<String, String> dbsMap = new HashMap<>();
        for (String key : dbNode.keys()) {
            dbsMap.put(key, dbNode.get(key, "*"));
        }
        return dbsMap;
    }

}
