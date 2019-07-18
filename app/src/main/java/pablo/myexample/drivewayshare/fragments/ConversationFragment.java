package pablo.myexample.drivewayshare.fragments;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pablo.myexample.drivewayshare.AdapterFour;
import pablo.myexample.drivewayshare.MyConversation;
import pablo.myexample.drivewayshare.MyPost;
import pablo.myexample.drivewayshare.MyReservation;
import pablo.myexample.drivewayshare.R;
import pablo.myexample.drivewayshare.barActivity;

public class ConversationFragment extends Fragment {

    FirebaseDatabase database;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    String userId, myName, name, clientOrHost, chatId;
    EditText inputMessageText;
    Button send;
    Button returning;
    TextView otherName;
    RecyclerView recyclerView;
    AdapterFour adapter;
    public static List<MyConversation> myConversationList;
    DatabaseReference mRef, mRef2, mRef3;
    ValueEventListener valueEventListener, valueEventListener2;
    Boolean entered = false;
    MyConversation newText;

    private RequestQueue requestQueue;
    String URL = "https://fcm.googleapis.com/fcm/send";
    String sentFrom;

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        barActivity.showBottomNavigationView(getActivity().findViewById(R.id.botNav));
        mRef.removeEventListener(valueEventListener);
        mRef2.removeEventListener(valueEventListener2);

    }

    public ConversationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        barActivity.hideBottomNavigationView(getActivity().findViewById(R.id.botNav));
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        name = getArguments().getString("Name");
        clientOrHost = getArguments().getString("clientOrHost");

        return inflater.inflate(R.layout.fragment_conversation, container,false);

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        otherName = getView().findViewById(R.id.otherName);
        myConversationList = new ArrayList<>();
        recyclerView = getView().findViewById(R.id.conversationRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        inputMessageText = view.findViewById(R.id.inputmessagetext);
        inputMessageText.setEnabled(true);
        send = view.findViewById(R.id.send);

        //push notification
        requestQueue = Volley.newRequestQueue(getContext());

        returning = view.findViewById(R.id.patras);
        returning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //go back
                Fragment fragment = new ChatFragment();
                getFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();

            }
        });

        database = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();

        if (clientOrHost.equals("Host")) {//loop through reservations
            mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("MyReservationInfo");
            valueEventListener = mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            MyReservation myReservation = dataSnapshot1.getValue(MyReservation.class);
                            if (myReservation.getReservationhostname().equals(name)) {
                                otherName.setText(myReservation.getReservationhostname());
                                chatId = myReservation.getReservationhostpostid();
                                myName = myReservation.getReservationname().substring(0, 1);
                                //push notification stuff
                                FirebaseMessaging.getInstance().subscribeToTopic(chatId.substring(7));
                                sentFrom = myReservation.getReservationname();
                            }
                        }
                        displayChat();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        else {//loop through postings
            mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("MyPostInfo");
            valueEventListener = mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            MyPost myPost = dataSnapshot1.getValue(MyPost.class);
                            if (myPost.getClientName().equals(name)) {
                                otherName.setText(myPost.getClientName());
                                chatId = myPost.getCreatorPostId();
                                myName = myPost.getmypostname().substring(0, 1);
                                //push notification stuff
                                FirebaseMessaging.getInstance().subscribeToTopic(chatId.substring(7));
                                sentFrom = myPost.getmypostname();
                            }
                        }
                        displayChat();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        //update database while texting
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRef3 = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId);
                MyConversation myConversation = new MyConversation(inputMessageText.getText().toString(), myName);
                inputMessageText.setText("");
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                mRef3.push().setValue(myConversation);

                //send notification to other persons phone
                sendNotification();
            }
        });
    }

    private void displayChat() {
        //display chat on entering
        mRef2 = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId);
        valueEventListener2 = mRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (entered == false) {
                            MyConversation myConversation = dataSnapshot1.getValue(MyConversation.class);
                            myConversationList.add(myConversation);
                        } else {
                            newText = dataSnapshot1.getValue(MyConversation.class);
                        }
                    }
                    if (entered == false) {
                        adapter = new AdapterFour(getContext(), ConversationFragment.myConversationList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        myConversationList.add(newText);
                        adapter.notifyDataSetChanged();
                    }
                    entered = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendNotification() {

        JSONObject mainObj = new JSONObject();
        try {
            //this user and other user which are subscribed to the chatId will get the push notification
            mainObj.put("to", "/topics/" + chatId.substring(7));
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", "DS Message From:");
            notificationObj.put("body", sentFrom);
            mainObj.put("notification", notificationObj);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //code will run since success
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AIzaSyAflGpiQ5O88hlVaS1nN45hIyC_GS_pZWo");
                    return header;
                }
            };

            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}