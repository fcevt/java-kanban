package com.yandex.tracker.servise;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class InMemoryTaskManagerTest {
    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    //тесты на то что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    @Test
    void addNewTaskAndFindItTest() {
        Task task = taskManager.createTask(new Task("a", "a"));
        Assertions.assertNotNull(taskManager.getListOfTasks());
        Assertions.assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
    void addNewEpicAndFindItTest() {
        Epic epic = taskManager.createEpic(new Epic("a", "a"));
        Assertions.assertNotNull(taskManager.getListOfEpics());
        Assertions.assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void addNewSubtaskAndFindItTest() {
        Epic epic = taskManager.createEpic(new Epic("a", "a"));
        Subtask subtask = taskManager.createSubtask(new Subtask("a", "a"), epic.getId());
        Assertions.assertNotNull(taskManager.getListOfSubtasks());
        Assertions.assertEquals(subtask, taskManager.getSubtasksById(subtask.getId()));
    }

   //тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    void immutabilityOfTheTaskWhenAddedToManager() {
        Task task = taskManager.createTask(new Task("a", "a"));
        Task task1 = taskManager.getTaskById(task.getId());
        Assertions.assertEquals(task.getId(), task1.getId());
        Assertions.assertEquals(task.getDescription(),task1.getDescription());
        Assertions.assertEquals(task.getName(), task1.getName());
        Assertions.assertEquals(task.getStatus(), task1.getStatus());
    }

    //тест на то что из эпика удаляются id удаленных подзадач
    @Test
    void thereAreNoDeletedSubtasksInEpicTest() {
        Epic epic = taskManager.createEpic(new Epic("a","a"));
        Subtask subtask = taskManager.createSubtask(new Subtask("b", "B"), epic.getId());
        taskManager.deleteSubtaskById(subtask.getId());
        Assertions.assertEquals(0, epic.getListOfSubtasks().size());
    }
}