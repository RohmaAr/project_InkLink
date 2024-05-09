package com.example.project_inklink;

import java.io.Serializable;

public class User implements Serializable {

    String pfpUrl;

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    String username;

    public String getPfpUrl() {
        return pfpUrl;
    }

    public void setPfpUrl(String pfpUrl) {
        this.pfpUrl = pfpUrl;
    }

    public User(String pfpUrl, String username, String email) {
        this.pfpUrl = pfpUrl;
        this.username = username;
        this.email = email;
    }

    String email;
}
