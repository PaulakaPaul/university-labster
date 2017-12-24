package ro.ianders.universitylabsterremake.datatypes;

import java.util.HashMap;

/**
 * Created by paul.iusztin on 13.12.2017.
 */

public class Student {

    private String key;
    private String faculty;
    private String section;
    private int year;
    private String username;
    private String password;
    private Profile profile;

    public Student() {}

    public Student(String key, String faculty, String section, int year, String username, String password, Profile profile) {
        this.key = key;
        this.faculty = faculty;
        this.section = section;
        this.year = year;
        this.username = username;
        this.password = password;
        this.profile = profile;
    }

    public Student(String key, String faculty, String section, int year, String password, Profile profile) { //username is not mandatory
        this.key = key;
        this.faculty = faculty;
        this.section = section;
        this.year = year;
        this.password = password;
        this.profile = profile;
        this.username = null;
    }

    public Student(String faculty, String section, int year, String username, String password, Profile profile) {
        this.faculty = faculty;
        this.section = section;
        this.year = year;
        this.username = username;
        this.password = password;
        this.profile = profile;
    }

    public Student(String faculty, String section, int year, String password, Profile profile) {
        this.faculty = faculty;
        this.section = section;
        this.year = year;
        this.password = password;
        this.profile = profile;
    }

    public Student(String email, String password) {
        this.password = password;
        this.profile = new Profile(email);
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public HashMap<String, Object> toMap() {

        HashMap<String, Object> student = new HashMap<>();

        student.put(DatabaseConstants.STUDENT_KEY, key);
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
