package kr.ac.mjc.project;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY orderIndex ASC")
    List<Category> getAllCategories();

    @Insert
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("UPDATE categories SET name = :newName WHERE name = :oldName")
    void updateCategoryName(String oldName, String newName);
}
