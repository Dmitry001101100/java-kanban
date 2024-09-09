package manager;

import enumeration.Status;
import org.junit.jupiter.api.Test;
import tasks.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    TaskManager taskManager = Managers.getDefault();


    // в этом тесте выполняется: проверьте, что экземпляры класса Task равны друг другу, если равен их id;
    @Test
    void addNewTask() {// проверка сохраенния новых задач
        Task task1 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW);

        taskManager.saveTask(task1);

        System.out.println("Проверка на сохранение Task:");

        final int taskId = task1.id;

        final Task savedTask = taskManager.outIdTask(taskId);
        //  System.out.println(savedTask);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1.toString(), savedTask.toString(), "toString() эпиков не совпадает");// в этом месте
        assertEquals(task1.hashCode(), savedTask.hashCode(), "hashCode() задач не совпадает");


        final ArrayList<Task> tasks = taskManager.getTasks();


        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    // Проверьте, что наследники класса Task равны друг другу, если равен их id
    // Не уверен, что я правильно понял идею проверки
    @Test
    void epicInstancesAreEqualWhenTheirIdsEqual() {// так же в этом месте проверяется сохранение подзадач

        Epic epic4 = new Epic("Test titleEpic2", "Test description2", taskManager.getIdUp(), Status.NEW);

        SubTask sub8 = new SubTask(epic4.id, "Test titleSub4", "Test in Epic2", taskManager.getIdUp(), Status.NEW);
        // для этого теста возьмем обьекты epic4 и sub8
        taskManager.saveEpic(epic4);
        taskManager.saveSubTask(sub8);

        Epic savedEpic = taskManager.outIdEpic(epic4.id);
        SubTask savedSubtask = taskManager.outIdSubTask(sub8.id);

        assertEquals(epic4, savedEpic, "Эпики не совпадают");
        assertEquals(epic4.hashCode(), savedEpic.hashCode(), "hashCode() эпиков не совпадает");
        assertEquals(epic4.toString(), savedEpic.toString(), "toString() эпиков не совпадает");
        assertEquals(sub8, savedSubtask, "Подзадачи не совпадают");
        assertEquals(sub8.hashCode(), savedSubtask.hashCode(), "hashCode() подзадач не совпадает");
        assertEquals(sub8.toString(), savedSubtask.toString(), "toString() подзадач не совпадает");
    }

    //проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
    @Test
    void checkingTheSetAndGeneratedId() {// проверка конфликтности заданого и сгенерированнного id
        Task task01 = new Task("Test сгенерированный", "Test сгенерированный", taskManager.getIdUp(), Status.NEW);//id1
        Task task02 = new Task("Test titleTask", "Test заданый", 3, Status.NEW);//id1

        taskManager.saveTask(task01);
        taskManager.saveTask(task02);

        assertEquals(task01, taskManager.outIdTask(1), "Первая задача должна иметь идентификатор 1");
        assertEquals(task02, taskManager.outIdTask(3), "Вторая задача должна иметь идентификатор 2");
    }


    //создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    void checkingForImmutabilityByFields() {// проверка на неизменность задачи по полям после добавления ее через менеджер
        Task task01 = new Task("Test сгенерированный", "Test сгенерированный", taskManager.getIdUp(), Status.NEW);//id1

        taskManager.saveTask(task01);

        assertEquals(task01.getTitle(), taskManager.outIdTask(task01.id).getTitle(), "Изменилось название");
        assertEquals(task01.getDescription(), taskManager.outIdTask(task01.id).getDescription(), "Изменилось описание");
        assertEquals(task01.getId(), taskManager.outIdTask(task01.id).getId(), "Изменилось id");
        assertEquals(task01.getStatus(), taskManager.outIdTask(task01.id).getStatus(), "Изменился статус");


    }


//----------------------------------------------------------------------------------------------------------------------

    // убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
    @Test
    public void shouldReturnInMemoryTaskManagerByDefault() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "task manager не был создан");
        assertInstanceOf(InMemoryTaskManager.class, manager, "неправильный класс task manager");
    }

    @Test
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

    @Test
    void removeTask() {// удаление задачи по id
        Task task2 = new Task("Test titleTask2", "Test description2", taskManager.getIdUp(), Status.NEW);//id1
        taskManager.saveTask(task2);

        System.out.println("Проверка на удаление Task:");

        final int taskId = task2.id;

        taskManager.deleteTaskId(taskId);// удаляем задачу из мапы Таск

        assertNull(taskManager.outIdTask(taskId), "Задача успешно удалена.");// проверка на удаление задачи

        final ArrayList<Task> tasks = taskManager.getTasks();
        System.out.println("Список Task после удаления задачи под id: " + taskId);
        for (Task tas : tasks) {
            System.out.println(tas);
        }

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }


    @Test
    void changeContentTask() {// проверка на изменение задачи
        Task task1 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW);//id1
        taskManager.saveTask(task1);
        final int taskId = task1.id;// выбрали id который хотим изменить
        System.out.println(taskManager.outIdTask(taskId));// проверяем задачу перед изменением
        Task taskNew1 = new Task("New title", "New description", taskId, Status.IN_PROGRESS);//id1
        taskManager.saveTask(taskNew1);

        Task outTask = taskManager.outIdTask(taskId);

        System.out.println(outTask);// проверяем задачу после изменения
        final ArrayList<Task> tasks = taskManager.getTasks();

        assertEquals(taskNew1, outTask, "Задачи не совпадают.");

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteTask() {// удаление всех Task
        Task task1 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW);//id1
        Task task2 = new Task("Test titleTask2", "Test description2", taskManager.getIdUp(), Status.NEW);//id2

        taskManager.saveTask(task1);// сохраняем
        taskManager.saveTask(task2);

        final ArrayList<Task> tasks = taskManager.getTasks();// Вызываем Task для проверки перед удалением

        assertEquals(2, tasks.size(), "Неверное количество задач.");//проверяем длину перед удалением
        taskManager.clearTasks();
        final ArrayList<Task> tasks1 = taskManager.getTasks();// Вызываем Task для проверки перед удалением
        assertEquals(0, tasks1.size(), "Неверное количество задач.");// и после
    }


    @Test
    void deleteEpic() {//проверка на удаление подзадач при удалении эпика
        Epic epic3 = new Epic("Test titleEpic", "Test description", taskManager.getIdUp(), Status.NEW);

        SubTask sub5 = new SubTask(epic3.id, "Test titleSub1", "Test in Epic", taskManager.getIdUp(), Status.NEW);
        SubTask sub6 = new SubTask(epic3.id, "Test titleSub2", "Test in Epic", taskManager.getIdUp(), Status.NEW);
        SubTask sub7 = new SubTask(epic3.id, "Test titleSub3", "Test in Epic", taskManager.getIdUp(), Status.NEW);

        taskManager.saveEpic(epic3);// сохранение

        taskManager.saveSubTask(sub5);
        taskManager.saveSubTask(sub6);
        taskManager.saveSubTask(sub7);

        ArrayList<Epic> epics = taskManager.getEpics();

        assertEquals(1, epics.size(), "Неверное количество эпиков.");//проверяем длину эпиков перед удалением
        assertEquals(3, taskManager.outIdEpic(epic3.id).getSubtaskIds().size(), "Неверное количество подзадач.");//проверяем длину списка подзадач перед удалением

        taskManager.clearEpics();// удаляем все эпики

        ArrayList<Epic> epics1 = taskManager.getEpics();

        assertEquals(0, epics1.size(), "Неверное количество эпиков.");//проверяем длину эпиков после удаления
        assertEquals(0, taskManager.getSubTasks().size(), "Неверное количество подзадач.");//проверяем длину списка подзадач после удаления
    }

    @Test
    void removeEpic() {// удаление эпика по id
        Epic epic3 = new Epic("Test titleEpic", "Test description", taskManager.getIdUp(), Status.NEW);
        taskManager.saveEpic(epic3);// сохранение

        ArrayList<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size(), "Неверное количество эпиков.");//проверяем длину эпиков перед удалением

        taskManager.clearEpics();// удаляем все эпики

        ArrayList<Epic> epics1 = taskManager.getEpics();
        assertEquals(0, epics1.size(), "Неверное количество эпиков.");//проверяем длину эпиков после удаления

    }

    @Test
    void removeSubtask() {// удаление подзадачи по id
        Epic epic3 = new Epic("Test titleEpic", "Test description", taskManager.getIdUp(), Status.NEW);
        SubTask sub5 = new SubTask(epic3.id, "Test titleSub1", "Test in Epic", taskManager.getIdUp(), Status.NEW);
        SubTask sub6 = new SubTask(epic3.id, "Test titleSub1", "Test in Epic", taskManager.getIdUp(), Status.NEW);

        taskManager.saveEpic(epic3);// сохранение
        taskManager.saveSubTask(sub5);
        taskManager.saveSubTask(sub6);

        ArrayList<SubTask> subs = taskManager.getSubTasksId(epic3.id);

        assertEquals(2, taskManager.outIdEpic(epic3.id).getSubtaskIds().size(), "Неверное количество id подзадач в списке эпиков.");//проверяем длину списка подзадч у эпика
        assertEquals(2, subs.size(), "Неверное количество подзадач.");//проверяем длину списка подзадач по id эпика перед удалением

        taskManager.deleteSubTaskId(sub5.id);// удаляем одну подзадачу по id

        ArrayList<SubTask> subs1 = taskManager.getSubTasksId(epic3.id);


        assertEquals(1, taskManager.outIdEpic(epic3.id).getSubtaskIds().size(), "Неверное количество id подзадач в списке эпиков.");//проверяем длину списка подзадч у эпика
        assertEquals(1, subs1.size(), "Неверное количество подзадач.");//проверяем длину списка подзадач по id эпика перед удалением
        assertEquals(sub6.toString(),taskManager.outIdSubTask(sub6.id).toString(),"toString() подзадачи не совпадает");
        // проверяем что осталась та подзадача из 2 которая должна остаться

    }

    @Test
    void clerSubTaskofEpic() { //проверка удаления всех эпиков у одного эпика
        Epic epic3 = new Epic("Test titleEpic", "Test description", taskManager.getIdUp(), Status.NEW);
        SubTask sub5 = new SubTask(epic3.id, "Test titleSub1", "Test in Epic", taskManager.getIdUp(), Status.NEW);
        SubTask sub6 = new SubTask(epic3.id, "Test titleSub1", "Test in Epic", taskManager.getIdUp(), Status.NEW);

        taskManager.saveEpic(epic3);// сохранение
        taskManager.saveSubTask(sub5);
        taskManager.saveSubTask(sub6);

        ArrayList<SubTask> subs = taskManager.getSubTasksId(epic3.id);

        assertEquals(2, taskManager.outIdEpic(epic3.id).getSubtaskIds().size(), "Неверное количество id подзадач в списке эпиков.");//проверяем длину списка подзадч у эпика
        assertEquals(2, subs.size(), "Неверное количество подзадач.");//проверяем длину списка подзадач по id эпика перед удалением

        taskManager.clearSubTasksOfEpic(epic3.id);// удаляем одну подзадачу по id

        ArrayList<SubTask> subs1 = taskManager.getSubTasksId(epic3.id);


        assertEquals(0, taskManager.outIdEpic(epic3.id).getSubtaskIds().size(), "Неверное количество id подзадач в списке эпиков.");//проверяем длину списка подзадч у эпика
        assertEquals(0, subs1.size(), "Неверное количество подзадач.");//проверяем длину списка подзадач по id эпика перед удалением

    }

    @Test
    void clearTasks(){// полное удаление всех видов задач
        Task task1 = new Task("Test titleTask", "Test description", taskManager.getIdUp(), Status.NEW);//id1

        Epic epic3 = new Epic("Test titleEpic", "Test description", taskManager.getIdUp(), Status.NEW);
        SubTask sub5 = new SubTask(epic3.id, "Test titleSub1", "Test in Epic", taskManager.getIdUp(), Status.NEW);

        taskManager.saveTask(task1);// сохраняем
        taskManager.saveEpic(epic3);
        taskManager.saveSubTask(sub5);

        //проверяем длину списков систем хранения перед полным удалением
        assertEquals(1, taskManager.getTasks().size(), "Неверное количество задач.");
        assertEquals(1, taskManager.getEpics().size(), "Неверное эпиков задач.");
        assertEquals(1, taskManager.getSubTasksId(epic3.id).size(), "Неверное количество подзадач.");

        taskManager.clearContent();

        assertEquals(0, taskManager.getTasks().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getEpics().size(), "Неверное эпиков задач.");
        assertEquals(0, taskManager.getSubTasks().size(), "Неверное количество подзадач.");

    }
// ----------------------- проверка обновленной истории ---------------------------------------------------------------

    Task task1 = new Task("Задача1", "Описание задачи1", 1, Status.NEW);//id1
    Epic epic1 = new Epic("Эпик1", "Описание эпик1", 2, Status.NEW);//id3
    Epic epic2 = new Epic("Эпик2", "Описание эпик2", 3, Status.NEW);//id4
    SubTask sub1 = new SubTask(epic1.id, "Подазадча 1", "от эпик1", 4, Status.NEW);//id5
    SubTask sub2 = new SubTask(epic2.id, "Подзадача 2", "от эпик2", 5, Status.NEW);//id6
    Task task2 = new Task("Задача2", "Описание задачи1", 6, Status.NEW);//id1
    Epic epic12 = new Epic("Эпик3", "Описание эпик1", 7, Status.NEW);//id3
    Epic epic22 = new Epic("Эпик4", "Описание эпик2", 8, Status.NEW);//id4
    SubTask sub12 = new SubTask(epic1.id, "Подазадча 3", "от эпик1", 9, Status.NEW);//id5
    SubTask sub22 = new SubTask(epic2.id, "Подзадача 4", "от эпик2", 10, Status.NEW);//id6
    Epic epic13 = new Epic("Эпик2", "Описание эпик1", 11, Status.NEW);//id3



    @Test
    public void addAndGetTaskHis(){// сохраняем задачи в истории
        taskManager.saveTask(task1);
        taskManager.saveEpic(epic1);
        taskManager.saveEpic(epic2);
        taskManager.saveSubTask(sub1);
        taskManager.saveSubTask(sub2);
        taskManager.saveTask(task2);
        taskManager.saveEpic(epic12);
        taskManager.saveEpic(epic22);
        taskManager.saveSubTask(sub22);
        taskManager.saveSubTask(sub12);
        taskManager.saveEpic(epic13);

        taskManager.outIdTask(task1.id);
        taskManager.outIdEpic(epic1.id);
        taskManager.outIdEpic(epic2.id);
        taskManager.outIdSubTask(sub1.id);
        taskManager.outIdSubTask(sub2.id);
        taskManager.outIdTask(task2.id);
        taskManager.outIdEpic(epic12.id);
        taskManager.outIdEpic(epic22.id);
        taskManager.outIdSubTask(sub22.id);
        taskManager.outIdSubTask(sub12.id);
        taskManager.outIdEpic(epic13.id);
    }


    @Test
    void getHistory(){// проверка может ли история сохранять больше 10 элементов
        addAndGetTaskHis();
        List<Task> list = taskManager.getHistory();
        for (Task tas : list){
            System.out.println(tas);
        }
    }

    @Test
    void removeTaskHis(){// проверка н аповедение при удалении
        assertEquals(0, taskManager.getHistory().size(), "Длина списка должна быть равна 0");
        taskManager.saveTask(task1);
        taskManager.saveEpic(epic1);
        taskManager.saveEpic(epic2);
        taskManager.outIdTask(task1.id);
        taskManager.outIdEpic(epic1.id);
        taskManager.outIdEpic(epic2.id);

        assertEquals(3, taskManager.getHistory().size(), "Длина списка должна быть равна 3");

        taskManager.deleteEpicId(epic1.id);
        assertEquals(2, taskManager.getHistory().size(), "Длина списка должна быть равна 2");

    }

    @Test
    void getUniqueHistory(){// проверка на хранение только уникальной истории
        assertEquals(0, taskManager.getHistory().size(), "Длина списка должна быть равна 0");
        taskManager.saveTask(task1);

        taskManager.outIdTask(task1.id);
        assertEquals(1, taskManager.getHistory().size(), "Длина списка должна быть равна 1");

        taskManager.outIdTask(task1.id);
        assertEquals(1, taskManager.getHistory().size(), "Длина списка должна быть равна 1");
    }


    @Test
    void historyIsEmpty() {// проверка не пуст ли список
        assertEquals(0, taskManager.getHistory().size(), "Длина списка должна быть равна 0");
    }


}

