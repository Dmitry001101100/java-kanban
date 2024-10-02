package manager;

import enumeration.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;

import java.io.File;
import java.io.IOException;

public class FileBackedTaskManagerTest {
    File temp = new File("Task.csv");


    @Test
    public void saveTask() throws IOException {

        FileBackedTaskManager manager = new FileBackedTaskManager(temp);
        Task task1 = new Task("Task1", "Description1",1,Status.NEW);
        Task task2 = new Task("Task2", "Description2",2,Status.DONE);
       // Epic epic1 = new Epic("Epic1", "Description1");
        manager.saveTas(task1);
        manager.saveTas(task2);
      //  manager.add(epic1);
    }


}
