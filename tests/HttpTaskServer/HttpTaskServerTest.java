package HttpTaskServer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import enumeration.Status;
import manager.Managers;
import manager.Task.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    final File file = new File("taskToList.csv");
    private static final int PORT = 8080;
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer httpTaskServer = new HttpTaskServer();
    Gson gson = Managers.getGson();
    HttpClient client;

    //----------------------------------------------------------------------------------------------------------------------
    @BeforeEach
    void startTaskServer() throws IOException {
        client = HttpClient.newHttpClient();
        httpTaskServer.startServer(PORT, taskManager);
    }

    @AfterEach
    void afterEach() {
        httpTaskServer.stopServer();
    }

    //----------------------------------------------------------------------------------------------------------------------
    @Test
    void postCreateTask() throws IOException, InterruptedException { // сохранение task
        taskManager.clearContent(); // В зависимости от taskManager очищаем все задачи
        // Создаем задачу для тестирования
        Task task1 = new Task("Test titleTask", "Test description", 1, Status.NEW,
                LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(140));

        // Преобразуем задачу в JSON строку
        String taskJson = gson.toJson(task1);

        // Формируем POST-запрос к серверу для сохранения задачи
        URI url = URI.create("http://localhost:8080/taskServer/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Выводим тело ответа в строку
        String responseBody = response.body();

        assertEquals(201, response.statusCode(), "статус ответа несовпадает"); // проверяем статус ответа
        assertEquals("Задача сохранена.", responseBody, "Задачи не совпадают");
        assertEquals(task1, taskManager.getTaskById(1), "Задачи не совпадают");

    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        postCreateTask();
        taskManager.clearContent();
        // Создаем задачу для тестирования
        Task task1 = new Task("Test titleTask", "Test description", 1, Status.NEW,
                LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(140));
        taskManager.createTask(task1);

        // Формируем запрос к серверу для получения задачи по ID
        URI url = URI.create("http://localhost:8080/taskServer/task/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(404, response.statusCode());

        // Выводим тело ответа в строку
        String responseBody = response.body();
        System.out.println("Тело ответа: " + responseBody);

        // Преобразуем ответ обратно в объект Task
        Task receivedTask = gson.fromJson(responseBody, Task.class);
        System.out.println(receivedTask);

        // Проверяем, что полученная задача совпадает с отправленной
        assertEquals(task1, receivedTask);
        taskManager.clearContent();
    }

    @Test
    void postUpdateTask() throws IOException, InterruptedException { // сохранение task
        taskManager.clearContent();

        // Создаем задачу для тестирования
        Task task1 = new Task("Test titleTask", "Test description", 1, Status.NEW,
                LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(140));

        taskManager.createTask(task1); // для теста обновления таска

        // Преобразуем задачу в JSON строку
        String taskJson = gson.toJson(task1);

        // Формируем POST-запрос к серверу для сохранения задачи
        URI url = URI.create("http://localhost:8080/taskServer/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Выводим тело ответа в строку
        String responseBody = response.body();

        assertEquals(201, response.statusCode(), "статус ответа несовпадает"); // проверяем статус ответа
        assertEquals("Задача обновлена.", responseBody, "Задачи не совпадают");
        assertEquals(task1, taskManager.getTaskById(1), "Задачи не совпадают");
        taskManager.clearContent();
    }

}
