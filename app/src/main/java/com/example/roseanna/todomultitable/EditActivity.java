package com.example.roseanna.todomultitable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by roseanna on 3/19/16.
 */
public class EditActivity extends AppCompatActivity implements View.OnClickListener{

    Button done;
    EditText title, desc;
    Intent myIntent;
    Bundle myBundle;
    String oldTitle, date, parent, child;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);

        title   = (EditText) findViewById(R.id.title);
        desc    = (EditText) findViewById(R.id.showTextView);
        done    = (Button) findViewById(R.id.done);

        myIntent = getIntent();
        myBundle = myIntent.getExtras();
        oldTitle = myBundle.getString("title");
        String y = myBundle.getString("description");
        if (y.equals("None"))
            y = " ";
        date     = myBundle.getString("date");
        parent   = myBundle.getString("parent");
        child    = myBundle.getString("child");
        title.setText(oldTitle);
        desc.setText(y);

        done.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        myBundle.putString("newTitle", String.valueOf(title.getText()));
        myBundle.putString("newDesc", String.valueOf(desc.getText()));
        myBundle.putString("oldDate", date);
        myBundle.putString("oldParent", parent);
        myBundle.putString("oldChild", child);
        myIntent.putExtras(myBundle);
        setResult(Activity.RESULT_OK, myIntent);
        finish();
    }
}
