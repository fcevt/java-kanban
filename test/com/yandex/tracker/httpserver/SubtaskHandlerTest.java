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

public class SubtaskHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    @BeforeEach
    void startServer() throws IOException {
        taskServer.start();
    }

    @AfterEach
    void stop() {
        manager.removeSubtasks();
        manager.removeEpics();
        taskServer.stop();
    }

    @Test
    void getSubtasksTest() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic(2,"ttttttt", "yyyyyyyy",
                TaskStatus.NEW, new ArrayList<>())).get();
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
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(manager.getListOfSubtasks(), HttpTaskServer.getGson().fromJson(response.body(), new SubtaskListTypeToken().getType()));
    }

    @Test
    void getExistingSubtaskByIdTest() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic(2,"ttttttt", "yyyyyyyy",
                TaskStatus.NEW, new ArrayList<>())).get();
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
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(manager.getSubtasksById(subtask.getId()), HttpTaskServer.getGson().fromJson(response.body(), Subtask.class));
    }

    @Test
    void getNotExistingSubtaskByIdTest() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic(2,"ttttttt", "yyyyyyyy",
                TaskStatus.NEW, new ArrayList<>())).get();
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
        URI url = URI.create("http://localhost:8080/subtasks/" + 26);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void deleteSubtaskByIdTest() throws IOException, InterruptedException {
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
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertThrows(NonExistingTaskException.class, () -> manager.getSubtasksById(subtask.getId()));
    }

    @Test
    void postSubtask() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic(2,"ttttttt", "yyyyyyyy",
                TaskStatus.NEW, new ArrayList<>())).get();
        Subtask subtask = new Subtask(4,"oooooooooo",
                "ppppppppp", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 9, 0),
                Duration.ofMinutes(30), epic.getId());
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(HttpTaskServer.getGson().toJson(subtask)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getListOfSubtasks().size());
        assertEquals(subtask.getName(), manager.getListOfSubtasks().getFirst().getName());
    }

    @Test
    void postSubtaskWithIntersection() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic(2,"ttttttt", "yyyyyyyy",
                TaskStatus.NEW, new ArrayList<>())).get();
        manager.createSubtask(new Subtask(4,"oooooooooo",
                "ppppppppp", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 9, 0),
                Duration.ofMinutes(30), epic.getId()), epic.getId());
        Subtask subtask = new Subtask(4,"new",
                "sub", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 9, 0),
                Duration.ofMinutes(30), epic.getId());
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(HttpTaskServer.getGson().toJson(subtask)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        assertEquals(2, manager.getListOfSubtasks().size());
    }

    @Test
    void updateSubtask() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic(2,"ttttttt", "yyyyyyyy",
                TaskStatus.NEW, new ArrayList<>())).get();
        Subtask subtask1 = manager.createSubtask(new Subtask(4,"oooooooooo",
                "ppppppppp", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 9, 0),
                Duration.ofMinutes(30), epic.getId()), epic.getId()).get();
        Subtask subtask = new Subtask(subtask1.getId(),"new",
                "sub", TaskStatus.DONE,
                LocalDateTime.of(2025, 1, 1, 9, 0),
                Duration.ofMinutes(30), epic.getId());
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(HttpTaskServer.getGson().toJson(subtask)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getListOfSubtasks().size());
        assertEquals(subtask, manager.getListOfSubtasks().getFirst());
    }
}
