package ro.ianders.universitylabsterremake.datatypes;

import java.util.Date;

/**
 * Created by paul.iusztin on 12.12.2017.
 */

public class Schedule {

    private Date date;
    private String startTime;
    private String endTime;

    public Schedule() {}

    public Schedule(Date date, String startTime, String endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Date getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
