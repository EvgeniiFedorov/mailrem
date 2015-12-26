package com.example.mailrem.app.pojo;

import android.content.Context;
import android.util.Log;
import com.example.mailrem.app.Constants;
import com.example.mailrem.app.components.SpecialWordsDataBase;

import java.util.LinkedList;
import java.util.List;

public class MessageAnalyzer {

    private static final String DELIMITER = "[ .,:?!]+";

    private final List<String> specialWords;

    public MessageAnalyzer(Context context) {
        Log.d(Constants.LOG_TAG, "MessageAnalyzer constructor");

        SpecialWordsDataBase dataBase = SpecialWordsDataBase.getInstance(context);
        dataBase.open();
        specialWords = dataBase.getAllWords();
        dataBase.close();
    }

    public List<MessageWrap> analyzeMessages(List<MessageWrap> messages) {
        Log.d(Constants.LOG_TAG, "MessageAnalyzer analyzeMessages");

        List<MessageWrap> newMessages = new LinkedList<MessageWrap>();

        for (MessageWrap message : messages) {
            if (checkMessage(message)) {
                newMessages.add(message);
            }
        }

        return newMessages;
    }

    private boolean checkMessage(MessageWrap message) {
        Log.d(Constants.LOG_TAG, "MessageAnalyzer checkMessage");

        String[] wordInMessage = message.getBody().split(DELIMITER);
        for (String word : wordInMessage) {
            if (specialWords.contains(word.toLowerCase())) {
                return true;
            }
        }

        wordInMessage = message.getSubject().split(DELIMITER);
        for (String word : wordInMessage) {
            if (specialWords.contains(word.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
