package com.nc.model;

import javafx.beans.property.*;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Task implements Serializable
{
    private String title;
    private String time;
    private String text;
    private Boolean read = false;
    private String login = "Unknown";
    private int id = 0;

    public Task()
    {
        this(null, null, null);
    }

    public Task(String title, String text, String timeString)
    {
        this.title = title;
        this.time = timeString;
        this.text = text;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setTime(String timeString)
    {
        this.time = timeString;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public StringProperty getTitleProperty()
    {
        return new SimpleStringProperty(title);
    }

    public StringProperty getTextProperty()
    {
        return new SimpleStringProperty(text);
    }

    public StringProperty getTimeProperty()
    {
        return new SimpleStringProperty(time);
    }

    public String getTime()
    {
        return time;
    }

    public LocalDateTime toLocalDateTime()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(time, formatter);
    }

    public String getText()
    {
        return text;
    }

    public String getTitle()
    {
        return title;
    }

    public Boolean getRead()
    {
        return read;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getLogin()
    {
        return login;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setRead(Boolean read)
    {
        this.read = read;
    }

    public static boolean validTime(String timeString)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime time = LocalDateTime.parse(timeString, formatter);
        return time != null;
    }
}
