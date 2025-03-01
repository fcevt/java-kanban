package com.yandex.tracker.httpserver;


import com.yandex.tracker.model.Task;
import com.yandex.tracker.model.TaskStatus;
import com.yandex.tracker.servise.InMemoryTaskManager;
import com.yandex.tracker.servise.TaskManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrioritizedHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    @Test
    void returnPrioritizedTasksTest() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task(0, "qqqqqq", "wwwwww", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(30))).get();
        Task task1 = manager.createTask(new Task(1, "eeee", "rrr",
                TaskStatus.NEW, LocalDateTime.of(2025, 1, 1, 10, 30),
                Duration.ofMinutes(30))).get();
        taskServer.start();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(manager.getPrioritizedTasks(), HttpTaskServer.getGson().fromJson(response.body(), new TaskListTypeToken().getType()));
        taskServer.stop();
    }
}
