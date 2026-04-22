package kr.ac.mjc.project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class VideoListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private AppDatabase db;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        db = AppDatabase.getInstance(this);
        categoryName = getIntent().getStringExtra("category");

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 제목 및 뒤로가기 버튼 설정
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(categoryName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // 툴바의 뒤로가기 버튼 클릭 이벤트
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recycler_view_videos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VideoAdapter();
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add_video);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(VideoListActivity.this, AddEditVideoActivity.class);
            intent.putExtra("category", categoryName);
            startActivity(intent);
        });

        // 1. 단순 클릭 시: 유튜브 영상 재생
        adapter.setOnItemClickListener(video -> {
            String url = video.getUrl();
            if (url != null && !url.isEmpty()) {
                try {
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "https://" + url;
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "유효하지 않은 링크입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 2. 길게 클릭 시: 수정/삭제
        adapter.setOnItemLongClickListener(video -> {
            Intent intent = new Intent(VideoListActivity.this, AddEditVideoActivity.class);
            intent.putExtra("id", video.getId());
            intent.putExtra("title", video.getTitle());
            intent.putExtra("url", video.getUrl());
            intent.putExtra("memo", video.getMemo());
            intent.putExtra("category", video.getCategory());
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_video_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit_category) {
            showEditCategoryDialog();
            return true;
        } else if (item.getItemId() == R.id.action_delete_category) {
            showDeleteCategoryDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEditCategoryDialog() {
        EditText etNewName = new EditText(this);
        etNewName.setText(categoryName);
        etNewName.setSelection(categoryName.length());

        new AlertDialog.Builder(this)
                .setTitle("카테고리 이름 변경")
                .setView(etNewName)
                .setPositiveButton("변경", (dialog, which) -> {
                    String newName = etNewName.getText().toString().trim();
                    if (!newName.isEmpty() && !newName.equals(categoryName)) {
                        new Thread(() -> {
                            db.categoryDao().updateCategoryName(categoryName, newName);
                            db.videoDao().updateCategoryNameForVideos(categoryName, newName);
                            categoryName = newName;
                            runOnUiThread(() -> {
                                if (getSupportActionBar() != null) {
                                    getSupportActionBar().setTitle(categoryName);
                                }
                                loadVideos();
                                Toast.makeText(this, "이름이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                            });
                        }).start();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void showDeleteCategoryDialog() {
        new AlertDialog.Builder(this)
                .setTitle("카테고리 삭제")
                .setMessage("'" + categoryName + "' 카테고리를 삭제하시겠습니까?\n이 카테고리의 영상은 삭제되지 않습니다.")
                .setPositiveButton("삭제", (dialog, which) -> {
                    new Thread(() -> {
                        Category category = null;
                        List<Category> categories = db.categoryDao().getAllCategories();
                        for (Category c : categories) {
                            if (c.getName().equals(categoryName)) {
                                category = c;
                                break;
                            }
                        }
                        if (category != null) {
                            db.categoryDao().delete(category);
                            runOnUiThread(() -> {
                                Toast.makeText(this, "카테고리가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }
                    }).start();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVideos();
    }

    private void loadVideos() {
        List<VideoItem> videos = db.videoDao().getVideosByCategory(categoryName);
        adapter.setVideos(videos);
    }
}
