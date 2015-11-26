package com.example.mailrem.app.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mailrem.app.pojo.MessageWrap;
import com.example.mailrem.app.R;

import java.util.List;

public class MessageArrayAdapter extends ArrayAdapter<MessageWrap> {
    private final Context context;
    private final List<MessageWrap> values;

    public MessageArrayAdapter(Context context, List<MessageWrap> values) {
        super(context, R.layout.messages_list_view, values);
        this.context = context;
        this.values = values;
    }

    static class ViewHolder {
        public TextView textViewFrom;
        public TextView textViewSubject;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.messages_list_view, null, true);

            holder = new ViewHolder();
            holder.textViewFrom = (TextView) rowView.findViewById(R.id.from);
            holder.textViewSubject = (TextView) rowView.findViewById(R.id.subject);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.textViewFrom.setText(values.get(position).getFrom());
        holder.textViewSubject.setText(values.get(position).getSubject());

        return rowView;
    }
}
