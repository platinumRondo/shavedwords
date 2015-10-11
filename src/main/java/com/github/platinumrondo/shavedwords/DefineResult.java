package com.github.platinumrondo.shavedwords;

/**
 * Helper class that keep everything of a single definition.
 */
public class DefineResult {
    private final String word;
    private final String dbName;
    private final String dbDescription;
    private final String definition;

    public DefineResult(String word, String dbname, String dbdescr,
                        String definition) {
        this.word = word;
        this.dbName = dbname;
        this.dbDescription = dbdescr;
        this.definition = definition;
    }

    public String getWord() {
        return word;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbDescription() {
        return dbDescription;
    }

    public String getDefinition() {
        return definition;
    }
}
