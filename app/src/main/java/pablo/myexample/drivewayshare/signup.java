package pablo.myexample.drivewayshare;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText username;
    EditText email;
    EditText password;
    EditText confirmpassword;

    public void signUpUser(final String email, String password, final String userName) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //after successful sign up, update user profile info and send user to home screen
                if (task.isSuccessful()) {

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    FirebaseUser firebaseUser = mAuth.getInstance().getCurrentUser();
                    String userId = firebaseUser.getUid();
                    User user = new User(userName, "null", "null");
                    DatabaseReference mRef = database.getReference().child("Users").child(userId);
                    mRef.setValue(user);

                    Intent intent = new Intent(signup.this, barActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    String s = "Sign up Failed: " + task.getException();
                    Toast.makeText(signup.this, s, Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    //execute signUp method after successful text input
    public void SignUp(View view) {

        if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {

            Toast.makeText(this, "Missing field.", Toast.LENGTH_LONG).show();

        } else if (!password.getText().toString().equals(confirmpassword.getText().toString())) {

            Toast.makeText(this, "Check password confirmation.", Toast.LENGTH_LONG).show();

        } else if (password.getText().toString().length() < 6) {

            Toast.makeText(this, "Password cannot be under 6 characters.", Toast.LENGTH_LONG).show();

        } else {

            signUpUser(email.getText().toString(), password.getText().toString(), username.getText().toString());

        }
    }

    //go back to login screen
    public void goBack(View view) {

        Intent intent = new Intent(signup.this, LoginSignUp.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.usernameinput);
        email = findViewById(R.id.emailinput);
        password = findViewById(R.id.passwordinput);
        confirmpassword = findViewById(R.id.confirmpasswordinput);

    }

    public void hideKeyBoardAgain(View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }
}
