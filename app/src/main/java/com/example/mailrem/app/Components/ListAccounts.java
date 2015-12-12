package com.example.mailrem.app.components;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.example.mailrem.app.R;
import com.example.mailrem.app.components.activity.LoginActivity;
import com.example.mailrem.app.pojo.Account;

public class ListAccounts extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String COLUMN_LOGIN = "login";
    private static final int REQUEST_CODE = 1;
    private static final String ACCOUNT = "Account";
    private static final String LOGIN = "Login";


    private SimpleCursorAdapter adapter;
    private AccountsDataBase dataBase;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dataBase = new AccountsDataBase(getActivity());
        dataBase.open();

        String[] from = new String[]{COLUMN_LOGIN};
        int[] to = new int[]{R.id.login};

        setEmptyText(getResources().getString(R.string.empty_account_list));

        adapter = new SimpleCursorAdapter(getActivity(), R.layout.accouunts_list_view,
                null, from, to, 0);
        setListAdapter(adapter);

        registerForContextMenu(getListView());
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataBase.close();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.account_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapterInfo;

        switch (item.getItemId()) {
            case R.id.change:
                adapterInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                String login = dataBase.getLoginById(adapterInfo.id);

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra(LOGIN, login);

                startActivityForResult(intent, REQUEST_CODE);
                return true;

            case R.id.delete:
                adapterInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                dataBase.deleteAccount(adapterInfo.id);

                refresh();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Account account = data.getParcelableExtra(ACCOUNT);
                AccountsDataBase db = new AccountsDataBase(getActivity());
                db.updateAccountByLogin(account);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AccountsCursorLoader(getActivity(), dataBase);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private void refresh() {
        getLoaderManager().getLoader(0).forceLoad();
    }
}
