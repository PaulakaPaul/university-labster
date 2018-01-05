package ro.ianders.universitylabsterremake.datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by paul.iusztin on 03.01.2018.
 */

public class PendingCourse extends Course {

    private List<Student> validations;
    public static final int NUMBER_OF_VALIDATIONS = 2;

    public PendingCourse(String key, CourseData courseData, List<Professor> professors, List<Schedule> schedules) {
        super(key, courseData, professors, schedules);
        validations = new ArrayList<>();
    }

    public PendingCourse(CourseData courseData, List<Professor> professors, List<Schedule> schedules) {
        super(courseData, professors, schedules);
        validations = new ArrayList<>();
    }

    public PendingCourse(String key, CourseData courseData, List<Professor> professors, List<Schedule> schedules, List<Student> validations) {
        super(key, courseData, professors, schedules);

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
        HashMap<String, Object> u = new HashMap<>();

        u.put(DatabaseConstants.PENDING_COURSE_STUDENTS_VALIDATIONS, validations);
        u.put(DatabaseConstants.COURSE_KEY, getKey());
        u.put(DatabaseConstants.COURSE_DATA, getCourseData());
        u.put(DatabaseConstants.COURSE_PROFESSORS, getProfessors());
        u.put(DatabaseConstants.COURSE_SCHEDULES, getSchedules());

        return u;
    }

    public List<Student> getValidations() {
        return validations;
    }
}
