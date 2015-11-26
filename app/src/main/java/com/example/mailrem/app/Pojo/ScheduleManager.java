package com.example.mailrem.app.pojo;

public class ScheduleManager {

    private final static int[] shedule = {60 * 60, 2 * 60 * 60, 3 * 60 * 60};

    public static int sheduleTime(int status) {
        return shedule[status];
    }
}
