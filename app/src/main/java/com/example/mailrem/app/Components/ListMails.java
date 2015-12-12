package com.example.mailrem.app.components;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import android.widget.SimpleCursorAdapter;
import com.example.mailrem.app.R;

public class ListMails extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String COLUMN_FROM = "from_field";
    private static final String COLUMN_SUBJECT = "subject_field";

    private SimpleCursorAdapter adapter;
    private MessagesDataBase dataBase;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dataBase = new MessagesDataBase(getActivity());
        dataBase.open();

        String[] from = new String[]{COLUMN_FROM, COLUMN_SUBJECT};
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MessagesCursorLoader(getActivity(), dataBase);
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
