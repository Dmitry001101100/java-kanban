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
        taskManager.clearContent();
    }

    @Test
    void getTaskById() throws IOException, InterruptedException { // проверка вывода по id

        taskManager.clearContent();
        // Создаем задачу для тестирования
        Task task1 = new Task("Test titleTask", "Test description", 1, Status.NEW,
                LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(140));
        taskManager.createTask(task1);

        // Формируем запрос к серверу для получения задачи по ID
        URI url = URI.create("http://localhost:8080/taskServer/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(200, response.statusCode());

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
    void getTasks() throws IOException, InterruptedException {  // проверка вывода всех задач

        taskManager.clearContent(); // очищаем содержимое менеджера
        // Создаем задачу для тестирования
        Task task1 = new Task("Test titleTask", "Test description", Status.NEW,
                LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(140));
        Task task2 = new Task("Test titleTask", "Test description", Status.NEW,
                LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(140));
        Task task3 = new Task("Test titleTask", "Test description", Status.NEW,
                LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(140));
        taskManager.createTask(task1); // 1
        taskManager.createTask(task2); // 2
        taskManager.createTask(task3); // 3
        // Формируем запрос к серверу для получения задачи по ID
        URI url = URI.create("http://localhost:8080/taskServer/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Проверяем статус ответа
        assertEquals(200, response.statusCode());

        List<Task> list = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());
        System.out.println("Тело ответа: " + list);
        // Проверяем, что полученная задача совпадает с отправленной
        assertEquals(task1, list.get(0),"задача с id1 несовпадают");
        assertEquals(task2, list.get(1),"задача с id1 несовпадают");
        assertEquals(task3, list.get(2),"задача с id1 несовпадают");
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

    @Test
    void deleteTasksBuId() throws IOException, InterruptedException { // удаление задачи по id
        taskManager.clearContent();
        // Создаем задачу для тестирования
        Task task1 = new Task("Test titleTask", "Test description", 1, Status.NEW,
                LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(140));
        taskManager.createTask(task1); // сохраняем задачу
        // Формируем запрос к серверу для получения задачи по ID
        URI url = URI.create("http://localhost:8080/taskServer/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());// Проверяем статус ответа
        String responseBody = response.body();// Выводим тело ответа в строку
        System.out.println("Тело ответа: " + responseBody);
        // Проверяем, что полученная задача совпадает с отправленной
        assertEquals("Задача удалена.", responseBody,"Ошибка удаленя задачи.");
        assertEquals(taskManager.containsKeyTask(1), false,"Задача неудалилась из менеджера."); // проверяем что задача удалилась из менеджера

        taskManager.clearContent();
    }

}
