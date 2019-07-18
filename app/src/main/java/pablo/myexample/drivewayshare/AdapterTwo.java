package pablo.myexample.drivewayshare;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pablo.myexample.drivewayshare.fragments.MyReservationFragment;

public class AdapterTwo extends RecyclerView.Adapter<AdapterTwo.ViewHolder> {

    private Context mCtx;
    private List<MyReservation> myReservationList;
    DatabaseReference databaseReference, databaseReference2, databaseReference3, databaseReference4;
    ValueEventListener valueEventListener, valueEventListener2, valueEventListener3, valueEventListener4;
    String userId;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    Calendar calendar;
    TimePickerDialog timePickerDialog;

    int totalMinutesLeft;
    int a = 0;
    int b = 0;

    public AdapterTwo(Context mCtx, List<MyReservation> myReservationList) {

        this.mCtx = mCtx;
        this.myReservationList = myReservationList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        a = 1;
        LayoutInflater layoutInflater = LayoutInflater.from(mCtx);
        View view = layoutInflater.inflate(R.layout.myreservationcardview, null);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {

        final MyReservation myReservation = myReservationList.get(i);
        viewHolder.textViewResName.setText(myReservation.getReservationhostname());
        viewHolder.textViewResAddress.setText(myReservation.getReservationaddress());
        viewHolder.textViewResTime.setText(myReservation.getReservationtime());
        viewHolder.textViewResPrice.setText(myReservation.getReservationprice());
        Picasso.with(mCtx).load(myReservation.getReservationImageUrl()).fit().centerCrop().into(viewHolder.drivewayResImage);

        viewHolder.deleteResButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if time is up
                String time = myReservation.getReservationtime();
                int startHour = Integer.parseInt(time.substring(0, 2));
                int startMinutes = Integer.parseInt(time.substring(3, 5));
                String startM = time.substring(5, 7);
                int endHour = Integer.parseInt(time.substring(11, 13));
                int endMinutes = Integer.parseInt(time.substring(14, 16));
                String endM = time.substring(16, 18);
                calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY); //% 12;//4
                int currentMinute = calendar.get(Calendar.MINUTE);//40
                int currentM = calendar.get(Calendar.AM_PM);//PM == 1 AM != 1

                //convert start and end to 24hr time if not AM
                if(startM.equals("PM")){
                    if(startHour != 12){
                        startHour = startHour + 12;
                    }
                }
                if(endM.equals("PM")){
                    if(endHour != 12){
                        endHour = endHour + 12;
                    }
                }
                if((currentHour+(currentMinute/100))>(endHour+(endMinutes/100))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                    builder.setTitle("Delete Your Reservation?").setMessage("Are You Sure?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final int thepos = viewHolder.getAdapterPosition();
                            MyReservationFragment.MyReservationList.remove(thepos);
                            notifyItemRemoved(thepos);
                            notifyItemRangeChanged(thepos, MyReservationFragment.MyReservationList.size());
                            //delete from database
                            firebaseStorage = FirebaseStorage.getInstance();
                            firebaseUser = firebaseAuth.getInstance().getCurrentUser();
                            userId = firebaseUser.getUid();
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("MyReservationInfo");
                            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        int i = 0;
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            if (i == thepos) {
                                                //delete myreservation child card info
                                                dataSnapshot1.getRef().removeValue();
                                            }
                                            i = i + 1;
                                        }
                                    }
                                    //remove the listener after i am done
                                    databaseReference.removeEventListener(valueEventListener);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }
                    }).setNegativeButton("Cancel", null);
                    AlertDialog alert = builder.create();
                    alert.show();

                }else{
                    Toast.makeText(mCtx,"Time is not up yet.",Toast.LENGTH_LONG).show();
                }
            }
        });

        viewHolder.infoResButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    final int thepos = viewHolder.getAdapterPosition();
                    firebaseUser = firebaseAuth.getInstance().getCurrentUser();
                    userId = firebaseUser.getUid();
                    databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("MyReservationInfo");
                    valueEventListener2 = databaseReference2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                int i = 0;
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    if (i == thepos) {
                                        MyReservation myReservation2 = dataSnapshot1.getValue(MyReservation.class);
                                        String clientName = myReservation2.getReservationname();
                                        String clientPlate = myReservation2.getReservationPlate();
                                        String clientModel = myReservation2.getReservationModel();
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(mCtx);
                                        builder2.setTitle("Your Info:").setMessage("Name : " + clientName + "\n" + "Car Model : " + clientModel + "\n" + "License Plates : " + clientPlate).setNegativeButton("Close", null);
                                        AlertDialog alert = builder2.create();
                                        alert.show();
                                    }
                                    i = i + 1;
                                }
                            }
                            //remove the listener after i am done
                            databaseReference2.removeEventListener(valueEventListener2);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } catch (Exception e) {
                    Log.i("look", e.getMessage());
                }
            }
        });

        //change text to 'time' for client
        viewHolder.pendingResButton.setText(myReservation.getMyreservationrequested());

        databaseReference3 = FirebaseDatabase.getInstance().getReference().child("Users").child(myReservation.getReservationhostid()).child("MyPostInfo").child(myReservation.getReservationhostpostid());
            valueEventListener3 = databaseReference3.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final MyPost myPost = dataSnapshot.getValue(MyPost.class);
                            //save 'Time' to database of client
                            firebaseUser = firebaseAuth.getInstance().getCurrentUser();
                            userId = firebaseUser.getUid();
                            databaseReference4 = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("MyReservationInfo");
                            valueEventListener4 = databaseReference4.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final int thepos = viewHolder.getAdapterPosition();
                                    if (dataSnapshot.exists()) {
                                        int i = 0;
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            if (i == thepos) {
                                                if(myPost.getRequested().equals("Time") && myReservation.getMyreservationrequested().equals("Requested")) {
                                                    dataSnapshot1.child("myreservationrequested").getRef().setValue("Time");
                                                    //Refresh fragment to avoid card duplicate
                                                    MyReservationFragment f = new MyReservationFragment();
                                                    FragmentTransaction transaction = ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction();
                                                    transaction.replace(R.id.frame_content, f);
                                                    transaction.addToBackStack(null);
                                                    transaction.commit();
                                                    //remove after im done
                                                    databaseReference4.removeEventListener(valueEventListener4);
                                                    //remove the listener for host after i am done
                                                    databaseReference3.removeEventListener(valueEventListener3);
                                                }else if(myPost.getRequested().equals("Unrequested")){
                                                    //if host changes to 'unrequested': delete reservation info,update List, Toast a message
                                                    dataSnapshot1.getRef().removeValue();
                                                    MyReservationFragment f = new MyReservationFragment();
                                                    FragmentTransaction transaction = ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction();
                                                    transaction.replace(R.id.frame_content, f);
                                                    transaction.addToBackStack(null);
                                                    transaction.commit();
                                                    Toast.makeText(mCtx,"Host Denied You",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                            i = i + 1;
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        viewHolder.pendingResButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.pendingResButton.getText().toString().equals("Time")) {
                    String time = myReservation.getReservationtime();
                    int startHour = Integer.parseInt(time.substring(0, 2));
                    int startMinutes = Integer.parseInt(time.substring(3, 5));
                    String startM = time.substring(5, 7);
                    int endHour = Integer.parseInt(time.substring(11, 13));
                    int endMinutes = Integer.parseInt(time.substring(14, 16));
                    String endM = time.substring(16, 18);
                    calendar = Calendar.getInstance();
                    int currentHour = calendar.get(Calendar.HOUR_OF_DAY); //% 12;//4
                    int currentMinute = calendar.get(Calendar.MINUTE);//40
                    int currentM = calendar.get(Calendar.AM_PM);//PM == 1 AM != 1

                    //convert start and end to 24hr time if not AM
                    if(startM.equals("PM")){
                        if(startHour != 12){
                            startHour = startHour + 12;
                        }
                    }
                    if(endM.equals("PM")){
                        if(endHour != 12){
                            endHour = endHour + 12;
                        }
                    }
                    if((currentHour+(currentMinute/100))<(startHour+(startMinutes/100))){
                        Toast.makeText(mCtx,"Don't Park Yet!",Toast.LENGTH_LONG).show();
                    }else if((currentHour+(currentMinute/100))>(endHour+(endMinutes/100))){
                        Toast.makeText(mCtx,"Remove Your Car!",Toast.LENGTH_LONG).show();
                    } else{
                        totalMinutesLeft = ((endHour * 60) + endMinutes) - ((currentHour * 60) + currentMinute);
                        //countdown dialog
                        final AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                        builder.setTitle("Time Remaining: ").setMessage("00:00").setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        final AlertDialog alert = builder.create();
                        alert.show();
                        new CountDownTimer(totalMinutesLeft * 60 * 1000, 1000) {
                            @Override
                            public void onTick(long l) {
                                int hourLeft = (int) ((l / 60000) / 60);
                                int minutesLeft = (int) ((l / 60000) % 60) + 1;
                                alert.setMessage(hourLeft + "h : " + minutesLeft + "m");
                            }
                            @Override
                            public void onFinish() {
                                alert.setMessage("Time Is Up!");
                            }
                        }.start();
                    }
                    }
                }
        });

        //Spinner stuff
        final ArrayList<String> list = new ArrayList<>();
        list.add(viewHolder.pendingResButton.getText().toString());
        list.add(viewHolder.infoResButton.getText().toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mCtx, android.R.layout.simple_spinner_item, list);
        viewHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (b < a)
                {
                    b++;
                }else {
                    if (position == 0) {
                        viewHolder.pendingResButton.performClick();
                    } else {
                        viewHolder.infoResButton.performClick();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        viewHolder.spinner.setAdapter(adapter);
}

    @Override
    public int getItemCount() {
        return myReservationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textViewResName;
        TextView textViewResAddress;
        TextView textViewResTime;
        TextView textViewResPrice;
        Button deleteResButton;
        ImageView drivewayResImage;

        Button pendingResButton;
        Button infoResButton;

        Spinner spinner;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewResName = itemView.findViewById(R.id.textViewResName);
            textViewResAddress = itemView.findViewById(R.id.textViewResAddress);
            textViewResTime = itemView.findViewById(R.id.textViewResTime);
            textViewResPrice = itemView.findViewById(R.id.textViewResPrice);
            deleteResButton = itemView.findViewById(R.id.deleteResButton);
            drivewayResImage = itemView.findViewById(R.id.imageChatView);

            pendingResButton = itemView.findViewById(R.id.pendingResButton);
            infoResButton = itemView.findViewById(R.id.infoResButton);

            spinner = itemView.findViewById(R.id.spinner_services);
        }
    }
}
