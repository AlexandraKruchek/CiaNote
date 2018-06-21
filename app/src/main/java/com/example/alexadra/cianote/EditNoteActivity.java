package com.example.alexadra.cianote;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditNoteActivity extends AppCompatActivity implements View.OnClickListener {

    long id_note;

    boolean edit = false;  // переменная, определяющая состояние заметки "Просмотр" или "Редактирование"

    DBHelper dbHelper;

    EditText etTitle;
    EditText etNote;

    KeyListener tlistener;
    KeyListener nlistener;
    Drawable tcolor;
    Drawable ncolor;

    Button btnDelete, btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        dbHelper = new DBHelper(this);

        Intent intent = getIntent();
        id_note = intent.getLongExtra("id_note", -1);

        edit = false; // переменная, определяющая состояние заметки "Просмотр" или "Редактирование"

        etTitle = findViewById(R.id.etTitle);
        etNote = findViewById(R.id.etNote);
        tlistener = etTitle.getKeyListener();
        nlistener = etNote.getKeyListener();
        tcolor = etTitle.getBackground();
        ncolor = etNote.getBackground();


        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);

        btnEdit   = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(this);

        Open(id_note);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private  void  BlockNote(){

        if(edit == false){

            etTitle.setCursorVisible(false);
            etTitle.setBackgroundColor(Color.TRANSPARENT);
            etTitle.setKeyListener(null);

            etNote.setCursorVisible(false);
            etNote.setBackgroundColor(Color.TRANSPARENT);
            etNote.setKeyListener(null);

        } else {
            etTitle.setCursorVisible(true);
            etTitle.setBackground(tcolor);
            etTitle.setKeyListener(tlistener);

            etNote.setCursorVisible(true);
            etNote.setBackground(ncolor);
            etNote.setKeyListener(nlistener);
        }
    }

    /**       Вывод заметки      **/
    private void Open(long id_note) {

        String selection = null;

        dbHelper = new DBHelper(this);

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        selection = "_id = " + id_note;

        Cursor cursor = database.query("notes", null, selection, null, null, null, null);

        if(cursor!=null){
            if(cursor.moveToFirst()){

                etTitle.setText(cursor.getString(1));
                etNote.setText(cursor.getString(2));
            }
            cursor.close();
        } //else
        //Log.d("mylogs", "Cursor is null");
        dbHelper.close();

        BlockNote();

    }

    @Override
    public void onClick(View v) {
        if (edit==false){
            switch (v.getId()){
                case R.id.btnDelete: // удаление
                    /** удаление из базы по id */
                    Delete();

                    /* Выход в MainActivity */
                    Intent intent=new Intent(EditNoteActivity.this,MainnActivity.class);
                    startActivity(intent);

                    /* Тост */
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Удалено", Toast.LENGTH_SHORT);
                    toast.show();
                    break;

                case R.id.btnEdit:  // режим редактирования
                    /* Режим редактирования */
                    edit = true;

                    /* Разблокировка полей для редактирования */
                    BlockNote();

                    /* Изменение надписей кнопок */
                    btnDelete.setText("Отмена");
                    btnEdit.setText("Сохранить");

                    break;
            }
        } else {
            switch (v.getId()) {
                case R.id.btnDelete: // Отмена редактирования
                    /* Выход в MainActivity */
                    Intent intent=new Intent(EditNoteActivity.this,MainnActivity.class);
                    startActivity(intent);

                    break;

                case R.id.btnEdit: // Обновление данных

                    if (etTitle.getText().toString().equals(""))
                    {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Введите название", Toast.LENGTH_LONG);
                        toast.show();
                    }
                    else
                    {
                        /** обновление поля в базе по id */
                        Update();

                        /* Тост */
                        Toast toast3 = Toast.makeText(getApplicationContext(),
                                "Изменено", Toast.LENGTH_SHORT);
                        toast3.show();

                        /* Режим просмотра */
                        edit = false;

                        /* Блокирование полей */
                        BlockNote();

                        /* Изменение надписей кнопок */
                        btnDelete.setText("Удалить");
                        btnEdit.setText("Изменить");

                        break;
                    }

            }
        }
    }

    private void Delete() {

        String title = etTitle.getText().toString();
        String note = etNote.getText().toString();

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.delete(DBHelper.TABLE_NOTES, "_id = ?", new String[]{Long.toString(id_note)});
    }

    private void Update() {

        String title = etTitle.getText().toString();
        String note = etNote.getText().toString();

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_TITLE, title);
        contentValues.put(DBHelper.KEY_NOTE, note);

        database.update(DBHelper.TABLE_NOTES, contentValues, "_id = ?", new String[]{Long.toString(id_note)});
    }
}
