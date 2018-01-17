package ro.ianders.universitylabsterremake;

import android.app.Activity;
import android.app.Notification;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import ro.ianders.universitylabsterremake.mainactivityfragments.ProfileFragment;
import ro.ianders.universitylabsterremake.mainactivityfragments.TimetableFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ChangeProfilePicHeader {

    private ImageView ivCurrentUser;
    private TextView tvNameCurrentUser;
    private TextView tvEmailCurrentUser;
    private ProgressBar pbLoadData;

    private FirebaseAuth firebaseAuth;

    private int clickListenerCounter = 3; //used to check if the user should go to the RegisterActivityFillData on the View.OnClickListener, if
    //the user passed the first 5 clicks without going to the other activity we wont waste more power to validate this


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        pbLoadData = findViewById(R.id.pbLoadData);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        clickListenerCounter = 3;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // adds the navigation view button on the toolbar (top left side)
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
               this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //references for the from the navigation view header
        View headerView = navigationView.getHeaderView(0);
        ivCurrentUser = headerView.findViewById(R.id.ivCurrentUser);
        tvNameCurrentUser = headerView.findViewById(R.id.tvNameCurrentUser);
        tvEmailCurrentUser = headerView.findViewById(R.id.tvEmailCurrentUser);
       // populateHeader(); -> moved this to PopulateAsyncTask()

       // onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_courses)); // we select at start the course fragment -> we select it from
        // PopulateAsyncTask()
        navigationView.setNavigationItemSelectedListener(this);

        new PopulateAsyncTask().execute(navigationView); // it populated the view when we load up all the data from the
        //database AND after the data is loaded it calls the util method that increments the dates and clears the check-ins

    }

    // Called when the activity has detected the user's press of the back key.
    // The default implementation simply finishes the current activity, but you can override this to do whatever you want.
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
            Toast.makeText(this, "Hello there, no settings yet!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();


        if (id == R.id.nav_profile) {

            ProfileFragment profileFragment = new ProfileFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentPlaceHolder, profileFragment)
                    .addToBackStack(null)
                    .commit();

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


            TimetableFragment timetableFragment = new TimetableFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentPlaceHolder, timetableFragment)
                    .addToBackStack(null)
                    .commit();


        }  else if (id == R.id.nav_sign_out) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    private void signOut() {
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

    private void populateHeader() {

        Student student = LabsterApplication.getCurrentStudent();

        if(student!= null) {
            if(student.getProfile().getFirstName() != null)
                tvNameCurrentUser.setText(String.format("%s %s", student.getProfile().getFirstName(), student.getProfile().getLastName()));

            if(student.getProfile().getPicture() != null)
                ivCurrentUser.setImageBitmap(ProfileFragment.decodeBitmapFromString(student.getProfile().getPicture())); //profile pictured
                // stored as a String
            else
                ivCurrentUser.setImageResource(R.mipmap.uptlogo); // default view
        }

        if(firebaseAuth.getCurrentUser() != null)
            tvEmailCurrentUser.setText(firebaseAuth.getCurrentUser().getEmail());

    }

    private class PopulateAsyncTask extends AsyncTask<NavigationView, Void, NavigationView> { // it populated the view when we load up all the data from the
        //database AND after the data is loaded it calls the util method that increments the dates and clears the check-ins


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoadData.setVisibility(View.VISIBLE);
        }

        @Override
        protected NavigationView doInBackground(NavigationView... v) {
            while (LabsterApplication.getInstace().getCourses().size() == 0 || LabsterApplication.getInstace().getActivities().size() == 0
                    || LabsterApplication.getInstace().getStudents().size() == 0 );

            return v[0]; // the navigationView to be called upon selecting the right view
        }

        @Override
        protected void onPostExecute(NavigationView navigationView) {
            super.onPostExecute(navigationView);
            checkForEmptyUserData(); // sends the user to fill it's data if it is not filled otherwise continue
            LabsterApplication.getInstace().updateDatesFromDatabase(); // we update the dates from the courses and activity courses and clear the check-ins
            populateHeader(); // we populate the header after the data is loaded in the system
            pbLoadData.setVisibility(View.GONE);
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_courses)); // we select at start the course fragment
        }
    }


    @Override
    public void changePic(Bitmap bitmap) {
        ivCurrentUser.setImageBitmap(bitmap);
    }
}

