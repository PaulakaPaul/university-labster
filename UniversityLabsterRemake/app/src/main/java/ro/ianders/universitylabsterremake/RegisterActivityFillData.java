package ro.ianders.universitylabsterremake;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ro.ianders.universitylabsterremake.datatypes.LabsterConstants;
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

        askForPermissionContacts(); // implemented in this activity


        //save the user
        btnSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser(); // implemented in this activity
                startActivity(new Intent(RegisterActivityFillData.this, MainActivity.class)); // we go to the main activity
                finish(); // after registration go to the main activity
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        new PopulateAutocompleteArrays().execute(); //we start a background thread to generate autocomplete arrays and adapters

    }


    //permission for querying contacts
    private void askForPermissionContacts() {

            // if the user doesn't checks "Never ask again" in the dialog
            if (ContextCompat.checkSelfPermission(RegisterActivityFillData.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                //if we don't have permission to read contacts


                ActivityCompat.requestPermissions(RegisterActivityFillData.this, new String[]{android.Manifest.permission.READ_CONTACTS}, LabsterConstants.UNIQUE_CODE_CONTACTS_READ_PERMISSION);
                // this will ask the user for the specific permission
                    /* you also have to add the permission to the manifest file !!!!!!!
                    <uses-permission android:name="android.permission.READ_CONTACTS"/>
                     */
            }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { //inherited from AppCompatActivity
    //response from the ActivityCompat.requestPermissions() method

        if(requestCode == LabsterConstants.UNIQUE_CODE_CONTACTS_READ_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) { // we send only one permission with this request code
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                    // if the user doesn't checks "Never ask again" in the dialog (first time until the user presses the no button this will return false)

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
                    // a pop up dialog to give extra information on critical situations

                    alertDialog.setMessage("This permission is important to give you extra help on filling this data!")
                                .setTitle("Important permission required!");


                    alertDialog.setPositiveButton("OK",
                            (dialog, which) -> ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CONTACTS}, LabsterConstants.UNIQUE_CODE_CONTACTS_READ_PERMISSION));
                    // setting listener with lambda

                    alertDialog.setNegativeButton("NO",
                            (dialog, which) -> Toast.makeText(RegisterActivityFillData.this, "We will not bother you again!", Toast.LENGTH_SHORT).show() );
                    // setting listener with lambda

                    alertDialog.show();
                }
            }
        }


    }


    private void saveUser() {


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
        new SaveDataAsync().execute(faculty, section, yearString, firstName, lastName); // we start a background thread to save our data
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

            Student newStudent = null;
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if(currentUser != null) {

                //getting reference to the registered student from the RegisterActivity (which is already put in the database)
                for (Student student : LabsterApplication.getInstace().getStudents())
                    if (student.getUserUID().equals(currentUser.getUid())) // looking for the same uid
                        newStudent = student;

                // we know that in this vector are 6 elements because we validate it before(in the right order)
                int year = Integer.parseInt(strings[2]);

                //setting other fields to the student
                if (newStudent != null) {
                    newStudent.setFaculty(strings[0]);
                    newStudent.setSection(strings[1]);
                    newStudent.setYear(year);
                    newStudent.getProfile().setFirstName(strings[3]);
                    newStudent.getProfile().setLastName(strings[4]);
                }

                LabsterApplication.getInstace().saveStudent(newStudent, false);
                // false cuz at this point a student only with email and password exists
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pbRegisterFill.setVisibility(View.GONE);
            svFillData.setVisibility(View.VISIBLE);

        }

    }

    // thread to generate autocomplete arrays
    private class PopulateAutocompleteArrays extends AsyncTask<Void, Void, Map<String, ArrayList<String>> > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbRegisterFill.setVisibility(View.VISIBLE);
            svFillData.setVisibility(View.GONE);
        }


        @Override
        protected  Map<String, ArrayList<String>> doInBackground(Void... voids) {

            Map<String, ArrayList<String>> map = new HashMap<>();


            // getting contacts from phone
            ArrayList<String> contactNames = null;
            if (ContextCompat.checkSelfPermission(RegisterActivityFillData.this, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) // if we can read contacts
                contactNames = getContactsFromPhone(); // implemented in this activity

            //set autocomplete adapter

            ArrayList<String> firstNames = new ArrayList<>();
            ArrayList<String> lastNames = new ArrayList<>();
            ArrayList<String> faculties = new ArrayList<>();
            ArrayList<String> sections = new ArrayList<>();

            //getting first and last name arrays
            if(contactNames != null) { // if we have contacts

                Iterator<String> namesIte = contactNames.iterator();

                while (namesIte.hasNext()) { //putting data into lists
                    String[] namesPerContact = namesIte.next().split(" ");

                    if(namesPerContact.length >= 1) {

                        if(!firstNames.contains(namesPerContact[0])) // we add only unique elements
                        firstNames.add(namesPerContact[0]);

                        if(namesPerContact.length >= 2)
                            if(!lastNames.contains(namesPerContact[1])) //we add only unique elements
                            lastNames.add(namesPerContact[1]);
                    }
                }

            }

            //getting faculties and sections from the data

            for(Student student : LabsterApplication.getInstace().getStudents()) {

                if(student.getFaculty() != null) //if we add a null object the adapter won't work
                    if(!faculties.contains(student.getFaculty())) // we add only unique elements
                    faculties.add(student.getFaculty());

                if(student.getSection() != null) //if we add a null object the adapter won't work
                    if(!sections.contains(student.getSection())) //we add only unique elements
                    sections.add(student.getSection());
            }

            // finish autocomplete adapters

            map.put("firstNames", firstNames);
            map.put("lastNames", lastNames);
            map.put("faculties", faculties);
            map.put("sections", sections);

            return  map;
        }


        @Override
        protected void onPostExecute( Map<String, ArrayList<String>> map) {
            super.onPostExecute(map);


            //setting adapters
            ArrayAdapter<String> sectionsAdapter = new ArrayAdapter<String>(RegisterActivityFillData.this, android.R.layout.simple_dropdown_item_1line, map.get("sections"));
            etSection.setThreshold(2);
            etSection.setAdapter(sectionsAdapter);

            ArrayAdapter<String> firstNamesAdapter = new ArrayAdapter<String>(RegisterActivityFillData.this, android.R.layout.simple_dropdown_item_1line, map.get("firstNames"));
            etFirstName.setThreshold(3);
            etFirstName.setAdapter(firstNamesAdapter);

            ArrayAdapter<String> lastNamesAdapter = new ArrayAdapter<String>(RegisterActivityFillData.this, android.R.layout.simple_dropdown_item_1line, map.get("lastNames"));
            etLastName.setThreshold(3);
            etLastName.setAdapter(lastNamesAdapter);

            ArrayAdapter<String> facultiesAdapter = new ArrayAdapter<String>(RegisterActivityFillData.this, android.R.layout.simple_dropdown_item_1line, map.get("faculties"));
            etFaculty.setThreshold(2);
            etFaculty.setAdapter(facultiesAdapter);

            pbRegisterFill.setVisibility(View.GONE);
            svFillData.setVisibility(View.VISIBLE);

        }



        private ArrayList<String> getContactsFromPhone() { // querying phones database for contacts to set them to the autocomplete text views

            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, null, null, null);
            ArrayList<String> names = new ArrayList<>();


            if(phones != null) { // if we have any contacts

                while (phones.moveToNext()) {

                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    Log.e("contacts: ", name);
                    names.add(name);
                }

                phones.close();

            }

            return names;
        }
    }



}
