package com.example.alexadra.cianote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

public class OpenActivity extends AppCompatActivity {


    int groupPosition;
    ExpandableListView exListView;

    EditText etTaskName, etSubtask;     // текст задачи
    Switch swReminder;       // свитч отвечает за включение/отключение напоминания
    TextView tvDate, tvTime; // поля, содержащие заданные дату и время
    RatingBar ratingBar;
    ListView lvSubtask;      // список для отображения подзадач


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        etTaskName = findViewById(R.id.etTaskName);
        etSubtask = findViewById(R.id.etSubtask);
        swReminder = findViewById(R.id.swReminder);
        tvDate=findViewById(R.id.tvDate);
        tvTime     = findViewById(R.id.tvTime);
        ratingBar=findViewById(R.id.ratingBar2);
        lvSubtask = findViewById(R.id.lvSubtask);
        exListView = (ExpandableListView)findViewById(R.id.exListView);

        Intent intent = getIntent();
        String ID = intent.getStringExtra("ID");
        Log.d("mylogs", "ID = " + ID);

    }

    public void Edit(){



    }

}
