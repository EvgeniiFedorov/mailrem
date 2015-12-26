package com.example.mailrem.app.components.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mailrem.app.Constants;
import com.example.mailrem.app.R;
import com.example.mailrem.app.components.MessagesDataBase;
import com.example.mailrem.app.pojo.MessageWrap;

public class MessageViewActivity extends Activity {

    private static final String MESSAGE_TYPE = "message/rfc822";
    private MessageWrap message;

    private TextView from;
    private TextView to;
    private TextView date;
    private TextView subject;
    private TextView body;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.LOG_TAG, "MessageViewActivity onCreate");

        setContentView(R.layout.activity_message_view);

        from = (TextView) findViewById(R.id.from_field);
        to = (TextView) findViewById(R.id.to_field);
        date = (TextView) findViewById(R.id.date_field);
        subject = (TextView) findViewById(R.id.subject_field);
        body = (TextView) findViewById(R.id.body_field);

        Intent intent = getIntent();

        message = intent.getParcelableExtra(Constants.MESSAGE_INTENT_FIELD);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(Constants.LOG_TAG, "MessageViewActivity onResume");

        from.setText(message.getFromName() + " <" + message.getFromAddress() + ">");
        to.setText(message.getTo());
        date.setText(message.getDate().toString());
        subject.setText(message.getSubject());
        body.setText(message.getBody());
    }

    public void onClickBack(View view) {
        finish();
    }

    public void onClickAnswer(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(MESSAGE_TYPE);

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{message.getFromAddress()});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Re: " + message.getSubject());
        intent.putExtra(Intent.EXTRA_TEXT, "--- Last message ---\n" + message.getBody());

        try {
            startActivity(Intent.createChooser(intent,
                    getString(R.string.choose_email_client)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.not_client, Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void onClickIgnore(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.text_dialog_ignore)
                .setTitle(R.string.title_dialog_ignore)
                .setPositiveButton(R.string.button_dialog_yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MessagesDataBase db = MessagesDataBase
                                        .getInstance(getBaseContext());
                                db.open();
                                db.deleteMessage(message.getId());
                                db.close();

                                dialog.dismiss();
                                finish();
                            }
                        })
                .setNegativeButton(R.string.button_dialog_no,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        builder.create().show();
    }
}
