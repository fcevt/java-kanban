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
            deleteTaskFromPrioritizedTasks(id);
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
            deleteTaskFromPrioritizedTasks(id);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        for (Integer id : subtasks.keySet()) {
            browsingHistory.remove(id);
            deleteTaskFromPrioritizedTasks(id);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeListOfSubtasks();
            epic.setStatus(TaskStatus.NEW);
            updateEpic(epic);
            updateEpicTime(epic.getId());
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
    public Optional<Task> createTask(Task task) {
        id++;
        task.setId(id);
        try {
            addToPrioritizedTasks(task);
        } catch (TimeIntersectionException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
        tasks.put(id, task);
        return Optional.of(task);
    }

    @Override
    public Optional<Epic> createEpic(Epic epic) {
        id++;
        epic.setId(id);
        epics.put(id, epic);
        return Optional.of(epic);
    }

    @Override
    public Optional<Subtask> createSubtask(Subtask subtask, int epicId) {
        id++;
        subtask.setId(id);
        subtask.setEpicId(epicId);
        try {
            addToPrioritizedTasks(subtask);
        } catch (TimeIntersectionException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
        subtasks.put(id, subtask);
        Epic epic = epics.get(epicId);
        epic.addSubtaskToList(subtask.getId());
        updateEpic(epic);
        updateEpicStatus(epic.getId());
        updateEpicTime(subtask.getEpicId());
        return Optional.of(subtask);
    }

    //обновление данных
    @Override
    public void updateTask(Task task) {
        final int id = task.getId();
        final Task savedTask = tasks.get(id);
        try {
            if (savedTask == null) {
                throw new NonExistingTaskException("Задача не обновлена т.к задачи с id=" + id + " не существует");
            }
            if (task.getStartTime().isEqual(savedTask.getStartTime()) && //если в метод передали задачу с
                    task.getEndTime().isEqual(savedTask.getEndTime())) { //временем начала и конца уже существующей
                prioritizedTasks.removeIf(t -> t.equals(task));    //задачи
                prioritizedTasks.add(task);
                tasks.put(task.getId(), task);
                return;
            }
            prioritizedTasks.stream()
                    .filter(existingTask -> timeIntersectionSearch(task, existingTask))
                    .findFirst()
                    .ifPresentOrElse(
                            // Если нашлась задача с пересекающимся временем, то генерируем исключение
                            overlappedTask -> {
                                throw new TimeIntersectionException("Задача не обновлена т.к пересекается во времени с" +
                                        " уже существующей");
                            },
                            () -> {
                                prioritizedTasks.removeIf(t -> t.equals(task));
                                prioritizedTasks.add(task);
                            });
        } catch (TimeIntersectionException | NonExistingTaskException e) {
            System.out.println(e.getMessage());
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        final int id = subtask.getId();
        final int epicId = subtask.getEpicId();
        final Subtask savedTask = subtasks.get(id);
        try {
            if (savedTask == null) {
                throw new NonExistingTaskException("Задача не обновлена т.к задачи с id=" + id + " не существует");
            }
            if (subtask.getStartTime().isEqual(savedTask.getStartTime()) && //если в метод передали задачу с
                    subtask.getEndTime().isEqual(savedTask.getEndTime())) { //временем начала и конца уже существующей
                prioritizedTasks.removeIf(t -> t.equals(subtask));    //задачи
                prioritizedTasks.add(subtask);
                subtasks.put(id, subtask);
                updateEpicStatus(epicId);
                return;
            }
            prioritizedTasks.stream()
                    .filter(existingTask -> timeIntersectionSearch(subtask, existingTask))
                    .findFirst()
                    .ifPresent(t -> {
                        throw new TimeIntersectionException("Задача не обновлена т.к пересекается во" +
                            " времени с уже существующей");
                    });
        } catch (TimeIntersectionException | NonExistingTaskException e) {
            System.out.println(e.getMessage());
            return;
        }
        prioritizedTasks.removeIf(t -> t.equals(subtask));
        prioritizedTasks.add(subtask);
        subtasks.put(id, subtask);
        updateEpicTime(epicId);
        updateEpicStatus(epicId);
    }

    // удаление по id
    @Override
    public void deleteTaskById(int id) {
        browsingHistory.remove(id);
        deleteTaskFromPrioritizedTasks(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        final Epic epic = epics.remove(id);
        browsingHistory.remove(id);
        for (Integer subtaskId : epic.getListOfSubtasks()) {
            subtasks.remove(subtaskId);
            browsingHistory.remove(subtaskId);
            deleteTaskFromPrioritizedTasks(subtaskId);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        browsingHistory.remove(id);
        deleteTaskFromPrioritizedTasks(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtaskFromList(id);
        updateEpicStatus(subtask.getEpicId());
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
    @Override
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
        prioritizedTasks.stream()
                .filter(existingTask -> timeIntersectionSearch(task, existingTask))
                .findFirst()
                .ifPresentOrElse(
                        // Если нашлась задача с пересекающимся временем, то генерируем исключение
                        overlappedTask -> {
                            throw new TimeIntersectionException("Задача не создана т.к пересекается во времени с уже" +
                                    "существующей");
                        },
                        // если задач пересекающихся по времени нет, то добавляем новую задачу

                        () -> prioritizedTasks.add(task));
    }

    public Set<Task> getPrioritizedTasksForLoadFromFile() {
        return prioritizedTasks;
    }

    //удаление подзадачи по id из отсортированного списка при удалении эпика по id
    private void deleteTaskFromPrioritizedTasks(int id) {
        prioritizedTasks.removeIf(task -> task.getId() == id);
    }
}
