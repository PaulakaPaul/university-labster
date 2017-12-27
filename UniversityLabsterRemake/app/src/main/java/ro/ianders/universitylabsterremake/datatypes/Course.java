package ro.ianders.universitylabsterremake.datatypes;

import java.util.HashMap;
import java.util.List;

/**
 * Created by paul.iusztin on 11.12.2017.
 */

public class Course {

    private String key;
    private CourseData courseData;
    private List<Professor> professors;
    private List<String> checkins;
    private List<Schedule> schedules;

    public Course() {}

    public Course(String key, CourseData courseData, List<Professor> professors, List<String> checkins, List<Schedule> schedules) {
        this.key = key;
        this.courseData = courseData;
        this.professors = professors;
        this.checkins = checkins;
        this.schedules = schedules;
    }

    public Course(CourseData courseData, List<Professor> professors, List<String> checkins, List<Schedule> schedules) {
        this.courseData = courseData;
        this.professors = professors;
        this.checkins = checkins;
        this.schedules = schedules;
    }



    public HashMap<String, Object> toMap() {

        HashMap<String, Object> u = new HashMap<>();
        u.put(DatabaseConstants.COURSE_KEY, key);
        u.put(DatabaseConstants.COURSE_DATA, courseData);
        u.put(DatabaseConstants.COURSE_PROFESSORS, professors);
        u.put(DatabaseConstants.COURSE_CHECKINS, checkins);
        u.put(DatabaseConstants.COURSE_SCHEDULES, schedules);

        return u;
    }

    public CourseData getCourseData() {
        return courseData;
    }

    public List<Professor> getProfessors() {
        return professors;
    }

    public List<String> getCheckins() {
        return checkins;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String toString() {
        String s = courseData.getNameCourse() + "\n";

        // TODO delete dummy for
        for(Schedule schedule : schedules)
            s += schedule.getDate() + "\n";

        return s;
    }
}
