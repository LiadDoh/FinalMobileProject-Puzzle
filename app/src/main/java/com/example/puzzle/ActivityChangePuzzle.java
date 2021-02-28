package com.example.puzzle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ActivityChangePuzzle extends AppCompatActivity {

    private int changePuzzle;
    private EditText changePuzzleETWritePuzzle;
    private EditText changePuzzleETWriteAnswer;
    private Button changePuzzleBTNFinish;
    private Button changePuzzleBTNCancel;
    private DatabaseReference reference;
    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_puzzle);
        findViews();
        initViews();
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sharedPref), Context.MODE_PRIVATE);
        changePuzzle = sharedPref.getInt(getString(R.string.changePuzzleNumber), 0);
        fAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.puzzlesDataBase)).child(fAuth.getCurrentUser().getUid());
    }

    private void initViews() {
        changePuzzleBTNFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (changePuzzle) {
                    case (1):
                        reference.child(getString(R.string.firstPuzzleText)).setValue(changePuzzleETWritePuzzle.getText().toString());
                        reference.child(getString(R.string.firstPuzzleAnswer)).setValue(changePuzzleETWriteAnswer.getText().toString());
                        break;
                    case (2):
                        reference.child(getString(R.string.secondPuzzleText)).setValue(changePuzzleETWritePuzzle.getText().toString());
                        reference.child(getString(R.string.secondPuzzleAnswer)).setValue(changePuzzleETWriteAnswer.getText().toString());
                        break;
                    case (3):
                        reference.child(getString(R.string.thirdPuzzleText)).setValue(changePuzzleETWritePuzzle.getText().toString());
                        reference.child(getString(R.string.thirdPuzzleAnswer)).setValue(changePuzzleETWriteAnswer.getText().toString());
                        break;
                }
                Toast.makeText(getApplicationContext(), getString(R.string.puzzleChangedSuccess), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), HomeScreenTabbed.class));
            }
        });

        changePuzzleBTNCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void findViews() {
        changePuzzleETWritePuzzle = findViewById(R.id.changePuzzleETWritePuzzle);
        changePuzzleETWriteAnswer = findViewById(R.id.changePuzzleETWriteAnswer);
        changePuzzleBTNFinish = findViewById(R.id.changePuzzleBTNFinish);
        changePuzzleBTNCancel = findViewById(R.id.changePuzzleBTNCancel);
    }

    public static class FaceBookUser extends Fragment {
        RecyclerView recyclerView;
        DatabaseReference reference;
        ArrayList<Profile> list;
        AdapterUsers adapterUsers;
        String TAG;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_facebook_user, container, false);
            recyclerView = view.findViewById(R.id.facebook_users);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            list = new ArrayList<Profile>();
            AccessToken token = AccessToken.getCurrentAccessToken();
            if (token != null)
                getAllUsers();
            return view;
        }


        private void getAllUsers() {
            final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
            //facebook token --> get all user friends
            AccessToken token = AccessToken.getCurrentAccessToken();
            reference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.members));
            list.clear();
            GraphRequest request = GraphRequest.newMyFriendsRequest(
                    token,
                    new GraphRequest.GraphJSONArrayCallback() {
                        @Override
                        public void onCompleted(JSONArray array, GraphResponse response) {
                            try {
                                for (int i = 0; i < array.length(); i++) {
                                    String userFBID = array.getJSONObject(i).get(getString(R.string.id)).toString();
                                    Query sortByQuery = reference.orderByChild(getString(R.string.facebookIDKey)).equalTo(userFBID);
                                    sortByQuery.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot scoreSnapshot : dataSnapshot.getChildren()) {
                                                Profile modelUser = scoreSnapshot.getValue(Profile.class);
                                                if (!modelUser.getUid().equals(fUser.getUid()) && modelUser.getFacebookID() != null) {
                                                    list.add(modelUser);

                                                }
                                            }
                                            adapterUsers = new AdapterUsers(getActivity(), list);
                                            recyclerView.setAdapter(adapterUsers);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Getting Post failed, log a message
                                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            request.executeAsync();
        }
    }
}