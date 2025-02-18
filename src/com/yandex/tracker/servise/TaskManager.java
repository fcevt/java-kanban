package com.yandex.tracker.servise;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    Optional<Task> createTask(Task task);

    Optional<Epic> createEpic(Epic epic);

    Optional<Subtask> createSubtask(Subtask subtask, int epicId);

    //обновление данных
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    // получение истории просмотров
    List<Task> getHistory();

    //получение сортированного по времени списка задач
    List<Task> getPrioritizedTasks();

    // удаление по id
    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);
}