package manager;

import enumeration.Status;
import enumeration.TypeTask;
import exception.ManagerSaveException;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.*;
import java.util.ArrayList;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;


    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }


//----------------------------------------------------------------------------------------------------------------------


    // метод для сохранения всех видов задач в файл;

    public void save() {

        try (Writer writer = new FileWriter(file)) {

            writer.write("id,type,name,status,description,epic\n");
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

    public FileBackedTaskManager downloadingFromAFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        TaskManager taskManager = Managers.getDefault();

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
                if (id > super.idUp) {
                    super.idUp = id;
                }
                if (parts.length > 4) {
                    description = parts[4];
                } else {
                    description = "";
                }
                if (type.equals("SUBTASK")) {
                    idOfEpic = Integer.parseInt(parts[5]);
                }
                switch (TypeTask.valueOf(type)) {
                    case SUBTASK -> {
                        SubTask subtask = new SubTask(idOfEpic, name, description, id, Status.valueOf(status));
                        super.saveSubTask(subtask);

                    }
                    case TASK -> {
                        Task task = new Task(name, description, id, Status.valueOf(status));
                        super.saveTask(task);
                    }
                    case EPIC -> {
                        Epic epic = new Epic(name, description, id, Status.valueOf(status));
                        super.saveEpic(epic);
                    }
                }
            }
            return manager;
        } catch (IOException e) {
            throw new ManagerSaveException("Saving error!");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public int getIdUp() { // герерирует id
        return super.getIdUp();
    }

    // -------------------------------Сохранение ----------------------------------------------------------------------------


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

    // ------------------------------ удаление по id -----------------------------------------------------------------------
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
    public ArrayList<Epic> getEpics() { // кладем значение из мапы эпиков в лист и возвращаем его

        return super.getEpics();
    }

    @Override
    public ArrayList<Task> getTasks() { // кладем значение из мап тасков в лист и возвращаем его
        return super.getTasks();
    }

    @Override
    public ArrayList<SubTask> getSubTasksId(int epicId) { // кладем значение из мап подзадач в лист и возвращаем его
        return super.getSubTasksId(epicId);
    }

    @Override
    public ArrayList<SubTask> getSubTasks() { // кладем значение из мап тасков в лист и возвращаем его
        return super.getSubTasks();
    }

    //-------------------------------------- 3 - Вывод по id -----------------------------------------------------------
    @Override
    public Task outIdTask(int numberId) { //вывод задачи по id

        return super.outIdTask(numberId);
    }

    @Override
    public SubTask outIdSubTask(int numberId) { //вывод подзадачи по id

        return super.outIdSubTask(numberId);
    }

    @Override
    public Epic outIdEpic(int numberId) { //вывод эпика по id

        return super.outIdEpic(numberId);
    }

    // ---------------------------------------------------------------------------------------------------------------------
    @Override
    public ArrayList<Task> getHistory() {
        return super.getHistory();
    }


}
