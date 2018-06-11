package com.example.alexadra.cianote;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.TextView;


public class ExpAdapter extends CursorTreeAdapter {
    LayoutInflater mInflator;
    DBHelper dbHelper;


    public ExpAdapter(Cursor cursor, Context context) {
        super(cursor, context);
        dbHelper=new DBHelper(context);
        mInflator = LayoutInflater.from(context);
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        TextView tvChild = (TextView) view.findViewById(R.id.textChild);
        tvChild.setText(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STEXT)));
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        TextView tvGrp = (TextView) view.findViewById(R.id.textGroup);
        tvGrp.setText(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_NAME)));
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        int groupId = groupCursor.getInt(groupCursor.getColumnIndex(DBHelper.KEY_ID));
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        return db.query(DBHelper.TABLE_SUBTASK,null,DBHelper.KEY_TASK+"="+groupId,null,null,null,null);  /*** ??????????????????????????? ***/

    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        View mView = mInflator.inflate(R.layout.child_view, null);
        TextView tvChild = (TextView) mView.findViewById(R.id.textChild);
        String str=cursor.getString(/*cursor.getColumnIndex(DBHelper.KEY_STEXT))*/1);
        tvChild.setText(str);

        return mView;
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
        View mView = mInflator.inflate(R.layout.group_view, null);
        TextView tvGrp = (TextView) mView.findViewById(R.id.textGroup);
        tvGrp.setText(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_NAME)));

        return mView;
    }

}
