package com.example.securestorage.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String username;
    private String email;
    private String password;

    public User(String username, String email, String password) {
        this.username = username;
        this.email    = email;
        this.password = password;
    }

    public int    getId()       { return id; }
    public String getUsername() { return username; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }

    public void setId(int id)             { this.id = id; }
    public void setUsername(String u)     { this.username = u; }
    public void setEmail(String e)        { this.email = e; }
    public void setPassword(String p)     { this.password = p; }

    @Override
    public String toString() {
        return "User{ id=" + id + ", username='" + username + "' }";
    }
}