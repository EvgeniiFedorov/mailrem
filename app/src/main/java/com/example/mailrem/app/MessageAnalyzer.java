package com.example.mailrem.app;

import android.util.Log;

import javax.mail.*;
import java.io.IOException;
import java.util.Date;

public final class MessageAnalyzer {

    private final static String LOG_TAG = "log_debug";
    private final static String ERROR_STRING = "error in read message";
    private final static String TEXT_NOT_FOUND = "text not found";

    public static MessageWrap messageWrapping(Message message) {
        Log.d(LOG_TAG, "wrapping message");
        MessageWrap messageWrap = new MessageWrap();

        messageWrap.setFrom(getFrom(message));
        messageWrap.setTo(getTo(message));
        messageWrap.setDate(getDate(message));
        messageWrap.setSubject(getSubject(message));
        messageWrap.setBody(getBody(message));

        return messageWrap;
    }

    public static MessageWrap[] messageWrapping(Message[] messages) {
        Log.d(LOG_TAG, "wrapping messages");
        MessageWrap[] messageWraps = new MessageWrap[messages.length];

        for (int index = 0; index < messages.length; ++index) {
            messageWraps[index] = messageWrapping(messages[index]);
        }

        return messageWraps;
    }

    private static String getFrom(Message message) {
        Log.d(LOG_TAG, "message get from");
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

    private static String getTo(Message message) {
        Log.d(LOG_TAG, "message get to");
        try {
            Address[] addresses = message.getAllRecipients();
            if (addresses == null || addresses.length == 0) {
                Log.i(LOG_TAG, "No field to");
                return ERROR_STRING;
            }
            return addresses[0].toString();
        } catch (MessagingException e) {
            Log.e(LOG_TAG, e.getMessage());
            return ERROR_STRING;
        }
    }

    private static Date getDate(Message message) {
        Log.d(LOG_TAG, "message get date");
        try {
            return message.getReceivedDate();
        } catch (MessagingException e) {
            Log.e(LOG_TAG, e.getMessage());
            return new Date(0);
        }
    }

    private static String getSubject(Message message) {
        Log.d(LOG_TAG, "message get subject");
        try {
            return message.getSubject();
        } catch (MessagingException e) {
            Log.e(LOG_TAG, e.getMessage());
            return ERROR_STRING;
        }
    }

    private static String getBody(Message message) {
        /*Log.d(LOG_TAG, "message get body");
        try {
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
            return TEXT_NOT_FOUND;
        } catch (MessagingException e) {
            Log.e(LOG_TAG, e.getMessage());
            return ERROR_STRING;
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
            return ERROR_STRING;
        }*/
        return "text";
    }

    private static boolean isAnswered(Message message) throws MessagingException{
        return message.isSet(Flags.Flag.ANSWERED);
    }
}
