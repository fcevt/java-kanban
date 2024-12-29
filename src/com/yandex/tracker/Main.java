package com.yandex.tracker;
import com.yandex.tracker.model.*;
import com.yandex.tracker.servise.FileBackedTaskManager;
import com.yandex.tracker.servise.TaskManager;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(new File(System.getProperty("user.dir"),
                "testFile.txt"));
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
        System.out.println("меняю статус подзадачи");
        System.out.println();
        taskManager1.updateSubtask(new Subtask(5, "замочить семена", "Посадить помидоры",
                TaskStatus.DONE, 3));
        System.out.println("содержание менеджера");
        printAllTasks(taskManager1);
        System.out.println();
        System.out.println("содержание восстановленного из файла менеджера");
        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(taskManager.getFile());
        printAllTasks(taskManager2);
        System.out.println();
        System.out.println("Удаляю эпик с подзадачами");
        taskManager2.deleteEpicById(epic.getId());
        System.out.println();
        System.out.println("содержание менеджера");
        printAllTasks(taskManager2);
        System.out.println();
        System.out.println("содержание восстановленного из файла менеджера");
        FileBackedTaskManager taskManager3 = FileBackedTaskManager.loadFromFile(taskManager.getFile());
        printAllTasks(taskManager3);
        System.out.println();
        System.out.println("Удаляю задачу");
        System.out.println();
        taskManager3.deleteTaskById(task.getId());
        System.out.println("содержание менеджера");
        System.out.println();
        printAllTasks(taskManager3);
        System.out.println();
        System.out.println("содержание восстановленного из файла менеджера");
        FileBackedTaskManager taskManager4 = FileBackedTaskManager.loadFromFile(taskManager.getFile());
        printAllTasks(taskManager4);
        System.out.println();
        System.out.println("Удаляю эпики и задачу - делаю пустым файл");
        System.out.println();
        taskManager4.removeEpics();
        taskManager4.removeTasks();
        System.out.println("содержание менеджера");
        System.out.println();
        FileBackedTaskManager taskManager5 = FileBackedTaskManager.loadFromFile(taskManager.getFile());
        printAllTasks(taskManager4);
        System.out.println("содержание восстановленного из пустого файла менеджера");
        System.out.println();
        printAllTasks(taskManager5);
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
