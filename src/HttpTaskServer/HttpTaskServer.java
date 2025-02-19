package HttpTaskServer;

import HttpTaskServer.handle.BaseHandle;
import HttpTaskServer.handle.EpicHandle;
import HttpTaskServer.handle.SubTaskHandler;
import HttpTaskServer.handle.TaskHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.Task.TaskManager;
import tasks.Task;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.stream.Collectors;

public class HttpTaskServer extends BaseHandle implements HttpHandler {

    File file = new File("taskToList.csv"); // используется для проверки
    TaskManager taskManager = Managers.getDefaultFileBackedTaskManager(file);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        switch (pathParts[2]) {
            case "tasks": // вывод всех задач
                new TaskHandler(taskManager).handle(exchange);
                break;

            case "subtasks": try {
                new SubTaskHandler(taskManager).handle(exchange);
                break;
            } catch (Exception e){
                System.out.println(e);
                e.printStackTrace();
            }
            case "epics" :
                new EpicHandle(taskManager).handle(exchange);
                break;
            case "history":
                handleGetHistory(exchange);
                break;
            case "prioritized":
                handleGetPrioritizedTasks(exchange);
                break;

            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
                break;
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        String response = taskManager.getHistory().stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
        writeResponse(exchange, response, 200);
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        String response = taskManager.getPrioritizedTasks().stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
        writeResponse(exchange, response, 200);
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
