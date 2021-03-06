package com.example.alexadra.cianote;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorTreeAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Calendar;


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
        CheckBox checkBox=(CheckBox) view.findViewById(R.id.checkSubtask);
        boolean check=cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_CHECKED))==0;
        checkBox.setChecked(!check);
        if (checkBox.isChecked()){
            tvChild.setPaintFlags(tvChild.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            tvChild.setPaintFlags(tvChild.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        }

    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        TextView tvGrp = (TextView) view.findViewById(R.id.textGroup);
        RatingBar bar = (RatingBar) view.findViewById(R.id.ratingBar);
        TextView createDate = (TextView) view.findViewById(R.id.textView);
        /** установка даты создания */
        Calendar calendar = Calendar.getInstance();
        int m = 0;
        calendar.setTimeInMillis(cursor.getLong(2)*1000);
        m = calendar.get(Calendar.MONTH)+1;
        tvGrp.setText(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_NAME)));
        createDate.setText(calendar.get(Calendar.DAY_OF_MONTH) + "." + m + "." + calendar.get(Calendar.YEAR));
        bar.setRating(cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_PRIORITY)));
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        int groupId = groupCursor.getInt(groupCursor.getColumnIndex(DBHelper.KEY_ID));
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        return db.query(DBHelper.TABLE_SUBTASK,null,DBHelper.KEY_TASK+"="+groupId,
                null,null,null,null);  /*** ??????????????????????????? ***/
    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        View mView = mInflator.inflate(R.layout.child_view, null);
        TextView tvChild = (TextView) mView.findViewById(R.id.textChild);
        CheckBox checkBox = (CheckBox) mView.findViewById(R.id.checkSubtask);
//        String str=cursor.getString(/*cursor.getColumnIndex(DBHelper.KEY_STEXT))*/1);
//        tvChild.setText(str);
        return mView;
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
        View mView = mInflator.inflate(R.layout.group_view, null);
        TextView tvGrp = (TextView) mView.findViewById(R.id.textGroup);
        tvGrp.setText(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_NAME)));
        return mView;
    }

//    public int getId();

}
