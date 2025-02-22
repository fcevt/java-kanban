package com.yandex.tracker.httpserver;

import com.sun.net.httpserver.HttpServer;
import com.yandex.tracker.servise.*;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager manager;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
    }

    public HttpTaskServer() {
        manager = Managers.getDefault();
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(manager));
        httpServer.createContext("/subtasks", new SubtasksHandler(manager));
        httpServer.createContext("/epics", new EpicsHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }
}