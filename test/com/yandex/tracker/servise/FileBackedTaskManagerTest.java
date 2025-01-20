package com.yandex.tracker.servise;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;
import com.yandex.tracker.model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest extends TaskManagerTest {

    @Override
    TaskManager createTaskManager() {
        return  Managers.getDefault();
    }

//    тест на то что созданный и восстановленный из файла менеджеры равны по полям
    @Test
    void restoringManagerFromFileTest() {
        FileBackedTaskManager taskManager1 = (FileBackedTaskManager) taskManager;
        taskManager1.createTask(new Task(0, "e", "r", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 6, 0), Duration.ofMinutes(30)));
        Epic epic = taskManager1.createEpic(new Epic("a","b"));
        taskManager1.createSubtask(new Subtask(0, "a", "b", TaskStatus.NEW,
                LocalDateTime.of(2025, 1,1, 7, 0), Duration.ofMinutes(30),
                epic.getId()), epic.getId());
        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(taskManager1.getFile());
        Assertions.assertEquals(taskManager1.getTasks(), taskManager2.getTasks());
        Assertions.assertEquals(taskManager1.getEpics(), taskManager2.getEpics());
        Assertions.assertEquals(taskManager1.getSubtasks(), taskManager2.getSubtasks());
        Assertions.assertEquals(taskManager1.getId(), taskManager2.getId());
    }

//  тест на то что изменения сохраняются в файл и восстановленный из этого файла менеджер равен исходному по всем полям
    @Test
    void restoringManagerFromFileAfterChangesTest() {
        FileBackedTaskManager taskManager1 = (FileBackedTaskManager) taskManager;
        taskManager1.createTask(new Task(0, "e", "r", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 6, 0), Duration.ofMinutes(30)));
        Epic epic = taskManager1.createEpic(new Epic("b", "c"));
        Subtask subtask = taskManager1.createSubtask(new Subtask(0, "a", "b", TaskStatus.NEW,
                LocalDateTime.of(2025, 1,1, 7, 0), Duration.ofMinutes(30),
                epic.getId()), epic.getId());
        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(taskManager1.getFile());
        Assertions.assertEquals(taskManager1.getTasks(), taskManager2.getTasks());
        Assertions.assertEquals(taskManager1.getEpics(), taskManager2.getEpics());
        Assertions.assertEquals(taskManager1.getSubtasks(), taskManager2.getSubtasks());
        Assertions.assertEquals(taskManager1.getId(), taskManager2.getId());
        taskManager2.updateSubtask(new Subtask(subtask.getId(), "a", "b", TaskStatus.DONE,
                LocalDateTime.of(2025, 1,1, 7, 0), Duration.ofMinutes(30),
                epic.getId()));
        FileBackedTaskManager taskManager3 = FileBackedTaskManager.loadFromFile(taskManager2.getFile());
        Assertions.assertEquals(taskManager1.getTasks(), taskManager3.getTasks());
        Assertions.assertEquals(taskManager1.getEpics(), taskManager3.getEpics());
        Assertions.assertEquals(taskManager1.getSubtasks(), taskManager3.getSubtasks());
        Assertions.assertEquals(taskManager1.getId(), taskManager3.getId());
    }

    //тест на корректный перехват исключений при не существующем файле
    @Test
    void correctCatchExceptionTest() {
        Assertions.assertThrows(ManagerReadException.class,
                () -> FileBackedTaskManager.loadFromFile(Path.of("tvt", ".txt").toFile()));

    }
}
