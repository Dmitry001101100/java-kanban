package httpTaskServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import enumeration.Status;
import httpTaskServer.adapters.DurationAdapter;
import httpTaskServer.adapters.LocalDateTimeAdapter;
import manager.Managers;
import manager.Task.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServer {

    Gson gson = getGson();

    HttpClient client;
    TaskManager taskManager = Managers.getDefault();

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }

    @Test
    void shouldReceiveAllTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Должен вернуться код 200");

        List<Task> list = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());

        assertEquals(list.getFirst(), taskManager.getTasks().getFirst(),
                "Задача должна быть первой в списке переданных и полученных задач");
        assertEquals(list.size(), taskManager.getTasks().size(),
                "Размер списка переданных задач должен быть равен размеру списка полученных");
    }

    @Test
    void shouldCreateNewTask() throws IOException, InterruptedException {
        LocalDateTime startTime2 = LocalDateTime.of(2024, 10, 23, 18, 10);
        Task newTask = new Task("Задача 2", "Описание задачи 2",1, Status.NEW, startTime2, Duration.ofMinutes(5));
        String json = gson.toJson(newTask);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(),"Должен вернуться код 201");
        assertEquals(4, taskManager.outIdTask(4).getId(), "Записанная задача должна иметь id=4");
        assertEquals("Задача 2", taskManager.outIdTask(4).getName(),
                "Название записанной задачи должно соответствовать строке 'Задача 2'");

        String jsonObject = response.body();
        Task receivedTask = gson.fromJson(jsonObject, Task.class);

        assertEquals(taskManager.outIdTask(4), receivedTask,
                "Отправленная и полученная задачи должны быть идентичными");
    }
}
