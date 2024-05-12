package com.example.project_inklink;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DisplayImageBook extends AppCompatActivity {

   RecyclerView rvDisplayImageBook;
    ImageView ivback;
    TextView tvFragName;
    ArrayList<String> uris;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_image_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent=getIntent();
        rvDisplayImageBook=findViewById(R.id.rvDisplayImages);
        tvFragName=findViewById(R.id.toolbartitle);
        tvFragName.setText(intent.getStringExtra("bookname"));
        ivback=findViewById(R.id.ivtoolbarback);
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
         uris=intent.getStringArrayListExtra("Uris");
        if(uris!=null)
        {

            rvDisplayImageBook.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvDisplayImageBook.setAdapter(new displayAllRecycler());

        }
        else {
            Toast.makeText(this, "Cannot find images", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private class displayAllRecycler extends RecyclerView.Adapter<displayAllRecycler.ViewHold>
    {


        public displayAllRecycler() {
            super();
        }

        @NonNull
        @Override
        public ViewHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater=LayoutInflater.from(parent.getContext());
            View view=inflater.inflate(R.layout.singleimage,parent,false);
            return new ViewHold(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHold holder, int position) {
            Picasso.get()
                    .load(uris.get(position)) // Assuming coverUrl is the Firebase Storage URL
                    .placeholder(R.drawable.ic_bookcover) // Placeholder image while loading
                    .into( holder.imageView);
        }

        @Override
        public int getItemCount() {
            return uris.size();
        }

        public class ViewHold extends RecyclerView.ViewHolder{

            public ImageView imageView;

            public ViewHold(@NonNull View itemView) {
                super(itemView);
                imageView=itemView.findViewById(R.id.ivItemPickedImage);
            }
        }
    }
}