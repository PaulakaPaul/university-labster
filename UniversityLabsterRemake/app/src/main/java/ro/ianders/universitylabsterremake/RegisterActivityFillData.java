package ro.ianders.universitylabsterremake;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import ro.ianders.universitylabsterremake.datatypes.Profile;
import ro.ianders.universitylabsterremake.datatypes.Student;

public class RegisterActivityFillData extends AppCompatActivity {

    private ScrollView svFillData;
    private ProgressBar pbRegisterFill;
    private AutoCompleteTextView etFirstName;
    private AutoCompleteTextView etLastName;
    private AutoCompleteTextView etFaculty;
    private AutoCompleteTextView etSection;
    private EditText etYear;
    private Button btnSaveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_fill_data);

        //getting references to UI elements
        svFillData = findViewById(R.id.svFillData);
        pbRegisterFill = findViewById(R.id.pbRegisterFill);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etFaculty = findViewById(R.id.etFaculty);
        etSection = findViewById(R.id.etSection);
        etYear = findViewById(R.id.etYear);
        btnSaveData = findViewById(R.id.btnSaveData);


        //set autocomplete adapter
        ArrayAdapter<String> faculties = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.faculty));
        etFaculty.setThreshold(1);
        etFaculty.setAdapter(faculties);

        ArrayAdapter<String> sections = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.section));
        etSection.setThreshold(1);
        etSection.setAdapter(sections);

        //save the user
        btnSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });

    }


    private void saveUser() {

        String email = getIntent().getStringExtra("email"); // data from the first step
        String password = getIntent().getStringExtra("password"); // data from the first step

        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String faculty = etFaculty.getText().toString().trim();
        String section = etSection.getText().toString().trim();
        String yearString = etYear.getText().toString().trim(); // we need it as a string to manipulate with other data (we know that
                                                                // are only digits because is a number edit text)

        if(TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(faculty) || TextUtils.isEmpty(section) || TextUtils.isEmpty(yearString)) {
            Toast.makeText(this, "Please fill with all the required data!", Toast.LENGTH_SHORT).show();
            return;
        }

        // if data is filled we proceed saving the user
        new SaveDataAsync().execute(faculty, section, yearString, password, firstName, lastName, email); // we start a background thread to save our data
    }




    private class SaveDataAsync extends AsyncTask<String, Void, Void> { // thread to save data to server

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbRegisterFill.setVisibility(View.VISIBLE);
            svFillData.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... strings) {

                // we know that in this vector are 6 elements because we validate it before(in the right order)
                int year = Integer.parseInt(strings[2]);
                Student student = new Student(strings[0], strings[1], year, strings[3], new Profile(strings[4], strings[5], strings[6]));
                LabsterApplication.getInstace().saveStudent(student);  //TODO implement reverse system for when the internet is turning off during the registration ( to have 100% data saved -> database and auth)

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pbRegisterFill.setVisibility(View.GONE);
            svFillData.setVisibility(View.VISIBLE);

            startActivity(new Intent(RegisterActivityFillData.this, MainActivity.class)); // we go to the main activity
            finish();

        }

    }
}
