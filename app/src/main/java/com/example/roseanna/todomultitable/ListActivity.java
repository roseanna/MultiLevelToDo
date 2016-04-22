package com.example.roseanna.todomultitable;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by roseanna on 3/19/16.
 */
public class ListActivity extends Activity implements View.OnClickListener{
    TaskAdapter adapter;
    ArrayList<ToDoList> tasks = new ArrayList<>();
    public ListView todoLV;
    public EditText newTask;

    public Button addButton, delButton, clearButton, showButton, editButton, backButton;
    Intent myIntent;
    // For edit
    public boolean returned = false;
    public String newTitle;
    public int oldPosition;
    public String newDesc;
    public String oldDate, oldParent, oldChild;

    // For SQL stuff
    private static String tableName = "Todo_Table";
    private SQLiteDatabase sampleDB = null;
    private Cursor cursor           = null;

    // For Button clicks
    @Override
    public void onClick(View v) {
        if(!databaseEmpty()) {
            switch (v.getId()) {
                case R.id.addButton:
                    addClick();
                    break;
                case R.id.deleteButton:
                    deleteClick();
                    break;
                case R.id.clearButton:
                    clearClick();
                    break;
                case R.id.showButton:
                    showClick();
                    break;
                case R.id.editButton:
                    editClick();
                    break;
                case R.id.backButton:
                    backClick();
                    break;
            }
            clearSelect();
        }
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
            Toast.makeText(ListActivity.this, "CHOOSE ONE TASK!", Toast.LENGTH_SHORT).show();
            clearSelect();
            return;
        }
        ToDoList toEdit     = tasks.get(oldPosition);
        Intent showActivity = new Intent(ListActivity.this, EditActivity.class);
        Bundle myBundle     = new Bundle();

        myBundle.putString("title", toEdit.getTitle());
        myBundle.putString("description", toEdit.getDescription());
        myBundle.putString("date", toEdit.getDate());

        showActivity.putExtras(myBundle);
        startActivityForResult(showActivity, 200);
    }
    public void showClick() {
        if(dbEmpty()){
            Toast.makeText(ListActivity.this, "Database Empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        int count = 0;
        ToDoList chosen = tasks.get(0);
        for(ToDoList t : tasks){
            if(t.isSelected()) {
                chosen = t;
                count++;
            }
        }

        if (count != 1) {
            Toast.makeText(ListActivity.this, "CHOOSE ONE TASK!", Toast.LENGTH_SHORT).show();
            clearSelect();
            return;
        }

        Intent showActivity = new Intent(ListActivity.this, ShowActivity.class);
        Bundle myBundle     = new Bundle();
        myBundle.putString("date", chosen.getDate());
        myBundle.putString("title", chosen.getTitle());
        myBundle.putString("description", chosen.getDescription());

        showActivity.putExtras(myBundle);
        startActivityForResult(showActivity, 100);
    }

    public boolean dbEmpty(){
        Cursor cursor = sampleDB.rawQuery("Select * from " + tableName, null);
        if(cursor.getCount() == 0){
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public void clearClick() {
        sampleDB.execSQL("DROP TABLE " + tableName);
        sampleDB.execSQL("CREATE TABLE " + tableName +
                " (Title VARCHAR, " +
                "  Desc VARCHAR, " +
                "  Date VARCHAR," +
                "  Parent VARCHAR," +
                "  Child VARCHAR);");
        tasks.clear();
        adapter.notifyDataSetChanged();
    }
    public void deleteClick() {
        ArrayList<ToDoList> rem = new ArrayList();
        for (ToDoList t : tasks) {
            if (t.isSelected())
                rem.add(t);
        }
        for (ToDoList a : rem) {
            deleteData(a.getDate());
        }
        updateList();
        if (rem.size() == 0) {
            Toast.makeText(ListActivity.this, "Choose something to delete", Toast.LENGTH_SHORT).show();
        }
    }
    public void addClick() {
        String input = newTask.getText().toString();
        Log.i("add", input);
        if (!input.isEmpty()) {
            Date d = new Date();
            insertData(input, "None", String.valueOf(d), "parent", "null");
            newTask.setText("");
            cursor.close();
        } else {
            Toast.makeText(ListActivity.this, "Add a task!", Toast.LENGTH_SHORT).show();
        }
    }
    public void backClick(){
        Log.i("back", "working");
        setResult(Activity.RESULT_OK, myIntent);
        finish();
    }
    // To clear out the selects on return from clicks
    public void clearSelect(){
        for(ToDoList i: tasks) {
            i.unset();
        }
        updateList();
    }

    public boolean databaseEmpty(){
        cursor = sampleDB.rawQuery("SELECT * from " + tableName , null);
        if(cursor == null)
            return true;
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        TextView title = (TextView) findViewById(R.id.childTitle);
        todoLV      = (ListView) findViewById(R.id.todoLV);
        newTask     = (EditText) findViewById(R.id.newTaskField);

        addButton   = (Button) findViewById(R.id.addButton);
        delButton   = (Button) findViewById(R.id.deleteButton);
        editButton  = (Button) findViewById(R.id.editButton);
        showButton  = (Button) findViewById(R.id.showButton);
        clearButton = (Button) findViewById(R.id.clearButton);
        backButton  = (Button) findViewById(R.id.backButton);

        myIntent = getIntent();
        Bundle myBundle = myIntent.getExtras();
        String parentTitle = myBundle.getString("title");
        tableName = myBundle.getString("child");
        Log.i("tablename", tableName);

        try{
            sampleDB = openOrCreateDatabase("NAME", MODE_PRIVATE, null);
            createTable(tableName);
        }catch(SQLiteException se) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        }

        title.setText(parentTitle);
    }


    private void createTable(String name) {
        Log.d(getLocalClassName(), "in create table");
        sampleDB.execSQL("CREATE TABLE IF NOT EXISTS " + name +
                " (Title VARCHAR, " +
                "  Desc VARCHAR, " +
                "  Date VARCHAR," +
                "  Parent VARCHAR," +
                "  Child VARCHAR);");
        Log.i("Created Table", "Done");
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
        insertData(title, desc, date, "parent", "null");
    }

    public void updateList(){
        tasks.clear();
        Log.i("Update", tableName);
        cursor = sampleDB.rawQuery("SELECT * FROM " + tableName , null);
        if(cursor != null) {
            Log.i("Cursor not null", String.valueOf(cursor.getColumnCount()));
            for(int i = 0; i < cursor.getCount(); i++){
                cursor.moveToPosition(i);
                String title    = cursor.getString(cursor.getColumnIndex("Title"));
                String desc     = cursor.getString(cursor.getColumnIndex("Desc"));
                String date     = cursor.getString(cursor.getColumnIndex("Date"));
                String parent   = cursor.getString(cursor.getColumnIndex("Parent"));
                ToDoList newTodo    = new ToDo(title, desc, date, parent, "null");
                tasks.add(newTodo);
                Log.i("update list", String.valueOf(tasks.size()));
            }
            cursor.close();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("Tablename", tableName);
        cursor = sampleDB.rawQuery("SELECT * FROM " + tableName , null);
        if(cursor != null) {
            cursor.moveToFirst();
            while(cursor.moveToNext()){
                String title    = cursor.getString(cursor.getColumnIndex("Title"));
                String desc     = cursor.getString(cursor.getColumnIndex("Desc"));
                String date     = cursor.getString(cursor.getColumnIndex("Date"));
                String parent   = cursor.getString(cursor.getColumnIndex("Parent"));
                ToDo newTodo    = new ToDo(title, desc, date, parent, "null");
                tasks.add(newTodo);
            }
            cursor.close();
            adapter = new TaskAdapter(this, tasks);
            Log.i("adapter", String.valueOf(adapter.getTasks().size()));
        }
        todoLV.setAdapter(adapter);
        addButton.setOnClickListener(this);
        delButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
        showButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume(){
        super.onResume();
        updateList();
    }
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
                editData(newTitle, newDesc, oldDate, "parent", "null");
                returned = true;
            }
        } catch (Exception e) {
            Log.i("ERROR", String.valueOf(requestCode));
        }
    }
}