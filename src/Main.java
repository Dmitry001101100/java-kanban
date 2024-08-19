import manager.Managers;
import manager.TaskManager;
import enumeration.Status;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    //------------------------------------------------------------------------------------------------------------------
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = Managers.getDefault();

        while (true) {
            mainCommand();
            int command = Integer.parseInt(scanner.nextLine());

            if (command == 1) {// сохранение для всех видов задач
                formSaveContent(scanner, taskManager);
            } else if (command == 2) {//Вывод всех видов задач
                outPutContent(scanner, taskManager);
            } else if (command == 3) {// Вывод всех видов задач по id
                outPutId(scanner, taskManager);
            } else if (command == 4) {// изменение
                changeContent(scanner, taskManager);
            } else if (command == 5) {// удаление(полное)
                deleteContent(scanner, taskManager);
            } else if (command == 6) {// удаление по id
                deleteId(scanner, taskManager);
            } else if (command == 7) {
                historyTask(taskManager);
            } else if (command == 0) {
                break;
            } else {
                System.out.println("Введенная команда отсутствует.");

            }
        }

    }

    //------------------------------------------------------------------------------------------------------------------
    public static void mainCommand() {
        System.out.println("1 - Сохранение.");
        System.out.println("2 - Вывод всех задач.");
        System.out.println("3 - Вывод по id.");
        System.out.println("4 - Изменение");
        System.out.println("5 - Удаление.");
        System.out.println("6 - Удаление по id.");
        System.out.println("7 - Вывод истории." + "\n");
        System.out.println("0 - выход.");
    }

    //-------------------------------------- Просмотр всех ключей по id ------------------------------------------------
    public static void idTes(TaskManager taskManager, int command) {//

        if (command == 1) {
            if (!taskManager.isEmptyTaskMap()) {
                System.out.println(taskManager.keySetTaskMap());
            } else {
                System.out.println("список задач пуст,введите любую команду для выхода");
            }

        } else if (command == 2) {

            if (!taskManager.isEmptyEpicMap()) {
                System.out.println(taskManager.keySetEpicMap());
            } else {
                System.out.println("список задач пуст,введите любую команду для выхода");
            }

        } else if (command == 3) {

            if (!taskManager.isEmptySubTaskMap()) {
                System.out.println(taskManager.keySetSubTaskMap());
            } else {
                System.out.println("список задач пуст,введите любую команду для выхода");
            }

        } else {
            System.out.println("Введенная команда отсутствует.");
        }
    }
//---------------------------------1 - Сохранение-----------------------------------------------------------------------

    public static void formSaveContent(Scanner scanner, TaskManager taskManager) {//контент для сохранения всех типов


        System.out.println("Что вы хотите сохранить?");
        System.out.println("1 - Задачу, 2 - Эпик, 3 - Подзадачу.");
        int commandTES = Integer.parseInt(scanner.nextLine());

        //---------------------------------сохранение для задач---------------------------------------------------------
        if (commandTES == 1) {// добавление задач

            System.out.println("Введите название задачи: ");
            String newTitle = scanner.nextLine();

            System.out.println("Введите описание задачи: ");
            String newDescription = scanner.nextLine();

            int id = taskManager.getIdUp();
            Task savetheTask = new Task(newTitle, newDescription, id, Status.NEW);

            taskManager.saveTask(savetheTask);

            //------------------------------------- сохранение для эпиков-----------------------------------------------
        } else if (commandTES == 2) { // Добавление эпиков

            System.out.println("Введите название эпика: ");
            String newTitle = scanner.nextLine();

            System.out.println("Введите описание эпика: ");
            String newDescription = scanner.nextLine();

            int id = taskManager.getIdUp();
            Epic savetheEpic = new Epic(newTitle, newDescription, id, Status.NEW);

            taskManager.saveEpic(savetheEpic);

            //------------------------------------------сохранение для подзадач-----------------------------------------
        } else if (commandTES == 3) { // подзадачи

            System.out.println("К какому эпику вы хотите добавить подзадачу?");

            System.out.println(taskManager.keySetEpicMap());

            System.out.println("Введите id.");
            int epicSubTask = Integer.parseInt(scanner.nextLine());

            if (taskManager.keySearchEpic(epicSubTask)) {

                System.out.println("Введите название подзадачи: ");
                String newTitle = scanner.nextLine();

                System.out.println("Введите описание подзадачи: ");
                String newDescription = scanner.nextLine();

                int id = taskManager.getIdUp();
                SubTask saveSubTask = new SubTask(epicSubTask, newTitle, newDescription, id, Status.NEW);

                taskManager.saveSubTask(saveSubTask);

            } else {
                System.out.println("Ошибка! Введенный эпик отсутствует!");
            }

        } else {
            System.out.println("Введенная команда отсутствует,выберите команду из списка:");
        }
    }

    //---------------------------------------- 2 - Вывод всех задач ----------------------------------------------------
    public static void outPutContent(Scanner scanner, TaskManager taskManager) {

        System.out.println("Что вы вы хотите вывести?");
        System.out.println("1 - Задачи, 2 - Эпики, 3 - Подзадачи одного эпика, 4 - Все виды задач.");
        int command = Integer.parseInt(scanner.nextLine());
        if (command == 1) {// вывод задач

            if (!taskManager.isEmptyTaskMap()) {// проверка не пуст ли список задач
                for (Task tas : taskManager.getTasks()) {
                    System.out.println(tas);
                }

            } else {
                System.out.println("Список задач пуст.");

            }
        } else if (command == 2) {// вывод эпиков

            if (!taskManager.isEmptyEpicMap()) { //  проверка пуст ли список эпиков

                for (Epic epic1 : taskManager.getEpics()) {// вывод всех эпиков
                    System.out.println(epic1);
                }

            } else {
                System.out.println("Список эпиков пуст.");

            }

        } else if (command == 3) {// вывод подзадач одного эпика

            if (!taskManager.isEmptyEpicMap()) {
                command = 2;// для корректного вывода id эпиков
                System.out.println("Выберите id эпика для вывода подзадач");
                idTes(taskManager, command);
                int commandTES = Integer.parseInt(scanner.nextLine());

                ArrayList<SubTask> subTasksId = taskManager.getSubTasksId(commandTES);

                for (SubTask sub1 : subTasksId) {
                    System.out.println(sub1);
                }

            } else {
                System.out.println("Список подзадач пуст.");
            }

        } else if (command == 4) {// вывод всех задач

            if (!taskManager.isEmptyTaskMap()) {

                for (Task tas : taskManager.getTasks()) {
                    System.out.println(tas);
                }
            }

            if (!taskManager.isEmptyEpicMap()) {

                for (Epic epic1 : taskManager.getEpics()) {// вывод всех эпиков
                    System.out.println(epic1);
                    for (int numberId : epic1.subtaskIds) {// поиск и вывод привязанных к эпику задач
                        System.out.println("   " + taskManager.outIdSubTask(numberId));
                    }
                }
            }

            if (taskManager.isEmptyTaskMap() && !taskManager.isEmptyEpicMap()) {// если список задач пуст
                System.out.println("Список задач пуст.");

            } else if (taskManager.isEmptyEpicMap() && !taskManager.isEmptyTaskMap()) {// если список эпиков пуст
                System.out.println("Список эпиков пуст.");

            } else if ((taskManager.isEmptyTaskMap()) && (taskManager.isEmptyEpicMap())) {// если списки задач и эпиков пусты
                System.out.println("Списов всех видов задач пуст.");

            }

        } else {
            System.out.println("Введенная команда отсутствует,выберите команду из списка:");

        }
    }

    //------------------------------------------- 3- Вывод по id -------------------------------------------------------
    public static void outPutId(Scanner scanner, TaskManager taskManager) {


        System.out.println("Что вы вы хотите вывести?");
        System.out.println("1 - Задачу, 2 - Эпик, 3 - Подзадачу.");
        int command = Integer.parseInt(scanner.nextLine());

        System.out.println("Выберите id для вывода из списка:");
        idTes(taskManager, command);

        int commandTES = Integer.parseInt(scanner.nextLine());
        if (taskManager.keySearch(commandTES)) {
            if (command == 1) {// вывод задачи по id
                if (taskManager.keySearchTask(commandTES)) {// проверка на наличие id в списке задач
                    System.out.println(taskManager.outIdTask(commandTES));
                } else {
                    System.out.println("Выбранной задачи нет в списке.");
                }

            } else if (command == 2) {// вывод эпика по id

                if (taskManager.keySearchEpic(commandTES)) {// проверка на наличие id в списке задач
                    Epic epic = taskManager.outIdEpic(commandTES);
                    System.out.println(epic);// почему-то не работает

                    for (int numberId : epic.subtaskIds) {
                        System.out.println("   " + taskManager.outIdSubTask(numberId));
                    }

                } else {
                    System.out.println("Выбранного эпика нет в списке.");
                }

            } else if (command == 3) {// вывод подзадачи по id

                if (taskManager.keySearchSubTask(commandTES)) {// проверка на наличие id в списке задач
                    System.out.println("   " + taskManager.outIdSubTask(commandTES));

                } else {
                    System.out.println("Выбранной подзадачи нет в списке.");
                }
            }

        } else {
            System.out.println("Выбранного id не найдено.");
        }
    }

    //--------------------------------------- 4 - Изменение ------------------------------------------------------------
    public static void changeContent(Scanner scanner, TaskManager taskManager) {

        System.out.println("Выберите вы хотите изменить?");
        System.out.println("1 - Задачу, 2 - Эпик, 3 - Подзадачу.");
        int command = Integer.parseInt(scanner.nextLine());

        System.out.println("Выберите id для вывода из списка:");
        idTes(taskManager, command);
        int commandTES = Integer.parseInt(scanner.nextLine());

        // ----------------------------------------- изменение задачи --------------------------------------------------
        if (command == 1) {// изменить задачу
            if (taskManager.keySearchTask(commandTES)) {// проверка на наличие id в списке задач
                Task tas1 = taskManager.outIdTask(commandTES);

                String newTitle;
                String newDescription;
                Status newStatus;

                System.out.println(tas1);
                System.out.println("Изменить название?" + "\n" + "1 - Да. Хотите оставить название пре ведущим нажмите любую цифру.");
                int commandT = Integer.parseInt(scanner.nextLine());

                if (commandT == 1) {
                    System.out.println("Введите новое название");
                    newTitle = scanner.nextLine();
                } else {
                    newTitle = tas1.getTitle();
                }

                System.out.println("Изменить описание?" + "\n" + "1 - Да. Хотите оставить название пре ведущим нажмите любую цифру.");
                int commandD = Integer.parseInt(scanner.nextLine());

                if (commandD == 1) {
                    System.out.println("Введите новое описание");
                    newDescription = scanner.nextLine();
                } else {
                    newDescription = tas1.getDescription();
                }

                System.out.println("Изменить статус?" + "\n" + "1 - Да. Хотите оставить название преведущим нажмите любую цифру.");
                int commandS = Integer.parseInt(scanner.nextLine());

                if (commandS == 1) {
                    System.out.println("На какой статус вы хотите изменить?");
                    System.out.println("1 - Done, 2 - In_process, New - другие цифры.");
                    int commandState = Integer.parseInt(scanner.nextLine());

                    if (commandState == 1) {// статус выполнено
                        newStatus = Status.DONE;
                    } else if (commandState == 2) {// в процессе
                        newStatus = Status.IN_PROGRESS;
                    } else {
                        newStatus = tas1.getStatus();
                    }

                } else {
                    newStatus = tas1.getStatus();
                }

                Task saveNewTask = new Task(newTitle, newDescription, tas1.id, newStatus);// записываем новые значения в обьект
                taskManager.saveTask(saveNewTask);


            } else {
                System.out.println("Выбраного эпика нет в списке.");
            }

        } else if (command == 2) {// изменить эпик
            if (taskManager.keySearchEpic(commandTES)) {// проверка на наличие id в списке задач
                Epic epic1 = taskManager.outIdEpic(commandTES);

                String newTitle;
                String newDescription;
                Status newStatus = epic1.getStatus();

                System.out.println(epic1);
                System.out.println("Изменить название?" + "\n" + "1 - Да. Хотите оставить навзниве преведущим нажмите любую цифру.");
                int commandT = Integer.parseInt(scanner.nextLine());

                if (commandT == 1) {
                    System.out.println("Введите новое название");
                    newTitle = scanner.nextLine();
                } else {
                    newTitle = epic1.getTitle();
                }

                System.out.println("Изменить описание?" + "\n" + "1 - Да. Хотите оставить навзниве преведущим нажмите любую цифру.");
                int commandD = Integer.parseInt(scanner.nextLine());

                if (commandD == 1) {
                    System.out.println("Введите новое описание");
                    newDescription = scanner.nextLine();
                } else {
                    newDescription = epic1.getDescription();
                }

                Epic saveNewEpic = new Epic(newTitle, newDescription, epic1.id, newStatus);// записываем новые значения в обьект
                taskManager.saveEpic(saveNewEpic);

            } else {
                System.out.println("Выбраного эпика нет в списке.");
            }

        } else if (command == 3) {// изменить подзадачу
            if (taskManager.keySearchSubTask(commandTES)) {// проверка на наличие id в списке задач

                SubTask sub1 = taskManager.outIdSubTask(commandTES);

                String newTitle;
                String newDescription;
                Status newStatus;

                System.out.println(sub1);
                System.out.println("Изменить название?" + "\n" + "1 - Да. Хотите оставить навзниве преведущим нажмите любую цифру.");
                int commandT = Integer.parseInt(scanner.nextLine());

                if (commandT == 1) {
                    System.out.println("Введите новое название");
                    newTitle = scanner.nextLine();
                } else {
                    newTitle = sub1.getTitle();
                }

                System.out.println("Изменить описание?" + "\n" + "1 - Да. Хотите оставить навзниве преведущим нажмите любую цифру.");
                int commandD = Integer.parseInt(scanner.nextLine());

                if (commandD == 1) {
                    System.out.println("Введите новое описание");
                    newDescription = scanner.nextLine();
                } else {
                    newDescription = sub1.getDescription();
                }

                System.out.println("Изменить статус?" + "\n" + "1 - Да. Хотите оставить навзниве преведущим нажмите любую цифру.");
                int commandS = Integer.parseInt(scanner.nextLine());

                if (commandS == 1) {
                    System.out.println("На какой статус вы хотите изменить?");
                    System.out.println("1 - Done, 2 - In_process, New - другие цифры.");
                    int commandState = Integer.parseInt(scanner.nextLine());

                    if (commandState == 1) {// статус выполнено
                        newStatus = Status.DONE;
                    } else if (commandState == 2) {// в процессе
                        newStatus = Status.IN_PROGRESS;
                    } else {
                        newStatus = Status.NEW;
                    }

                } else {
                    newStatus = sub1.getStatus();
                }

                SubTask saveNewSubTask = new SubTask(sub1.epicId, newTitle, newDescription, sub1.id, newStatus);// записываем новые значения в обьект
                taskManager.saveSubTask(saveNewSubTask);


            } else {
                System.out.println("Выбраной подзадачи нет в списке.");
            }

        } else {
            System.out.println("Введённая команда отсутствует");
        }
    }

//---------------------------------------- 5 - Удаление всех видов задач -----------------------------------------------

    public static void deleteContent(Scanner scanner, TaskManager taskManager) {


        System.out.println("Выберите вы вотите удалить?");
        System.out.println("1 - Все задачи, 2 - Все подзадачи у одного эпика,3 - Все подзадачи, 4 - Все эпики и подзадачи, 5 - Всё.");
        int command = Integer.parseInt(scanner.nextLine());

        if (command == 1) {// удалить все задачи
            if (!taskManager.isEmptyTaskMap()) {
                taskManager.clearTasks();
            } else {
                System.out.println("Список задач пуст.");
            }

        } else if (command == 2) {// удалить все подзадачи у одного эпика
            System.out.println("Выберите id эпика:");
            idTes(taskManager, command);
            int commandTas = Integer.parseInt(scanner.nextLine());
            if (taskManager.keySearch(commandTas)) {
                taskManager.clearSubTasksOfEpic(commandTas);

            } else {
                System.out.println("Выбранного id нет в списке.");
            }

        } else if (command == 3) {// все подзадачи

            taskManager.clearSubtasks();

        } else if (command == 4) {// удалить все эпики
            taskManager.clearEpics();

        } else if (command == 5) {// удалить всё
            taskManager.clearContent();
        } else {
            System.out.println("Введённая команда отсутствует");
        }

    }

    // ------------------------------------------- 6 - Удаление по id --------------------------------------------------
    public static void deleteId(Scanner scanner, TaskManager taskManager) {


        System.out.println("Что вы вотите удалить?");
        System.out.println("1 - Задачу, 2 - Эпик, 3 - Подзадачу.");

        int command = Integer.parseInt(scanner.nextLine());

        System.out.println("Выберите id для удаления:");
        idTes(taskManager, command);

        int commandTES = Integer.parseInt(scanner.nextLine());


        if (command == 1) {// удаление задачи по id

            if (taskManager.keySearchTask(commandTES)) {// проверка на наличие id в списке задач
                taskManager.deleteTaskId(commandTES);
            } else {
                System.out.println("Выбранной задачи нет в списке.");
            }

        } else if (command == 2) {// удаление эпика по id
            if (taskManager.keySearchEpic(commandTES)) {// проверка на наличие id в списке эпиков
                taskManager.deleteEpicId(commandTES);
            } else {
                System.out.println("Выбранной задачи нет в списке.");
            }
        } else if (command == 3) {// удаление подзадачи по id

            if (taskManager.keySearchSubTask(commandTES)) {// проверка на наличие id в списке подзадач
                taskManager.deleteSubTaskId(commandTES);
            } else {
                System.out.println("Выбранной задачи нет в списке.");
            }
        }
    }

    //---------------------------------------------- вывод истории -----------------------------------------------------
    public static void historyTask(TaskManager taskManager) {// вывод id истории просмотра задач

        ArrayList<Task> hisId = taskManager.getHistory();
        ArrayList<Task> nullList = new ArrayList<>();

        System.out.println("Ваша история просмотров:");
        if (Objects.equals(hisId, nullList)) {
            System.out.println("на данный момент пуста.");
        }

        for(Task task : hisId){

            if (taskManager.keySearch(task.id)){
                System.out.println(task);
            }else {
                System.out.println("id " + task.id + " из истории просмотров уже не существует.");
            }

        }

    }
}
