package ro.ianders.universitylabsterremake;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private DatabaseReference databaseReferenceTemporaryEmail;

    //local data from the database
    private List<Course> courses;
    private List<ActivityCourse> activities;
    private List<Student> students;

    //comparator to sort Date types (using a lambda expression)
    private Comparator<Schedule> byDateComparator = (d1, d2) -> {

        //we save the date in the database as a string so we need to see it as a date when we compare it

        //TODO add a DateTimeFormatter for the month for the catch block in case of example: 10/1/2017 if the data that you get from the EditText does not match and if matches dd/MM/yyyy for all the data remove the day catch block


        /* this is better for available only from the API 26 which represents <1% of the used androids

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatter1ForException = DateTimeFormatter.ofPattern("d/MM/yyyy");

        LocalDate date1;
        try {
            date1 = LocalDate.parse(d1.getDate(), formatter);
        } catch (DateTimeParseException e) {
            date1 = LocalDate.parse(d1.getDate(), formatter1ForException);
        }
        LocalDate date2;
        try {
            date2  = LocalDate.parse(d2.getDate(), formatter);
        } catch (DateTimeParseException e) {
            date2 = LocalDate.parse(d2.getDate(), formatter1ForException);
        }

        return date1.compareTo(date2);*/

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatter1ForException = new SimpleDateFormat("d/MM/yyyy");


        Date date1;
        try {
            date1 = formatter.parse(d1.getDate());
        } catch (ParseException e) {
            try {
                date1 = formatter1ForException.parse(d1.getDate());
            } catch (ParseException e1) {
                throw new RuntimeException("Wrong data type to parse !!!");
            }
        }
        Date date2;
        try {
            date2  = formatter.parse(d2.getDate());
        } catch (ParseException e) {
            try {
                date2 = formatter1ForException.parse(d2.getDate());
            } catch (ParseException e1) {
                throw new RuntimeException("Wrong data type to parse !!!");
            }
        }

        return date1.compareTo(date2);
    };


    public static LabsterApplication getInstace() {
        return instace;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        instace = this; // get the singleton instance

        //login Facebook initializations
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //getting references from the database
        databaseReferenceCourses = FirebaseDatabase.getInstance().getReference(DatabaseConstants.COURSES_NODE);
        databaseReferenceActivityCourses = FirebaseDatabase.getInstance().getReference(DatabaseConstants.ACTIVITYCOURSES_NODE);
        databaseReferenceStudents = FirebaseDatabase.getInstance().getReference(DatabaseConstants.STUDENTS_NODE);
        databaseReferenceTemporaryEmail = FirebaseDatabase.getInstance().getReference(DatabaseConstants.TEMPORARY_EMAIL);


        //creating lists of local data
        courses = new ArrayList<>();
        activities = new ArrayList<>();
        students = new ArrayList<>();


        //calling local method to set the listeners for the database
        settingListenersForDataBase();
       // updateDatesFromDatabase();

        //dummy set of data : DON'T DELETE IT, IT's USED FOR TESTING

        /*
        Course c ;
        CourseData courseData = new CourseData("POO", "Parvan", 2, "AC", "CTI");
        Professor professor = new Professor("profesor", "email");
        Schedule schedule = new Schedule("09/12/2017", "10:00", "12:00",1);

        List<Professor> professors = new ArrayList<>();
        professors.add(professor);
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(schedule);

        List<String> checkins  = new ArrayList<String>() {{
            add("paul");
            add("mihai");
        }};

        c = new Course(courseData, professors, checkins, schedules);

        saveCourse(c, true);


        Course b ;

        professor = new Professor("profesor", "email");
        Professor professor2 = new Professor("newProfesor");
        professors = new ArrayList<>();
        professors.add(professor);
        professors.add(professor2);

        courseData = new CourseData("AC", "Parvan", 1, "AC", "IS");

        b = new Course(courseData, professors, checkins, schedules);

       saveCourse(c, true);

        //dummy set of data
        ActivityCourse activityCourse;
        CourseData courseData2 = new CourseData("POO", "Parvan", 2, "AC", "CTI");
        Professor professor3 = new Professor("profesor", "email");
        Schedule schedule2 = new Schedule("10/12/2017", "10:00", "12:00", 1);

        List<Professor> professors1 = new ArrayList<>();
        professors1.add(professor3);
        List<Schedule> schedules1 = new ArrayList<>();
        schedules1.add(schedule2);
        schedules1.add(schedule);
        schedules1.add(new Schedule("8/12/2017", "16:00", "18:00", 2));

        List<String> checkins1  = new ArrayList<String>() {{
            add("paul");
            add("mihai");
        }};

        activityCourse = new ActivityCourse("laborator", courseData2, professors1, checkins1, schedules1);

        saveActivityCourse(activityCourse, true);

        ActivityCourse activityCourse1;

        professor = new Professor("profesor", "email");
        professor2 = new Professor("newProfesor");
        professors = new ArrayList<>();
        professors.add(professor);
        professors.add(professor2);

        courseData = new CourseData("AC", "Parvan", 1, "AC", "IS");

        activityCourse1 = new ActivityCourse("seminar", courseData, professors, checkins, schedules);

       saveActivityCourse(activityCourse1, true);

        //student dummy data

        Profile profile = new Profile("Paul", "Iusztin", "email");
        Profile profile1 = new Profile("Mihai", "Iovanac", "yolo@email");

        Student student = new Student("AC", "CTI", 2, "PaulakaPaul", "1234", profile );
        Student student1 = new Student("AC", "IS", 1, "prostul", "hello", profile1);

        saveStudent(student, true);
        saveStudent(student1, true);


        //testing methods for students

        saveStudent(new Student("ETC", "ETC1", 3, "Mihaita", "5678", new Profile("Mihai", "Popescu", "mail")), true);
        saveFieldToStudent(student1, DatabaseConstants.STUDENT_USERNAME, "desteptul");


        //testing methods for activity courses

        saveActivityCourse(new ActivityCourse("laborator", new CourseData("Circuite Digitale", "Parvan", 2, "AC", "CTI"),
                professors, checkins, schedules), true);
        checkins.add("Ionel");
        saveFieldToActivityCourse(activityCourse1, DatabaseConstants.ACTIVITYCOURSE_CHECKINS, checkins);
        */


        /** USED FOR GETTING HASH KEY **/
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "ro.ianders.universitylabsterremake",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        } /** TILL HERE**/
    }



    public void settingListenersForDataBase() {


        //listener for the Courses
        databaseReferenceCourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                courses.clear();

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String key = dataSnapshot1.child(DatabaseConstants.COURSE_KEY).getValue(String.class);
                    List<String> checkins = dataSnapshot1.child(DatabaseConstants.COURSE_CHECKINS).getValue(new GenericTypeIndicator<List<String>>(){});
                    CourseData courseData = dataSnapshot1.child(DatabaseConstants.COURSE_DATA).getValue(CourseData.class);
                    List<Professor> professors = dataSnapshot1.child(DatabaseConstants.COURSE_PROFESSORS).getValue(new GenericTypeIndicator<List<Professor>>(){});
                    List<Schedule> schedules = dataSnapshot1.child(DatabaseConstants.COURSE_SCHEDULES).getValue(new GenericTypeIndicator<List<Schedule>>(){});
                    if(schedules != null)
                        Collections.sort(schedules, byDateComparator); // sort the schedules by Date

                    // !!!!!!!!!!!!!!!! you need to put {} to the GenericTypeIndicator to WORK!!!!!!!!!!!!!!!!!!

                    Course c = new Course(key, courseData, professors, checkins, schedules);
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

                    String key = dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_KEY).getValue(String.class);
                    String type = dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_TYPE).getValue(String.class);
                    List<String> checkins = dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_CHECKINS).getValue(new GenericTypeIndicator<List<String>>(){});
                    CourseData courseData = dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_DATA).getValue(CourseData.class);
                    List<Professor> professors = dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_PROFESSORS).getValue(new GenericTypeIndicator<List<Professor>>(){});
                    List<Schedule> schedules = dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_SCHEDULES).getValue(new GenericTypeIndicator<List<Schedule>>(){});
                    if(schedules != null)
                        Collections.sort(schedules, byDateComparator); // sort the schedules by Date

                    // !!!!!!!!!!!!!!!! you need to put {} to the GenericTypeIndicator to WORK!!!!!!!!!!!!!!!!!!

                    ActivityCourse c = new ActivityCourse(key, type, courseData, professors, checkins, schedules);
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
    public void saveCourse(Course course, boolean generateKey) {

        if(generateKey) { // if we have to generate the key for the first time

            String key = databaseReferenceCourses.push().getKey();
            course.setKey(key);
        }


        databaseReferenceCourses.child(course.getKey())
                    .setValue(course.toMap());


    }

    public void saveCheckinsToACourse(Course course, List<String> checkins) {

        //using maps it's usually ok when you update multiple locations at the same time
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(course.getKey() + "/" + DatabaseConstants.COURSE_CHECKINS, checkins);

        databaseReferenceCourses.updateChildren(updates);
    }

    public void saveSchedulesToACourse(Course course, List<Schedule> schedules) {
        databaseReferenceCourses.child(course.getKey() + "/" + DatabaseConstants.COURSE_SCHEDULES).setValue(schedules);
    }

    public void saveProfessorsToACourse(Course course, List<Professor> professors) {
        databaseReferenceCourses.child(course.getKey() + "/" + DatabaseConstants.COURSE_PROFESSORS).setValue(professors);
    }

    public void saveFieldToCourse(Course course, String field, Object valueToSave) {
        //the field is from the DatabaseConstants specifiers

        //TODO check the field to be only from the DatabaseConstants, otherwise you can't update with this method
        databaseReferenceCourses.child(course.getKey()).child(field).setValue(valueToSave);
    }


    //saving functions for students
    public void saveStudent(Student student, boolean generateKey) {

        if(generateKey) { // if we have to generate the key for the first time
            String key = databaseReferenceStudents.push().getKey();
            student.setKey(key);
        }

        databaseReferenceStudents.child(student.getKey()).setValue(student.toMap());
    }

    public void saveFieldToStudent(Student student, String field, Object valueToSave) {
        //the field is from the DatabaseConstants specifiers

        //TODO check the field to be only from the DatabaseConstants, otherwise you can't update with this method
        databaseReferenceStudents.child(student.getKey()).child(field).setValue(valueToSave);
    }


    //saving functions for activitycoures
    public void saveActivityCourse(ActivityCourse activityCourse, boolean generateKey) {

        if(generateKey) { // if we have to generate the key for the first time
            String key = databaseReferenceActivityCourses.push().getKey();
            activityCourse.setKey(key);
        }

        databaseReferenceActivityCourses.child(activityCourse.getKey()).setValue(activityCourse.toMap());
    }

    public void saveFieldToActivityCourse(ActivityCourse activityCourse, String field, Object valueToSave) {
        //the field is from the DatabaseConstants specifiers

        //TODO check the field to be only from the DatabaseConstants, otherwise you can't update with this method
        databaseReferenceActivityCourses.child(activityCourse.getKey()).child(field).setValue(valueToSave);
    }


    // save field to temporary email
    // for those who registered but did not filled they data
    // if we find data there it means we go to the RegisterFillDataActivity
    public void saveTemporaryEmail(String email) {
         String newEmail = getKeyEmail(email);
        databaseReferenceTemporaryEmail.child(newEmail).setValue(newEmail);
    }

    //remove the temporary email to show the user filled all his data
    public void removeTemporaryEmail(String email) {
        String newEmail = getKeyEmail(email);
        databaseReferenceTemporaryEmail.child(newEmail).removeValue();
    }


    // getters for data
    public List<Course> getCourses() {
        return courses;
    }

    public List<ActivityCourse> getActivities() {
        return activities;
    }

    public List<Student> getStudents() {
        return students;
    }

    public DatabaseReference getDatabaseReferenceTemporaryEmail() {
        return databaseReferenceTemporaryEmail;
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

    //temporary email stored form in temporary emails
    public String getKeyEmail(String email) {
        String[] emails = email.split("\\.");
        StringBuilder newEmail = new StringBuilder();

        for(String s : emails)
            newEmail.append(s);

        return newEmail.toString();
    }

    private void updateDatesFromDatabase() {

        //TODO when courses will be created the date has to be of this type
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Date date;
        Calendar calendar = Calendar.getInstance();

        Date currentDate = Calendar.getInstance().getTime(); //today's date
        Calendar currentDateCallendar = Calendar.getInstance();
        currentDateCallendar.setTime(currentDate); // and calendar

        for(Course c : courses) {
            for (Schedule s : c.getSchedules()) {
                try {

                    date = formatter.parse(s.getDate());
                    calendar.setTime(date); // we transform the string to a calendar because we can use the add method just on this class

                    while (currentDateCallendar.before(calendar)) { //we add the course step until the date is updated
                        calendar.add(Calendar.DAY_OF_MONTH, 7 * s.getCourseStep());
                    }

                    date = calendar.getTime(); // we save the date as a string back to the schedule
                    s.setDate(formatter.format(date));

                } catch (ParseException e) {
                    Log.e("PARSEEXCEPTION", "VALIDATE YOUR DATE TYPE");
                }

            }

            // after changing the schedules we save them again to the database
            saveFieldToCourse(c, DatabaseConstants.ACTIVITYCOURSE_SCHEDULES, c.getSchedules());
        }



    }

    public static String generateTodayDate() {
        //get today's date to show only the activities from today
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/YYYY");
        String todayDate = dateformat.format(cal.getTime());
        //TODO delete this
        Log.e("TODAY's DATE", todayDate);
        return todayDate;
    }

}
