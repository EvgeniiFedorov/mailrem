package com.example.mailrem.app.pojo;

import android.content.Context;
import android.util.Log;
import com.example.mailrem.app.Constants;
import com.example.mailrem.app.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class LoadSpecialWords {

    private static final String WORD = "word";
    private final Context context;

    public LoadSpecialWords(Context context) {
        Log.d(Constants.LOG_TAG, "LoadSpecialWords constructor");

        this.context = context;
    }

    public List<String> getSpecialWords() {
        Log.d(Constants.LOG_TAG, "LoadSpecialWords getSpecialWords");

        List<String> wordList = new LinkedList<String>();

        XmlPullParser parser = context.getResources().getXml(R.xml.special_words);

        try {
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG
                        && parser.getName().equals(WORD)) {

                    parser.next();
                    String word = parser.getText();

                    wordList.add(word);
                }
                parser.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(Constants.LOG_TAG, "LoadSpecialWords getSpecialWords: " +
                    "xml parser error: " + e.getMessage());
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "LoadSpecialWords getSpecialWords: " +
                    "i/o exception in xml parser: " + e.getMessage());
        }

        return wordList;
    }
}
