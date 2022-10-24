package com.fyp.intellitutor_smartieltsapp.mentor.activity;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.intellitutor_smartieltsapp.R;
import com.fyp.intellitutor_smartieltsapp.Utils.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;

public class UpdatePackageRequestActivity extends AppCompatActivity {

    Button freeBuyBtn, proBuyBtn, businessBuyBtn;
    String packageName;
    FirebaseAuth auth;
    Util util;
    String myUid;
    String mentorName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_package_request);
        util = new Util();
        myUid = util.getUID();
        auth = FirebaseAuth.getInstance();

        DatabaseReference count = FirebaseDatabase.getInstance().getReference("User").child(myUid);
        count.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mentorName = snapshot.child("username").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Credentials").child("mentor").child(mentorName);
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("User").child(myUid);
        HashMap<String, Object> updateData = new HashMap<>();
        updateData.put("packageName", packageName);
        reference.updateChildren(updateData);
        reference2.updateChildren(updateData);
        TastyToast.makeText(UpdatePackageRequestActivity.this, "Plan Update Successfully", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
    }

    private void initView() {
        freeBuyBtn = findViewById(R.id.freeBuyBtn);
        proBuyBtn = findViewById(R.id.proBuyBtn);
        businessBuyBtn = findViewById(R.id.businessBuyBtn);
    }
}