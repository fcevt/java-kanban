package com.yandex.tracker.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.tracker.model.Endpoint;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.servise.NonExistingTaskException;
import com.yandex.tracker.servise.TaskManager;
import com.yandex.tracker.servise.TimeIntersectionException;

import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    public SubtasksHandler(TaskManager manager) {
       super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoint endpoint = getEndpoint(httpExchange);
        switch (endpoint) {
            case GET_SUBTASKS -> {
                List<Subtask> subtasks = getManager().getListOfSubtasks();
                if (subtasks.isEmpty()) {
                    sendText(httpExchange, SUCCESSFULLY_CODE, "Подзадач пока нет");
                } else {
                    sendText(httpExchange, SUCCESSFULLY_CODE, getGson().toJson(subtasks));
                }
            }
            case GET_SUBTASK_BY_ID -> {
                try {
                    Subtask subtask = getManager().getSubtasksById(getIdFromPath(httpExchange));
                    sendText(httpExchange, SUCCESSFULLY_CODE, getGson().toJson(subtask));
                } catch (NonExistingTaskException e) {
                    sendNotFound(httpExchange, e);
                }
            }
            case DELETE_SUBTASK_BY_ID -> {
                getManager().deleteSubtaskById(getIdFromPath(httpExchange));
                sendText(httpExchange, SUCCESSFULLY_CODE, getGson().toJson("Подзадача удалена"));
            }
            case POST_CREATE_SUBTASK -> {
               try {
                   String jsonSubtask = new String(httpExchange.getRequestBody().readAllBytes());
                   Subtask subtask = getGson().fromJson(jsonSubtask, Subtask.class);
                   getManager().createSubtask(subtask, subtask.getEpicId());
                   sendText(httpExchange, SUCCESSFULLY_UPD_OR_CREATE_CODE, "Подзадача создана");
               } catch (TimeIntersectionException exception) {
                   sendHasInteractions(httpExchange, exception);
               }
            }
            case POST_UPDATE_SUBTASK -> {
                try {
                    String jsonSubtask = new String(httpExchange.getRequestBody().readAllBytes());
                    Subtask subtask = getGson().fromJson(jsonSubtask, Subtask.class);
                    getManager().updateSubtask(subtask);
                    sendText(httpExchange, SUCCESSFULLY_UPD_OR_CREATE_CODE, "Подзадача обновлена");
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
