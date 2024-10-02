package manager;

import enumeration.Status;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import java.io.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest {

    File savesTasksToFile = new File("savesTasksToFile.csv"); // основной файл для записи
    File testFileReset = new File("testFileReset.csv"); // файл для теста выгрузки задач из файла
    FileBackedTaskManager manager = new FileBackedTaskManager(savesTasksToFile);

    @Test
    public void savesTask() { // сохраняем все виды задач через новый класс

        Task task1 = new Task("Task1", "Description1", manager.getIdUp(), Status.NEW); // 1
        Task task2 = new Task("Task2", "Description2", manager.getIdUp(), Status.DONE); // 2
        Epic epic3 = new Epic("Test titleEpic", "Test description", manager.getIdUp(), Status.NEW); // 3
        SubTask sub5 = new SubTask(epic3.id, "Test titleSub1", "Test in Epic", manager.getIdUp(), Status.DONE); // 4
        SubTask sub6 = new SubTask(epic3.id, "Test titleSub2", "Test in Epic", manager.getIdUp(), Status.IN_PROGRESS); // 5

        manager.saveTask(task1);
        manager.saveTask(task2);
        manager.saveEpic(epic3);
        manager.saveSubTask(sub5);
        manager.saveSubTask(sub6);
    }

    @Test
    public void checkingForSaving() {  // проверка на запись файла
        savesTask();// сохраняем

        ArrayList<SubTask> subTasks;
        subTasks = manager.getSubTasks();

        assertNotNull(manager.getTasks(), "Задачи не возвращаются.");
        assertEquals(2, manager.getTasks().size(), "Неверное количество задач.");
        assertNotNull(manager.getEpics(), "Задачи не возвращаются.");
        assertEquals(1, manager.getEpics().size(), "Неверное количество задач.");
        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(2, subTasks.size(), "Неверное количество задач.");
    }

    @Test
    public void downloadingFromFile(){ // проверка на загрузку задач из файла
        manager.downloadingFromAFile(testFileReset);

        ArrayList<SubTask> subTasks;
        subTasks = manager.getSubTasks();

        assertNotNull(manager.getTasks(), "Задачи не возвращаются.");
        assertEquals(2, manager.getTasks().size(), "Неверное количество задач.");
        assertNotNull(manager.getEpics(), "Задачи не возвращаются.");
        assertEquals(1, manager.getEpics().size(), "Неверное количество задач.");
        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(2, subTasks.size(), "Неверное количество задач.");
    }

    @Test
    public void checkingForDelet(){ // проверка на удаление файлов
        savesTask();// сохраняем
        // проверка сохранения всех видов задач перед удалением

        ArrayList<SubTask> subTasks;
        manager.clearContent(); // удаляем файлы

        subTasks = manager.getSubTasks();

        assertNotNull(manager.getTasks(), "Задачи не возвращаются.");
        assertEquals(0, manager.getTasks().size(), "Неверное количество задач.");
        assertNotNull(manager.getEpics(), "Задачи не возвращаются.");
        assertEquals(0, manager.getEpics().size(), "Неверное количество задач.");
        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(0, subTasks.size(), "Неверное количество задач.");

    }




}
