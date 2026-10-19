package com.example.moderncalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton store so MainActivity and HistoryActivity share the same history list.
 */
public class HistoryStore {

    private static HistoryStore instance;
    private final List<CalculatorEngine.HistoryEntry> history = new ArrayList<>();

    private HistoryStore() {}

    public static HistoryStore getInstance() {
        if (instance == null) instance = new HistoryStore();
        return instance;
    }

    public List<CalculatorEngine.HistoryEntry> getHistory() { return history; }

    public void add(CalculatorEngine.HistoryEntry entry) {
        history.add(0, entry);
        if (history.size() > 50) history.remove(history.size() - 1);
    }

    public void clear() { history.clear(); }
}
