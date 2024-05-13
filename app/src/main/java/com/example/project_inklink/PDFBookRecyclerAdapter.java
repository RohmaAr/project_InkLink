package com.example.project_inklink;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class PDFBookRecyclerAdapter extends FirebaseRecyclerAdapter<PDFBook, RecyclerView.ViewHolder> {

    Context context;
    Fragment fragment;
    String userName;
    public PDFBookRecyclerAdapter(Context context, @NonNull FirebaseRecyclerOptions<PDFBook> options,String userName) {
        super(options);
        this.userName=userName;
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
                    .inflate(R.layout.simplebookitem, parent, false);
            return new HomeViewHolder(v);
        }else if(fragment instanceof ProfileFrag){
            System.out.println("Profile frag");
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.simplebookitem, parent, false);
            return new HomeViewHolder(v);
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
            ((HomeViewHolder)viewHolder).bind(pdfBook);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(context, DisplayPDF.class);
//                intent.putExtra("url",pdfBook.getUrl());
//                intent.putExtra("bookname",pdfBook.getName());
//                context.startActivity(intent);

                Intent intent=new Intent(context, ViewBookDetail.class);
                intent.putExtra("book",pdfBook);
                intent.putExtra("username",userName);
                context.startActivity(intent);
            }
        });
    }

    private class WholeWidthViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvItemBook;
        ImageView ivCover;
        public WholeWidthViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivItemBook);
            tvItemBook=itemView.findViewById(R.id.tvItemBookName);
        }

        public void bind(PDFBook book){
            tvItemBook.setText(book.getName());
            System.out.println("VIEW BIND OF SIMPLEVIEW");
            if(book.getCoverUrl()!=null) {
                Picasso.get()
                        .load(book.getCoverUrl()) // Assuming coverUrl is the Firebase Storage URL
                        .placeholder(R.drawable.ic_noimage) // Placeholder image while loading
                        .into(ivCover);
            }else
            {
                ivCover.setImageResource(R.drawable.ic_noimage);
            }
        }
    }
    private class HomeViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvItemBook;
        ImageView ivCover;
        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemBook = itemView.findViewById(R.id.tvSimpleBookItem);
            ivCover=itemView.findViewById(R.id.ivSimpleBookItem);
        }
        public void bind(PDFBook book){
            tvItemBook.setText(book.getName());
            System.out.println("VIEW BIND OF HOMEVIEW");

            // Load the image into the ImageView using Glide
            if(book.getCoverUrl()!=null) {
                Picasso.get()
                        .load(book.getCoverUrl()) // Assuming coverUrl is the Firebase Storage URL
                        .placeholder(R.drawable.ic_noimage) // Placeholder image while loading
                        .into(ivCover);
            }else
            {
                ivCover.setImageResource(R.drawable.ic_noimage);
            }
        }
    }
}