package com.example.mailrem;

import android.test.suitebuilder.annotation.SmallTest;
import junit.framework.TestCase;
import org.junit.Assert;

import javax.mail.Message;
import java.util.Date;

public class MailAgentTest extends TestCase {

    private final static String MAIL_HOST = "imap.mail.ru";
    private final static int SERVER_PORT = 993;
    private final static String USER_EMAIL = "ttestname1@mail.ru";
    private final static String USER_PASSWORD = "testpassword";

    public MailAgentTest() {
        //super(MainActivity.class);
    }

    @SmallTest
    public void testGetMessagesInPeriod() throws Exception {
        MailAgent mailAgent = new MailAgent();
        mailAgent.connect(MAIL_HOST, SERVER_PORT, USER_EMAIL, USER_PASSWORD);

        Date startDate = new Date(0);
        Date endDate = new Date();

        Message[] messages = mailAgent.getMessagesInPeriod("test1", startDate, endDate);
        mailAgent.disconnect();

        Assert.assertEquals(messages.length, 4);
    }

    public void testGetUnreadMessages() throws Exception {

    }
}