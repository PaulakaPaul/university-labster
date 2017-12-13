package ro.ianders.universitylabsterremake;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ro.ianders.universitylabsterremake.datatypes.Course;
import ro.ianders.universitylabsterremake.datatypes.CourseData;
import ro.ianders.universitylabsterremake.datatypes.DatabaseConstants;
import ro.ianders.universitylabsterremake.datatypes.ActivityCourse;
import ro.ianders.universitylabsterremake.datatypes.Professor;
import ro.ianders.universitylabsterremake.datatypes.Profile;
import ro.ianders.universitylabsterremake.datatypes.Schedule;
import ro.ianders.universitylabsterremake.datatypes.Student;

/**
 * Created by paul.iusztin on 11.12.2017.
 */

public class LabsterApplication extends Application {

    //singleton instance
    private static LabsterApplication instace;

    //database references
    private DatabaseReference databaseReferenceCourses;
    private DatabaseReference databaseReferenceStudents;
    private DatabaseReference databaseReferenceActivityCourses;

    //local data from the database
    private Set<Course> courses;
    private Set<ActivityCourse> activities;
    private List<Student> students;

    //comparator to sort Date types (using a lambda expression)
    private Comparator<Schedule> byDateComparator = (d1, d2) -> d1.getDate().compareTo(d2.getDate());


    public static LabsterApplication getInstace() {
        return instace;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        instace = this; // get the singleton instance

        //getting references from the database
        databaseReferenceCourses = FirebaseDatabase.getInstance().getReference(DatabaseConstants.COURSES_NODE);
        databaseReferenceActivityCourses = FirebaseDatabase.getInstance().getReference(DatabaseConstants.ACTIVITYCOURSES_NODE);
        databaseReferenceStudents = FirebaseDatabase.getInstance().getReference(DatabaseConstants.STUDENTS_NODE);


        //creating lists of local data
        courses = new HashSet<>();
        activities = new HashSet<>();
        students = new ArrayList<>();


        //calling local method to set the listeners for the database
        settingListenersForDataBase();


        //dummy set of data

        /*
        Course c ;
        CourseData courseData = new CourseData("POO", "Parvan", 2, "AC", "CTI");
        Professor professor = new Professor("profesor", "email");
        Schedule schedule = new Schedule(new Date(2017, 10, 26), "10:00", "12:00");

        List<Professor> professors = new ArrayList<>();
        professors.add(professor);
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(schedule);

        List<String> checkins  = new ArrayList<String>() {{
            add("paul");
            add("mihai");
        }};

        c = new Course(courseData, professors, checkins, schedules);

        FirebaseDatabase.getInstance().getReference().child("courses").child("poo").setValue(c.toMap());


        Course b ;

        professor = new Professor("profesor", "email");
        Professor professor2 = new Professor("newProfesor");
        professors = new ArrayList<>();
        professors.add(professor);
        professors.add(professor2);

        courseData = new CourseData("AC", "Parvan", 1, "AC", "IS");

        b = new Course(courseData, professors, checkins, schedules);

        FirebaseDatabase.getInstance().getReference().child("courses").child("ac").setValue(b.toMap());

        //dummy set of data
        ActivityCourse activityCourse;
        CourseData courseData2 = new CourseData("POO", "Parvan", 2, "AC", "CTI");
        Professor professor3 = new Professor("profesor", "email");
        Schedule schedule2 = new Schedule(new Date(2017, 10, 3), "10:00", "12:00");

        List<Professor> professors1 = new ArrayList<>();
        professors1.add(professor3);
        List<Schedule> schedules1 = new ArrayList<>();
        schedules1.add(schedule2);
        schedules1.add(schedule);
        schedules1.add(new Schedule(new Date(2017, 10, 1), "16:00", "18:00"));

        List<String> checkins1  = new ArrayList<String>() {{
            add("paul");
            add("mihai");
        }};

        activityCourse = new ActivityCourse("laborator", courseData2, professors1, checkins1, schedules1);

        FirebaseDatabase.getInstance().getReference().child(DatabaseConstants.ACTIVITYCOURSES_NODE).child("poo").setValue(activityCourse.toMap());


        ActivityCourse activityCourse1;

        professor = new Professor("profesor", "email");
        professor2 = new Professor("newProfesor");
        professors = new ArrayList<>();
        professors.add(professor);
        professors.add(professor2);

        courseData = new CourseData("AC", "Parvan", 1, "AC", "IS");

        activityCourse1 = new ActivityCourse("seminar", courseData, professors, checkins, schedules);

        FirebaseDatabase.getInstance().getReference().child(DatabaseConstants.ACTIVITYCOURSES_NODE).child("ac").setValue(activityCourse1.toMap());

        //student dummy data

        Profile profile = new Profile("Paul", "Iusztin", "email");
        Profile profile1 = new Profile("Mihai", "Iovanac", "yolo@email");

        Student student = new Student("AC", "CTI", 2, "PaulakaPaul", "1234", profile );
        Student student1 = new Student("AC", "IS", 1, "prostul", "hello", profile1);

        FirebaseDatabase.getInstance().getReference(DatabaseConstants.STUDENTS_NODE).child("pauliusztin").setValue(student.toMap());
        FirebaseDatabase.getInstance().getReference(DatabaseConstants.STUDENTS_NODE).child("mihaiiovanac").setValue(student1.toMap());


        //testing methods for students

        saveStudent(new Student("ETC", "ETC1", 3, "Mihaita", "5678", new Profile("Mihai", "Popescu", "mail")));
        saveFieldToStudent(student1, DatabaseConstants.STUDENT_USERNAME, "desteptul");


        //testing methods for activity courses

        saveActivityCourse(new ActivityCourse("laborator", new CourseData("Circuite Digitale", "Parvan", 2, "AC", "CTI"),
                professors, checkins, schedules));
        checkins.add("Ionel");
        saveFieldToActivityCourse(activityCourse1, DatabaseConstants.ACTIVITYCOURSE_CHECKINS, checkins);
        */


    }



    private void settingListenersForDataBase() {


        //listener for the Courses
        databaseReferenceCourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                courses.clear();

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    List<String> checkins = dataSnapshot1.child(DatabaseConstants.COURSE_CHECKINS).getValue(new GenericTypeIndicator<List<String>>(){});
                    CourseData courseData = dataSnapshot1.child(DatabaseConstants.COURSE_DATA).getValue(CourseData.class);
                    List<Professor> professors = dataSnapshot1.child(DatabaseConstants.COURSE_PROFESSORS).getValue(new GenericTypeIndicator<List<Professor>>(){});
                    List<Schedule> schedules = dataSnapshot1.child(DatabaseConstants.COURSE_SCHEDULES).getValue(new GenericTypeIndicator<List<Schedule>>(){});
                    if(schedules != null)
                        Collections.sort(schedules, byDateComparator); // sort the schedules by Date

                    // !!!!!!!!!!!!!!!! you need to put {} to the GenericTypeIndicator to WORK!!!!!!!!!!!!!!!!!!

                    Course c = new Course(courseData, professors, checkins, schedules);
                    courses.add(c);

                    // TODO delete debugging info log.e
                    Log.e("tag", c.toString());

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        databaseReferenceActivityCourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                activities.clear();

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String type = dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_TYPE).getValue(String.class);
                    List<String> checkins = dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_CHECKINS).getValue(new GenericTypeIndicator<List<String>>(){});
                    CourseData courseData = dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_DATA).getValue(CourseData.class);
                    List<Professor> professors = dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_PROFESSORS).getValue(new GenericTypeIndicator<List<Professor>>(){});
                    List<Schedule> schedules = dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_SCHEDULES).getValue(new GenericTypeIndicator<List<Schedule>>(){});
                    if(schedules != null)
                        Collections.sort(schedules, byDateComparator); // sort the schedules by Date

                    // !!!!!!!!!!!!!!!! you need to put {} to the GenericTypeIndicator to WORK!!!!!!!!!!!!!!!!!!

                    ActivityCourse c = new ActivityCourse(type, courseData, professors, checkins, schedules);
                    activities.add(c);

                    // TODO delete debugging info log.e
                    Log.e("tag", c.toString());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReferenceStudents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                students.clear();

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Student student = dataSnapshot1.getValue(Student.class);
                    students.add(student);

                    Log.e("student", student.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    //saving functions for courses
    public void saveCourse(Course course) {

        databaseReferenceCourses.child(getAcronym(course.getCourseData().getNameCourse()).
                toLowerCase()).setValue(course.toMap());
    }

    public void saveCheckinsToACourse(String courseName, List<String> checkins) {

        //using maps it's usually ok when you update multiple locations at the same time
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(getAcronym(courseName).toLowerCase() + "/" + DatabaseConstants.COURSE_CHECKINS, checkins);

        databaseReferenceCourses.updateChildren(updates);
    }

    public void saveSchedulesToACourse(String courseName, List<Schedule> schedules) {
        databaseReferenceCourses.child(getAcronym(courseName).toLowerCase() + "/" + DatabaseConstants.COURSE_SCHEDULES).setValue(schedules);
    }

    public void saveProfessorsToACourse(String courseName, List<Professor> professors) {
        databaseReferenceCourses.child(getAcronym(courseName).toLowerCase() + "/" + DatabaseConstants.COURSE_PROFESSORS).setValue(professors);
    }

    public void saveFieldToCourse(Course course, String field, Object valueToSave) {
        //the field is from the DatabaseConstants specifiers

        //TODO check the field to be only from the DatabaseConstants, otherwise you can't update with this method
        databaseReferenceCourses.child(createCourseKey(course)).child(field).setValue(valueToSave);
    }


    //saving functions for students
    public void saveStudent(Student student) {
        databaseReferenceStudents.child(createStudentKey(student)).setValue(student.toMap());
    }

    public void saveFieldToStudent(Student student, String field, Object valueToSave) {
        //the field is from the DatabaseConstants specifiers

        //TODO check the field to be only from the DatabaseConstants, otherwise you can't update with this method
        databaseReferenceStudents.child(createStudentKey(student)).child(field).setValue(valueToSave);
    }

    //saving functions for activitycoures
    public void saveActivityCourse(ActivityCourse activityCourse) {
        databaseReferenceActivityCourses.child(createActivityCourseKey(activityCourse)).setValue(activityCourse.toMap());
    }

    public void saveFieldToActivityCourse(ActivityCourse activityCourse, String field, Object valueToSave) {
        //the field is from the DatabaseConstants specifiers

        //TODO check the field to be only from the DatabaseConstants, otherwise you can't update with this method
        databaseReferenceActivityCourses.child(createActivityCourseKey(activityCourse)).child(field).setValue(valueToSave);
    }


    public Set<Course> getCourses() {
        return courses;
    }


    public String getAcronym(String name) {

        boolean isAcronym = true;

        //checking if we pass a acronym
        for(int i = 0; i < name.length() ; i++)
            if (name.charAt(i) == name.toLowerCase().charAt(i))
                isAcronym = false;
            else if(name.charAt(i) == ' ')
                isAcronym = false;


        String acronym;

        if(!isAcronym) {

            // making the acronym

            String[] words = name.split(" ");
            StringBuilder stringBuilder = new StringBuilder();

            for(String s : words)
                if(s.charAt(0) == s.toUpperCase().charAt(0))
                    stringBuilder.append(s.charAt(0));

            acronym = stringBuilder.toString();

        } else {
            //it already a acronym
            acronym = name;
        }

        return acronym;
    }


    //creating keys for data
    public String createCourseKey(Course course) {
        return getAcronym(course.getCourseData().getNameCourse()).toLowerCase();
    }

    public String createStudentKey(Student student) {
        return (student.getProfile().getFirstName() + student.getProfile().getLastName()).toLowerCase();
    }

    public String createActivityCourseKey(ActivityCourse activityCourse) {
        return getAcronym(activityCourse.getCourseData().getNameCourse()).toLowerCase();
    }

}
