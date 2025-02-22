package com.yandex.tracker.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.tracker.model.Endpoint;
import com.yandex.tracker.servise.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoint endpoint = getEndpoint(httpExchange);
        switch (endpoint) {
            case GET_PRIORITIZED_TASKS -> sendText(httpExchange, SUCCESSFULLY_CODE, getGson()
                    .toJson(getManager().getPrioritizedTasks()));
            case UNKNOWN -> sendUnknownEndpoint(httpExchange);

        }
    }
}
