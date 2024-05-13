package com.example.project_inklink;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class ViewBookDetail extends AppCompatActivity {

    ImageView imageView,ivBack;
    TextView tvName,tvOwner,tvDesc,tvTitle;
    Button btnAddTolib,btnread;
    String username;
    Book book;
    Library library;
    ScrollView scrollView;
    DatabaseReference reference;
    String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_book_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvTitle=findViewById(R.id.toolbartitle);
        tvTitle.setVisibility(View.GONE);
        ivBack=findViewById(R.id.ivtoolbarback);
        scrollView=findViewById(R.id.scrollGenres);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageView=findViewById(R.id.ivBookdetail);
        tvName=findViewById(R.id.tvbookdetailname);
        tvDesc=findViewById(R.id.tvBookdetailDesc);
        tvOwner=findViewById(R.id.tvbookdetailowner);
        btnAddTolib=findViewById(R.id.btnBookdetailAddtolib);
        btnread=findViewById(R.id.btnread);
        Intent intent=getIntent();
        username=intent.getStringExtra("username");
        book= (Book) intent.getSerializableExtra("book");
        assert book != null;
        // Assuming scrollView is your ScrollView
        LinearLayout genreButtonsContainer = findViewById(R.id.genreButtonsContainer);

        for (String genre : book.getGenres()) {
            Button button = new Button(this);
            button.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            button.setText(genre);
            button.setTextSize(16);

            // Add the Button to the LinearLayout inside ScrollView
            genreButtonsContainer.addView(button);
        }

        tvName.setText(book.getName());
        tvDesc.setText(book.getDesc());
        if(book.getOwner()!=null)
            tvOwner.setText(book.getOwner());
        else
            tvOwner.setText("INKLINK");
        book.setKEY(getImageBookId(book.getName()));
        if(book.isPaid())
        {
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_money);

            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        tvName.setCompoundDrawables(drawable, null, null, null);
        }
            Picasso.get()
                    .load(book.getCoverUrl()) // Assuming coverUrl is the Firebase Storage URL
                    .placeholder(R.drawable.ic_noimage) // Placeholder image while loading
                    .into(imageView);
        btnread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference libraryRef = FirebaseDatabase.getInstance().getReference().child("library").child(username);

                libraryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            library = dataSnapshot.getValue(Library.class);
                            if (library != null) {
                                ArrayList<String> readsList = library.getReads();
                                if (readsList != null && readsList.contains(book.getKEY())) {
                                    // Book is already in the user's reads
                                    // Proceed with your logic for opening or displaying the book
                                    openOrDisplayBook();
                                } else {
                                    // Book is not in the user's reads
                                    // Proceed with your logic for checking if the book is paid
                                    if (book.isPaid()) {
                                        //PAYMENT MAKINGGGGGG
                                        addToLibrary();
                                     //   Toast.makeText(ViewBookDetail.this, "book is paid", Toast.LENGTH_SHORT).show();
                                    }
                                    openOrDisplayBook();
                                       // Toast.makeText(ViewBookDetail.this, "book is not paid", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            // The reads node for the user doesn't exist
                            // Proceed with your logic for checking if the book is paid
                            if (book.isPaid()) {
                                Toast.makeText(ViewBookDetail.this, "book is paid", Toast.LENGTH_SHORT).show();
                            }
                                openOrDisplayBook();

//                                Toast.makeText(ViewBookDetail.this, "book is not paid", Toast.LENGTH_SHORT).show()
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ViewBookDetail.this, "Failed to check reads", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnAddTolib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(book.isPaid())
                {
                    //PAYMENT MODULE AND THEN ADD TO LIBRARY
                }
                addToLibrary();
            }
        });


    }
    private void addToLibrary()
    {
        DatabaseReference libraryRef = FirebaseDatabase.getInstance().getReference().child("library").child(username);
        libraryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    library = dataSnapshot.getValue(Library.class);
                    if (library != null) {
                        ArrayList<String> readsList = library.getReads();
                        if (readsList == null) {
                            readsList = new ArrayList<>();
                        }
                        if (!readsList.contains(book.getKEY())) {
                            readsList.add(book.getKEY());
                            library.setReads(readsList);
                            libraryRef.setValue(library);
                            Toast.makeText(getApplicationContext(), "Book added to library", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Book already in library", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // The reads node for the user doesn't exist
                    // Create a new Library object and add the book to the reads list
                    library = new Library();
                    library.setUsername(username);
                    ArrayList<String> readsList = new ArrayList<>();
                    readsList.add(book.getKEY());
                    library.setReads(readsList);
                    libraryRef.setValue(library);
                    Toast.makeText(getApplicationContext(), "Book added to library", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to add book to library", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getImageBookId(String imageBookName) {
        if (book instanceof PDFBook) {
            PDFBook pdfBook = (PDFBook) book;
            reference= FirebaseDatabase.getInstance().getReference().child("pdfbooks");
            Query query = reference.orderByChild("name").equalTo(pdfBook.getName());
            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                        PDFBook pdfbook = bookSnapshot.getValue(PDFBook.class);
                        if (pdfbook != null) {
                            String bookId = bookSnapshot.getKey();
                            book.setKEY(bookId);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ViewBookDetail.this, "Cannot fetch content", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (book instanceof ImageBook) {
            ImageBook imageBook = (ImageBook) book;
            reference = FirebaseDatabase.getInstance().getReference().child("imagebooks");
            Query query = reference.orderByChild("name").equalTo(imageBook.getName());
            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                        ImageBook imagebook = bookSnapshot.getValue(ImageBook.class);
                        if (imagebook != null) {
                            String bookId = bookSnapshot.getKey();
                            book.setKEY(bookId);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ViewBookDetail.this, "Cannot fetch content", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return "";
    }
    private void openOrDisplayBook() {
        if (book instanceof PDFBook) {
            // Proceed with opening the PDF book
            Intent intent = new Intent(ViewBookDetail.this, DisplayPDF.class);
            intent.putExtra("url", ((PDFBook) book).getUrl());
            intent.putExtra("bookname", book.getName());
            startActivity(intent);
        } else if (book instanceof ImageBook) {
            // Proceed with displaying the Image book
            Intent intent = new Intent(ViewBookDetail.this, DisplayImageBook.class);
            intent.putExtra("Uris", ((ImageBook) book).getUrls());
            intent.putExtra("bookname", book.getName());
            startActivity(intent);
        }
    }
}