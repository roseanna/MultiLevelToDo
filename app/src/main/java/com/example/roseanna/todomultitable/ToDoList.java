package com.example.roseanna.todomultitable;

/**
 * Created by roseanna on 3/19/16.
 */
public interface ToDoList {
    public boolean isChild();
    public boolean isParent();
    public boolean isSelected();
    public String getTitle();
    public String getParent();
    public String getChild();
    public String getDescription();
    public String getDate();
    public void set();
    public void unset();
}
