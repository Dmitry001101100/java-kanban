package HttpTaskServer.handle;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enumeration.Endpoint;
import manager.Task.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubTaskHandler extends BaseHandle implements HttpHandler {

    private TaskManager taskManager;
    private final Gson gson = getGson();

    public SubTaskHandler(TaskManager manager) {
        this.taskManager = manager;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASKS: {
                handleGetSubTasks(exchange);
                break;
            } case GET_SUBTASK: {
                handleGetSubTask(exchange);
                break;
            }


            default:
                writeResponse(exchange, "Такого эндпоинта не существует.", 404);
        }
    }

    private void handleGetSubTasks(HttpExchange exchange) throws IOException { // вывод всех задач
        String response = taskManager.getSubTasks().stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
        writeResponse(exchange, response, 200);
    }

    private void handleGetSubTask(HttpExchange exchange) throws IOException { // вывод всех задач
        Optional<Integer> subTaskIdOpt = getOptionalId(exchange);
        if (subTaskIdOpt.isEmpty()) {
            writeResponse(exchange, "Некорректный id задачи.", 400);
            return;
        }

        int id = subTaskIdOpt.get();
        String response;

        if ((taskManager.containsKeySubTask(id))) {
            response = taskManager.getSubTaskById(id).toString();
            writeResponse(exchange, response, 200);
        } else {
            response = "Подзадачи с id: " + id + " не существует.";
            writeResponse(exchange, response, 404);
        }
    }
}
