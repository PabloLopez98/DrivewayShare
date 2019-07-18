package pablo.myexample.drivewayshare;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

    }

    //if user is logged in, then send user to home screen
    //else send user to login screen
    @Override
    public void onStart() {
        super.onStart();

        FirebaseApp.initializeApp(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            Intent intent = new Intent(MainActivity.this, barActivity.class);
            startActivity(intent);
            finish();

        } else {

            Intent intent = new Intent(MainActivity.this, LoginSignUp.class);
            startActivity(intent);
            finish();

        }
    }
}