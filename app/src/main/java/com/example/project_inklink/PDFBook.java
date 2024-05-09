package com.example.project_inklink;

import java.util.ArrayList;

public class PDFBook extends Book {
    String url;

    public PDFBook(String url, ArrayList<String> genres, String name, String desc, int views, String owner) {
        super(genres, name, desc, views, owner);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
