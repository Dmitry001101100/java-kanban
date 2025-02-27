package server.handle;

import com.sun.net.httpserver.HttpExchange;
import enumeration.Endpoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHandle {


    protected Optional<Integer> getOptionalId(HttpExchange exchange) { // проверка, что id для вывода задачи является числом
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        try {
            String idStr = pathParts[3];
            return Optional.of(Integer.parseInt(idStr));
        } catch (NumberFormatException e) {
            // Не удалось преобразовать в Integer, значит id отсутствует или некорректен
            return Optional.empty();
        }
    }

    protected void writeResponse(HttpExchange exchange,
                                 String text,
                                 int responseCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected Endpoint getEndpoint(String path, String requestMethod) { // переделал метод.
        String[] pathParts = path.split("/");
        String resource = pathParts[2];
        int pathLength = pathParts.length;

        // Определяем константы для типов ресурсов
        final String TASKS = "tasks";
        final String SUBTASKS = "subtasks";
        final String EPICS = "epics";
        final String HISTORY = "history";
        final String PRIORITIZED = "prioritized";

        switch (resource) {
            case TASKS:
                return switch (requestMethod) {
                    case "GET" -> pathLength == 3 ? Endpoint.GET_TASKS : Endpoint.GET_TASK;
                    case "POST" -> Endpoint.POST_TASK;
                    case "DELETE" -> pathLength == 4 ? Endpoint.DELETE_TASK : Endpoint.DEFAULT;
                    default -> Endpoint.DEFAULT;
                };
            case SUBTASKS:
                return switch (requestMethod) {
                    case "GET" -> pathLength == 3 ? Endpoint.GET_SUBTASKS : Endpoint.GET_SUBTASK;
                    case "POST" -> Endpoint.POST_SUBTASK;
                    case "DELETE" -> pathLength == 4 ? Endpoint.DELETE_SUBTASK : Endpoint.DEFAULT;
                    default -> Endpoint.DEFAULT;
                };
            case EPICS:
                return switch (requestMethod) {
                    case "GET" -> {
                        if (pathLength == 5 && pathParts[4].equals(SUBTASKS)) {
                            yield Endpoint.GET_SUBTASK_BY_EPIC;
                        }
                        yield pathLength == 3 ? Endpoint.GET_EPICS : Endpoint.GET_EPIC;
                    }
                    case "POST" -> Endpoint.POST_EPIC;
                    case "DELETE" -> pathLength == 4 ? Endpoint.DELETE_EPIC : Endpoint.DEFAULT;
                    default -> Endpoint.DEFAULT;
                };
            case HISTORY:
                return requestMethod.equals("GET") ? Endpoint.GET_HISTORY : Endpoint.DEFAULT;
            case PRIORITIZED:
                return requestMethod.equals("GET") ? Endpoint.GET_PRIORITIZED : Endpoint.DEFAULT;
            default:
                return Endpoint.DEFAULT;
        }
    }
}
