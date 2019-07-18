package pablo.myexample.drivewayshare;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import pablo.myexample.drivewayshare.fragments.ChatFragment;
import pablo.myexample.drivewayshare.fragments.MapFragment;
import pablo.myexample.drivewayshare.fragments.MyPostFragment;
import pablo.myexample.drivewayshare.fragments.MyReservationFragment;

public class barActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ValueEventListener valueEventListener;
    FirebaseDatabase database;
    FirebaseUser firebaseUser;
    String userId;
    DatabaseReference mRef;
    BottomNavigationView bottomNavigationView;
    Fragment fragment;
    static DrawerLayout drawerLayout;
    NavigationView navigationView;

    public static void OpenMenu() {

        drawerLayout.openDrawer(GravityCompat.START);

    }

    public static void hideBottomNavigationView(View view) {

        view.clearAnimation();
        view.animate().translationY(view.getHeight());

    }

    public static void showBottomNavigationView(View view) {

        view.clearAnimation();
        view.animate().translationY(0);

    }

    public void retrieveMenuData() {

        database = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();

        mRef = database.getReference().child("Users").child(userId);
        valueEventListener = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                NavigationView navigationView = findViewById(R.id.navView);
                View headerView = navigationView.getHeaderView(0);
                TextView name = headerView.findViewById(R.id.nameDisplay);
                name.setText(user.getUserName());
                TextView carmodel = headerView.findViewById(R.id.carmodelDisplay);
                carmodel.setText(user.getCarModel());
                TextView licenseplate = headerView.findViewById(R.id.licenseplateDisplay);
                licenseplate.setText(user.getLicensePlate());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView = findViewById(R.id.botNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new MapFragment()).commit();

        retrieveMenuData();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Intent intent;

        switch (menuItem.getItemId()) {

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(getApplicationContext(), LoginSignUp.class);
                startActivity(intent);
                finish();
                break;

            case R.id.profile:
                intent = new Intent(getApplicationContext(), profile.class);
                startActivity(intent);
                break;

        }

        return true;

    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()) {

                case R.id.map:
                    fragment = new MapFragment();
                    break;

                case R.id.mypost:
                    fragment = new MyPostFragment();
                    break;

                case R.id.reservations:
                    fragment = new MyReservationFragment();
                    break;

                case R.id.chat:
                    fragment = new ChatFragment();
                    break;

            }

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
            return true;

        }
    };
}
