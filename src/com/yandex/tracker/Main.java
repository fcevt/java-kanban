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
        Subtask subtask4 = taskManager.createSubtask(new Subtask("изучить отзывы и цены на репетиторов",
                        "Найти репетитора"), epic1.getId());

        printAllTasks(taskManager);
        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getSubtasksById(5);
        System.out.println();
        System.out.println("Меняю статус");
        System.out.println();
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        subtask4.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask4);
        printAllTasks(taskManager);
        System.out.println();
        System.out.println("Создаю новую подзадачу");
        Subtask subtask5 = taskManager.createSubtask(new Subtask("Найти и купить учебники по английскому",
                "Купить учебники"), epic1.getId());
        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getSubtasksById(5);
        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getSubtasksById(5);
        taskManager.getTaskById(0);
        taskManager.getEpicById(0);
        taskManager.getSubtasksById(0);
        System.out.println();
        printAllTasks(taskManager);
        System.out.println();
        System.out.println("Удаляю эпик");
        taskManager.deleteEpicById(epic.getId());
        System.out.println();
        printAllTasks(taskManager);
        System.out.println();
        System.out.println("Удаляю задачу и подзадачу");
        System.out.println();
        taskManager.deleteSubtaskById(subtask4.getId());
        taskManager.deleteTaskById(task1.getId());
        System.out.println();
        printAllTasks(taskManager);
        System.out.println("Удаляю эпики");
        System.out.println();
        taskManager.removeEpics();
        taskManager.getTaskById(2);
        taskManager.getEpicById(4);
        taskManager.getSubtasksById(6);
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
