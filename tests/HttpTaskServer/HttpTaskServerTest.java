package HttpTaskServer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import enumeration.Status;
import manager.Managers;
import manager.Task.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    // tasks

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

        assertEquals(201, response.statusCode(), "статус ответа не совпадает"); // проверяем статус ответа
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

        List<Task> list = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        System.out.println("Тело ответа: " + list);
        // Проверяем, что полученная задача совпадает с отправленной
        assertEquals(task1, list.get(0), "задача с id1 не совпадают");
        assertEquals(task2, list.get(1), "задача с id2 не совпадают");
        assertEquals(task3, list.get(2), "задача с id3 не совпадают");
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

        assertEquals(201, response.statusCode(), "Статус ответа не совпадает"); // проверяем статус ответа
        assertEquals("Задача обновлена.", responseBody, "Тело ответа не совпадает.");
        assertEquals(task1, taskManager.getTaskById(1), "Задачи не совпадают.");
        taskManager.clearContent();
    }

    @Test
    void deleteTasksBuId() throws IOException, InterruptedException { // удаление задачи по id
        taskManager.clearContent();
        // Создаем задачу для тестирования
        Task task1 = new Task("Test titleTask", "Test description", 1, Status.NEW,
                LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(140));
        taskManager.createTask(task1); // сохраняем задачу
        assertEquals(task1, taskManager.getTaskById(1), "задача не сохранилась в менеджере.");
        // Формируем запрос к серверу для получения задачи по ID
        URI url = URI.create("http://localhost:8080/taskServer/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());// Проверяем статус ответа
        String responseBody = response.body();// Выводим тело ответа в строку
        System.out.println("Тело ответа: " + responseBody);
        // Проверяем, что полученная задача совпадает с отправленной
        assertEquals("Задача удалена.", responseBody, "Ошибка удаления задачи.");
        assertFalse(taskManager.containsKeyTask(1), "Задача не удалилась из менеджера."); // проверяем что задача удалилась из менеджера

        taskManager.clearContent();
    }
    // ----------------------------------------------------------------------------------------------------------------
    //epics

    @Test
    void postCreateEpics() throws IOException, InterruptedException { // сохранение эпика
        taskManager.clearContent(); // В зависимости от taskManager очищаем все задачи
        // Создаем задачу для тестирования
        Epic epic1 = new Epic("Епик", "описание", 1, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        // Преобразуем задачу в JSON строку
        String taskJson = gson.toJson(epic1);
        // Формируем POST-запрос к серверу для сохранения задачи
        URI url = URI.create("http://localhost:8080/taskServer/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Выводим тело ответа в строку
        String responseBody = response.body();

        assertEquals(201, response.statusCode(), "статус ответа не совпадает."); // проверяем статус ответа

        assertEquals("Эпик сохранен.", responseBody, "Ответ сервера не совпадает.");
        // проверяем эпик по переменным кроме временных рамок т.к они меняются при сохранении на null
        assertEquals(epic1.getId(), taskManager.getEpicById(1).getId(), "Id не совпадают");
        assertEquals(epic1.getTitle(), taskManager.getEpicById(1).getTitle(), "Названия не совпадают");
        assertEquals(epic1.getStatus(), taskManager.getEpicById(1).getStatus(), "Статус не совпадают");
        assertEquals(epic1.getDescription(), taskManager.getEpicById(1).getDescription(), "Описание не совпадают");
        assertEquals(epic1.getSubtaskIds(), taskManager.getEpicById(1).getSubtaskIds(), "Id subTasks не совпадают");

        taskManager.clearContent();
    }

    @Test
    void getEpics() throws IOException, InterruptedException {  // проверка вывода всех эпиков

        taskManager.clearContent(); // очищаем содержимое менеджера
        // Создаем задачу для тестирования
        Epic epic1 = new Epic("Епик", "описание", 1, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        Epic epic2 = new Epic("Епик", "описание", 2, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        Epic epic3 = new Epic("Епик", "описание", 3, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        taskManager.createEpic(epic1); // 1
        taskManager.createEpic(epic2); // 2
        taskManager.createEpic(epic3); // 3
        // Формируем запрос к серверу для получения задачи по ID
        URI url = URI.create("http://localhost:8080/taskServer/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Проверяем статус ответа
        assertEquals(200, response.statusCode());

        List<Epic> list = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {
        }.getType());
        System.out.println("Тело ответа: " + list);
        // Проверяем, что полученная задача совпадает с отправленной
        assertEquals(epic1.getId(), list.getFirst().getId(), "Id не совпадают");
        assertEquals(epic1.getTitle(), list.getFirst().getTitle(), "Названия не совпадают");
        assertEquals(epic1.getStatus(), list.getFirst().getStatus(), "Статус не совпадают");
        assertEquals(epic1.getDescription(), list.getFirst().getDescription(), "Описание не совпадают");
        assertEquals(epic1.getSubtaskIds(), list.getFirst().getSubtaskIds(), "Id subTasks не совпадают");

        assertEquals(epic2.getId(), list.get(1).getId(), "Id не совпадают");
        assertEquals(epic2.getTitle(), list.get(1).getTitle(), "Названия не совпадают");
        assertEquals(epic2.getStatus(), list.get(1).getStatus(), "Статус не совпадают");
        assertEquals(epic2.getDescription(), list.get(1).getDescription(), "Описание не совпадают");
        assertEquals(epic2.getSubtaskIds(), list.get(1).getSubtaskIds(), "Id subTasks не совпадают");

        assertEquals(epic3.getId(), list.get(2).getId(), "Id не совпадают");
        assertEquals(epic3.getTitle(), list.get(2).getTitle(), "Названия не совпадают");
        assertEquals(epic3.getStatus(), list.get(2).getStatus(), "Статус не совпадают");
        assertEquals(epic3.getDescription(), list.get(2).getDescription(), "Описание не совпадают");
        assertEquals(epic3.getSubtaskIds(), list.get(2).getSubtaskIds(), "Id subTasks не совпадают");

        taskManager.clearContent();
    }

    @Test
    void postUpdateEpics() throws IOException, InterruptedException { // обновление епика
        taskManager.clearContent(); // В зависимости от taskManager очищаем все задачи
        // Создаем задачу для тестирования
        Epic epic1 = new Epic("Епик", "описание", 1, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        taskManager.createEpic(epic1);
        // Преобразуем задачу в JSON строку
        String taskJson = gson.toJson(epic1);
        // Формируем POST-запрос к серверу для сохранения задачи
        URI url = URI.create("http://localhost:8080/taskServer/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Выводим тело ответа в строку
        String responseBody = response.body();

        assertEquals(201, response.statusCode(), "статус ответа не совпадает."); // проверяем статус ответа
        assertEquals("Эпик обновлен.", responseBody, "Ответ сервера не совпадает.");
        // проверяем эпик по переменным кроме временных рамок т.к они меняются при сохранении на null
        assertEquals(epic1.getId(), taskManager.getEpicById(1).getId(), "Id не совпадают");
        assertEquals(epic1.getTitle(), taskManager.getEpicById(1).getTitle(), "Названия не совпадают");
        assertEquals(epic1.getStatus(), taskManager.getEpicById(1).getStatus(), "Статус не совпадают");
        assertEquals(epic1.getDescription(), taskManager.getEpicById(1).getDescription(), "Описание не совпадают");
        assertEquals(epic1.getSubtaskIds(), taskManager.getEpicById(1).getSubtaskIds(), "Id subTasks не совпадают");

        taskManager.clearContent();
    }

    @Test
    void deleteEpicBuId() throws IOException, InterruptedException { // удаление эпика по id
        taskManager.clearContent();
        // Создаем задачу для тестирования
        Epic epic1 = new Epic("Епик", "описание", 1, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        taskManager.createEpic(epic1);
        assertEquals(epic1.getId(), taskManager.getEpicById(1).getId(), "Эпик не сохранился в менеджере.");
        assertTrue(taskManager.containsKeyEpic(1), "Эпик не сохранился в менеджере.");
        // Формируем запрос к серверу для получения задачи по ID
        URI url = URI.create("http://localhost:8080/taskServer/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());// Проверяем статус ответа
        String responseBody = response.body();// Выводим тело ответа в строку
        System.out.println("Тело ответа: " + responseBody);
        // Проверяем, что полученная задача совпадает с отправленной
        assertEquals("Эпик удален.", responseBody, "Ошибка удаления задачи.");
        assertFalse(taskManager.containsKeyEpic(1), "Задача не удалилась из менеджера."); // проверяем что задача удалилась из менеджера

        taskManager.clearContent();
    }

    @Test
    void getEpic() throws IOException, InterruptedException {  // проверка вывода эпика по id

        taskManager.clearContent(); // очищаем содержимое менеджера
        // Создаем задачу для тестирования
        Epic epic1 = new Epic("Епик", "описание", 1, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        Epic epic2 = new Epic("Епик", "описание", 2, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        Epic epic3 = new Epic("Епик", "описание", 3, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));

        taskManager.createEpic(epic1); // 1
        taskManager.createEpic(epic2); // 2
        taskManager.createEpic(epic3); // 3
        // Формируем запрос к серверу для получения задачи по ID
        URI url = URI.create("http://localhost:8080/taskServer/epics/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Проверяем статус ответа
        assertEquals(200, response.statusCode());

        Epic epicResponse = gson.fromJson(response.body(), Epic.class);
        System.out.println("Тело ответа: " + epicResponse);
        // Проверяем, что полученная задача совпадает с отправленной

        assertEquals(epic2.getId(), epicResponse.getId(), "Id не совпадают");
        assertEquals(epic2.getTitle(), epicResponse.getTitle(), "Названия не совпадают");
        assertEquals(epic2.getStatus(), epicResponse.getStatus(), "Статус не совпадают");
        assertEquals(epic2.getDescription(), epicResponse.getDescription(), "Описание не совпадают");
        assertEquals(epic2.getSubtaskIds(), epicResponse.getSubtaskIds(), "Id subTasks не совпадают");

        taskManager.clearContent();
    }

    @Test
    void deleteEpic() throws IOException, InterruptedException {  // проверка удаления эпика по id

        taskManager.clearContent(); // очищаем содержимое менеджера
        // Создаем задачу для тестирования
        Epic epic1 = new Epic("Епик", "описание", 1, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        Epic epic2 = new Epic("Епик", "описание", 2, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        Epic epic3 = new Epic("Епик", "описание", 3, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));

        taskManager.createEpic(epic1); // 1
        taskManager.createEpic(epic2); // 2
        taskManager.createEpic(epic3); // 3

        assertTrue(taskManager.containsKeyEpic(1), "эпик с Id1 не сохранен.");
        assertTrue(taskManager.containsKeyEpic(2), "эпик с Id2 не сохранен.");
        assertTrue(taskManager.containsKeyEpic(3), "эпик с Id3 не сохранен.");
        // Формируем запрос к серверу для получения задачи по ID
        URI url = URI.create("http://localhost:8080/taskServer/epics/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Проверяем статус ответа
        assertEquals(201, response.statusCode(), "Статус ответа не совпадает.");

        assertTrue(taskManager.containsKeyEpic(1), "эпик с Id1 не сохранен.");
        assertFalse(taskManager.containsKeyEpic(2), "эпик с Id2 не сохранен.");
        assertTrue(taskManager.containsKeyEpic(3), "эпик с Id3 не сохранен.");

        taskManager.clearContent();
    }

    @Test
    void getSubTaskBuEpicId() throws IOException, InterruptedException {  // проверка вывода всех подзадач одного эпика

        taskManager.clearContent(); // очищаем содержимое менеджера
        // Создаем задачу для тестирования
        Epic epic1 = new Epic("Епик", "описание", 1, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        SubTask sub4 = new SubTask(epic1.getId(), "Test titleSub2", "Test in Epic", 4, Status.NEW,
                LocalDateTime.of(2024, 8, 25, 16, 40), Duration.ofMinutes(12));
        SubTask sub6 = new SubTask(epic1.getId(), "Test titleSub2", "Test in Epic", 6, Status.DONE,
                LocalDateTime.of(2024, 8, 25, 16, 40), Duration.ofMinutes(12));

        taskManager.createEpic(epic1); // 1
        taskManager.createSubTask(sub4); // 4
        taskManager.createSubTask(sub6); // 6
        // проверяем на сохранение
        assertTrue(taskManager.containsKeyEpic(1), "эпик с Id1 не сохранен.");
        assertTrue(taskManager.containsKeySubTask(4), "эпик с Id4 не сохранен.");
        assertTrue(taskManager.containsKeySubTask(6), "эпик с Id3 не сохранен.");
        // Формируем запрос к серверу для получения задачи по ID
        URI url = URI.create("http://localhost:8080/taskServer/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<SubTask> list = gson.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>() {
        }.getType());

        assertEquals(sub4.getId(), list.getFirst().getId(), "Подзадача с id4 не совпадают после выгрузки");
        assertEquals(sub6.getTitle(), list.getFirst().getTitle(), "Подзадача с id4 не совпадают после выгрузки");

        taskManager.clearContent();
    }

}
