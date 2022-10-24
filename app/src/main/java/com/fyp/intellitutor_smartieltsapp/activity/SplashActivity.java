package com.fyp.intellitutor_smartieltsapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.intellitutor_smartieltsapp.R;
import com.fyp.intellitutor_smartieltsapp.customview.NeoSansProTextView;
import com.fyp.intellitutor_smartieltsapp.mentor.activity.MentorDashboardActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private static final long Splash_Time = 3500;
    private ShapeableImageView logo, logo2;
    private NeoSansProTextView slogan;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Take instance of Action Bar
        // using getSupportActionBar and
        // if it is not Null
        // then call hide function
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_splash);
        auth = FirebaseAuth.getInstance();
        initView();
        initFunctionality();
    }

    private void initFunctionality() {
        setAnimationOnView();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(auth.getCurrentUser() != null)
                {
                    if(auth.getCurrentUser().isEmailVerified()){
                        auth = FirebaseAuth.getInstance();
                        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getUid());
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String role = dataSnapshot.child("role").getValue(String.class);
                                String username = dataSnapshot.child("username").getValue(String.class);
                                if(role.equals("mentor")){
                                    Intent intent = new Intent(SplashActivity.this, MentorDashboardActivity.class);
                                    intent.putExtra("username",username);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_slideup, R.anim.anim_slidebottom);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(SplashActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    nextActivity(); //memory leak
                }


            }
        }, Splash_Time);

    }

    private void nextActivity() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.anim_slideup, R.anim.anim_slidebottom);
        finish();
    }

    private void initView() {
        logo = findViewById(R.id.logo);
        logo2 = findViewById(R.id.logo2);
        slogan = findViewById(R.id.slogan);

    }

    private void setAnimationOnView() {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(2500);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.anim_slideup);
        logo.setAnimation(anim);
        logo2.setAnimation(anim);
        slogan.setAnimation(slideUpAnimation);
    }
}