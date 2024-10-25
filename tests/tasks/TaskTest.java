package tasks;

import manager.History.HistoryManager;
import manager.Managers;
import manager.Task.FileBackedTaskManager;
import manager.Task.TaskManager;
import enumeration.Status;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    TaskManager taskManager = Managers.getDefault();

    HistoryManager historyManager = Managers.getDefaultHistory();

    Task task1 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW,
            LocalDateTime.of(2024,12,14,14,42), Duration.ofMinutes(140));

    @Test
    void addNewTask() {

        taskManager.saveTask(task1);

        Task savedTask = taskManager.outIdTask(1);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        List<Task> tasks = taskManager.getTasks();

        System.out.println(taskManager.outIdTask(task1.getId()));

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }
/*
    @Test
    void addNewHistoryRecord() {
        Task task1 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "В истории просмотров пусто.");
        assertEquals(1, history.size(), "В истории просмотров пусто.");
    }
*/

}