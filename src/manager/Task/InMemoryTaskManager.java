package manager.Task;

import enumeration.*;
import manager.History.HistoryManager;
import manager.Managers;
import tasks.*;


import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private static final Comparator<Task> taskComparator = Comparator.comparing(
            Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);
    //------------------------------------------------------------------------------------------------------------------

    protected final Map<Integer, Task> taskMap = new HashMap<>();
    protected final Map<Integer, Epic> epicMap = new HashMap<>();
    protected final Map<Integer, SubTask> subTaskMap = new HashMap<>();
    private final Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);

    HistoryManager historyManager = Managers.getDefaultHistory();

    //-------------------------------Вспомогательные--------------------------------------------------------------------
    int idUp = 0;

    @Override
    public int getIdUp() { // герерирует id
        idUp++;
        while (true) {
            if (containsKeyTask(idUp) || containsKeyEpic(idUp) || containsKeySubTask(idUp)) {
                idUp++;
            } else {
                break;
            }
        }
        return idUp;
    }

    public void updateEpicStatus(int id) { // определение статуса эпика
        Epic epic = epicMap.get(id);
        List<Integer> subtaskList = epicMap.get(id).getSubtaskIds();
        if (subtaskList == null || subtaskList.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        int newStatus = 0;
        int doneStatus = 0;

        for (Integer subtaskId : subtaskList) {
            Status status = subTaskMap.get(subtaskId).getStatus();
            if (status.equals(Status.NEW)) newStatus++;
            if (status.equals(Status.DONE)) doneStatus++;
        }

        if (newStatus == subtaskList.size()) epic.setStatus(Status.NEW);
        else if (doneStatus == subtaskList.size()) epic.setStatus(Status.DONE);
        else epic.setStatus(Status.IN_PROGRESS);
    }

    @Override
    public boolean containsKeyTask(int id) {
        return taskMap.containsKey(id);
    }

    @Override
    public boolean containsKeySubTask(int id) {
        return subTaskMap.containsKey(id);
    }

    @Override
    public boolean containsKeyEpic(int id) {
        return epicMap.containsKey(id);
    }

    @Override
    public boolean containsKeyTasks(int id) {
        if (taskMap.containsKey(id)) {
            return true;
        } else if (epicMap.containsKey(id)) {
            return true;
        } else if (subTaskMap.containsKey(id)) {
            return true;
        }
        return false;
    }

    // -------------------------------------- prioritizedTasks ---------------------------------------------------------
    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public void setPrioritizedTasks(Task task) {
        this.prioritizedTasks.add(task);
    }

    //---------------------------------- 1 - Сохранение ----------------------------------------------------------------

    @Override
    public void createTask(Task task) { // сохранение задач
        if (task.getId() == null) {
            task.setId(getIdUp());
            System.out.println("id задачи изменен.");
        }

        if (!containsKeyTasks(task.getId())) {
            taskMap.put(task.getId(), task);
            prioritizedTasks.add(task);
            System.out.println("Задача успешно записана!");
        } else {
            System.out.println("Запись прервана,задача пересекается с существующей.");
        }
    }

    @Override
    public void updateTask(Task newTask) { // пока так попозже подкорректировать
        if (containsKeyTasks(newTask.getId())) {
            taskMap.put(newTask.getId(), newTask);
            System.out.println("Задача успешно обновлена!");
        } else {
            System.out.println("Задача с данным id не найдена.");
        }

    }

    @Override
    public void createEpic(Epic epic) { // сохранение и перезапись эпиков
        if (epic.getId() == null) {
            epic.setId(getIdUp());
            System.out.println("id эпика изменен.");
        }

        if (!containsKeyTasks(epic.getId())) {
            epicMap.put(epic.getId(), epic);
            searchForTheStartTimeAndDuration(epic.getId());  //  мешает серверу для записи новых задач
            System.out.println("Эпик успешно записан!");
        } else {
            System.out.println("Запись прервана,эпик пересекается с существующим.");
        }
    }

    @Override
    public void updateEpic(Epic epic) { // сохранение и перезапись эпиков
        if (containsKeyEpic(epic.getId())) {
            epicMap.put(epic.getId(), epic);
            searchForTheStartTimeAndDuration(epic.getId());
            System.out.println("Эпик успешно изменен!");
        } else {
            System.out.println("Эпик с таким id не найден.");
        }
    }

    @Override
    public void createSubTask(SubTask subTask) { // сохранение и перезапись подзадач

        if (subTask.getId() == null) {
            subTask.setId(getIdUp());
            System.out.println("Изменен id");
        }

        Epic epic1 = epicMap.get(subTask.getEpicId()); // вызываем нужный элемент хеш таблицы

        if (epic1.getSubtaskIds() == null) { // для записи субтаксов в эпик добавленный через сервер
            epic1.setSubtaskIds(new ArrayList<>());
        }

        if (!containsKeyTasks(subTask.getId())) {

            subTaskMap.put(subTask.getId(), subTask);
            updateEpicStatus(subTask.getEpicId()); //проверка и если требуется изменение статуса эпика
            searchForTheStartTimeAndDuration(subTask.getEpicId()); // временные рамки эпика
            prioritizedTasks.add(subTask);
            System.out.println("Подзадача успешно записана.");
        }

        if (epic1.getSubtaskIds().isEmpty()) { // записываем в список id подзадач новое значение
            epic1.addSubtaskIds(subTask.getId());
        } else if (!epic1.getSubtaskIds().contains(subTask.getId())) {
            epic1.addSubtaskIds(subTask.getId());
        }
         updateEpic(epic1); // обновляем эпик
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask.getId() == null) {
            subTask.setId(getIdUp());
            System.out.println("Изменен id");
        }
        if (containsKeySubTask(subTask.getId())) {
            subTaskMap.put(subTask.getId(), subTask);
            updateEpicStatus(subTask.getEpicId()); //проверка и если требуется изменение статуса эпика
            searchForTheStartTimeAndDuration(subTask.getEpicId()); // временные рамки эпика
            prioritizedTasks.add(subTask);
            System.out.println("Подзадача успешно обновлена!");
        }
    }
//------------------------------------------- 2 - Вывод полный ---------------------------------------------------------

    @Override
    public ArrayList<Epic> getEpics() { // кладем значение из мапы эпиков в лист и возвращаем его
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public ArrayList<Task> getTasks() { // кладем значение из мап тасков в лист и возвращаем его
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpicId(int epicId) { // кладем значение из мап подзадач в лист и возвращаем его
        return epicMap.get(epicId).getSubtaskIds().stream()
                .map(subTaskMap::get)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<SubTask> getSubTasks() { // кладем значение из мап тасков в лист и возвращаем его
        return new ArrayList<>(subTaskMap.values());
    }

    //-------------------------------------- 3 - Вывод по id -----------------------------------------------------------
    @Override
    public Task getTaskById(int numberId) { //вывод задачи по id
        historyManager.add(taskMap.get(numberId));
        return taskMap.get(numberId);
    }

    @Override
    public SubTask getSubTaskById(int numberId) { //вывод подзадачи по id
        historyManager.add(subTaskMap.get(numberId));
        return subTaskMap.get(numberId);
    }
    //

    @Override
    public Epic getEpicById(int numberId) { //вывод эпика по id
        historyManager.add(epicMap.get(numberId));
        return epicMap.get(numberId);
    }

    //--------------------------------------------- 5 - Полное удаление ------------------------------------------------
    @Override
    public void clearContent() { // удалить всё
        clearTasks();
        clearEpics();
        prioritizedTasks.clear();
        historyManager.clear();
        idUp = 0;// обнуляем переменную для id
        System.out.println("Все содержимое полностью очищено.");
    }

    @Override
    public void clearTasks() { // удалить все задачи

        for (int id : taskMap.keySet()) {
            historyManager.remove(id);
        }
        prioritizedTasks.removeIf(task -> task.getType() == TypeTask.TASK);
        taskMap.clear();
        System.out.println("Задачи полностью удалены.");
    }

    @Override
    public void clearSubTasksOfEpic(int epicId) { // удалить все подзадачи у одного эпика

        for (int id : epicMap.get(epicId).getSubtaskIds()) {
            historyManager.remove(id);
        }

        subTaskMap.values().removeIf(subTask -> subTask.getEpicId() == epicId); // удаляем все подзадачи относящиеся к этому эпику
        prioritizedTasks.removeIf(task -> epicMap.get(epicId).getSubtaskIds().contains(task.getId())); // удаляем все подзадачи из приоритетного списка
        epicMap.get(epicId).getSubtaskIds().clear(); // очищаем список подзадач у эпика
        searchForTheStartTimeAndDuration(epicId);   // временные рамки эпика
        updateEpicStatus(epicId);   // обновляем статус эпика
        System.out.println("Все подзадачи в рамках одного эпика удалены");

    }

    @Override
    public void clearEpics() { // удалить все эпики и подзадачи к ним относящиеся

        for (int id : epicMap.keySet()) {
            historyManager.remove(id);
        }

        subTaskMap.clear();
        epicMap.clear();
        prioritizedTasks.removeIf(task -> task.getType() == TypeTask.SUBTASK);
        System.out.println("Эпики и подзадачи к ним относящиеся полностью удалены.");

    }

    @Override
    public void clearSubtasks() { // удаление всех подзадач

        for (int id : subTaskMap.keySet()) {
            historyManager.remove(id);
        }

        epicMap.values().stream()
                .filter(epic -> !epic.getSubtaskIds().isEmpty())
                .peek(Epic::clearSubtaskIds)
                .peek(epic -> updateEpicStatus(epic.getId()))
                .peek(epic -> historyManager.remove(epic.getId()))
                .peek(epic -> searchForTheStartTimeAndDuration(epic.getId()))
                .peek(epic -> subTaskMap.clear())
                .collect(Collectors.toList());

    }

    // -------------------------------------- 6 - Удаление по id -------------------------------------------------------
    @Override
    public void deleteTaskId(int numberId) { // удаление задачи по id
        prioritizedTasks.removeIf(task -> task.getId() == numberId);
        taskMap.remove(numberId);
        historyManager.remove(numberId);
        System.out.println("Задача под номером " + numberId + " была удалена!");

    }

    @Override
    public void deleteSubTaskId(int numberId) { // удаление подзадачи по id

        subTaskMap.values().stream()
                .filter(subTask -> subTask.getId().equals(numberId)) // проверяем есть ли подзадача с этим id
                .peek(prioritizedTasks::remove) // удаляем из списка приоритетных задач
                .peek(subTask -> historyManager.remove(subTask.getId())) // удаляем из истории
                .peek(subTask -> epicMap.get(subTask.getEpicId()).removeSubtaskIds(subTask.getId())) // удаляем подзадачу из списка подзадач эпика
                .peek(subTask -> updateEpicStatus(subTask.getEpicId())) // обновляем статус эпика после удаления подзадачи
                .peek(subTask -> searchForTheStartTimeAndDuration(subTask.getEpicId())) //обновляем временные рамки эпика
                .toList();

        subTaskMap.remove(numberId);
        System.out.println("Подзадача под номером " + numberId + " была удалёна!");
    }

    @Override
    public void deleteEpicId(int numberId) { // удаление эпика по id

        if (epicMap.get(numberId).getSubtaskIds() != null && !epicMap.get(numberId).getSubtaskIds().isEmpty()) { // проверка для EpicHandle
            for (int i : epicMap.get(numberId).getSubtaskIds()) { // если в списке есть id подзадачи, то удаляем эту подзадачу
                subTaskMap.remove(i);
                historyManager.remove(i);
            }
        }
        epicMap.remove(numberId);
        historyManager.remove(numberId);
        System.out.println("Эпик под номером " + numberId + " был удалён!");
    }

// ---------------------------------------------------------------------------------------------------------------------

    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) historyManager.getHistory();
    }

// ----------------------------------- расчет начального и конечного времени и продолжительности  эпика ----------------------------------

    public void searchForTheStartTimeAndDuration(int epicId) {
        Epic epic = epicMap.get(epicId);

        LocalDateTime epicStartTime = null;
        LocalDateTime epicEndTime = null;
        Duration epicDuration = null;

        if (epic.getSubtaskIds() != null && !epic.getSubtaskIds().isEmpty()) {
            for (int i : epic.getSubtaskIds()) {
                SubTask subTask = subTaskMap.get(i);
                if (subTask.getEndTime() != null) {
                    if (epicEndTime == null || epicEndTime.isAfter(subTask.getEndTime())) {
                        epicEndTime = subTask.getEndTime();
                    }
                }
                if (subTask.getStartTime() != null) {
                    if (epicStartTime == null || epicStartTime.isBefore(subTask.getStartTime())) {
                        epicStartTime = subTask.getStartTime();
                    }
                }
                if (subTask.getDuration() != null) {
                    if (epicDuration == null) {
                        epicDuration = subTask.getDuration();
                    } else {
                        epicDuration = epicDuration.plus(subTask.getDuration());
                    }
                }
            }
        }
        // String startTime = epicStartTime.format(DATE_TIME_FORMATTER);

        epic.setStartTime(epicStartTime);
        epic.setDuration(epicDuration);
        epic.setEndTime(epicEndTime);
    }
}
