package com.example.project_inklink;

import java.util.ArrayList;

public class PDFBook extends Book {
    String url;

    public PDFBook(String url, ArrayList<String> genres, String name, String desc, int views, String owner,String cover) {
        super(genres, name, desc, views, owner,cover);
        this.url = url;
    }

    public PDFBook() {
    }

    public String getUrl() {
        return url;
    }
}
