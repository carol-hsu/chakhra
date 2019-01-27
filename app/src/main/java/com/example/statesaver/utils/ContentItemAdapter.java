package com.example.statesaver.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.statesaver.QaActivity;
import com.example.statesaver.R;
import com.example.statesaver.ViewActivity;
import com.example.statesaver.types.ContentData;

import java.util.ArrayList;

public class ContentItemAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<ContentData> list = new ArrayList<ContentData>();
    private Context context;

    public ContentItemAdapter(ArrayList<ContentData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.content_text);
        listItemText.setText(list.get(position).getDesc());

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.content_delete_button);
        Button viewBtn = (Button)view.findViewById(R.id.content_read_button);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //

                // TODO : Delete the content
                list.remove(position); //or some other task
                notifyDataSetChanged();
            }
        });
        viewBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                notifyDataSetChanged();
                // Need to start a new ViewActivity
                Intent intent = new Intent(context, ViewActivity.class);
                intent.putExtra("contentId", list.get(position).getId());
                context.startActivity(intent);
            }
        });

        return view;
    }
}
