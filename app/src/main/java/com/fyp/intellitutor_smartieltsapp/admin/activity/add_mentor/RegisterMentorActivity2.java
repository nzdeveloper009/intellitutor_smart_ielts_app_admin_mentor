package com.fyp.intellitutor_smartieltsapp.admin.activity.add_mentor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.intellitutor_smartieltsapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;

public class RegisterMentorActivity2 extends AppCompatActivity {

    Button freeBuyBtn, proBuyBtn, businessBuyBtn;
    String uid, username;
    String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mentor2);

        uid = getIntent().getStringExtra("uid");
        username = getIntent().getStringExtra("username");

        initView();

        freeBuyBtn.setOnClickListener(plan -> {
            packageName = "basic";
            setPlan(packageName);
        });
        proBuyBtn.setOnClickListener(plan -> {
            packageName = "pro";
            setPlan(packageName);
        });
        businessBuyBtn.setOnClickListener(plan -> {
            packageName = "business";
            setPlan(packageName);
        });
    }

    private void setPlan(String packageName) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Credentials").child("mentor").child(username);
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("User").child(uid);
        HashMap<String, Object> updateData = new HashMap<>();
        updateData.put("packageName", packageName);
        reference.updateChildren(updateData);
        reference2.updateChildren(updateData);
        TastyToast.makeText(RegisterMentorActivity2.this, "plan set Successfully", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterMentorActivity2.this, RegisterMentorActivity.class);
        startActivity(intent);
    }

    private void initView() {
        freeBuyBtn = findViewById(R.id.freeBuyBtn);
        proBuyBtn = findViewById(R.id.proBuyBtn);
        businessBuyBtn = findViewById(R.id.businessBuyBtn);
    }
}