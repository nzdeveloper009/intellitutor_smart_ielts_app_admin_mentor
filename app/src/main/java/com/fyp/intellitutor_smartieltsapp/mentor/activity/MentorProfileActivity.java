package com.fyp.intellitutor_smartieltsapp.mentor.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fyp.intellitutor_smartieltsapp.R;
import com.fyp.intellitutor_smartieltsapp.model.MentorModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;

public class MentorProfileActivity extends AppCompatActivity {

    ShapeableImageView profileImage;
    ImageView editableAddress, editableQualification, editableGmail;
    TextView idNoTv;
    TextView personalInfo, nameTv, noOfRegisterStudentsTv;
    TextView studentInfo, editQualificationTv;
    LinearLayout editAddressLL, editQualification, editGmail, personalInfoSlide, credentialInfoSlide;
    TextView emailTv, viewAllStudents, registerStudent, updatePackageStudent;
    EditText qualificationTv;
    private Dialog dialog;

    String gmailStr, nameStr, qualificationStr, profileUriStr, usernameStr,noOfStudentRegisterStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_mentor_profile);
        initView();
        initialFunctionality();
        setListeneres();
    }

    private void initialFunctionality() {
        dialog = new Dialog(MentorProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_bar);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        showUserProfile();
    }

    private void showUserProfile() {
        // Extracting User Reference from Database for "Credentials"
        DatabaseReference mGetUsernameReference = FirebaseDatabase.getInstance().getReference();
        mGetUsernameReference.child("User").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        usernameStr = snapshot.child("username").getValue(String.class);

                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Credentials");
                        referenceProfile.child("mentor")
                                .child(usernameStr)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        MentorModel readMentorDetails = snapshot.getValue(MentorModel.class);
                                        if (readMentorDetails != null) {
                                            gmailStr = readMentorDetails.getGmail();
                                            nameStr = readMentorDetails.getName();
                                            qualificationStr = readMentorDetails.getQualification();
                                            profileUriStr = readMentorDetails.getNicCardImage();
                                            noOfStudentRegisterStr = readMentorDetails.getCountStudents();

                                            try {
                                                Glide.with(getApplicationContext()).load(readMentorDetails.getNicCardImage()).placeholder(R.drawable.logo).into(profileImage);
                                            } catch (Exception e) {
                                                Toast.makeText(getApplicationContext(), "Image Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }

                                            emailTv.setText(gmailStr);
                                            nameTv.setText(nameStr);
                                            qualificationTv.setText(qualificationStr);
                                            noOfRegisterStudentsTv.setText(noOfStudentRegisterStr);
                                            dialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        dialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void setListeneres() {
        studentInfo.setOnClickListener(this::StudentInfo);
        personalInfo.setOnClickListener(this::ShowPersonalInfo);
        editQualification.setOnClickListener(edit -> {
            if(String.valueOf(editQualificationTv.getText()).equals(getResources().getString(R.string.edit))){
                qualificationTv.setEnabled(true);
                editableQualification.setImageDrawable(getResources().getDrawable(R.drawable.ic_done));
                editQualificationTv.setText(getResources().getString(R.string.done));
            } else {
                String updatedQualification = String.valueOf(qualificationTv.getText());
                if(updatedQualification.equals(qualificationStr)){
                    Toast.makeText(MentorProfileActivity.this, "Nothing Changed", Toast.LENGTH_SHORT).show();
                } else {
                    UpdateContactNo(updatedQualification);
                }
                qualificationTv.setEnabled(false);
                editableQualification.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit));
                editQualificationTv.setText(getResources().getString(R.string.edit));
            }
        });
        registerStudent.setOnClickListener(add -> {
            startActivity(new Intent(getApplicationContext(),RegisterStudentActivity.class));
        });
        viewAllStudents.setOnClickListener( view ->{
            startActivity(new Intent(getApplicationContext(),StudentListActivity.class));
        });
        updatePackageStudent.setOnClickListener(update -> {
            startActivity(new Intent(getApplicationContext(),UpdatePackageRequestActivity.class));
        });
    }

    private void UpdateContactNo(String ph) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("qualification",ph);

        DatabaseReference update = FirebaseDatabase.getInstance().getReference("Credentials").child("mentor").child(usernameStr);
        update.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                TastyToast.makeText(MentorProfileActivity.this,"Qualification Update Successfully",TastyToast.LENGTH_SHORT,TastyToast.SUCCESS).show();
            }
        });
    }

    private void ShowPersonalInfo(View view) {
        studentInfo.setTextColor(getResources().getColor(R.color.grayColor));
        personalInfo.setTextColor(Color.BLACK);
        Animation pushDownInAnim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.anim_pushdownin);
        Animation pushDownOutAnim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.anim_pushdownout);
        credentialInfoSlide.setAnimation(pushDownOutAnim);
        credentialInfoSlide.setVisibility(View.GONE);
        personalInfoSlide.setVisibility(View.VISIBLE);
        personalInfoSlide.setAnimation(pushDownInAnim);
    }

    private void StudentInfo(View view) {
        studentInfo.setTextColor(Color.BLACK);
        personalInfo.setTextColor(getResources().getColor(R.color.grayColor));
        Animation pushDownInAnim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.anim_pushdownin);
        Animation pushDownOutAnim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.anim_pushdownout);
        personalInfoSlide.setAnimation(pushDownOutAnim);
        personalInfoSlide.setVisibility(View.GONE);
        credentialInfoSlide.setVisibility(View.VISIBLE);
        credentialInfoSlide.setAnimation(pushDownInAnim);

    }

    private void initView() {
        profileImage = findViewById(R.id.profileImage);
        nameTv = findViewById(R.id.nameTv);
        idNoTv = findViewById(R.id.idNoTv);
        personalInfo = findViewById(R.id.personalInfo);
        studentInfo = findViewById(R.id.studentInfo);

        editAddressLL = findViewById(R.id.editAddressLL);
        editableAddress = findViewById(R.id.editableAddress);

        editQualification = findViewById(R.id.editQualification);
        editableQualification = findViewById(R.id.editableQualification);
        qualificationTv = findViewById(R.id.qualificationTv);
        editQualificationTv = findViewById(R.id.editQualificationTv);

        editGmail = findViewById(R.id.editGmail);
        editableGmail = findViewById(R.id.editableGmail);
        emailTv = findViewById(R.id.emailTv);

        noOfRegisterStudentsTv = findViewById(R.id.noOfRegisterStudentsTv);
        personalInfoSlide = findViewById(R.id.personalInfoSlide);
        credentialInfoSlide = findViewById(R.id.credentialInfoSlide);

        viewAllStudents = findViewById(R.id.viewAllStudents);
        registerStudent = findViewById(R.id.registerStudent);
        updatePackageStudent = findViewById(R.id.updatePackageStudent);
    }
}