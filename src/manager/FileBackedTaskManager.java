package manager;

import exception.ManagerSaveException;
import tasks.Epic;
import tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTaskManager( File file) {
        super();
        this.file = file;
    }
// ------------------------------- сохранение и перезапись -------------------------------------------------------------
    public void saveTask(Task task){
        saveTask(task);
        save();
    }
    public void saveEpic(Epic epic){
        saveEpic(epic);
        save();
    }

    public void saveSubTask(Epic epic){
        saveSubTask(epic);
        save();
    }



    // метод для сохранения всех видов задач в файл;

    public void save(){
        try (FileWriter writer = new FileWriter(file)) {
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


}
