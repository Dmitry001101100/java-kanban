package HttpTaskServer.handle;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enumeration.Endpoint;
import manager.Managers;
import manager.Task.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EpicHandle extends BaseHandle implements HttpHandler {


    private final TaskManager taskManager;
    private final Gson gson = Managers.getGson();

    public EpicHandle(TaskManager manager) {
        this.taskManager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPICS: {
                handleGetEpics(exchange);
                break;
            }
            case GET_EPIC: {
                handleGetEpic(exchange);
                break;
            }
            case POST_EPIC: {
                handlePostEpic(exchange);
                break;
            }
            case DELETE_EPIC:
                handleDeleteEpic(exchange);
                break;
            case GET_SUBTASK_BY_EPIC:
                handGetSubTasksByEpicId(exchange);
                break;

            default: {
                writeResponse(exchange, "Такого эндпоинта не существует в эпике.", 404);
            }
        }
    }


    private void handleGetEpics(HttpExchange exchange) throws IOException { // вывод всех задач
        List<Epic> epicList = taskManager.getEpics();
        String jsonResponse = gson.toJson(epicList);
        writeResponse(exchange, jsonResponse, 200);
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException { // вывод задачи по id

        Optional<Integer> epicIdOpt = getOptionalId(exchange);
        if (epicIdOpt.isEmpty()) {
            writeResponse(exchange, "Некорректный id эпика.", 400);
            return;
        }

        int id = epicIdOpt.get();
        String response;

        if ((taskManager.containsKeyEpic(id))) {
            Epic task = taskManager.getEpicById(id);
            String jsonResponse = gson.toJson(task);
            writeResponse(exchange, jsonResponse, 200);
        } else {
            response = "Эпик с id: " + id + " не существует.";
            writeResponse(exchange, response, 404);
        }

    }

    private void handGetSubTasksByEpicId(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOpt = getOptionalId(exchange);
        if (epicIdOpt.isEmpty()) {
            writeResponse(exchange, "Некорректный id эпика.", 400);
            return;
        }

        int id = epicIdOpt.get();
        String response;

        if ((taskManager.containsKeyEpic(id))) {
            List<SubTask> subtaskList = taskManager.getSubTasksByEpicId(id);
            String jsonResponse = gson.toJson(subtaskList);
            writeResponse(exchange, jsonResponse, 200);
        } else {
            response = "Эпик с id: " + id + " не существует.";
            writeResponse(exchange, response, 404);
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException { // сохранение и перезапись задач
        try (InputStream inputStream = exchange.getRequestBody()) {
            Optional<Epic> epicOpt = parseTask(inputStream);

            if (epicOpt.isEmpty()) {
                writeResponse(exchange, "Передан пустой запрос", 400);
                return;
            }

            Epic epic = epicOpt.get();

            if (epic.getId() == null || !taskManager.containsKeyEpic(epic.getId())) {
                taskManager.createEpic(epic);
                writeResponse(exchange, "Эпик сохранен.", 201);
            } else if (taskManager.containsKeyEpic(epic.getId())) {
                taskManager.updateEpic(epic);
                writeResponse(exchange, "Эпик обновлен.", 201);
            }

        } catch (IOException e) {
            writeResponse(exchange, "Внутренняя ошибка сервера", 500);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException { // удаление задач по id
        Optional<Integer> epicIdOpt = getOptionalId(exchange);

        if (epicIdOpt.isPresent()) {
            if (taskManager.containsKeyEpic(epicIdOpt.get())) {
                taskManager.deleteEpicId(epicIdOpt.get());
                writeResponse(exchange, "Эпик удален.", 201);
            } else {
                writeResponse(exchange, "Эпик не найден.", 400);
            }

        } else {
            writeResponse(exchange, "Неверный id эпика.", 400);
        }
    }
    // ----------------------------------------------------------------------------------------------------------------


    private Optional<Epic> parseTask(InputStream inputStream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            Epic epic = gson.fromJson(reader, Epic.class);
            return Optional.ofNullable(epic);
        }
    }
}
