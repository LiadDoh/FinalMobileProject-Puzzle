package com.example.puzzle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActivitySpecificPersonPuzzles extends AppCompatActivity {
    private String uID;
    private FirebaseAuth fAuth;
    private DatabaseReference riddleReference;
    private DatabaseReference profileReference;
    private String text1, text2, text3;
    private String answer1, answer2, answer3;
    private TextView firstPuzzle, secondPuzzle, thirdPuzzle;
    private EditText firstPuzzleAnswer, secondPuzzleAnswer, thirdPuzzleAnswer;
    private Button firstBTNApply, secondBTNApply, thirdBTNApply;
    private List firstList;
    private List secondList;
    private List thirdList;
    private int tries = 7;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_person_puzzles);
        findViews();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shardPreferences), Context.MODE_PRIVATE);
        uID = sharedPref.getString(getString(R.string.shardPreferencesIDkey), getString(R.string.personNotFound));
        riddleReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.puzzlesDataBase)).child(uID);
        profileReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.members)).child(fAuth.getCurrentUser().getUid());
        initViews();

    }

    private void initViews() {
        profileReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                score = Integer.parseInt(snapshot.child(getString(R.string.scoreKey)).getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        riddleReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    text1 = snapshot.child(getString(R.string.firstPuzzleText)).getValue().toString();
                    text2 = snapshot.child(getString(R.string.secondPuzzleText)).getValue().toString();
                    text3 = snapshot.child(getString(R.string.thirdPuzzleText)).getValue().toString();
                    answer1 = snapshot.child(getString(R.string.firstPuzzleAnswer)).getValue().toString();
                    answer2 = snapshot.child(getString(R.string.secondPuzzleAnswer)).getValue().toString();
                    answer3 = snapshot.child(getString(R.string.thirdPuzzleAnswer)).getValue().toString();
                    firstPuzzle.setText(text1);
                    secondPuzzle.setText(text2);
                    thirdPuzzle.setText(text3);
                    firstList = new ArrayList();
                    secondList = new ArrayList();
                    thirdList = new ArrayList();
                    if (snapshot.child(getString(R.string.firstPuzzleSolvedByKey)).exists()) {
                        firstList = (List) snapshot.child(getString(R.string.firstPuzzleSolvedByKey)).getValue();
                        for (Object id : firstList) {
                            if (id.equals(fAuth.getCurrentUser().getUid())) {
                                firstPuzzle.setVisibility(View.INVISIBLE);
                                firstPuzzleAnswer.setVisibility(View.INVISIBLE);
                                firstBTNApply.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                    if (snapshot.child(getString(R.string.secondPuzzleSolvedByKey)).exists()) {
                        secondList = (List) snapshot.child(getString(R.string.secondPuzzleSolvedByKey)).getValue();
                        for (Object id : secondList) {
                            if (id.equals(fAuth.getCurrentUser().getUid())) {
                                secondPuzzle.setVisibility(View.INVISIBLE);
                                secondPuzzleAnswer.setVisibility(View.INVISIBLE);
                                secondBTNApply.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                    if (snapshot.child(getString(R.string.thirdPuzzleSolvedByKey)).exists()) {
                        thirdList = (List) snapshot.child(getString(R.string.thirdPuzzleSolvedByKey)).getValue();
                        for (Object id : thirdList) {
                            if (id.equals(fAuth.getCurrentUser().getUid())) {
                                thirdPuzzle.setVisibility(View.INVISIBLE);
                                thirdPuzzleAnswer.setVisibility(View.INVISIBLE);
                                thirdBTNApply.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                    firstBTNApply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String yourAnswer = firstPuzzleAnswer.getText().toString();
                            if (yourAnswer.toLowerCase().equals(answer1.toLowerCase())) {
                                Toast.makeText(getApplicationContext(), getString(R.string.rightAnswer) + " " + tries + " " + getString(R.string.points), Toast.LENGTH_SHORT).show();
                                firstList.add(fAuth.getCurrentUser().getUid());
                                score = score + tries;
                                profileReference.child(getString(R.string.scoreKey)).setValue(score);
                                riddleReference.child(getString(R.string.firstPuzzleSolvedByKey)).setValue(firstList);
                            } else {
                                if (tries > 1)
                                    tries = tries - 1;
                                Toast.makeText(getApplicationContext(), getString(R.string.wrongAnswer) + " " + tries, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    secondBTNApply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String yourAnswer = secondPuzzleAnswer.getText().toString();
                            if (yourAnswer.toLowerCase().equals(answer2.toLowerCase())) {
                                Toast.makeText(getApplicationContext(), getString(R.string.rightAnswer) + " " + tries + " " + getString(R.string.points), Toast.LENGTH_SHORT).show();
                                secondList.add(fAuth.getCurrentUser().getUid());
                                score = score + tries;
                                profileReference.child(getString(R.string.scoreKey)).setValue(score);
                                riddleReference.child(getString(R.string.secondPuzzleSolvedByKey)).setValue(secondList);
                            } else {
                                if (tries > 1)
                                    tries = tries - 1;
                                Toast.makeText(getApplicationContext(), getString(R.string.wrongAnswer) + " " + tries, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    thirdBTNApply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String yourAnswer = thirdPuzzleAnswer.getText().toString();
                            if (yourAnswer.toLowerCase().equals(answer3.toLowerCase())) {
                                Toast.makeText(getApplicationContext(), getString(R.string.rightAnswer) + " " + tries + " " + getString(R.string.points), Toast.LENGTH_SHORT).show();
                                thirdList.add(fAuth.getCurrentUser().getUid());
                                score = score + tries;
                                profileReference.child(getString(R.string.scoreKey)).setValue(score);
                                riddleReference.child(getString(R.string.thirdPuzzleSolvedByKey)).setValue(thirdList);
                            } else {
                                if (tries > 1)
                                    tries = tries - 1;
                                Toast.makeText(getApplicationContext(), getString(R.string.wrongAnswer) + " " + tries, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }

    private void findViews() {
        fAuth = FirebaseAuth.getInstance();
        firstPuzzle = findViewById(R.id.firstPuzzle);
        secondPuzzle = findViewById(R.id.secondPuzzle);
        thirdPuzzle = findViewById(R.id.thirdPuzzle);
        firstPuzzleAnswer = findViewById(R.id.firstPuzzleAnswer);
        secondPuzzleAnswer = findViewById(R.id.secondPuzzleAnswer);
        thirdPuzzleAnswer = findViewById(R.id.thirdPuzzleAnswer);
        firstBTNApply = findViewById(R.id.firstBTNApply);
        secondBTNApply = findViewById(R.id.secondBTNApply);
        thirdBTNApply = findViewById(R.id.thirdBTNApply);
    }
}
