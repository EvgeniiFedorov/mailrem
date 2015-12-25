package com.example.mailrem.app.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.example.mailrem.app.Constants;

import java.util.Date;

public class MessageWrap implements Parcelable {

    private String fromName;
    private String fromAddress;
    private String to;
    private Date date;
    private String subject;
    private String body;

    public MessageWrap() {
        Log.d(Constants.LOG_TAG, "MessageWrap constructor");
    }

    public MessageWrap(Parcel parcel) {
        Log.d(Constants.LOG_TAG, "MessageWrap constructor");

        fromName = parcel.readString();
        fromAddress = parcel.readString();
        to = parcel.readString();
        date = new Date(parcel.readLong());
        subject = parcel.readString();
        body = parcel.readString();
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public int describeContents() {
        Log.d(Constants.LOG_TAG, "MessageWrap describeContents");

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d(Constants.LOG_TAG, "MessageWrap writeToParcel");

        dest.writeString(fromName);
        dest.writeString(fromAddress);
        dest.writeString(to);
        dest.writeLong(date.getTime());
        dest.writeString(subject);
        dest.writeString(body);
    }

    public static final Parcelable.Creator<MessageWrap> CREATOR
            = new Parcelable.Creator<MessageWrap>() {

        @Override
        public MessageWrap createFromParcel(Parcel source) {
            Log.d(Constants.LOG_TAG, "Parcelable.Creator createFromParcel");

            return new MessageWrap(source);
        }

        @Override
        public MessageWrap[] newArray(int size) {
            Log.d(Constants.LOG_TAG, "Parcelable.Creator newArray");

            return new MessageWrap[size];
        }
    };
}
