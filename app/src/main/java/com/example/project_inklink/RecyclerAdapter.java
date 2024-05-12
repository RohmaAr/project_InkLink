package com.example.project_inklink;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.CustomViewHolder> {
    private ArrayList<Uri> imagesArrayList;
    Context context;
    public RecyclerAdapter(Context context, ArrayList<Uri> imagesArrayList) {
        this.imagesArrayList = imagesArrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.singleimage,parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.imageView.setImageURI(imagesArrayList.get(position));
        System.out.println("IN RECYCLER ADAPTER ONCREATEVH");

    }



    @Override
    public int getItemCount() {
        return imagesArrayList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.ivItemPickedImage);

            //DECIDE IF YOU WANT TO ALLOW DELETING AFTER SELECTION
//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//
//
//                    return true;
//                }
//            });
        }
    }
}
