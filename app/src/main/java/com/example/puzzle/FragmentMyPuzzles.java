package com.example.puzzle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FragmentMyPuzzles extends Fragment {

    private DatabaseReference reference;
    private FirebaseAuth fAuth;
    private TextView myPuzzleLBLPuzzle1, myPuzzleLBLPuzzle2, myPuzzleLBLPuzzle3;
    private TextView myPuzzleLBLAnswer1, myPuzzleLBLAnswer2, myPuzzleLBLAnswer3;
    private Button myPuzzleLBTNChangeFirst, myPuzzleLBTNChangeSecond, myPuzzleLBTNChangeThird;
    private SharedPreferences sharedPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_puzzles, container, false);
        findViews(view);
        sharedPref = this.getActivity().getSharedPreferences(getString(R.string.sharedPref), Context.MODE_PRIVATE);
        initViews();


        fAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.puzzlesDataBase)).child(fAuth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    myPuzzleLBLPuzzle1.setText(getString(R.string.firstPuzzleSubTitle) + snapshot.child(getString(R.string.firstPuzzleText)).getValue().toString());
                    myPuzzleLBLPuzzle2.setText(getString(R.string.secondPuzzleSubTitle) + snapshot.child(getString(R.string.secondPuzzleText)).getValue().toString());
                    myPuzzleLBLPuzzle3.setText(getString(R.string.thirdPuzzleSubTitle) + snapshot.child(getString(R.string.thirdPuzzleText)).getValue().toString());
                    myPuzzleLBLAnswer1.setText(getString(R.string.firstPuzzleAnswerSubTitle) + snapshot.child(getString(R.string.firstPuzzleAnswer)).getValue().toString() + "\n");
                    myPuzzleLBLAnswer2.setText(getString(R.string.secondPuzzleAnswerSubTitle) + snapshot.child(getString(R.string.secondPuzzleAnswer)).getValue().toString() + "\n");
                    myPuzzleLBLAnswer3.setText(getString(R.string.thirdPuzzleAnswerSubTitle) + snapshot.child(getString(R.string.thirdPuzzleAnswer)).getValue().toString() + "\n");
                } else {
                    myPuzzleLBLPuzzle1.setText(getString(R.string.noFirstPuzzleFound));
                    myPuzzleLBLPuzzle2.setText(getString(R.string.noSecondPuzzleFound));
                    myPuzzleLBLPuzzle3.setText(getString(R.string.noThirdPuzzleFound));
                    myPuzzleLBLAnswer1.setText(getString(R.string.noFirstPuzzleAnswer));
                    myPuzzleLBLAnswer2.setText(getString(R.string.noSecondPuzzleAnswer));
                    myPuzzleLBLAnswer3.setText(getString(R.string.noThirdPuzzleAnswer));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return view;
    }

    private void initViews() {
        myPuzzleLBTNChangeFirst.setOnClickListener(new View.OnClickListener() {
            final SharedPreferences.Editor editor = sharedPref.edit();

            @Override
            public void onClick(View v) {
                editor.putInt(getString(R.string.changePuzzleNumber), 1);
                editor.apply();
                startActivity(new Intent(getContext(), ActivityChangePuzzle.class));
            }
        });
        myPuzzleLBTNChangeSecond.setOnClickListener(new View.OnClickListener() {
            final SharedPreferences.Editor editor = sharedPref.edit();

            @Override
            public void onClick(View v) {
                editor.putInt(getString(R.string.changePuzzleNumber), 2);
                editor.apply();
                startActivity(new Intent(getContext(), ActivityChangePuzzle.class));
            }
        });
        myPuzzleLBTNChangeThird.setOnClickListener(new View.OnClickListener() {
            final SharedPreferences.Editor editor = sharedPref.edit();

            @Override
            public void onClick(View v) {
                editor.putInt(getString(R.string.changePuzzleNumber), 3);
                editor.apply();
                startActivity(new Intent(getContext(), ActivityChangePuzzle.class));
            }
        });
    }

    private void findViews(View view) {
        myPuzzleLBLPuzzle1 = view.findViewById(R.id.myPuzzleLBLPuzzle1);
        myPuzzleLBLPuzzle2 = view.findViewById(R.id.myPuzzleLBLPuzzle2);
        myPuzzleLBLPuzzle3 = view.findViewById(R.id.myPuzzleLBLPuzzle3);
        myPuzzleLBLAnswer1 = view.findViewById(R.id.myPuzzleLBLAnswer1);
        myPuzzleLBLAnswer2 = view.findViewById(R.id.myPuzzleLBLAnswer2);
        myPuzzleLBLAnswer3 = view.findViewById(R.id.myPuzzleLBLAnswer3);
        myPuzzleLBTNChangeFirst = view.findViewById(R.id.myPuzzleLBTNChangeFirst);
        myPuzzleLBTNChangeSecond = view.findViewById(R.id.myPuzzleLBTNChangeSecond);
        myPuzzleLBTNChangeThird = view.findViewById(R.id.myPuzzleLBTNChangeThird);
    }

}