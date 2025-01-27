package com.yandex.tracker.servise;

import com.yandex.tracker.model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.yandex.tracker.model.Task.DEFAULT_DURATION;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File tasksFile;

    public FileBackedTaskManager(File file) {
        tasksFile = file;
    }

    //добавление в трисет при загрузке из файла
    private void addTaskToSortedSetFromFile(Task task) {
        if (task.getDuration().equals(DEFAULT_DURATION)) {
            return;
        }
        getPrioritizedTasksForLoadFromFile().add(task);
    }

    //создание менеджера из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        int id;
        final FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
            br.readLine();
            id = Integer.parseInt(br.readLine());
            taskManager.setId(id);
            while (br.ready()) {
                String[] line = br.readLine().split(",");
                Task task = taskFromArray(line);
                if (task instanceof Epic epic) {
                    taskManager.getEpics().put(epic.getId(), epic);
                } else if (task instanceof Subtask subtask) {
                    taskManager.getSubtasks().put(subtask.getId(), subtask);
                    taskManager.addTaskToSortedSetFromFile(subtask);
                } else {
                    taskManager.getTasks().put(task.getId(), task);
                    taskManager.addTaskToSortedSetFromFile(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerReadException(e.getMessage());
        }
        return taskManager;
    }

    //создание тасок из файла в зависимости от типа
    private static Task taskFromArray(String[] array) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        final int id = Integer.parseInt(array[0]);
        final TaskType type = TaskType.valueOf(array[1]);
        final String name = array[2];
        final String description = array[3];
        final TaskStatus status = TaskStatus.valueOf(array[4]);
        final LocalDateTime startTime = LocalDateTime.parse(array[5], formatter);
        final Duration duration = Duration.ofMinutes(Long.parseLong(array[6]));
        if (type.equals(TaskType.TASK)) {
            return new Task(id, name, description, status, startTime, duration);
        } else if (type.equals(TaskType.SUBTASK)) {
            final int epicId = Integer.parseInt(array[7]);
            return new Subtask(id, name, description, status, startTime, duration, epicId);
        } else {
            final LocalDateTime endTime = LocalDateTime.parse(array[8], formatter);
            String[] subtaskId = array[7].split(" ");
            if (Integer.parseInt(subtaskId[0]) < 0) {
                return new Epic(id, name, description, status, startTime, duration, new ArrayList<>(), endTime);
            } else {
                ArrayList<Integer> listId = new ArrayList<>();
                for (String subId : subtaskId) {
                    listId.add(Integer.parseInt(subId));
                }
                return new Epic(id, name, description, status, startTime,duration, listId, endTime);
            }
        }
    }

    //сохранение текущего состояния менеджера в файл
    private void save() {
        try (FileWriter fw = new FileWriter(tasksFile, StandardCharsets.UTF_8)) {
            fw.write(String.format("%s%n",
                    "id,type,name,description,status,startTime,duration,epicIdOrListSubtaskId,endTimeForEpic"));
            fw.write(String.format("%d%n", getId()));
            for (Task task : getTasks().values()) {
                fw.write(String.format("%s%n", task.toStringToSave()));
            }
            for (Epic epic : getEpics().values()) {
                fw.write(String.format("%s%n", epic.toStringToSave()));
            }
            for (Subtask subtask : getSubtasks().values()) {
                fw.write(String.format("%s%n", subtask.toStringToSave()));
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public File getFile() {
        return tasksFile;
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public Optional<Task> createTask(Task task) {
        Optional<Task> task1 = super.createTask(task);
        save();
        return task1;
    }

    @Override
    public Optional<Epic> createEpic(Epic epic) {
        Optional<Epic> epic1 = super.createEpic(epic);
        save();
        return epic1;
    }

    @Override
    public Optional<Subtask> createSubtask(Subtask subtask, int epicId) {
        Optional<Subtask> subtask1 = super.createSubtask(subtask, epicId);
        save();
        return subtask1;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
       super.updateSubtask(subtask);
       save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }
}