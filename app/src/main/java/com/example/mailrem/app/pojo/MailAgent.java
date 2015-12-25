package com.example.mailrem.app.pojo;

import android.os.StrictMode;
import android.util.Log;
import com.example.mailrem.app.Constants;
import com.sun.mail.imap.IMAPStore;

import javax.mail.*;
import javax.mail.search.*;

import java.io.IOException;
import java.util.*;

public class MailAgent {

    private static final String MAIL_STORE_PROTOCOL = "imaps";

    private IMAPStore store;

    public MailAgent() {
        Log.d(Constants.LOG_TAG, "MailAgent constructor");

        setThreadPolicy();
    }

    public void connect(Account account)
            throws MessagingException, IOException {

        Log.d(Constants.LOG_TAG, "MailAgent connect ...");

        if (store != null) {
            throw new MessagingException("Previous connection is not closed");
        }

        Properties properties = System.getProperties();
        properties.put("mail.store.protocol", MAIL_STORE_PROTOCOL);

        Session session = Session.getDefaultInstance(properties);
        store = (IMAPStore) session.getStore();

        store.connect(account.getHost(), account.getPort(),
                account.getLogin(), account.getPassword());

        Log.d(Constants.LOG_TAG, "MailAgent connect OK");
    }

    public void disconnect() throws MessagingException {
        Log.d(Constants.LOG_TAG, "MailAgent disconnect");

        if (store == null) {
            throw new MessagingException("Connection is not established");
        }

        store.close();
        store = null;
    }

    private Folder[] getFolders() throws MessagingException {
        Log.d(Constants.LOG_TAG, "MailAgent getFolders");

        return store.getDefaultFolder().list();
    }


    public List<MessageWrap> getUnreadMessagesFromAllFoldersSinceDate(Date date)
            throws MessagingException {

        Log.d(Constants.LOG_TAG, "MailAgent getMessagesFromAllFoldersSinceDate");

        List<MessageWrap> allMessages = new LinkedList<MessageWrap>();
        Folder[] folders = getFolders();

        for (Folder folder : folders) {
            MessageWrap[] messages = getUnreadMessagesByDate(date, folder);

            Collections.addAll(allMessages, messages);
        }

        return allMessages;
    }

    private MessageWrap[] getUnreadMessagesByDate(Date start, Folder folder)
            throws MessagingException {

        Log.d(Constants.LOG_TAG, "MailAgent getMessagesByDate");

        try {
            openFolder(folder);
        } catch (MessagingException e) {
            Log.e(Constants.LOG_TAG, "MailAgent getMessagesByDate: exception - "
                    + e.getMessage());
            return new MessageWrap[0];
        }

        SearchTerm newerThen = new ReceivedDateTerm(ComparisonTerm.GT, start);

        Flags flags = new Flags(Flags.Flag.ANSWERED);
        SearchTerm unansweredTerm = new FlagTerm(flags, false);
        SearchTerm resultTerm = new AndTerm(newerThen, unansweredTerm);

        Message[] messages = folder.search(resultTerm);

        MessageWrap[] messageWraps = MessageExtract.messageWrapping(messages);

        closeFolder(folder);
        return messageWraps;
    }

    private void openFolder(Folder folder) throws MessagingException {
        Log.d(Constants.LOG_TAG, "MailAgent openFolder " + folder.getName());

        folder.open(Folder.READ_ONLY);
    }

    private void closeFolder(Folder folder) throws MessagingException {
        Log.d(Constants.LOG_TAG, "MailAgent closeFolder " + folder.getName());

        folder.close(false);
    }

    private void setThreadPolicy() {
        Log.d(Constants.LOG_TAG, "MailAgent setThreadPolicy");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);
    }
}
