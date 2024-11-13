package com.yandex.tracker.servise;

import com.yandex.tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> browsingHistory;

    InMemoryHistoryManager() {
        browsingHistory = new ArrayList<>();
    }
    @Override
    public List<Task> getHistory() {
        return browsingHistory;
    }
    @Override
    public void add(Task task) {
        if (browsingHistory.size() == 10) {
            browsingHistory.removeFirst();
        }
        browsingHistory.add(task);
    }

}
