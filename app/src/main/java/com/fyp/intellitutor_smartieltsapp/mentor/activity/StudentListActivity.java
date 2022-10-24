package com.fyp.intellitutor_smartieltsapp.mentor.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.intellitutor_smartieltsapp.R;
import com.fyp.intellitutor_smartieltsapp.Utils.Util;
import com.fyp.intellitutor_smartieltsapp.adapter.StudentListAdapter;
import com.fyp.intellitutor_smartieltsapp.model.StudentModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentListActivity extends AppCompatActivity {

    private Util util;
    private String myId;
    RecyclerView userListRV;
    StudentListAdapter adapter;
    ArrayList<StudentModel> userLists;
    EditText filterEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        util = new Util();
        myId = util.getUID();
        userListRV = findViewById(R.id.userListRV);
        filterEt = findViewById(R.id.filterEt);

        filterEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        loadAllStudent();
    }

    private void loadAllStudent() {
//        init Array list
        userLists = new ArrayList<>();

        // load users
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("mentorStudents").child(FirebaseAuth.getInstance().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Clear list before adding new data in it
                userLists.clear();
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    StudentModel users = ds.getValue(StudentModel.class);
                    // add to list
                    userLists.add(users);
                }
                //setup adapter
                adapter = new StudentListAdapter(getApplicationContext(),userLists);
                //set adapter to recyclerview
                userListRV.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filter(String text)
    {
        ArrayList<StudentModel> filteredList = new ArrayList<>();

        for(StudentModel item: userLists){
            if(item.getName().toLowerCase().contains(text.toLowerCase()) || item.getGmail().toLowerCase().contains(text.toLowerCase()) )
            {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }
}