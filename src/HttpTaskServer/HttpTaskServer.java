package HttpTaskServer;

import HttpTaskServer.handle.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.Task.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer extends BaseHandle implements HttpHandler {

    private final File file = new File("taskToList.csv"); // используется для проверки
    private final TaskManager taskManager = Managers.getDefaultFileBackedTaskManager(file);
    private HttpServer httpServer; // добавляем поле для хранения экземпляра сервера

    public void startServer(int port) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/taskServer", this);
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + port + " порту!");
    }

    public void stopServer() {
        if (httpServer != null) {
            httpServer.stop(0); // останавливаем сервер
            System.out.println("HTTP-сервер остановлен.");
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        switch (pathParts[2]) {
            case "tasks": // вывод всех задач
                new TaskHandler(taskManager).handle(exchange);
                break;

            case "subtasks":
                new SubTaskHandler(taskManager).handle(exchange);
                break;
            case "epics":
                new EpicHandle(taskManager).handle(exchange);
                break;
            case "history", "prioritized":
                new HistoryAndPrioritizedHandle(taskManager).handle(exchange);
                break;
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
                break;
        }
    }


}

class MainHttpTaskServer {
    private static final int PORT = 8080;


    MainHttpTaskServer() throws IOException {
    }

    public static void main(String[] qw) throws IOException {

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
      //  httpServer.createContext("/taskServer", new HttpTaskServer());
        httpServer.start(); // запускаем сервер

       // httpServer.stop(0);

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");

    }
}
