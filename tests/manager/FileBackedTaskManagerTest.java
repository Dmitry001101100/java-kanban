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
    File savesTasksToFile = new File("savesTasksToFile.csv");
    FileBackedTaskManager manager = new FileBackedTaskManager(savesTasksToFile);

    @Test
    public void savesTask(){

        Task task1 = new Task("Task1", "Description1",1,Status.NEW);
        Task task2 = new Task("Task2", "Description2",2,Status.DONE);
        Epic epic3 = new Epic("Test titleEpic", "Test description", manager.getIdUp(), Status.NEW);
        SubTask sub5 = new SubTask(epic3.id, "Test titleSub1", "Test in Epic", manager.getIdUp(), Status.NEW);
        SubTask sub6 = new SubTask(epic3.id, "Test titleSub1", "Test in Epic", manager.getIdUp(), Status.NEW);

        manager.saveTask(task1);
        manager.saveTask(task2);
        manager.saveEpic(epic3);
        manager.saveSubTask(sub5);
        manager.saveSubTask(sub6);

    }


    @Test
    public void saveEpic(){
        Epic epic3 = new Epic("Test titleEpic", "Test description", manager.getIdUp(), Status.NEW);
        SubTask sub5 = new SubTask(epic3.id, "Test titleSub1", "Test in Epic", manager.getIdUp(), Status.NEW);
        SubTask sub6 = new SubTask(epic3.id, "Test titleSub1", "Test in Epic", manager.getIdUp(), Status.NEW);



        for (Epic t : manager.getEpics()){
            System.out.println(t);
            for (SubTask e : manager.getSubTasksId(t.id)){
                System.out.println(e);
            }
        }

        EpicMap();// удаляем все эпики с подзадачами через InMemoryTaskManager для того чтобы не сохранилась новая версия файла



        for (Epic t : manager.getEpics()){
            System.out.println(t);
            for (SubTask e : manager.getSubTasksId(t.id)){
                System.out.println(e);
            }
        }



        manager.downloadingFromAFile(temp);

        for (Epic t : manager.getEpics()){
            System.out.println(t.toString());
        }
        System.out.println(manager.getEpics().size());
    }


}
