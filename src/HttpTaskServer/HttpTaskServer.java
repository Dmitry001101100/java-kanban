package HttpTaskServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import enumeration.Endpoint;
import manager.Managers;
import manager.Task.InMemoryTaskManager;
import manager.Task.TaskManager;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class HttpTaskServer implements HttpHandler {
    File file = new File("taskToList.csv"); // используется для проверки

    TaskManager taskManager = Managers.getDefaultFileBackedTaskManager(file);

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS: {
                handleGetTasks(exchange);
                break;
            }

            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String response = taskManager.getTasks().stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
        writeResponse(exchange, response, 200);
    }

    private Endpoint getEndpoint(String path, String requestMethod) {
        return Endpoint.GET_TASKS;
    }
}

class MainHttpTaskServer{
    private static final int PORT = 8080;

    public static void main(String[]qw) throws IOException {


        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/server",new HttpTaskServer());
        httpServer.start(); // запускаем сервер

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");


    }
}
