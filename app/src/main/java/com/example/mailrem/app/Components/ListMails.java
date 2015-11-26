package com.example.mailrem.app.components;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.mailrem.app.pojo.MessageWrap;
import com.example.mailrem.app.R;

import java.util.List;

public class ListMails extends ListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(getResources().getString(R.string.empty_mail_list));

        DataBase dataBase = new DataBase(getActivity());
        List<MessageWrap> messages = dataBase.getAllMessages();

        MessageArrayAdapter adapter = new MessageArrayAdapter(getActivity(), messages);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }
}
