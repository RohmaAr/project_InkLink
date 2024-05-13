package com.example.project_inklink;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Tabs extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tabs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent=getIntent();
        user= (User) intent.getSerializableExtra("user");
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        tabLayout=findViewById(R.id.tabLayoutHome);
        viewPager=findViewById(R.id.viewpagerHome);
        MainPagerAdapter mainPagerAdapter= new MainPagerAdapter(this);
        mainPagerAdapter.setBundle(bundle);
        viewPager.setAdapter(mainPagerAdapter);
        TabLayoutMediator tabLayoutMediator=new TabLayoutMediator(
                tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {

            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int i) {
                switch (i) {
                    case 0: {
                          tab.setIcon(R.drawable.ic_home);
                        break;
                    }
                    case 1: {
                        tab.setIcon(R.drawable.ic_search);

                        break;
                    }
                    case 2:
                    {
                        tab.setIcon(R.drawable.ic_listicon);
                        break;
                    }
                    default:{
                        tab.setIcon(R.drawable.ic_user);
                    }
                }
            }
        });
        tabLayoutMediator.attach();
    }
}