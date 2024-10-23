public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
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

        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println(taskManager.getListOfSubtasks());
        System.out.println();
        System.out.println("Меняю статус");
        System.out.println();
        taskManager.updateTaskStatus(task.getId(), TaskStatus.DONE);
        taskManager.updateSubtaskStatus(subtask4.getId(), TaskStatus.DONE);
        taskManager.updateSubtaskStatus(subtask3.getId(), TaskStatus.DONE);
        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println(taskManager.getListOfSubtasks());
        System.out.println();
        System.out.println("Удаляю задачу и эпик");
        System.out.println();
        taskManager.deleteEpicById(epic1.getId());
        taskManager.deleteTaskById(task1.getId());
        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println(taskManager.getListOfSubtasks());
        System.out.println("Удаляю все");
        taskManager.removeAll();
        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println(taskManager.getListOfSubtasks());
    }
}
