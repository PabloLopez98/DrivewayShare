package pablo.myexample.drivewayshare.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

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
import java.util.Objects;

import pablo.myexample.drivewayshare.Adapter;
import pablo.myexample.drivewayshare.LoginSignUp;
import pablo.myexample.drivewayshare.MyPost;
import pablo.myexample.drivewayshare.R;
import pablo.myexample.drivewayshare.User;
import pablo.myexample.drivewayshare.barActivity;
import pablo.myexample.drivewayshare.inputMyPostInfo;
import pablo.myexample.drivewayshare.profile;

public class MyPostFragment extends Fragment implements View.OnClickListener {

    RecyclerView recyclerView;
    Adapter adapter;
    public static List<MyPost> MyPostList;
    FirebaseAuth firebaseAuth;
    DrawerLayout drawer;
    Button menuButton;
    Button addPostButton;
    DatabaseReference mRef2;
    FirebaseDatabase database;
    String userId;
    ValueEventListener valueEventListener;
    ValueEventListener valueEventListener2;
    Boolean entered = false;

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
        return inflater.inflate(R.layout.fragment_my_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        menuButton = getView().findViewById(R.id.menubutton);
        menuButton.setOnClickListener(this);
        addPostButton = getView().findViewById(R.id.addPostButton);
        addPostButton.setOnClickListener(this);

        MyPostList = new ArrayList<>();
        recyclerView = getView().findViewById(R.id.recycleriView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        database = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();

        //Make it so that i loop through all child nodes of MyPostInfo Child to display all cards!!!
        mRef2 = database.getReference().child("Users").child(userId).child("MyPostInfo");
        valueEventListener2 = mRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    if (entered == false) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            MyPost myPost = dataSnapshot1.getValue(MyPost.class);
                            MyPostList.add(myPost);
                        }
                        adapter = new Adapter(getContext(), MyPostFragment.MyPostList);
                        recyclerView.setAdapter(adapter);
                        entered = true;
                    } else {
                        MyPostFragment f = new MyPostFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_content, f);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menubutton:
                barActivity.OpenMenu();
                break;
            case R.id.addPostButton:
                Intent toinputMyPostInfo = new Intent(getContext(), inputMyPostInfo.class);
                startActivity(toinputMyPostInfo);
                break;
        }

    }
}