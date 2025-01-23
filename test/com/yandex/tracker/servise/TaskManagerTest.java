package com.yandex.tracker.servise;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;
import com.yandex.tracker.model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

    abstract T createTaskManager();

    @BeforeEach
    void setTaskManager() {
        taskManager = createTaskManager();
    }

    // тест на изменения статуса эпика
    @Test
    void checkEpicStatusTest() {
        Epic epic = taskManager.createEpic(new Epic("a","b")).get();
        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
        //все подзадачи new
        Subtask subtask = taskManager.createSubtask(new Subtask(0, "a", "b", TaskStatus.NEW,
                LocalDateTime.of(2025, 1,1, 6, 0), Duration.ofMinutes(60),
                epic.getId()), epic.getId()).get();
        Subtask subtask1 = taskManager.createSubtask(new Subtask(0, "a", "b", TaskStatus.NEW,
                LocalDateTime.of(2025, 1,1, 8, 0), Duration.ofMinutes(60),
                epic.getId()), epic.getId()).get();
        Subtask subtask2 = taskManager.createSubtask(new Subtask(0, "a", "b", TaskStatus.NEW,
                LocalDateTime.of(2025, 1,1, 7, 0), Duration.ofMinutes(60),
                epic.getId()), epic.getId()).get();
        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
        //две подзадачи new одна done
        subtask.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
        //две подзадачи new одна in Progress
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
        //все подзадачи done
        subtask.setStatus(TaskStatus.DONE);
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        Assertions.assertEquals(TaskStatus.DONE, taskManager.getEpicById(epic.getId()).getStatus());
        //все задачи in progress
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }

    //тест на правильность расчета продолжительности, начала и конца эпика
    @Test
    void checkEpicTimeTest() {
        Epic epic = taskManager.createEpic(new Epic("a","b")).get();
        Assertions.assertEquals(Duration.ZERO, epic.getDuration());
        Subtask subtask = taskManager.createSubtask(new Subtask(0, "a", "b", TaskStatus.NEW,
                LocalDateTime.of(2025, 1,1, 6, 0), Duration.ofMinutes(60),
                epic.getId()), epic.getId()).get();
        Assertions.assertEquals(subtask.getDuration(), epic.getDuration());
        Assertions.assertEquals(subtask.getStartTime(), epic.getStartTime());
        Assertions.assertEquals(subtask.getEndTime(), epic.getEndTime());
        Subtask subtask1 = taskManager.createSubtask(new Subtask(0, "a", "b", TaskStatus.NEW,
                LocalDateTime.of(2025, 1,1, 8, 0), Duration.ofMinutes(60),
                epic.getId()), epic.getId()).get();
        Assertions.assertEquals(subtask.getDuration().plus(subtask1.getDuration()), epic.getDuration());
        Assertions.assertEquals(subtask.getStartTime(), epic.getStartTime());
        Assertions.assertEquals(subtask1.getEndTime(), epic.getEndTime());
        Subtask subtask2 = taskManager.createSubtask(new Subtask(0, "a", "b", TaskStatus.NEW,
                LocalDateTime.of(2025, 1,1, 7, 0), Duration.ofMinutes(60),
                epic.getId()), epic.getId()).get();
        Assertions.assertEquals(subtask.getDuration().plus(subtask1.getDuration()).plus(subtask2.getDuration()),
                epic.getDuration());
        Assertions.assertEquals(subtask.getStartTime(), epic.getStartTime());
        Assertions.assertEquals(subtask1.getEndTime(), epic.getEndTime());
        taskManager.deleteSubtaskById(subtask1.getId());
        Assertions.assertEquals(Duration.ofMinutes(120), epic.getDuration());
        Assertions.assertEquals(subtask2.getEndTime(), epic.getEndTime());
        Assertions.assertEquals(subtask.getStartTime(), epic.getStartTime());
        taskManager.removeSubtasks();
        Assertions.assertEquals(LocalDateTime.MAX, epic.getStartTime());
        Assertions.assertEquals(LocalDateTime.MAX, epic.getEndTime());
        Assertions.assertEquals(Duration.ZERO, epic.getDuration());
    }

    //тест на корректность расчетов пересечений при создании задач/подзадач
    @Test
    void timeIntersectionTest() {
        taskManager.createTask(new Task(0, "e", "r", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 6, 0), Duration.ofMinutes(30)));
        Epic epic = taskManager.createEpic(new Epic("a","b")).get();
        taskManager.createSubtask(new Subtask(0, "a", "b", TaskStatus.NEW,
                LocalDateTime.of(2025, 1,1, 7, 0), Duration.ofMinutes(30),
                epic.getId()), epic.getId());
        //попытка создать задачу, совпадающую по времени начала с уже существующей
        Assertions.assertThrows(TimeIntersectionException.class, () -> taskManager.createTask(new Task(0, "e",
                "r", TaskStatus.NEW, LocalDateTime.of(2025, 1, 1, 6, 0),
                Duration.ofMinutes(30))));
        //попытка создать подзадачу, совпадающую по времени окончания с уже существующей
        Assertions.assertThrows(TimeIntersectionException.class, () -> taskManager.createSubtask(new Subtask(0, "a", "b", TaskStatus.NEW,
                LocalDateTime.of(2025, 1,1, 7, 15), Duration.ofMinutes(15),
                epic.getId()), epic.getId()));
        //попытка создать задачу начало которой находится между началом и концом уже существующей
        Assertions.assertThrows(TimeIntersectionException.class, () -> taskManager.createTask(new Task(0, "e", "r", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 6, 15),
                Duration.ofMinutes(30))));
        //попытка создать подзадачу конец которой между началом и концом существующей задачи
        Assertions.assertThrows(TimeIntersectionException.class, () -> taskManager.createSubtask(new Subtask(0, "a", "b", TaskStatus.NEW,
                LocalDateTime.of(2025, 1,1, 5, 50), Duration.ofMinutes(15),
                epic.getId()), epic.getId()));
        //попытка создать задачу время которой содержит в себе время уже существующей
        Assertions.assertThrows(TimeIntersectionException.class, () -> taskManager.createTask(new Task(0, "e", "r", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 5, 30),
                Duration.ofMinutes(70))));
    }

    //тест на верную сортировку
    @Test
    void correctlySortedList() {
        InMemoryTaskManager taskManager1 = (InMemoryTaskManager) taskManager;
        Task task3 = taskManager1.createTask(new Task(0, "e", "r", TaskStatus.NEW,
                LocalDateTime.of(2025, 2, 1, 5, 30), Duration.ofMinutes(70))).get();
        Epic epic = taskManager1.createEpic(new Epic("a","b")).get();
        Subtask subtask = taskManager1.createSubtask(new Subtask(0, "a", "b", TaskStatus.NEW,
                LocalDateTime.of(2025, 1,1, 6, 0), Duration.ofMinutes(60),
                epic.getId()), epic.getId()).get();
        Subtask subtask1 = taskManager1.createSubtask(new Subtask(0, "a", "b", TaskStatus.NEW,
                LocalDateTime.of(2025, 1,1, 8, 0), Duration.ofMinutes(60),
                epic.getId()), epic.getId()).get();
        Subtask subtask2 = taskManager1.createSubtask(new Subtask(0, "a", "b", TaskStatus.NEW,
                LocalDateTime.of(2025, 1,1, 7, 0), Duration.ofMinutes(60),
                epic.getId()), epic.getId()).get();
        Assertions.assertEquals(task3.getStartTime(), taskManager1.getPrioritizedTasks().getLast().getStartTime());
        Assertions.assertEquals(subtask.getStartTime(), taskManager1.getPrioritizedTasks().getFirst().getStartTime());
    }

    //тесты на то что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    @Test
    void addNewTaskAndFindItTest() {
        Task task = taskManager.createTask(new Task("a", "a")).get();
        Assertions.assertNotNull(taskManager.getListOfTasks());
        Assertions.assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
    void addNewEpicAndFindItTest() {
        Epic epic = taskManager.createEpic(new Epic("a", "a")).get();
        Assertions.assertNotNull(taskManager.getListOfEpics());
        Assertions.assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void addNewSubtaskAndFindItTest() {
        Epic epic = taskManager.createEpic(new Epic("a", "a")).get();
        Subtask subtask = taskManager.createSubtask(new Subtask(0, "a", "b", TaskStatus.NEW,
                LocalDateTime.of(2025, 1,1, 7, 15), Duration.ofMinutes(15),
                epic.getId()), epic.getId()).get();
        Assertions.assertNotNull(taskManager.getListOfSubtasks());
        Assertions.assertEquals(subtask, taskManager.getSubtasksById(subtask.getId()));
    }

    //тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    void immutabilityOfTheTaskWhenAddedToManager() {
        Task task = taskManager.createTask(new Task("a", "a")).get();
        Task task1 = taskManager.getTaskById(task.getId());
        Assertions.assertEquals(task.getId(), task1.getId());
        Assertions.assertEquals(task.getDescription(),task1.getDescription());
        Assertions.assertEquals(task.getName(), task1.getName());
        Assertions.assertEquals(task.getStatus(), task1.getStatus());
    }

    //тест на то что из эпика удаляются id удаленных подзадач
    @Test
    void thereAreNoDeletedSubtasksInEpicTest() {
        Epic epic = taskManager.createEpic(new Epic("a","a")).get();
        Subtask subtask = taskManager.createSubtask(new Subtask(0, "a", "b", TaskStatus.NEW,
                LocalDateTime.of(2025, 1,1, 7, 15), Duration.ofMinutes(15),
                epic.getId()), epic.getId()).get();
        taskManager.deleteSubtaskById(subtask.getId());
        Assertions.assertEquals(0, epic.getListOfSubtasks().size());
    }
}
