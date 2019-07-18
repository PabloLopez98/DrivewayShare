package pablo.myexample.drivewayshare;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;

import androidx.annotation.NonNull;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginSignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText email;
    EditText password;

    public void signInUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginSignUp.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //if sign in is successful, send user to home screen
                if (task.isSuccessful()) {

                    Intent intent = new Intent(LoginSignUp.this, barActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(getApplicationContext(), "Login unsuccessful.", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    //if text is missing 'try again'
    //else successfully sign in user
    public void Login(View view) {

        if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {

            Toast.makeText(getApplicationContext(), "Try Again", Toast.LENGTH_LONG).show();

        } else {

            signInUser(email.getText().toString(), password.getText().toString());

        }
    }

    public void toCreateAccount(View view) {

        Intent intent = new Intent(LoginSignUp.this, signup.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

    }

    public void hideKeyBoard(View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }
}
