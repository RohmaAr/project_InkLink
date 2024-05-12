package com.example.project_inklink;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ImageBookRecyclerAdapter extends FirebaseRecyclerAdapter<ImageBook, RecyclerView.ViewHolder> {
    Context context;

    Fragment fragment;
    public ImageBookRecyclerAdapter(@NonNull FirebaseRecyclerOptions<ImageBook> options, Context context) {
        super(options);
        this.context = context;
        System.out.println("CALLING IMAGERECYCLE CONSTRUCTOR FOR "+context.toString());
    }
    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
        System.out.println("IMAGEEEE Fragment set of "+fragment.toString());
    }
    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i, @NonNull ImageBook imageBook) {
        String key = getRef(i).getKey();
        System.out.println("IMAGEEEE IN ON BINDVIEWHOLDER");
        if(fragment instanceof HomeFrag)
        {
            ((ImageBookRecyclerAdapter.HomeViewHolder)viewHolder).bind(imageBook);
        }
        else if(fragment instanceof ProfileFrag){
            ((ImageBookRecyclerAdapter.SimpleViewHolder)viewHolder).bind(imageBook);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, DisplayImageBook.class);
                intent.putExtra("Uris",imageBook.getUrls());
                intent.putExtra("bookname",imageBook.getName());
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        System.out.println("IMAGEEEE IN ON CREATEVIEW HOLDER");
        if(fragment instanceof HomeFrag) {
            System.out.println("IMAGE HOME FRAMENT");
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.book_item, parent, false);
            return new ImageBookRecyclerAdapter.HomeViewHolder(v);
        }else if(fragment instanceof ProfileFrag){
            System.out.println("IMAGEEEE Profile frag");
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.simplebookitem, parent, false);
            return new ImageBookRecyclerAdapter.SimpleViewHolder(v);
        }
        return null;
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

        public void bind(ImageBook book){
            tvItemBook.setText(book.getName());
            System.out.println("IMAGEEEE VIEW BIND OF SIMPLEVIEW");
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

        public void bind(ImageBook book){
            tvItemBook.setText(book.getName());
            System.out.println("IMAGEEEE VIEW BIND OF SEARCHVIEW");
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
        public void bind(ImageBook book){
            tvItemBook.setText(book.getName());
            System.out.println("IMAGEEEE VIEW BIND OF HOMEVIEW");

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
