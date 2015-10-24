package com.example.mailrem;

import android.util.Log;

import javax.mail.*;
import java.io.IOException;
import java.util.Date;

public class MessageAnalyzer {

    private final static String LOG_TAG = "log_debug";
    private Message message;

    public MessageAnalyzer(Message message) throws NullPointerException {
        reset(message);
    }

    public void reset(Message message) throws NullPointerException {
        if (message == null) {
            throw new NullPointerException("message is null");
        }
        this.message = message;
    }

    public String getSubject() {
        try {
            return message.getSubject();
        } catch (MessagingException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return "";
    }

    public String getText() throws MessagingException, IOException {
        if (message.isMimeType("text/*")) {
            return (String)message.getContent();
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

    public Date getDate() throws MessagingException {
        return message.getSentDate();
    }

    public String getFrom() throws MessagingException {
        Address[] addresses = message.getFrom();
        if (addresses == null || addresses.length != 0) {
            return null;
        }
        return message.getFrom()[0].toString();
    }

    public boolean isAnswered()throws MessagingException{
        return message.isSet(Flags.Flag.ANSWERED);
    }
}
