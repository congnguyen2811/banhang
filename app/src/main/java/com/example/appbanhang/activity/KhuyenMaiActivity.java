package com.example.appbanhang.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appbanhang.R;

public class KhuyenMaiActivity extends AppCompatActivity {
    TextView txtNoidung_KM;
    ImageView img_KM;
    Toolbar toolbarKM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khuyen_mai);


        initView();
        initData();
        getactionBar();
    }

    private void getactionBar() {
        setSupportActionBar(toolbarKM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarKM.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        String nd = getIntent().getStringExtra("noidung");
        String url = getIntent().getStringExtra("url");
        txtNoidung_KM.setText(nd);
        Glide.with(this).load(url).into(img_KM);
    }

    private void initView() {
        txtNoidung_KM = findViewById(R.id.txtNoidung_KM);
        img_KM = findViewById(R.id.img_KM);
        toolbarKM = findViewById(R.id.toolbarKM);
    }
}