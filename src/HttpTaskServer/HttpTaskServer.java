package HttpTaskServer;

import HttpTaskServer.adapters.DurationAdapter;
import HttpTaskServer.adapters.LocalDateTimeAdapter;
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
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpTaskServer implements HttpHandler {
    File file = new File("taskToList.csv"); // используется для проверки

    protected final Gson gson = getGson();
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");
    ;

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
            case POST_TASK: { // запись задачи и обновление задачи
                handlePostTask(exchange);
                break;
            }
            case DELETE_TASK: { // удаление по id задачи
                handleDeleteTask(exchange);
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

    private void handlePostTask(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            Optional<Task> taskOpt = parseTask(inputStream);

            if (taskOpt.isEmpty()) {
                writeResponse(exchange, "Передан пустой запрос", 400);
                return;
            }

            Task task = taskOpt.get();
            Optional<Integer> taskIdOpt = getOptionalId(exchange);
            //  System.out.println("taskopt - " + taskIdOpt.orElse(-1));

            if (taskIdOpt.isPresent()) {
                int taskId = taskIdOpt.get();
                if (task.getId() == null) {
                    task.setId(taskId);
                }

                if (!taskManager.containsKeyTask(taskId)) {
                    writeResponse(exchange, "Задачи с таким id не существует", 404);
                } else {
                    taskManager.updateTask(task);
                    writeResponse(exchange, "Задача обновлена.", 201);
                }
            } else {
                if (task.getId() == null) {
                    taskManager.saveTask(task);
                    writeResponse(exchange, "Задача сохранена", 201);
                } else if (taskManager.containsKeyTask(task.getId())) {
                    writeResponse(exchange, "Задача пересекается с существующей", 406);
                } else {
                    taskManager.saveTask(task);
                    writeResponse(exchange, "Задача сохранена", 201);
                }
            }
        } catch (IOException e) {
            writeResponse(exchange, "Внутренняя ошибка сервера", 500);
            e.printStackTrace();
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getOptionalId(exchange);

        if (taskIdOpt.isPresent()) {
            if(taskManager.containsKeyTask(taskIdOpt.get())){
                taskManager.deleteTaskId(taskIdOpt.get());
                writeResponse(exchange, "Задача удалена.", 201);
            } else {
                writeResponse(exchange, "Задача не найдена.", 400);
            }

        } else {
            writeResponse(exchange, "Неверный id задачи.", 400);
        }
    }
    // ----------------------------------------------------------------------------------------------------------------
    private Optional<Task> parseTask(InputStream inputStream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            Task task = gson.fromJson(reader, Task.class);
            return Optional.ofNullable(task);
        }
    }


    //---------------------------------------------------------------------------------------------------------------------
    private Endpoint getEndpoint(String path, String requestMethod) {
        String[] pathParts = path.split("/");
        System.out.println("0 -" + pathParts[0]);
        System.out.println("1 " + pathParts[1]);


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
        } else if (requestMethod.equals("DELETE")) {
            if (pathParts[2].equals("tasks")) {

                if (pathParts.length >= 3) {
                    return Endpoint.DELETE_TASK;
                }


            }
        }
        return Endpoint.DEFAULT;
    }


    private Optional<Integer> getOptionalId(HttpExchange exchange) { // проверка что id для вывода задачи является числом
        String path = exchange.getRequestURI().getPath();
        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            try {
                String idStr = path.substring(lastSlashIndex + 1);
                return Optional.of(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                // Не удалось преобразовать в Integer, значит id отсутствует или некорректен
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
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
