package com.example.blogapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.blogapplication.databinding.ActivityHomePageBinding;

public class HomePageActivity extends AppCompatActivity {

    private ActivityHomePageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}