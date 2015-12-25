package com.example.mailrem.app.components.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.mailrem.app.Constants;
import com.example.mailrem.app.R;
import com.example.mailrem.app.components.ListSpecialWords;
import com.example.mailrem.app.components.SpecialWordsDataBase;

public class SpecialWordListActivity extends Activity {

    private EditText newWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.LOG_TAG, "SpecialWordListActivity onCreate");

        setContentView(R.layout.activity_special_words_screen);

        newWord = (EditText) findViewById(R.id.add_word);
    }

    public void onClickAddWord(View view) {
        Log.d(Constants.LOG_TAG, "SpecialWordListActivity onClickAddWord");

        String word = newWord.getText().toString();

        if (word.isEmpty()) {
            newWord.setError(getString(R.string.word_empty));
        } else {
            SpecialWordsDataBase dataBase = SpecialWordsDataBase.getInstance(this);
            dataBase.open();

            if (dataBase.addIfNotExistWord(word)) {
                Toast.makeText(this, R.string.word_add_ok, Toast.LENGTH_SHORT)
                        .show();

                newWord.setText("");

                ListSpecialWords fragment = (ListSpecialWords) getFragmentManager()
                        .findFragmentById(R.id.list_special_words);
                fragment.refresh();
            } else {
                Toast.makeText(this, R.string.word_already_exists, Toast.LENGTH_LONG)
                        .show();
            }

            dataBase.close();
        }
    }
}
