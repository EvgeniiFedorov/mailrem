package com.example.mailrem.app.pojo;

import android.content.Context;
import android.util.Log;
import com.example.mailrem.app.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class TryExtendDefinitionAccount {

    private final static String LOG_TAG = "mailrem_log";

    private static final String SERVICE = "service";
    private static final String AT = "@";

    public static Account getAccount(Context context, String login, String password) {

        String[] splitLogin = login.split(AT);
        String userService;

        if (splitLogin.length == 2) {
            userService = splitLogin[1];
        } else {
            userService = "";
        }

        XmlPullParser parser = context.getResources().getXml(R.xml.email_services);

        try {
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG
                        && parser.getName().equals(SERVICE)
                        && parser.getAttributeValue(0).equals(userService)) {

                    parser.next();
                    parser.next();
                    String host = parser.getText();
                    parser.next();
                    parser.next();
                    parser.next();
                    String port = parser.getText();

                    return new Account(login, password, host, Integer.parseInt(port));
                }
                parser.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, "xml parser error: " + e.getMessage());
        } catch (IOException e) {
            Log.e(LOG_TAG, "i/o exception in xml parser: " + e.getMessage());
        }

        return null;
    }
}
