package com.example.roseanna.todomultitable;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;


import java.io.Serializable;
import java.util.Date;

/**
 * Created by roseanna on 3/19/16.
 */
public class ToDoParent implements Serializable, ToDoList {
    private String title;
    private String date;
    private String description;
    private String parent, child;
    private boolean selected;

    public ToDoParent(String title, String desc, String date, String parent, String child){
        this.title          = title;
        this.description    = desc;
        this.date           = date;
        this.parent         = parent;
        this.child          = child;
        this.selected       = false;
    }

    public String getChild(){
        return this.child;
    }
    public String getParent(){
        return this.parent;
    }

    public boolean isChild(){
        return this.parent == null;
    }
    public boolean isParent(){
        return this.child == null;
    }
    public String getDescription(){
        return description;
    }

    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getDate(){
        Log.i("getdate", date.toString());
        return date.toString();
    }
    public void set() {
        this.selected = true;
    }
    public void unset(){
        this.selected = false;
    }
    public boolean isSelected(){return selected;}
}