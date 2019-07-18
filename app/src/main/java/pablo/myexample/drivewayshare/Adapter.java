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
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pablo.myexample.drivewayshare.fragments.MyPostFragment;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private Context mCtx;
    private List<MyPost> myPostList;

    DatabaseReference databaseReference, databaseReference2, databaseReference3,databaseReference4;
    ValueEventListener valueEventListener, valueEventListener2, valueEventListener3, valueEventListener4;
    String userId;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    String cName, cPlate, cModel;
    Calendar calendar;
    TimePickerDialog timePickerDialog;

    int totalMinutesLeft;

    int a = 0;
    int b = 0;

    public Adapter(Context mCtx, List<MyPost> myPostList) {

        this.mCtx = mCtx;
        this.myPostList = myPostList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        a = 1;
        LayoutInflater layoutInflater = LayoutInflater.from(mCtx);
        View view = layoutInflater.inflate(R.layout.mypostcardview, null);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {

        final MyPost myPost = myPostList.get(i);

        viewHolder.textViewName.setText(myPost.getmypostname());
        viewHolder.textViewAddress.setText(myPost.getAddress());
        viewHolder.textViewTime.setText(myPost.getTime());
        viewHolder.textViewPrice.setText(myPost.getPrice());
        Picasso.with(mCtx).load(myPost.getImageUrl()).fit().centerCrop().into(viewHolder.drivewayImage);

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if time is up
                String time = myPost.getTime();
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
                if(((currentHour+(currentMinute/100))>(endHour+(endMinutes/100))) || viewHolder.pendingButton.getText().equals("Unrequested")){
                AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                builder.setTitle("Delete Your Post?").setMessage("Are You Sure?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final int thepos = viewHolder.getAdapterPosition();
                        MyPostFragment.MyPostList.remove(thepos);
                        notifyItemRemoved(thepos);
                        notifyItemRangeChanged(thepos, MyPostFragment.MyPostList.size());

                        //delete from database
                        firebaseStorage = FirebaseStorage.getInstance();
                        firebaseUser = firebaseAuth.getInstance().getCurrentUser();
                        userId = firebaseUser.getUid();
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("MyPostInfo");
                        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {

                                    int i = 0;

                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                        if (i == thepos) {
                                            MyPost myPost = dataSnapshot1.getValue(MyPost.class);
                                            //delete Driveway picture
                                            String ImageUrl = myPost.getImageUrl();
                                            StorageReference imageRef = firebaseStorage.getReferenceFromUrl(ImageUrl);
                                            imageRef.delete();
                                            //delete mypost child card info
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
        }});

        viewHolder.pendingButton.setText(myPost.getRequested());

        //refresh the fragment here to avoid duplicates after client accepts host driveway
        /*firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();
        databaseReference3 = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("MyPostInfo");
        valueEventListener3 = databaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final int thepos = viewHolder.getAdapterPosition();
                if (dataSnapshot.exists()) {
                    int i = 0;
                    for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (i == thepos) {
                            final MyPost myPost = dataSnapshot1.getValue(MyPost.class);
                            String requestedornot = myPost.getRequested();
                            if (viewHolder.pendingButton.getText().equals("Unrequested") && requestedornot.equals("Requested")){
                                MyPostFragment f = new MyPostFragment();
                                FragmentTransaction transaction = ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.frame_content, f);
                                transaction.addToBackStack(null);
                                transaction.commit();
                                databaseReference3.removeEventListener(valueEventListener3);
                            }
                        }
                        i = i + 1;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }});*/

            viewHolder.pendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.pendingButton.getText().toString().equals("Requested")){
                    final int thepos = viewHolder.getAdapterPosition();
                    firebaseUser = firebaseAuth.getInstance().getCurrentUser();
                    userId = firebaseUser.getUid();
                    databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("MyPostInfo");
                    valueEventListener2 = databaseReference2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                int i = 0;
                                for(final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    if(i == thepos){
                                        final MyPost myPost = dataSnapshot1.getValue(MyPost.class);
                                        String clientName = myPost.getClientName();
                                        String clientPlate = myPost.getClientPlate();
                                        String clientModel = myPost.getClientModel();
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(mCtx);
                                        builder2.setTitle("Client Info:").setMessage("Name : " + clientName + "\n" + "Car Model : " + clientModel +  "\n" + "License Plates : " + clientPlate).setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //change 'requested' to 'Time'
                                                dataSnapshot1.child("requested").getRef().setValue("Time");
                                                //and add chat messages branch
                                                FirebaseDatabase.getInstance().getReference().child("Chats").child(myPost.getCreatorPostId()).setValue("Once time is up, Messages will be deleted.");
                                                //refresh fragment
                                                MyPostFragment f = new MyPostFragment();
                                                FragmentTransaction transaction = ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction();
                                                transaction.replace(R.id.frame_content, f);
                                                transaction.addToBackStack(null);
                                                transaction.commit();
                                            }
                                        }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //change db 'requested' to 'unrequested' and refresh
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("MyPostInfo").child(myPost.getCreatorPostId()).child("requested").setValue("Unrequested");
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("MyPostInfo").child(myPost.getCreatorPostId()).child("clientModel").setValue("clientModel");
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("MyPostInfo").child(myPost.getCreatorPostId()).child("clientName").setValue("clientName");
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("MyPostInfo").child(myPost.getCreatorPostId()).child("clientPlate").setValue("clientPlate");

                                                MyPostFragment f = new MyPostFragment();
                                                FragmentTransaction transaction = ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction();
                                                transaction.replace(R.id.frame_content, f);
                                                transaction.addToBackStack(null);
                                                transaction.commit();
                                            }
                                        });
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
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                } else if(viewHolder.pendingButton.getText().toString().equals("Unrequested")){
                    Toast.makeText(mCtx,"No Client Yet.", Toast.LENGTH_LONG).show();
                } else{
                    if (viewHolder.pendingButton.getText().toString().equals("Time")) {
                        String time = myPost.getTime();
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
        }});

        final int thepos = viewHolder.getAdapterPosition();
        firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();
        databaseReference4 = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("MyPostInfo");
        valueEventListener4 = databaseReference4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int i = 0;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (i == thepos) {
                            MyPost mypost1 = dataSnapshot1.getValue(MyPost.class);
                            cName = mypost1.getClientName();
                            cPlate = mypost1.getClientPlate();
                            cModel = mypost1.getClientModel();
                        }
                        i = i + 1;
                    }
                }
                //remove the listener after i am done
                databaseReference4.removeEventListener(valueEventListener4);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        if(viewHolder.pendingButton.getText().toString().equals("Time")) {
            viewHolder.infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builderlast = new AlertDialog.Builder(mCtx);
                    builderlast.setTitle("Client Info:").setMessage("Name : " + cName + "\n" + "Car Model : " + cModel + "\n" + "License Plates : " + cPlate).setNegativeButton("Close", null);
                    AlertDialog alert = builderlast.create();
                    alert.show();
                }});}


        //Spinner stuff
        final ArrayList<String> list = new ArrayList<>();
        list.add(viewHolder.pendingButton.getText().toString());
        list.add(viewHolder.infoButton.getText().toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mCtx, android.R.layout.simple_spinner_item, list);
        viewHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (b < a)
                {
                    b++;
                }else{
                    if (position == 0) {
                        viewHolder.pendingButton.performClick();
                    } else {
                        viewHolder.infoButton.performClick();
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

        return myPostList.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textViewName;
        TextView textViewAddress;
        TextView textViewTime;
        TextView textViewPrice;
        Button deleteButton;
        ImageView drivewayImage;
        Button pendingButton;
        Button infoButton;
        Spinner spinner;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewName);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            drivewayImage = itemView.findViewById(R.id.imageChatView);
            pendingButton = itemView.findViewById(R.id.pendingButton);
            infoButton = itemView.findViewById(R.id.infoButton);

            spinner = itemView.findViewById(R.id.spinner_services);
        }
    }
}
