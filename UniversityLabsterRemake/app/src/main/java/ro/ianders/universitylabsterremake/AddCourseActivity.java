package ro.ianders.universitylabsterremake;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ro.ianders.universitylabsterremake.datatypes.ActivityCourse;
import ro.ianders.universitylabsterremake.datatypes.Course;
import ro.ianders.universitylabsterremake.datatypes.CourseData;
import ro.ianders.universitylabsterremake.datatypes.PendingActivityCourse;
import ro.ianders.universitylabsterremake.datatypes.PendingCourse;
import ro.ianders.universitylabsterremake.datatypes.Professor;
import ro.ianders.universitylabsterremake.datatypes.Schedule;


public class AddCourseActivity extends AppCompatActivity {


    //buttons to control the logic
    private Button btnContinue1;
    private Button btnContinue2;
    private Button btnSaveCourse;

    //references from the first fragment
    private AutoCompleteTextView etFaculty;
    private AutoCompleteTextView etLocation;
    private AutoCompleteTextView etNameAddCourse;
    private AutoCompleteTextView etSectionAddCourse;
    private EditText etYear;
    private Spinner spinnerType;
    private boolean isSpinnerDataOk; // we use this to check in further logic if the Spinner Data Is OK
    private EditText etNumberOfProfessors;
    private EditText etNumberOfSchedules;

    //data from the first fragment
    private String faculty;
    private String location;
    private String nameCourse;
    private String section;
    private String year;
    private String type;
    private String numberOfProfessors;
    private String numberOfSchedules;

    //references from the second fragment
    private LinearLayout linAddProfessors;

    //data from the second fragment
    private List<Professor> professors;

    //references from the third fragment
    private LinearLayout linAddSchedule;

    //data from the third fragment
    private List<Schedule> schedules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        //toolbar setup
        Toolbar tbAddCourse = findViewById(R.id.tbAddCourse);
        setSupportActionBar(tbAddCourse);
        tbAddCourse.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        isSpinnerDataOk = false; // by default it is not ok

        //allocate memory for the data lists
        professors = new ArrayList<>();
        schedules = new ArrayList<>();

        //references for the buttons that control the logic flow
        btnContinue1 = findViewById(R.id.btnContinue1);
        btnContinue2 = findViewById(R.id.btnContinue2);
        btnSaveCourse = findViewById(R.id.btnSaveCourse);

        //references from the first fragment
        etFaculty = findViewById(R.id.etFacultyAddCourse);
        etLocation = findViewById(R.id.etLocationAddCourse);
        etNameAddCourse = findViewById(R.id.etNameAddCourse);
        etSectionAddCourse = findViewById(R.id.etSectionAddCourse);
        etYear = findViewById(R.id.etYearAddCourse);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // this is a special case so we put this listener here
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String typeInListener;
                typeInListener = parent.getItemAtPosition(position).toString();
                if(typeInListener.equals(getResources().getStringArray(R.array.types)[0])) { // this is : Choose type which is not a type , we use
                    // this just like a hint
                    Toast.makeText(AddCourseActivity.this, "Please " + getResources().getStringArray(R.array.types)[0].toLowerCase(),
                            Toast.LENGTH_SHORT).show();
                    isSpinnerDataOk = false; // we don't continue with this data
                } else { // we pass the data to the logic system
                    type = typeInListener.toLowerCase(); // we need to store the data all in lowercase
                    isSpinnerDataOk = true;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        etNumberOfProfessors = findViewById(R.id.etNumberOfProfessors);
        etNumberOfSchedules = findViewById(R.id.etNumberOfSchedules);

        //references from the second fragment
        linAddProfessors = findViewById(R.id.linAddProfessors);

        //references from the third fragment
        linAddSchedule = findViewById(R.id.linAddSchedule);

        //background thread to generate arrays for autocomplete
        new PopulateAutocompleteArrays().execute();

        FragmentManager fragmentManager = getSupportFragmentManager();

        /*fragmentManager.beginTransaction()
                .show(fragmentManager.findFragmentById(R.id.fragAddCourseFirst))
                .hide(fragmentManager.findFragmentById(R.id.fragAddCourseProfessor))
                .hide(fragmentManager.findFragmentById(R.id.fragAddCourseSchedule))
                .commit();*/

        // we use this to keep the references to the views created programmatically
        List<LinearLayout> professorReferences = new ArrayList<>();
        List<LinearLayout> scheduleReferences = new ArrayList<>();


        btnContinue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(grabFirstFragData()) { //if data is correct we continue

                    //we create dynamically the view to add the professors
                    for(int i = 1 ; i <= Integer.parseInt(numberOfProfessors); i ++) {
                        LinearLayout linProfessor = (LinearLayout) View.inflate(AddCourseActivity.this, R.layout.professor_for_fragment, null);
                        professorReferences.add(linProfessor); // we need to keep the references so we can get the data from the view

                        ((TextView) linProfessor.findViewById(R.id.etNumberOfProfessor)).setText( // setting the text view
                                String.format("%s  ->  %d", getResources().getString(R.string.add_proffesor), i));

                        linAddProfessors.addView(linProfessor); //adding it to the fragment

                    }

                    fragmentManager.beginTransaction()
                            .hide(fragmentManager.findFragmentById(R.id.fragAddCourseFirst))
                            .show(fragmentManager.findFragmentById(R.id.fragAddCourseProfessor))
                            .hide(fragmentManager.findFragmentById(R.id.fragAddCourseSchedule))
                            .commit();
                }

            }
        });

        btnContinue2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(grabSecondSetOfData(professorReferences)) {

                    //we create dynamically the view to add the schedules

                    for(int i = 1 ; i <= Integer.parseInt(numberOfSchedules); i++) {
                        LinearLayout linSchedules = (LinearLayout) View.inflate(AddCourseActivity.this, R.layout.schedule_for_fragment, null);
                        scheduleReferences.add(linSchedules); // we need to keep the references so we can get the data from the view

                        ((TextView) linSchedules.findViewById(R.id.tvNumberSchedule)).setText( // setting the text view
                                String.format("%s  ->  %d", getResources().getString(R.string.add_schedule), i));

                        linAddSchedule.addView(linSchedules); //adding it to the fragment
                    }

                    fragmentManager.beginTransaction()
                            .hide(fragmentManager.findFragmentById(R.id.fragAddCourseFirst))
                            .hide(fragmentManager.findFragmentById(R.id.fragAddCourseProfessor))
                            .show(fragmentManager.findFragmentById(R.id.fragAddCourseSchedule))
                            .commit();
                }
            }
        });

        btnSaveCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(grabThirdSetOfData(scheduleReferences)) {// if everything is ok with the last part of the data we save the course to the database
                    saveData();
                    Toast.makeText(AddCourseActivity.this, "Saved successfully!", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK); // result to update the view on the pending recycler list fragment
                    finish(); // this is the last phase so we finish the activity
                }

            }
        });
    }

    private boolean grabFirstFragData() { //returns true if everything is ok
        faculty = etFaculty.getText().toString().trim();
        location = etLocation.getText().toString().trim();
        nameCourse = etNameAddCourse.getText().toString().trim();
        section = etSectionAddCourse.getText().toString().trim();
        year = etYear.getText().toString().trim();
        // the spinnerType has it's own listener
        numberOfProfessors = etNumberOfProfessors.getText().toString().trim();
        numberOfSchedules =  etNumberOfSchedules.getText().toString().trim();

        if(!isSpinnerDataOk) {
            Toast.makeText(AddCourseActivity.this, "Please " + getResources().getStringArray(R.array.types)[0].toLowerCase(),
                    Toast.LENGTH_SHORT).show();
            return  false;
        }


        if(TextUtils.isEmpty(faculty) || TextUtils.isEmpty(location) || TextUtils.isEmpty(nameCourse) || TextUtils.isEmpty(section) ||
                TextUtils.isEmpty(year) || TextUtils.isEmpty(type) || TextUtils.isEmpty(numberOfProfessors) || TextUtils.isEmpty(numberOfSchedules)) {
            Toast.makeText(this, "Please fill all the data to continue!", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    private boolean grabSecondSetOfData(List<LinearLayout> linearLayouts) {

        int i = 1;
        for(LinearLayout linearLayout : linearLayouts) {

            String professorName = ((EditText) linearLayout.findViewById(R.id.etProfessorName)).getText().toString().trim();
            String professorEmail = ((EditText) linearLayout.findViewById(R.id.etProfessorEmail)).getText().toString().trim();


            if(TextUtils.isEmpty(professorName) || TextUtils.isEmpty(professorEmail)) {
                Toast.makeText(this, "Please fill the data!", Toast.LENGTH_SHORT).show();
                return false;
            }


            String emailPattern = "(.+)@(.+\\.)(.+)";
            Pattern pattern = Pattern.compile(emailPattern);
            Matcher matcher = pattern.matcher(professorEmail);

            if(!matcher.matches()) {
                Toast.makeText(this, "Professor " + i + " does not have a valid email!", Toast.LENGTH_SHORT).show();
                return false;
            }


            Professor professor = new Professor(professorName, professorEmail);
            professors.add(professor);
            i++;
        }


        return true;
    }

    private boolean grabThirdSetOfData(List<LinearLayout> linearLayouts) {

        for(LinearLayout linearLayout : linearLayouts) {

            String date = ((EditText) linearLayout.findViewById(R.id.etDate)).getText().toString().trim();
            String startTime = ((EditText) linearLayout.findViewById(R.id.etStartTme)).getText().toString().trim();
            String endTime = ((EditText) linearLayout.findViewById(R.id.etEndTime)).getText().toString().trim();
            String step = ((EditText) linearLayout.findViewById(R.id.etStep)).getText().toString().trim();


            if(TextUtils.isEmpty(date) || TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime) || TextUtils.isEmpty(step)) {
                Toast.makeText(this, "Please fill the data", Toast.LENGTH_SHORT).show();
                return false;
            }


            //test for correct data format
            String datePattern = "([0-3][0-9]/)([0-1][0-9]/)(20[1-4][0-9])";
            Pattern pattern =  Pattern.compile(datePattern);
            Matcher matcher = pattern.matcher(date);

            if(!matcher.matches()) {
                Toast.makeText(this, "Wrong date format!", Toast.LENGTH_SHORT).show();
                return false;
            }


            // test for correct date value
            String[] dates = date.split("/");
            if(Integer.parseInt(dates[0]) > 31 || Integer.parseInt(dates[1]) > 12) {
                Toast.makeText(this, "Wrong date format!!!", Toast.LENGTH_SHORT).show();
                return false;
            }


            //test for correct time form
            String timePattern = "([0-1][0-9]):(00)";
            pattern = Pattern.compile(timePattern);
            matcher = pattern.matcher(startTime);

            if(!matcher.matches()) {
                Toast.makeText(this, "Wrong start time format!", Toast.LENGTH_SHORT).show();
                return false;
            }


            matcher = pattern.matcher(endTime);
            if(!matcher.matches()) {
                Toast.makeText(this, "Wrong end time format!", Toast.LENGTH_SHORT).show();
                return false;
            }

            // only 2 hours length courses are allowed
            int intStartTime = Integer.parseInt(startTime.split(":")[0]);
            int intEndTime = Integer.parseInt(endTime.split(":")[0]);
            if((intEndTime - intStartTime) != 2 ) {
                Toast.makeText(this, "Length of an hour is of 2 hours, if you want a special case write this to the special course info section", Toast.LENGTH_SHORT).show();
                return false;
            }

            Schedule schedule = new Schedule(date, startTime, endTime, Integer.parseInt(step));
            schedules.add(schedule);

        }

        return true;
    }

    private void saveData() { // save as a pending course or activity course
        CourseData courseData = new CourseData(nameCourse, location, Integer.parseInt(year), faculty, section);
        if(type.equalsIgnoreCase("course")) {
            PendingCourse pendingCourse = new PendingCourse(courseData, professors, schedules);
            LabsterApplication.getInstace().addDynamicallyPendingCourse(pendingCourse); // so we can update the the pending list view when we go back
            LabsterApplication.getInstace().savePendingCourse(pendingCourse);
        } else {
            PendingActivityCourse pendingActivityCourse = new PendingActivityCourse(type, courseData, professors, schedules);
            LabsterApplication.getInstace().addDynamicallyActivityPendingCourse(pendingActivityCourse); // so we can update the the pending list view when we go back
            LabsterApplication.getInstace().savePendingActivityCourse(pendingActivityCourse);
        }
    }


    //background thread to generate arrays for autocomplete
    private class PopulateAutocompleteArrays extends AsyncTask<Void, Void, Map<String, List<String>> > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.pbAddCourse).setVisibility(View.VISIBLE);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .hide(fragmentManager.findFragmentById(R.id.fragAddCourseFirst))
                    .hide(fragmentManager.findFragmentById(R.id.fragAddCourseProfessor))
                    .hide(fragmentManager.findFragmentById(R.id.fragAddCourseSchedule))
                    .commit();
        }



        @Override
        protected Map<String, List<String>> doInBackground(Void... voids) {

            Map<String, List<String>> map = new HashMap<>();


            ArrayList<String> courseNames = new ArrayList<>();
            ArrayList<String> sections = new ArrayList<>();
            ArrayList<String> facultys = new ArrayList<>();
            ArrayList<String> locations = new ArrayList<>();



            for(Course c : LabsterApplication.getInstace().getCourses()) {

                String name =  c.getCourseData().getNameCourse();
                if(!courseNames.contains(name) & name != null)
                    courseNames.add(name);

                String s = c.getCourseData().getSection();
                if(!sections.contains(s) & s != null)
                    sections.add(s);

                String faculty = c.getCourseData().getFaculty();
                if(!facultys.contains(faculty) & faculty != null)
                    facultys.add(faculty);

                String location = c.getCourseData().getLocation();
                if(!locations.contains(location) & location != null)
                    locations.add(location);
            }

            for(ActivityCourse c : LabsterApplication.getInstace().getActivities()) {

                String name =  c.getCourseData().getNameCourse();
                if(!courseNames.contains(name) & name != null)
                    courseNames.add(name);

                String s = c.getCourseData().getSection();
                if(!sections.contains(s) & s != null)
                    sections.add(s);

                String faculty = c.getCourseData().getFaculty();
                if(!facultys.contains(faculty) & faculty != null)
                    facultys.add(faculty);

                String location = c.getCourseData().getLocation();
                if(!locations.contains(location) & location != null)
                    locations.add(location);
            }


            map.put("courseNames", courseNames);
            map.put("sections", sections);
            map.put("faculties", facultys);
            map.put("locations", locations);


            return map;
        }

        @Override
        protected void onPostExecute(Map<String, List<String>> stringArrayListMap) {
            super.onPostExecute(stringArrayListMap);


            //setting adapters
            ArrayAdapter<String> sectionsAdapter = new ArrayAdapter<String>(AddCourseActivity.this, android.R.layout.simple_dropdown_item_1line, stringArrayListMap.get("sections"));
            etSectionAddCourse.setThreshold(2);
            etSectionAddCourse.setAdapter(sectionsAdapter);

            ArrayAdapter<String> facultiesAdapter = new ArrayAdapter<String>(AddCourseActivity.this, android.R.layout.simple_dropdown_item_1line, stringArrayListMap.get("faculties"));
            etFaculty.setThreshold(2);
            etFaculty.setAdapter(facultiesAdapter);

            ArrayAdapter<String> locationsAdapter = new ArrayAdapter<String>(AddCourseActivity.this, android.R.layout.simple_dropdown_item_1line, stringArrayListMap.get("locations"));
            etLocation.setThreshold(2);
            etLocation.setAdapter(locationsAdapter);


            ArrayAdapter<String> courseNamesAdapter = new ArrayAdapter<String>(AddCourseActivity.this, android.R.layout.simple_dropdown_item_1line, stringArrayListMap.get("courseNames"));
            etNameAddCourse.setThreshold(2);
            etNameAddCourse.setAdapter(courseNamesAdapter);


            findViewById(R.id.pbAddCourse).setVisibility(View.GONE);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .show(fragmentManager.findFragmentById(R.id.fragAddCourseFirst))
                    .hide(fragmentManager.findFragmentById(R.id.fragAddCourseProfessor))
                    .hide(fragmentManager.findFragmentById(R.id.fragAddCourseSchedule))
                    .commit();
        }

    }

}
