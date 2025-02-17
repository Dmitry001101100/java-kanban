package HttpTaskServer.handle;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enumeration.Endpoint;
import manager.Task.TaskManager;

import java.io.IOException;

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


            default:
                writeResponse(exchange, "Такого эндпоинта не существует.", 404);
        }
    }
}
