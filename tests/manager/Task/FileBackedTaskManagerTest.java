package manager.Task;

import enumeration.Status;
import manager.Managers;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest extends AbstractTaskManagerTest {

    File taskToList = new File("taskToList.csv"); // основной файл для записи
    FileBackedTaskManager manager = Managers.getDefaultFileBackedTaskManager(taskToList);








    // -----------------------------совместимые тесты ------------------------------------------------------------------
    @Test
    void saveTask1() {
        taskManager.clearContent();
        savesTask(manager);
    }

    @Test
    void taskToTask() {
        manager.clearContent();
        taskToTask(manager);
    }

    @Test
    void epicInstancesAreEqualWhenTheirIdsEqual() {
        manager.clearContent();
        epicInstancesAreEqualWhenTheirIdsEqual(manager);
    }

    @Test
    void checkingTheSetAndGeneratedId() {
        manager.clearContent();
        checkingTheSetAndGeneratedId(manager);
    }

    @Test
    void checkingForImmutabilityByFields() {
        checkingForImmutabilityByFields(manager);
    }

    @Test
    public void shouldReturnInMemoryTaskManagerByDefault1() {
        shouldReturnInMemoryTaskManagerByDefault();
    }

    @Test
    void shouldReturnInMemoryHistoryManagerByDefault1() {
        shouldReturnInMemoryHistoryManagerByDefault();
    }

    @Test
    void removeTask() {
        manager.clearContent();
        removeTask(manager);
    }

    @Test
    void changeContentTask1() {
        manager.clearContent();
        changeContentTask(manager);
    }

    @Test
    void deleteTask1() {
        manager.clearContent();
        deleteTask(manager);
    }

    @Test
    void deleteEpic() {
        manager.clearContent();
        deleteEpic(manager);
    }

    @Test
    void removeEpic1() {
        manager.clearContent();
        removeEpic(manager);
    }

    @Test
    void removeSubtask1() {
        manager.clearContent();
        removeSubtask(manager);
    }

    @Test
    void clerSubTaskofEpic1() {
        manager.clearContent();
        clerSubTaskofEpic(manager);
    }

    @Test
    void clearTasks1() {
        manager.clearContent();
        clearTasks(manager);
    }

    @Test
    void getHistory1() {
        manager.clearContent();
        getHistory(manager);
    }

    @Test
    void removeTaskHis1() {
        manager.clearContent();
        removeTaskHis(manager);
    }

    @Test
    void getUniqueHistory1() {
        manager.clearContent();
        getUniqueHistory(manager);
    }

    @Test
    void historyIsEmpty1() {
        manager.clearContent();
        historyIsEmpty(manager);
    }


    @Test
    public void checkingForSaving() {  // проверка на запись файла`
        savesTask(manager);// сохраняем

        ArrayList<SubTask> subTasks;
        subTasks = manager.getSubTasks();

        assertNotNull(manager.getTasks(), "Задачи не возвращаются.");
        assertEquals(2, manager.getTasks().size(), "Неверное количество задач.");
        assertNotNull(manager.getEpics(), "Задачи не возвращаются.");
        assertEquals(1, manager.getEpics().size(), "Неверное количество эпиков.");
        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(2, subTasks.size(), "Неверное количество подзадач.");
    }


}

