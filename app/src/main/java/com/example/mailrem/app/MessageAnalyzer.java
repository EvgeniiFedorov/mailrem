package com.example.mailrem.app;

import android.util.Log;

import javax.mail.*;
import java.io.IOException;
import java.util.Date;

public final class MessageAnalyzer {

    private final static String LOG_TAG = "log_debug";
    private final static String ERROR_STRING = "error in read message";

    private MessageAnalyzer() {}

    public static String getSubject(Message message) {
        try {
            return message.getSubject();
        } catch (MessagingException e) {
            Log.e(LOG_TAG, e.getMessage());
            return ERROR_STRING;
        }
    }

    public static String getText(Message message) throws MessagingException, IOException {
        if (message.isMimeType("text/*")) {
            return (String) message.getContent();
        }

        if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            for (int partNumber = 0; partNumber < multipart.getCount(); ++partNumber) {
                BodyPart bodyPart = multipart.getBodyPart(partNumber);
                if (bodyPart.isMimeType("text/*")) {
                    return (String) bodyPart.getContent();
                }
            }
        }
        return "";
    }

    public static Date getDate(Message message) throws MessagingException {
        return message.getSentDate();
    }

    public static String getFrom(Message message) {
        try {
            Address[] addresses = message.getFrom();
            if (addresses == null || addresses.length == 0) {
                Log.i(LOG_TAG, "No field from");
                return ERROR_STRING;
            }
            return addresses[0].toString();
        } catch (MessagingException e) {
            Log.e(LOG_TAG, e.getMessage());
            return ERROR_STRING;
        }
    }

    public static boolean isAnswered(Message message) throws MessagingException{
        return message.isSet(Flags.Flag.ANSWERED);
    }
}
