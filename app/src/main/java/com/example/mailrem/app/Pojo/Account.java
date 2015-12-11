package com.example.mailrem.app.pojo;

public class Account {
    private int id;
    private String login;
    private String password;
    private String host;
    private int port;

    public Account(String login, String password, String host, int port) {
        this.login = login;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String toString() {
        return login;
    }
}
