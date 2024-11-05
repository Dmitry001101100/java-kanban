package manager.Task;

import enumeration.Status;
import manager.History.HistoryManager;
import manager.History.InMemoryHistoryManager;
import manager.Managers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import tasks.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class AbstractTaskManagerTest {

    TaskManager taskManager = Managers.getDefault();


    Task task1 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW,
            LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(140));
    Task task2 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW,
            LocalDateTime.of(2024, 12, 23, 14, 42), Duration.ofMinutes(12));

    Epic epic3 = new Epic("Епик", "описание", taskManager.getIdUp(), Status.NEW,
            LocalDateTime.now(), Duration.ofMinutes(20));
    SubTask sub4 = new SubTask(epic3.getId(), "Test titleSub1", "Test in Epic", taskManager.getIdUp(), Status.IN_PROGRESS,
            LocalDateTime.of(24, 12, 4, 10, 17), Duration.ofMinutes(24));
    SubTask sub5 = new SubTask(epic3.getId(), "Test titleSub2", "Test in Epic", taskManager.getIdUp(), Status.NEW,
            LocalDateTime.of(24, 8, 25, 16, 40), Duration.ofMinutes(12));
    SubTask sub7 = new SubTask(epic3.getId(), "Test titleSub2", "Test in Epic", taskManager.getIdUp(), Status.DONE,
            LocalDateTime.of(24, 8, 25, 16, 40), Duration.ofMinutes(12));


    public void savesTask(TaskManager taskManager) { // сохраняем все виды задач

        Task task1 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.of(2024, 2, 14, 14, 42), Duration.ofMinutes(14)); // 1
        Task task2 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(11)); // 2
        Epic epic3 = new Epic("Епик", "описание", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));                                       // 3
        SubTask sub4 = new SubTask(epic3.getId(), "Test titleSub1", "Test in Epic", taskManager.getIdUp(), Status.IN_PROGRESS,
                LocalDateTime.of(24, 12, 4, 10, 17), Duration.ofMinutes(24)); // 4
        SubTask sub5 = new SubTask(epic3.getId(), "Test titleSub2", "Test in Epic", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.of(24, 8, 25, 16, 40), Duration.ofMinutes(12)); // 5

        taskManager.saveTask(task1);
        taskManager.saveTask(task2);
        taskManager.saveEpic(epic3);
        taskManager.saveSubTask(sub4);
        taskManager.saveSubTask(sub5);
    }


    // в этом тесте выполняется: проверьте, что экземпляры класса Task равны друг другу, если равен их id;
    protected void taskToTask(TaskManager taskManager) {// проверка сохраенния новых задач
        Task task1 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(140));

        taskManager.saveTask(task1);

        System.out.println("Проверка на сохранение Task:");

        final int taskId = task1.getId();

        final Task savedTask = taskManager.outIdTask(taskId);
        System.out.println(savedTask);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1.toString(), savedTask.toString(), "toString() эпиков не совпадает");// в этом месте
        assertEquals(task1.hashCode(), savedTask.hashCode(), "hashCode() задач не совпадает");

        final ArrayList<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.getFirst(), "Задачи не совпадают.");
        taskManager.clearContent();
    }

    // Проверьте, что наследники класса Task равны друг другу, если равен их id
    // Не уверен, что я правильно понял идею проверки

    void epicInstancesAreEqualWhenTheirIdsEqual(TaskManager taskManager) {// так же в этом месте проверяется сохранение подзадач


        Epic epic1 = new Epic("Епик", "описание", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));

        SubTask sub2 = new SubTask(epic1.getId(), "Test titleSub1", "Test in Epic", taskManager.getIdUp(), Status.IN_PROGRESS,
                LocalDateTime.of(24, 12, 4, 10, 17), Duration.ofMinutes(15));

        taskManager.saveEpic(epic1);
        taskManager.saveSubTask(sub2);

        Epic savedEpic = taskManager.outIdEpic(epic1.getId());
        SubTask savedSubtask = taskManager.outIdSubTask(sub2.getId());

        assertEquals(epic1, savedEpic, "Эпики не совпадают");
        assertEquals(epic1.hashCode(), savedEpic.hashCode(), "hashCode() эпиков не совпадает");
        assertEquals(epic1.toString(), savedEpic.toString(), "toString() эпиков не совпадает");
        assertEquals(sub2, savedSubtask, "Подзадачи не совпадают");
        assertEquals(sub2.hashCode(), savedSubtask.hashCode(), "hashCode() подзадач не совпадает");
        assertEquals(sub2.toString(), savedSubtask.toString(), "toString() подзадач не совпадает");
    }

    //проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;

    void checkingTheSetAndGeneratedId(TaskManager taskManager) {// проверка конфликтности заданого и сгенерированнного id
        Task task1 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(140));
        Task task2 = new Task("Test titleTask", "Test description", 2, Status.NEW,
                LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(140));
        taskManager.saveTask(task1);
        taskManager.saveTask(task2);

        assertEquals(task1, taskManager.outIdTask(1), "Первая задача должна иметь идентификатор 1");
        assertEquals(task2, taskManager.outIdTask(2), "Вторая задача должна иметь идентификатор 2");
    }


    //создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер

    void checkingForImmutabilityByFields(TaskManager taskManager) {// проверка на неизменность задачи по полям после добавления ее через менеджер

        taskManager.saveTask(task1);

        assertEquals(task1.getTitle(), taskManager.outIdTask(task1.getId()).getTitle(), "Изменилось название");
        assertEquals(task1.getDescription(), taskManager.outIdTask(task1.getId()).getDescription(), "Изменилось описание");
        assertEquals(task1.getId(), taskManager.outIdTask(task1.getId()).getId(), "Изменилось id");
        assertEquals(task1.getStatus(), taskManager.outIdTask(task1.getId()).getStatus(), "Изменился статус");
    }


//----------------------------------------------------------------------------------------------------------------------

    // убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;

    public void shouldReturnInMemoryTaskManagerByDefault() {
        taskManager = Managers.getDefault();
        assertNotNull(taskManager, "task manager не был создан");
        assertInstanceOf(InMemoryTaskManager.class, taskManager, "неправильный класс task manager");
    }


    public void shouldReturnInMemoryHistoryManagerByDefault() {
        HistoryManager manager = Managers.getDefaultHistory();
        assertNotNull(manager, "history manager не был создан");
        assertInstanceOf(InMemoryHistoryManager.class, manager, "неправильный класс history manager");
    }


    // Проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи
    // проверьте, что объект Subtask нельзя сделать своим же эпиком
    /* Такие проверки невозможно выполнить, т.к. методы по созданию
    задач/подзадач/эпиков принимают объекты определённого типа */


    //-------------------------------------Остальные тесты-----------------------------------------------------------------------------


    void removeTask(TaskManager taskManager) {// удаление задачи по id

        taskManager.saveTask(task2);

        int idTask = task2.getId();

        System.out.println("Проверка на удаление Task:");
        taskManager.deleteTaskId(idTask);// удаляем задачу из мапы Таск
        final ArrayList<Task> tasks = taskManager.getTasks();
        System.out.println("Список Task после удаления задачи под id: " + idTask);

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }


    void changeContentTask(TaskManager taskManager) {// проверка на изменение задачи

        taskManager.saveTask(task1);
        final int taskId = task1.getId();// выбрали id который хотим изменить
        System.out.println(taskManager.outIdTask(taskId));// проверяем задачу перед изменением
        Task taskNew1 = new Task("Test titleTask", "Test description", taskId, Status.NEW,
                LocalDateTime.of(2024, 12, 23, 14, 42), Duration.ofMinutes(12));
        taskManager.saveTask(taskNew1);

        Task outTask = taskManager.outIdTask(taskId);

        System.out.println(outTask);// проверяем задачу после изменения
        final ArrayList<Task> tasks = taskManager.getTasks();

        assertEquals(taskNew1, outTask, "Задачи не совпадают.");

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }


    void deleteTask(TaskManager taskManager) {// удаление всех Task


        taskManager.saveTask(task1);// сохраняем
        taskManager.saveTask(task2);

        final ArrayList<Task> tasks = taskManager.getTasks();// Вызываем Task для проверки перед удалением

        assertEquals(2, tasks.size(), "Неверное количество задач.");//проверяем длину перед удалением
        taskManager.clearTasks();
        final ArrayList<Task> tasks1 = taskManager.getTasks();// Вызываем Task для проверки перед удалением
        assertEquals(0, tasks1.size(), "Неверное количество задач.");// и после
    }


    void deleteEpic(TaskManager taskManager) {//проверка на удаление подзадач при удалении эпика

        savesTask(taskManager);

        ArrayList<Epic> epics = taskManager.getEpics();

        assertEquals(1, epics.size(), "Неверное количество эпиков.");//проверяем длину эпиков перед удалением
        assertEquals(3, taskManager.outIdEpic(epic3.getId()).getSubtaskIds().size(), "Неверное количество подзадач.");//проверяем длину списка подзадач перед удалением

        taskManager.clearEpics();// удаляем все эпики

        ArrayList<Epic> epics1 = taskManager.getEpics();

        assertEquals(0, epics1.size(), "Неверное количество эпиков.");//проверяем длину эпиков после удаления
        assertEquals(0, taskManager.getSubTasks().size(), "Неверное количество подзадач.");//проверяем длину списка подзадач после удаления
    }


    void removeEpic(TaskManager taskManager) {// удаление эпика по id
        Epic epic3 = new Epic("Епик", "описание", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        taskManager.saveEpic(epic3);// сохранение

        ArrayList<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size(), "Неверное количество эпиков.");//проверяем длину эпиков перед удалением

        taskManager.clearEpics();// удаляем все эпики

        ArrayList<Epic> epics1 = taskManager.getEpics();
        assertEquals(0, epics1.size(), "Неверное количество эпиков.");//проверяем длину эпиков после удаления

    }


    void removeSubtask(TaskManager taskManager) {// удаление подзадачи по id
        taskManager.saveEpic(epic3);
        taskManager.saveSubTask(sub7);
        taskManager.saveSubTask(sub5);

        ArrayList<SubTask> subs = taskManager.getSubTasksId(epic3.getId());

        assertEquals(2, taskManager.outIdEpic(epic3.getId()).getSubtaskIds().size(), "Неверное количество id подзадач в списке эпиков.");//проверяем длину списка подзадч у эпика
        assertEquals(2, subs.size(), "Неверное количество подзадач.");//проверяем длину списка подзадач по id эпика перед удалением

        int dellId = sub5.getId();
        taskManager.deleteSubTaskId(dellId);// удаляем одну подзадачу по id

        ArrayList<SubTask> subs1 = taskManager.getSubTasksId(epic3.getId());


        assertEquals(1, taskManager.outIdEpic(epic3.getId()).getSubtaskIds().size(), "Неверное количество id подзадач в списке эпиков.");//проверяем длину списка подзадч у эпика
        assertEquals(1, subs1.size(), "Неверное количество подзадач.");//проверяем длину списка подзадач по id эпика перед удалением
        assertEquals(sub7.toString(), taskManager.outIdSubTask(sub7.getId()).toString(), "toString() подзадачи не совпадает");
        // проверяем что осталась та подзадача из 2 которая должна остаться


    }


    void clerSubTaskofEpic(TaskManager taskManager) { //проверка удаления всех эпиков у одного эпика

        taskManager.saveEpic(epic3);// сохранение
        taskManager.saveSubTask(sub5);
        taskManager.saveSubTask(sub7);

        ArrayList<SubTask> subs = taskManager.getSubTasksId(epic3.getId());
        System.out.println(taskManager.outIdEpic(epic3.getId()));

        assertEquals(2, taskManager.outIdEpic(epic3.getId()).getSubtaskIds().size(), "Неверное количество id подзадач в списке эпиков.");//проверяем длину списка подзадч у эпика
        assertEquals(2, subs.size(), "Неверное количество подзадач.");//проверяем длину списка подзадач по id эпика перед удалением

        taskManager.clearSubTasksOfEpic(epic3.getId());

        System.out.println(taskManager.outIdEpic(epic3.getId()));

        ArrayList<SubTask> subs1 = taskManager.getSubTasksId(epic3.getId());


        assertEquals(0, taskManager.outIdEpic(epic3.getId()).getSubtaskIds().size(), "Неверное количество id подзадач в списке эпиков.");//проверяем длину списка подзадч у эпика
        assertEquals(0, subs1.size(), "Неверное количество подзадач.");//проверяем длину списка подзадач по id эпика перед удалением

    }


    void clearTasks(TaskManager taskManager) {// полное удаление всех видов задач
        savesTask(taskManager);

        //проверяем длину списков систем хранения перед полным удалением
        assertEquals(2, taskManager.getTasks().size(), "Неверное количество задач.");
        assertEquals(1, taskManager.getEpics().size(), "Неверное эпиков задач.");
        assertEquals(2, taskManager.getSubTasks().size(), "Неверное количество подзадач.");

        taskManager.clearContent();

        assertEquals(0, taskManager.getTasks().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getEpics().size(), "Неверное эпиков задач.");
        assertEquals(0, taskManager.getSubTasks().size(), "Неверное количество подзадач.");

    }
// ----------------------- проверка обновленной истории ---------------------------------------------------------------


    public void addAndGetTaskHis(TaskManager taskManager) {// сохраняем задачи в истории
        Task task1 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(140));
        Task task2 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.of(2024, 12, 23, 14, 42), Duration.ofMinutes(12));

        Epic epic3 = new Epic("Епик", "описание", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        SubTask sub4 = new SubTask(epic3.getId(), "Test titleSub1", "Test in Epic", taskManager.getIdUp(), Status.IN_PROGRESS,
                LocalDateTime.of(24, 12, 4, 10, 17), Duration.ofMinutes(24));
        SubTask sub5 = new SubTask(epic3.getId(), "Test titleSub2", "Test in Epic", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.of(24, 8, 25, 16, 40), Duration.ofMinutes(12));
        SubTask sub7 = new SubTask(epic3.getId(), "Test titleSub2", "Test in Epic", taskManager.getIdUp(), Status.DONE,
                LocalDateTime.of(24, 8, 25, 16, 40), Duration.ofMinutes(12));


        Task task3 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.of(2024, 12, 14, 14, 42), Duration.ofMinutes(140));
        Task task4 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.of(2024, 12, 23, 14, 42), Duration.ofMinutes(12));

        Epic epic4 = new Epic("Епик", "описание", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20));
        SubTask sub1 = new SubTask(epic3.getId(), "Test titleSub1", "Test in Epic", taskManager.getIdUp(), Status.IN_PROGRESS,
                LocalDateTime.of(24, 12, 4, 10, 17), Duration.ofMinutes(24));
        SubTask sub2 = new SubTask(epic3.getId(), "Test titleSub2", "Test in Epic", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.of(24, 8, 25, 16, 40), Duration.ofMinutes(12));

        taskManager.saveTask(task1);
        taskManager.saveTask(task2);
        taskManager.saveEpic(epic3);
        taskManager.saveSubTask(sub4);
        taskManager.saveSubTask(sub5);
        taskManager.saveSubTask(sub7);

        taskManager.saveTask(task3);
        taskManager.saveTask(task4);
        taskManager.saveEpic(epic4);
        taskManager.saveSubTask(sub1);
        taskManager.saveSubTask(sub2);

        taskManager.outIdTask(task1.getId());
        taskManager.outIdEpic(epic3.getId());
        taskManager.outIdSubTask(sub4.getId());
        taskManager.outIdSubTask(sub5.getId());
        taskManager.outIdTask(task2.getId());
        taskManager.outIdEpic(epic3.getId());
        taskManager.outIdTask(task3.getId());
        taskManager.outIdTask(task4.getId());
        taskManager.outIdEpic(epic4.getId());
        taskManager.outIdSubTask(sub1.getId());
        taskManager.outIdSubTask(sub2.getId());

    }


    void getHistory(TaskManager taskManager) {// проверка может ли история сохранять больше 10 элементов
        addAndGetTaskHis(taskManager);
        List<Task> list = taskManager.getHistory();
        for (Task tas : list) {
            System.out.println(tas);
        }
        assertEquals(10, list.size(), "Длина списка должна быть равна 3");
    }


    void removeTaskHis(TaskManager taskManager) {// проверка на поведение при удалении
        assertEquals(0, taskManager.getHistory().size(), "Длина списка должна быть равна 0");
        taskManager.saveTask(task1);
        taskManager.saveEpic(epic3);

        taskManager.outIdTask(task1.getId());
        taskManager.outIdEpic(epic3.getId());

        assertEquals(2, taskManager.getHistory().size(), "Длина списка должна быть равна 2");

    }


    void getUniqueHistory(TaskManager taskManager) {// проверка на хранение только уникальной истории
        assertEquals(0, taskManager.getHistory().size(), "Длина списка должна быть равна 0");
        taskManager.saveTask(task1);

        taskManager.outIdTask(task1.getId());
        assertEquals(1, taskManager.getHistory().size(), "Длина списка должна быть равна 1");

        taskManager.outIdTask(task1.getId());
        assertEquals(1, taskManager.getHistory().size(), "Длина списка должна быть равна 1");
    }


    void historyIsEmpty(TaskManager taskManager) {// проверка не пуст ли список
        assertEquals(0, taskManager.getHistory().size(), "Длина списка должна быть равна 0");
    }


}

