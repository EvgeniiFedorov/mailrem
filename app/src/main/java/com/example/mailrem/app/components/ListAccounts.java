package com.example.mailrem.app.components;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.example.mailrem.app.Constants;
import com.example.mailrem.app.R;
import com.example.mailrem.app.components.activity.LoginActivity;
import com.example.mailrem.app.pojo.Account;
import com.example.mailrem.app.pojo.ProcessesManager;

public class ListAccounts extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REQUEST_CODE = 1;

    private SimpleCursorAdapter adapter;
    private AccountsDataBase dataBase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(Constants.LOG_TAG, "ListAccounts onCreateView");

        dataBase = AccountsDataBase.getInstance(getActivity());
        dataBase.open();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(Constants.LOG_TAG, "ListAccounts onActivityCreated");

        String[] from = new String[]{AccountsDataBase.COLUMN_LOGIN};
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
        Log.d(Constants.LOG_TAG, "ListAccounts onResume");

        refresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(Constants.LOG_TAG, "ListAccounts onDestroyView");

        dataBase.close();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        Log.d(Constants.LOG_TAG, "ListAccounts  onListItemClick");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Log.d(Constants.LOG_TAG, "ListAccounts onCreateContextMenu");

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.account_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d(Constants.LOG_TAG, "ListAccounts onContextItemSelected");

        AdapterView.AdapterContextMenuInfo adapterInfo;

        switch (item.getItemId()) {
            case R.id.change:
                adapterInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                String login = dataBase.getLoginById(adapterInfo.id);

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra(Constants.LOGIN_INTENT_FIELD, login);

                startActivityForResult(intent, REQUEST_CODE);
                return true;

            case R.id.delete:
                adapterInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                dataBase.deleteAccount(adapterInfo.id);

                if (dataBase.countAccount() == 0) {
                    ProcessesManager.stop();
                }

                refresh();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(Constants.LOG_TAG, "ListAccounts onActivityResult");

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Account account = data.getParcelableExtra(Constants.ACCOUNT_INTENT_FIELD);

                dataBase.updateAccountByLogin(account);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(Constants.LOG_TAG, "ListAccounts onCreateLoader");

        return new AccountsCursorLoader(getActivity(), dataBase);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(Constants.LOG_TAG, "ListAccounts onLoadFinished");

        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(Constants.LOG_TAG, "ListAccounts onLoaderReset");

        adapter.swapCursor(null);
    }

    private void refresh() {
        Log.d(Constants.LOG_TAG, "ListAccounts refresh");

        getLoaderManager().getLoader(0).forceLoad();
    }
}
