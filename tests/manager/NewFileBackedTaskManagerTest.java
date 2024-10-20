package manager;

import enumeration.Status;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.File;

public class NewFileBackedTaskManagerTest {

    /* В этом классе будут прововдиться следующие тесты:
     операции несущие в себе изменение БЗ всех видов задач(сохранение,изменение,удаление(полное и по id))
     далее выгрузка задач из файла и проверка на их соответствие
     так же проверка записи новой задачи после выгрузки из файла(проверка на неконфликтность id)
     */

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



}
