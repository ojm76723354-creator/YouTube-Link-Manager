package kr.ac.mjc.project;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "videos")
public class VideoItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String title;
    private String url;
    private String memo;
    private String category;
    private boolean isWatched;

    public VideoItem(String title, String url, String memo, String category, boolean isWatched) {
        this.title = title;
        this.url = url;
        this.memo = memo;
        this.category = category;
        this.isWatched = isWatched;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isWatched() { return isWatched; }
    public void setWatched(boolean watched) { isWatched = watched; }
}
