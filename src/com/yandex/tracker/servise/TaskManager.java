package com.yandex.tracker.servise;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;
import com.yandex.tracker.model.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    // получение списков задач
    ArrayList<Task> getListOfTasks();

    ArrayList<Epic> getListOfEpics();

    ArrayList<Subtask> getListOfSubtasks();

    // получение списка подзадач определенного эпика
    List<Subtask> getListOfEpicSubtask(int epicId);

    // удаление всех задач списка
    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    // получение задач по id
    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtasksById(int id);

    // создание задач
    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask, int epicId);

    //обновление данных
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    // удаление по id
    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    void updateEpicStatus(int epicId, TaskStatus newStatus);

    void updateEpicStatus(int epicId);

    // получение истории просмотров
    List<Task> getHistory();
}