package manager;

import enumeration.Status;
import manager.Task.FileBackedTaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import javax.swing.plaf.PanelUI;
import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest {



    File savesTasksToFile = new File("savesTasksToFile.csv"); // основной файл для записи
    File testFileReset = new File("testFileReset.csv"); // файл для теста выгрузки задач из файла

    FileBackedTaskManager manager = new FileBackedTaskManager(savesTasksToFile);
    FileBackedTaskManager reset = new FileBackedTaskManager(testFileReset);

    @Test
    public void savesTask() { // сохраняем все виды задач через новый класс

        Task task1 = new Task("Test titleTask", "Test description", manager.getIdUp(), Status.NEW,
                LocalDateTime.of(2024,12,14,14,42), Duration.ofMinutes(140)); // 1

        Task task2 = new Task("Test titleTask", "Test description", manager.getIdUp(), Status.NEW,
                LocalDateTime.of(2024,12,14,14,42), Duration.ofMinutes(140)); // 2

        Epic epic3 = new Epic("Епик", "описание", manager.getIdUp(), Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));                                       // 3

        SubTask sub4 = new SubTask(epic3.getId(), "Test titleSub1", "Test in Epic", manager.getIdUp(), Status.IN_PROGRESS,
                LocalDateTime.of(24, 12, 4, 10, 17), Duration.ofMinutes(15)); // 4
        SubTask sub5 = new SubTask(epic3.getId(), "Test titleSub2", "Test in Epic", manager.getIdUp(), Status.NEW,
                LocalDateTime.of(24, 3, 25, 16, 40), Duration.ofMinutes(45)); // 5

        manager.saveTask(task1);
        manager.saveTask(task2);
        manager.saveEpic(epic3);
        manager.saveSubTask(sub4);
        manager.saveSubTask(sub5);
    }

    @Test
    public void checkingForSaving() {  // проверка на запись файла`
        savesTask();// сохраняем

        ArrayList<SubTask> subTasks;
        subTasks = manager.getSubTasks();

        assertNotNull(manager.getTasks(), "Задачи не возвращаются.");
        assertEquals(2, manager.getTasks().size(), "Неверное количество задач.");
        assertNotNull(manager.getEpics(), "Задачи не возвращаются.");
        assertEquals(1, manager.getEpics().size(), "Неверное количество эпиков.");
        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(2, subTasks.size(), "Неверное количество подзадач.");
    }

    @Test
    public void downloadingFromFile(){ // проверка на загрузку задач из файла
        manager.downloadingFromAFile(testFileReset);

        ArrayList<SubTask> subTasks;
        subTasks = manager.getSubTasks();

        for(SubTask subTask : subTasks){
            System.out.println(subTask);
        }
        System.out.println(manager.outIdEpic(3));

        for (Task task: manager.getTasks()){
            System.out.println(task);
        }

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


    @Test
    public void saveTaskTest(){
        Task task1 = new Task("Test titleTask", "Test description", manager.getIdUp(), Status.NEW,
               LocalDateTime.of(24,12,14,14,42),
             //   null,
             //   null
               Duration.ofMinutes(134)
        );

        manager.saveTask(task1);

        Task savedTask = manager.outIdTask(1);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        List<Task> tasks = manager.getTasks();

        System.out.println(manager.outIdTask(task1.getId()));

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void loaginTaskTest(){
        Task task1 = new Task("Test titleTask", "Test description", reset.getIdUp(), Status.NEW,
                LocalDateTime.of(2024,12,14,14,42), Duration.ofMinutes(140));

        reset.downloadingFromAFile(savesTasksToFile);

        Task savedTask = reset.outIdTask(1);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        List<Task> tasks = reset.getTasks();

        System.out.println(reset.outIdTask(task1.getId()));

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.getFirst(), "Задачи не совпадают.");
    }

}
