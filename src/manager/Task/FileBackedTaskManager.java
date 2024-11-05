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

    private final File file;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    public FileBackedTaskManager(File file) {

        if(!isFileEmpty(file) && file.length() >=2){
            downloadingFromAFile(file);
        }
        this.file = file;
    }

    public boolean isFileEmpty(File file) {
        return file.length() == 0;
    }


//----------------------------------------------------------------------------------------------------------------------
    // метод для сохранения всех видов задач в файл;

    public void save() {

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

    public void downloadingFromAFile(File file) {

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
                if (parts[5].equals("null")) { startTime = null; }
                else { startTime = LocalDateTime.parse(parts[5], DATE_TIME_FORMATTER); }

                if (parts[6].equals("null")){ endTime = null; }
                else { endTime = LocalDateTime.parse(parts[6], DATE_TIME_FORMATTER); }
// ---------------------------------------------------------------------------------------------------------------------
                if (startTime != null && endTime != null) { duration = Duration.between(startTime, endTime); }
                else { duration = null; }

                if (type.equals("SUBTASK")) {
                    idOfEpic = Integer.parseInt(parts[7]);
                }
                switch (TypeTask.valueOf(type)) {
                    case SUBTASK -> {
                        SubTask subtask = new SubTask(idOfEpic, name, description, id, Status.valueOf(status), startTime, duration);
                        super.saveSubTask(subtask);
                    }
                    case TASK -> {
                        Task task = new Task(name, description, id, Status.valueOf(status), startTime, duration);// аналогично
                        super.saveTask(task);
                    }
                    case EPIC -> {
                        Epic epic = new Epic(name, description, id, Status.valueOf(status), startTime, duration);// исправить сохраняемую дату из списка
                        super.saveEpic(epic);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи!");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public int getIdUp() { // герерирует id
        return super.getIdUp();
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
    public void saveTask(Task savetheTask) {
        super.saveTask(savetheTask);
        save();
    }

    @Override
    public void saveEpic(Epic savetheEpic) {
        super.saveEpic(savetheEpic);
        save();
    }

    @Override
    public void saveSubTask(SubTask saveSubTask) {
        super.saveSubTask(saveSubTask);
        save();
    }
    // -------------------------------- полное удаление ----------------------------------------------------------------

    @Override
    public void clearContent() {
        super.clearContent();
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubTasksOfEpic(int epicId) {
        super.clearSubTasksOfEpic(epicId);
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    // ------------------------------ удаление по id -------------------------------------------------------------------
    @Override
    public void deleteTaskId(int numberId) {
        super.deleteTaskId(numberId);
        save();
    }

    @Override
    public void deleteSubTaskId(int numberId) {
        super.deleteSubTaskId(numberId);
        save();
    }

    @Override
    public void deleteEpicId(int numberId) {
        super.deleteEpicId(numberId);
        save();
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
    public ArrayList<SubTask> getSubTasksId(int epicId) {
        return super.getSubTasksId(epicId);
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        return super.getSubTasks();
    }

    //-------------------------------------- 3 - Вывод по id -----------------------------------------------------------
    @Override
    public Task outIdTask(int numberId) {

        return super.outIdTask(numberId);
    }

    @Override
    public SubTask outIdSubTask(int numberId) {

        return super.outIdSubTask(numberId);
    }

    @Override
    public Epic outIdEpic(int numberId) {

        return super.outIdEpic(numberId);
    }

    // ---------------------------------------------------------------------------------------------------------------------
    @Override
    public ArrayList<Task> getHistory() {
        return super.getHistory();
    }


}
