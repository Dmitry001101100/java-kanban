package HttpTaskServer.handle;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enumeration.Endpoint;
import manager.Task.TaskManager;
import tasks.Epic;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskHandler extends BaseHandle implements HttpHandler {

    private TaskManager taskManager;
    private final Gson gson = getGson();

    public TaskHandler(TaskManager manager) {
        this.taskManager = manager;
    }

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
                writeResponse(exchange, "Такого эндпоинта не существует.", 404);
        }
    }

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
            response = taskManager.getTaskById(id).toString();
            writeResponse(exchange, response, 200);
        } else {
            response = "Задачи с id: " + id + " не существует.";
            writeResponse(exchange, response, 404);
        }

    }

    private void handlePostTask(HttpExchange exchange) throws IOException { // сохранение и перезапись задач
        try (InputStream inputStream = exchange.getRequestBody()) {
            Optional<Task> taskOpt = parseTask(inputStream);

            if (taskOpt.isEmpty()) {
                writeResponse(exchange, "Передан пустой запрос.", 400);
                return;
            }

            Task task = taskOpt.get();

            if (task.getId() == null || !taskManager.containsKeyTask(task.getId())) {
                taskManager.createTask(task);
                writeResponse(exchange, "Задача сохранена.", 201);
            } else if (taskManager.containsKeyTask(task.getId())) {
                taskManager.updateTask(task);
                writeResponse(exchange, "Задача обновлена.", 201);
            }

        } catch (IOException e) {
            writeResponse(exchange, "Внутренняя ошибка сервера", 500);
            e.printStackTrace();
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException { // удаление задач по id
        Optional<Integer> taskIdOpt = getOptionalId(exchange);

        if (taskIdOpt.isPresent()) {
            if (taskManager.containsKeyTask(taskIdOpt.get())) {
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
}
