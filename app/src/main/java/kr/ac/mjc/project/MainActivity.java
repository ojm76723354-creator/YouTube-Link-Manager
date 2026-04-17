package kr.ac.mjc.project;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 다크 모드 고정
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        // 레이아웃의 recycler_view_categories 연결
        recyclerView = findViewById(R.id.recycler_view_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new CategoryAdapter();
        recyclerView.setAdapter(adapter);

        // 카테고리 클릭 시 -> 해당 카테고리의 영상 목록 화면으로 이동
        adapter.setOnItemClickListener(category -> {
            Intent intent = new Intent(MainActivity.this, VideoListActivity.class);
            intent.putExtra("category", category.getName());
            startActivity(intent);
        });

        // 우측 하단 추가 버튼 (기존 기능 유지: 영상 추가 화면으로 이동)
        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditVideoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
    }

    private void loadCategories() {
        List<Category> categories = db.categoryDao().getAllCategories();
        
        // 데이터가 아예 없으면 기본 카테고리 생성
        if (categories.isEmpty()) {
            db.categoryDao().insert(new Category("공부"));
            db.categoryDao().insert(new Category("자기계발"));
            db.categoryDao().insert(new Category("개발"));
            db.categoryDao().insert(new Category("취미"));
            categories = db.categoryDao().getAllCategories();
        }
        
        adapter.setCategories(categories);
    }
}
