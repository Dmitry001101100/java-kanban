package manager.Task;

import manager.Managers;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

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
        saveTask1(); // сохраняем все виды задач
        manager.deleteTaskId(1);// удаляем некоторые чтобы проверить что они удалились из списка приоритетных
        manager.deleteSubTaskId(4);

        assertEquals(2, manager.getPrioritizedTasks().size(), "Длинна приоритетных задач отличается от ожидаемой");
        // создаем новый менеждер
        FileBackedTaskManager fileBackedTaskManager = Managers.getDefaultFileBackedTaskManager(taskToList);
        // проверяем все хранилища по длине задач
        assertEquals(fileBackedTaskManager.getPrioritizedTasks().size(), manager.getPrioritizedTasks().size(),
                "Длинна приоритетных задач отличается от ожидаемой");
        assertEquals(fileBackedTaskManager.getTasks().size(), manager.getTasks().size(),
                "Неверное количество задач");
        assertEquals(fileBackedTaskManager.getSubTasks().size(), manager.getSubTasks().size(),
                "Неверное количество подзадач");
        assertEquals(fileBackedTaskManager.getEpics().size(), manager.getEpics().size(),
                "Неверное количество эпиков");

        // сравниваем taskMap
        assertEquals(manager.getTaskById(2), fileBackedTaskManager.getTaskById(2), "Задачи не совпадают");
        // по переменным
        assertEquals(manager.getTaskById(2).getId(), fileBackedTaskManager.getTaskById(2).getId(),
                "id не совпадает");
        assertEquals(manager.getTaskById(2).getType(), fileBackedTaskManager.getTaskById(2).getType(),
                "Тип не совпадает");
        assertEquals(manager.getTaskById(2).getTitle(), fileBackedTaskManager.getTaskById(2).getTitle(),
                "title не совпадают");
        assertEquals(manager.getTaskById(2).getStatus(), fileBackedTaskManager.getTaskById(2).getStatus(),
                "status не совпадает");
        assertEquals(manager.getTaskById(2).getDescription(), fileBackedTaskManager.getTaskById(2).getDescription(),
                "description не совпадает");
        assertEquals(manager.getTaskById(2).getStartTime(), fileBackedTaskManager.getTaskById(2).getStartTime(),
                "StartTime не совпадают");
        assertEquals(manager.getTaskById(2).getEndTime(), fileBackedTaskManager.getTaskById(2).getEndTime(),
                "endTime не совпадает");
        assertEquals(manager.getTaskById(2).getDuration(), fileBackedTaskManager.getTaskById(2).getDuration(),
                "duration не совпадает");

        // сравниваем епик
        assertEquals(manager.getEpicById(3), fileBackedTaskManager.getEpicById(3), "Задачи не совпадают");
        // по переменным
        assertEquals(manager.getEpicById(3).getId(), fileBackedTaskManager.getEpicById(3).getId(),
                "id не совпадает");
        assertEquals(manager.getEpicById(3).getType(), fileBackedTaskManager.getEpicById(3).getType(),
                "Тип не совпадает");
        assertEquals(manager.getEpicById(3).getTitle(), fileBackedTaskManager.getEpicById(3).getTitle(),
                "title не совпадают");
        assertEquals(manager.getEpicById(3).getStatus(), fileBackedTaskManager.getEpicById(3).getStatus(),
                "status не совпадает");
        assertEquals(manager.getEpicById(3).getDescription(), fileBackedTaskManager.getEpicById(3).getDescription(),
                "description не совпадает");
        assertEquals(manager.getEpicById(3).getStartTime(), fileBackedTaskManager.getEpicById(3).getStartTime(),
                "StartTime не совпадают");
        assertEquals(manager.getEpicById(3).getEndTime(), fileBackedTaskManager.getEpicById(3).getEndTime(),
                "endTime не совпадает");
        assertEquals(manager.getEpicById(3).getDuration(), fileBackedTaskManager.getEpicById(3).getDuration(),
                "duration не совпадает");
        assertEquals(manager.getEpicById(3).getSubtaskIds(), fileBackedTaskManager.getEpicById(3).getSubtaskIds(),
                "id подзадач в эпике не совпадают");

        // сравниваем подзадачи
        assertEquals(manager.getSubTaskById(5), fileBackedTaskManager.getSubTaskById(5), "подзадачи не совпадают");
        // по переменным
        assertEquals(manager.getSubTaskById(5).getId(), fileBackedTaskManager.getSubTaskById(5).getId(),
                "id не совпадает");
        assertEquals(manager.getSubTaskById(5).getType(), fileBackedTaskManager.getSubTaskById(5).getType(),
                "Тип не совпадает");
        assertEquals(manager.getSubTaskById(5).getTitle(), fileBackedTaskManager.getSubTaskById(5).getTitle(),
                "title не совпадают");
        assertEquals(manager.getSubTaskById(5).getStatus(), fileBackedTaskManager.getSubTaskById(5).getStatus(),
                "status не совпадает");
        assertEquals(manager.getSubTaskById(5).getDescription(), fileBackedTaskManager.getSubTaskById(5).getDescription(),
                "description не совпадает");
        assertEquals(manager.getSubTaskById(5).getStartTime(), fileBackedTaskManager.getSubTaskById(5).getStartTime(),
                "StartTime не совпадают");
        assertEquals(manager.getSubTaskById(5).getEndTime(), fileBackedTaskManager.getSubTaskById(5).getEndTime(),
                "endTime не совпадает");
        assertEquals(manager.getSubTaskById(5).getDuration(), fileBackedTaskManager.getSubTaskById(5).getDuration(),
                "duration не совпадает");
        assertEquals(manager.getSubTaskById(5).getEpicId(), fileBackedTaskManager.getSubTaskById(5).getEpicId(),
                "id эпиков не совпадает");

        // сравниваем задачи до и после выгрузки из приоритетного списка по всем параметрам

        Task task1 = manager.getPrioritizedTasks().get(1); // первоначальная задача
        Task task2 = fileBackedTaskManager.getPrioritizedTasks().get(1); // после выгрузки из файла

        assertEquals(task1, task2, "Задачи не совпадают");
        // по переменным
        assertEquals(task1.getId(), task2.getId(), "id не совпадает");
        assertEquals(task1.getType(), task2.getType(), "Тип не совпадает");
        assertEquals(task1.getTitle(), task2.getTitle(), "title не совпадают");
        assertEquals(task1.getStatus(), task2.getStatus(), "status не совпадает");
        assertEquals(task1.getDescription(), task2.getDescription(), "description не совпадает");
        assertEquals(task1.getStartTime(), task2.getStartTime(), "StartTime не совпадают");
        assertEquals(task1.getEndTime(), task2.getEndTime(), "endTime не совпадает");
        assertEquals(task1.getDuration(), task2.getDuration(), "duration не совпадает");

        // проверка подзадачи
        SubTask subTask1 = (SubTask) manager.getPrioritizedTasks().get(0); // первоначальная подзадача
        SubTask subTask2 = (SubTask) fileBackedTaskManager.getPrioritizedTasks().get(0); // после выгрузки из файла

        assertEquals(subTask1, subTask2, "подзадачи не совпадают");
        // по переменным
        assertEquals(subTask1.getId(), subTask2.getId(), "id не совпадает");
        assertEquals(subTask1.getType(), subTask2.getType(), "Тип не совпадает");
        assertEquals(subTask1.getTitle(), subTask2.getTitle(), "title не совпадают");
        assertEquals(subTask1.getStatus(), subTask2.getStatus(), "status не совпадает");
        assertEquals(subTask1.getDescription(), subTask2.getDescription(), "description не совпадает");
        assertEquals(subTask1.getStartTime(), subTask2.getStartTime(), "StartTime не совпадают");
        assertEquals(subTask1.getEndTime(), subTask2.getEndTime(), "endTime не совпадает");
        assertEquals(subTask1.getDuration(), subTask2.getDuration(), "duration не совпадает");
        assertEquals(subTask1.getEpicId(), subTask2.getEpicId(), "id эпиков не совпадает");

        fileBackedTaskManager.clearContent();
        manager.clearContent();
    }

    @Test
    void comparisonHistoryFromFile(){ // тест выгрузки исписка истории из файла
        manager.clearContent();
        saveTask1(); // сохраняем все виды задач
        // выводим несколько видов задач в разном порядке id для попадания этих задач в историю просмотра
        manager.getTaskById(2);
        manager.getTaskById(1);
        manager.getSubTaskById(4);
        manager.getEpicById(3);
        // удаляем одну задачу чтобы убедиться что это действие несет изменения сохранения файла истории
        manager.deleteTaskId(1);
        // создаем новый менеждер
        FileBackedTaskManager fileBackedTaskManager = Managers.getDefaultFileBackedTaskManager(taskToList);

        // проверка порядка списка истории до и после выгрузки
        assertEquals(manager.getHistory().toString(), fileBackedTaskManager.getHistory().toString(), "списки истории не совпадают");
        // проверка всех видов задач из ссписка истории
        Task task1 = manager.getHistory().getFirst();
        Task task2 = fileBackedTaskManager.getHistory().getFirst();
        assertEquals(task1, task2, "задачи из истории не совпадают");
        assertEquals(task1.getId(), task2.getId(), "id не совпадает");

        fileBackedTaskManager.getHistory().forEach(System.out :: println);
        Epic epi1 = (Epic) manager.getHistory().get(2);
        Epic epic2 = (Epic) fileBackedTaskManager.getHistory().get(2);
        assertEquals(epi1, epic2, "списки истории не совпадают");
        assertEquals(epi1.getId(), epic2.getId(), "id не совпадает");

        SubTask subTask1 = (SubTask) manager.getHistory().get(1);
        SubTask subTask2 = (SubTask) fileBackedTaskManager.getHistory().get(1);
        assertEquals(subTask1, subTask2, "списки истории не совпадают");
        assertEquals(subTask1.getId(), subTask2.getId(), "id не совпадает");

    }

    @Test
    public void testSaveHistoryToFile(){
        manager.clearContent();
        saveTask1(); // сохраняем все виды задач
        // выводим несколько видов задач в разном порядке id для попадания этих задач в историю просмотра
        manager.getTaskById(2);
        manager.getSubTaskById(4);
        manager.getTaskById(1);
       // manager.outIdSubTask(4);
        manager.getEpicById(3);
        // удаляем одну задачу чтобы убедиться что это действие несет изменения сохранения файла истории
        manager.deleteTaskId(1);

        manager.getHistory().forEach(System.out :: println);

        FileBackedTaskManager fileBackedTaskManager = Managers.getDefaultFileBackedTaskManager(taskToList);

        assertEquals(manager.getHistory().toString(), fileBackedTaskManager.getHistory().toString(), "списки истории не совпадают");
        manager.clearContent();
    }

    @Test
    public void qwqw1(){
        manager.clearContent();
        saveTask1(); // сохраняем все виды задач
        // выводим несколько видов задач в разном порядке id для попадания этих задач в историю просмотра

        manager.getTaskById(2);
        manager.getSubTaskById(4);

        manager.getTaskById(1);

        manager.getSubTaskById(3);


        manager.getHistory().forEach(System.out :: println);
/*
         FileBackedTaskManager fileBackedTaskManager = Managers.getDefaultFileBackedTaskManager(taskToList);
        System.out.println("Вывод истории "+fileBackedTaskManager.getHistory().size());
     //   fileBackedTaskManager.clearContent();
        System.out.println("Вывод истории "+fileBackedTaskManager.getHistory().size());


 */
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

