package com.example.securestorage.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes",
        foreignKeys = @ForeignKey(
                entity        = User.class,
                parentColumns = "id",
                childColumns  = "userId",
                onDelete      = ForeignKey.CASCADE))
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int    userId;
    private String title;
    private String content;
    private long   createdAt;

    public Note(int userId, String title, String content) {
        this.userId    = userId;
        this.title     = title;
        this.content   = content;
        this.createdAt = System.currentTimeMillis();
    }

    public int    getId()        { return id; }
    public int    getUserId()    { return userId; }
    public String getTitle()     { return title; }
    public String getContent()   { return content; }
    public long   getCreatedAt() { return createdAt; }

    public void setId(int id)           { this.id = id; }
    public void setUserId(int u)        { this.userId = u; }
    public void setTitle(String t)      { this.title = t; }
    public void setContent(String c)    { this.content = c; }
    public void setCreatedAt(long ts)   { this.createdAt = ts; }

    @Override
    public String toString() {
        return "Note{ id=" + id + ", title='" + title + "' }";
    }
}