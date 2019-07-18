package pablo.myexample.drivewayshare.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import pablo.myexample.drivewayshare.FillDriverInfo;
import pablo.myexample.drivewayshare.Filter;
import pablo.myexample.drivewayshare.FilterSearch;
import pablo.myexample.drivewayshare.LoginSignUp;
import pablo.myexample.drivewayshare.MarkerTag;
import pablo.myexample.drivewayshare.MyPost;
import pablo.myexample.drivewayshare.MyReservation;
import pablo.myexample.drivewayshare.R;
import pablo.myexample.drivewayshare.User;
import pablo.myexample.drivewayshare.barActivity;
import pablo.myexample.drivewayshare.profile;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    Boolean mLocationPermissionsGranted = false;
    EditText mSearchText;
    float DEFAULT_ZOOM = 10;
    Button menuButton;
    FirebaseAuth firebaseAuth;
    BitmapDrawable bitmapdraw;
    Bitmap b;
    Bitmap smallMarker;
    Button toFilterButton;
    DatabaseReference mapRef2;
    ValueEventListener mapvalueEvenetListener2;
    public int maxPrice = 100;
    public String startingHour = "11:59PM";
    DatabaseReference mapRef3, clientMRef;
    ValueEventListener mapvalueEventListener3;
    ValueEventListener clientListener;
    FirebaseDatabase database;

    // Required empty public constructor
    public MapFragment() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mapRef2.removeEventListener(mapvalueEvenetListener2);
        mapRef3.removeEventListener(mapvalueEventListener3);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLocationPermission();

    }

    public void searchLocation() {

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    geoLocate();

                }

                return true;

            }
        });
    }

    public void geoLocate() {

        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();

        try {

            list = geocoder.getFromLocationName(mSearchText.getText().toString(), 1);

        } catch (Exception e) {

            Log.i("geoLocateError", String.valueOf(e));

        }

        if (list.size() > 0) {

            Address address = list.get(0);
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM);

        }
    }

    public void geoLocateWhenStarting(String address, int price, String time, String creatorId, String creatorPostId, String takenOrNot) {

        Geocoder geocoder2 = new Geocoder(getContext());
        List<Address> list2 = new ArrayList<>();

        try {

            list2 = geocoder2.getFromLocationName(address, 1);

        } catch (Exception e) {

            Log.i("geoLocateError2", String.valueOf(e));

        }

        if (list2.size() > 0) {

            String ReservedOrNot;
            Address addressObject = list2.get(0);

            if (takenOrNot.equals("clientName")) {

                ReservedOrNot = "Unreserved";

            } else {

                ReservedOrNot = "Reserved";

            }

            MarkerTag tag = new MarkerTag(creatorId, creatorPostId, ReservedOrNot);
            Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(addressObject.getLatitude(), addressObject.getLongitude())).title("$" + String.valueOf(price)).snippet(address + "\n" + time + "\n" + ReservedOrNot));
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            marker.setTag(tag);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_map, container, false);
        return mView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSearchText = getView().findViewById(R.id.searchInput);

        database = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();

        menuButton = getView().findViewById(R.id.menubutton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                barActivity.OpenMenu();

            }
        });

        mMapView = mView.findViewById(R.id.theMap);

        if (mMapView != null) {

            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);

        }

        //to retrieve filter conditions
        mapRef3 = database.getReference().child("Users").child(userId).child("SearchFilter");
        mapvalueEventListener3 = mapRef3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    Filter filter = dataSnapshot.getValue(Filter.class);
                    maxPrice = Integer.parseInt(filter.getMaxPrice());
                    startingHour = filter.getStartingHour();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //plot by condition
        mapRef2 = database.getReference().child("Users");
        mapvalueEvenetListener2 = mapRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    for (DataSnapshot uniqueId : dataSnapshot.getChildren()) {

                        for (DataSnapshot uniquePost : uniqueId.child("MyPostInfo").getChildren()) {

                            MyPost myPost = uniquePost.getValue(MyPost.class);
                            int postPrice = Integer.parseInt(myPost.getPrice());
                            String postAddress = myPost.getAddress();
                            String time = myPost.getTime();
                            String creatorId = myPost.getCreatorId();
                            String creatorPostId = myPost.getCreatorPostId();
                            //to tell them its taken or not
                            String takenOrNot = myPost.getClientName();

                            //Umbrella filter: $
                            if (postPrice <= maxPrice) {

                                if (myPost.getTime().substring(5, 7).equals("AM") && startingHour.substring(5, 7).equals("AM")) {

                                    if (Integer.parseInt(myPost.getTime().substring(0, 2)) <= Integer.parseInt(startingHour.substring(0, 2))) {

                                        geoLocateWhenStarting(postAddress, postPrice, time, creatorId, creatorPostId, takenOrNot);
                                    }

                                } else if (startingHour.substring(5, 7).equals("PM")) {

                                    if ((myPost.getTime().substring(5, 7).equals("PM") && Integer.parseInt(myPost.getTime().substring(0, 2)) <= Integer.parseInt(startingHour.substring(0, 2))) || (myPost.getTime().substring(5, 7).equals("AM"))) {

                                        geoLocateWhenStarting(postAddress, postPrice, time, creatorId, creatorPostId, takenOrNot);

                                    }
                                }
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //go to the filter screen
        toFilterButton = getView().findViewById(R.id.filterButton);
        toFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intenttofilter = new Intent(getContext(), FilterSearch.class);
                startActivity(intenttofilter);

            }
        });
    }

    private void getLocationPermission() {

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionsGranted = true;

        } else {

            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if (permissions.length == 1 && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                mLocationPermissionsGranted = true;

            } else {

                Toast.makeText(getContext(), "Location Denied", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void getDeviceLocation() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_LONG).show();

        } else {//else if location permission is true

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            Task location = mFusedLocationProviderClient.getLastLocation();

            location.addOnCompleteListener(new OnCompleteListener() {

                @Override
                public void onComplete(@NonNull Task task) {

                    if (task.isSuccessful()) {

                        Location currentLocation = (Location) task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);

                    } else {

                        //do nothing i guess

                    }
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap = googleMap;

        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {

                return null;

            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getContext());
                info.setOrientation(LinearLayout.VERTICAL);
                TextView title = new TextView(getContext());
                title.setGravity(Gravity.CENTER);
                title.setTextColor(Color.BLACK);
                title.setText(marker.getTitle());
                TextView snippet = new TextView(getContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());
                info.addView(title);
                info.addView(snippet);

                return info;

            }
        });

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {

                final MarkerTag getTagInfo = (MarkerTag) marker.getTag();
                FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
                String userId = firebaseUser.getUid();

                //get client reservation data to tell client that your reservation
                clientMRef = database.getReference().child("Users").child(userId).child("MyReservationInfo");
                clientListener = clientMRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot data : dataSnapshot.getChildren()) {

                            MyReservation myReservation = data.getValue(MyReservation.class);

                            if (myReservation.getReservationhostid().equals(myReservation.getReservationhostid())) {

                                Toast.makeText(getContext(), "Your Reservation", Toast.LENGTH_LONG).show();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                if (getTagInfo.getCreatorId().equals(userId)) {

                    Toast.makeText(getContext(), "Your Post", Toast.LENGTH_LONG).show();

                } else if (getTagInfo.getReservedOrNot().equals("Reserved")) {

                    Toast.makeText(getContext(), "Reserved", Toast.LENGTH_LONG).show();

                } else {

                    Intent toFillDriverInfo = new Intent(getContext(), FillDriverInfo.class);
                    toFillDriverInfo.putExtra("creatorId", getTagInfo.getCreatorId());
                    toFillDriverInfo.putExtra("creatorPostId", getTagInfo.getCreatorPostId());
                    startActivity(toFillDriverInfo);

                }
            }
        });

        //customize the map marker
        bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.mapmarker);
        b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, 84, 120, false);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_LONG).show();

        }

        if (mLocationPermissionsGranted) {

            mGoogleMap.setMyLocationEnabled(true);
            getDeviceLocation();
            searchLocation();

        }
    }

    private void moveCamera(LatLng latLng, float zoom) {

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

    }
}




