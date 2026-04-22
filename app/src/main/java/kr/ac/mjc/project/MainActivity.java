package kr.ac.mjc.project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
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

        // 드래그 앤 드롭으로 순서 변경 설정
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                
                List<Category> categories = adapter.getCategories();
                Collections.swap(categories, fromPosition, toPosition);
                adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // 사용 안 함
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                // 이동이 완료되면 DB에 순서 저장
                saveCategoryOrder();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

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
        
        // 최초 실행 시에만 기본 카테고리 생성 (SharedPreferences 사용)
        android.content.SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("is_first_run", true);

        if (isFirstRun && categories.isEmpty()) {
            db.categoryDao().insert(new Category("공부"));
            db.categoryDao().insert(new Category("자기계발"));
            db.categoryDao().insert(new Category("개발"));
            db.categoryDao().insert(new Category("취미"));
            
            // 초기화 완료 플래그 저장
            prefs.edit().putBoolean("is_first_run", false).apply();

            categories = db.categoryDao().getAllCategories();
        }
        
        adapter.setCategories(categories);
    }

    private void saveCategoryOrder() {
        List<Category> categories = adapter.getCategories();
        new Thread(() -> {
            for (int i = 0; i < categories.size(); i++) {
                Category category = categories.get(i);
                category.setOrderIndex(i);
                db.categoryDao().update(category);
            }
        }).start();
    }
}
