package com.example.mailrem.app.pojo;

import android.util.Log;
import com.example.mailrem.app.Constants;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.*;
import javax.mail.internet.InternetAddress;

import java.util.Date;

public final class MessageAnalyzer {

    private final static String ERROR_STRING = "error in read message";
    private final static String TEXT_NOT_FOUND = "text not found";

    public static MessageWrap messageWrapping(Message message, IMAPFolder folder) {
        Log.d(Constants.LOG_TAG, "wrapping message " + String.valueOf(getUID(message, folder)));
        MessageWrap messageWrap = new MessageWrap();

        messageWrap.setUID(getUID(message, folder));
        messageWrap.setFrom(getFrom(message));
        messageWrap.setTo(getTo(message));
        messageWrap.setDate(getDate(message));
        messageWrap.setSubject(getSubject(message));
        messageWrap.setBody(getBody(message));

        return messageWrap;
    }

    public static MessageWrap[] messageWrapping(Message[] messages, IMAPFolder folder) {
        Log.d(Constants.LOG_TAG, "wrapping messages");
        MessageWrap[] messageWraps = new MessageWrap[messages.length];

        for (int index = 0; index < messages.length; ++index) {
            messageWraps[index] = messageWrapping(messages[index], folder);
        }

        return messageWraps;
    }

    public static long[] getMessagesUID(Message[] messages, IMAPFolder folder) {
        Log.d(Constants.LOG_TAG, "wrapping get messages uid");

        long[] messagesUID = new long[messages.length];

        for (int index = 0; index < messages.length; ++index) {
            messagesUID[index] = getUID(messages[index], folder);
        }

        return messagesUID;
    }

    private static long getUID(Message message, IMAPFolder folder) {
        Log.d(Constants.LOG_TAG, "message get uid");
        try {
            return folder.getUID(message);
        } catch(MessagingException e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
            return 0;
        }
    }

    private static String getFrom(Message message) {
        Log.d(Constants.LOG_TAG, "message get from");
        try {
            Address[] addresses = message.getFrom();
            if (addresses == null || addresses.length == 0) {
                Log.i(Constants.LOG_TAG, "No field from");
                return ERROR_STRING;
            }

            InternetAddress internetAddress = (InternetAddress) addresses[0];

            String personal = "undefined";
            if (internetAddress.getPersonal() != null) {
                personal = internetAddress.getPersonal();
            }
            String address = "undefined";
            if (internetAddress.getAddress() != null) {
                address = internetAddress.getAddress();
            }
            return "From: " + personal + "\nAddress: " + address;
        } catch (MessagingException e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
            return ERROR_STRING;
        }
    }

    private static String getTo(Message message) {
        Log.d(Constants.LOG_TAG, "message get to");
        try {
            Address[] addresses = message.getAllRecipients();
            if (addresses == null || addresses.length == 0) {
                Log.i(Constants.LOG_TAG, "No field to");
                return ERROR_STRING;
            }
            return addresses[0].toString();
        } catch (MessagingException e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
            return ERROR_STRING;
        }
    }

    private static Date getDate(Message message) {
        Log.d(Constants.LOG_TAG, "message get date");
        try {
            return message.getReceivedDate();
        } catch (MessagingException e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
            return new Date(0);
        }
    }

    private static String getSubject(Message message) {
        Log.d(Constants.LOG_TAG, "message get subject");
        try {
            return message.getSubject();
        } catch (MessagingException e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
            return ERROR_STRING;
        }
    }

    private static String getBody(Message message) {
        Log.d(Constants.LOG_TAG, "message get body");
        /*try {
            Log.d(Constants.LOG_TAG,"111");
            Object content = message.getContent();
            Log.d(Constants.LOG_TAG,"112");
            if (message.isMimeType("text/*")) {
                Log.d(Constants.LOG_TAG,"12");
                return (String) message.getContent();
            }
            Log.d(Constants.LOG_TAG,"13");

            if (message.isMimeType("multipart/*")) {
                Log.d(Constants.LOG_TAG, "1");
                Multipart multipart = (Multipart) message.getContent();
                Log.d(Constants.LOG_TAG,"2");
                for (int partNumber = 0; partNumber < multipart.getCount(); ++partNumber) {
                    Log.d(Constants.LOG_TAG,"3");
                    BodyPart bodyPart = multipart.getBodyPart(partNumber);
                    Log.d(Constants.LOG_TAG,"4");
                    if (bodyPart.isMimeType("text/plain")) {
                        Log.d(Constants.LOG_TAG,"5");
                        return (String) bodyPart.getContent();
                    }
                    Log.d(Constants.LOG_TAG,"6");
                }
                Log.d(Constants.LOG_TAG,"7");
            }
            Log.d(Constants.LOG_TAG,"8");
            return TEXT_NOT_FOUND;
        } */ /*catch (MessagingException e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
            return ERROR_STRING;
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
            return ERROR_STRING;
        } */ /*catch (Exception e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Log.e(Constants.LOG_TAG, errors.toString());
            return ERROR_STRING;
        }*/
        return "text";
    }

    private static boolean isNeedAnswer(Message message) {
        Log.d(Constants.LOG_TAG, "message isNeedAnswer");
        try {
            return message.isSet(Flags.Flag.ANSWERED);
        } catch (MessagingException e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
            return false;
        }
    }
}
