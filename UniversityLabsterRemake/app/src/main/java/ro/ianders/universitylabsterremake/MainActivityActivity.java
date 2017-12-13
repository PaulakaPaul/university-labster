package ro.ianders.universitylabsterremake;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ro.ianders.universitylabsterremake.datatypes.Course;
import ro.ianders.universitylabsterremake.datatypes.CourseData;
import ro.ianders.universitylabsterremake.datatypes.DatabaseConstants;
import ro.ianders.universitylabsterremake.datatypes.Professor;
import ro.ianders.universitylabsterremake.datatypes.Schedule;

public class MainActivityActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
               this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action

            //TODO delete dummy data
            Log.e("acronym", LabsterApplication.getInstace().getAcronym("POO"));

        } else if (id == R.id.nav_gallery) {

            // TODO delete debugging log.e info
            for(Course c : LabsterApplication.getInstace().getCourses())
                Log.e("main", c.toString());

        } else if (id == R.id.nav_slideshow) {

            // TODO delete dummy data
            Course c ;
            CourseData courseData = new CourseData("Structuri de Date si Algoritmi", "Parvan", 3, "AC", "CTI");
            Professor professor = new Professor("profesor", "email");
            Schedule schedule = new Schedule(new Date(), "10:00", "12:00");

            List<Professor> professors = new ArrayList<>();
            professors.add(professor);
            List<Schedule> schedules = new ArrayList<>();
            schedules.add(schedule);

            List<String> checkins  = new ArrayList<String>() {{
                add("marcel");
                add("dani");
            }};

            c = new Course(courseData, professors, checkins, schedules);

            LabsterApplication.getInstace().saveCourse(c);


        } else if (id == R.id.nav_manage) {


            // TODO delete dummy data
            List<String> checkins = new ArrayList<String>() {{
                add("Tudor");
                add("Marcela");
                add("Viorel");
                add("Grasanu");
            }};

            LabsterApplication.getInstace().saveCheckinsToACourse("Programarea Orientata pe Obiecte", checkins);

        } else if (id == R.id.nav_share) {

            // TODO delete dummy data
            List<Schedule> schedules = new ArrayList<>();
            schedules.add(new Schedule(new Date(), "13:00", "17:00"));
            schedules.add(new Schedule(new Date(), "15:00", "19:00"));

            LabsterApplication.getInstace().saveSchedulesToACourse("Structuri de Date si Algoritmi", schedules);

        } else if (id == R.id.nav_send) {

            // TODO delete dummy data
            List<Professor> professors = new ArrayList<>();
            professors.add(new Professor("prof1", "email1"));
            professors.add(new Professor("prof11"));

            LabsterApplication.getInstace().saveProfessorsToACourse("Arhitectura Calculatoarelor", professors);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
