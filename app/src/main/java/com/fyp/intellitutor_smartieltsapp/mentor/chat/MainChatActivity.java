package com.fyp.intellitutor_smartieltsapp.mentor.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.fyp.intellitutor_smartieltsapp.R;
import com.fyp.intellitutor_smartieltsapp.adapter.TabAccessorAdapter;
import com.fyp.intellitutor_smartieltsapp.fragments.ChatFragment;
import com.fyp.intellitutor_smartieltsapp.fragments.GroupsFragment;
import com.google.android.material.tabs.TabLayout;

public class MainChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabAccessorAdapter myTabAccessorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        Intent intent = getIntent();
        String with = intent.getStringExtra("with");

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Intellitutor");

        myViewPager = findViewById(R.id.main_tabs_pager);
        myTabAccessorAdapter = new TabAccessorAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        myTabAccessorAdapter.addFragment(new ChatFragment(),"Chats");
        myTabAccessorAdapter.addFragment(new GroupsFragment(),"Groups");
        /*if(with.equals("student")){
            myTabAccessorAdapter.addFragment(new GroupsFragment(),"Groups");
        } */
        myViewPager.setAdapter(myTabAccessorAdapter);

        myTabLayout = findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
    }
    // https://www.youtube.com/watch?v=yjBCXa01dZc&list=PLxefhmF0pcPmtdoud8f64EpgapkclCllj&index=7

}