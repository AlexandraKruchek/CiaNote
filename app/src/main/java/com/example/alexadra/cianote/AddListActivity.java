package com.example.alexadra.cianote;

/*    Класс осуществляет добавление данных в базу данных  */

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;

public class AddListActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    EditText etTaskName, etSubtask;     // текст задачи
    Switch swReminder;       // свитч отвечает за включение/отключение напоминания
    TextView tvDate, tvTime; // поля, содержащие заданные дату и время
    RatingBar ratingBar;
    ListView lvSubtask;      // список для отображения подзадач

    final ArrayList<String> subtasks = new ArrayList<>(); // список для хранения списка подзадач
    /** Создаём адаптер ArrayAdapter, чтобы привязать массив к ListView */
    private ArrayAdapter<String> adapter;

    /*** здесь должен быть еще приоритет */

    DBHelper dbHelper;

    /** дата и время. запоминает текущую дату**/
    Calendar calendar = Calendar.getInstance();

    int myYear = calendar.get(Calendar.YEAR);
    int myMonth = calendar.get(Calendar.MONTH);
    int myDay = calendar.get(Calendar.DAY_OF_MONTH);
    int myHour = 0;
    int myMinute = 0;
    int listItem=-1;

    public static final int NOTIFICATION_ID = 1;

    /*public AddListActivity(ArrayAdapter<String> adapter) {
        this.adapter = adapter;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        dbHelper = new DBHelper(this); // подключение к БД


        // получение данных из полей
        etTaskName = findViewById(R.id.etTaskName);
        etSubtask = findViewById(R.id.etSubtask);
        swReminder = findViewById(R.id.swReminder);
        swReminder.setOnCheckedChangeListener(this); // слушатель для свитча
        tvDate=findViewById(R.id.tvDate);
        tvTime     = findViewById(R.id.tvTime);
        tvTime.setVisibility(View.GONE);
        tvDate.setVisibility(View.GONE);
        ratingBar=findViewById(R.id.ratingBar2);
        lvSubtask = findViewById(R.id.lvSubtask);
        /*** здесь должен быть еще приоритет */

        // обработка нажатия на пункт списка подзадач в добавлении/редактирован
        lvSubtask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etSubtask.setText(subtasks.get(position));
                listItem=position;
            }
        });

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
            new TimePickerDialog(AddListActivity.this, t,
                    0 ,
                    0, true)
                    .show();
     }

    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        DatePickerDialog datePickerDialog=new DatePickerDialog(AddListActivity.this, d,
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

/***----------------------Обработка нажатий кнопок добавления----------------------------------***/

    public void addClick(View view) {

        // добавленние записи в базу данных

        String name = etTaskName.getText().toString();
        int dateCreate = (int) (System.currentTimeMillis()/1000);
        int dateRemind = (int) (calendar.getTimeInMillis()/1000);
        int priority = (int) ratingBar.getRating();
        boolean noted = false;


        ContentValues contentValues=new ContentValues();

        contentValues.put("name",name);
        contentValues.put("date_create",dateCreate);
        contentValues.put("reminder",dateRemind);
        contentValues.put("priority",priority);
        contentValues.put("noted",noted);

        SQLiteDatabase database=dbHelper.getWritableDatabase();

        // запрос в БД на запись
        database.insert("work_list",null,contentValues);

        Cursor cursor = database.query(DBHelper.TABLE_LIST, null, null, null, null, null, null);

        /** получаем id добавленной задачи, чтобы добавить его в таблицу подзадач */
        cursor.moveToLast();
        long mTaskId = cursor.getLong(cursor.getColumnIndex("_id"));
        int mainTaskId = (int) mTaskId;

        // вывод записей из БД в лог
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int dateIndex = cursor.getColumnIndex(DBHelper.KEY_CREATE_DATE);
            int remIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER);
            int priorIndex = cursor.getColumnIndex(DBHelper.KEY_PRIORITY);
            int notedIndex = cursor.getColumnIndex(DBHelper.KEY_NOTED);
            do {
                Log.d("mLog", "ID = " + cursor.getInt(idIndex) +
                        ", name = " + cursor.getString(nameIndex) +
                        ", date = " + cursor.getString(dateIndex) +
                        ", rem = " + cursor.getString(remIndex) +
                        ", prior = " + cursor.getString(priorIndex) +
                        ", ID = " + mTaskId +
                        ", IDd = " + mainTaskId +
                        ", noted = " + cursor.getString(notedIndex));
            } while (cursor.moveToNext());
        } else
            Log.d("mLog","0 rows");

        ContentValues contentValues1 = new ContentValues();

        // получение данных подзадачи
        String subtask = "";

        /** Цикл, который записывает по очереди из списка подзадачи в базу данных */

        if(subtasks.isEmpty()){
            Log.d("MLog: ","is Empty");
        }else{
            for (int i = 0; i < subtasks.size(); i++) {
                subtask = subtasks.get(i);

                contentValues1.put("subtask_text",subtask);
                contentValues.put("checked",0);
                contentValues1.put("main_task",mainTaskId);

                // запрос в БД на запись
                database.insert("subtask",null,contentValues1);

                cursor = database.query(DBHelper.TABLE_SUBTASK, null, null, null, null, null, null);
                contentValues1.clear();
            }
        }

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int textIndex = cursor.getColumnIndex(DBHelper.KEY_STEXT);
            int main_task = cursor.getColumnIndex(DBHelper.KEY_TASK);

            /*do {
                Log.d("mLog", "ID = " + cursor.getInt(idIndex) +
                        ", text = " + cursor.getString(textIndex) +
                        ", main_task = " + cursor.getString(main_task));
            } while (cursor.moveToNext());*/
        } else
            Log.d("mLog","0 rows");
        cursor.close();

        if(swReminder.isChecked()){
            sendNotification();
         }

    }

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

    /**----------------------------------------------------------------------------------**/



}
