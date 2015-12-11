package com.example.mailrem.app.components;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.mailrem.app.pojo.MessageWrap;
import com.example.mailrem.app.R;

import java.util.List;

public class ListMails extends ListFragment {

    private MessageArrayAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(getResources().getString(R.string.empty_mail_list));

        MessagesDataBase dataBase = new MessagesDataBase(getActivity());

        List<MessageWrap> messages = dataBase.getAllMessages();

        adapter = new MessageArrayAdapter(getActivity(), messages);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
    }
}
