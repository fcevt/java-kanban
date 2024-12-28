package com.yandex.tracker;
import com.yandex.tracker.model.*;
import com.yandex.tracker.servise.FileBackedTaskManager;
import com.yandex.tracker.servise.TaskManager;

public class Main {

    public static void main(String[] args) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager();
        System.out.println("Поехали!");
        System.out.println("Создаю задачи");
        System.out.println();

        Task task = taskManager.createTask(new Task("Сделать уборку", "Уборка"));
        Task task1 = taskManager.createTask(new Task("Постирать вещи", "Стирка"));
        Epic epic = taskManager.createEpic(new Epic("Посадить овощи", "Заняться огородом"));
        Epic epic1 = taskManager.createEpic(new Epic("Начать учить английский",
                "Заняться саморазвитием"));
        Subtask subtask = taskManager.createSubtask(new Subtask("замочить семена", "Посадить помидоры"),
                epic.getId());
        Subtask subtask2 = taskManager.createSubtask(new Subtask("замочить семена", "Посадить огурцы"),
                epic.getId());
        Subtask subtask3 = taskManager.createSubtask(new Subtask("вскопать огород",
                        "Посадить картошку"), epic.getId());

        printAllTasks(taskManager);
        System.out.println();
        FileBackedTaskManager taskManager1 = FileBackedTaskManager.loadFromFile(taskManager.getFile());
        System.out.println("содержание восстановленного из файла менеджера");
        printAllTasks(taskManager1);
        System.out.println();
        System.out.println("менеджеры равны?");
        System.out.println(taskManager.equals(taskManager1));
        System.out.println();
        System.out.println("меняю статус подзадачи");
        System.out.println();
        taskManager.updateSubtask(new Subtask(5, "замочить семена", "Посадить помидоры",
                TaskStatus.DONE, 3));
        System.out.println("содержание менеджера");
        printAllTasks(taskManager);
        System.out.println();
        System.out.println("содержание восстановленного из файла менеджера");
        taskManager1 = FileBackedTaskManager.loadFromFile(taskManager.getFile());
        printAllTasks(taskManager1);
        System.out.println();
        System.out.println("менеджеры равны?");
        System.out.println(taskManager.equals(taskManager1));
        System.out.println();
        System.out.println("Удаляю эпик с подзадачами");
        taskManager.deleteEpicById(epic.getId());
        System.out.println();
        System.out.println("содержание менеджера");
        printAllTasks(taskManager);
        System.out.println();
        System.out.println("содержание восстановленного из файла менеджера");
        taskManager1 = FileBackedTaskManager.loadFromFile(taskManager.getFile());
        printAllTasks(taskManager1);
        System.out.println();
        System.out.println("менеджеры равны?");
        System.out.println(taskManager.equals(taskManager1));
        System.out.println();
        System.out.println("Удаляю задачу");
        System.out.println();
        taskManager.deleteTaskById(task.getId());
        System.out.println("содержание менеджера");
        System.out.println();
        printAllTasks(taskManager);
        System.out.println();
        System.out.println("содержание восстановленного из файла менеджера");
        taskManager1 = FileBackedTaskManager.loadFromFile(taskManager.getFile());
        printAllTasks(taskManager1);
        System.out.println();
        System.out.println("менеджеры равны?");
        System.out.println(taskManager.equals(taskManager1));
        System.out.println();
        System.out.println("Удаляю эпики и задачу - делаю пустым файл");
        System.out.println();
        taskManager.removeEpics();
        taskManager.removeTasks();
        System.out.println("содержание менеджера");
        System.out.println();
        taskManager1 = FileBackedTaskManager.loadFromFile(taskManager.getFile());
        printAllTasks(taskManager);
        System.out.println("содержание восстановленного из пустого файла менеджера");
        System.out.println();
        printAllTasks(taskManager1);
        System.out.println("менеджеры равны?");
        System.out.println(taskManager.equals(taskManager1));
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getListOfTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getListOfEpics()) {
            System.out.println(epic);

            for (Task task : manager.getListOfEpicSubtask(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getListOfSubtasks()) {
            System.out.println(subtask);
        }
    }
}
