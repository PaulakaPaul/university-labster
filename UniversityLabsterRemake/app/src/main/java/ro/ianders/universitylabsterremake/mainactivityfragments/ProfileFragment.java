package ro.ianders.universitylabsterremake.mainactivityfragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ro.ianders.universitylabsterremake.ChangeProfilePicHeader;
import ro.ianders.universitylabsterremake.LabsterApplication;
import ro.ianders.universitylabsterremake.MainActivity;
import ro.ianders.universitylabsterremake.R;
import ro.ianders.universitylabsterremake.datatypes.DatabaseConstants;
import ro.ianders.universitylabsterremake.datatypes.Student;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final int REQUEST_CODE_PROFILE_PICTURE = 3;

    private Toolbar tbProfile;
    private FloatingActionButton fbtnProfileEdit;
    private FloatingActionButton fbtnProfile;
    private ImageView ivProfile;
    private CollapsingToolbarLayout ctlProfile;

    private TextView tvFaculty;
    private TextView tvSection;
    private TextView tvYear;
    private TextView tvName;
    private TextView tvEmail;

    private EditText etFaculty; //for editing
    private EditText etSection;
    private EditText etYear;
    private EditText etName;

    private ChangeProfilePicHeader changeProfilePicHeaderListener;
    private boolean editClicked; // we control the edit logic with this var

    private Student currentStudent;
    private Bitmap profilePicture;

    public ProfileFragment() {
        // Required empty public constructor
        editClicked = false; // by default the edit button is not clicked
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            changeProfilePicHeaderListener = (ChangeProfilePicHeader) context;
        } catch (ClassCastException e) {
            throw new RuntimeException("Please implement ChangeProfilePicHeader interface on " + context + " context");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        tbProfile = view.findViewById(R.id.tbProfile);
        fbtnProfileEdit = view.findViewById(R.id.fbtnProfileEdit);
        fbtnProfile = view.findViewById(R.id.fbtnProfile);
        ivProfile = view.findViewById(R.id.ivProfile);

        tvFaculty = view.findViewById(R.id.tvFaculty);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvSection = view.findViewById(R.id.tvSection);
        tvYear = view.findViewById(R.id.tvYear);
        tvName = view.findViewById(R.id.tvName);

        etFaculty = view.findViewById(R.id.etFaculty);
        etSection = view.findViewById(R.id.etSection);
        etYear = view.findViewById(R.id.etYear);
        etName = view.findViewById(R.id.etName);

        ctlProfile = view.findViewById(R.id.toolbar_layout);

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

            if(currentStudent.getProfile().getPicture() != null) //else the default image from the xml file will be displayed
                ivProfile.setImageBitmap(decodeBitmapFromString(currentStudent.getProfile().getPicture()));

            ctlProfile.setTitle("Hello " + currentStudent.getProfile().getFirstName().split("-")[0]); // if the name has not any '-' than
            // all his first name will be displayed cuz the split will return an array with one element

        } else { //in case the data was not loaded from the database
            tvFaculty.setText(getResources().getString(R.string.profile_faculty));
            tvSection.setText(getResources().getString(R.string.profile_section));
            tvEmail.setText(getResources().getString(R.string.profile_email));
            tvName.setText(getResources().getString(R.string.profile_name));
            tvYear.setText(getResources().getString(R.string.profile_year));

            ctlProfile.setTitle("Hello there!");
        }

        //edit profile
        fbtnProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile();
            }
        });

        fbtnProfile.setOnClickListener((view) -> startActivityForResult(new Intent
                (Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), REQUEST_CODE_PROFILE_PICTURE) );
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Request from the fbtnProfile button that asks for a implicit intent
        if(requestCode == REQUEST_CODE_PROFILE_PICTURE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), selectedImage);
            } catch (IOException e) {
                Log.e("PROFILEPICTURE", "IOException " + e.getMessage());
                bitmap = null;
            } catch (NullPointerException e){
                Log.e("PROFILEPICTURE", "NullPointerException " + e.getMessage());
                bitmap = null;
            }

            if(bitmap != null) {
                profilePicture = getCroppedBitmap(bitmap); // grab the picture dynamically and crop it
                ivProfile.setImageBitmap(profilePicture); //upload the view -> we save it to the database only when we press the save edit button
            }
            else profilePicture = null;


        }

    }

    private void editProfile() {

        if(editClicked) { // if we have the edit view opened

            tvFaculty.setVisibility(View.VISIBLE);
            tvSection.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.VISIBLE);
            tvYear.setVisibility(View.VISIBLE);

            etFaculty.setVisibility(View.GONE);
            etSection.setVisibility(View.GONE);
            etName.setVisibility(View.GONE);
            etYear.setVisibility(View.GONE);

            String faculty = etFaculty.getText().toString().trim();
            String section = etSection.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String year = etYear.getText().toString().trim();

            tvFaculty.setText(faculty);
            tvSection.setText(section);
            tvName.setText(name);
            tvYear.setText(year);

            // we save the data both dynamically and to the database
            currentStudent.setFaculty(faculty);
            currentStudent.setSection(section);
            currentStudent.setYear(Integer.parseInt(year));
            currentStudent.setName(name);
            LabsterApplication.getInstace().saveStudent(currentStudent, false);

            fbtnProfileEdit.setImageResource(R.drawable.ic_edit);
            fbtnProfile.setVisibility(View.GONE);

            if(profilePicture != null) { // if we choose a picture
                currentStudent.getProfile().setPicture(encodeBitmapAndSaveToString(profilePicture)); //save the encoded bitmap to the user
                LabsterApplication.getInstace().saveFieldToStudent(currentStudent, DatabaseConstants.STUDENT_PROFILE, currentStudent.getProfile());
                // save to the database
                changeProfilePicHeaderListener.changePic(profilePicture); //change the picture from the header with this callback on the activity
            }

            ctlProfile.setTitle("Hello " + currentStudent.getProfile().getFirstName().split("-")[0]); // if the name has not any '-' than
            // all his first name will be displayed cuz the split will return an array with one element

            editClicked = false;


        } else { // if we have the normal view open

            tvFaculty.setVisibility(View.GONE);
            tvSection.setVisibility(View.GONE);
            tvName.setVisibility(View.GONE);
            tvYear.setVisibility(View.GONE);

            etFaculty.setVisibility(View.VISIBLE);
            etSection.setVisibility(View.VISIBLE);
            etName.setVisibility(View.VISIBLE);
            etYear.setVisibility(View.VISIBLE);

            etFaculty.setText(tvFaculty.getText());
            etSection.setText(tvSection.getText());
            etName.setText(tvName.getText());
            etYear.setText(tvYear.getText());

            fbtnProfileEdit.setImageResource(R.drawable.ic_save_white);

            fbtnProfile.setVisibility(View.VISIBLE);

            editClicked = true;
        }

    }

    private String encodeBitmapAndSaveToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); //byte output stream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //it compresses the bitmap to the output stream
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT); // we get the string representation of the compressed bitmal
    }

    public static Bitmap decodeBitmapFromString(String image) {
        byte[] decodedByteArray = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    private Bitmap getCroppedBitmap(Bitmap bitmap) { // it makes a circle from the bitmap
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
