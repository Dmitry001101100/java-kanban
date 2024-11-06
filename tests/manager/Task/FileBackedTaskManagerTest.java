package manager.Task;

import manager.Managers;
import org.junit.jupiter.api.Test;
import tasks.SubTask;
import java.io.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest extends AbstractTaskManagerTest {

    File taskToList = new File("taskToList.csv"); // основной файл для записи
    FileBackedTaskManager manager = Managers.getDefaultFileBackedTaskManager(taskToList);


    @Test
    void checkingTheUnloadingOfAllTypesOfTasksFromAFile() {
        manager.clearContent();
        saveTask1(); // сохраняем все выды задач
        manager.deleteTaskId(1);// удаляем некоторые чтобы проверить что они удалились из списка приоритетных
        manager.deleteSubTaskId(4);

        assertEquals(2, manager.getPrioritizedTasks().size(), "Длинна приоритетных задач отличается от ожидаемой");
        // создаем новый менеждер
        FileBackedTaskManager fileBackedTaskManager = Managers.getDefaultFileBackedTaskManager(taskToList);
        // проверяем все хранилища по длинне задач
        assertEquals(fileBackedTaskManager.getPrioritizedTasks().size(), manager.getPrioritizedTasks().size(),
                "Длинна приоритетных задач отличается от ожидаемой");
        assertEquals(fileBackedTaskManager.getTasks().size(), manager.getTasks().size(),
                "Неверное количество задач");
        assertEquals(fileBackedTaskManager.getSubTasks().size(), manager.getSubTasks().size(),
                "Неверное количество подзадач");
        assertEquals(fileBackedTaskManager.getEpics().size(), manager.getEpics().size(),
                "Неверное количество эпиков");

        // сравниваем taskMap
        assertEquals(manager.outIdTask(2), fileBackedTaskManager.outIdTask(2), "Задачи не совпадают");
        // по переменным
        assertEquals(manager.outIdTask(2).getId(), fileBackedTaskManager.outIdTask(2).getId(),
                "id не совпадает");
        assertEquals(manager.outIdTask(2).getType(), fileBackedTaskManager.outIdTask(2).getType(),
                "Тип не совпадает");
        assertEquals(manager.outIdTask(2).getTitle(), fileBackedTaskManager.outIdTask(2).getTitle(),
                "title не совпадают");
        assertEquals(manager.outIdTask(2).getStatus(), fileBackedTaskManager.outIdTask(2).getStatus(),
                "status не совпадает");
        assertEquals(manager.outIdTask(2).getDescription(), fileBackedTaskManager.outIdTask(2).getDescription(),
                "description не совпадает");
        assertEquals(manager.outIdTask(2).getStartTime(), fileBackedTaskManager.outIdTask(2).getStartTime(),
                "StartTime не совпадают");
        assertEquals(manager.outIdTask(2).getEndTime(), fileBackedTaskManager.outIdTask(2).getEndTime(),
                "endTime не совпадает");
        assertEquals(manager.outIdTask(2).getDuration(), fileBackedTaskManager.outIdTask(2).getDuration(),
                "duration не совпадает");

        // сравниваем епик
        assertEquals(manager.outIdEpic(3), fileBackedTaskManager.outIdEpic(3), "Задачи не совпадают");
        // по переменным
        assertEquals(manager.outIdEpic(3).getId(), fileBackedTaskManager.outIdEpic(3).getId(),
                "id не совпадает");
        assertEquals(manager.outIdEpic(3).getType(), fileBackedTaskManager.outIdEpic(3).getType(),
                "Тип не совпадает");
        assertEquals(manager.outIdEpic(3).getTitle(), fileBackedTaskManager.outIdEpic(3).getTitle(),
                "title не совпадают");
        assertEquals(manager.outIdEpic(3).getStatus(), fileBackedTaskManager.outIdEpic(3).getStatus(),
                "status не совпадает");
        assertEquals(manager.outIdEpic(3).getDescription(), fileBackedTaskManager.outIdEpic(3).getDescription(),
                "description не совпадает");
        assertEquals(manager.outIdEpic(3).getStartTime(), fileBackedTaskManager.outIdEpic(3).getStartTime(),
              "StartTime не совпадают");
        assertEquals(manager.outIdEpic(3).getEndTime(), fileBackedTaskManager.outIdEpic(3).getEndTime(),
                "endTime не совпадает");
        assertEquals(manager.outIdEpic(3).getDuration(), fileBackedTaskManager.outIdEpic(3).getDuration(),
                "duration не совпадает");
        assertEquals(manager.outIdEpic(3).getSubtaskIds(),fileBackedTaskManager.outIdEpic(3).getSubtaskIds(),
                "id подзадач в эпике не совпадают");

        // сравниваем подзадачи
          assertEquals(manager.outIdSubTask(5), fileBackedTaskManager.outIdSubTask(5), "подзадачи не совпадают");
        // по переменным
        assertEquals(manager.outIdSubTask(5).getId(), fileBackedTaskManager.outIdSubTask(5).getId(),
                "id не совпадает");
        assertEquals(manager.outIdSubTask(5).getType(), fileBackedTaskManager.outIdSubTask(5).getType(),
                "Тип не совпадает");
        assertEquals(manager.outIdSubTask(5).getTitle(), fileBackedTaskManager.outIdSubTask(5).getTitle(),
                "title не совпадают");
        assertEquals(manager.outIdSubTask(5).getStatus(), fileBackedTaskManager.outIdSubTask(5).getStatus(),
                "status не совпадает");
        assertEquals(manager.outIdSubTask(5).getDescription(), fileBackedTaskManager.outIdSubTask(5).getDescription(),
                "description не совпадает");
        assertEquals(manager.outIdSubTask(5).getStartTime(), fileBackedTaskManager.outIdSubTask(5).getStartTime(),
                "StartTime не совпадают");
        assertEquals(manager.outIdSubTask(5).getEndTime(), fileBackedTaskManager.outIdSubTask(5).getEndTime(),
                "endTime не совпадает");
        assertEquals(manager.outIdSubTask(5).getDuration(), fileBackedTaskManager.outIdSubTask(5).getDuration(),
                "duration не совпадает");
        assertEquals(manager.outIdSubTask(5).getEpicId(),fileBackedTaskManager.outIdSubTask(5).getEpicId(),
                "id эпиков не совпадает");
    }

    // -----------------------------совместимые тесты ------------------------------------------------------------------
    @Test
    void saveTask1() {
        manager.clearContent();
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

