package com.example.project_inklink;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PDFBookRecyclerAdapter extends FirebaseRecyclerAdapter<PDFBook, RecyclerView.ViewHolder> {

    Context context;
    Fragment fragment;

    public PDFBookRecyclerAdapter(Context context, @NonNull FirebaseRecyclerOptions<PDFBook> options) {
        super(options);
        this.context = context;
        System.out.println("CALLING CONSTRUCTOR FOR "+context.toString());
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
        System.out.println("Fragment set of "+fragment.toString());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        System.out.println("IN ON CREATEVIEW HOLDER");
        if(fragment instanceof HomeFrag) {
            System.out.println("HOME FRAMENT PDFFFF");
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.book_item, parent, false);
            return new HomeViewHolder(v);
        }else if(fragment instanceof ProfileFrag){
            System.out.println("Profile frag");
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.simplebookitem, parent, false);
            return new SimpleViewHolder(v);
        }
        return null;
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i, @NonNull PDFBook pdfBook) {
        String key = getRef(i).getKey();
        System.out.println("IN ON BINDVIEWHOLDER");
        if(fragment instanceof HomeFrag)
        {
            ((HomeViewHolder)viewHolder).bind(pdfBook);
        }
        else if(fragment instanceof ProfileFrag){
            ((SimpleViewHolder)viewHolder).bind(pdfBook);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, DisplayPDF.class);
                intent.putExtra("url",pdfBook.getUrl());
                intent.putExtra("bookname",pdfBook.getName());
                context.startActivity(intent);
            }
        });
    }

    private class SimpleViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvItemBook;
        ImageView ivCover;
        public SimpleViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivSimpleBookItem);
            tvItemBook=itemView.findViewById(R.id.tvSimpleBookItem);
        }

        public void bind(PDFBook book){
            tvItemBook.setText(book.getName());
            System.out.println("VIEW BIND OF SIMPLEVIEW");
            if(book.getCoverUrl()!=null) {
                Picasso.get()
                        .load(book.getCoverUrl()) // Assuming coverUrl is the Firebase Storage URL
                        .placeholder(R.drawable.ic_bookcover) // Placeholder image while loading
                        .into(ivCover);
            }else
            {
                ivCover.setImageResource(R.drawable.ic_bookcover);
            }
        }
    }
    private class SearchViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvItemBook;
        ImageView ivCover;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemBook = itemView.findViewById(R.id.tvItemBookName);
            ivCover=itemView.findViewById(R.id.ivItemBook);
        }

        public void bind(PDFBook book){
            tvItemBook.setText(book.getName());
            System.out.println("VIEW BIND OF SEARCHVIEW");
        }
    }
    private class HomeViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvItemBook;
        ImageView ivCover;
        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemBook = itemView.findViewById(R.id.tvItemBookName);
            ivCover=itemView.findViewById(R.id.ivItemBook);
        }
        public void bind(PDFBook book){
            tvItemBook.setText(book.getName());
            System.out.println("VIEW BIND OF HOMEVIEW");

            // Load the image into the ImageView using Glide
            if(book.getCoverUrl()!=null) {
                Picasso.get()
                        .load(book.getCoverUrl()) // Assuming coverUrl is the Firebase Storage URL
                        .placeholder(R.drawable.ic_bookcover) // Placeholder image while loading
                        .into(ivCover);
            }else
            {
                ivCover.setImageResource(R.drawable.ic_bookcover);
            }
        }
    }
}