package kr.ac.mjc.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AddEditVideoActivity extends AppCompatActivity {

    private EditText etTitle, etUrl, etMemo;
    private Spinner spinnerCategory;
    private Button btnSave, btnDelete, btnCancel, btnAddCategory;
    
    private AppDatabase db;
    private int videoId = -1;
    private boolean isEditMode = false;

    private List<Category> categoryList = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_video);

        db = AppDatabase.getInstance(this);

        etTitle = findViewById(R.id.et_title);
        etUrl = findViewById(R.id.et_url);
        etMemo = findViewById(R.id.et_memo);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);
        btnCancel = findViewById(R.id.btn_cancel);
        btnAddCategory = findViewById(R.id.btn_add_category);

        loadCategories();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            isEditMode = true;
            videoId = intent.getIntExtra("id", -1);
            etTitle.setText(intent.getStringExtra("title"));
            etUrl.setText(intent.getStringExtra("url"));
            etMemo.setText(intent.getStringExtra("memo"));
            String categoryName = intent.getStringExtra("category");
            
            // 저장된 카테고리 선택
            if (categoryName != null) {
                int selection = categoryNames.indexOf(categoryName);
                if (selection >= 0) {
                    spinnerCategory.setSelection(selection);
                }
            }
            btnDelete.setVisibility(View.VISIBLE);
        } else if (intent != null && intent.hasExtra("category")) {
            // 특정 카테고리에서 들어온 경우 자동 선택
            String categoryName = intent.getStringExtra("category");
            int selection = categoryNames.indexOf(categoryName);
            if (selection >= 0) {
                spinnerCategory.setSelection(selection);
            }
        }

        btnSave.setOnClickListener(v -> saveVideo());
        btnDelete.setOnClickListener(v -> deleteVideo());
        btnCancel.setOnClickListener(v -> finish()); // 나가기 기능
        btnAddCategory.setOnClickListener(v -> showAddCategoryDialog());
    }

    private void loadCategories() {
        categoryList = db.categoryDao().getAllCategories();
        
        if (categoryList.isEmpty()) {
            db.categoryDao().insert(new Category("공부"));
            db.categoryDao().insert(new Category("자기계발"));
            db.categoryDao().insert(new Category("개발"));
            db.categoryDao().insert(new Category("취미"));
            categoryList = db.categoryDao().getAllCategories();
        }

        categoryNames.clear();
        for (Category c : categoryList) {
            categoryNames.add(c.getName());
        }

        categoryAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item_custom, categoryNames);
        categoryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_custom);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    private void showAddCategoryDialog() {
        EditText etNewCategory = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("새 카테고리 추가")
                .setView(etNewCategory)
                .setPositiveButton("추가", (dialog, which) -> {
                    String name = etNewCategory.getText().toString().trim();
                    if (!name.isEmpty()) {
                        db.categoryDao().insert(new Category(name));
                        loadCategories(); // 다시 불러오기
                        Toast.makeText(this, "카테고리가 추가되었습니다", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void saveVideo() {
        String title = etTitle.getText().toString().trim();
        String url = etUrl.getText().toString().trim();
        String memo = etMemo.getText().toString().trim();
        
        if (spinnerCategory.getSelectedItem() == null) {
            Toast.makeText(this, "카테고리를 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        String category = spinnerCategory.getSelectedItem().toString();

        if (title.isEmpty() || url.isEmpty()) {
            Toast.makeText(this, "제목과 링크를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        VideoItem video = new VideoItem(title, url, memo, category, false);
        if (isEditMode) {
            video.setId(videoId);
            db.videoDao().update(video);
            Toast.makeText(this, "수정되었습니다", Toast.LENGTH_SHORT).show();
        } else {
            db.videoDao().insert(video);
            Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void deleteVideo() {
        VideoItem video = new VideoItem("", "", "", "", false);
        video.setId(videoId);
        db.videoDao().delete(video);
        Toast.makeText(this, "삭제되었습니다", Toast.LENGTH_SHORT).show();
        finish();
    }
}
