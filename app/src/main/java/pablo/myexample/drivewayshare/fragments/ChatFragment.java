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

import pablo.myexample.drivewayshare.AdapterThree;
import pablo.myexample.drivewayshare.LoginSignUp;
import pablo.myexample.drivewayshare.MyChat;
import pablo.myexample.drivewayshare.MyPost;
import pablo.myexample.drivewayshare.MyReservation;
import pablo.myexample.drivewayshare.R;
import pablo.myexample.drivewayshare.User;
import pablo.myexample.drivewayshare.barActivity;
import pablo.myexample.drivewayshare.profile;

public class ChatFragment extends Fragment{

    FirebaseAuth firebaseAuth;
    Button menuButton;
    DatabaseReference mRef2, mRef3;
    FirebaseDatabase database;
    FirebaseUser firebaseUser;
    String userId;
    ValueEventListener valueEventListener2, valueEventListener3;

    RecyclerView recyclerView;
    AdapterThree theAdapter;
    public static List<MyChat> chatList;

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mRef2.removeEventListener(valueEventListener2);
        mRef3.removeEventListener(valueEventListener3);

    }

    // Required empty public constructor
    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatList = new ArrayList<>();
        recyclerView = getView().findViewById(R.id.chatRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        menuButton = getView().findViewById(R.id.chatMenuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barActivity.OpenMenu();
            }
        });

        database = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();

                mRef2 = database.getReference().child("Users").child(userId).child("MyReservationInfo");
                valueEventListener2 = mRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                MyReservation myReservation = dataSnapshot1.getValue(MyReservation.class);
                                final MyChat myChat = new MyChat(myReservation.getReservationhostname(), myReservation.getReservationImageUrl(), "Host");
                                if("Time".equals(myReservation.getMyreservationrequested())) {
                                    chatList.add(myChat);
                                }
                                theAdapter = new AdapterThree(getContext(), ChatFragment.chatList);
                                recyclerView.setAdapter(theAdapter);
                                theAdapter.setOnItemClickListener(new AdapterThree.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        //to conversation fragment
                                        Fragment fragment = new ConversationFragment();
                                        Bundle args = new Bundle();
                                        args.putString("Name", myChat.getChatName());
                                        args.putString("clientOrHost", myChat.getClientOrNotText());
                                        fragment.setArguments(args);
                                        getFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                mRef3 = database.getReference().child("Users").child(userId).child("MyPostInfo");
                valueEventListener3 = mRef3.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                MyPost myPost = dataSnapshot1.getValue(MyPost.class);
                                final MyChat myChat = new MyChat(myPost.getClientName(), myPost.getImageUrl(), "Client");
                                if("Time".equals(myPost.getRequested())) {
                                    chatList.add(myChat);
                                }
                                theAdapter = new AdapterThree(getContext(), ChatFragment.chatList);
                                recyclerView.setAdapter(theAdapter);
                                theAdapter.setOnItemClickListener(new AdapterThree.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        //to conversation fragment
                                        Fragment fragment = new ConversationFragment();
                                        Bundle args = new Bundle();
                                        args.putString("Name", myChat.getChatName());
                                        args.putString("clientOrHost", myChat.getClientOrNotText());
                                        fragment.setArguments(args);
                                        getFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
        }
}
