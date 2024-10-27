package tasks;

import enumeration.Status;
import manager.Managers;
import manager.Task.TaskManager;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    TaskManager taskManager = Managers.getDefault();
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");
    Epic epic1 = new Epic("Епик", "описание", taskManager.getIdUp(), Status.NEW, LocalDateTime.now(), Duration.ofMinutes(20));

    SubTask sub2 = new SubTask(epic1.getId(), "Test titleSub1", "Test in Epic", taskManager.getIdUp(), Status.NEW,
            LocalDateTime.of(24, 7, 4, 10, 17), Duration.ofMinutes(15));
    SubTask sub3 = new SubTask(epic1.getId(), "Test titleSub2", "Test in Epic", taskManager.getIdUp(), Status.IN_PROGRESS,
            LocalDateTime.of(25, 3, 25, 16, 40), Duration.ofMinutes(45));

    @Test
    public void epicHasNewStatusWhenSubtaskListIsEmpty() {// когда список пуст или подзадача имеет статус новый

        taskManager.saveEpic(epic1);

        System.out.println(taskManager.outIdEpic(epic1.getId()));
        assertEquals(Status.NEW, taskManager.outIdEpic(epic1.getId()).getStatus(), "Статус генерируется неправильно");

        taskManager.saveSubTask(sub2);
        assertEquals(Status.NEW, taskManager.outIdEpic(epic1.getId()).getStatus(), "Статус генерируется неправильно");
    }

    @Test
    public void epicHasNewStatusWhenAllSubtasksAreNew() {// если есть подзадачи со статусом New


        taskManager.saveEpic(epic1);
        taskManager.saveSubTask(sub2);
        taskManager.saveSubTask(sub3);

        System.out.println(taskManager.outIdEpic(epic1.getId()));
        assertEquals(Status.IN_PROGRESS, taskManager.outIdEpic(epic1.getId()).getStatus(), "Статус рассчитывается неправильно");
    }

    @Test
    public void epiсIsAssignedAnIn_ProcessStatusWhenAtLeastOneSubtaskIsInProgress() {
        // статус у эпика статус в процессе когда не все подзадачи в статусе Done или New
        taskManager.saveEpic(epic1);

        taskManager.saveSubTask(sub2);
        taskManager.saveSubTask(sub3);
        assertEquals(Status.IN_PROGRESS, taskManager.outIdEpic(epic1.getId()).getStatus(), "Статус рассчитывается неправильно");

        // меняем статус на выполнен и проверяем чтобы статус у эпика оставался в процессе
        sub2.setStatus(Status.DONE);
        taskManager.saveSubTask(sub2);

        assertEquals(Status.IN_PROGRESS, taskManager.outIdEpic(epic1.getId()).getStatus(), "Статус рассчитывается неправильно");
    }

    @Test
    public void epicHasDoneStatusWhenAllSubtasksAreDone() {
        // у эпика статус Done когда все подзадачи выполнены
        taskManager.saveEpic(epic1);
        assertEquals(Status.NEW, taskManager.outIdEpic(epic1.getId()).getStatus(), "Статус рассчитывается неправильно");

        // меняем статус у подзадач на выполнен
        sub2.setStatus(Status.DONE);
        sub3.setStatus(Status.DONE);

        taskManager.saveSubTask(sub2);
        taskManager.saveSubTask(sub3);
        assertEquals(Status.DONE, taskManager.outIdEpic(epic1.getId()).getStatus(), "Статус рассчитывается неправильно");
    }

    @Test
    public void temporaryVerificationOfTheEpic(){
        // конечное время до сохранения подзадач
        taskManager.saveEpic(epic1);

        LocalDateTime endTimeEpic = epic1.getStartTime().plus(epic1.getDuration());
        LocalDateTime endTimeIsTaskManager = taskManager.outIdEpic(epic1.getId()).getEndTime();

       // System.out.println(endTimeEpic.format(DATE_TIME_FORMATTER));
       // System.out.println(endTimeIsTaskManager.format(DATE_TIME_FORMATTER));
        assertEquals(endTimeEpic.format(DATE_TIME_FORMATTER),endTimeIsTaskManager.format(DATE_TIME_FORMATTER), "Конечное время насчитывается неправильно");

        // временые рамки после сохранения подзадач
         taskManager.saveSubTask(sub2);
         taskManager.saveSubTask(sub3);

         // конечное время расчитывается по самому длинному конечному времени подзадачи.

         endTimeEpic = sub3.getStartTime().plus(epic1.getDuration());
         LocalDateTime endTimeIsTaskManager1 = taskManager.outIdEpic(epic1.getId()).getEndTime();

        System.out.println(endTimeEpic.format(DATE_TIME_FORMATTER));
        System.out.println(endTimeIsTaskManager1.format(DATE_TIME_FORMATTER));


    }
}