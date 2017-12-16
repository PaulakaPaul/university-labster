package ro.ianders.universitylabsterremake.datatypes;

import java.util.HashMap;

/**
 * Created by paul.iusztin on 13.12.2017.
 */

public class Student {

    private String faculty;
    private String section;
    private int year;
    private String username;
    private String password;
    private Profile profile;

    public Student() {}

    public Student(String faculty, String section, int year, String username, String password, Profile profile) {
        this.faculty = faculty;
        this.section = section;
        this.year = year;
        this.username = username;
        this.password = password;
        this.profile = profile;
    }

    public Student(String faculty, String section, int year, String password, Profile profile) { //username is not mandatory
        this.faculty = faculty;
        this.section = section;
        this.year = year;
        this.password = password;
        this.profile = profile;
        this.username = null;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getSection() {
        return section;
    }

    public int getYear() {
        return year;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Profile getProfile() {
        return profile;
    }

    public HashMap<String, Object> toMap() {

        HashMap<String, Object> student = new HashMap<>();

        student.put(DatabaseConstants.STUDENT_FACULTY, faculty);
        student.put(DatabaseConstants.STUDENT_PASSWORD, password);
        student.put(DatabaseConstants.STUDENT_PROFILE, profile);
        student.put(DatabaseConstants.STUDENT_SECTION, section);
        student.put(DatabaseConstants.STUDENT_YEAR, year);
        student.put(DatabaseConstants.STUDENT_USERNAME, username);

        return student;
    }

    public String toString() {
        return profile.getFirstName() + " " + profile.getLastName();
    }
}
