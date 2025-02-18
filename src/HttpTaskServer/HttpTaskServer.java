package HttpTaskServer;

import HttpTaskServer.adapters.DurationAdapter;
import HttpTaskServer.adapters.LocalDateTimeAdapter;
import HttpTaskServer.handle.BaseHandle;
import HttpTaskServer.handle.SubTaskHandler;
import HttpTaskServer.handle.TaskHandler;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import enumeration.Endpoint;
import enumeration.Status;
import manager.Managers;
import manager.Task.InMemoryTaskManager;
import manager.Task.TaskManager;
import tasks.Task;

import java.io.*;
import java.net.InetSocketAddress;


public class HttpTaskServer extends BaseHandle implements HttpHandler {

    File file = new File("taskToList.csv"); // используется для проверки
    TaskManager taskManager = Managers.getDefaultFileBackedTaskManager(file);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        switch (pathParts[2]) {
            case "tasks": { // вывод всех задач
                new TaskHandler(taskManager).handle(exchange);
            }
            case "subtasks": {
                new SubTaskHandler(taskManager).handle(exchange);
            }


            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

}

class MainHttpTaskServer {
    private static final int PORT = 8080;

    public static void main(String[] qw) throws IOException {

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/taskServer", new HttpTaskServer());
        httpServer.start(); // запускаем сервер

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");

    }
}
