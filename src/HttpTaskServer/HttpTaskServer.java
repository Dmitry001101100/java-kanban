package HttpTaskServer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpTaskServer implements HttpHandler {
    File file = new File("taskToList.csv"); // используется для проверки

    TaskManager taskManager = Managers.getDefaultFileBackedTaskManager(file);

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS: { // вывод всех задач
                handleGetTasks(exchange);
                break;
            }
            case GET_TASK: {   // вывод задачи по id
                handleGetTask(exchange);
                break;
            }
            case POST_TASK: { // запись задачи
                handlePostTask(exchange);
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

    //---------------------------------------------------------------------------------------------------------------------
    // Task
    private void handleGetTasks(HttpExchange exchange) throws IOException { // вывод всех задач
        String response = taskManager.getTasks().stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
        writeResponse(exchange, response, 200);
    }

    private void handleGetTask(HttpExchange exchange) throws IOException { // вывод задачи по id

        Optional<Integer> taskIdOpt = getOptionalId(exchange);
        if (taskIdOpt.isEmpty()) {
            writeResponse(exchange, "Некорректный id задачи.", 400);
            return;
        }

        int id = taskIdOpt.get();
        String response;

        if ((taskManager.containsKeyTask(id))) {
            response = taskManager.outIdTask(id).toString();
            writeResponse(exchange, response, 200);
        } else {
            response = "Задачи с id: " + id + " не существует.";
            writeResponse(exchange, response, 404);
        }

    }

    private void handlePostTask(HttpExchange exchange) throws IOException { // сохранение задачи
        Optional<Task> commentOpt = parseTask(exchange.getRequestBody());
        System.out.println(commentOpt);

        if (commentOpt.isEmpty()) {

            writeResponse(exchange, "Поля комментария не могут быть пустыми", 400);

        } else {
            taskManager.saveTask(commentOpt.get());
            writeResponse(exchange, "задача сохранена", 201);
        }

    }

    private Optional<Task> parseTask(InputStream bodyInputStream) throws IOException {




        BufferedReader reader = new BufferedReader(new InputStreamReader(bodyInputStream, DEFAULT_CHARSET));
        StringBuilder bodyBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            bodyBuilder.append(line).append("\n");
        }

        String body = bodyBuilder.toString().trim();
        int newLineInd = body.indexOf('\n');

        if (newLineInd <= 0 || body.length() == newLineInd + 1) {
            return Optional.empty();
        }

        String user = body.substring(0, newLineInd).trim();
        String text = body.substring(newLineInd + 1);

        System.out.println("Строка 1"+user);

        System.out.println("Строка 2"+ text);


        return Optional.of(new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(140)));
    }



    //---------------------------------------------------------------------------------------------------------------------
    private Endpoint getEndpoint(String path, String requestMethod) {
        String[] pathParts = path.split("/");
        System.out.println("0 -"+pathParts[0]);
        System.out.println("1 "+pathParts[1]);



        if (requestMethod.equals("GET")) {
            if (pathParts[2].equals("tasks")) {

                if (pathParts.length <= 3) {
                    return Endpoint.GET_TASKS;
                } else {
                    return Endpoint.GET_TASK;
                }


            }
        } else if (requestMethod.equals("POST")) {
            if (pathParts[2].equals("tasks")) {
                return Endpoint.POST_TASK;
            }
        }
        return Endpoint.DEFAULT;
    }

    private Optional<Integer> getOptionalId(HttpExchange exchange) { // проверка что id для вывода задачи является числом
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[3]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
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
