package com.example.project_inklink;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AllAdapter extends RecyclerView.Adapter<AllAdapter.ViewHolder>
{
    ArrayList<Book> books;
    Context context;
    String userName;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.book_item,parent,false);
        return new ViewHolder(view);
    }

    public AllAdapter(ArrayList<Book> books, Context context,String username) {
        this.books = books;
        this.userName=username;
        this.context = context;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvItemBookName.setText(books.get(position).getName());
        if(!books.get(position).isPaid())
            holder.ivPaid.setVisibility(View.GONE);


        Picasso.get()
                .load(books.get(position).getCoverUrl()) // Assuming coverUrl is the Firebase Storage URL
                .placeholder(R.drawable.ic_noimage) // Placeholder image while loading
                .into(holder.ivItemBook);

        if(books.get(position) instanceof PDFBook)
        {
            holder.ivBookType.setImageResource(R.drawable.ic_bookcover);
        }
        else {
            holder.ivBookType.setImageResource(R.drawable.ic_camera);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ViewBookDetail.class);
                intent.putExtra("book",books.get(position));
                intent.putExtra("username",userName);
                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return books.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivItemBook, ivPaid, ivBookType;
        private TextView tvItemBookName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookType=itemView.findViewById(R.id.ivBookType);
            ivPaid=itemView.findViewById(R.id.ivPaid);
            ivItemBook=itemView.findViewById(R.id.ivItemBook);
            tvItemBookName=itemView.findViewById(R.id.tvItemBookName);

        }
    }
}