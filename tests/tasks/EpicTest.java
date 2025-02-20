package tasks;

import enumeration.Status;
import manager.Managers;
import manager.Task.TaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EpicTest {

    TaskManager taskManager = Managers.getDefault();
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");
    Epic epic1 = new Epic("Епик", "описание", taskManager.getIdUp(), Status.NEW,
            LocalDateTime.now(), Duration.ofMinutes(20));

    SubTask sub2 = new SubTask(epic1.getId(), "Test titleSub1", "Test in Epic", taskManager.getIdUp(), Status.IN_PROGRESS,
            LocalDateTime.of(24, 12, 4, 10, 17), Duration.ofMinutes(15));
    SubTask sub3 = new SubTask(epic1.getId(), "Test titleSub2", "Test in Epic", taskManager.getIdUp(), Status.NEW,
            LocalDateTime.of(24, 3, 25, 16, 40), Duration.ofMinutes(45));

    @Test
    public void epicHasNewStatusWhenSubtaskListIsEmpty() {// когда список пуст или подзадача имеет статус новый

        taskManager.createEpic(epic1);

        //   System.out.println("до добавления подзадачи "+taskManager.getEpicById(epic1.getId()));
        assertEquals(Status.NEW, taskManager.getEpicById(epic1.getId()).getStatus(), "Статус генерируется неправильно");

        taskManager.createSubTask(sub2);
        taskManager.createSubTask(sub3);
       // System.out.println("после добавления"+taskManager.getEpicById(epic1.getId()));
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus(), "Статус генерируется неправильно");
       // taskManager.clearSubTasksOfEpic(epic1.getId());
        System.out.println("после удаления"+taskManager.getEpicById(epic1.getId()));
    }

    @Test
    public void epicHasNewStatusWhenAllSubtasksAreNew() {// если есть подзадачи со статусом New

        taskManager.createEpic(epic1);
        taskManager.createSubTask(sub2);
        taskManager.createSubTask(sub3);

        // System.out.println(taskManager.getEpicById(epic1.getId()));
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus(), "Статус рассчитывается неправильно");
    }

    @Test
    public void epiсIsAssignedAnIn_ProcessStatusWhenAtLeastOneSubtaskIsInProgress() {
        // статус у эпика статус в процессе когда не все подзадачи в статусе Done или New
        taskManager.createEpic(epic1);

        taskManager.createSubTask(sub2);
        taskManager.createSubTask(sub3);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus(), "Статус рассчитывается неправильно");

        // меняем статус на выполнен и проверяем чтобы статус у эпика оставался в процессе
        sub2.setStatus(Status.DONE);
        taskManager.createSubTask(sub2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus(), "Статус рассчитывается неправильно");
    }

    @Test
    public void epicHasDoneStatusWhenAllSubtasksAreDone() {
        // у эпика статус Done когда все подзадачи выполнены
        taskManager.createEpic(epic1);
        assertEquals(Status.NEW, taskManager.getEpicById(epic1.getId()).getStatus(), "Статус рассчитывается неправильно");

        // меняем статус у подзадач на выполнен
        sub2.setStatus(Status.DONE);
        sub3.setStatus(Status.DONE);

        taskManager.createSubTask(sub2);
        taskManager.createSubTask(sub3);
        assertEquals(Status.DONE, taskManager.getEpicById(epic1.getId()).getStatus(), "Статус рассчитывается неправильно");
    }

    @Test
    public void temporaryVerificationOfTheEpic() {
        // конечное время до сохранения подзадач
        taskManager.createEpic(epic1);
        LocalDateTime endTimeIsTaskManager = taskManager.getEpicById(epic1.getId()).getEndTime();

        assertNull(endTimeIsTaskManager, "Конечное время насчитывается неправильно");

        // временные рамки после сохранения подзадач----------------------------------------------------------------------
        taskManager.createSubTask(sub2);
        taskManager.createSubTask(sub3);

        // конечное время насчитывается по самому длинному конечному времени подзадачи.
        LocalDateTime endTimeSub3 = sub3.getEndTime();
        LocalDateTime endTimeIsTaskManager1 = taskManager.getEpicById(epic1.getId()).getEndTime();

        assertEquals(endTimeSub3.format(DATE_TIME_FORMATTER), endTimeIsTaskManager1.format(DATE_TIME_FORMATTER), "Конечное время насчитывается неправильно");

        // время старта
        LocalDateTime startTime = epic1.getStartTime(); // проверочное время из обьекта перед записью
        LocalDateTime startTimeTaskManager = taskManager.getEpicById(epic1.getId()).getStartTime();

        assertEquals(startTime.format(DATE_TIME_FORMATTER), startTimeTaskManager.format(DATE_TIME_FORMATTER), "Время старта насчитывается неправильно");

        // продолжительность эпика эта сумма продолжительности всех его подзадач
        Duration duration1 = sub2.getDuration().plus(sub3.getDuration()); // проверочное время из обьекта перед записью
        Duration durationTaskManager = taskManager.getEpicById(epic1.getId()).getDuration();

        assertEquals(duration1.toMinutes(), durationTaskManager.toMinutes(), "Продолжительность времени насчитывается неправильно");
    }

    @Test
    public void clearSubTask(){ // в этом тесте удаляем через обновленный метод очищения подзадач и сверяем что временные рамки равны нулю
        temporaryVerificationOfTheEpic();

        taskManager.clearSubtasks();

        assertNull(epic1.getDuration(), "Продолжительность времени насчитывается неправильно");
        assertNull(epic1.getStartTime(), "Начальное время насчитывается неправильно");
        assertNull(epic1.getEndTime(), "Конечное время насчитывается неправильно");
        System.out.println(taskManager.getEpicById(epic1.getId()));
    }

    @Test
    public void getSubtaskofEpicId(){
        temporaryVerificationOfTheEpic();

        for (SubTask subTask : taskManager.getSubTasksByEpicId(epic1.getId())){
            System.out.println(subTask);
        }
    }
}