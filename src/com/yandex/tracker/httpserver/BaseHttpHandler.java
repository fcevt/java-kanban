package com.yandex.tracker.httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.tracker.model.Endpoint;
import com.yandex.tracker.servise.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {
    public static final int TIME_INTERACTIONS_CODE = 406;
    public static final int NOT_EXISTING_CODE = 404;
    public static final int SUCCESSFULLY_UPD_OR_CREATE_CODE = 201;
    public static final int SUCCESSFULLY_CODE = 200;
    public static final int BAD_REQUEST_ERROR = 400;
    private final Gson gson;
    private final TaskManager manager;

    protected BaseHttpHandler(TaskManager manager) {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
                .create();
        this.manager = manager;
    }

    public void sendText(HttpExchange exchange, int code, String body) throws IOException {
        exchange.sendResponseHeaders(code, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void sendNotFound(HttpExchange exchange, NonExistingTaskException exception) throws IOException {
        exchange.sendResponseHeaders(NOT_EXISTING_CODE, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(exception.getMessage().getBytes(StandardCharsets.UTF_8));
        }
    }

    public void sendHasInteractions(HttpExchange exchange, TimeIntersectionException exception) throws IOException {
        exchange.sendResponseHeaders(TIME_INTERACTIONS_CODE, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(exception.getMessage().getBytes(StandardCharsets.UTF_8));
        }
    }

    public void sendUnknownEndpoint(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(BAD_REQUEST_ERROR, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write("Некорректно составленный запрос".getBytes(StandardCharsets.UTF_8));
        }
    }

    public Gson getGson() {
        return gson;
    }

    public TaskManager getManager() {
        return manager;
    }

    public Endpoint getEndpoint(HttpExchange httpExchange) {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        String path = pathParts[1];
        String requestMethod = httpExchange.getRequestMethod();

        if (pathParts.length == 2 && path.equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKS;
            } else if (requestMethod.equals("POST")) {
                return Endpoint.POST_CREATE_TASK;
            }
        } else if (pathParts.length == 3 && path.equals("tasks")) {
            switch (requestMethod) {
                case "GET" -> {
                    return Endpoint.GET_TASK_BY_ID;
                }
                case "POST" -> {
                    return Endpoint.POST_UPDATE_TASK;
                }
                case "DELETE" -> {
                    return Endpoint.DELETE_TASK_BY_ID;
                }
            }
        } else if (pathParts.length == 2 && path.equals("subtasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASKS;
            } else if (requestMethod.equals("POST")) {
                return Endpoint.POST_CREATE_SUBTASK;
            }
        } else if (pathParts.length == 3 && path.equals("subtasks")) {
            switch (requestMethod) {
                case "GET" -> {
                    return Endpoint.GET_SUBTASK_BY_ID;
                }
                case "POST" -> {
                    return Endpoint.POST_UPDATE_SUBTASK;
                }
                case "DELETE" -> {
                    return Endpoint.DELETE_SUBTASK_BY_ID;
                }
            }
        } else if (pathParts.length == 2 && path.equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPICS;
            } else if (requestMethod.equals("POST")) {
                return Endpoint.POST_CREATE_EPIC;
            }
        } else if (pathParts.length == 3 && path.equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPIC_BY_ID;
            } else if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_EPIC_BY_ID;
            }
        } else if (pathParts.length == 4 && path.equals("epics") && requestMethod.equals("GET")) {
            return Endpoint.GET_EPIC_SUBTASKS;
        } else if (pathParts.length == 2 && path.equals("history") && requestMethod.equals("GET")) {
            return Endpoint.GET_HISTORY;
        } else if (pathParts.length == 2 && path.equals("prioritized") && requestMethod.equals("GET")) {
            return Endpoint.GET_PRIORITIZED_TASKS;
        }
        return Endpoint.UNKNOWN;
    }

    public int getIdFromPath(HttpExchange exchange) {
        String pathPart = exchange.getRequestURI().getPath().split("/")[2];
        return Integer.parseInt(pathPart);
    }
}