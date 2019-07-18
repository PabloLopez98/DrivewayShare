package pablo.myexample.drivewayshare.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pablo.myexample.drivewayshare.AdapterTwo;
import pablo.myexample.drivewayshare.LoginSignUp;
import pablo.myexample.drivewayshare.MyReservation;
import pablo.myexample.drivewayshare.R;
import pablo.myexample.drivewayshare.User;
import pablo.myexample.drivewayshare.barActivity;
import pablo.myexample.drivewayshare.profile;

public class MyReservationFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterTwo theAdapter;
    public static List<MyReservation> MyReservationList;
    FirebaseAuth firebaseAuth;
    Button menuButton;
    DatabaseReference mRef2;
    FirebaseDatabase database;
    FirebaseUser firebaseUser;
    String userId;
    ValueEventListener valueEventListener2;

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mRef2.removeEventListener(valueEventListener2);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_reservation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        menuButton = getView().findViewById(R.id.menuResbutton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barActivity.OpenMenu();
            }
        });

        MyReservationList = new ArrayList<>();
        recyclerView = getView().findViewById(R.id.recycleriView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        database = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();

        //Make it so that i loop through all child nodes of MyReservationInfo Child to display all reservation cards!!!
        mRef2 = database.getReference().child("Users").child(userId).child("MyReservationInfo");
        valueEventListener2 = mRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        MyReservation myReservation = dataSnapshot1.getValue(MyReservation.class);
                        MyReservationList.add(myReservation);
                        theAdapter = new AdapterTwo(getContext(), MyReservationFragment.MyReservationList);
                        recyclerView.setAdapter(theAdapter);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
}
