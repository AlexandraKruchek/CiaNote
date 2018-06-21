package com.example.alexadra.cianote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

public class SubtaskAdapter extends ArrayAdapter<OpenActivity.Subtask> {
    LayoutInflater mInflater;

    public SubtaskAdapter(@NonNull Context context, int resource, @NonNull List<OpenActivity.Subtask> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        OpenActivity.Subtask subtask= (OpenActivity.Subtask) getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.subtask_view, parent, false);
        }
        TextView subtaskName=convertView.findViewById(R.id.subtaskName);
        subtaskName.setText(subtask.name);
        return convertView;
    }
}
