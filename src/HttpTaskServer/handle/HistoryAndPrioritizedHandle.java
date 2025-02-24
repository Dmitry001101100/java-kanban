package HttpTaskServer.handle;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enumeration.Endpoint;
import manager.Managers;
import manager.Task.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryAndPrioritizedHandle extends BaseHandle implements HttpHandler {
    private final TaskManager taskManager;
    Gson gson = Managers.getGson();

    public HistoryAndPrioritizedHandle(TaskManager manager) {
        this.taskManager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_PRIORITIZED: {
                handleGetPrioritizedTasks(exchange);
                break;
            }
            case GET_HISTORY: {
                handleGetHistory(exchange);
                break;
            }
            default: {
                writeResponse(exchange, "Такого эндпоинта не существует в эпике.", 404);
            }
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getHistory();
        String jsonResponse = gson.toJson(tasks);
        writeResponse(exchange, jsonResponse, 200);
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getPrioritizedTasks();
        String jsonResponse = gson.toJson(tasks);
        writeResponse(exchange, jsonResponse, 200);
    }
}