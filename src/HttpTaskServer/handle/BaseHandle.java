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

        switch (requestMethod) {
            case "GET" -> {
                switch (pathParts[2]) {
                    case "tasks" -> {
                        if (pathParts.length == 3) {
                            return Endpoint.GET_TASKS;
                        } else {
                            return Endpoint.GET_TASK;
                        }
                    }
                    case "subtasks" -> {
                        if (pathParts.length == 3) {
                            return Endpoint.GET_SUBTASKS;
                        } else {
                            return Endpoint.GET_SUBTASK;
                        }
                    }
                    case "epics" -> {
                        if (pathParts.length == 5) {
                            return Endpoint.GET_SUBTASK_BY_EPIC; // длина 5
                        } else if (pathParts.length == 3) {
                            return Endpoint.GET_EPICS; // длинна 3
                        } else if (pathParts.length == 4) {
                            return Endpoint.GET_EPIC; // длинна 4
                        }
                    }
                    case "history" -> {
                        return Endpoint.GET_HISTORY;
                    }
                    case "prioritized" -> {
                        return Endpoint.GET_PRIORITIZED;
                    }
                }
            }
            case "POST" -> {
                switch (pathParts[2]) {
                    case "tasks" -> {
                        return Endpoint.POST_TASK;
                    }
                    case "subtasks" -> {
                        return Endpoint.POST_SUBTASK;
                    }
                    case "epics" -> {
                        return Endpoint.POST_EPIC;
                    }
                }
            }
            case "DELETE" -> {

                switch (pathParts[2]) {
                    case "tasks" -> {

                        if (pathParts.length == 4) {
                            return Endpoint.DELETE_TASK;
                        }
                    }
                    case "epics" -> {
                        if (pathParts.length == 4) {
                            return Endpoint.DELETE_EPIC;
                        }
                    }
                    case "subtasks" -> {

                        if (pathParts.length == 4) {
                            return Endpoint.DELETE_SUBTASK;
                        }
                    }
                }
            }
        }
        return Endpoint.DEFAULT;
    }
}
