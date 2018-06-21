package com.example.alexadra.cianote;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


public class AddActivity extends AppCompatActivity {

    DBHelper dbHelper;

    EditText etTitle, etNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
      //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etNote = (EditText) findViewById(R.id.etNote);

        dbHelper = new DBHelper(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    public  void Insert() {
        String title = etTitle.getText().toString();
        String note = etNote.getText().toString();

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_TITLE, title);
        contentValues.put(DBHelper.KEY_NOTE, note);

        database.insert(DBHelper.TABLE_NOTES, null, contentValues);
    }

    //___

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {

            if (etTitle.getText().toString().equals(""))
            {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Введите название", Toast.LENGTH_LONG);
                toast.show();
            }
            else
            {
                // выход в MainActivity
                Insert();
                Intent intent=new Intent(AddActivity.this,MainnActivity.class);
                startActivity(intent);

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Сохранено", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            }


        }

        return super.onOptionsItemSelected(item);
    }
}

