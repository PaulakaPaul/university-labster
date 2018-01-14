package ro.ianders.universitylabsterremake.datatypes;

import android.util.Log;

/**
 * Created by paul.iusztin on 09.01.2018.
 */

public class TimetableData {
    private String name;
    private String type;
    private String periodOfTime; // "startTime - endTime"

    public TimetableData(String name, String type, String periodOfTime) {
        this.name = name;
        this.type = type;
        this.periodOfTime = periodOfTime;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getPeriodOfTime() {
        return periodOfTime;
    }

    public boolean equals(Object o) { // we compare the objects with the String representation of the period of time
        return (o instanceof TimetableData) ? ((TimetableData) o).periodOfTime.equals(this.periodOfTime) : false;
    }

    public int hashCode() {
        return 13*periodOfTime.hashCode() + 2;
    }
}
