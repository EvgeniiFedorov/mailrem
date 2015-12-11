package com.example.mailrem.app.components;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.mailrem.app.R;
import com.example.mailrem.app.pojo.Account;

import java.util.ArrayList;
import java.util.List;

public class ListAccounts extends ListFragment {

    private ArrayAdapter<Account> arrayAdapter;
    private AccountsDataBase db;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(getResources().getString(R.string.empty_account_list));

        db = new AccountsDataBase(getActivity());

        arrayAdapter = new ArrayAdapter<Account>(getActivity(),
                android.R.layout.simple_list_item_1);
        setListAdapter(arrayAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        arrayAdapter.clear();
        List<Account> accounts = db.getAllAccounts();
        arrayAdapter.addAll(accounts);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
    }
}
