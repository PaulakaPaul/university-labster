package ro.ianders.universitylabsterremake.datatypes;

import java.time.LocalDate;

/**
 * Created by paul.iusztin on 12.12.2017.
 */

public class Schedule {

    private String date;
    private String startTime;
    private String endTime;
    private int courseStep; // if it repeats from one week to another or only in the even weekens etc...

    public Schedule() {}

    public Schedule(String date, String startTime, String endTime, int courseStep) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.courseStep = courseStep;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getCourseStep() {
        return courseStep;
    }
}
