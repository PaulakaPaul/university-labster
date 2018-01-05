package ro.ianders.universitylabsterremake.datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by paul.iusztin on 03.01.2018.
 */

public class PendingActivityCourse extends ActivityCourse {

    private List<Student> validations;
    public static final int NUMBER_OF_VALIDATIONS = PendingCourse.NUMBER_OF_VALIDATIONS;

    public PendingActivityCourse(String key, String type, CourseData courseData, List<Professor> professors, List<Schedule> schedules) {
        super(key,type, courseData, professors, schedules);
        validations = new ArrayList<>();
    }

    public PendingActivityCourse(String type, CourseData courseData, List<Professor> professors, List<Schedule> schedules) {
        super(type, courseData, professors, schedules);
        validations = new ArrayList<>();
    }

    public PendingActivityCourse(String key, String type, CourseData courseData, List<Professor> professors, List<Schedule> schedules, List<Student> validations) {
        super(key,type, courseData, professors, schedules);

        if(validations != null)
            this.validations = validations;
        else
            this.validations = new ArrayList<>();
    }

    public void addValidation(Student student) {
        validations.add(student);
    }

    public void removeAllValidations() {
        validations.clear();
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> activityCourses = new HashMap<>();

        activityCourses.put(DatabaseConstants.PENDING_ACTIVITYCOURSE_STUDENTS_VALIDATIONS, validations);
        activityCourses.put(DatabaseConstants.ACTIVITYCOURSE_TYPE, getType());
        activityCourses.put(DatabaseConstants.ACTIVITYCOURSE_DATA, getCourseData());
        activityCourses.put(DatabaseConstants.ACTIVITYCOURSE_KEY, getKey());
        activityCourses.put(DatabaseConstants.ACTIVITYCOURSE_PROFESSORS, getProfessors());
        activityCourses.put(DatabaseConstants.ACTIVITYCOURSE_SCHEDULES, getSchedules());

        return activityCourses;
    }

    public List<Student> getValidations() {
        return validations;
    }
}
