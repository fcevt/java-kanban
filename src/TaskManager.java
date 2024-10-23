import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int id;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        id = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    // получение списков задач
    public ArrayList<Task> getListOfTasks() {
        ArrayList<Task> listOfTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            listOfTasks.add(task);
        }
        return listOfTasks;
    }

    public ArrayList<Epic> getListOfEpics() {
        ArrayList<Epic> listOfEpics = new ArrayList<>();
        for (Epic epic : epics.values()) {
            listOfEpics.add(epic);
        }
        return listOfEpics;
    }

    public ArrayList<Subtask> getListOfSubtasks() {
        ArrayList<Subtask> listOfSubtask = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            listOfSubtask.add(subtask);
        }
        return listOfSubtask;
    }
    // получение списка подзадач определенного эпика
    public ArrayList<Subtask> getListOfEpicSubtask(int epicId) {
        ArrayList<Subtask> listOfSubtask = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                listOfSubtask.add(subtask);
            }
        }
        return listOfSubtask;
    }

    // удаление всех задач списка
     void removeTasks() {
        tasks.clear();
    }

    void removeEpics() {
        epics.clear();
    }

    void removeSubtasks() {
        subtasks.clear();
    }

    // удаление всех задач
    void removeAll() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    // получение задач по id
    Task getTaskById(int id) {
        return tasks.get(id);
    }

    Epic getEpicById (int id) {
        return epics.get(id);
    }

    Subtask getSubtasksById (int id) {
        return subtasks.get(id);
    }

    // создание задач
    Task createTask(Task task) {
        id++;
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    Epic createEpic(Epic epic) {
        id++;
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    Subtask createSubtask(Subtask subtask, int epicId) {
        id++;
        subtask.setId(id);
        subtask.setEpicId(epicId);
        subtasks.put(id, subtask);
        return subtask;
    }

    //обновление данных
    void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    // удаление по id
    void  deleteTaskById(int id) {
        tasks.remove(id);
    }

    void  deleteEpicById(int id) {
        epics.remove(id);
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == id) {
                subtasks.remove(subtask.getId());
            }
        }
    }

    void  deleteSubtaskById(int id) {
        subtasks.remove(id);
    }

    //обновление статуса
    void updateTaskStatus(int id, TaskStatus newStatus) {
        Task task = tasks.get(id);
        if (task.getStatus() != newStatus) {
            task.setStatus(newStatus);
            tasks.put(id, task);
        }
    }

    private void updateEpicStatus(int id, TaskStatus newStatus) {
        Epic epic = epics.get(id);
        epic.setStatus(newStatus);
        epics.put(id, epic);
    }

    void updateSubtaskStatus(int id, TaskStatus newStatus) {
        Subtask subtask = subtasks.get(id);
        subtask.setStatus(newStatus);
        ArrayList<Subtask> list = getListOfEpicSubtask(subtask.getEpicId());
        if (list.isEmpty()) {
            updateEpicStatus(subtask.getEpicId(), TaskStatus.NEW);
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
            updateEpicStatus(subtask.getEpicId(), TaskStatus.DONE);
        } else if (list.size() == numberNewSubtasks) {
            updateEpicStatus(subtask.getEpicId(), TaskStatus.NEW);
        } else {
            updateEpicStatus(subtask.getEpicId(), TaskStatus.IN_PROGRESS);
        }

    }
    
}
