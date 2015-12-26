package com.example.mailrem.app.pojo;

import android.util.Log;
import com.example.mailrem.app.Constants;

import javax.mail.*;
import javax.mail.internet.InternetAddress;

import java.io.IOException;
import java.util.Date;

public final class MessageExtract {

    private final static String ERROR_STRING = "error in read message";
    private final static String TEXT_NOT_FOUND = "Text not found";
    private final static String SUBJECT_NOT_FOUND = "Subject not found";
    private final static String UNDEFINED = "undefined";

    public static MessageWrap messageWrapping(Message message) {
        Log.d(Constants.LOG_TAG, "MessageExtract messageWrapping");

        MessageWrap messageWrap = new MessageWrap();

        messageWrap.setFromName(getFromName(message));
        messageWrap.setFromAddress(getFromAddress(message));
        messageWrap.setTo(getTo(message));
        messageWrap.setDate(getDate(message));
        messageWrap.setSubject(getSubject(message));
        messageWrap.setBody(getBody(message));

        return messageWrap;
    }

    public static MessageWrap[] messageWrapping(Message[] messages) {
        Log.d(Constants.LOG_TAG, "MessageExtract messageWrapping");

        MessageWrap[] messageWraps = new MessageWrap[messages.length];

        for (int index = 0; index < messages.length; ++index) {
            messageWraps[index] = messageWrapping(messages[index]);
        }

        return messageWraps;
    }

    private static String getFromName(Message message) {
        Log.d(Constants.LOG_TAG, "MessageExtract getFromName");

        try {
            Address[] addresses = message.getFrom();
            if (addresses == null || addresses.length == 0) {
                Log.i(Constants.LOG_TAG, "No field from");
                return ERROR_STRING;
            }

            InternetAddress internetAddress = (InternetAddress) addresses[0];

            if (internetAddress.getPersonal() != null) {
                return internetAddress.getPersonal();
            } else {
                return UNDEFINED;
            }
        } catch (MessagingException e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
            return ERROR_STRING;
        }
    }

    private static String getFromAddress(Message message) {
        Log.d(Constants.LOG_TAG, "MessageExtract getFromName");

        try {
            Address[] addresses = message.getFrom();
            if (addresses == null || addresses.length == 0) {
                Log.i(Constants.LOG_TAG, "No field from");
                return ERROR_STRING;
            }

            InternetAddress internetAddress = (InternetAddress) addresses[0];

            if (internetAddress.getAddress() != null) {
                return internetAddress.getAddress();
            } else {
                return UNDEFINED;
            }
        } catch (MessagingException e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
            return ERROR_STRING;
        }
    }

    private static String getTo(Message message) {
        Log.d(Constants.LOG_TAG, "MessageExtract getTo");

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
        Log.d(Constants.LOG_TAG, "MessageExtract getDate");

        try {
            return message.getReceivedDate();
        } catch (MessagingException e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
            return new Date(0);
        }
    }

    private static String getSubject(Message message) {
        Log.d(Constants.LOG_TAG, "MessageExtract getSubject");

        try {
            String subject = message.getSubject();

            if (subject != null) {
                return subject;
            } else {
                return SUBJECT_NOT_FOUND;
            }
        } catch (MessagingException e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
            return ERROR_STRING;
        }
    }

    private static String getBody(Message message) {
        Log.d(Constants.LOG_TAG, "MessageExtract getBody");
        try {
            if (message.isMimeType("text/*")) {
                Object text = message.getContent();
                if (text != null) {
                    return (String) text;
                } else {
                    return TEXT_NOT_FOUND;
                }
            }

            if (message.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) message.getContent();
                String text = "";

                for (int partNumber = 0; partNumber < multipart.getCount(); ++partNumber) {
                    BodyPart bodyPart = multipart.getBodyPart(partNumber);

                    if (bodyPart.isMimeType("text/*")) {
                        text += (String) bodyPart.getContent();
                    }
                }

                if (!text.equals("")) {
                    return text;
                } else {
                    return TEXT_NOT_FOUND;
                }
            }
            return TEXT_NOT_FOUND;
        } catch (MessagingException e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
            return ERROR_STRING;
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
            return ERROR_STRING;
        }
    }
}
