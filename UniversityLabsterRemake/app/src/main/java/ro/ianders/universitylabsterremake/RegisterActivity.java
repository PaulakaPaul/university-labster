package ro.ianders.universitylabsterremake;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ro.ianders.universitylabsterremake.datatypes.Student;


/**
 * A login screen that offers login via etEmail/password.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private ProgressBar pbLogin;
    private ScrollView svRegister;
    private LinearLayout emailLoginForm;
    private AutoCompleteTextView etEmail;
    private EditText etPassword;
    private Button btnRegister;
    private TextView tvGoToSignIn;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //get instance for firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        pbLogin = findViewById(R.id.login_progress);
        svRegister = findViewById(R.id.svRegister);
        emailLoginForm = findViewById(R.id.email_login_form);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        btnRegister = findViewById(R.id.email_sign_in_button);
        tvGoToSignIn = findViewById(R.id.tvGoToSignIn);

        btnRegister.setOnClickListener(this); // will be called on method onClick() from the View.OnClickListener interface
        tvGoToSignIn.setOnClickListener(this); // will be called on method onClick() from the View.OnClickListener interface

    }

    //method from the View.OnClickListener interface which you can use to set listeners for views
    @Override
    public void onClick(View v) {

        if(v == btnRegister) {
            registerUser();
        } else if ( v == tvGoToSignIn) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }


    // function called when btnRegister is clicked
    private void registerUser() {

        String email =  etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }


        //we continue registration
        // TODO more email and password validation

        pbLogin.setVisibility(View.VISIBLE); // we want to see only the progress bar
        svRegister.setVisibility(View.GONE);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) { // if everything is ok we proceed send the user to fill he's other profile data
                            Toast.makeText(RegisterActivity.this, "Registered Succesfully!", Toast.LENGTH_SHORT).show();

                            Student newStudent = new Student(email, password);
                            LabsterApplication.getInstace().saveStudent(newStudent, true); //first save student to database

                            startActivity(new Intent(RegisterActivity.this, RegisterActivityFillData.class)
                                                .putExtra("email", email)
                                                .putExtra("password", password)); // continue your registration
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed... Please try again !", Toast.LENGTH_SHORT).show();
                        }

                        pbLogin.setVisibility(View.GONE);
                        svRegister.setVisibility(View.VISIBLE);

                        if(task.isSuccessful()) // finish the activity only if we created the account
                            finish(); // to not be able to go back to the same activity

                    }
                });

    }

}

