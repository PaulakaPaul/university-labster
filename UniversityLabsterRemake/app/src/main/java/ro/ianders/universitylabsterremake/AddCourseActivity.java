package ro.ianders.universitylabsterremake;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.ianders.universitylabsterremake.datatypes.ActivityCourse;
import ro.ianders.universitylabsterremake.datatypes.Course;
import ro.ianders.universitylabsterremake.datatypes.CourseData;
import ro.ianders.universitylabsterremake.datatypes.PendingActivityCourse;
import ro.ianders.universitylabsterremake.datatypes.PendingCourse;
import ro.ianders.universitylabsterremake.datatypes.Professor;
import ro.ianders.universitylabsterremake.datatypes.Schedule;
import ro.ianders.universitylabsterremake.mainactivityfragments.AddCourseProfessorFragment;


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
    private AutoCompleteTextView etType;
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


        Toolbar tbAddCourse = findViewById(R.id.tbAddCourse);
        setSupportActionBar(tbAddCourse);
        tbAddCourse.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
        etType = findViewById(R.id.etTypeAddCourse);
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
        type = etType.getText().toString().trim().toLowerCase();
        numberOfProfessors = etNumberOfProfessors.getText().toString().trim();
        numberOfSchedules =  etNumberOfSchedules.getText().toString().trim();

        if(TextUtils.isEmpty(faculty) || TextUtils.isEmpty(location) || TextUtils.isEmpty(nameCourse) || TextUtils.isEmpty(section) ||
                TextUtils.isEmpty(year) || TextUtils.isEmpty(type) || TextUtils.isEmpty(numberOfProfessors) || TextUtils.isEmpty(numberOfSchedules)) {
            Toast.makeText(this, "Please fill all the data to continue!", Toast.LENGTH_SHORT).show();
            return false;
        }

        //TODO more data validation

        //type validation
        List<String> types = Arrays.asList(getResources().getStringArray(R.array.types));
        for(int i  = 0 ; i < types.size() ; i++) //we want to compare the data only with lower case
            types.set(i, types.get(i).toLowerCase());


        if(!types.contains(type)) {
            Toast.makeText(this, "Please insert a correct form of type!", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    private boolean grabSecondSetOfData(List<LinearLayout> linearLayouts) {

        for(LinearLayout linearLayout : linearLayouts) {

            String professorName = ((EditText) linearLayout.findViewById(R.id.etProfessorName)).getText().toString().trim();
            String professorEmail = ((EditText) linearLayout.findViewById(R.id.etProfessorEmail)).getText().toString().trim();

            //TODO more data validation

            if(TextUtils.isEmpty(professorName) || TextUtils.isEmpty(professorEmail)) {
                Toast.makeText(this, "Please fill the data!", Toast.LENGTH_SHORT).show();
                return false;
            }

            Professor professor = new Professor(professorName, professorEmail);
            professors.add(professor);
        }


        return true;
    }

    private boolean grabThirdSetOfData(List<LinearLayout> linearLayouts) {

        for(LinearLayout linearLayout : linearLayouts) {

            String date = ((EditText) linearLayout.findViewById(R.id.etDate)).getText().toString().trim();
            String startTime = ((EditText) linearLayout.findViewById(R.id.etStartTme)).getText().toString().trim();
            String endTime = ((EditText) linearLayout.findViewById(R.id.etEndTime)).getText().toString().trim();
            String step = ((EditText) linearLayout.findViewById(R.id.etStep)).getText().toString().trim();

            //TODO more data validation

            if(TextUtils.isEmpty(date) || TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime) || TextUtils.isEmpty(step)) {
                Toast.makeText(this, "Please fill the data", Toast.LENGTH_SHORT).show();
                return false;
            }

            //test for correct data format
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                simpleDateFormat.parse(date);
            } catch (ParseException e) {
                Toast.makeText(this, "Wrong date format!", Toast.LENGTH_SHORT).show();
                return false;
            }

            //test for correct data length
            String[] dates = date.split("/");
            for(int i = 0 ; i < dates.length ; i++)
                if(dates[i].length() != 2 && i < 2) {
                    Toast.makeText(this, "Wrong date format!!", Toast.LENGTH_SHORT).show();
                    return false;
                }

            //test for correct data length
            if (dates[2].length() != 4) {
                Toast.makeText(this, "Wrong date format!!", Toast.LENGTH_SHORT).show();
                return false;
            }

            // test for correct date value
            if(Integer.parseInt(dates[0]) > 31 || Integer.parseInt(dates[1]) > 12) {
                Toast.makeText(this, "Wrong date format!!!", Toast.LENGTH_SHORT).show();
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
            List<String> types = Arrays.asList(getResources().getStringArray(R.array.types));


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
            map.put("types", types);

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

            ArrayAdapter<String> typesAdapter = new ArrayAdapter<String>(AddCourseActivity.this, android.R.layout.simple_dropdown_item_1line, stringArrayListMap.get("types"));
            etType.setThreshold(1);
            etType.setAdapter(typesAdapter);

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
