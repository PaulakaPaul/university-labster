package ro.ianders.universitylabsterremake.mainactivityfragments.auxiliaractivitesandfragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ro.ianders.universitylabsterremake.LabsterApplication;
import ro.ianders.universitylabsterremake.LabsterConstants;
import ro.ianders.universitylabsterremake.R;
import ro.ianders.universitylabsterremake.datatypes.ActivityCourse;
import ro.ianders.universitylabsterremake.datatypes.Course;
import ro.ianders.universitylabsterremake.datatypes.ListData;
import ro.ianders.universitylabsterremake.datatypes.Professor;
import ro.ianders.universitylabsterremake.datatypes.Student;

public class CourseActivity extends AppCompatActivity {

    private ImageView ivType;
    private TextView tvFullName;
    private TextView tvHour;
    private TextView tvProfessorNames;
    private TextView tvLocation;
    private TabLayout tabCourse;

    private FirebaseAuth firebaseAuth;
    private Student currentStudent;
    private Course currentHour; // we use this Class to show everything we need
    private ViewPager pagerCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        ivType = findViewById(R.id.ivType);
        tvFullName = findViewById(R.id.tvFullName);
        tvHour = findViewById(R.id.tvHour);
        tvProfessorNames = findViewById(R.id.tvProfessorNames);
        tvLocation = findViewById(R.id.tvLocation);
        tabCourse = findViewById(R.id.tabCourse);
        pagerCourse = findViewById(R.id.pagerCourse);

        firebaseAuth = FirebaseAuth.getInstance();

        ListData currentListData = getIntent().getParcelableExtra("data"); // get the ListData from the list view
        ivType.setImageResource(currentListData.getType()); // setting the image
        GridLayout gridCourseMain = findViewById(R.id.gridCourseMain);
        gridCourseMain.setBackgroundColor(currentListData.getColor()); //setting background

        currentStudent = getCurrentStudent(); // getting current Student for querying data

        currentHour = getCurrentHour(currentStudent.getYear(), currentStudent.getFaculty(), currentStudent.getSection(), currentListData.getType(), currentListData.getName());
        // getting current hour to show the data

        //setting currentHour and ListData data
        String[] name = currentHour.getCourseData().getNameCourse().split(" "); //putting every word on a different line
        String newName = "";
        for(String s : name)
            newName += s + "\n";
        newName = newName.substring(0, newName.length() - 1);
        tvFullName.setText(newName);
        tvHour.setText(currentListData.getSchedule());

        StringBuilder proffesor = new StringBuilder();
        for(Professor p : currentHour.getProfessors()) {
            proffesor.append(p.getName());

            if(p.getEmail() != null)
                proffesor.append("\n").append("email: ").append(p.getEmail());

            proffesor.append("\n");
        }

        tvProfessorNames.setText(proffesor.toString().substring(0, proffesor.length() - 1));
        tvLocation.setText(currentHour.getCourseData().getLocation());

        //setting tab
        tabCourse.addTab(tabCourse.newTab().setText(LabsterConstants.TAB_COURSE_CHECKINS));
        tabCourse.addTab(tabCourse.newTab().setText(LabsterConstants.TAB_COURSE_NOTES));
        tabCourse.setTabGravity(TabLayout.GRAVITY_FILL);

        //setting pager
        CoursePagerAdapter coursePagerAdapter = new CoursePagerAdapter(getSupportFragmentManager(), tabCourse.getTabCount());
        pagerCourse.setAdapter(coursePagerAdapter);
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

        ((GradientDrawable) pagerCourse.getBackground()).setColor(Color.parseColor("#A4A4A4")); //setting background within the drawable set
        // as background -> we keep the drawable and change only the colour

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


 }
