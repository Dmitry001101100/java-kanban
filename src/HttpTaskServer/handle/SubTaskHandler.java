package HttpTaskServer.handle;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enumeration.Endpoint;
import manager.Task.TaskManager;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        System.out.println("перед свич");
        switch (endpoint) {
            case GET_SUBTASKS:
                handleGetSubTasks(exchange);
                break;
            case GET_SUBTASK:
                handleGetSubTask(exchange);
                break;
            case POST_SUBTASK:
                handlePostSubTask(exchange);
                break;
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

    private void handlePostSubTask(HttpExchange exchange) throws IOException { // сохранение и перезапись задач
        try (InputStream inputStream = exchange.getRequestBody()) {
            Optional<SubTask> subTaskOpt = parseSubTask(inputStream);

            if (subTaskOpt.isEmpty()) {
                writeResponse(exchange, "Передан пустой запрос", 400);
                return;
            }

            SubTask subTask = subTaskOpt.get();
            System.out.println(subTask);


            if (taskManager.containsKeyEpic(subTask.getEpicId())) {

                if (subTask.getId() == null) {
                    taskManager.createSubTask(subTask);
                    writeResponse(exchange, "Подзадача сохранена.", 201);

                } else {

                    if (taskManager.containsKeySubTask(subTask.getId())) {
                        taskManager.updateSubTask(subTask);
                        writeResponse(exchange, "Подзадача обновлена.", 201);


                    } else {
                        if (taskManager.containsKeyTasks(subTask.getId())) {
                            writeResponse(exchange, "Подзадача конфликтует с другими видами задач.", 406);
                        } else {
                            taskManager.createSubTask(subTask);
                            writeResponse(exchange, "Подзадача сохранена.", 201);
                        }
                    }
                }

            } else {
                writeResponse(exchange, "Эпик с таким id не найден.", 404);
            }


        } catch (IOException e) {
            writeResponse(exchange, "Внутренняя ошибка сервера", 500);
            e.printStackTrace();
        }
    }

    private Optional<SubTask> parseSubTask(InputStream inputStream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            SubTask subTask = gson.fromJson(reader, SubTask.class);
            return Optional.ofNullable(subTask);
        }
    }
}
