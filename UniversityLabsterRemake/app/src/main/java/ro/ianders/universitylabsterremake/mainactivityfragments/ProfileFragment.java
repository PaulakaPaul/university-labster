package ro.ianders.universitylabsterremake.mainactivityfragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import ro.ianders.universitylabsterremake.LabsterApplication;
import ro.ianders.universitylabsterremake.R;
import ro.ianders.universitylabsterremake.datatypes.Student;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private Toolbar tbProfile;
    private FloatingActionButton fbtnProfileEdit;

    private TextView tvFaculty;
    private TextView tvSection;
    private TextView tvYear;
    private TextView tvName;
    private TextView tvEmail;

    private EditText etFaculty; //for editing
    private EditText etSection;
    private EditText etYear;
    private EditText etName;
    private EditText etEmail;

    private boolean editClicked; // we control the edit logic with this var

    private Student currentStudent;

    public ProfileFragment() {
        // Required empty public constructor
        editClicked = false; // by default the edit button is not clicked
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        tbProfile = view.findViewById(R.id.tbProfile);
        fbtnProfileEdit = view.findViewById(R.id.fbtnProfileEdit);

        tvFaculty = view.findViewById(R.id.tvFaculty);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvSection = view.findViewById(R.id.tvSection);
        tvYear = view.findViewById(R.id.tvYear);
        tvName = view.findViewById(R.id.tvName);

        etFaculty = view.findViewById(R.id.etFaculty);
        etEmail = view.findViewById(R.id.etEmail);
        etSection = view.findViewById(R.id.etSection);
        etYear = view.findViewById(R.id.etYear);
        etName = view.findViewById(R.id.etName);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //filling profile with current students data
        currentStudent = LabsterApplication.getCurrentStudent();
        if(currentStudent != null) {
            tvFaculty.setText(currentStudent.getFaculty());
            tvSection.setText(currentStudent.getSection());
            tvEmail.setText(currentStudent.getProfile().getEmail());
            tvName.setText(String.format("%s %s", currentStudent.getProfile().getLastName(), currentStudent.getProfile().getFirstName()));
            tvYear.setText(String.format("%d",currentStudent.getYear()));
        } else { //in case the data was not loaded from the database
            tvFaculty.setText(getResources().getString(R.string.profile_faculty));
            tvSection.setText(getResources().getString(R.string.profile_section));
            tvEmail.setText(getResources().getString(R.string.profile_email));
            tvName.setText(getResources().getString(R.string.profile_name));
            tvYear.setText(getResources().getString(R.string.profile_year));
        }

        //edit profile
        fbtnProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile();
            }
        });
    }


    private void editProfile() {

        if(editClicked) { // if we have the edit view opened

            tvFaculty.setVisibility(View.VISIBLE);
            tvSection.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.VISIBLE);
           // tvEmail.setVisibility(View.VISIBLE);
            tvYear.setVisibility(View.VISIBLE);

            etFaculty.setVisibility(View.GONE);
            etSection.setVisibility(View.GONE);
            etName.setVisibility(View.GONE);
          //  etEmail.setVisibility(View.GONE);
            etYear.setVisibility(View.GONE);

            String faculty = etFaculty.getText().toString().trim();
            String section = etSection.getText().toString().trim();
            String name = etName.getText().toString().trim();
         //   String email = etEmail.getText().toString().trim();
            String year = etYear.getText().toString().trim();

            tvFaculty.setText(faculty);
            tvSection.setText(section);
            tvName.setText(name);
          //  tvEmail.setText(email);
            tvYear.setText(year);


            // we save the data both dynamically and to the database
            currentStudent.setFaculty(faculty);
            currentStudent.setSection(section);
            currentStudent.setYear(Integer.parseInt(year));
          //  currentStudent.setEmail(email);
            currentStudent.setName(name);
            LabsterApplication.getInstace().saveStudent(currentStudent, false);

            fbtnProfileEdit.setImageResource(R.drawable.ic_edit);

            editClicked = false;


        } else { // if we have the normal view open

            tvFaculty.setVisibility(View.GONE);
            tvSection.setVisibility(View.GONE);
            tvName.setVisibility(View.GONE);
          //  tvEmail.setVisibility(View.GONE);
            tvYear.setVisibility(View.GONE);

            etFaculty.setVisibility(View.VISIBLE);
            etSection.setVisibility(View.VISIBLE);
            etName.setVisibility(View.VISIBLE);
          //  etEmail.setVisibility(View.VISIBLE);
            etYear.setVisibility(View.VISIBLE);

            etFaculty.setText(tvFaculty.getText());
            etSection.setText(tvSection.getText());
            etName.setText(tvName.getText());
         //   etEmail.setText(tvEmail.getText());
            etYear.setText(tvYear.getText());

            fbtnProfileEdit.setImageResource(R.drawable.ic_save_white);

            editClicked = true;
        }

    }
}
