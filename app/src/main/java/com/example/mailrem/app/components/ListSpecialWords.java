package com.example.mailrem.app.components;

import android.app.ListFragment;
import android.app.LoaderManager;
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

public class ListSpecialWords extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapter;
    private SpecialWordsDataBase dataBase;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(Constants.LOG_TAG, "ListSpecialWords onActivityCreated");

        String[] from = new String[]{SpecialWordsDataBase.COLUMN_WORD};
        int[] to = new int[]{R.id.word};

        setEmptyText(getResources().getString(R.string.empty_word_list));

        dataBase = SpecialWordsDataBase.getInstance(getActivity());
        dataBase.open();

        adapter = new SimpleCursorAdapter(getActivity(), R.layout.words_list_view,
                null, from, to, 0);
        setListAdapter(adapter);

        registerForContextMenu(getListView());
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(Constants.LOG_TAG, "ListSpecialWords onResume");

        refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroyView();
        Log.d(Constants.LOG_TAG, "ListSpecialWords onDestroy");

        dataBase.close();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        Log.d(Constants.LOG_TAG, "ListSpecialWords  onListItemClick");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Log.d(Constants.LOG_TAG, "ListSpecialWords onCreateContextMenu");

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.word_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d(Constants.LOG_TAG, "ListSpecialWords onContextItemSelected");

        AdapterView.AdapterContextMenuInfo adapterInfo;

        switch (item.getItemId()) {
            case R.id.delete_word:
                adapterInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                dataBase.deleteWord(adapterInfo.id);

                refresh();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(Constants.LOG_TAG, "ListSpecialWords onCreateLoader");

        return new MyCursorLoader<SpecialWordsDataBase>(getActivity(), dataBase);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(Constants.LOG_TAG, "ListSpecialWords onLoadFinished");

        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(Constants.LOG_TAG, "ListSpecialWords onLoaderReset");

        adapter.swapCursor(null);
    }

    public void refresh() {
        Log.d(Constants.LOG_TAG, "ListSpecialWords refresh");

        getLoaderManager().getLoader(0).forceLoad();
    }
}
