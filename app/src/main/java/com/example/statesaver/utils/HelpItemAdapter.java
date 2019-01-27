package com.example.statesaver.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.statesaver.MainActivity;
import com.example.statesaver.QaActivity;
import com.example.statesaver.R;
import com.example.statesaver.ViewActivity;
import com.example.statesaver.types.HelpItem;

import java.util.ArrayList;

public class HelpItemAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<HelpItem> list = new ArrayList<HelpItem>();
    private Context context;

    public HelpItemAdapter(ArrayList<HelpItem> list, Context context) {
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
            view = inflater.inflate(R.layout.help_row, null);
        }
        HelpItem hi = list.get(position);

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.help_text);
        listItemText.setText(hi.getQuestion());

        //Handle TextView and display string from your list
        TextView listItemCountText = (TextView)view.findViewById(R.id.count_text);
        listItemCountText.setText(""+hi.getAnswersCount());

        listItemText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, QaActivity.class);
                intent.putExtra(QaActivity.QUESTION, list.get(position).getQuestion());
                context.startActivity(intent);
            }
        });

        return view;
    }
}
