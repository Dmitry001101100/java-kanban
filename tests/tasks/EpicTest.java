package tasks;

import enumeration.Status;
import manager.Managers;
import manager.Task.TaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    TaskManager taskManager = Managers.getDefault();
    Epic epic1 = new Epic("Епик", "описание", taskManager.getIdUp(), Status.NEW, LocalDateTime.now(), Duration.ofMinutes(23));

    @Test
    public void epicHasNewStatusWhenSubtaskListIsEmpty() {// когда список пуст или подзадача имеет статус новый

        taskManager.saveEpic(epic1);

        for (Epic x : taskManager.getEpics()) {
            System.out.println(x);
        }

        System.out.println(taskManager.outIdEpic(epic1.getId()));
        assertEquals(Status.NEW, taskManager.outIdEpic(epic1.getId()).getStatus(), "Статус генерируется неправильно");
    }

    @Test
    public void epicHasNewStatusWhenAllSubtasksAreNew() {// если есть подзадачи со статусом New

        SubTask sub2 = new SubTask(epic1.getId(), "Test titleSub1", "Test in Epic", taskManager.getIdUp(), Status.NEW,
                LocalDateTime.of(24, 7, 4, 10, 17),
                Duration.ofMinutes(15));
        SubTask sub3 = new SubTask(epic1.getId(), "Test titleSub2", "Test in Epic", taskManager.getIdUp(), Status.IN_PROGRESS,
                LocalDateTime.of(25, 3, 25, 16, 40), Duration.ofMinutes(45));

        taskManager.saveEpic(epic1);
        taskManager.saveSubTask(sub2);
        taskManager.saveSubTask(sub3);

        System.out.println(taskManager.outIdEpic(epic1.getId()));
        assertEquals(Status.IN_PROGRESS, taskManager.outIdEpic(epic1.getId()).getStatus(), "Статус рассчитывается неправильно");
    }
/*
    @Test
    public void epiсIsAssignedAnIn_ProcessStatusWhenAtLeastOneSubtaskIsInProgress() {
        // статус у эпика статус в процессе когда не все подзадачи в статусе Done или New
        Epic epic1 = new Epic("Test titleEpic", "Test description", taskManager.getIdUp(), Status.NEW);
        taskManager.saveEpic(epic1);

        int idSub2 = taskManager.getIdUp();
        int idSub3 = taskManager.getIdUp();
        SubTask sub2 = new SubTask(epic1.id, "Test titleSub2", "Test in Epic", idSub2, Status.IN_PROGRESS);
        SubTask sub3 = new SubTask(epic1.id, "Test titleSub3", "Test in Epic", idSub3, Status.NEW);

        taskManager.saveSubTask(sub2);
        taskManager.saveSubTask(sub3);
        assertEquals(Status.IN_PROGRESS, taskManager.outIdEpic(epic1.id).getStatus(), "Статус рассчитывается неправильно");

        SubTask newSub2 = new SubTask(epic1.id, "Test titleSub2", "Test in Epic", idSub2, Status.IN_PROGRESS);
        SubTask newSub3 = new SubTask(epic1.id, "Test titleSub3", "Test in Epic", idSub3, Status.DONE);

        taskManager.saveSubTask(newSub2);
        taskManager.saveSubTask(newSub3);
        assertEquals(Status.IN_PROGRESS, taskManager.outIdEpic(epic1.id).getStatus(), "Статус рассчитывается неправильно");
    }

    @Test
    public void epicHasDoneStatusWhenAllSubtasksAreDone() {
        // у эпика статус Done когда все подзадачи выполнены
        Epic epic1 = new Epic("Test titleEpic", "Test description", taskManager.getIdUp(), Status.NEW);
        taskManager.saveEpic(epic1);

        int idSub2 = taskManager.getIdUp();
        int idSub3 = taskManager.getIdUp();
        SubTask sub2 = new SubTask(epic1.id, "Test titleSub2", "Test in Epic", idSub2, Status.DONE);
        SubTask sub3 = new SubTask(epic1.id, "Test titleSub3", "Test in Epic", idSub3, Status.DONE);

        taskManager.saveSubTask(sub2);
        taskManager.saveSubTask(sub3);
        assertEquals(Status.DONE, taskManager.outIdEpic(epic1.id).getStatus(), "Статус рассчитывается неправильно");
    }
*/
}