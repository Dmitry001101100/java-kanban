package manager.Task;

import enumeration.Status;
import enumeration.TypeTask;
import exception.ManagerSaveException;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private  File historyList = new File("historyList.csv"); // файл для сохранения истории

    private final File file;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    public FileBackedTaskManager(File file) {

        if (!isFileEmpty(file) && file.length() >= 2) {
            downloadingFromAFile(file);
        }
        if (!isFileEmpty(historyList) && historyList.length() >= 2) {
            downloadingHistoryFromAFile();
        }
        this.file = file;
    }

    private boolean isFileEmpty(File file) {
        return file.length() == 0;
    }

    //----------------------------------------------------------------------------------------------------------------------
    // сохранение истории в файла
    private void saveHistoryList() {
        try (Writer writer = new FileWriter(historyList)) { // сохранение истории в файл

            writer.write("id,type\n");
            for (Task task : getHistory()) {
                writer.write(task.getId().toString() + "," + task.getType() + "\n");
            }
        } catch (IOException exp) {
            throw new ManagerSaveException("Произошла ошибка записи в файл", exp);
        }
    }

    // загрузка списка истории
    private void downloadingHistoryFromAFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(historyList))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",");

                int id = Integer.parseInt(parts[0]);
                String type = parts[1];
                // выводим id задач согласно списку по их типу

                if (containsKeyTask(id)) {
                    super.getTaskById(id);
                    System.out.println("задача сохранена в историю");
                }
                if (containsKeyEpic(id)) {
                    super.getEpicById(id);
                    System.out.println("эпик сохранена в историю");
                }
                if (containsKeySubTask(id)) {
                    super.getSubTaskById(id);
                    System.out.println("подзадача сохранена в историю");
                }

            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи!");
        }

    }


//----------------------------------------------------------------------------------------------------------------------
    // метод для сохранения всех видов задач в файл;

    private void save() {

        try (Writer writer = new FileWriter(file)) {

            writer.write("id,type,name,status,description,startTime,duration,epic\n");
            for (Integer key : taskMap.keySet()) {
                writer.write(taskMap.get(key).toString() + "\n");
            }
            for (Integer key : epicMap.keySet()) {
                writer.write(epicMap.get(key).toString() + "\n");
            }
            for (Integer key : subTaskMap.keySet()) {
                writer.write(subTaskMap.get(key).toString() + "\n");
            }
        } catch (IOException exp) {
            throw new ManagerSaveException("Произошла ошибка записи в файл", exp);
        }
    }

    private void downloadingFromAFile(File file) {

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String type = parts[1];
                String name = parts[2];
                String status = parts[3];
                String description;
                int idOfEpic = 0;
                if (parts.length > 4) {
                    description = parts[4];
                } else {
                    description = "";
                }
                LocalDateTime startTime;
                LocalDateTime endTime;
                Duration duration;
// ----------------------------------- проверка времени начала и конца задачи ------------------------------------------
                if (parts[5].equals("null")) {
                    startTime = null;
                } else {
                    startTime = LocalDateTime.parse(parts[5], DATE_TIME_FORMATTER);
                }

                if (parts[6].equals("null")) {
                    endTime = null;
                } else {
                    endTime = LocalDateTime.parse(parts[6], DATE_TIME_FORMATTER);
                }
// ---------------------------------------------------------------------------------------------------------------------
                if (startTime != null && endTime != null) {
                    duration = Duration.between(startTime, endTime);
                } else {
                    duration = null;
                }

                if (type.equals("SUBTASK")) {
                    idOfEpic = Integer.parseInt(parts[7]);
                }
                switch (TypeTask.valueOf(type)) {
                    case SUBTASK -> {
                        SubTask subtask = new SubTask(idOfEpic, name, description, id, Status.valueOf(status), startTime, duration);
                        super.createSubTask(subtask);
                    }
                    case TASK -> {
                        Task task = new Task(name, description, id, Status.valueOf(status), startTime, duration);// аналогично
                        super.createTask(task);
                    }
                    case EPIC -> {
                        Epic epic = new Epic(name, description, id, Status.valueOf(status), startTime, duration);// исправить сохраняемую дату из списка
                        super.createEpic(epic);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи!");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------


    @Override
    public boolean containsKeyTask(int id) {
        return super.containsKeyTask(id);
    }

    @Override
    public boolean containsKeySubTask(int id) {
        return super.containsKeySubTask(id);
    }

    @Override
    public boolean containsKeyEpic(int id) {
        return super.containsKeyEpic(id);
    }

    //------------------------------------------------------------------------------------------------------------------

    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    public void setPrioritizedTasks(Task task) {
        super.setPrioritizedTasks(task);
    }

    // -------------------------------Сохранение -----------------------------------------------------------------------



    @Override
    public void createTask(Task savetheTask) {
        super.createTask(savetheTask);
        save();
    }

    @Override
    public void updateTask(Task updateTask) {
        super.updateTask(updateTask);
        save();
    }

    @Override
    public void createEpic(Epic savetheEpic) {
        super.createEpic(savetheEpic);
        save();
    }

    @Override
    public void createSubTask(SubTask saveSubTask) {
        super.createSubTask(saveSubTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }
    // -------------------------------- полное удаление ----------------------------------------------------------------

    @Override
    public void clearContent() {
        super.clearContent();
        save();
        saveHistoryList();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
        saveHistoryList();
    }

    @Override
    public void clearSubTasksOfEpic(int epicId) {
        super.clearSubTasksOfEpic(epicId);
        save();
        saveHistoryList();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
        saveHistoryList();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
        saveHistoryList();
    }

    // ------------------------------ удаление по id -------------------------------------------------------------------
    @Override
    public void deleteTaskId(int numberId) {
        super.deleteTaskId(numberId);
        save();
        saveHistoryList();
    }

    @Override
    public void deleteSubTaskId(int numberId) {
        super.deleteSubTaskId(numberId);
        save();
        saveHistoryList();
    }

    @Override
    public void deleteEpicId(int numberId) {
        super.deleteEpicId(numberId);
        save();
        saveHistoryList();
    }

//------------------------------------------- 2 - Вывод полный ---------------------------------------------------------

    @Override
    public ArrayList<Epic> getEpics() {

        return super.getEpics();
    }

    @Override
    public ArrayList<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpicId(int epicId) {
        return super.getSubTasksByEpicId(epicId);
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        return super.getSubTasks();
    }

    //-------------------------------------- 3 - Вывод по id -----------------------------------------------------------
    @Override
    public Task getTaskById(int numberId) {
        Task task = super.getTaskById(numberId); // добавляем сохранение в файл после выгрузки
        saveHistoryList();
        return task;

    }

    @Override
    public SubTask getSubTaskById(int numberId) {
        SubTask subTask = super.getSubTaskById(numberId);
        saveHistoryList();
        return subTask;
    }

    @Override
    public Epic getEpicById(int numberId) {
        Epic epic = super.getEpicById(numberId);
        saveHistoryList();
        return epic;
    }

    // ---------------------------------------------------------------------------------------------------------------------
    @Override
    public ArrayList<Task> getHistory() {
        return super.getHistory();
    }


}
