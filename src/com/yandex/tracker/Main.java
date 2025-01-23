package com.yandex.tracker;
import com.yandex.tracker.model.*;
import com.yandex.tracker.servise.FileBackedTaskManager;
import com.yandex.tracker.servise.TimeIntersectionException;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(new File(System.getProperty("user.dir"),
                "testFile.txt"));
        System.out.println("Поехали!");
        System.out.println("Создаю задачи");
        System.out.println();

        Task task = taskManager.createTask(new Task(0,"qqqqqq", "wwwwww", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(30))).get();
        Task task1 = taskManager.createTask(new Task(1,"eeee", "rrr",
                TaskStatus.NEW, LocalDateTime.of(2025, 1, 1, 10, 30),
                Duration.ofMinutes(30))).get();
        Epic epic = taskManager.createEpic(new Epic(2,"ttttttt", "yyyyyyyy",
                TaskStatus.NEW, new ArrayList<>())).get();
        Epic epic1 = taskManager.createEpic(new Epic(3,"uuuuuuu",
                "iiiiiiiii", TaskStatus.NEW, new ArrayList<>())).get();
        Subtask subtask = taskManager.createSubtask(new Subtask(4,"oooooooooo",
                "ppppppppp", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 9, 0),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();
        Subtask subtask2 = taskManager.createSubtask(new Subtask(5,"aaaaaaaaaaaaa",
                "sssssssssss", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 6, 30),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();
        Subtask subtask3 = taskManager.createSubtask(new Subtask(6,"ddddddd",
                "ffffffffff", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 8, 0),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();

        printAllTasks(taskManager);
        System.out.println();
        FileBackedTaskManager taskManager1 = FileBackedTaskManager.loadFromFile(taskManager.getFile());
        System.out.println("содержание восстановленного из файла менеджера");
        printAllTasks(taskManager1);
        System.out.println("создаю подзадачу пересекающуюся по времени с существующей");
        try {
            taskManager1.createSubtask(new Subtask(6,"ddddddd",
                    "ffffffffff", TaskStatus.NEW,
                    LocalDateTime.of(2025, 1, 1, 8, 0),
                    Duration.ofMinutes(30), epic.getId()), epic.getId());
        } catch (TimeIntersectionException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("содержание менеджера");
        printAllTasks(taskManager1);
        System.out.println();
        System.out.println("содержание восстановленного из файла менеджера");
        FileBackedTaskManager taskManager20 = FileBackedTaskManager.loadFromFile(taskManager1.getFile());
        printAllTasks(taskManager20);
        System.out.println();
        System.out.println("обновляю статус подзадачи");
        System.out.println();
        subtask2.setStatus(TaskStatus.DONE);
        taskManager20.updateSubtask(subtask2);
        System.out.println("содержание менеджера");
        printAllTasks(taskManager20);
        System.out.println();
        System.out.println("содержание восстановленного из файла менеджера");
        FileBackedTaskManager taskManager112 = FileBackedTaskManager.loadFromFile(taskManager20.getFile());
        printAllTasks(taskManager112);
        System.out.println();
        System.out.println("создаю задачу пересекающуюся с уже созданной");
        try {
            taskManager112.createTask(new Task(0,"xxx", "xxx", TaskStatus.NEW,
                    LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(30)));
        } catch (TimeIntersectionException e) {
            System.out.println(e.getMessage());
        }
        System.out.println();
        System.out.println("содержание менеджера");
        printAllTasks(taskManager112);
        System.out.println();
        System.out.println("содержание восстановленного из файла менеджера");
        FileBackedTaskManager taskManager4 = FileBackedTaskManager.loadFromFile(taskManager112.getFile());
        printAllTasks(taskManager4);
        System.out.println();
        System.out.println("создаю задачу без времени");
        taskManager4.createTask(new Task("eee", "rrr"));
        System.out.println("содержание менеджера");
        printAllTasks(taskManager4);
        System.out.println();
        System.out.println("содержание восстановленного из файла менеджера");
        FileBackedTaskManager taskManager7 = FileBackedTaskManager.loadFromFile(taskManager4.getFile());
        printAllTasks(taskManager7);
        System.out.println();
        System.out.println("Удаляю эпик с подзадачами");
        taskManager7.deleteEpicById(epic.getId());
        System.out.println();
        System.out.println("содержание менеджера");
        printAllTasks(taskManager7);
        System.out.println();
        System.out.println("содержание восстановленного из файла менеджера");
        FileBackedTaskManager taskManager5 = FileBackedTaskManager.loadFromFile(taskManager7.getFile());
        printAllTasks(taskManager5);
        System.out.println();
        System.out.println("Удаляю задачу");
        System.out.println();
        taskManager5.deleteTaskById(task.getId());
        System.out.println("содержание менеджера");
        System.out.println();
        printAllTasks(taskManager5);
        System.out.println();
        System.out.println("содержание восстановленного из файла менеджера");
        FileBackedTaskManager taskManager6 = FileBackedTaskManager.loadFromFile(taskManager5.getFile());
        printAllTasks(taskManager6);
        System.out.println();
        System.out.println("добавляю подзадачу в пустой эпик");
        Subtask subtask1 = taskManager6.createSubtask(new Subtask(5, "nnn", "mmmm",
                TaskStatus.DONE, LocalDateTime.of(2025, 1, 1, 22, 30),
                Duration.ofMinutes(120), epic1.getId()), epic1.getId()).get();
        System.out.println("содержание менеджера");
        printAllTasks(taskManager6);
        System.out.println();
        System.out.println("содержание восстановленного из файла менеджера");
        FileBackedTaskManager taskManager9 = FileBackedTaskManager.loadFromFile(taskManager6.getFile());
        printAllTasks(taskManager9);
        System.out.println();
        System.out.println("удаляю подзадачу из эпика");
        taskManager9.deleteSubtaskById(subtask1.getId());
        System.out.println("содержание менеджера");
        printAllTasks(taskManager9);
        System.out.println();
        System.out.println("содержание восстановленного менеджера");
        FileBackedTaskManager taskManager8 = FileBackedTaskManager.loadFromFile(taskManager9.getFile());
        printAllTasks(taskManager8);
        System.out.println();
        System.out.println("удаляю таску со временем");
        taskManager8.deleteTaskById(2);
        System.out.println();
        printAllTasks(taskManager8);
        System.out.println("удаляю таску без времени");
        taskManager8.deleteTaskById(8);
        printAllTasks(taskManager8);
        System.out.println("Удаляю эпики и задачу - делаю пустым файл");
        System.out.println();
        taskManager9.removeEpics();
        taskManager9.removeTasks();
        System.out.println("содержание менеджера");
        System.out.println();
        FileBackedTaskManager taskManager10 = FileBackedTaskManager.loadFromFile(taskManager8.getFile());
        printAllTasks(taskManager9);
        System.out.println("содержание восстановленного из пустого файла менеджера");
        System.out.println();
        printAllTasks(taskManager10);
    }

    private static void printAllTasks(FileBackedTaskManager manager) {
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
        System.out.println("Список сортированный по дате");
        for (Task task : manager.getPrioritizedTasks()) {
            System.out.println(task);
        }
    }
}
