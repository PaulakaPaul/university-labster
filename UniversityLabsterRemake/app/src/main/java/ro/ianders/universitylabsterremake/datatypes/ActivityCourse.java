package ro.ianders.universitylabsterremake.datatypes;

import java.util.HashMap;
import java.util.List;

/**
 * Created by paul.iusztin on 13.12.2017.
 */

public class ActivityCourse extends Course {

    private String type; //laboratory or seminar

    //same data type as a Course but it's easier to manage data like this
    public ActivityCourse(String key, String type, CourseData courseData, List<Professor> professors, List<Schedule> schedules) {
        super(key, courseData, professors, schedules);
        this.type = type;
    }

    public ActivityCourse(String type, CourseData courseData, List<Professor> professors, List<Schedule> schedules) {
        super(courseData, professors, schedules);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> activityCourses = new HashMap<>();

        activityCourses.put(DatabaseConstants.ACTIVITYCOURSE_TYPE, type);
        activityCourses.put(DatabaseConstants.ACTIVITYCOURSE_DATA, getCourseData());
        activityCourses.put(DatabaseConstants.ACTIVITYCOURSE_KEY, getKey());
        activityCourses.put(DatabaseConstants.ACTIVITYCOURSE_PROFESSORS, getProfessors());
        activityCourses.put(DatabaseConstants.ACTIVITYCOURSE_SCHEDULES, getSchedules());

        return activityCourses;
    }

    public String toString() {
        return super.toString() + " " + type;
    }
}
