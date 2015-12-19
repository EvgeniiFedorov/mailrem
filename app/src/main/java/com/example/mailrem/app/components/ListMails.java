package com.example.mailrem.app.components;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import android.widget.SimpleCursorAdapter;
import com.example.mailrem.app.Constants;
import com.example.mailrem.app.R;

public class ListMails extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapter;
    private MessagesDataBase dataBase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(Constants.LOG_TAG, "ListMails onCreateView");

        dataBase = MessagesDataBase.getInstance(getActivity());
        dataBase.open();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(Constants.LOG_TAG, "ListMails onActivityCreated");

        String[] from = new String[]
                {MessagesDataBase.COLUMN_FROM, MessagesDataBase.COLUMN_SUBJECT};
        int[] to = new int[]{R.id.from, R.id.subject};

        setEmptyText(getResources().getString(R.string.empty_mail_list));

        adapter = new SimpleCursorAdapter(getActivity(), R.layout.messages_list_view,
                null, from, to, 0);
        setListAdapter(adapter);

        registerForContextMenu(getListView());
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(Constants.LOG_TAG, "ListMails onResume");

        refresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(Constants.LOG_TAG, "ListMails onDestroyView");

        dataBase.close();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        Log.d(Constants.LOG_TAG, "ListMails onListItemClick");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(Constants.LOG_TAG, "ListMails onCreateLoader");

        return new MessagesCursorLoader(getActivity(), dataBase);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(Constants.LOG_TAG, "ListMails onLoadFinished");

        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(Constants.LOG_TAG, "ListMails onLoaderReset");

        adapter.swapCursor(null);
    }

    private void refresh() {
        Log.d(Constants.LOG_TAG, "ListMails refresh");

        getLoaderManager().getLoader(0).forceLoad();
    }
}
