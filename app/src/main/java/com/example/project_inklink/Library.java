package com.example.project_inklink;

import java.util.ArrayList;

public class Library {
    public Library(){}

    public String getUsername() {
        return username;
    }

    public ArrayList<String> getReads() {
        return reads;
    }

    public void setReads(ArrayList<String> reads) {
        this.reads = reads;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    String username;
    ArrayList<String> reads;
}
