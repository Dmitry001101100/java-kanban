package HttpTaskServer.handle;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enumeration.Endpoint;
import manager.Task.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.stream.Collectors;

public class HistoryAndPrioritizedHandle extends BaseHandle implements HttpHandler {
    private final TaskManager taskManager;

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