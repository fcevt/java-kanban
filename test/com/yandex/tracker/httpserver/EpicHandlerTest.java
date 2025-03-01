package com.yandex.tracker.httpserver;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.TaskStatus;
import com.yandex.tracker.servise.InMemoryTaskManager;
import com.yandex.tracker.servise.NonExistingTaskException;
import com.yandex.tracker.servise.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EpicHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    @BeforeEach
    void startServer() throws IOException {
        taskServer.start();
    }

    @AfterEach
    void stop() {
        manager.removeEpics();
        taskServer.stop();
    }

    @Test
    void getListEpicSubtasksTest() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic(2,"ttttttt", "yyyyyyyy",
                TaskStatus.NEW, new ArrayList<>())).get();
        Epic epic1 = manager.createEpic(new Epic(3,"uuuuuuu",
                "iiiiiiiii", TaskStatus.NEW, new ArrayList<>())).get();
        Subtask subtask = manager.createSubtask(new Subtask(4,"oooooooooo",
                "ppppppppp", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 9, 0),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();
        Subtask subtask2 = manager.createSubtask(new Subtask(5,"aaaaaaaaaaaaa",
                "sssssssssss", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 6, 30),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();
        Subtask subtask3 = manager.createSubtask(new Subtask(6,"ddddddd",
                "ffffffffff", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 8, 0),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(manager.getListOfEpicSubtask(epic.getId()), HttpTaskServer.getGson().fromJson(response.body(),
                new SubtaskListTypeToken().getType()));
    }

    @Test
    void getEpicsTest() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic(2,"ttttttt", "yyyyyyyy",
                TaskStatus.NEW, new ArrayList<>())).get();
        Epic epic1 = manager.createEpic(new Epic(3,"uuuuuuu",
                "iiiiiiiii", TaskStatus.NEW, new ArrayList<>())).get();
        Subtask subtask = manager.createSubtask(new Subtask(4,"oooooooooo",
                "ppppppppp", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 9, 0),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();
        Subtask subtask2 = manager.createSubtask(new Subtask(5,"aaaaaaaaaaaaa",
                "sssssssssss", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 6, 30),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();
        Subtask subtask3 = manager.createSubtask(new Subtask(6,"ddddddd",
                "ffffffffff", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 8, 0),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(manager.getListOfEpics(), HttpTaskServer.getGson().fromJson(response.body(), new EpicListTypeToken().getType()));
    }

    @Test
    void getExistingEpicByIdTest() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic(2,"ttttttt", "yyyyyyyy",
                TaskStatus.NEW, new ArrayList<>())).get();
        Epic epic1 = manager.createEpic(new Epic(3,"uuuuuuu",
                "iiiiiiiii", TaskStatus.NEW, new ArrayList<>())).get();
        Subtask subtask = manager.createSubtask(new Subtask(4,"oooooooooo",
                "ppppppppp", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 9, 0),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();
        Subtask subtask2 = manager.createSubtask(new Subtask(5,"aaaaaaaaaaaaa",
                "sssssssssss", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 6, 30),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();
        Subtask subtask3 = manager.createSubtask(new Subtask(6,"ddddddd",
                "ffffffffff", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 8, 0),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(manager.getEpicById(epic.getId()), HttpTaskServer.getGson().fromJson(response.body(), Epic.class));
    }

    @Test
    void getNotExistingEpicByIdTest() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic(2,"ttttttt", "yyyyyyyy",
                TaskStatus.NEW, new ArrayList<>())).get();
        Epic epic1 = manager.createEpic(new Epic(3,"uuuuuuu",
                "iiiiiiiii", TaskStatus.NEW, new ArrayList<>())).get();
        Subtask subtask = manager.createSubtask(new Subtask(4,"oooooooooo",
                "ppppppppp", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 9, 0),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();
        Subtask subtask2 = manager.createSubtask(new Subtask(5,"aaaaaaaaaaaaa",
                "sssssssssss", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 6, 30),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();
        Subtask subtask3 = manager.createSubtask(new Subtask(6,"ddddddd",
                "ffffffffff", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 8, 0),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + 26);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void deleteEpicByIdTest() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic(2,"ttttttt", "yyyyyyyy",
                TaskStatus.NEW, new ArrayList<>())).get();
        Epic epic1 = manager.createEpic(new Epic(3,"uuuuuuu",
                "iiiiiiiii", TaskStatus.NEW, new ArrayList<>())).get();
        Subtask subtask = manager.createSubtask(new Subtask(4,"oooooooooo",
                "ppppppppp", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 9, 0),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();
        Subtask subtask2 = manager.createSubtask(new Subtask(5,"aaaaaaaaaaaaa",
                "sssssssssss", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 6, 30),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();
        Subtask subtask3 = manager.createSubtask(new Subtask(6,"ddddddd",
                "ffffffffff", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 8, 0),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertThrows(NonExistingTaskException.class, () -> manager.getEpicById(epic1.getId()));
    }

    @Test
    void postEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(2,"ttttttt", "yyyyyyyy", TaskStatus.NEW, new ArrayList<>());
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(HttpTaskServer.getGson().toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getListOfEpics().size());
        assertEquals(epic.getName(), manager.getListOfEpics().getFirst().getName());
    }
}
