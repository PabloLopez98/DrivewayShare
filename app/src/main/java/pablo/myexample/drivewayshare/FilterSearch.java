package pablo.myexample.drivewayshare;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class FilterSearch extends AppCompatActivity implements View.OnClickListener {

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    String userId;
    DatabaseReference databaseRef;
    Calendar calendar;
    TimePickerDialog timePickerDialog;
    int currentHour;
    int currentMinute;
    String amPm;
    TextView pricefilterinput;
    TextView timefilterinput;
    Button backToMap;
    Button filterConfirmButton;
    String maxPrice;
    String startingHour;

    public void goBack() {

        Intent intent = new Intent(FilterSearch.this, barActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.backToMap:

                goBack();

                break;

            case R.id.timefilterinput:

                calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar.get(Calendar.MINUTE);
                timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {

                        if (hourOfDay >= 12) {

                            amPm = "PM";

                        } else {

                            amPm = "AM";

                        }
                        if (hourOfDay % 12 == 0) {

                            hourOfDay = 12;

                        } else {

                            hourOfDay = hourOfDay % 12;

                        }

                        timefilterinput.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm);

                    }
                }, currentHour, currentMinute, false);

                timePickerDialog.show();

                break;

            case R.id.filterConfirmButton:

                maxPrice = pricefilterinput.getText().toString();
                startingHour = timefilterinput.getText().toString();

                if (maxPrice != "" || startingHour != "") {

                    final Filter filter = new Filter(startingHour, maxPrice);
                    firebaseUser = firebaseAuth.getInstance().getCurrentUser();
                    userId = firebaseUser.getUid();
                    databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("SearchFilter");
                    databaseRef.setValue(filter);
                    goBack();

                }

                break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_search);

        backToMap = findViewById(R.id.backToMap);
        backToMap.setOnClickListener(this);
        pricefilterinput = findViewById(R.id.pricefilterinput);
        timefilterinput = (EditText) findViewById(R.id.timefilterinput);
        timefilterinput.setOnClickListener(this);
        filterConfirmButton = findViewById(R.id.filterConfirmButton);
        filterConfirmButton.setOnClickListener(this);

    }

    public void hideKeyBoardAgainfifth(View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }
}
