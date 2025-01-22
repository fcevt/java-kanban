package com.yandex.tracker.servise;


import com.yandex.tracker.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class InMemoryHistoryManagerTest {
    TaskManager taskManager;

    @BeforeEach
    void setManager() {
        taskManager = Managers.getDefault();
    }

    // тест на то что история задач не сохраняет повторы.
    @Test
    void historyHasNoRepetitionsTest() {
        Task task = taskManager.createTask(new Task("a", "a")).get();
        Task task1 = taskManager.createTask(new Task("a", "a")).get();
        Task task2 = taskManager.createTask(new Task("a", "a")).get();
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
        Task task = taskManager.createTask(new Task("a", "a")).get();
        Task task1 = taskManager.createTask(new Task("a", "a")).get();
        Task task2 = taskManager.createTask(new Task("a", "a")).get();
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        Assertions.assertEquals(task, taskManager.getHistory().get(0));
        Assertions.assertEquals(task1, taskManager.getHistory().get(1));
        Assertions.assertEquals(task2, taskManager.getHistory().get(2));
    }

    //тест на то что повторный вызов перемещает задачу в конец истории
    @Test
    void repeatedCallsMovedToTheEnd() {
        Task task = taskManager.createTask(new Task("a", "a")).get();
        Task task1 = taskManager.createTask(new Task("a", "a")).get();
        Task task2 = taskManager.createTask(new Task("a", "a")).get();
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

    //тест на удаление из истории по id из середины конца и начала
    @Test
    void deleteFromHistoryTest() {
        InMemoryTaskManager taskManager1 = (InMemoryTaskManager) taskManager;
        Task task = taskManager1.createTask(new Task("a", "a")).get();
        Task task1 = taskManager1.createTask(new Task("a", "a")).get();
        Task task2 = taskManager1.createTask(new Task("a", "a")).get();
        taskManager1.getTaskById(task.getId());
        taskManager1.getTaskById(task1.getId());
        taskManager1.getTaskById(task2.getId());
        Assertions.assertEquals(3, taskManager1.getHistory().size());
        taskManager1.deleteTaskById(task1.getId());                             //удаление из середины
        Assertions.assertEquals(2, taskManager1.getHistory().size());
        Assertions.assertEquals(task, taskManager1.getHistory().getFirst());
        Assertions.assertEquals(task2, taskManager1.getHistory().getLast());
        taskManager1.deleteTaskById(task2.getId());                            //удаление из конца истории
        Assertions.assertEquals(1, taskManager1.getHistory().size());
        Assertions.assertEquals(task, taskManager1.getHistory().getFirst());
        taskManager1.deleteTaskById(task.getId());                             //удаление последней записи
        Assertions.assertEquals(0, taskManager1.getHistory().size());
    }

}