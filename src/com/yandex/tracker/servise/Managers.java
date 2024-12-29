package com.yandex.tracker.servise;

import java.io.File;

public class Managers {

    private Managers() {
    }

    public static FileBackedTaskManager getDefault() {
        return new FileBackedTaskManager(new File(System.getProperty("user.dir"), "tasksFile.txt"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
