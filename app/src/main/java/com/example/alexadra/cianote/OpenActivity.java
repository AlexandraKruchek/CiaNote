package com.example.alexadra.cianote;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class OpenActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {


    int groupPosition;
    ExpandableListView exListView;

    EditText etTaskName, etSubtask;     // текст задачи
    Switch swReminder;       // свитч отвечает за включение/отключение напоминания
    TextView tvDate, tvTime; // поля, содержащие заданные дату и время
    RatingBar ratingBar;
    ListView lvSubtask;      // список для отображения подзадач

    Long ID;

    final ArrayList<String> subtasks = new ArrayList<>(); // список для хранения списка подзадач
    /** Создаём адаптер ArrayAdapter, чтобы привязать массив к ListView */
    private ArrayAdapter<String> adapter;

    int listItem=-1;

    DBHelper dbHelper;

    /** дата и время. запоминает текущую дату**/
    Calendar calendar = Calendar.getInstance();

    int myYear = calendar.get(Calendar.YEAR);
    int myMonth = calendar.get(Calendar.MONTH);
    int myDay = calendar.get(Calendar.DAY_OF_MONTH);
    int myHour = 0;
    int myMinute = 0;

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
        swReminder.setOnCheckedChangeListener(this);

        dbHelper = new DBHelper(this);

        Intent intent = getIntent();
        ID = intent.getLongExtra("ID", -1);

        lvSubtask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etSubtask.setText(subtasks.get(position));
                listItem=position;
            }
        });

        Edit();

    }

    /**  Слушатель на свитч. Делает поля с датой и временем видимыми и невидимыми  **/
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {

            tvTime.setVisibility(View.VISIBLE);
            tvDate.setVisibility(View.VISIBLE);

        } else {

            tvTime.setVisibility(View.GONE);
            tvDate.setVisibility(View.GONE);
        }
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(OpenActivity.this, t,
                0 ,
                0, true)
                .show();
    }

    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        DatePickerDialog datePickerDialog=new DatePickerDialog(OpenActivity.this, d,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
        datePickerDialog.show();
    }

    /***----------------------- Создание диалога даты  -------------------***/

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            calendar.set(year,monthOfYear,dayOfMonth);
            myYear = calendar.get(Calendar.YEAR);
            myMonth = calendar.get(Calendar.MONTH);
            myDay = calendar.get(Calendar.DAY_OF_MONTH);
            monthOfYear++;
            tvDate.setText(dayOfMonth + "." + monthOfYear + "." + year);
        }
    };

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            myHour = hourOfDay;
            myMinute = minute;
            tvTime.setText(hourOfDay + ":" + minute);

        }
    };

    /****-----------------------------Уведомления--------------------------------------****/

    public void sendNotification() {

        long rem = calendar.getTimeInMillis();
        Intent intent = new Intent(this, NotifReceiver.class);
        intent.putExtra("task",etTaskName.getText().toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, rem, pendingIntent);

    }

    public void Edit(){

        String selection = "";

        //Cursor cursor=(Cursor)exListView.getExpandableListAdapter().getGroup(groupPosition);
        DBHelper dbHelper=new DBHelper(this);
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();

        selection = "_id = " + ID;

        Cursor cursor = sqLiteDatabase.query("work_list",null,selection,null,null,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){

                etTaskName.setText(cursor.getString(1)); // имя задачи
                ratingBar.setRating(cursor.getInt(4));
            }
            cursor.close();
        }
        /** ВЫВОД ПОДЗАДАЧ **/

        selection = "main_task = " + ID;
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

        database.update(DBHelper.TABLE_LIST, contentValues, "_id = ?", new String[]{Long.toString(ID)});

        /** добавление подзадач*/

       // Cursor cursor = database.query("subtask", null, null, null, null, null, null);
        ContentValues contentValues1 = new ContentValues();
        // получение данных подзадачи
        String subtask = "";
        Cursor cursor;
        if(subtasks.isEmpty()){
            Log.d("MLog: ","is Empty");
        }else{
            for (int i = 0; i < subtasks.size(); i++) {
                subtask = subtasks.get(i);

                contentValues1.put("subtask_text",subtask);

                // запрос в БД на запись
                database.update("subtask", contentValues1, DBHelper.KEY_TASK + " = " + ID,null);
                contentValues1.clear();
            }
        }

        if(swReminder.isChecked()){
            sendNotification();
        }

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
