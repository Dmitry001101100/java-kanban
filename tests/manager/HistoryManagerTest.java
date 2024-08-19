package manager;

import enumeration.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryManagerTest {

    protected HistoryManager historyManager;

    Task task1 = new Task("Задача1", "Описание задачи1", 1, Status.NEW);//id1
    Epic epic1 = new Epic("Эпик1", "Описание эпик1", 2, Status.NEW);//id3
    Epic epic2 = new Epic("Эпик2", "Описание эпик2", 3, Status.NEW);//id4
    SubTask sub1 = new SubTask(epic1.id, "Подазадча 1", "от эпик1", 4, Status.NEW);//id5
    SubTask sub2 = new SubTask(epic2.id, "Подзадача 2", "от эпик2", 5, Status.NEW);//id6

    @BeforeEach
    void beforeEach(){
        historyManager = Managers.getDefaultHistory();
    }


    @Test
    public void saveTasks(){// сохраняем задачи в истории
        historyManager.setHistory(task1);
        historyManager.setHistory(epic1);
        historyManager.setHistory(epic2);
        historyManager.setHistory(sub1);
        historyManager.setHistory(sub2);
    }

    @Test
    public void setHistory(){// проверка сохранения
        saveTasks();
        ArrayList<Task> list = historyManager.getHistory();

        assertEquals(task1, list.get(0), "Задача 1 должна быть первой в списке");
        assertEquals(epic1, list.get(1), "Эпик 1 должен быть вторым в списке");
        assertEquals(epic2, list.get(2), "Эпик 2 должна быть третьей в списке");
        assertEquals(sub1, list.get(3), "Подзадача 1 должна быть четвертой в списке");
        assertEquals(sub2, list.get(4), "Подзадача 2 должна быть пятой в списке");

    }
    // в тесте представленном ниже показывается
// убеждение, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    void getHistory() {// проверка вывода
        saveTasks();
        List<Task> list = historyManager.getHistory();
        for (Task tas : list){
            System.out.println(tas);
        }

        assertEquals(5, list.size(), "Длина списка должна быть равна 3");

        System.out.println("\n"+"Если в списке больше 10 задач");
        saveTasks();//повторно сохранили задачи с id 1 2 3 4 5 (получается сейчас в списке 1 2 3 4 5 1 2 3 4 5)
        historyManager.setHistory(epic1);// сохраняем в общей сложности 11 задач(id последней задачи 2)
        List<Task> list1 = historyManager.getHistory();
        for (Task tas : list1){// проверяем по выводу
            System.out.println(tas);
        }
        assertEquals(10, list.size(), "Длина списка должна быть равна 10");

        assertEquals(epic1, list.get(0), "Эпик 1 должен быть первым в списке");
        assertEquals(epic2, list.get(1), "Эпик 2 должен быть вторым в списке");
        assertEquals(sub1, list.get(2), "Подзадача 1 должна быть третей в списке");
        assertEquals(sub2, list.get(3), "Подзадача 2 должна быть четвертой в списке");
        assertEquals(task1, list.get(4), "Задача 1 должна быть пятой в списке");

        assertEquals(epic1, list.get(5), "Эпик 1 должен быть шестой в списке");
        assertEquals(epic2, list.get(6), "Эпик 2 должен быть седьмой в списке");
        assertEquals(sub1, list.get(7), "Подзадача 1 должна быть восьмой в списке");
        assertEquals(sub2, list.get(8), "Подзадача 2 должна быть девятой в списке");
        assertEquals(epic1, list.get(9), "Эпик 2 должен быть десятый в списке");
    }

    @Test
    void historyIsEmpty() {
        assertEquals(0, historyManager.getHistory().size(), "Длина списка должна быть равна 0");
    }
}
