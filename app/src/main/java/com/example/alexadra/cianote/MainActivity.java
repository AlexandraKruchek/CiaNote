package com.example.alexadra.cianote;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int groupPosition;
    ExpandableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView= (ExpandableListView)findViewById(R.id.exListView);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,AddListActivity.class);
                startActivity(intent);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

// Находим наш list

        //Создаем набор данных для адаптера
        ArrayList<ArrayList<String>> groups = new ArrayList<ArrayList<String>>(); // массив групп подзадач
        ArrayList<String> tasks = new ArrayList<String>();                             // массив задач
        ArrayList<String> children = new ArrayList<String>();                          // массив подзадач одной группы

      /*  while(!cursor.isafterlast){
            Arraylist<string> stringArraylist
                    cursor2
                            select * from table where id=
            while (cursor2.isafterlast) {
                stringArraylist.add(cursor2.getString(10))
                cursor2.moveto next
            }
            groups.add(stringArraylist);
            cursor.movetonext();
        }*/



        ArrayList<String> children2 = new ArrayList<String>();
        /*children1.add("Child_1");
        children1.add("Child_2");
        groups.add(children1);*/

        children2.add("Child_1");
        children2.add("Child_2");
        children2.add("Child_3");
        groups.add(children2);
        //Создаем адаптер и передаем context и список с данными
        //ExpListAdapter adapter = new ExpListAdapter(getApplicationContext(), groups, tasks);
        DBHelper dbHelper=new DBHelper(this);
        final SQLiteDatabase database=dbHelper.getReadableDatabase();

        final Cursor cursor=database.query(DBHelper.TABLE_LIST,null,null,null,null,null,null);
        ExpAdapter expAdapter=new ExpAdapter(cursor, getApplicationContext());
        listView.setAdapter(expAdapter);
        registerForContextMenu(listView);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                CheckBox check=v.findViewById(R.id.checkSubtask);
                TextView textView=v.findViewById(R.id.textChild);

                check.setChecked(!check.isChecked());
                if (check.isChecked()){
                    textView.setPaintFlags(textView.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
                }else{
                    textView.setPaintFlags(textView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                }
                Cursor child=(Cursor)parent.getExpandableListAdapter().getChild(groupPosition,childPosition);
                ContentValues contentValues=new ContentValues();
                contentValues.put(DBHelper.KEY_CHECKED,check.isChecked());
                database.update(DBHelper.TABLE_SUBTASK,contentValues,DBHelper.KEY_ID+"="+child.getString(0),null);
                return false;
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (ExpandableListView.getPackedPositionType(id)==ExpandableListView.PACKED_POSITION_TYPE_GROUP){
                      int groupPosition = listView.getPackedPositionGroup(id);
//                      int childPosition = listView.getPackedPositionChild(id);
                      openContextMenu(listView);
                      return true;
                    } else {
                        //лонгклик был на группе
//                        int groupPosition = listView.getPackedPositionGroup(id);
                        return true;
                }
                //return false;
           }
       });

        /// ExpAdapter adapter1 = new ExpAdapter()



        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(AddListActivity.NOTIFICATION_ID);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(v.getId(),1,0, "Редактировать");
        menu.add(v.getId(),2,0,"Удалить");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle()=="Редактировать"){


        }
        if (item.getTitle()=="Удалить"){
            Cursor cursor=(Cursor)listView.getExpandableListAdapter().getGroup(groupPosition);
            DBHelper dbHelper=new DBHelper(this);
            SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
            sqLiteDatabase.delete(DBHelper.TABLE_LIST,DBHelper.KEY_ID+"="+cursor.getString(0),null);
            sqLiteDatabase.delete(DBHelper.TABLE_SUBTASK,DBHelper.KEY_TASK+"="+cursor.getString(0),null);
            cursor=sqLiteDatabase.query(DBHelper.TABLE_LIST,null,null,null,null,null,null);
            ExpAdapter expAdapter=new ExpAdapter(cursor,getApplicationContext());
            listView.setAdapter(expAdapter);
        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {
        DBHelper dbHelper=new DBHelper(this);
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        Cursor cursor=database.query(DBHelper.TABLE_LIST,null,null,null,null,null,null);
        ExpAdapter expAdapter=new ExpAdapter(cursor,getApplicationContext());
        ExpandableListView listView = (ExpandableListView)findViewById(R.id.exListView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setIndicatorBoundsRelative(600, 750);
        listView.setAdapter(expAdapter);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_list) {
            // Handle the camera action
            //Intent intent=new Intent(MainActivity.this,WorkListActivity.class);
            //startActivity(intent);
        } else if (id == R.id.nav_note) {

        } else if (id == R.id.nav_calendar) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // действия кнопки на МэинАктивити тестовая кнопка
    public void OpenActivity(View view) {

        Intent intent=new Intent(MainActivity.this,OpenActivity.class);
        startActivity(intent);
    }
/*прилоЖенька*

SUN☼
    нормальный*/


}
