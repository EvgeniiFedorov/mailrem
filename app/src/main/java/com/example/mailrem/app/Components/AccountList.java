package com.example.mailrem.app.components;

import android.accounts.*;
import android.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.mailrem.app.R;

public class AccountList extends ListFragment implements OnAccountsUpdateListener {

    private final static String LOG_TAG = "mailrem_log";

    private final Handler handler = new Handler();

    private AccountManager accountManager;
    private ArrayAdapter<Account> arrayAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(getResources().getString(R.string.empty_account_list));

        accountManager = AccountManager.get(getActivity());
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1);
        setListAdapter(arrayAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        accountManager.addOnAccountsUpdatedListener(this, handler, true);
    }

    @Override
    public void onPause() {
        accountManager.removeOnAccountsUpdatedListener(this);
        super.onPause();
    }

    @Override
    public void onAccountsUpdated(Account[] accounts) {
        arrayAdapter.setNotifyOnChange(false);
        arrayAdapter.clear();
        for (Account account : accounts) {
            if (TextUtils.equals(account.type, MailAccount.TYPE)) {
                arrayAdapter.add(account);
            }
        }
        arrayAdapter.setNotifyOnChange(true);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        Account account = arrayAdapter.getItem(position);
        /*accountManager.getAuthToken(account, MailAccount.TOKEN_FULL_ACCESS, new Bundle(), true,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle result = future.getResult();
                            Log.d(LOG_TAG, result.getString(AccountManager.KEY_AUTHTOKEN));
                        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                        }
                    }
                }, null
        );*/
    }
}
