package ro.ianders.universitylabsterremake.mainactivityfragments.auxiliaractivitesandfragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import ro.ianders.universitylabsterremake.LabsterApplication;
import ro.ianders.universitylabsterremake.LabsterConstants;
import ro.ianders.universitylabsterremake.R;
import ro.ianders.universitylabsterremake.datatypes.ActivityCourse;
import ro.ianders.universitylabsterremake.datatypes.Course;
import ro.ianders.universitylabsterremake.datatypes.DatabaseConstants;
import ro.ianders.universitylabsterremake.datatypes.ListData;
import ro.ianders.universitylabsterremake.datatypes.Message;
import ro.ianders.universitylabsterremake.datatypes.MessagesCourse;
import ro.ianders.universitylabsterremake.datatypes.MessagesPerSchedule;
import ro.ianders.universitylabsterremake.datatypes.Professor;
import ro.ianders.universitylabsterremake.datatypes.Schedule;
import ro.ianders.universitylabsterremake.datatypes.Student;

public class CourseActivity extends AppCompatActivity {

    private TextView tvFullName;
    private TextView tvHour;
    private TabLayout tabCourse;
    private ViewPager pagerCourse;
    private LinearLayout linCheckinAndNotes;
    private TextView tvExtraInfo; //when you click the tvFullName we will show this instead of linCheckinAndNotes and vice versa
    private CheckBox checkBoxCourse;

    private boolean showCheckinsAndNotes = false; // we control if we show linCheckinAndNotes with this variable (false cuz on the first click we will show the extra info)

    private FirebaseAuth firebaseAuth;
    private Student currentStudent;
    private Course currentHour; // we use this Class to show everything we need
    private int type; // it is a reference to the 3 types of pictures from the list (course, laboratory, seminary)
    private ListData currentListData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        tvFullName = findViewById(R.id.tvFullName);
        tvHour = findViewById(R.id.tvHour);
        tabCourse = findViewById(R.id.tabCourse);
        pagerCourse = findViewById(R.id.pagerCourse);
        linCheckinAndNotes = findViewById(R.id.linCheckinsAndNotes);
        tvExtraInfo = findViewById(R.id.tvExtraInfo);
        checkBoxCourse = findViewById(R.id.checkBoxCourse);

        firebaseAuth = FirebaseAuth.getInstance();

        currentListData = getIntent().getParcelableExtra("data"); // get the ListData from the list view
        type = currentListData.getType();

        LinearLayout linCourseMain = findViewById(R.id.linCourseMain);
        linCourseMain.setBackgroundColor(currentListData.getColor()); //setting background

        currentStudent = getCurrentStudent(); // getting current Student for querying data

        currentHour = getCurrentHour(currentStudent.getYear(), currentStudent.getFaculty(), currentStudent.getSection(), currentListData.getType(), currentListData.getName());
        // getting current hour to show the data

        //setting name
        String[] name = currentHour.getCourseData().getNameCourse().split(" "); //putting every word on a different line
        String newName = "";
        boolean newline = false;
        for(String s : name) { // we put the '\n' after 2 words
            if(newline) {
                newName += s + "\n";
                newline = false;
            }  else {
                newName += s + " ";
                newline = true;
            }
        }
        if(newName.charAt(newName.length() - 1) == '\n') // if the last char is '\n' remove it
            newName = newName.substring(0, newName.length() - 1);
        tvFullName.setText(newName);

        //setting hour
        tvHour.setText(currentListData.getSchedule());

        //setting tab
        tabCourse.addTab(tabCourse.newTab().setText(LabsterConstants.TAB_COURSE_CHECKINS));
        tabCourse.addTab(tabCourse.newTab().setText(LabsterConstants.TAB_COURSE_NOTES));
        tabCourse.setTabGravity(TabLayout.GRAVITY_FILL);

        //TODO query the right checkins
        //setting pager
        bindAdapterWithPager(false, false);
        pagerCourse.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabCourse));
        tabCourse.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pagerCourse.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ((GradientDrawable) pagerCourse.getBackground()).setColor(Color.parseColor("#FFFFFF")); //setting background within the drawable set
        // as background -> we keep the drawable and change only the colour

        //when we click the tvFullName we change the data we show
        tvFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchBottomData(); //implemented in this class
            }
        });

        checkBoxCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((CheckBox) view).isChecked()) {
                    bindAdapterWithPager(true, false); //implemnted in this class

                }
                else {
                    bindAdapterWithPager(false, true);
                }
            }
        });

    }


    private Student getCurrentStudent() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Student currentStudent = null;

        if(firebaseUser != null) { //retrieving current student

            for(Student s : LabsterApplication.getInstace().getStudents())
                if(s.getUserUID().equals(firebaseUser.getUid())) {
                    currentStudent = s;
                    break;
                }
        }

       return currentStudent;
    }

    //type is a reference to the pictures that define every type of hour (we use the information from the parameters to find that info)
    private Course getCurrentHour(int year,@NonNull String faculty,@NonNull String section,int type,@NonNull String name) {

        Course currentCourse = null;

        if(type == R.drawable.course) {

            for(Course c : LabsterApplication.getInstace().getCourses())
                if((c.getCourseData().getYear() == year) & (c.getCourseData().getFaculty().equals(faculty)) &
                        (c.getCourseData().getSection().equals(section)) & (c.getCourseData().getNameCourse().equals(name))) {
                            currentCourse = c;
                            break;
                }
        } else {

            char charType = type == R.drawable.laboratory ? 'l' : 's';

            for(ActivityCourse c : LabsterApplication.getInstace().getActivities())
                if((c.getCourseData().getYear() == year) & (c.getCourseData().getFaculty().equals(faculty)) &
                        (c.getCourseData().getSection().equals(section)) & (c.getCourseData().getNameCourse().equals(name))
                        & (c.getType().charAt(0) == charType) ) {
                    currentCourse = c;
                    break;
                }
        }
        return currentCourse;
    }


    private void switchBottomData() {

        if(showCheckinsAndNotes) {
            linCheckinAndNotes.setVisibility(View.VISIBLE);
            tvExtraInfo.setVisibility(View.GONE);
            showCheckinsAndNotes = false;
        } else {
            String courseExtraInfo = generateCourseExtraInfo();
            linCheckinAndNotes.setVisibility(View.GONE);
            tvExtraInfo.setText(courseExtraInfo);
            tvExtraInfo.setVisibility(View.VISIBLE);
            showCheckinsAndNotes = true;
        }
    }

    private String generateCourseExtraInfo() {
        StringBuilder text = new StringBuilder();

            text.append("faculty: ").append(currentHour.getCourseData().getFaculty()).append("\n");
            text.append("section: ").append(currentHour.getCourseData().getSection()).append("\n");
            text.append("year: ").append(currentHour.getCourseData().getYear()).append("\n");
            text.append("Location: ").append(currentHour.getCourseData().getLocation()).append("\n");

            int i = 1;
            text.append("Professors: ").append("\n");
            for(Professor p : currentHour.getProfessors()) {
                text.append(i).append(". ").append(p.getName());

                if(p.getEmail() != null)
                    text.append("\n").append("email: ").append(p.getEmail());

                text.append("\n");
            }

            text.delete(text.length() - 1, text.length()); // we delete the last \n

            return text.toString();
    }

    private ArrayList<String> generateCorrectCheckins(boolean addCurrentStudent, boolean removeCurrentStudent) {

        String todayDate = LabsterApplication.generateTodayDate();
        ArrayList<String> uidcheckins = null;
        ArrayList<String> namesCheckins = new ArrayList<>();
        String startTime = currentListData.getSchedule().split(" - ")[0];
        String endTime = currentListData.getSchedule().split(" - ")[1];

        int i = 0; // we need need this for the database path
        for(Schedule s : currentHour.getSchedules()) {//the checkins from today and the right hour
            if (s.getDate().equals(todayDate) & s.getStartTime().equals(startTime) & s.getEndTime().equals(endTime)) {

                if (addCurrentStudent || removeCurrentStudent) { // we solicit the add / remove logic
                    // only if one of this one is true(even saving to database)
                    if (addCurrentStudent) {
                        s.addCheckin(currentStudent.getUserUID());
                    }

                    if (removeCurrentStudent) {
                        s.removeCheckin(currentStudent.getUserUID());
                    }

                    // save to database
                    if (type == R.drawable.course) {
                        LabsterApplication.getInstace().saveFieldToCourse(currentHour, DatabaseConstants.COURSE_SCHEDULES + "/" + i + "/" + DatabaseConstants.COURSE_CHECKINS, s.getCheckins());
                    } else {
                        LabsterApplication.getInstace().saveFieldToActivityCourse((ActivityCourse) currentHour, DatabaseConstants.ACTIVITYCOURSE_SCHEDULES + "/" + i + "/" +
                                DatabaseConstants.ACTIVITYCOURSE_CHECKINS, s.getCheckins());
                    }
                }

                uidcheckins = (ArrayList<String>) s.getCheckins();
                break;
            }
            i++;
        }

        if(uidcheckins != null) {
            setCheckinBoxState(uidcheckins); // by default is unchecked (in case there are no check-ins -> uidcheckins = null)
            for (String uid : uidcheckins) {
                Student s = new Student(uid);
                int index = LabsterApplication.getInstace().getStudents().indexOf(s);
                if (index != -1) {
                    String fullName = LabsterApplication.getInstace().getStudents().get(index).getProfile().getLastName() + " " +
                            LabsterApplication.getInstace().getStudents().get(index).getProfile().getFirstName();
                    namesCheckins.add(fullName);
                }
            }
        return namesCheckins;
        }

       return null;
    }

    private void bindAdapterWithPager(boolean addCurrentStudent, boolean removeCurrentStudent) {
        CoursePagerAdapter coursePagerAdapter = new CoursePagerAdapter(getSupportFragmentManager(), tabCourse.getTabCount(), generateCorrectCheckins(addCurrentStudent, removeCurrentStudent), null);
        pagerCourse.setAdapter(coursePagerAdapter);
    }

    private void setCheckinBoxState(ArrayList<String> uidCheckins) {

        int indexStudent = uidCheckins.indexOf(currentStudent.getUserUID());
        if(indexStudent != -1) // we found the currentStudent
            checkBoxCourse.setChecked(true);
        else // if not found uncheck the box
            checkBoxCourse.setChecked(false);
    }

    private ArrayList<Message> generateNotes() {

        MessagesCourse messagesCourse = new MessagesCourse(currentHour.getKey()); // we create this object just to query the list and get the
        //messages course with the schedules
        int index = LabsterApplication.getInstace().getMessages().indexOf(messagesCourse);

        messagesCourse = LabsterApplication.getInstace().getMessages().get(index); // the messages and the course have the same key

        String todayDate = LabsterApplication.generateTodayDate();
        String startTime = currentListData.getSchedule().split(" - ")[0];
        String endTime = currentListData.getSchedule().split(" - ")[1];
        Schedule schedule = new Schedule(todayDate, startTime, endTime); // we make this so we can query the currentHour for the currentSchedules index

        int indexOfSchedule = currentHour.getSchedules().indexOf(schedule);

        MessagesPerSchedule notes =  messagesCourse.getAllMessages().get(indexOfSchedule); //the schedule and the messages from that schedule have the
        // same index

        return (ArrayList<Message>) notes.getNotes();
    }


 }
