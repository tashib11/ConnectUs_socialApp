package com.example.connectus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.connectus.databinding.ActivityAboutBinding;
import com.example.connectus.databinding.ActivityMainBinding;

public class aboutActivity extends AppCompatActivity {
ActivityAboutBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ((Window) window).setStatusBarColor(this.getResources().getColor(R.color.black));

//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
        binding= ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
binding.backArrow.setOnClickListener(view -> {
    Intent intent=new Intent(aboutActivity.this,SettingsActivity.class);
    startActivity(intent);
});
    }
}