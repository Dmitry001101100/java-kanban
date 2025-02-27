package server;

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
        /*
        - сохранение задач+
        - вывод задачи по id+
        - вывод всех задач +
        - обновление задачи +
        - удаление задач +

         */
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

        assertEquals(201, response.statusCode(), "статус ответа не совпадает."); // проверяем статус ответа
        assertEquals("Задача сохранена.", responseBody, "Тело ответа не совпадает.");
        assertEquals(task1, taskManager.getTaskById(1), "Задачи не совпадают.");
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
        assertEquals(task1, list.get(0), "задача с id1 не совпадают.");
        assertEquals(task2, list.get(1), "задача с id2 не совпадают.");
        assertEquals(task3, list.get(2), "задача с id3 не совпадают.");
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

        assertEquals(201, response.statusCode(), "Статус ответа не совпадает."); // проверяем статус ответа
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
    /*
    - сохранение эпика+
    - выгрузка эпка по id+
    - обновление эпика+
    - удаление эпика +
    - вывод эпика по id+
    - удаление эпика по id+
    - вывод всех подзадач одного эпика+
(так же присутствует проверка временных рамок и статуса до и после сохранения подзадач)

     */


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
        // проверяем эпик по переменным т.к. После сохранения эпика эпика меняются его временные рамки на noll до сохранения подзадач
        assertEquals(epic1.getId(), taskManager.getEpicById(1).getId(), "Id не совпадают.");
        assertEquals(epic1.getTitle(), taskManager.getEpicById(1).getTitle(), "Названия не совпадают.");
        assertEquals(epic1.getStatus(), taskManager.getEpicById(1).getStatus(), "Статус не совпадают.");
        assertEquals(epic1.getDescription(), taskManager.getEpicById(1).getDescription(), "Описание не совпадают.");
        assertEquals(epic1.getSubtaskIds(), taskManager.getEpicById(1).getSubtaskIds(), "Id subTasks не совпадают.");
        assertNull(taskManager.getEpicById(1).getStartTime(), "Время начала эпика не совпадает.");
        assertNull(taskManager.getEpicById(1).getEndTime(), "Время начала эпика не совпадает.");
        assertNull(taskManager.getEpicById(1).getDuration(), "Время начала эпика не совпадает.");

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
        assertEquals(epic1.getId(), list.getFirst().getId(), "Id не совпадают.");
        assertEquals(epic1.getTitle(), list.getFirst().getTitle(), "Названия не совпадают.");
        assertEquals(epic1.getStatus(), list.getFirst().getStatus(), "Статус не совпадают.");
        assertEquals(epic1.getDescription(), list.getFirst().getDescription(), "Описание не совпадают.");
        assertEquals(epic1.getSubtaskIds(), list.getFirst().getSubtaskIds(), "Id subTasks не совпадают.");

        assertEquals(epic2.getId(), list.get(1).getId(), "Id не совпадают.");
        assertEquals(epic2.getTitle(), list.get(1).getTitle(), "Названия не совпадают.");
        assertEquals(epic2.getStatus(), list.get(1).getStatus(), "Статус не совпадают.");
        assertEquals(epic2.getDescription(), list.get(1).getDescription(), "Описание не совпадают.");
        assertEquals(epic2.getSubtaskIds(), list.get(1).getSubtaskIds(), "Id subTasks не совпадают.");

        assertEquals(epic3.getId(), list.get(2).getId(), "Id не совпадают.");
        assertEquals(epic3.getTitle(), list.get(2).getTitle(), "Названия не совпадают.");
        assertEquals(epic3.getStatus(), list.get(2).getStatus(), "Статус не совпадают.");
        assertEquals(epic3.getDescription(), list.get(2).getDescription(), "Описание не совпадают.");
        assertEquals(epic3.getSubtaskIds(), list.get(2).getSubtaskIds(), "Id subTasks не совпадают.");

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
        assertEquals(epic1.getId(), taskManager.getEpicById(1).getId(), "Id не совпадают.");
        assertEquals(epic1.getTitle(), taskManager.getEpicById(1).getTitle(), "Названия не совпадают.");
        assertEquals(epic1.getStatus(), taskManager.getEpicById(1).getStatus(), "Статус не совпадают.");
        assertEquals(epic1.getDescription(), taskManager.getEpicById(1).getDescription(), "Описание не совпадают.");
        assertEquals(epic1.getSubtaskIds(), taskManager.getEpicById(1).getSubtaskIds(), "Id subTasks не совпадают.");

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

        assertEquals(epic2.getId(), epicResponse.getId(), "Id не совпадают.");
        assertEquals(epic2.getTitle(), epicResponse.getTitle(), "Названия не совпадают.");
        assertEquals(epic2.getStatus(), epicResponse.getStatus(), "Статус не совпадают.");
        assertEquals(epic2.getDescription(), epicResponse.getDescription(), "Описание не совпадают.");
        assertEquals(epic2.getSubtaskIds(), epicResponse.getSubtaskIds(), "Id subTasks не совпадают.");

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
                LocalDateTime.of(2024, 9, 25, 16, 40), Duration.ofMinutes(12));
        taskManager.createEpic(epic1); // 1
        // проверяем статус и изменение временных рамок после сохранения эпика.
        assertEquals(Status.NEW, taskManager.getEpicById(1).getStatus(), "Статус эпика не совпадает.");
        assertNull(taskManager.getEpicById(1).getStartTime(), "Время начала эпика не совпадает.");
        assertNull(taskManager.getEpicById(1).getEndTime(), "Время начала эпика не совпадает.");
        assertNull(taskManager.getEpicById(1).getDuration(), "Время начала эпика не совпадает.");

        taskManager.createSubTask(sub4); // 4
        taskManager.createSubTask(sub6); // 6
        // проверяем на сохранение
        assertTrue(taskManager.containsKeyEpic(1), "эпик с Id1 не сохранен.");
        assertTrue(taskManager.containsKeySubTask(4), "эпик с Id4 не сохранен.");
        assertTrue(taskManager.containsKeySubTask(6), "эпик с Id3 не сохранен.");
        System.out.println(taskManager.getEpicById(1));
        // проверяем статус и временные рамки после сохранения подзадач.
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(1).getStatus(), "Статус эпика не совпадает.");
        assertEquals(taskManager.getSubTaskById(6).getStartTime(), taskManager.getEpicById(1).getStartTime(), "Время начала эпика не совпадает.");
        assertEquals(taskManager.getSubTaskById(4).getEndTime(), taskManager.getEpicById(1).getEndTime(), "Время начала эпика не совпадает.");
        assertEquals(Duration.ofMinutes(24), taskManager.getEpicById(1).getDuration(), "Время начала эпика не совпадает.");

        // Формируем запрос к серверу для получения задачи по ID
        URI url = URI.create("http://localhost:8080/taskServer/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<SubTask> list = gson.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>() {
        }.getType());
        assertEquals(sub4.getId(), list.getFirst().getId(), "Подзадача с id4 не совпадают после выгрузки.");
        assertEquals(sub6.getTitle(), list.getFirst().getTitle(), "Подзадача с id4 не совпадают после выгрузки.");
        taskManager.clearContent();
    }
    // --------------------------------------------------------------------------------------------------------------------------
    // субтакс

    /*
    - сохранение подзадачи
    - обновление задачи
    - вывод всех подзадач
    - вывод подзадачи по id
    - удалние подзадачи по id
    - проверка вывести по id подзадачу которой не
     */

    @Test
    void postCreatesSubtasks() throws IOException, InterruptedException { // сохранение подзадач
        taskManager.clearContent(); // В зависимости от taskManager очищаем все задачи
        Epic epic1 = new Epic("Епик", "описание", 1, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        SubTask sub2 = new SubTask(epic1.getId(), "Test titleSub1", "Test in Epic", 2, Status.IN_PROGRESS,
                LocalDateTime.of(2024, 12, 4, 10, 17), Duration.ofMinutes(24));
        taskManager.createEpic(epic1); // сохраняем эпик для теста
        // Преобразуем задачу в JSON строку
        String subTaskJson = gson.toJson(sub2);
        // Формируем POST-запрос к серверу для сохранения задачи
        URI url = URI.create("http://localhost:8080/taskServer/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Выводим тело ответа в строку
        String responseBody = response.body();
        assertEquals(201, response.statusCode(), "Статус ответа не совпадает."); // проверяем статус ответа
        assertEquals("Подзадача сохранена.", responseBody, "Тело ответа не совпадает.");
        assertEquals(sub2, taskManager.getSubTaskById(2), "Подзадачи не совпадают.");
        // проверяем статус и временные рамки после сохранения подзадач.
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(1).getStatus(), "Статус эпика не совпадает.");
        assertEquals(taskManager.getSubTaskById(2).getStartTime(), taskManager.getEpicById(1).getStartTime(), "Время начала эпика не совпадает.");
        assertEquals(taskManager.getSubTaskById(2).getEndTime(), taskManager.getEpicById(1).getEndTime(), "Время начала эпика не совпадает.");
        assertEquals(Duration.ofMinutes(24), taskManager.getEpicById(1).getDuration(), "Время начала эпика не совпадает.");
        taskManager.clearContent();
    }

    @Test
    void postUpdateSubtasks() throws IOException, InterruptedException { // обновление подзадач подзадач
        taskManager.clearContent(); // В зависимости от taskManager очищаем все задачи
        Epic epic1 = new Epic("Епик", "описание", 1, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        SubTask sub2 = new SubTask(epic1.getId(), "Test titleSub1", "Test in Epic", 2, Status.IN_PROGRESS,
                LocalDateTime.of(2024, 12, 4, 10, 17), Duration.ofMinutes(24));
        taskManager.createEpic(epic1); // сохраняем для теста
        taskManager.createSubTask(sub2);
        SubTask sub3 = new SubTask(epic1.getId(), "Test субтаска", "описание 2", 2, Status.IN_PROGRESS,
                LocalDateTime.of(2024, 12, 4, 10, 17), Duration.ofMinutes(24));
        // Преобразуем задачу в JSON строку
        String subTaskJson = gson.toJson(sub3);
        // Формируем POST-запрос к серверу для сохранения задачи
        URI url = URI.create("http://localhost:8080/taskServer/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Выводим тело ответа в строку
        String responseBody = response.body();
        assertEquals(201, response.statusCode(), "Статус ответа не совпадает."); // проверяем статус ответа
        assertEquals("Подзадача обновлена.", responseBody, "Тело ответа не совпадает.");
        assertEquals(sub3, taskManager.getSubTaskById(2), "Подзадачи не совпадают.");
        // проверяем статус и временные рамки после сохранения подзадач.
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(1).getStatus(), "Статус эпика не совпадает.");
        assertEquals(taskManager.getSubTaskById(2).getStartTime(), taskManager.getEpicById(1).getStartTime(), "Время начала эпика не совпадает.");
        assertEquals(taskManager.getSubTaskById(2).getEndTime(), taskManager.getEpicById(1).getEndTime(), "Время начала эпика не совпадает.");
        assertEquals(Duration.ofMinutes(24), taskManager.getEpicById(1).getDuration(), "Время начала эпика не совпадает.");
        taskManager.clearContent();
    }

    @Test
    void getSubTasks() throws IOException, InterruptedException {  // проверка вывода всех подзадач
        taskManager.clearContent(); // очищаем содержимое менеджера
        // Создаем задачу для тестирования
        Epic epic1 = new Epic("Епик", "описание", 1, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        SubTask sub2 = new SubTask(epic1.getId(), "Test titleSub1", "Test in Epic", 2, Status.IN_PROGRESS,
                LocalDateTime.of(2024, 12, 4, 10, 17), Duration.ofMinutes(24));
        SubTask sub3 = new SubTask(epic1.getId(), "Test titleSub1", "Test in Epic", 3, Status.IN_PROGRESS,
                LocalDateTime.of(2024, 12, 4, 10, 17), Duration.ofMinutes(24));
        SubTask sub4 = new SubTask(epic1.getId(), "Test titleSub1", "Test in Epic", 4, Status.IN_PROGRESS,
                LocalDateTime.of(2024, 12, 4, 10, 17), Duration.ofMinutes(24));
        taskManager.createEpic(epic1); // сохраняем для теста
        taskManager.createSubTask(sub2);
        taskManager.createSubTask(sub3);
        taskManager.createSubTask(sub4);
        // Формируем запрос к серверу для получения задачи по ID
        URI url = URI.create("http://localhost:8080/taskServer/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Проверяем статус ответа
        assertEquals(200, response.statusCode());
        List<SubTask> list = gson.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>() {
        }.getType());
        System.out.println("Тело ответа: " + list);
        // Проверяем, что полученная задача совпадает с отправленной
        assertEquals(sub2, list.get(0), "задача с id1 не совпадают.");
        assertEquals(sub3, list.get(1), "задача с id2 не совпадают.");
        assertEquals(sub4, list.get(2), "задача с id3 не совпадают.");
        taskManager.clearContent();
    }

    @Test
    void getSubTask() throws IOException, InterruptedException {  // проверка вывода подзадачи по id
        taskManager.clearContent(); // очищаем содержимое менеджера
        // Создаем задачу для тестирования
        Epic epic1 = new Epic("Епик", "описание", 1, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        SubTask sub2 = new SubTask(epic1.getId(), "Test titleSub1", "Test in Epic", 2, Status.IN_PROGRESS,
                LocalDateTime.of(2024, 12, 4, 10, 17), Duration.ofMinutes(24));
        SubTask sub3 = new SubTask(epic1.getId(), "тест субтаска", "описание субтаска", 3, Status.IN_PROGRESS,
                LocalDateTime.of(2024, 12, 4, 10, 17), Duration.ofMinutes(24));
        SubTask sub4 = new SubTask(epic1.getId(), "Test titleSub1", "Test in Epic", 4, Status.IN_PROGRESS,
                LocalDateTime.of(2024, 12, 4, 10, 17), Duration.ofMinutes(24));
        taskManager.createEpic(epic1); // сохраняем для теста
        taskManager.createSubTask(sub2);
        taskManager.createSubTask(sub3);
        taskManager.createSubTask(sub4);
        // Формируем запрос к серверу для получения задачи по ID
        URI url = URI.create("http://localhost:8080/taskServer/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Проверяем статус ответа
        assertEquals(200, response.statusCode());
        SubTask subTasksResp = gson.fromJson(response.body(), SubTask.class);
        System.out.println("Тело ответа: " + subTasksResp);
        // Проверяем, что полученная задача совпадает с отправленной
        assertEquals(sub3, subTasksResp, "задача с id3 не совпадают.");
        taskManager.clearContent();
    }

    @Test
    void deleieteSubTaskById() throws IOException, InterruptedException {  // проверка проверка удаления подзадачи по id
        taskManager.clearContent(); // очищаем содержимое менеджера
        // Создаем задачу для тестирования
        Epic epic1 = new Epic("Епик", "описание", 1, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        SubTask sub2 = new SubTask(epic1.getId(), "Test titleSub1", "Test in Epic", 2, Status.IN_PROGRESS,
                LocalDateTime.of(2024, 12, 4, 10, 17), Duration.ofMinutes(24));
        taskManager.createEpic(epic1); // сохраняем для теста
        taskManager.createSubTask(sub2);
        // Формируем запрос к серверу для получения задачи по ID
        URI url = URI.create("http://localhost:8080/taskServer/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Проверяем статус ответа
        assertEquals(200, response.statusCode());

        String responsetoJson = response.body();
        System.out.println("Тело ответа: " + responsetoJson);
        // Проверяем, что полученная задача совпадает с отправленной
        assertEquals("Подзадача удалена.", responsetoJson, "тело ответа не совпадает.");
        taskManager.clearContent();
    }

    @Test
    void errorDeleieteSubTaskById() throws IOException, InterruptedException {  // проверка на вывод несуществующего id подзадачи

        taskManager.clearContent(); // очищаем содержимое менеджера
        // Создаем задачу для тестирования
        Epic epic1 = new Epic("Епик", "описание", 1, Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        SubTask sub2 = new SubTask(epic1.getId(), "Test titleSub1", "Test in Epic", 2, Status.IN_PROGRESS,
                LocalDateTime.of(2024, 12, 4, 10, 17), Duration.ofMinutes(24));


        taskManager.createEpic(epic1); // сохраняем для теста
        taskManager.createSubTask(sub2);

        // Формируем запрос к серверу для получения задачи по ID
        URI url = URI.create("http://localhost:8080/taskServer/subtasks/5"); // указываем несуществующий id
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Проверяем статус ответа
        assertEquals(404, response.statusCode());

        String responsetoJson = response.body();
        System.out.println("Тело ответа: " + responsetoJson);
        // Проверяем, что полученная задача совпадает с отправленной
        assertEquals("Подзадача не найдена.", responsetoJson, "тело ответа не совпадает.");
        taskManager.clearContent();
    }

    //-----------------------------------------------------------------------------------------------------------------------
    @Test
    void getPrioritized() throws IOException, InterruptedException {
        taskManager.clearContent(); // очищаем содержимое менеджера
        // Создаем задачу для тестирования
        Task task1 = new Task("ДЛя теста", "Test description", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.of(2024, 2, 14, 14, 42), Duration.ofMinutes(14)); // 1
        Task task2 = new Task("ДЛя теста 2", "Test description", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(11)); // 2
        Epic epic3 = new Epic("Епик", "описание", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));                                       // 3
        SubTask sub4 = new SubTask(epic3.getId(), "Test titleSub1", "Test in Epic", taskManager.getIdUp(), Status.IN_PROGRESS,
                LocalDateTime.of(2024, 12, 4, 10, 17), Duration.ofMinutes(24)); // 4
        SubTask sub5 = new SubTask(epic3.getId(), "Test titleSub2", "Test in Epic", 8, Status.NEW,
                LocalDateTime.of(2024, 8, 25, 16, 40), Duration.ofMinutes(12)); // 5

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic3);
        taskManager.createSubTask(sub4);
        taskManager.createSubTask(sub5);
        List<Task> tasks = taskManager.getPrioritizedTasks();
        String jsonResponse = gson.toJson(tasks); // преобразуем приоритетный список в json строку для сравнения с ответом сервера.
        // Формируем запрос к серверу для получения задачи по ID
        URI url = URI.create("http://localhost:8080/taskServer/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Проверяем статус ответа
        assertEquals(200, response.statusCode());

        String listGson = response.body();

        assertNotNull(listGson, "Список пустой.");
        assertEquals(jsonResponse, listGson, "Приоритетные списки не совпадают.");
        taskManager.clearContent();
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        taskManager.clearContent();
        Task task1 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(140));
        Task task2 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.of(2024, 12, 23, 14, 42), Duration.ofMinutes(12));
        Task task3 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.of(2024, 12, 23, 14, 42), Duration.ofMinutes(12));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(3);

        URI url = URI.create("http://localhost:8080/taskServer/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        assertNotNull(tasks);
        assertEquals(3, tasks.size());
        assertEquals(task2, tasks.get(0), "Задача под id2 не совпадает.");
        assertEquals(task1, tasks.get(1), "Задача под id1 не совпадает.");
        assertEquals(task3, tasks.get(2), "Задача под id3 не совпадает.");
    }
}
