package com.fyp.intellitutor_smartieltsapp.admin.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import com.fyp.intellitutor_smartieltsapp.model.AdminModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    ShapeableImageView profileImage;
    ImageView toggleEye, editableAddress, editableNumber, editableGmail;
    TextView idNoTv;
    TextView personalInfo, nameTv, usernameTv;
    TextView textPassword, credential, editNumberTv;
    LinearLayout editAddressLL, editNumber, editGmail, personalInfoSlide, credentialInfoSlide;
    TextView addressTv, subAddressTv, emailTv;
    EditText phoneNoTv;
    private Dialog dialog;

    String addressStr, gmailStr, nameStr, passwordStr, phoneNoStr, profileUriStr, usernameStr;
    String pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_profile);
        pin = getResources().getString(R.string.pin);
        initView();
        initialFunctionality();
        setListeneres();
    }

    private void initialFunctionality() {
        dialog = new Dialog(ProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_bar);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        showUserProfile();
    }

    private void showUserProfile() {
        // Extracting User Reference from Database for "Credentials"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Credentials");
        referenceProfile.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AdminModel readAdminDetails = snapshot.getValue(AdminModel.class);
                if (readAdminDetails != null) {
                    addressStr = readAdminDetails.getAddress();
                    gmailStr = readAdminDetails.getGmail();
                    nameStr = readAdminDetails.getName();
                    passwordStr = readAdminDetails.getPassword();
                    phoneNoStr = readAdminDetails.getPhoneNo();
                    profileUriStr = readAdminDetails.getProfileUri();
                    usernameStr = readAdminDetails.getUsername();

                    try {
                        Glide.with(getApplicationContext()).load(readAdminDetails.getProfileUri()).placeholder(R.drawable.logo).into(profileImage);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Image Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    addressTv.setText(addressStr);
                    subAddressTv.setText(addressStr);
                    emailTv.setText(gmailStr);
                    nameTv.setText(nameStr);
                    textPassword.setText(passwordStr);
                    phoneNoTv.setText(phoneNoStr);
                    idNoTv.setText(phoneNoStr);
                    usernameTv.setText(usernameStr);
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

    private void setListeneres() {
        toggleEye.setOnClickListener(this::ToggleEye);
        credential.setOnClickListener(this::ShowCredentials);
        personalInfo.setOnClickListener(this::ShowPersonalInfo);
        editNumber.setOnClickListener(edit -> {
            if(String.valueOf(editNumberTv.getText()).equals(getResources().getString(R.string.editNumber))){
                phoneNoTv.setEnabled(true);
                editableNumber.setImageDrawable(getResources().getDrawable(R.drawable.ic_done));
                editNumberTv.setText(getResources().getString(R.string.done));
            } else {
                if(String.valueOf(phoneNoTv.getText()).equals(phoneNoStr)){
                    Toast.makeText(ProfileActivity.this, "Contact Number Not Changed", Toast.LENGTH_SHORT).show();
                } else {
                    UpdateContactNo(String.valueOf(phoneNoTv.getText()));
                }
                phoneNoTv.setEnabled(false);
                editableNumber.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit));
                editNumberTv.setText(getResources().getString(R.string.editNumber));
            }
        });

    }

    private void UpdateContactNo(String ph) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("phoneNo",ph);

        DatabaseReference update = FirebaseDatabase.getInstance().getReference("Credentials").child("admin");
        update.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                TastyToast.makeText(ProfileActivity.this,"Phone Number Update Successfully",TastyToast.LENGTH_SHORT,TastyToast.SUCCESS).show();
            }
        });
    }

    private void ShowPersonalInfo(View view) {
        credential.setTextColor(getResources().getColor(R.color.grayColor));
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

    private void ShowCredentials(View view) {
        credential.setTextColor(Color.BLACK);
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

    private void ToggleEye(View view) {

        if (textPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
            showAlertDialog();
        } else {
            toggleEye.setImageResource(R.drawable.hiden_eye);
            //Hide Password
            textPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    private void showAlertDialog() {
        final Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.setContentView(R.layout.text_inpu_password);
        TextView okBtn = dialog.findViewById(R.id.okTv);
        TextView cancelBtn = dialog.findViewById(R.id.cancelTv);
        TextInputEditText pwdEt = dialog.findViewById(R.id.passwordET);
        okBtn.setOnClickListener(view -> {
            if (pwdEt.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter Pin!!", Toast.LENGTH_SHORT).show();
            } else {
                String chkPwd = pwdEt.getText().toString();
                if (pin.equals(chkPwd)) {
                    toggleEye.setImageResource(R.drawable.ic_show_eye);
                    //hide Password
                    textPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect Pin!!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();


    }

    private void initView() {
        profileImage = findViewById(R.id.profileImage);
        nameTv = findViewById(R.id.nameTv);
        idNoTv = findViewById(R.id.idNoTv);
        personalInfo = findViewById(R.id.personalInfo);
        credential = findViewById(R.id.credential);

        editAddressLL = findViewById(R.id.editAddressLL);
        editableAddress = findViewById(R.id.editableAddress);
        addressTv = findViewById(R.id.addressTv);
        subAddressTv = findViewById(R.id.subAddressTv);

        editNumber = findViewById(R.id.editNumber);
        editableNumber = findViewById(R.id.editableNumber);
        phoneNoTv = findViewById(R.id.phoneNoTv);
        editNumberTv = findViewById(R.id.editNumberTv);

        editGmail = findViewById(R.id.editGmail);
        editableGmail = findViewById(R.id.editableGmail);
        emailTv = findViewById(R.id.emailTv);

        toggleEye = findViewById(R.id.toggleEye);

        textPassword = findViewById(R.id.textPassword);
        usernameTv = findViewById(R.id.usernameTv);
        personalInfoSlide = findViewById(R.id.personalInfoSlide);
        credentialInfoSlide = findViewById(R.id.credentialInfoSlide);
    }
}