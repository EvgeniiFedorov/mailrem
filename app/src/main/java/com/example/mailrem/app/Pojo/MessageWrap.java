package com.example.mailrem.app.pojo;

import java.util.Date;

public class MessageWrap {

    private long uid;
    private String from;
    private String to;
    private Date date;
    private String subject;
    private String body;

    public MessageWrap() {}

    public long getUID() {
        return uid;
    }

    public void setUID(long uid) {
        this.uid = uid;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
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
    public String toString() {
        return subject;
    }
}
