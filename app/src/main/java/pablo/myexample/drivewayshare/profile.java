package pablo.myexample.drivewayshare;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class profile extends AppCompatActivity {

    Button button;
    EditText profileUsername;
    EditText profileLicensePlate;
    EditText profileCarModel;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    FirebaseUser firebaseUser;
    DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        button = findViewById(R.id.backtomap);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        profileUsername = findViewById(R.id.usernameprofileinput);
        profileCarModel = findViewById(R.id.carmodelinput);
        profileLicensePlate = findViewById(R.id.licenseplateinput);

    }

    public void confirmClick(View view) {

        //Update database info of current user. The email cannot be changed, nor password.
        database = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        User user = new User(profileUsername.getText().toString(), profileLicensePlate.getText().toString(), profileCarModel.getText().toString());
        mRef = database.getReference().child("Users").child(userId);
        mRef.setValue(user);

        Intent intent = new Intent(profile.this, barActivity.class);
        startActivity(intent);
        finish();

    }

    public void hideKeyBoardThird(View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }
}
