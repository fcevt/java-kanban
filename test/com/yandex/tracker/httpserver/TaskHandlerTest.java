package com.yandex.tracker.httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.tracker.model.Task;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TaskHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .create();

    @BeforeEach
    void startServer() throws IOException {
        taskServer.start();
    }

    @AfterEach
    void stop() {
        manager.removeTasks();
        taskServer.stop();
    }

    @Test
    void getTasksTest() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task(0, "qqqqqq", "wwwwww", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(30))).get();
        Task task1 = manager.createTask(new Task(1, "eeee", "rrr",
                TaskStatus.NEW, LocalDateTime.of(2025, 1, 1, 10, 30),
                Duration.ofMinutes(30))).get();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(manager.getListOfTasks(), gson.fromJson(response.body(), new TaskListTypeToken().getType()));
    }

    @Test
    void getExistingTaskByIdTest() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task(0, "qqqqqq", "wwwwww", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(30))).get();
        Task task1 = manager.createTask(new Task(1, "eeee", "rrr",
                TaskStatus.NEW, LocalDateTime.of(2025, 1, 1, 10, 30),
                Duration.ofMinutes(30))).get();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(manager.getTaskById(task.getId()), gson.fromJson(response.body(), Task.class));
    }

    @Test
    void getNotExistingTaskByIdTest() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task(0, "qqqqqq", "wwwwww", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(30))).get();
        Task task1 = manager.createTask(new Task(1, "eeee", "rrr",
                TaskStatus.NEW, LocalDateTime.of(2025, 1, 1, 10, 30),
                Duration.ofMinutes(30))).get();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + 26);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void deleteTaskByIdTest() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task(0, "qqqqqq", "wwwwww", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(30))).get();
        Task task1 = manager.createTask(new Task(1, "eeee", "rrr",
                TaskStatus.NEW, LocalDateTime.of(2025, 1, 1, 10, 30),
                Duration.ofMinutes(30))).get();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertThrows(NonExistingTaskException.class, () -> manager.getTaskById(task.getId()));
    }

    @Test
    void postTask() throws IOException, InterruptedException {
        Task task1 = new Task(1, "eeee", "rrr",
                TaskStatus.NEW, LocalDateTime.of(2025, 1, 1, 10, 30),
                Duration.ofMinutes(30));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getListOfTasks().size());
        assertEquals(task1, manager.getListOfTasks().getFirst());
    }

    @Test
    void postTaskWithIntersection() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task(0, "qqqqqq", "wwwwww", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(30))).get();
        Task task1 = new Task(1, "eeee", "rrr",
                TaskStatus.NEW, LocalDateTime.of(2025, 1, 1, 10, 0),
                Duration.ofMinutes(20));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        assertEquals(2, manager.getListOfTasks().size());
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task(0, "qqqqqq", "wwwwww", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(30))).get();
        Task task1 = new Task(task.getId(), "new", "Task", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(30));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getListOfTasks().size());
        assertEquals(task1, manager.getListOfTasks().getFirst());
    }
}
