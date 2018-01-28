package com.nc.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Вспомогательный класс, используется для сохранения списка в xml файл.
 */
@XmlRootElement(name = "tasks")
public class TaskListWrapper {
    private List <Task> tasks;

    @XmlElement(name = "task")
    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}

