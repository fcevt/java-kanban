package com.yandex.tracker.servise;
import com.yandex.tracker.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int id;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private final HistoryManager browsingHistory;

    public InMemoryTaskManager() {
        id = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        browsingHistory = Managers.getDefaultHistory();

    }

    public InMemoryTaskManager(int id, HashMap<Integer, Task> tasks, HashMap<Integer, Epic> epics,
                               HashMap<Integer, Subtask> subtasks) {
        this.id = id;
        this.tasks = new HashMap<>(tasks);
        this.epics = new HashMap<>(epics);
        this.subtasks = new HashMap<>(subtasks);
        browsingHistory = Managers.getDefaultHistory();

    }

    public int getId() {
        return id;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    // получение списков задач
    @Override
    public ArrayList<Task> getListOfTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    @Override
    public ArrayList<Epic> getListOfEpics() {
        return new ArrayList<>(this.epics.values());
    }

    @Override
    public ArrayList<Subtask> getListOfSubtasks() {
        return new ArrayList<>(this.subtasks.values());
    }

    // получение списка подзадач определенного эпика
    @Override
    public ArrayList<Subtask> getListOfEpicSubtask(int epicId) {
        ArrayList<Subtask> listOfSubtask = new ArrayList<>();
        Epic epic = epics.get(epicId);
        for (Integer subtaskId : epic.getListOfSubtasks()) {
            listOfSubtask.add(subtasks.get(subtaskId));
        }
        return listOfSubtask;
    }

    // удаление всех задач списка
    @Override
    public void removeTasks() {
        for (Integer id : tasks.keySet()) {
            browsingHistory.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        for (Integer id : epics.keySet()) {
            browsingHistory.remove(id);
        }
        for (Integer id : subtasks.keySet()) {
            browsingHistory.remove(id);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        for (Integer id : subtasks.keySet()) {
            browsingHistory.remove(id);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeListOfSubtasks();
            epic.setStatus(TaskStatus.NEW);
            updateEpic(epic);
        }
    }

    // получение задач по id
    @Override
    public Task getTaskById(int id) {
        browsingHistory.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        browsingHistory.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtasksById(int id) {
        browsingHistory.add(subtasks.get(id));
        return subtasks.get(id);
    }

    // создание задач
    @Override
    public Task createTask(Task task) {
        id++;
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        id++;
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask, int epicId) {
        id++;
        subtask.setId(id);
        subtask.setEpicId(epicId);
        subtasks.put(id, subtask);
        Epic epic = epics.get(epicId);
        epic.addSubtaskToList(subtask.getId());
        updateEpic(epic);
        updateEpicStatus(epic.getId());
        return subtask;
    }

    //обновление данных
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());

    }

    // удаление по id
    @Override
    public void  deleteTaskById(int id) {
        tasks.remove(id);
        browsingHistory.remove(id);
    }

    @Override
    public void  deleteEpicById(int id) {
        final Epic epic = epics.remove(id);
        browsingHistory.remove(id);
        for (Integer subtaskId : epic.getListOfSubtasks()) {
            subtasks.remove(subtaskId);
            browsingHistory.remove(subtaskId);
        }
    }

    @Override
    public void  deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        subtasks.remove(id);
        browsingHistory.remove(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtaskFromList(id);
        updateEpicStatus(subtask.getEpicId());
    }

    //обновление статуса
    @Override
    public void updateEpicStatus(int epicId, TaskStatus newStatus) {
        Epic epic = epics.get(epicId);
        epic.setStatus(newStatus);
        epics.put(epicId, epic);
    }

    @Override
    public void updateEpicStatus(int epicId) {
        ArrayList<Subtask> list = getListOfEpicSubtask(epicId);
        if (list.isEmpty()) {
            updateEpicStatus(epicId, TaskStatus.NEW);
            return;
        }
        int numberDoneSubtasks = 0;
        int numberNewSubtasks = 0;
        for (Subtask task : list) {
            if (task.getStatus() == TaskStatus.DONE) {
                numberDoneSubtasks++;
            } else if (task.getStatus() == TaskStatus.NEW) {
                numberNewSubtasks++;
            }
        }
        if (list.size() == numberDoneSubtasks) {
            updateEpicStatus(epicId, TaskStatus.DONE);
        } else if (list.size() == numberNewSubtasks) {
            updateEpicStatus(epicId, TaskStatus.NEW);
        } else {
            updateEpicStatus(epicId, TaskStatus.IN_PROGRESS);
        }
    }

    //получение истории просмотров
    @Override
    public List<Task> getHistory() {
        return browsingHistory.getHistory();
    }
}
