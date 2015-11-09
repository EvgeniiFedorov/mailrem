package com.example.mailrem.app;

import android.os.StrictMode;
import android.util.Log;

import javax.mail.*;
import javax.mail.search.*;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class MailAgent {

    private final static String LOG_TAG = "log_debug";
    private final static String MAIL_STORE_PROTOCOL = "imaps";

    private Store store;
    private Folder folder;

    public MailAgent() {
        Log.d(LOG_TAG, "mail constructor");
        setThreadPolicy();
    }

    public void connect(String mailHost, int serverPort, String userMail,
                        String userPassword) throws MessagingException, IOException {
        Log.d(LOG_TAG, "mail connect ...");

        if (store != null) {
            throw new MessagingException("Previous connection is not closed");
        }

        Properties properties = System.getProperties();
        properties.put("mail.store.protocol", MAIL_STORE_PROTOCOL);

        Session session = Session.getDefaultInstance(properties);
        store = session.getStore();

        store.connect(mailHost, serverPort, userMail, userPassword);

        Log.d(LOG_TAG, "mail connect OK");
    }

    public void disconnect() throws MessagingException {
        Log.d(LOG_TAG, "mail disconnect");
        if (store == null) {
            throw new MessagingException("Connection is not established");
        }

        if (folder != null) {
            throw new MessagingException("Not all folder are closed");
        }

        store.close();
        store = null;
    }

    public void openFolder(String name) throws MessagingException {
        Log.d(LOG_TAG, "mail openFolder " + name);
        if (folder != null) {
            throw new MessagingException("One folder is now open");
        }

        folder = store.getFolder(name);
        folder.open(Folder.READ_ONLY);
    }

    public void closeFolder() throws MessagingException {
        Log.d(LOG_TAG, "mail closeFolder");
        if (folder == null) {
            throw new MessagingException("Folder already closed");
        }

        folder.close(false);
        folder = null;
    }

    public Folder[] getFolders() throws MessagingException {
        return store.getDefaultFolder().list();
    }

    public Message[] getMessagesInPeriod(Date start, Date end) throws MessagingException {
        Log.d(LOG_TAG, "mail getMessagesInPeriod");

        SearchTerm laterThen = new ReceivedDateTerm(ComparisonTerm.GT, start);
        SearchTerm earlierThen = new ReceivedDateTerm(ComparisonTerm.LT, end);
        SearchTerm inPeriod = new AndTerm(laterThen, earlierThen);

        return folder.search(inPeriod);
    }

    public Message[] getUnreadMessages() throws MessagingException {
        Log.d(LOG_TAG, "mail getUnreadMessages");

        SearchTerm searchTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);

        return folder.search(searchTerm);
    }

    private void setThreadPolicy() {
        Log.d(LOG_TAG, "mail set STP");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);
    }
}