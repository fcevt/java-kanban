package com.yandex.tracker.servise;


import com.yandex.tracker.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class InMemoryHistoryManagerTest {

// тест на то что история задач не сохраняет повторы.
    @Test
    void historyHasNoRepetitionsTest() {
        TaskManager taskManager = Managers.getDefault();
        Task task = taskManager.createTask(new Task("a", "a"));
        Task task1 = taskManager.createTask(new Task("a", "a"));
        Task task2 = taskManager.createTask(new Task("a", "a"));
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task2.getId());
        Assertions.assertEquals(3, taskManager.getHistory().size());
    }

    // тест на то что в истории сохраняется порядок вызова.
    @Test
    void savingTheOrderOfCallingTasksInManagerTest() {
        TaskManager taskManager = Managers.getDefault();
        Task task = taskManager.createTask(new Task("a", "a"));
        Task task1 = taskManager.createTask(new Task("a", "a"));
        Task task2 = taskManager.createTask(new Task("a", "a"));
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        Assertions.assertEquals(task, taskManager.getHistory().get(0));
        Assertions.assertEquals(task1, taskManager.getHistory().get(1));
        Assertions.assertEquals(task2, taskManager.getHistory().get(2));
    }

    @Test
    void repeatedCallsMovedToTheEnd() {
        TaskManager taskManager = Managers.getDefault();
        Task task = taskManager.createTask(new Task("a", "a"));
        Task task1 = taskManager.createTask(new Task("a", "a"));
        Task task2 = taskManager.createTask(new Task("a", "a"));
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        Assertions.assertEquals(task2, taskManager.getHistory().get(2));
        taskManager.getTaskById(task1.getId());
        Assertions.assertEquals(task1, taskManager.getHistory().get(2));
        taskManager.getTaskById(task.getId());
        Assertions.assertEquals(task, taskManager.getHistory().get(2));
        taskManager.getTaskById(task2.getId());
        Assertions.assertEquals(task2, taskManager.getHistory().get(2));
    }

}