package com.yandex.tracker.servise;


import com.yandex.tracker.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class InMemoryHistoryManagerTest {
// тест на то что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    void taskSavesPreviousVersionWhenAddedToHistoryTest() {
        TaskManager taskManager = Managers.getDefault();
        Task task = taskManager.createTask(new Task("a", "a"));
        Task task1 = taskManager.getTaskById(task.getId());
        Assertions.assertEquals(task1.getName(), taskManager.getHistory().get(0).getName());
        Task taskForUpdate = new Task("b", "b");
        taskForUpdate.setId(task.getId());
        taskManager.updateTask(taskForUpdate);
        Task task2 =  taskManager.getTaskById(taskForUpdate.getId());
        Assertions.assertEquals(task2.getName(),taskManager.getHistory().get(1).getName());
        Assertions.assertNotEquals(task2.getName(),taskManager.getHistory().get(0).getName());


    }

    // тест на то что размер истории просмотров не превышает 10 объектов и при заполнении удаляется наиболее старый.
    @Test
    void historyManagerSizeTest() {
        TaskManager taskManager = Managers.getDefault();
        Task task = taskManager.createTask(new Task("a", "a"));
        Task task1 = taskManager.createTask(new Task("a", "a"));
        Task task2 = taskManager.createTask(new Task("a", "a"));
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());

        Assertions.assertEquals(10, taskManager.getHistory().size());
        Assertions.assertEquals(task, taskManager.getHistory().get(0));
        Assertions.assertEquals(task1, taskManager.getHistory().get(9));
        taskManager.getTaskById(task2.getId());
        Assertions.assertEquals(10, taskManager.getHistory().size());
        Assertions.assertEquals(task1, taskManager.getHistory().get(0));
        Assertions.assertEquals(task2, taskManager.getHistory().get(9));

    }
}