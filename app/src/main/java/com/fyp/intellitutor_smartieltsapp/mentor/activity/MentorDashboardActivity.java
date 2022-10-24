package com.fyp.intellitutor_smartieltsapp.mentor.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.intellitutor_smartieltsapp.R;
import com.fyp.intellitutor_smartieltsapp.activity.LoginActivity;
import com.fyp.intellitutor_smartieltsapp.admin.activity.material.AddMaterialActivity;
import com.fyp.intellitutor_smartieltsapp.admin.activity.material.ViewExistingMaterialActivity;
import com.fyp.intellitutor_smartieltsapp.mentor.chat.MainChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

public class MentorDashboardActivity extends AppCompatActivity {

    private MaterialCardView viewProfile, mentor, material, chat, logout;
    String username;
    TextView changePasswordTv;

    private FirebaseAuth authProfile;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_mentor_dashboard);
        username = getIntent().getStringExtra("username");
        authProfile = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateUI();
            }
        };
        initView();
        setListeneres();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            authProfile.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        authProfile.addAuthStateListener(mAuthStateListener);
    }

    private void updateUI() {
        FirebaseUser user = authProfile.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slideup, R.anim.anim_slidebottom);
            finish();
        }
    }


    private void setListeneres() {
        viewProfile.setOnClickListener(this::ViewProfile);
        mentor.setOnClickListener(this::Student);
        material.setOnClickListener(this::Material);
        chat.setOnClickListener(this::openChat);
        logout.setOnClickListener(this::LogOut);
        changePasswordTv.setOnClickListener(this::ChangePassword);
    }

    private void ChangePassword(View view) {
        DatabaseReference mGetUsernameReference = FirebaseDatabase.getInstance().getReference();
        mGetUsernameReference.child("Credentials").child("mentor").child(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String gmail = snapshot.child("gmail").getValue(String.class);
                        authProfile.sendPasswordResetEmail(gmail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            TastyToast.makeText(MentorDashboardActivity.this,"Check Email for Reset Password!!!",TastyToast.LENGTH_LONG,TastyToast.INFO).show();
                                        }
                                    }
                                });;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void LogOut(View view) {
        authProfile.signOut();
        updateUI();
    }

    private void openChat(View view) {
        CharSequence options[] = new CharSequence[]
                {
                        "With Admin",
                        "With Student"
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(MentorDashboardActivity.this);
        builder.setTitle("Select The Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    Intent intent = new Intent(MentorDashboardActivity.this, MainChatActivity.class);
                    intent.putExtra("with","admin");
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slideup, R.anim.anim_slidebottom);
                }
                if (i == 1) {
                    Intent intent = new Intent(MentorDashboardActivity.this, MainChatActivity.class);
                    intent.putExtra("with","student");
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slideup, R.anim.anim_slidebottom);
                }
            }
        });
        builder.show();
    }

    private void Material(View view) {
        CharSequence options[] = new CharSequence[]
                {
                        "Add Material",
                        "View Existing Material"
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(MentorDashboardActivity.this);
        builder.setTitle("Select The Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    Intent intent = new Intent(MentorDashboardActivity.this, AddMaterialActivity.class);
                    intent.putExtra("type", "mentor");
                    intent.putExtra("username", username);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slideup, R.anim.anim_slidebottom);
                }
                if (i == 1) {
                    startActivity(new Intent(MentorDashboardActivity.this, ViewExistingMaterialActivity.class));
                    overridePendingTransition(R.anim.anim_slideup, R.anim.anim_slidebottom);
                }
            }
        });
        builder.show();
    }

    private void Student(View view) {

        CharSequence options[] = new CharSequence[]
                {
                        "View All Students",
                        "Add Student"
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(MentorDashboardActivity.this);
        builder.setTitle("Select The Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    Intent intent = new Intent(MentorDashboardActivity.this, StudentListActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slideup, R.anim.anim_slidebottom);
                }
                if (i == 1) {
                    startActivity(new Intent(MentorDashboardActivity.this, RegisterStudentActivity.class));
                    overridePendingTransition(R.anim.anim_slideup, R.anim.anim_slidebottom);
                }
            }
        });
        builder.show();
    }

    private void ViewProfile(View view) {
        startActivity(new Intent(MentorDashboardActivity.this, MentorProfileActivity.class));
        overridePendingTransition(R.anim.anim_slideup, R.anim.anim_slidebottom);
    }

    private void initView() {
        viewProfile = findViewById(R.id.viewProfile);
        mentor = findViewById(R.id.mentor);
        material = findViewById(R.id.material);
        chat = findViewById(R.id.chat);
        logout = findViewById(R.id.logout);
        changePasswordTv = findViewById(R.id.changePasswordTv);
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