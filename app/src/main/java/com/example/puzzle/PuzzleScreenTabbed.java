package com.example.puzzle;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class PuzzleScreenTabbed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzles_screen_tabbed);
        regularUserfacebookUsersPagerAdapter sectionsPagerAdapter = new regularUserfacebookUsersPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.puzzlesScreenTabbedViewPager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.puzzlesScreenTabbedTabLayout);
        tabs.setupWithViewPager(viewPager);

    }
}
