package com.yandex.tracker.servise;

import com.yandex.tracker.model.Node;
import com.yandex.tracker.model.Task;

import java.util.List;

public interface HistoryManager  {

    List<Task> getHistory();

    void removeNode(int id);

    void add(Task task);

}
