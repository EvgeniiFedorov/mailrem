package com.example.mailrem.app.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Account implements Parcelable {
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

    public Account(Parcel parcel) {
        login = parcel.readString();
        password = parcel.readString();
        host = parcel.readString();
        port = parcel.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(login);
        dest.writeString(password);
        dest.writeString(host);
        dest.writeInt(port);
    }

    public static final Parcelable.Creator<Account> CREATOR
            = new Parcelable.Creator<Account>() {

        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
}
