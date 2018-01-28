package com.nc;

import com.nc.exception.TaskManagerException;
import com.nc.model.Task;
import com.nc.model.TaskListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;

public class TaskManagerServer extends Thread
{
    private static final String FILE_PATH_XML = "./src/main/resources/tasks.xml";
    private static Connect connect = new Connect();

    public static void main(String[] args) throws Exception
    {
        loadFile();
        connect.startServer();
    }

    // Не получилось вынести методы работы с файлом в отдельный класс
    private static void loadFile()
    {
        loadTaskDataFromFile(getTaskFilePath(), connect.getTaskData());
    }

    public static void saveFile()
    {
        saveTaskDataToFile(getTaskFilePath(), connect.getTaskData());
    }

    private static File getTaskFilePath()
    {
        return new File(FILE_PATH_XML);
    }

    private static void saveTaskDataToFile(File file, ObservableList<Task> taskData)
    {
        try
        {
            JAXBContext context = JAXBContext.newInstance(TaskListWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            TaskListWrapper wrapper = new TaskListWrapper();
            wrapper.setTasks(taskData);

            m.marshal(wrapper, file);
        }
        catch (Exception e)
        {
            TaskManagerException.createSavingException(file.getPath(), e);
        }
    }

    private static void loadTaskDataFromFile(File file, ObservableList<Task> taskData)
    {
        try
        {
            JAXBContext context = JAXBContext.newInstance(TaskListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();

            TaskListWrapper wrapper = (TaskListWrapper)um.unmarshal(file);

            taskData.clear();
            taskData.addAll(wrapper.getTasks());
        }
        catch (Exception e)
        {
            TaskManagerException.createLoadingException(file.getPath(), e);
        }
    }
}

class Connect
{
    private Random rand = new SecureRandom();
    private Socket socket;
    private volatile ObservableList<Task> taskData = FXCollections.observableArrayList();

    public void startServer() throws Exception
    {
        try (ServerSocket serverSocket = new ServerSocket(11111))
        {
            while (true)
            {
                socket = serverSocket.accept();
                Thread thread = new Thread(new Server());
                thread.start();
            }
        }
    }

    class Server implements Runnable
    {
        private final Logger LOG = Logger.getLogger(Server.class.getName());

        @Override
        public void run()
        {
            LOG.info("Serving client " + socket.getInetAddress());
            Task task;

            try
            {
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

                while (true)
                {
                    HashMap<String, Object> request = (HashMap<String, Object>) inputStream.readObject();

                    if (request.containsKey("create"))
                    {
                        task = (Task)request.get("create");
                        task.setId(rand.nextInt(9999));

                        getTaskData().add(task);
                        outputStream.writeObject(new ArrayList<>(selectionToUser(task.getLogin())));
                    }
                    else if (request.containsKey("edit"))
                    {
                        task = (Task)request.get("edit");

                        for (Task task1 : taskData)
                        {
                            if (task1.getId() == task.getId())
                            {
                                taskData.remove(task1);
                                taskData.add(task);
                                break;
                            }
                        }
                        outputStream.writeObject(new ArrayList<>(selectionToUser(task.getLogin())));
                    }
                    else if (request.containsKey("delete"))
                    {
                        task = (Task)request.get("delete");

                        for (Task task1 : taskData)
                        {
                            if (task1.getId() == task.getId())
                            {
                                taskData.remove(task1);
                                break;
                            }
                        }
                        outputStream.writeObject(new ArrayList<>(selectionToUser(task.getLogin())));
                    }
                    else if (request.containsKey("load"))
                    {
                        String login = (String)request.get("load");
                        outputStream.writeObject(new ArrayList<>(selectionToUser(login)));
                    }
                    else if (request.containsKey("save"))
                    {
                        TaskManagerServer.saveFile();
                        outputStream.writeObject(null);
                    }
                    LOG.info("Request: " + request);

                    //outputStream.writeObject(new ArrayList<>(taskData));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public ObservableList<Task> getTaskData()
    {
        return taskData;
    }

    public ObservableList<Task> selectionToUser(String login)
    {
        ObservableList<Task> newTaskData = FXCollections.observableArrayList();
        getTaskData().forEach(task ->
        {
            if (task.getLogin().equals(login))
            {
                newTaskData.add(task);
            }
        });
        return newTaskData;
    }
}
