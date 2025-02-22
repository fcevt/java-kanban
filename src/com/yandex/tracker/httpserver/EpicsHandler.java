package com.yandex.tracker.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.tracker.model.Endpoint;
import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.servise.NonExistingTaskException;
import com.yandex.tracker.servise.TaskManager;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {


    public EpicsHandler(TaskManager manager) {
       super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoint endpoint = getEndpoint(httpExchange);
        switch (endpoint) {
            case GET_EPICS -> {
                List<Epic> epics = getManager().getListOfEpics();
                if (epics.isEmpty()) {
                    sendText(httpExchange, SUCCESSFULLY_CODE, "Эпиков пока нет");
                } else {
                    sendText(httpExchange, SUCCESSFULLY_CODE, getGson().toJson(getManager().getListOfEpics()));
                }
            }
            case GET_EPIC_BY_ID -> {
                try {
                    Epic epic = getManager().getEpicById(getIdFromPath(httpExchange));
                    sendText(httpExchange, SUCCESSFULLY_CODE, getGson().toJson(epic));
                } catch (NonExistingTaskException e) {
                    sendNotFound(httpExchange, e);
                }
            }
            case GET_EPIC_SUBTASKS -> {
               try {
                   Epic epic = getManager().getEpicById(getIdFromPath(httpExchange));
                   List<Subtask> subtaskList = getManager().getListOfEpicSubtask(epic.getId());
                   if (subtaskList.isEmpty()) {
                       sendText(httpExchange, SUCCESSFULLY_CODE, "У этого эпика нет подзадач");
                   } else {
                       sendText(httpExchange, SUCCESSFULLY_CODE, getGson().toJson(subtaskList));
                   }
               } catch (NonExistingTaskException e) {
                   sendNotFound(httpExchange, e);
               }
            }
            case DELETE_EPIC_BY_ID -> {
                getManager().deleteEpicById(getIdFromPath(httpExchange));
                sendText(httpExchange, SUCCESSFULLY_CODE, getGson().toJson("Эпик удален"));
            }
            case POST_CREATE_EPIC -> {
                String jsonEpic = new String(httpExchange.getRequestBody().readAllBytes());
                Epic epic = getGson().fromJson(jsonEpic, Epic.class);
                getManager().createEpic(epic);
                sendText(httpExchange, SUCCESSFULLY_UPD_OR_CREATE_CODE, "Эпик создан");
            }
            case UNKNOWN -> sendUnknownEndpoint(httpExchange);
        }
    }
}
