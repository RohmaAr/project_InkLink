package com.example.project_inklink;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GenreSearchActivity extends AppCompatActivity {


    String userName;
    RecyclerView rvGenreSearch;
    AllAdapter adapter;
    ArrayList<Book> books;
    String genre;
    ImageView ivback;

    TextView tvNoResults,tvTitle;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_genre_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent=getIntent();

        userName=intent.getStringExtra("username");
        genre=intent.getStringExtra("genre");
        rvGenreSearch=findViewById(R.id.rvGenreSearch);
        rvGenreSearch.setLayoutManager(new LinearLayoutManager(this));
        rvGenreSearch.setHasFixedSize(true);
        tvTitle=findViewById(R.id.toolbartitle);
        tvTitle.setText(genre);
        books = new ArrayList<>();
        ivback=findViewById(R.id.ivtoolbarback);
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Fetch PDFBooks from the pdfbooks collection
        reference.child("pdfbooks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PDFBook pdfBook = snapshot.getValue(PDFBook.class);
                    if(pdfBook != null && pdfBook.getGenres().contains(genre))
                    {
                        System.out.println(" "+pdfBook.getName());
                        books.add(pdfBook);
                    }
                }
                // Fetch ImageBooks from the imagebooks collection
                reference.child("imagebooks").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ImageBook imageBook = snapshot.getValue(ImageBook.class);
                            if (imageBook != null && imageBook.getGenres().contains(genre)) {
                                books.add(imageBook);
                            }
                        }
                        // Initialize and set the adapter with the populated books ArrayList
                        adapter = new AllAdapter(books, GenreSearchActivity.this,userName);
                        rvGenreSearch.setAdapter(adapter);
                        if(books.size()==0)
                        {
                            tvNoResults.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });

    }

}