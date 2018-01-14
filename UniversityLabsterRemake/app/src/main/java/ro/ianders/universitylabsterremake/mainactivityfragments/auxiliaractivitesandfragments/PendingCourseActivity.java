package ro.ianders.universitylabsterremake.mainactivityfragments.auxiliaractivitesandfragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ro.ianders.universitylabsterremake.LabsterApplication;
import ro.ianders.universitylabsterremake.R;
import ro.ianders.universitylabsterremake.datatypes.ActivityCourse;
import ro.ianders.universitylabsterremake.datatypes.Course;
import ro.ianders.universitylabsterremake.datatypes.PendingActivityCourse;
import ro.ianders.universitylabsterremake.datatypes.PendingCourse;
import ro.ianders.universitylabsterremake.datatypes.PendingListData;
import ro.ianders.universitylabsterremake.datatypes.Professor;
import ro.ianders.universitylabsterremake.datatypes.Schedule;
import ro.ianders.universitylabsterremake.datatypes.Student;

public class PendingCourseActivity extends AppCompatActivity {

    private TextView tvFullName;
    private TextView tvValidate;
    private TextView tvExtraInfo; //when you click the tvFullName we will the need numbers of validations and vice versa
    private TextView tvNumberOfValidations;
    private Toolbar tbCourse;

    private Course currentHour;
    private PendingListData pendingListData;
    private boolean isExtraInfoOnBottom; // we control the state of the info in the bottom of the activity with this boolean
    private boolean resultOkOnBackButtonpressed; //when we get out of the activity with the back button we have to control what result we sent (true if a validation occurred)
                                                // we update the view only if the current student can validate (every user validates only once)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_course);

        isExtraInfoOnBottom = true; // we start with the normal extra info
        resultOkOnBackButtonpressed = false; // by default no validation occurred

        tvFullName = findViewById(R.id.tvFullName);
        tvValidate = findViewById(R.id.tvValidate);
        tvExtraInfo = findViewById(R.id.tvExtraInfo);
        tbCourse = findViewById(R.id.tbCourse);
        tvNumberOfValidations = findViewById(R.id.tvNumberOfValidations);

        pendingListData = getIntent().getParcelableExtra("data");

        //setting the toolbar
        setSupportActionBar(tbCourse);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // we active the default back button
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            // getSupportActionBar().setDisplayShowHomeEnabled(true);

            int type = pendingListData.getType();

            if(type == R.drawable.course) {
                getSupportActionBar().setTitle(R.string.course);
            }
            else if(type == R.drawable.laboratory) {
                getSupportActionBar().setTitle(R.string.laboratory);
            }
            else if(type == R.drawable.seminary){
                getSupportActionBar().setTitle(R.string.seminary);
            }

        }

        //setting background
        LinearLayout linCourseMain = findViewById(R.id.linCourseMain);
        linCourseMain.setBackgroundColor(pendingListData.getColor());

        Student currentStudent = LabsterApplication.getCurrentStudent(); // getting current Student for querying data
        currentHour = getCurrentHour(currentStudent.getYear(), currentStudent.getFaculty(), currentStudent.getSection(), pendingListData.getType(), pendingListData.getName());
        // getting current hour to show the data


        //setting name
        String[] name = currentHour.getCourseData().getNameCourse().split(" "); //putting every word on a different line
        String newName = "";
        int i = 0;
        for(String s : name) { // we put the '\n' after 2 words
            if(i == 2) {
                newName += s + "\n";
                i = 0;
            }  else {
                newName += s + " ";
                i++;
            }
        }
        if(newName.charAt(newName.length() - 1) == '\n') // if the last char is '\n' remove it
            newName = newName.substring(0, newName.length() - 1);
        tvFullName.setText(newName);

        tvFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchBottomData();
            }
        });


        //setting listener for the validate text view
        tvValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        //setting tv number of validations
        tvNumberOfValidations.setText(String.format("%d",PendingCourse.NUMBER_OF_VALIDATIONS - pendingListData.getNumberOfValidations()));

        tvExtraInfo.setText(generateExtraInfo());

    }


    private void resultForPendingFragment() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("position", pendingListData.getPosition()); // we sent the position so we know what item to update
        setResult(Activity.RESULT_OK, resultIntent);
    }

    private void validate() {

        if(currentHour instanceof PendingCourse) {
            if (!((PendingCourse) currentHour).getValidations().contains(LabsterApplication.getCurrentStudent())) {

                if(PendingCourse.NUMBER_OF_VALIDATIONS - ((PendingCourse) currentHour).getValidations().size() == 1) { // it is validated so we delete the course from the pendings and add it to the normal ones

                    LabsterApplication.getInstace().removePendingCourse((PendingCourse) currentHour);
                    ((PendingCourse) currentHour).removeAllValidations(); // we save it as a Course so we don't need  the validations to be saved in the database
                    LabsterApplication.getInstace().saveCourse(currentHour, false); // save it to the validated data


                    //we finish the activity
                    resultForPendingFragment();
                    finish();

                } else { // we update the view and wait for more validations !!!
                    LabsterApplication.getInstace().addValidationToPendingCourse((PendingCourse) currentHour, LabsterApplication.getCurrentStudent());
                    ((PendingCourse) currentHour).addValidation(LabsterApplication.getCurrentStudent());
                    Toast.makeText(this, "Validated!", Toast.LENGTH_SHORT).show();
                    tvNumberOfValidations.setText(String.format("%d", PendingCourse.NUMBER_OF_VALIDATIONS - ((PendingCourse) currentHour).getValidations().size()));
                }

                resultOkOnBackButtonpressed = true;

            } else {
                setResult(Activity.RESULT_CANCELED);
                Toast.makeText(this, "Sorry, you already validated!", Toast.LENGTH_SHORT).show();
            }
        } else if(currentHour instanceof PendingActivityCourse) {
            if (!((PendingActivityCourse) currentHour).getValidations().contains(LabsterApplication.getCurrentStudent())) {

                if(PendingCourse.NUMBER_OF_VALIDATIONS - ((PendingActivityCourse) currentHour).getValidations().size() == 1) { // it is validated so we delete the course from the pendings and add it to the normal ones
                    LabsterApplication.getInstace().removePendingActivityCourse((PendingActivityCourse) currentHour);
                    ((PendingActivityCourse) currentHour).removeAllValidations(); // we save it as a ActivityCourse so we don't need  the validations to be saved in the database
                    LabsterApplication.getInstace().saveActivityCourse((ActivityCourse) currentHour, false); // save it to the validated data


                    //we finish the activity
                    resultForPendingFragment();
                    finish();

                } else { // we update the view and wait for more validations !!!

                    LabsterApplication.getInstace().addValidationToPendingActivityCourse((PendingActivityCourse) currentHour, LabsterApplication.getCurrentStudent());
                    ((PendingActivityCourse) currentHour).addValidation(LabsterApplication.getCurrentStudent());
                    Toast.makeText(this, "Validated!", Toast.LENGTH_SHORT).show();
                    tvNumberOfValidations.setText(String.format("%d", PendingCourse.NUMBER_OF_VALIDATIONS - ((PendingActivityCourse) currentHour).getValidations().size()));
                }

                resultOkOnBackButtonpressed = true;
            } else {
                setResult(Activity.RESULT_CANCELED);
                Toast.makeText(this, "Sorry, you already validated!", Toast.LENGTH_SHORT).show();
            }
        }

        if(resultOkOnBackButtonpressed) {
            resultForPendingFragment();

            if(!isExtraInfoOnBottom) //we have the validations opened -> we update the view
                    tvExtraInfo.setText(generateValidations()); // we recreate the view in the activity

        } else {
            setResult(Activity.RESULT_CANCELED);
            Toast.makeText(this, "Sorry, you already validated!", Toast.LENGTH_SHORT).show();
        }

    }

    private void switchBottomData() {
        if(isExtraInfoOnBottom) {
            tvExtraInfo.setText(generateValidations());
            isExtraInfoOnBottom = false;
        } else {
            tvExtraInfo.setText(generateExtraInfo());
            isExtraInfoOnBottom = true;
        }
    }

    private String generateExtraInfo() {

        StringBuilder text = new StringBuilder();

        text.append("faculty: ").append(currentHour.getCourseData().getFaculty()).append("\n");
        text.append("section: ").append(currentHour.getCourseData().getSection()).append("\n");
        text.append("year: ").append(currentHour.getCourseData().getYear()).append("\n");
        text.append("Location: ").append(currentHour.getCourseData().getLocation()).append("\n");

        int i = 1;
        text.append("\nProfessors: ").append("\n");
        for(Professor p : currentHour.getProfessors()) {
            text.append(i).append(". ").append(p.getName());

            if(p.getEmail() != null)
                text.append("\n").append("email: ").append(p.getEmail());

            text.append("\n");
        }


        i = 1;
        text.append("\nSchedules: ").append("\n");
        for(Schedule s: currentHour.getSchedules()) {
            text.append(i).append(". ").append(s.getDate()).append(" with step: ").append(s.getStep()).append("\n Hours: ")
                    .append(s.getStartTime()).append(" - ").append(s.getEndTime()).append("\n");
        }

        text.delete(text.length() - 1, text.length()); // we delete the last \n

        return text.toString();

    }

    private String generateValidations() {

        StringBuilder s = new StringBuilder();
        int i = 1;

        s.append("Maximum ").append(PendingCourse.NUMBER_OF_VALIDATIONS).append(" validations:\n");

        if(currentHour instanceof PendingCourse)
            for(Student student : ((PendingCourse) currentHour).getValidations()) {
                s.append(i).append(". ").append(student.getProfile().getLastName()).append(" ").append(student.getProfile().getFirstName());
                i++;
            }
        else if(currentHour instanceof PendingActivityCourse)
            for(Student student : ((PendingActivityCourse) currentHour).getValidations()) {
                s.append(i).append(". ").append(student.getProfile().getLastName()).append(" ").append(student.getProfile().getFirstName());
                i++;
            }

        return s.toString();
    }

    //type is a reference to the pictures that define every type of hour (we use the information from the parameters to find that info)
    private Course getCurrentHour(int year, @NonNull String faculty, @NonNull String section, int type, @NonNull String name) {

        Course currentCourse = null;

        if(type == R.drawable.course) {

            for(Course c : LabsterApplication.getInstace().getPendingCourses())
                if((c.getCourseData().getYear() == year) & (c.getCourseData().getFaculty().equalsIgnoreCase(faculty)) &
                        (c.getCourseData().getSection().equalsIgnoreCase(section)) & (c.getCourseData().getNameCourse().equalsIgnoreCase(name))) {
                    currentCourse = c;
                    break;
                }
        } else {

            char charType = type == R.drawable.laboratory ? 'l' : 's';

            for(ActivityCourse c : LabsterApplication.getInstace().getPendingActivityCourses())
                if((c.getCourseData().getYear() == year) & (c.getCourseData().getFaculty().equalsIgnoreCase(faculty)) &
                        (c.getCourseData().getSection().equalsIgnoreCase(section)) & (c.getCourseData().getNameCourse().equalsIgnoreCase(name))
                        & (c.getType().charAt(0) == charType) ) {
                    currentCourse = c;
                    break;
                }
        }
        return currentCourse;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_course_menu, menu);

        MenuItem courseRefresh = menu.findItem(R.id.course_refresh);
        courseRefresh.setVisible(false); //we don't need the refresh button in this case
        MenuItem addToCalendar = menu.findItem(R.id.course_add_to_calendar);
        addToCalendar.setVisible(false); // we don't need this in this activity

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.course_switch_data) {
            switchBottomData();
            return true;
        }  else if (id == android.R.id.home) { // this is for the back button

            if(!resultOkOnBackButtonpressed) //if no validation occurred there is no need to update the list
                setResult(Activity.RESULT_CANCELED);

            finish();
            return true;
        } else if(id == R.id.course_show_location) {
            String courseLocation = currentHour.getCourseData().getLocation();
            if(!courseLocation.contains("Timisoara")) courseLocation += ", Timisoara"; //add all the necessary data
            if(!courseLocation.contains("Romania")) courseLocation += ", Romania";

            LabsterApplication.getInstace().showLocationOnGoogleMaps(courseLocation);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
