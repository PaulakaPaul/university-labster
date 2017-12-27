package ro.ianders.universitylabsterremake.datatypes;

import java.util.HashMap;
import java.util.List;

/**
 * Created by paul.iusztin on 13.12.2017.
 */

public class ActivityCourse extends Course {

    private String type; //laboratory or seminar

    //same data type as a Course but it's easier to manage data like this
    public ActivityCourse(String key, String type, CourseData courseData, List<Professor> professors, List<String> checkins, List<Schedule> schedules) {
        super(key, courseData, professors, checkins, schedules);
        this.type = type;
    }

    public ActivityCourse(String type, CourseData courseData, List<Professor> professors, List<String> checkins, List<Schedule> schedules) {
        super(courseData, professors, checkins, schedules);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> activityCourses = super.toMap();
        activityCourses.put(DatabaseConstants.ACTIVITYCOURSE_TYPE, type); // putting extra information

        // putting the data with the correct key (course ->courseData ; activitycourse -> activitycourseData)
        activityCourses.remove(DatabaseConstants.COURSE_DATA);
        activityCourses.put(DatabaseConstants.ACTIVITYCOURSE_DATA, getCourseData());

        // putting the data with the correct key
        activityCourses.remove(DatabaseConstants.COURSE_KEY);
        activityCourses.put(DatabaseConstants.ACTIVITYCOURSE_KEY, getKey());

        return activityCourses;
    }

    public String toString() {
        return super.toString() + " " + type;
    }
}
