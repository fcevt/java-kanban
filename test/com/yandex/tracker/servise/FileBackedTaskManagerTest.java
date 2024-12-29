package com.yandex.tracker.servise;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;
import com.yandex.tracker.model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class FileBackedTaskManagerTest {
    FileBackedTaskManager taskManager = new FileBackedTaskManager(new File(System.getProperty("user.dir"),
            "testFile"));

    //тест на то что созданный и восстановленный из файла менеджеры равны по полям
    @Test
    void restoringManagerFromFileTest() {
        taskManager.createTask(new Task("a","a"));
        taskManager.createEpic(new Epic("b", "c"));
        taskManager.createSubtask(new Subtask("c", "d"), 2);
        FileBackedTaskManager taskManager1 = FileBackedTaskManager.loadFromFile(taskManager.getFile());
        Assertions.assertEquals(taskManager1.getTasks(), taskManager.getTasks());
        Assertions.assertEquals(taskManager1.getEpics(), taskManager.getEpics());
        Assertions.assertEquals(taskManager1.getSubtasks(), taskManager.getSubtasks());
        Assertions.assertEquals(taskManager1.getId(), taskManager.getId());
    }

    //тест на то что изменения сохраняются в файл и восстановленный из этого файла менеджер равен исходному по полям
    @Test
    void restoringManagerFromFileAfterChangesTest() {
        taskManager.createTask(new Task("a","a"));
        taskManager.createEpic(new Epic("b", "c"));
        taskManager.createSubtask(new Subtask("c", "d"), 2);
        FileBackedTaskManager taskManager1 = FileBackedTaskManager.loadFromFile(taskManager.getFile());
        Assertions.assertEquals(taskManager1.getTasks(), taskManager.getTasks());
        Assertions.assertEquals(taskManager1.getEpics(), taskManager.getEpics());
        Assertions.assertEquals(taskManager1.getSubtasks(), taskManager.getSubtasks());
        Assertions.assertEquals(taskManager1.getId(), taskManager.getId());
        taskManager.updateSubtask(new Subtask(3, "c", "d", TaskStatus.DONE, 2));
        taskManager1 = FileBackedTaskManager.loadFromFile(taskManager.getFile());
        Assertions.assertEquals(taskManager1.getTasks(), taskManager.getTasks());
        Assertions.assertEquals(taskManager1.getEpics(), taskManager.getEpics());
        Assertions.assertEquals(taskManager1.getSubtasks(), taskManager.getSubtasks());
        Assertions.assertEquals(taskManager1.getId(), taskManager.getId());
    }
}
