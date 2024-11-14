package com.yandex.tracker.servise;

import com.yandex.tracker.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_BROWSING_HISTORY_SIZE = 10;
    private final List<Task> browsingHistory;

    public InMemoryHistoryManager() {
        browsingHistory = new LinkedList<>();
    }
    @Override
    public List<Task> getHistory() {
        return List.copyOf(browsingHistory);
    }
    @Override
    public void add(Task task) {
        if (task != null) {
            if (browsingHistory.size() == MAX_BROWSING_HISTORY_SIZE) {
                browsingHistory.removeFirst();
            }
            browsingHistory.add(task);
        }
    }

}
