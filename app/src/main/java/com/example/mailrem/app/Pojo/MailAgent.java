package com.example.mailrem.app.pojo;

import android.os.StrictMode;
import android.util.Log;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.*;
import javax.mail.search.*;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class MailAgent {

    private final static String LOG_TAG = "mailrem_log";

    private final static String MAIL_STORE_PROTOCOL = "imaps";

    private Store store;

    public MailAgent() {
        Log.d(LOG_TAG, "mail constructor");
        setThreadPolicy();
    }

    public void connect(String mailHost, int serverPort,
                        String userMail, String userPassword)
            throws MessagingException, IOException {

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

        store.close();
        store = null;
    }

    public Folder[] getFolders() throws MessagingException {
        Log.d(LOG_TAG, "mail getFolders");
        return store.getDefaultFolder().list();
    }

    public MessageWrap[] getMessagesSinceUID(long uid, Folder folder)
            throws MessagingException {

        Log.d(LOG_TAG, "mail getMessagesSinceUID from folder");
        openFolder(folder);

        IMAPFolder imapFolder = (IMAPFolder) folder;
        Message[] messages = imapFolder.getMessagesByUID(uid, Long.MAX_VALUE);

        MessageWrap[] messageWraps = MessageAnalyzer.messageWrapping(messages,
                imapFolder);

        closeFolder(folder);
        return messageWraps;
    }

    public MessageWrap[] getMessagesSinceUID(long uid, String nameFolder)
            throws MessagingException {

        Log.d(LOG_TAG, "mail getMessagesSinceUID by name");
        Folder folder = getFolder(nameFolder);

        return getMessagesSinceUID(uid, folder);
    }

    public long[] getUIDForAnsweredMessages(Folder folder)
            throws MessagingException {


        Log.d(LOG_TAG, "mail getUIDForAnsweredMessages from folder");
        openFolder(folder);

        Flags flags = new Flags(Flags.Flag.ANSWERED);
        SearchTerm searchTerm = new FlagTerm(flags, false);

        Message[] messages = folder.search(searchTerm);

        long[] messagesUID = MessageAnalyzer.getMessagesUID(messages,
                (IMAPFolder) folder);

        closeFolder(folder);
        return messagesUID;
    }

    public MessageWrap[] getMessagesInPeriod(Date start, Date end,
                                             Folder folder)
            throws MessagingException {

        Log.d(LOG_TAG, "mail getMessagesInPeriod from folder");
        openFolder(folder);

        SearchTerm laterThen = new ReceivedDateTerm(ComparisonTerm.GT, start);
        SearchTerm earlierThen = new ReceivedDateTerm(ComparisonTerm.LT, end);
        SearchTerm inPeriod = new AndTerm(laterThen, earlierThen);

        Message[] messages = folder.search(inPeriod);
        MessageWrap[] messageWraps = MessageAnalyzer.messageWrapping(messages,
                (IMAPFolder) folder);

        closeFolder(folder);
        return messageWraps;
    }

    public MessageWrap[] getMessagesInPeriod(Date start, Date end,
                                             String nameFolder)
            throws MessagingException {

        Log.d(LOG_TAG, "mail getMessagesInPeriod by name");
        Folder folder = getFolder(nameFolder);

        return getMessagesInPeriod(start, end, folder);
    }


    public MessageWrap[] getNotAnsweredMessagesInPeriod(Date start, Date end,
                                                        Folder folder)
            throws MessagingException {

        Log.d(LOG_TAG, "mail getNotAnsweredMessagesInPeriod from folder");
        openFolder(folder);

        SearchTerm laterThen = new ReceivedDateTerm(ComparisonTerm.GT, start);
        SearchTerm earlierThen = new ReceivedDateTerm(ComparisonTerm.LT, end);
        SearchTerm inPeriod = new AndTerm(laterThen, earlierThen);

        Flags flags = new Flags(Flags.Flag.ANSWERED);
        SearchTerm answeredTerm = new FlagTerm(flags, false);
        SearchTerm resultTerm = new AndTerm(inPeriod, answeredTerm);

        Message[] messages = folder.search(resultTerm);
        MessageWrap[] messageWraps = MessageAnalyzer.messageWrapping(messages,
                (IMAPFolder) folder);

        closeFolder(folder);
        return messageWraps;
    }

    public MessageWrap[] getNotAnsweredMessagesInPeriod(Date start, Date end,
                                                        String nameFolder)
            throws MessagingException {

        Log.d(LOG_TAG, "mail getNotAnsweredMessagesInPeriod by name");
        Folder folder = getFolder(nameFolder);

        return getNotAnsweredMessagesInPeriod(start, end, folder);
    }

    public MessageWrap[] getUnreadMessages(Folder folder)
            throws MessagingException {

        Log.d(LOG_TAG, "mail getUnreadMessages from folder");
        openFolder(folder);

        Flags flags = new Flags(Flags.Flag.SEEN);
        SearchTerm searchTerm = new FlagTerm(flags, false);

        Message[] messages = folder.search(searchTerm);
        MessageWrap[] messageWraps = MessageAnalyzer.messageWrapping(messages,
                (IMAPFolder) folder);

        closeFolder(folder);
        return messageWraps;
    }

    public MessageWrap[] getUnreadMessages(String nameFolder)
            throws MessagingException {

        Log.d(LOG_TAG, "mail getUnreadMessages by name");
        Folder folder = getFolder(nameFolder);

        return getUnreadMessages(folder);
    }

    private void openFolder(Folder folder) throws MessagingException {
        Log.d(LOG_TAG, "mail openFolder " + folder.getName());

        folder.open(Folder.READ_ONLY);
    }

    private Folder getFolder(String nameFolder) throws MessagingException {
        Log.d(LOG_TAG, "mail getFolder");

        return store.getFolder(nameFolder);
    }

    private void closeFolder(Folder folder) throws MessagingException {
        Log.d(LOG_TAG, "mail closeFolder");

        folder.close(false);
    }

    private void setThreadPolicy() {
        Log.d(LOG_TAG, "mail set STP");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);
    }
}