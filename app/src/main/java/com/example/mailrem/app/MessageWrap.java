package com.example.mailrem.app;

public class MessageWrap {

    private String from;
    private String subject;

    public MessageWrap() {}

    public MessageWrap(String from, String subject) {
        this.from = from;
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
