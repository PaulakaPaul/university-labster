package ro.ianders.universitylabsterremake.datatypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul.iusztin on 12.12.2017.
 */

public class Schedule {

    private String date;
    private String startTime;
    private String endTime;
    private int step; // if it repeats from one week to another or only in the even weekens etc...
    private List<String> checkins;

    public Schedule() {}

    public Schedule(String date, String startTime, String endTime, int step, List<String> checkins ) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.step = step;

        if(checkins != null) //in case there are no check-ins in the database
        this.checkins = checkins;
        else
        this.checkins = new ArrayList<>();
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

    public int getStep() {
        return step;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getCheckins() {
        return checkins;
    }

    public void addCheckin(String person) {
        checkins.add(person);
    }

    public void removeCheckin(String person) {
        checkins.remove(person);
    }

    public void setCheckins(List<String> checkins) {
        this.checkins = checkins;
    }

    public String toString() {
        String r = "";
        if(checkins != null)
            for(String s: checkins)
                r += s + " ";
        return r;
    }
}
