package com.yandex.tracker.servise;

import com.yandex.tracker.model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File tasksFile;

    public FileBackedTaskManager() {
        super();
        tasksFile = new File(System.getProperty("user.dir"),"tasks.txt");
    }

    public FileBackedTaskManager(int id, HashMap<Integer, Task> tasks,HashMap<Integer, Epic> epics,
                                 HashMap<Integer, Subtask> subtasks, File file) {
        super(id, tasks, epics, subtasks);
        tasksFile = file;
    }

    //создание менеджера из файла
    public static FileBackedTaskManager loadFromFile(File file) throws ManagerReadException {
        HashMap<Integer, Task> tasks = new HashMap<>();
        HashMap<Integer, Epic> epics = new HashMap<>();
        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        int id = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
            while (br.ready()) {
                String[] line = br.readLine().split(",");
                if (line[0].equals("!")) {
                    id = Integer.parseInt(line[1]);
                } else if (line[0].equals("id")) {
                    continue;
                } else {
                    Task task = taskFromArray(line);
                    if (task instanceof Epic) {
                        epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        subtasks.put(task.getId(), (Subtask) task);
                    } else {
                        tasks.put(task.getId(), task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerReadException(e.getMessage());
        }
        return new FileBackedTaskManager(id, tasks, epics, subtasks, file);
    }

    //создание тасок из файла в зависимости от типа
    private static Task taskFromArray(String[] array) {
        if (array[1].equals(TaskType.TASK.name())) {
            return new Task(Integer.parseInt(array[0]), array[2], array[3], TaskStatus.valueOf(array[4]));
        } else if (array[1].equals(TaskType.SUBTASK.name())) {
            return new Subtask(Integer.parseInt(array[0]), array[2], array[3], TaskStatus.valueOf(array[4]),
                    Integer.parseInt(array[5]));
        } else  {
            String[] subtaskId = array[5].split(" ");
            if (Integer.parseInt(subtaskId[0]) < 0) {
                return new Epic(Integer.parseInt(array[0]), array[2], array[3], TaskStatus.valueOf(array[4]),
                        new ArrayList<>());
            } else {
                ArrayList<Integer> listId = new ArrayList<>();
                for (String id : subtaskId) {
                    listId.add(Integer.parseInt(id));
                }
                return new Epic(Integer.parseInt(array[0]), array[2], array[3], TaskStatus.valueOf(array[4]),
                       listId);
            }
        }
    }

    //сохранение текущего состояния менеджера в файл
    private void save() {
    try (FileWriter fw = new FileWriter(tasksFile, StandardCharsets.UTF_8)) {
        fw.write(String.format("!,%d%n", getId()));
        fw.write(String.format("%s%n", "id,type,name,description,status,epicIdOrListSubtaskId"));
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
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask, int epicId) {
        super.createSubtask(subtask, epicId);
        save();
        return subtask;
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
    public void  deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void  deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void  deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileBackedTaskManager fileBackedTaskManager = (FileBackedTaskManager) o;
        return getId() == fileBackedTaskManager.getId() &&
                getTasks().equals(fileBackedTaskManager.getTasks()) &&
                getEpics().equals(fileBackedTaskManager.getEpics()) &&
                getSubtasks().equals(fileBackedTaskManager.getSubtasks()) &&
                tasksFile.equals(fileBackedTaskManager.tasksFile);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (getTasks() != null) {
            hash += getTasks().hashCode();
        }
        hash *= 31;
        if (getEpics() != null) {
            hash += getEpics().hashCode();
        }
        hash *= 31;
        if (getSubtasks() != null) {
            hash += getSubtasks().hashCode();
        }
        hash *= 31;
        if (tasksFile != null) {
            hash += tasksFile.hashCode();
        }
        hash += Objects.hashCode(getId());
        return hash;
    }
}
