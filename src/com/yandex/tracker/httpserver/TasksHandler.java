package com.yandex.tracker.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.tracker.model.Endpoint;
import com.yandex.tracker.model.Task;
import com.yandex.tracker.servise.NonExistingTaskException;
import com.yandex.tracker.servise.TaskManager;
import com.yandex.tracker.servise.TimeIntersectionException;

import java.io.IOException;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    public TasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoint endpoint = getEndpoint(httpExchange);
        switch (endpoint) {
            case GET_TASKS -> {
                List<Task> tasks = getManager().getListOfTasks();
                if (tasks.isEmpty()) {
                    sendText(httpExchange, SUCCESSFULLY_CODE, "Задач пока нет");
                } else {
                    sendText(httpExchange, SUCCESSFULLY_CODE, getGson().toJson(tasks));
                }
            }
            case GET_TASK_BY_ID -> {
                try {
                    Task task = getManager().getTaskById(getIdFromPath(httpExchange));
                    sendText(httpExchange, SUCCESSFULLY_CODE, getGson().toJson(task));
                } catch (NonExistingTaskException e) {
                    sendNotFound(httpExchange, e);
                }
            }
            case DELETE_TASK_BY_ID -> {
                getManager().deleteTaskById(getIdFromPath(httpExchange));
                sendText(httpExchange, SUCCESSFULLY_CODE, getGson().toJson("Задача удалена"));
            }
            case POST_CREATE_TASK -> {
                try {
                    String jsonTask = new String(httpExchange.getRequestBody().readAllBytes());
                    Task task = getGson().fromJson(jsonTask, Task.class);
                    getManager().createTask(task);
                    sendText(httpExchange, SUCCESSFULLY_UPD_OR_CREATE_CODE, "Задача создана");
                } catch (TimeIntersectionException exception) {
                    sendHasInteractions(httpExchange, exception);
                }
            }
            case POST_UPDATE_TASK -> {
                try {
                    String jsonTask = new String(httpExchange.getRequestBody().readAllBytes());
                    Task task = getGson().fromJson(jsonTask, Task.class);
                    getManager().updateTask(task);
                    sendText(httpExchange, SUCCESSFULLY_UPD_OR_CREATE_CODE, "Задача обновлена");
                } catch (NonExistingTaskException exception) {
                    sendNotFound(httpExchange, exception);
                } catch (TimeIntersectionException exception) {
                    sendHasInteractions(httpExchange, exception);
                }
            }
            case UNKNOWN -> sendUnknownEndpoint(httpExchange);
        }
    }
}
