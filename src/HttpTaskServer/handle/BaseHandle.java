package HttpTaskServer.handle;

import HttpTaskServer.adapters.DurationAdapter;
import HttpTaskServer.adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import enumeration.Endpoint;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class BaseHandle {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }

    protected Optional<Integer> getOptionalId(HttpExchange exchange) { // проверка что id для вывода задачи является числом
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
                                 String responseString,
                                 int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    protected Endpoint getEndpoint(String path, String requestMethod) { // создание эндпоинта
        String[] pathParts = path.split("/");

        if (requestMethod.equals("GET")) {
            if (pathParts[2].equals("tasks")) {
                if (pathParts.length == 3) {
                    return Endpoint.GET_TASKS;
                } else {
                    return Endpoint.GET_TASK;
                }
            } else if (pathParts[2].equals("subtasks")) {
                if (pathParts.length == 3) {
                    return Endpoint.GET_SUBTASKS;
                } else {
                    return Endpoint.GET_SUBTASK;
                }
            } else if (pathParts[2].equals("epics")) {
                if (pathParts.length == 5) {
                    return Endpoint.GET_SUBTASK_BY_EPIC; // длина 5
                } else if (pathParts.length == 3) {
                    return Endpoint.GET_EPICS; // длинна 3
                } else if (pathParts.length == 4) {
                    return Endpoint.GET_EPIC; // длинна 4
                }
            } else if (pathParts[2].equals("history")) {
                return Endpoint.GET_HISTORY;
            } else if (pathParts[2].equals("prioritized")) {
                return Endpoint.GET_PRIORITIZED;
            }
        } else if (requestMethod.equals("POST")) {
            if (pathParts[2].equals("tasks")) {
                return Endpoint.POST_TASK;
            } else if (pathParts[2].equals("subtasks")) {
                return Endpoint.POST_SUBTASK;
            } else if (pathParts[2].equals("epics")) {
                return Endpoint.POST_EPIC;
            }
        } else if (requestMethod.equals("DELETE")) {

            if (pathParts[2].equals("tasks")) {

                if (pathParts.length == 4) {
                    return Endpoint.DELETE_TASK;
                }
            } else if (pathParts[2].equals("epics")) {
                if (pathParts.length == 4) {
                    return Endpoint.DELETE_EPIC;
                }
            } else if (pathParts[2].equals("subtasks")) {

                if (pathParts.length == 4) {
                    return Endpoint.DELETE_SUBTASK;
                }
            }

        }
        return Endpoint.DEFAULT;
    }
}
