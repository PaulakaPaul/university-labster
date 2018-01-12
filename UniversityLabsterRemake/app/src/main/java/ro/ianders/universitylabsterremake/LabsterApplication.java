package ro.ianders.universitylabsterremake;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ro.ianders.universitylabsterremake.datatypes.Course;
import ro.ianders.universitylabsterremake.datatypes.CourseData;
import ro.ianders.universitylabsterremake.datatypes.DatabaseConstants;
import ro.ianders.universitylabsterremake.datatypes.ActivityCourse;
import ro.ianders.universitylabsterremake.datatypes.Message;
import ro.ianders.universitylabsterremake.datatypes.MessagesCourse;
import ro.ianders.universitylabsterremake.datatypes.PendingActivityCourse;
import ro.ianders.universitylabsterremake.datatypes.PendingCourse;
import ro.ianders.universitylabsterremake.datatypes.Professor;
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
    private DatabaseReference databaseReferenceMessages;
    private DatabaseReference databaseReferencePendingCourses;
    private DatabaseReference databaseReferencePendingActivityCourses;

    //local data from the database
    private List<Course> courses;
    private List<ActivityCourse> activities;
    private List<Student> students;
    private List<MessagesCourse> messages;
    private List<PendingCourse> pendingCourses;
    private List<PendingActivityCourse> pendingActivityCourses;

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
        databaseReferenceMessages = FirebaseDatabase.getInstance().getReference(DatabaseConstants.NOTES_NODE);
        databaseReferencePendingCourses = FirebaseDatabase.getInstance().getReference(DatabaseConstants.PENDING_COURSE_NODE);
        databaseReferencePendingActivityCourses = FirebaseDatabase.getInstance().getReference(DatabaseConstants.PENDING_ACTIVITYCOURSE_NODE);


        //creating lists of local data
        courses = new ArrayList<>();
        activities = new ArrayList<>();
        students = new ArrayList<>();
        messages = new ArrayList<>();
        pendingCourses = new ArrayList<>();
        pendingActivityCourses = new ArrayList<>();

        //calling local method to set the listeners for the database
        settingListenersForDataBase();

        //dummy set of data : DON'T DELETE IT, IT's USED FOR TESTING


        /*Course c ;
        CourseData courseData = new CourseData("POO", "Parvan", 2, "AC", "CTI");
        Professor professor = new Professor("profesor", "email");

        List<Professor> professors = new ArrayList<>();
        professors.add(professor);
        List<String> checkins  = new ArrayList<String>() {{
            add("paul");
            add("mihai");
        }};
        List<Schedule> schedules = new ArrayList<>();
        Schedule schedule = new Schedule("09/12/2017", "10:00", "12:00",1, checkins);

        schedules.add(schedule);

        c = new Course(courseData, professors, schedules);

        saveCourse(c, true);


        Course b ;

        professor = new Professor("profesor", "email");
        Professor professor2 = new Professor("newProfesor");
        professors = new ArrayList<>();
        professors.add(professor);
        professors.add(professor2);

        courseData = new CourseData("AC", "Parvan", 1, "AC", "IS");

        b = new Course(courseData, professors, schedules);

       saveCourse(b, true);

        //dummy set of data
        ActivityCourse activityCourse;
        CourseData courseData2 = new CourseData("POO", "Parvan", 2, "AC", "CTI");
        Professor professor3 = new Professor("profesor", "email");
        Schedule schedule2 = new Schedule("10/12/2017", "10:00", "12:00", 1, checkins);

        List<Professor> professors1 = new ArrayList<>();
        professors1.add(professor3);
        List<Schedule> schedules1 = new ArrayList<>();
        schedules1.add(schedule2);
        schedules1.add(schedule);


        List<String> checkins1  = new ArrayList<String>() {{
            add("paul");
            add("mihai");
        }};

        schedules1.add(new Schedule("8/12/2017", "16:00", "18:00", 2, checkins1));

        activityCourse = new ActivityCourse("laborator", courseData2, professors1, schedules1);

        saveActivityCourse(activityCourse, true);

        ActivityCourse activityCourse1;

        professor = new Professor("profesor", "email");
        professor2 = new Professor("newProfesor");
        professors = new ArrayList<>();
        professors.add(professor);
        professors.add(professor2);

        courseData = new CourseData("AC", "Parvan", 1, "AC", "IS");

        activityCourse1 = new ActivityCourse("seminar", courseData, professors, schedules);

       saveActivityCourse(activityCourse1, true);

        //testing methods for activity courses

        saveActivityCourse(new ActivityCourse("laborator", new CourseData("Circuite Digitale", "Parvan", 2, "AC", "CTI"),
                professors, schedules), true);*/



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
                    CourseData courseData = dataSnapshot1.child(DatabaseConstants.COURSE_DATA).getValue(CourseData.class);
                    List<Professor> professors = dataSnapshot1.child(DatabaseConstants.COURSE_PROFESSORS).getValue(new GenericTypeIndicator<List<Professor>>(){});
                    List<Schedule> schedules = new ArrayList<>();

                    for(DataSnapshot schedule : dataSnapshot1.child(DatabaseConstants.COURSE_SCHEDULES).getChildren()) {
                        Integer courseStep = schedule.child(DatabaseConstants.SCHEDULE_COURSESTEP).getValue(Integer.class);
                        String date = schedule.child(DatabaseConstants.SCHEDULE_DATE).getValue(String.class);
                        String endTime = schedule.child(DatabaseConstants.SCHEDULE_ENDTIME).getValue(String.class);
                        String startTime = schedule.child(DatabaseConstants.SCHEDULE_STARTTIME).getValue(String.class);
                        List<String> checkins = schedule.child(DatabaseConstants.SCHEDULE_CHECKINS).getValue(new GenericTypeIndicator<List<String>>(){});

                        Schedule s = new Schedule(date, startTime, endTime, courseStep, checkins);
                        schedules.add(s);
                    }


                    Collections.sort(schedules, byDateComparator); // sort the schedules by Date

                    // !!!!!!!!!!!!!!!! you need to put {} to the GenericTypeIndicator to WORK!!!!!!!!!!!!!!!!!!

                    Course c = new Course(key, courseData, professors, schedules);
                    courses.add(c);

                    // TODO delete debugging info log.e
                    Log.e("COURSE", c.toString());

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
                    CourseData courseData = dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_DATA).getValue(CourseData.class);
                    List<Professor> professors = dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_PROFESSORS).getValue(new GenericTypeIndicator<List<Professor>>(){});
                    List<Schedule> schedules = new ArrayList<>();

                    for(DataSnapshot schedule : dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_SCHEDULES).getChildren()) {
                        Integer courseStep = schedule.child(DatabaseConstants.SCHEDULE_COURSESTEP).getValue(Integer.class);
                        String date = schedule.child(DatabaseConstants.SCHEDULE_DATE).getValue(String.class);
                        String endTime = schedule.child(DatabaseConstants.SCHEDULE_ENDTIME).getValue(String.class);
                        String startTime = schedule.child(DatabaseConstants.SCHEDULE_STARTTIME).getValue(String.class);
                        List<String> checkins = schedule.child(DatabaseConstants.SCHEDULE_CHECKINS).getValue(new GenericTypeIndicator<List<String>>(){});

                        Schedule s = new Schedule(date, startTime, endTime, courseStep, checkins);
                        schedules.add(s);
                    }

                    Collections.sort(schedules, byDateComparator); // sort the schedules by Date

                    // !!!!!!!!!!!!!!!! you need to put {} to the GenericTypeIndicator to WORK!!!!!!!!!!!!!!!!!!

                    ActivityCourse c = new ActivityCourse(key, type, courseData, professors, schedules);
                    activities.add(c);

                    // TODO delete debugging info log.e
                    Log.e("ACTIVITYCOURSE", c.toString());

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

        databaseReferenceMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                messages.clear();

                for(DataSnapshot m : dataSnapshot.getChildren()) {

                    String key = m.child(DatabaseConstants.NOTES_KEY).getValue(String.class);

                    /*List<Message> messagesCourse = new ArrayList<>();

                    for(DataSnapshot message : m.child(DatabaseConstants.NOTES_MESSAGES).getChildren()) {
                        String userUID = message.child(DatabaseConstants.MESSAGE_USERUID).getValue(String.class);
                        String note = message.child(DatabaseConstants.MESSAGE_STRING_MESSAGE).getValue(String.class);
                        messagesCourse.add(new Message(userUID, note));
                    }*/

                    List<Message> messagesCourse = m.child(DatabaseConstants.NOTES_MESSAGES).getValue(new GenericTypeIndicator<List<Message>>(){});

                    MessagesCourse messagesCourse1 =new MessagesCourse(key, messagesCourse);
                    messages.add(messagesCourse1);
                    Log.e("MESSAGES", messagesCourse1.toString());
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReferencePendingCourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pendingCourses.clear();

                for(DataSnapshot c : dataSnapshot.getChildren()) {

                    List<Student> validations =  c.child(DatabaseConstants.PENDING_COURSE_STUDENTS_VALIDATIONS).getValue(new GenericTypeIndicator<List<Student>>(){});
                    String key = c.child(DatabaseConstants.COURSE_KEY).getValue(String.class);
                    CourseData courseData = c.child(DatabaseConstants.COURSE_DATA).getValue(CourseData.class);
                    List<Professor> professors = c.child(DatabaseConstants.COURSE_PROFESSORS).getValue(new GenericTypeIndicator<List<Professor>>(){});
                    List<Schedule> schedules = new ArrayList<>();

                    for(DataSnapshot schedule : c.child(DatabaseConstants.COURSE_SCHEDULES).getChildren()) {
                        Integer courseStep = schedule.child(DatabaseConstants.SCHEDULE_COURSESTEP).getValue(Integer.class);
                        String date = schedule.child(DatabaseConstants.SCHEDULE_DATE).getValue(String.class);
                        String endTime = schedule.child(DatabaseConstants.SCHEDULE_ENDTIME).getValue(String.class);
                        String startTime = schedule.child(DatabaseConstants.SCHEDULE_STARTTIME).getValue(String.class);
                        List<String> checkins = schedule.child(DatabaseConstants.SCHEDULE_CHECKINS).getValue(new GenericTypeIndicator<List<String>>(){});

                        Schedule s = new Schedule(date, startTime, endTime, courseStep, checkins);
                        schedules.add(s);
                    }


                    Collections.sort(schedules, byDateComparator); // sort the schedules by Date

                    // !!!!!!!!!!!!!!!! you need to put {} to the GenericTypeIndicator to WORK!!!!!!!!!!!!!!!!!!

                    PendingCourse pendingCourse = new PendingCourse(key, courseData, professors, schedules, validations);
                    pendingCourses.add(pendingCourse);

                    // TODO delete debugging info log.e
                    Log.e("PENDINGCOURSE", pendingCourse.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        databaseReferencePendingActivityCourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                pendingActivityCourses.clear();


                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {


                    List<Student> validations =  dataSnapshot1.child(DatabaseConstants.PENDING_ACTIVITYCOURSE_STUDENTS_VALIDATIONS).getValue(new GenericTypeIndicator<List<Student>>(){});
                    String key = dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_KEY).getValue(String.class);
                    String type = dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_TYPE).getValue(String.class);
                    CourseData courseData = dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_DATA).getValue(CourseData.class);
                    List<Professor> professors = dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_PROFESSORS).getValue(new GenericTypeIndicator<List<Professor>>(){});
                    List<Schedule> schedules = new ArrayList<>();

                    for(DataSnapshot schedule : dataSnapshot1.child(DatabaseConstants.ACTIVITYCOURSE_SCHEDULES).getChildren()) {
                        Integer courseStep = schedule.child(DatabaseConstants.SCHEDULE_COURSESTEP).getValue(Integer.class);
                        String date = schedule.child(DatabaseConstants.SCHEDULE_DATE).getValue(String.class);
                        String endTime = schedule.child(DatabaseConstants.SCHEDULE_ENDTIME).getValue(String.class);
                        String startTime = schedule.child(DatabaseConstants.SCHEDULE_STARTTIME).getValue(String.class);
                        List<String> checkins = schedule.child(DatabaseConstants.SCHEDULE_CHECKINS).getValue(new GenericTypeIndicator<List<String>>(){});

                        Schedule s = new Schedule(date, startTime, endTime, courseStep, checkins);
                        schedules.add(s);
                    }

                    Collections.sort(schedules, byDateComparator); // sort the schedules by Date

                    // !!!!!!!!!!!!!!!! you need to put {} to the GenericTypeIndicator to WORK!!!!!!!!!!!!!!!!!!

                    PendingActivityCourse c = new PendingActivityCourse(key, type, courseData, professors, schedules, validations);
                    pendingActivityCourses.add(c);

                    // TODO delete debugging info log.e
                    Log.e("PENDING ACTIVITYCOURSE", c.toString());

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

    //functions for messages
    public void saveMessages(MessagesCourse messagesCourse, List<Message> listToSave) {
        databaseReferenceMessages.child(messagesCourse.getKey()).child(DatabaseConstants.NOTES_MESSAGES).setValue(listToSave);
    } // this is to save the whole list of messages for a schedule

    public void saveMessage(MessagesCourse messagesCourse, Message messageToSave) {
        databaseReferenceMessages.child(messagesCourse.getKey()).child(DatabaseConstants.NOTES_MESSAGES)
                .child(messagesCourse.getAllMessages().size() + "").setValue(messageToSave.toMap()); // we save a message on the last space
    } // this is to save only a specific message

    public void saveMessageAfterDynamics(MessagesCourse messagesCourse, Message messageToSave) {
        databaseReferenceMessages.child(messagesCourse.getKey()).child(DatabaseConstants.NOTES_MESSAGES)
                .child(messagesCourse.getAllMessages().size() - 1+ "").setValue(messageToSave.toMap()); // we save a message on the last space
    } // this is to save only a specific message (it has a -1 so it will be saved at the same index as the message was saved dynamically in the array

    public void saveCourseMessage(MessagesCourse messagesCourse) {
        databaseReferenceMessages.child(messagesCourse.getKey()).setValue(messagesCourse.toMap());
    }

    //functions for pending course
    public void savePendingCourse(PendingCourse pendingCourse) {

        // if we have to generate the key for the first time
            String key = databaseReferenceCourses.push().getKey();
            pendingCourse.setKey(key);

        databaseReferencePendingCourses.child(pendingCourse.getKey())
                .setValue(pendingCourse.toMap());
    }

    public void removePendingCourse(PendingCourse pendingCourse) {
        databaseReferencePendingCourses.child(pendingCourse.getKey())
                .removeValue();
    }

    //save only a validation
    public void addValidationToPendingCourse(PendingCourse pendingCourse, Student student) {
        databaseReferencePendingCourses.child(pendingCourse.getKey()).child(DatabaseConstants.PENDING_COURSE_STUDENTS_VALIDATIONS).child(pendingCourse.getValidations().size()+"").setValue(student.toMap());
    }

    //functions for pending activity course
    public void savePendingActivityCourse(PendingActivityCourse pendingActivityCourse) {

        // if we have to generate the key for the first time
        String key = databaseReferencePendingActivityCourses.push().getKey();
        pendingActivityCourse.setKey(key);

        databaseReferencePendingActivityCourses.child(pendingActivityCourse.getKey())
                .setValue(pendingActivityCourse.toMap());
    }

    public void removePendingActivityCourse(PendingActivityCourse pendingActivityCourse) {
        databaseReferencePendingActivityCourses.child(pendingActivityCourse.getKey())
                .removeValue();
    }

    //save only a validation
    public void addValidationToPendingActivityCourse(PendingActivityCourse pendingActivityCourse, Student student) {
        databaseReferencePendingActivityCourses.child(pendingActivityCourse.getKey()).child(DatabaseConstants.PENDING_ACTIVITYCOURSE_STUDENTS_VALIDATIONS)
                .child(pendingActivityCourse.getValidations().size()+"").setValue(student.toMap());
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

    public List<MessagesCourse> getMessages() {
        return messages;
        }

    public List<PendingCourse> getPendingCourses() {
        return pendingCourses;
    }

    public List<PendingActivityCourse> getPendingActivityCourses() {
        return pendingActivityCourses;
    }

    public void addDynamicallyPendingCourse(PendingCourse pendingCourse) {
        pendingCourses.add(pendingCourse);
    }

    public void addDynamicallyActivityPendingCourse(PendingActivityCourse pendingActivityCourse) {
        pendingActivityCourses.add(pendingActivityCourse);
    }

    public void addMessageCourse(MessagesCourse messagesCourse) {
        messages.add(messagesCourse);
    }

    public void addMessageToMesaageCourse(MessagesCourse messagesCourse, Message message) {
        int index = messages.indexOf(messagesCourse);

        if(index != -1)
            messages.get(index).addMessage(message);
        else { // if we add a message that has no messageCourse
            messagesCourse.addMessage(message);
            messages.add(messagesCourse);
        }
    }


    public static String getAcronym(String name) {

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

    public void updateDatesFromDatabase() {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Date date;
        Calendar calendar = Calendar.getInstance();

        Date currentDate = Calendar.getInstance().getTime(); //today's date
        Calendar currentDateCallendar = Calendar.getInstance();
        currentDateCallendar.setTime(currentDate); // and calendar

        //updating courses date schedules
        for(Course c : courses) {
            for (Schedule s : c.getSchedules()) {
                try {

                    date = formatter.parse(s.getDate());
                    calendar.setTime(date); // we transform the string to a calendar because we can use the add method just on this class

                    if(!formatter.format(currentDate).equals(s.getDate())) { // only if it is not today's date ( we compare it as a String cuz it's easier)
                        while (currentDateCallendar.after(calendar)) { //we add the course step until the date is updated
                            calendar.add(Calendar.DAY_OF_MONTH, 7 * s.getStep());
                        }

                        date = calendar.getTime(); // we save the date as a string back to the schedule
                        s.setDate(formatter.format(date));

                        if(currentDateCallendar.after(calendar)) // we delete it only if the currentDate is after the other date
                            s.getCheckins().clear(); // we delete the check-ins from previous dates
                    }

                    if(s.getCheckins() == null) // so we won't save null to database cuz it will crush
                        s.setCheckins(new ArrayList<>());

                } catch (ParseException e) {
                    Log.e("PARSEEXCEPTION", "VALIDATE YOUR DATE TYPE");
                }

            }

            // after changing the schedules we save them again to the database
            saveFieldToCourse(c, DatabaseConstants.COURSE_SCHEDULES, c.getSchedules());
        }


        //updating activity courses date schedules
        for(ActivityCourse ac : activities) {
            for (Schedule s : ac.getSchedules()) {
                try {

                    date = formatter.parse(s.getDate());
                    calendar.setTime(date); // we transform the string to a calendar because we can use the add method just on this class

                    if(!formatter.format(currentDate).equals(s.getDate())) { // only if it is not today's date ( we compare it as a String cuz it's easier)
                        while (currentDateCallendar.after(calendar)) { //we add the course step until the date is updated
                            calendar.add(Calendar.DAY_OF_MONTH, 7 * s.getStep());
                        }

                        date = calendar.getTime(); // we save the date as a string back to the schedule
                        s.setDate(formatter.format(date));

                        if(currentDateCallendar.after(calendar)) // we delete it only if the currentDate is after the other date
                            s.getCheckins().clear(); // we delete the check-ins from previous dates
                    }

                } catch (ParseException e) {
                    Log.e("PARSEEXCEPTION", "VALIDATE YOUR DATE TYPE");
                }

            }

            // after changing the schedules we save them again to the database
            saveFieldToActivityCourse(ac, DatabaseConstants.ACTIVITYCOURSE_SCHEDULES, ac.getSchedules());
        }

    }

    public static String generateTodayDate() {
        //get today's date to show only the activities from today
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/YYYY");
        String todayDate = dateformat.format(cal.getTime());

        return todayDate;
    }

    public static Student getCurrentStudent() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Student currentStudent = null;

        if(firebaseUser != null) {

            for (Student s : LabsterApplication.getInstace().getStudents())
                if (s.getUserUID().equals(firebaseUser.getUid())) {
                    currentStudent = s;
                    break; // the UserUid is unique
                }
        }

        return currentStudent;
    }

    public  void showLocationOnGoogleMaps(String address) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + address + "?z=20");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

}
