package kr.ac.mjc.project;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VideoDao {
    @Query("SELECT * FROM videos ORDER BY id DESC")
    List<VideoItem> getAllVideos();

    @Insert
    void insert(VideoItem video);

    @Update
    void update(VideoItem video);

    @Delete
    void delete(VideoItem video);
    
    @Query("SELECT * FROM videos WHERE category = :category ORDER BY id DESC")
    List<VideoItem> getVideosByCategory(String category);

    @Query("UPDATE videos SET category = :newCategory WHERE category = :oldCategory")
    void updateCategoryNameForVideos(String oldCategory, String newCategory);
}
