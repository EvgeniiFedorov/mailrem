package com.example.mailrem;

import android.os.StrictMode;
import android.util.Log;

import javax.mail.*;
import javax.mail.search.*;

import java.util.Date;
import java.util.Properties;

public class MailAgent {

    private final static String LOG_TAG = "log_debug";
    private final static String MAIL_STORE_PROTOCOL = "imaps";
    private Store store;

    public MailAgent() {
        Log.d(LOG_TAG, "mail constructor");
        setThreadPolicy();
    }

    public void connect(String mailHost, int serverPort, String userMail,
                        String userPassword) throws MessagingException {
        Log.d(LOG_TAG, "mail connect");
        Properties properties = System.getProperties();
        properties.put("mail.store.protocol", MAIL_STORE_PROTOCOL);

        Session session = Session.getInstance(properties);
        store = session.getStore();

        store.connect(mailHost, serverPort, userMail, userPassword);
    }

    public void disconnect() throws MessagingException{
        Log.d(LOG_TAG, "mail disconnect");
        store.close();
    }

    public Message [] getMessagesInPeriod(String name, Date start, Date end) throws MessagingException {
        Log.d(LOG_TAG, "mail getMessagesInPeriod");
        Folder folder = openFolder(name);

        SearchTerm laterThen = new ReceivedDateTerm(ComparisonTerm.GT, start);
        SearchTerm earlierThen = new ReceivedDateTerm(ComparisonTerm.LT, end);
        SearchTerm inPeriod = new AndTerm(laterThen, earlierThen);

        Message [] messagesInPeriod =  folder.search(inPeriod);

        closeFolder(folder);
        return messagesInPeriod;
    }

    public Message [] getUnreadMessages(String name) throws MessagingException {
        Log.d(LOG_TAG, "mail getUnreadMessages");
        Folder folder = openFolder(name);

        SearchTerm searchTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
        Message [] unreadMessages = folder.search(searchTerm);

        closeFolder(folder);
        return unreadMessages;
    }

    private Folder openFolder(String name) throws MessagingException {
        Log.d(LOG_TAG, "mail openFolder" + name);
        Folder folder = store.getFolder(name);
        folder.open(Folder.READ_ONLY);
        return folder;
    }

    private void closeFolder(Folder folder) throws MessagingException {
        Log.d(LOG_TAG, "mail closeFolder");
        folder.close(false);
    }

    private void setThreadPolicy() {
        Log.d(LOG_TAG, "mail STP");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);
    }
}
