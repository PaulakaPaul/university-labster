package ro.ianders.universitylabsterremake;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

import ro.ianders.universitylabsterremake.datatypes.Student;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private ProgressBar pbLogin;
    private ScrollView svLogin;
    private Button btnLogin;
    private EditText etEmail;
    private EditText etPassword;
    private TextView tvGoToSignUp;

    //firebase
    private FirebaseAuth firebaseAuth;

    //facebook login
    private CallbackManager mCallbackManager;
    private Button btnFacebookLogin;
    private static final String FACEBOOK_TAG = "FACELOG";

    //google login
    private GoogleSignInClient mGoogleSignInClient;
    private Button btnGoogleLogin;
    public static final int RC_SIGN_IN_GOOGLE = 9001;
    public static final String GOOGLE_TAG = "GoogleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        //getting references
        pbLogin = findViewById(R.id.pbLogin);
        svLogin = findViewById(R.id.svLogin);
        btnLogin = findViewById(R.id.btnSignIn);
        etEmail = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        tvGoToSignUp = findViewById(R.id.tvGoToSignUp);

        btnLogin.setOnClickListener(this);
        tvGoToSignUp.setOnClickListener(this);

        // Initialize Facebook Login button and manager
        mCallbackManager = CallbackManager.Factory.create();
        btnFacebookLogin = findViewById(R.id.btnFacebookLogin);
        btnFacebookLogin.setOnClickListener(this);

        // Initialize Google Login button and manager

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        btnGoogleLogin.setOnClickListener(this);

    }


    @Override
    protected void onStart() {
        super.onStart();

        //TODO make a logic to go to Main or RegisterFill activities if the user is already logged in
        /*FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null) { //already signed in
            updateUIToMain(); // implemented in this class
        }*/
    }


    private void updateUIToMain() {
            Intent goToMainIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(goToMainIntent);
            finish();
    }

    private void updateUIToFillData() {
        Intent goToMainIntent = new Intent(LoginActivity.this, RegisterActivityFillData.class);
        startActivity(goToMainIntent);
        finish();
    }


    // listeners
    @Override
    public void onClick(View v) {

        if(v == btnLogin) {
            userLoginEmail();
        } else if (v == tvGoToSignUp) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        } else if ( v == btnFacebookLogin) {
            userLoginWithFacebook();
        } else if (v == btnGoogleLogin) {
            signInWithGoogle();
        }
    }


    private void userLoginWithFacebook() {

        pbLogin.setVisibility(View.VISIBLE); // we want to see only the progress bar
        svLogin.setVisibility(View.GONE);

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(FACEBOOK_TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken()); //implemented in this class
            }

            @Override
            public void onCancel() {
                Log.d(FACEBOOK_TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(FACEBOOK_TAG, "facebook:onError", error);
            }
        });

        pbLogin.setVisibility(View.GONE);
        svLogin.setVisibility(View.VISIBLE);

    }


    // [START signin] with google
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }
    // [END signin]


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK ( we need this for callbacks with facebook)
        mCallbackManager.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN_GOOGLE) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account); //implemented in this class
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "API failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void userLoginEmail() {
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

                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                            if(currentUser != null) //check if the user filled his personal data or not(if not we put the user to fill his data)
                                for(Student student : LabsterApplication.getInstace().getStudents())
                                    if(student.getUserUID().equals(currentUser.getUid())) {
                                        if (student.getProfile().getFirstName() == null) {
                                            updateUIToFillData();
                                        } else {
                                            updateUIToMain();
                                        }
                                        break;
                                    }

                            if(LabsterApplication.getInstace().getStudents().size() == 0)
                                updateUIToMain(); //go to main activity in the case the LabsterApplication.getInstace().getStudents() is not yet filled with data

                        } else {
                            Toast.makeText(LoginActivity.this, "Login declined!", Toast.LENGTH_SHORT).show();
                        }

                        pbLogin.setVisibility(View.GONE);
                        svLogin.setVisibility(View.VISIBLE);

                    }
                });
    }


    // after we log in to facebook we have to auth to firebase with the AuthCredential
    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            Student currentStudent;
                            boolean alreadyRegistered = false;
                            boolean fillData = false;

                            if (currentUser != null) {

                                for (Student student : LabsterApplication.getInstace().getStudents()) // searching for the user in the database
                                    if (student.getUserUID().equals(currentUser.getUid())) {
                                        alreadyRegistered = true;

                                        if (student.getProfile().getFirstName() == null) // go to FillDataActivity
                                            fillData = true;

                                        break;
                                    }

                                if (!alreadyRegistered) { // if we login with fb for the first time
                                    currentStudent = new Student(currentUser.getUid(), currentUser.getEmail());
                                    LabsterApplication.getInstace().saveStudent(currentStudent, true);
                                    updateUIToFillData(); // implemented in this class
                                } else { // if we already have an account with fb
                                    if (fillData)
                                        updateUIToFillData();
                                    else
                                        updateUIToMain();
                                }
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        pbLogin.setVisibility(View.VISIBLE); // we want to see only the progress bar
        svLogin.setVisibility(View.GONE);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            Student currentStudent;
                            boolean alreadyRegistered = false;
                            boolean fillData = false;

                            if(currentUser != null) {

                            for(Student student : LabsterApplication.getInstace().getStudents()) // searching for the user in the database
                                if(student.getUserUID().equals(currentUser.getUid())) {
                                    alreadyRegistered = true;

                                    if(student.getProfile().getFirstName() == null) // go to FillDataActivity
                                        fillData = true;

                                    break;
                                }

                                if(!alreadyRegistered) { // if we login with google for the first time
                                    currentStudent = new Student(currentUser.getUid(), currentUser.getEmail());
                                    LabsterApplication.getInstace().saveStudent(currentStudent, true);
                                    updateUIToFillData(); // implemented in this class
                                } else { // if we already have an account with google
                                    if(fillData)
                                        updateUIToFillData();
                                    else
                                        updateUIToMain();
                                }
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(GOOGLE_TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(svLogin, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUIToMain(null);
                        }


                    }
                });

        pbLogin.setVisibility(View.GONE);
        svLogin.setVisibility(View.VISIBLE); //process if finished
    }
    // [END auth_with_google]

}
