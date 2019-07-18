package pablo.myexample.drivewayshare;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class inputMyPostInfo extends AppCompatActivity implements View.OnClickListener {

    TextView inputName;
    TextView inputAddress;
    TextView from;
    TextView to;
    String inputTime;
    TextView inputPrice;
    Button AddPost;
    Button backtomypost;
    static final int PICK_IMAGE_REQUEST = 1;
    Button addImageButton;
    ImageView drivewayImage;
    Uri imageUri;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userId;
    Calendar calendar;
    TimePickerDialog timePickerDialog;
    int currentHour;
    int currentMinute;
    String amPm;
    String MyPostId;

    @Override
    public void onClick(final View v) {

        if (v.getId() == R.id.addpostbutton) {

            //add time check
            if ((inputName.getText().toString() != "") || (inputAddress.getText().toString() != "") || (inputPrice.getText().toString() != "") || (imageUri != null)) {

                //give image reference name
                final StorageReference filereference = storageReference.child("images/").child(userId).child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

                filereference.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()) {

                            throw task.getException();

                        }

                        return filereference.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()) {

                            Uri downloadUri = task.getResult();

                            inputTime = from.getText().toString() + " to " + to.getText().toString();
                            MyPost myPost = new MyPost(inputName.getText().toString(), inputAddress.getText().toString(), inputTime, inputPrice.getText().toString(), downloadUri.toString(), "Unrequested", userId, MyPostId, "clientName", "clientPlate", "clientModel");
                            databaseReference.setValue(myPost);
                            finish();

                        } else {

                            Toast.makeText(getApplicationContext(), "upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        } else if (v.getId() == R.id.backtomapfragment) {

            finish();

        } else if (v.getId() == R.id.addImageButton) {

            openFileChooser();

        } else if (v.getId() == R.id.from || v.getId() == R.id.to) {

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

                    //set text to either from or to
                    if (v.getId() == R.id.from) {

                        from.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm);

                    } else {

                        to.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm);

                    }
                }
            }, currentHour, currentMinute, false);

            timePickerDialog.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_my_post_info);

        inputName = findViewById(R.id.nameinput);

        from = findViewById(R.id.from);
        to = findViewById(R.id.to);
        from.setOnClickListener(this);
        to.setOnClickListener(this);

        inputAddress = findViewById(R.id.inputAddress);
        inputPrice = findViewById(R.id.priceinput);

        AddPost = findViewById(R.id.addpostbutton);
        AddPost.setOnClickListener(this);

        backtomypost = findViewById(R.id.backtomapfragment);
        backtomypost.setOnClickListener(this);

        addImageButton = findViewById(R.id.addImageButton);
        drivewayImage = findViewById(R.id.drivewayImageSquare);
        addImageButton.setOnClickListener(this);

        storageReference = FirebaseStorage.getInstance().getReference("Uploads");
        firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();

        MyPostId = "MyPost " + System.currentTimeMillis();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("MyPostInfo").child(MyPostId);

    }

    //get file extension from image
    public String getFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    public void openFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
            Picasso.with(this).load(imageUri).into(drivewayImage);

        }

    }

    public void hideKeyBoardFourth(View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }
}




