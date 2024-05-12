package com.example.project_inklink;

import java.util.ArrayList;

public class ImageBook extends Book{
    ArrayList<String> urls;

    public ArrayList<String> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    public ImageBook(){

    }
    public ImageBook(ArrayList<String> genres, String name, String desc, int views, String owner, ArrayList<String> urls,String image) {
        super(genres, name, desc, views, owner,image);
        this.urls = urls;
    }
}
