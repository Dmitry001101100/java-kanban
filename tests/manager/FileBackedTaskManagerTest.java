package manager;

import enumeration.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.io.IOException;

public class FileBackedTaskManagerTest {
    File temp = new File("Task.csv");

    TaskManager taskManager = Managers.getDefault();
    FileBackedTaskManager manager = new FileBackedTaskManager(temp);


    @Test
    public void saveTask() throws IOException {


        Task task1 = new Task("Task1", "Description1",1,Status.NEW);
        Task task2 = new Task("Task2", "Description2",2,Status.DONE);
       // Epic epic1 = new Epic("Epic1", "Description1");
        manager.saveTask(task1);
        manager.saveTask(task2);
      //  manager.add(epic1);
    }

    @Test
    public void saveEpic(){
        Epic epic3 = new Epic("Test titleEpic", "Test description", taskManager.getIdUp(), Status.NEW);
        SubTask sub5 = new SubTask(epic3.id, "Test titleSub1", "Test in Epic", taskManager.getIdUp(), Status.NEW);
        SubTask sub6 = new SubTask(epic3.id, "Test titleSub1", "Test in Epic", taskManager.getIdUp(), Status.NEW);

        manager.saveEpic(epic3);
        manager.saveSubTask(sub5);
        manager.saveSubTask(sub6);

        taskManager.clearContent();// удаляем все эпики с подзадачами через InMemoryTaskManager для того чтобы не сохранилась новая версия файла

        for (Epic t : taskManager.getEpics()){
            System.out.println(t);
        }



        manager.downloadingFromAFile(temp);

        for (Epic t : taskManager.getEpics()){
            System.out.println(t.toString());
        }
    }


}
