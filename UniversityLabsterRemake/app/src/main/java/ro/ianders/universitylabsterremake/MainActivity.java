package ro.ianders.universitylabsterremake;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
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
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ro.ianders.universitylabsterremake.datatypes.Student;
import ro.ianders.universitylabsterremake.mainactivityfragments.CoursesFragment;
import ro.ianders.universitylabsterremake.mainactivityfragments.PendingCoursesFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private int clickListenerCounter = 3; //used to check if the user should go to the RegisterActivityFillData on the View.OnClickListener, if
    //the user passed the first 5 clicks without going to the other activity we wont waste more power to validate this


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        clickListenerCounter = 3;


        //TODO try to select the course item from the drawer so it will run the code from there (avoid duplication of code)
        FragmentManager fragmentManager = getSupportFragmentManager(); // when the app is opened we show the courses fragment
        CoursesFragment coursesFragment = new CoursesFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentPlaceHolder, coursesFragment)
                .commit();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
               this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //TODO add this line of code in a more appropriate place
        LabsterApplication.getInstace().updateDatesFromDatabase();

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

        checkForEmptyUserData();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();


        if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_courses) {

            CoursesFragment coursesFragment = new CoursesFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentPlaceHolder, coursesFragment)
                    .addToBackStack(null)
                    .commit();

        } else if (id == R.id.nav_pending_courses) {

            PendingCoursesFragment pendingCoursesFragment = new PendingCoursesFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentPlaceHolder, pendingCoursesFragment)
                    .addToBackStack(null)
                    .commit();

        } else if (id == R.id.nav_timetable) {



        }  else if (id == R.id.nav_sign_out) {
            //TODO added here the sign out logic

            Toast.makeText(this, "You are logged out!", Toast.LENGTH_SHORT).show();

            firebaseAuth.signOut();

            LoginManager.getInstance().logOut(); //logout from facebook

            // Configure Google Sign Out
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            mGoogleSignInClient.signOut();

            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        checkForEmptyUserData();

        return true;
    }


    @Override
    public void onClick(View view) { //from the View.OnClickListener interface

        checkForEmptyUserData(); // listened by all the clicks on the views

    }


    private void checkForEmptyUserData() { //send the user to fill his personal data if leaks on logic occur and data is still empty

        if(clickListenerCounter > 0) {
            Log.e("clickListenerCounter: ", clickListenerCounter + "");
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            if(currentUser != null)
                for(Student student : LabsterApplication.getInstace().getStudents())
                    if(student.getUserUID().equals(currentUser.getUid())) {
                        if (student.getProfile().getFirstName() == null) {
                            startActivity(new Intent(this, RegisterActivityFillData.class));
                            finish();
                        }
                        break;
                    }
                    clickListenerCounter--; //one less validation check
        }
    }


}
