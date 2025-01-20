package com.yandex.tracker.servise;
import com.yandex.tracker.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.yandex.tracker.model.Task.DEFAULT_DURATION;
import static com.yandex.tracker.model.Task.DEFAULT_TIME;

public class InMemoryTaskManager implements TaskManager {
    private int id;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private final HistoryManager browsingHistory;
    private final Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        id = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        browsingHistory = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>();
    }

    public HistoryManager getBrowsingHistory() {
        return browsingHistory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public List<Subtask> getListOfEpicSubtask(int epicId) {
        return epics.get(epicId).getListOfSubtasks().stream()
                .map(subtasks::get)
                .sorted()
                .collect(Collectors.toList());
    }

    // удаление всех задач списка
    @Override
    public void removeTasks() {
        for (Integer id : tasks.keySet()) {
            browsingHistory.remove(id);
        }
        tasks.clear();
        deleteAllTasksFromPrioritizedTasks();
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
        deleteAllSubtasksFromPrioritizedTasks();
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
            updateEpicTime(epic.getId());
        }
        deleteAllSubtasksFromPrioritizedTasks();
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
        addToPrioritizedTasks(task);
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
        addToPrioritizedTasks(subtask);
        updateEpicTime(subtask.getEpicId());
        return subtask;
    }

    //обновление данных
    @Override
    public void updateTask(Task task) {
        updatePrioritizedTasks(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        updatePrioritizedTasks(subtask);
    }

    // удаление по id
    @Override
    public void deleteTaskById(int id) {
        browsingHistory.remove(id);
        deleteOneTaskFromPrioritizedTasks(tasks.remove(id));
    }

    @Override
    public void deleteEpicById(int id) {
        final Epic epic = epics.remove(id);
        browsingHistory.remove(id);
        for (Integer subtaskId : epic.getListOfSubtasks()) {
            subtasks.remove(subtaskId);
            browsingHistory.remove(subtaskId);
            deleteOneSubtaskFromPrioritizedTasks(subtaskId);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        browsingHistory.remove(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtaskFromList(id);
        updateEpicStatus(subtask.getEpicId());
        deleteOneSubtaskFromPrioritizedTasks(id);
        updateEpicTime(subtask.getEpicId());
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
        List<Subtask> list = getListOfEpicSubtask(epicId);
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

    // поиск пересечений задач по времени
    private boolean timeIntersectionSearch(Task task1, Task task2) {
        if (task1.getStartTime().isEqual(task2.getStartTime()) || task1.getEndTime().isEqual(task2.getEndTime())) {
            return true;
        } else if (task2.getStartTime().isAfter(task1.getStartTime()) &&
                task2.getStartTime().isBefore(task1.getEndTime())) {
            return true;
        } else if (task2.getEndTime().isAfter(task1.getStartTime()) &&
                task2.getEndTime().isBefore(task1.getEndTime())) {
            return true;
        } else if (task2.getStartTime().isBefore(task1.getStartTime()) &&
                task2.getEndTime().isAfter(task1.getEndTime())) {
            return true;
        }
        return false;
    }

    //получение сортированного по времени списка задач
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    //обновление времени эпика
    private void updateEpicTime(int epicId) {
        Epic epic = getEpics().get(epicId);
        List<Subtask> sortedSubtasks = new ArrayList<>(getListOfEpicSubtask(epicId));
        if (sortedSubtasks.isEmpty()) {
            epic.setStartTime(DEFAULT_TIME);
            epic.setEndTime(DEFAULT_TIME);
            epic.setDuration(DEFAULT_DURATION);
            return;
        }
        LocalDateTime startEpicTime = sortedSubtasks.getFirst().getStartTime();
        Duration epicDuration = DEFAULT_DURATION;
        LocalDateTime endEpicTime = sortedSubtasks.getLast().getEndTime();
        for (Subtask subtask : sortedSubtasks) {
            epicDuration = epicDuration.plus(subtask.getDuration());
        }
        epic.setDuration(epicDuration);
        epic.setStartTime(startEpicTime);
        epic.setEndTime(endEpicTime);
    }

    //обновление задач в мапах и трисете для методов update
    private void updatePrioritizedTasks(Task task) {
       try { //если начало и конец подзадачи совпадает с уже существующей
           if (task instanceof Subtask subtask) {
               if (subtask.getStartTime().isEqual(subtasks.get(subtask.getId()).getStartTime()) &&
                       subtask.getEndTime().isEqual(subtasks.get(subtask.getId()).getEndTime())) {
                   prioritizedTasks.removeIf(t -> t.equals(subtask));
                   prioritizedTasks.add(subtask);
                   subtasks.put(subtask.getId(), subtask);
                   updateEpicStatus(subtask.getEpicId());
               } else if (getPrioritizedTasks().stream() //если пересекается как то подругому
                       .anyMatch(t -> timeIntersectionSearch(task, t))) {
                   throw new TimeIntersectionException("Задача не обновлена т.к пересекается во времени с уже" +
                           "существующей");
               } else { //если не пересекается
                   prioritizedTasks.removeIf(t -> t.equals(subtask));
                   prioritizedTasks.add(subtask);
                   subtasks.put(subtask.getId(), subtask);
                   updateEpicStatus(subtask.getEpicId());
                   updateEpicTime(subtask.getEpicId());
               }
           } else { //то же самое для задач
               if (task.getStartTime().isEqual(tasks.get(task.getId()).getStartTime()) &&
                       task.getEndTime().isEqual(tasks.get(task.getId()).getEndTime())) {
                   prioritizedTasks.removeIf(t -> t.equals(task));
                   prioritizedTasks.add(task);
                   tasks.put(task.getId(), task);
               } else if (getPrioritizedTasks().stream()
                       .anyMatch(t -> timeIntersectionSearch(task, t))) {
                   throw new TimeIntersectionException("Задача не обновлена т.к пересекается во времени с уже" +
                           "существующей");
               } else {
                   prioritizedTasks.removeIf(t -> t.equals(task));
                   prioritizedTasks.add(task);
                   tasks.put(task.getId(), task);
               }
           }
       } catch (TimeIntersectionException e) {
           System.out.println(e.getMessage());
       }
    }

    //добавление задач в трисет для методов create
    private void addToPrioritizedTasks(Task task) {
        if (task.getDuration().equals(DEFAULT_DURATION)) {
            return;
        }
        try {
            if (getPrioritizedTasks().stream()
                    .anyMatch(t -> timeIntersectionSearch(task, t))) {
                if (task instanceof Subtask) {
                    deleteSubtaskById(task.getId());
                } else {
                    deleteTaskById(task.getId());
                }
                throw new TimeIntersectionException("Задача не создана т.к пересекается во времени с уже" +
                        "существующей");
            }
        } catch (TimeIntersectionException e) {
            System.out.println(e.getMessage());
            return;
        }
        prioritizedTasks.removeIf(t -> t.equals(task));
        prioritizedTasks.add(task);
    }

    public Set<Task> getPrioritizedTasksForLoadFromFile() {
        return prioritizedTasks;
    }

    //удаление задачи из отсортированного списка при удалении задачи по id
    private void deleteOneTaskFromPrioritizedTasks(Task task) {
        prioritizedTasks.removeIf(t -> t.equals(task));
    }

    //удаление подзадачи по id из отсортированного списка при удалении эпика по id
    private void deleteOneSubtaskFromPrioritizedTasks(int id) {
        prioritizedTasks.removeIf(task -> task.getId() == id);
    }


    //удаление всех задач из отсортированного списка
    private void deleteAllTasksFromPrioritizedTasks() {
        prioritizedTasks.removeIf(task -> task instanceof Task);
    }

    //удаление всех подзадач из отсортированного списка
    private void deleteAllSubtasksFromPrioritizedTasks() {
        prioritizedTasks.removeIf(task -> task instanceof Subtask);
    }
}
