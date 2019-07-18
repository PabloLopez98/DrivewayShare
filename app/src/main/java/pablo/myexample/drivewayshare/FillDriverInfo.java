package pablo.myexample.drivewayshare;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FillDriverInfo extends AppCompatActivity implements View.OnClickListener {

    MyPost myPost;
    String creatorId, creatorPostId;
    Button backtomapfragment, requestreservationbutton;
    DatabaseReference mRef, mRefTwo, mRefThree;
    ValueEventListener mvalueEventListener;
    TextView HostName, HostAddress, HostTime, HostPrice;
    ImageView HostImageView;
    TextView LicensePlate, CarModel, UserName;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String MyReservationId;

    public void returnToTheMap() {

        Intent goBack = new Intent(getApplicationContext(), barActivity.class);
        startActivity(goBack);
        finish();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.backtomapfragment:
                returnToTheMap();

                break;

            case R.id.requestReservationButton:

                if (LicensePlate.getText().toString().equals("") || CarModel.getText().toString().equals("")) {

                    Toast.makeText(getApplicationContext(), "Fields are empty", Toast.LENGTH_LONG).show();

                } else {

                    firebaseUser = firebaseAuth.getInstance().getCurrentUser();
                    String userId = firebaseUser.getUid();
                    MyReservationId = "MyReservation " + System.currentTimeMillis();
                    mRefTwo = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("MyReservationInfo").child(MyReservationId);

                    MyReservation myReservation = new MyReservation(UserName.getText().toString(), LicensePlate.getText().toString(), CarModel.getText().toString(), myPost.getAddress(), myPost.getTime(), myPost.getPrice(), myPost.getImageUrl(), myPost.getmypostname(), "Requested", creatorId, creatorPostId);

                    mRefTwo.setValue(myReservation);

                    //change host Unrequested to Requested
                    //change clientName, clientPlate, clientModel to the clients info
                    mRefThree = FirebaseDatabase.getInstance().getReference().child("Users").child(creatorId).child("MyPostInfo").child(creatorPostId).child("requested");
                    mRefThree.setValue("Requested");
                    mRefThree = FirebaseDatabase.getInstance().getReference().child("Users").child(creatorId).child("MyPostInfo").child(creatorPostId).child("clientName");
                    mRefThree.setValue(UserName.getText().toString());
                    mRefThree = FirebaseDatabase.getInstance().getReference().child("Users").child(creatorId).child("MyPostInfo").child(creatorPostId).child("clientPlate");
                    mRefThree.setValue(LicensePlate.getText().toString());
                    mRefThree = FirebaseDatabase.getInstance().getReference().child("Users").child(creatorId).child("MyPostInfo").child(creatorPostId).child("clientModel");
                    mRefThree.setValue(CarModel.getText().toString());

                    //then go back
                    returnToTheMap();
                }

                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_driver_info);

        //get host id and post id
        Bundle extras = getIntent().getExtras();
        creatorId = extras.getString("creatorId");
        creatorPostId = extras.getString("creatorPostId");

        //find user textfields
        LicensePlate = findViewById(R.id.inputLicensePlate);
        CarModel = findViewById(R.id.inputCarModel);
        UserName = findViewById(R.id.inputUserName);

        //find host text fields
        HostName = findViewById(R.id.hostname);
        HostAddress = findViewById(R.id.hosttextaddress);
        HostTime = findViewById(R.id.hosttexttime);
        HostPrice = findViewById(R.id.hostprice);
        HostImageView = findViewById(R.id.hostDrivewayImageSquare);

        //find buttons and make them listenable
        backtomapfragment = findViewById(R.id.backtomapfragment);
        requestreservationbutton = findViewById(R.id.requestReservationButton);
        backtomapfragment.setOnClickListener(this);
        requestreservationbutton.setOnClickListener(this);

        //give host text fields their info
        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(creatorId).child("MyPostInfo").child(creatorPostId);
        mvalueEventListener = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                myPost = dataSnapshot.getValue(MyPost.class);
                HostName.setText(myPost.getmypostname());
                HostAddress.setText(myPost.getAddress());
                HostTime.setText(myPost.getTime());
                HostPrice.setText(myPost.getPrice());
                Picasso.with(getApplicationContext()).load(myPost.getImageUrl()).fit().centerCrop().into(HostImageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void hideTheKeyboard(View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRef.removeEventListener(mvalueEventListener);

    }
}
