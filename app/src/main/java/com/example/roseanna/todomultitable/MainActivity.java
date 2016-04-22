package com.example.roseanna.todomultitable;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Serializable {
    // For UI
    TaskAdapter adapter;
    ArrayList<ToDoList> tasks = new ArrayList<>();
    public ListView todoLV;
    public EditText newTask;
    public Button addButton, delButton, clearButton, showButton, editButton;

    // For edit
    public boolean returned = false;
    public String newTitle, newDesc, oldDate, oldParent, oldChild;
    public int oldPosition;

    // For SQL stuff
    private static String tableName = "Parent_Table";
    private SQLiteDatabase sampleDB = null;
    private Cursor cursor           = null;

// -------------------------------  FOR BUTTON CLICKS ------------------------------
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_parent:
                addClick();
                break;
            case R.id.delete_parent:
                if(!databaseEmpty())
                    deleteClick();
                else
                    Toast.makeText(MainActivity.this, "Empty Database", Toast.LENGTH_SHORT).show();
                break;
            case R.id.clear_parent:
                if(!databaseEmpty())
                    clearClick();
                else
                    Toast.makeText(MainActivity.this, "Empty Database", Toast.LENGTH_SHORT).show();
                break;
            case R.id.show_parent:
                if(!databaseEmpty())
                    showClick();
                else
                    Toast.makeText(MainActivity.this, "Empty Database", Toast.LENGTH_SHORT).show();
                break;
            case R.id.edit_parent:
                if(!databaseEmpty())
                    editClick();
                else
                    Toast.makeText(MainActivity.this, "Empty Database", Toast.LENGTH_SHORT).show();
                break;
            }
            clearSelect();
    }
    public void editClick(){
        int count = 0;
        for (int i = 0; i < tasks.size(); i++){
            if(tasks.get(i).isSelected()){
                oldPosition = i;
                count++;
            }
        }
        if (count != 1) {
            Toast.makeText(MainActivity.this, "CHOOSE ONE TASK!", Toast.LENGTH_SHORT).show();
            clearSelect();
            return;
        }
        ToDoList toEdit     = tasks.get(oldPosition);
        Intent showActivity = new Intent(MainActivity.this, EditActivity.class);
        Bundle myBundle     = new Bundle();

        myBundle.putString("title", toEdit.getTitle());
        myBundle.putString("description", toEdit.getDescription());
        myBundle.putString("date", toEdit.getDate());
        myBundle.putString("parent", toEdit.getParent());
        myBundle.putString("child", toEdit.getChild());

        showActivity.putExtras(myBundle);
        startActivityForResult(showActivity, 200);
    }
    public void showClick() {
        int count = 0;
        ToDoList chosen = tasks.get(0);
        for(ToDoList t : tasks){
            if(t.isSelected()) {
                chosen = t;
                count++;
            }
        }

        if (count != 1) {
            Toast.makeText(MainActivity.this, "CHOOSE ONE TASK!", Toast.LENGTH_SHORT).show();
            clearSelect();
            return;
        }

        Intent listActivity = new Intent(MainActivity.this, ListActivity.class);
        Bundle myBundle     = new Bundle();
        myBundle.putString("child", chosen.getChild());
        myBundle.putString("title", chosen.getTitle());
        listActivity.putExtras(myBundle);
        startActivityForResult(listActivity, 100);
    }
    public void clearClick() {
        for (ToDoList a: tasks){
            sampleDB.execSQL("Drop table if exists "+ a.getChild());
        }
        sampleDB.execSQL("Drop table if exists " + tableName);

        createTable(tableName);
        updateList();
        adapter.notifyDataSetChanged();
    }
    public void deleteClick() {
        ArrayList<ToDoList> rem = new ArrayList();
        for (ToDoList t : tasks) {
            if (t.isSelected())
                rem.add(t);
        }
        for (ToDoList a : rem) {
            sampleDB.execSQL("Drop table if exists " + a.getChild());
            deleteData(a.getDate());
        }
        updateList();
        if (rem.size() == 0) {
            Toast.makeText(MainActivity.this, "Choose something to delete", Toast.LENGTH_SHORT).show();
        }
    }
    public void addClick() {
        String input = newTask.getText().toString();
        if (!input.isEmpty()) {
            cursor = sampleDB.rawQuery("SELECT * FROM " + tableName , null);
            int dbIndex = cursor.getCount();
            String child = "ChildList" + String.valueOf(dbIndex);
            String parent = "null";
            Date d = new Date();
            insertData(input, "None", String.valueOf(d), parent, child);
            createTable(child);
            newTask.setText("");
        } else {
            Toast.makeText(MainActivity.this, "Add a task!", Toast.LENGTH_SHORT).show();
        }
    }
// -------------------------------  END BUTTON CLICKS ------------------------------


// -------------------------------  FOR TABLE ACTIONS ----------------------------------
    private void createTable(String name) {
        sampleDB.execSQL("CREATE TABLE IF NOT EXISTS " + name +
                " (Title VARCHAR, " +
                "  Desc VARCHAR, " +
                "  Date VARCHAR, " +
                "  Parent VARCHAR, " +
                "  Child VARCHAR)");
        Log.i("Created Table", name);
    }
    private void insertData(String title, String desc, String date, String parent, String child) {
        ContentValues values = new ContentValues();
        values.put("Title", title);
        values.put("Desc", desc);
        values.put("Date", date);
        values.put("Parent", parent);
        values.put("Child", child);
        Log.i("Insert Data", title);
        sampleDB.insert(tableName, null, values);
        updateList();
    }
    private void deleteData(String date) {
        sampleDB.delete(tableName, "Date=?", new String[]{date});
    }
    private void editData(String title, String desc, String date, String parent, String child){
        deleteData(date);
        Log.i("deleted ", date);
        insertData(title, desc, date, parent, child);
        Log.i("inserted ", date);
    }
// -------------------------------  END TABLE ACTIONS ---------------------------------


// -------------------------------------- HELPER FUNCTIONS ----------------------------------
    public void clearSelect(){
        for(ToDoList i: tasks) {
            i.unset();
        }
        updateList();
    }
    public boolean databaseEmpty(){
        cursor = sampleDB.rawQuery("SELECT * FROM " + tableName , null);
        Log.i("database empty", String.valueOf(cursor.getCount()));
        if(cursor.getCount() == 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }
    public void updateList(){
        tasks.clear();
        cursor = sampleDB.rawQuery("SELECT * FROM " + tableName , null);
        if(cursor != null) {
            for(int i = 0; i < cursor.getCount(); i++){
                cursor.moveToPosition(i);
                String title    = cursor.getString(cursor.getColumnIndex("Title"));
                String desc     = cursor.getString(cursor.getColumnIndex("Desc"));
                String date     = cursor.getString(cursor.getColumnIndex("Date"));
                String parent   = cursor.getString(cursor.getColumnIndex("Parent"));
                String child    = cursor.getString(cursor.getColumnIndex("Child"));

                ToDoList newTodo = new ToDoParent(title, desc, date, parent, child);
                tasks.add(newTodo);
                Log.i("update list", title);
            }
            cursor.close();
            adapter.notifyDataSetChanged();
        }
    }
// -----------------------------------  END HELPER FUNCTIONS ---------------------------------


// -----------------------------------  ACTIVITY STATES -------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoLV      = (ListView) findViewById(R.id.todoLV_parent);
        newTask     = (EditText) findViewById(R.id.newTask_parent);

        addButton   = (Button) findViewById(R.id.add_parent);
        delButton   = (Button) findViewById(R.id.delete_parent);
        editButton  = (Button) findViewById(R.id.edit_parent);
        showButton  = (Button) findViewById(R.id.show_parent);
        clearButton = (Button) findViewById(R.id.clear_parent);

        try{
            sampleDB = openOrCreateDatabase("NAME", MODE_PRIVATE, null);
            createTable(tableName);
        }catch(SQLiteException se) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        }
    }
    @Override
    public void onStart() {
        super.onStart();

        cursor = sampleDB.rawQuery("SELECT  * FROM " +  tableName, null);
        if(cursor != null) {
            cursor.moveToFirst();
            while(cursor.moveToNext()){
                String title        = cursor.getString(cursor.getColumnIndex("Title"));
                String date         = cursor.getString(cursor.getColumnIndex("Date"));
                String parent       = cursor.getString(cursor.getColumnIndex("Parent"));
                String child        = cursor.getString(cursor.getColumnIndex("Child"));
                String desc         = cursor.getString(cursor.getColumnIndex("Desc"));

                ToDoList newTodo    = new ToDoParent(title, desc, date, parent, child);
                tasks.add(newTodo);
            }
            cursor.close();
            adapter = new TaskAdapter(this, tasks);
        }
        todoLV.setAdapter(adapter);
        addButton.setOnClickListener(this);
        delButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
        showButton.setOnClickListener(this);
    }
    @Override
    public void onResume(){
        super.onResume();
        updateList();
    }
// -----------------------------------  END ACTIVITY STATES ----------------------------------------


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(requestCode == 200){
                Bundle dataBundle   = data.getExtras();
                newTitle            = dataBundle.getString("newTitle");
                newDesc             = dataBundle.getString("newDesc");
                oldDate             = dataBundle.getString("oldDate");
                oldParent           = dataBundle.getString("oldParent");
                oldChild            = dataBundle.getString("oldChild");
                editData(newTitle, newDesc, oldDate, oldParent, oldChild);
                returned = true;
            }
        } catch (Exception e) {
            Log.i("ERROR", String.valueOf(requestCode));
        }
    }
}