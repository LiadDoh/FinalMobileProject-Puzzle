package com.example.puzzle;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FragmentScoreboard extends Fragment {

    private static final String TAG = "";
    public FirebaseAuth fAuth;
    private TextView scoreboardLBLUserScore;
    private TextView scoreboardLBLScore1;
    private TextView scoreboardLBLScore2;
    private TextView scoreboardLBLScore3;
    private DatabaseReference db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scoreboard, container, false);
        findViews(view);
        db = FirebaseDatabase.getInstance().getReference().child(getString(R.string.members));
        fAuth = FirebaseAuth.getInstance();
        calculateScoreBoard();
        return view;
    }

    private void findViews(View view) {
        scoreboardLBLUserScore = view.findViewById(R.id.scoreboardLBLUserScore);
        scoreboardLBLScore1 = view.findViewById(R.id.scoreboardLBLScore1);
        scoreboardLBLScore2 = view.findViewById(R.id.scoreboardLBLScore2);
        scoreboardLBLScore3 = view.findViewById(R.id.scoreboardLBLScore3);
    }


    public void calculateScoreBoard() {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scoreboardLBLUserScore.setText("your Score is: " + snapshot.child(fAuth.getUid()).child(getString(R.string.scoreKey)).getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        Query sortByScoreQuery = db.orderByChild(getString(R.string.scoreKey)).limitToLast(3);

        final TextView[] tl = {scoreboardLBLScore3, scoreboardLBLScore2, scoreboardLBLScore1};
        sortByScoreQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot scoreSnapshot : dataSnapshot.getChildren()) {
                    tl[i].setText(scoreSnapshot.child(getString(R.string.firstNameKey)).getValue() + " " +
                            scoreSnapshot.child(getString(R.string.lastNamekey)).getValue() +
                            " \n Score: " + scoreSnapshot.child(getString(R.string.scoreKey)).getValue());
                    i++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

    }
}