package ro.ianders.universitylabsterremake.datatypes;

/**
 * Created by paul.iusztin on 12.12.2017.
 */

public class CourseData {
    private String nameCourse;
    private String location;
    private int year;
    private String faculty;
    private String section;

    public CourseData() {}

    public CourseData(String nameCourse, String location, int year, String faculty, String section) {
        this.nameCourse = nameCourse;
        this.location = location;
        this.year = year;
        this.faculty = faculty;
        this.section = section;
    }

    public String getNameCourse() {
        return nameCourse;
    }

    public String getLocation() {
        return location;
    }

    public int getYear() {
        return year;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getSection() {
        return section;
    }
}
