package com.example.project_inklink;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splashscreen extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    Animation imageAnim,textAnim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splashscreen);
        imageView=findViewById(R.id.inkpot);
        textView=findViewById(R.id.title);
        imageAnim= AnimationUtils.loadAnimation(this,R.anim.logo);
        textAnim=AnimationUtils.loadAnimation(this,R.anim.title);
        imageView.setAnimation(imageAnim);
        textView.setAnimation(textAnim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(Splashscreen.this,AuthenticationActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }
}