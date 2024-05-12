package com.example.project_inklink;

import java.util.ArrayList;

public class Book {
    ArrayList<String> genres;
    String name;
    String desc;
    int views;

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    String coverUrl;

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    int likes;

    public Book(ArrayList<String> genres, String name, String desc, int views, String owner,String image) {
        this.genres = genres;
        this.name = name;
        this.desc = desc;
        this.views = views;
        this.owner = owner;
        this.coverUrl=image;
    }

    String owner;

    public Book() {

    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
