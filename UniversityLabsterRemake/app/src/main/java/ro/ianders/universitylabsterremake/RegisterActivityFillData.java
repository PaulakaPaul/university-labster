package ro.ianders.universitylabsterremake;

import android.Manifest;
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

import java.util.ArrayList;
import java.util.Iterator;

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


        // getting contacts from phone
        ArrayList<String> contactNames = null;
        if (ContextCompat.checkSelfPermission(RegisterActivityFillData.this, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) // if we can read contacts
            contactNames = getContactsFromPhone(); // implemented in this activity


        //set autocomplete adapter


        //getting faculties and sections from the data
        ArrayList<String> faculties = new ArrayList<>();
        ArrayList<String> sections = new ArrayList<>();

        for(Student student : LabsterApplication.getInstace().getStudents()) {
            faculties.add(student.getFaculty());
            sections.add(student.getSection());
        }

        ArrayAdapter<String> facultiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, faculties);
        etFaculty.setThreshold(1);
        etFaculty.setAdapter(facultiesAdapter);

        ArrayAdapter<String> sectionsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, sections);
        etSection.setThreshold(1);
        etSection.setAdapter(sectionsAdapter);
        //TODO move all the ArrayList creating in a AsyncTask class


        if(contactNames != null) { // if we have contacts

            ArrayList<String> firstNames = new ArrayList<>();
            ArrayList<String> lastNames = new ArrayList<>();

            Iterator<String> namesIte = contactNames.iterator();

            while (namesIte.hasNext()) { //putting data into lists
                String[] namesPerContact = namesIte.next().split(" ");

                if(namesPerContact.length >= 1) {
                    firstNames.add(namesPerContact[0]);

                        if(namesPerContact.length >= 2)
                            lastNames.add(namesPerContact[1]);
                }
            }


            ArrayAdapter<String> firstNamesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, firstNames);
            etFirstName.setThreshold(3);
            etFirstName.setAdapter(firstNamesAdapter);


            ArrayAdapter<String> lastNamesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, lastNames);
            etLastName.setThreshold(3);
            etLastName.setAdapter(lastNamesAdapter);
        }
        // finish autocomplete adapters

        //save the user
        btnSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser(); // implemented in this activity
            }
        });

    }


    private void askForPermissionContacts() { // true if we get the permission

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


        if(requestCode == LabsterConstants.UNIQUE_CODE_CONTACTS_READ_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) { // we send only one permission with this request code
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                    // if the user doesn't checks "Never ask again" in the dialog (first time until the user presses the no button this will return false)

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
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
