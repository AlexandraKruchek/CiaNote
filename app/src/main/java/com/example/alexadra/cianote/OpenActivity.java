package com.example.alexadra.cianote;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class OpenActivity extends AppCompatActivity {


    int groupPosition;
    ExpandableListView exListView;

    EditText etTaskName, etSubtask;     // текст задачи
    Switch swReminder;       // свитч отвечает за включение/отключение напоминания
    TextView tvDate, tvTime; // поля, содержащие заданные дату и время
    RatingBar ratingBar;
    ListView lvSubtask;      // список для отображения подзадач

    final ArrayList<String> subtasks = new ArrayList<>(); // список для хранения списка подзадач
    /** Создаём адаптер ArrayAdapter, чтобы привязать массив к ListView */
    private ArrayAdapter<String> adapter;
    int listItem=-1;
    DBHelper dbHelper;

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

        dbHelper = new DBHelper(this);

        lvSubtask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etSubtask.setText(subtasks.get(position));
                listItem=position;
            }
        });

        Edit();

    }

    public void Edit(){

        String selection = "";

        Intent intent = getIntent();
        String ID = intent.getStringExtra("ID");
        Log.d("mylogs", "ID = " + ID);

        //Cursor cursor=(Cursor)exListView.getExpandableListAdapter().getGroup(groupPosition);
        DBHelper dbHelper=new DBHelper(this);
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();

        selection = "_id = " + 5;

        Cursor cursor = sqLiteDatabase.query("work_list",null,selection,null,null,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){

                etTaskName.setText(cursor.getString(1)); // имя задачи
                ratingBar.setRating(cursor.getInt(4));
            }
            cursor.close();
        }
        /** ВЫВОД ПОДЗАДАЧ **/

        selection = "main_task = " + 5;
        cursor = sqLiteDatabase.query("subtask",null,selection,null,null,null,null);
        if(cursor.moveToFirst()){
            int subtaskIndex = cursor.getColumnIndex(DBHelper.KEY_STEXT);

            do{
                subtasks.add(cursor.getString(subtaskIndex));
            }while (cursor.moveToNext());
        }
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, subtasks);
        lvSubtask.setAdapter(adapter);

        adapter.notifyDataSetChanged();

    }

    /**------------------------------------------------------------------------------------------**/

    public void addClick(View view) {

        Toast toast = Toast.makeText(this,
                "add click!", Toast.LENGTH_SHORT);
        toast.show();

        String name = etTaskName.getText().toString();
        int dateCreate = (int) (System.currentTimeMillis()/1000);
        //int dateRemind = (int) (calendar.getTimeInMillis()/1000);
        int priority = (int) ratingBar.getRating();
        boolean noted = false;


        ContentValues contentValues=new ContentValues();
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        contentValues.put("name",name);
        contentValues.put("date_create",dateCreate);
       // contentValues.put("reminder",dateRemind);
        contentValues.put("priority",priority);
        contentValues.put("noted",noted);

        //database.update(DBHelper.TABLE_LIST, contentValues, "_id = ?", new String[]{Long.toString()});

    }
    /**------------------------------------------------------------------------------------------**/
    public void addSubClick(View view) {

        if (listItem==-1) {
            subtasks.add(etSubtask.getText().toString());
        }else{
            subtasks.set(listItem,etSubtask.getText().toString());
        }
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, subtasks);
        lvSubtask.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        etSubtask.setText("");
        listItem=-1;
    }

    /**------------------------------------------------------------------------------------------**/
}
