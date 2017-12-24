package ro.ianders.universitylabsterremake;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ro.ianders.universitylabsterremake.datatypes.DatabaseConstants;
import ro.ianders.universitylabsterremake.datatypes.Student;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private ProgressBar pbLogin;
    private ScrollView svLogin;
    private Button btnLogin;
    private EditText etEmail;
    private EditText etPassword;
    private TextView tvGoToSignUp;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null) { //already signed in

            /*
            if(!currentStudent.equals(DatabaseConstants.TEMPORARY_EMAIL)) {// if he didn't filled all his data in the RegisterActivityFillData go the user to that point
                startActivity(new Intent(this, RegisterActivityFillData.class));
                gotoMain = false;
                finish();
                } */

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
        }

        //getting references
        pbLogin = findViewById(R.id.pbLogin);
        svLogin = findViewById(R.id.svLogin);
        btnLogin = findViewById(R.id.btnSignIn);
        etEmail = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        tvGoToSignUp = findViewById(R.id.tvGoToSignUp);

        btnLogin.setOnClickListener(this);
        tvGoToSignUp.setOnClickListener(this);
    }

    // listeners
    @Override
    public void onClick(View v) {

        if(v == btnLogin) {
            userLogin();
        } else if (v == tvGoToSignUp) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }
    }

    private void userLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        //verification
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
        svLogin.setVisibility(View.GONE);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {


                          /*  if(currentStudent != null) { // if he didn't filled all his data in the RegisterActivityFillData go the user to that point
                                startActivity(new Intent(LoginActivity.this, RegisterActivityFillData.class));
                                gotoMain = false;
                                finish();
                                }*/

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();

                        } else {
                            Toast.makeText(LoginActivity.this, "Login declined!", Toast.LENGTH_SHORT).show();
                        }

                        pbLogin.setVisibility(View.GONE);
                        svLogin.setVisibility(View.VISIBLE);

                    }
                });
    }




}
