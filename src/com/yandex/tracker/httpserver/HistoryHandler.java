package com.yandex.tracker.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.tracker.model.Endpoint;
import com.yandex.tracker.servise.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoint endpoint = getEndpoint(httpExchange);
        switch (endpoint) {
            case GET_HISTORY -> sendText(httpExchange, SUCCESSFULLY_CODE, HttpTaskServer.getGson().toJson(getManager().getHistory()));
            case UNKNOWN -> sendUnknownEndpoint(httpExchange);
        }
    }
}
