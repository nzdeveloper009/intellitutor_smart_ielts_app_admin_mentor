package com.fyp.intellitutor_smartieltsapp.admin.activity.material;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.fyp.intellitutor_smartieltsapp.R;
import com.fyp.intellitutor_smartieltsapp.Utils.Util;
import com.fyp.intellitutor_smartieltsapp.adapter.ViewMaterialAdapter;
import com.fyp.intellitutor_smartieltsapp.model.MaterialModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

public class ViewExistingMaterialActivity extends AppCompatActivity {

    private TextView listeningMaterialListTv,readingMaterialListTv,writingMaterialListTv,speakingMaterialListTv;

    private Util util;
    private String myId;
    RecyclerView materialListRv;
    ViewMaterialAdapter adapter;
    ArrayList<MaterialModel> userLists;
    EditText filterEt;
    String cat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_existing_material);
        runtimePermission();
        cat = "Listening Material";
        initView();
        initFunctionality();
        SetListeners();
        materialListRv = findViewById(R.id.materialListRv);

        loadAllUsers(cat);
    }

    private void runtimePermission(){
        Dexter.withContext(ViewExistingMaterialActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void loadAllUsers(String catStr) {
//        init Array list
        userLists = new ArrayList<>();

        // load users
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Material").child(catStr);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Clear list before adding new data in it
                userLists.clear();
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    MaterialModel users = ds.getValue(MaterialModel.class);
                    // add to list
                    userLists.add(users);
                }
                //setup adapter
                adapter = new ViewMaterialAdapter(getApplicationContext(),userLists);
                //set adapter to recyclerview
                materialListRv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void SetListeners() {
        listeningMaterialListTv.setOnClickListener(view -> {
            loadAllUsers(String.valueOf(listeningMaterialListTv.getText()));

            listeningMaterialListTv.setBackgroundColor(getResources().getColor(R.color.white));
            readingMaterialListTv.setBackgroundColor(getResources().getColor(R.color.disable_card));
            writingMaterialListTv.setBackgroundColor(getResources().getColor(R.color.disable_card));
            speakingMaterialListTv.setBackgroundColor(getResources().getColor(R.color.disable_card));

            listeningMaterialListTv.setTextColor(getResources().getColor(R.color.black));
            readingMaterialListTv.setTextColor(getResources().getColor(R.color.grayColor));
            writingMaterialListTv.setTextColor(getResources().getColor(R.color.grayColor));
            speakingMaterialListTv.setTextColor(getResources().getColor(R.color.grayColor));
        });

        readingMaterialListTv.setOnClickListener(view -> {
            loadAllUsers(String.valueOf(readingMaterialListTv.getText()));

            readingMaterialListTv.setBackgroundColor(getResources().getColor(R.color.white));
            listeningMaterialListTv.setBackgroundColor(getResources().getColor(R.color.disable_card));
            writingMaterialListTv.setBackgroundColor(getResources().getColor(R.color.disable_card));
            speakingMaterialListTv.setBackgroundColor(getResources().getColor(R.color.disable_card));

            readingMaterialListTv.setTextColor(getResources().getColor(R.color.black));
            listeningMaterialListTv.setTextColor(getResources().getColor(R.color.grayColor));
            writingMaterialListTv.setTextColor(getResources().getColor(R.color.grayColor));
            speakingMaterialListTv.setTextColor(getResources().getColor(R.color.grayColor));
        });

        writingMaterialListTv.setOnClickListener(view -> {
            loadAllUsers(String.valueOf(writingMaterialListTv.getText()));

            writingMaterialListTv.setBackgroundColor(getResources().getColor(R.color.white));
            readingMaterialListTv.setBackgroundColor(getResources().getColor(R.color.disable_card));
            listeningMaterialListTv.setBackgroundColor(getResources().getColor(R.color.disable_card));
            speakingMaterialListTv.setBackgroundColor(getResources().getColor(R.color.disable_card));

            writingMaterialListTv.setTextColor(getResources().getColor(R.color.black));
            readingMaterialListTv.setTextColor(getResources().getColor(R.color.grayColor));
            listeningMaterialListTv.setTextColor(getResources().getColor(R.color.grayColor));
            speakingMaterialListTv.setTextColor(getResources().getColor(R.color.grayColor));
        });
        speakingMaterialListTv.setOnClickListener(view -> {
            loadAllUsers(String.valueOf(speakingMaterialListTv.getText()));

            speakingMaterialListTv.setBackgroundColor(getResources().getColor(R.color.white));
            writingMaterialListTv.setBackgroundColor(getResources().getColor(R.color.disable_card));
            readingMaterialListTv.setBackgroundColor(getResources().getColor(R.color.disable_card));
            listeningMaterialListTv.setBackgroundColor(getResources().getColor(R.color.disable_card));

            speakingMaterialListTv.setTextColor(getResources().getColor(R.color.black));
            writingMaterialListTv.setTextColor(getResources().getColor(R.color.grayColor));
            readingMaterialListTv.setTextColor(getResources().getColor(R.color.grayColor));
            listeningMaterialListTv.setTextColor(getResources().getColor(R.color.grayColor));
        });

    }

    private void initFunctionality() {
    }

    private void initView() {
        listeningMaterialListTv = findViewById(R.id.listeningMaterialListTv);
        readingMaterialListTv = findViewById(R.id.readingMaterialListTv);
        writingMaterialListTv = findViewById(R.id.writingMaterialListTv);
        speakingMaterialListTv = findViewById(R.id.speakingMaterialListTv);
    }
}