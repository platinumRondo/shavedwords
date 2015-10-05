package com.github.platinumrondo.shavedwords;

/**
 * Helper class for the match query.
 */
public class MatchResult {
    private final String word;
    private final String database;

    public MatchResult(String word, String database) {
        this.word = word;
        this.database = database;
    }

    public String getWord() {
        return word;
    }

    public String getDatabase() {
        return database;
    }
}
