package com.example.mailrem.app;

import com.example.mailrem.app.pojo.MailAgent;
import com.example.mailrem.app.pojo.MessageWrap;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;

public class MailAgentTest {

    private final static String MAIL_HOST = "imap.mail.ru";
    private final static int SERVER_PORT = 993;
    private final static String USER_EMAIL = "ttestname1@mail.ru";
    private final static String USER_PASSWORD = "testpassword";

    public MailAgentTest() {
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testGetMessagesInPeriod() throws Exception {
        MailAgent mailAgent = new MailAgent();
        mailAgent.connect(MAIL_HOST, SERVER_PORT, USER_EMAIL, USER_PASSWORD);

        Date startDate = new Date(0);
        Date endDate = new Date();

        MessageWrap[] messages = mailAgent.getMessagesInPeriod(startDate, endDate, "test1");
        mailAgent.disconnect();

        assertEquals(messages.length, 4);
    }

    @Test
    public void testGetUnreadMessages() throws Exception {
        MailAgent mailAgent = new MailAgent();
        mailAgent.connect(MAIL_HOST, SERVER_PORT, USER_EMAIL, USER_PASSWORD);

        MessageWrap[] messages = mailAgent.getUnreadMessages("test1");
        mailAgent.disconnect();

        assertEquals(messages.length, 2);
    }
}