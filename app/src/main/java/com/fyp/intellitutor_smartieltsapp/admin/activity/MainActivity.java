package com.fyp.intellitutor_smartieltsapp.admin.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.intellitutor_smartieltsapp.R;
import com.fyp.intellitutor_smartieltsapp.activity.LoginActivity;
import com.fyp.intellitutor_smartieltsapp.admin.activity.add_mentor.RegisterMentorActivity;
import com.fyp.intellitutor_smartieltsapp.admin.activity.material.AddMaterialActivity;
import com.fyp.intellitutor_smartieltsapp.admin.activity.material.ViewExistingMaterialActivity;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    private MaterialCardView viewProfile,mentor,material,chat,logout;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("DAT",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        initView();
        setListeneres();

    }

    private void setListeneres() {
        viewProfile.setOnClickListener(this::ViewProfile);
        mentor.setOnClickListener(this::Mentor);
        material.setOnClickListener(this::Material);
        chat.setOnClickListener(this::openChat);
        logout.setOnClickListener(this::LogOut);
    }

    private void LogOut(View view) {
        editor.clear();
        editor.apply();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.anim_slideup, R.anim.anim_slidebottom);
        finish();
    }

    private void openChat(View view) {

    }

    private void Material(View view) {
        CharSequence options[] = new CharSequence[]
                {
                        "Add Material",
                        "View Existing Material"
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select The Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0)
                {
                    startActivity(new Intent(MainActivity.this, AddMaterialActivity.class));
                    overridePendingTransition(R.anim.anim_slideup, R.anim.anim_slidebottom);
                }
                if(i == 1)
                {
                    startActivity(new Intent(MainActivity.this, ViewExistingMaterialActivity.class));
                    overridePendingTransition(R.anim.anim_slideup, R.anim.anim_slidebottom);
                }
            }
        });
        builder.show();
    }

    private void Mentor(View view) {
        startActivity(new Intent(MainActivity.this, RegisterMentorActivity.class));
        overridePendingTransition(R.anim.anim_slideup, R.anim.anim_slidebottom);
    }

    private void ViewProfile(View view) {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        overridePendingTransition(R.anim.anim_slideup, R.anim.anim_slidebottom);
    }

    private void initView() {
        viewProfile = findViewById(R.id.viewProfile);
        mentor = findViewById(R.id.mentor);
        material = findViewById(R.id.material);
        chat = findViewById(R.id.chat);
        logout = findViewById(R.id.logout);
    }

    @Override
    public void onBackPressed() {
        new android.app.AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing App")
                .setMessage("Are you sure you want to close this App?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}