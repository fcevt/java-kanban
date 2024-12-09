package com.yandex.tracker;
import com.yandex.tracker.model.*;
import com.yandex.tracker.servise.Managers;
import com.yandex.tracker.servise.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
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
        System.out.println("вызываю все созданные задачи");
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtasksById(subtask.getId());
        taskManager.getSubtasksById(subtask2.getId());
        taskManager.getSubtasksById(subtask3.getId());
        printAllTasks(taskManager);
        System.out.println();
        System.out.println("повторно обращаюсь к задачам");
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtasksById(subtask.getId());
        printAllTasks(taskManager);
        System.out.println();
        System.out.println("Удаляю эпик с подзадачами");
        taskManager.deleteEpicById(epic.getId());
        System.out.println();
        printAllTasks(taskManager);
        System.out.println();
        System.out.println("Удаляю задачу");
        System.out.println();
        taskManager.deleteTaskById(task.getId());
        System.out.println();
        printAllTasks(taskManager);
        System.out.println("Удаляю эпики и обращаюсь к несуществующим задачам");
        System.out.println();
        taskManager.removeEpics();
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);
        taskManager.getSubtasksById(1);
        printAllTasks(taskManager);
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

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

    }
}
