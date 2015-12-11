package com.example.mailrem.app.pojo;

public class ScheduleManager {

    private final static int[] FREQUENCY = {60, 2 * 60, 3 * 60};
    private final static int[] DURATION = {60 * 60, 2 * 60 * 60, 3 * 60 * 60};

    public static int frequencyStage(int status) {
        return FREQUENCY[status];
    }

    public static int durationStage(int status) {
        return DURATION[status];
    }
}
