package ro.ianders.universitylabsterremake;


import android.content.Intent;
import android.os.AsyncTask;
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


/**
 * A login screen that offers login via etEmail/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private ProgressBar pbLogin;
    private ScrollView loginForm;
    private LinearLayout emailLoginForm;
    private AutoCompleteTextView etEmail;
    private EditText etPassword;
    private Button btnRegister;
    private TextView tvGoToSignIn;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        //get instance for firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        pbLogin = findViewById(R.id.login_progress);
        loginForm = findViewById(R.id.login_form);
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
            // go to login activity
        }
    }

    private void registerUser() {

        String email =  etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            //pasword is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        //we continue registration
        // TODO more email and password validation
        new LoginInBackground().execute(email, password); // it start a background thread
    }


    // background thread for registration
    public class LoginInBackground extends AsyncTask<String, Void, Void> {


        @Override
        protected void onPreExecute() { // first method that runs
            super.onPreExecute();
            pbLogin.setVisibility(View.VISIBLE);
        }
        // runs on the UI thread

        @Override
        protected Void doInBackground(String... strings) { // second method that runs in the background thread

            String email = strings[0]; // passed in this order from the UI thread
            String password = strings[1]; // passed in this order from the UI thread

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                //user is successfully registered and logged
                                // TODO we will start the profile activity here
                                Toast.makeText(LoginActivity.this, "Registered Succesfully!", Toast.LENGTH_SHORT).show();
                                //  finish(); // to not be able to go back to the same activity
                            } else {
                                Toast.makeText(LoginActivity.this, "Registration failed... Please try again !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) { // last method that runs on the UI thread
            super.onPostExecute(aVoid);

            pbLogin.setVisibility(View.GONE);
            startActivity(new Intent(LoginActivity.this, MainActivityActivity.class));
            finish();
        }
    }
}

