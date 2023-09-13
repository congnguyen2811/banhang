package com.example.appbanhang.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.appbanhang.R;
import com.example.appbanhang.Utils.Utils;
import com.example.appbanhang.adapter.GiohangAdapter;
import com.example.appbanhang.model.EventBus.TinhTongEvent;
import com.example.appbanhang.model.GioHang;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.List;

import okhttp3.internal.Util;

public class GioHangActivity extends AppCompatActivity {
    TextView txtGioHangTrong,txtTongTien;
    RecyclerView rcvGioHang;
    Button btnMuaHang;
    Toolbar toolbar;
    GiohangAdapter giohangAdapter;
    long tongtien;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);
        initData();
        initControl();

        if(Utils.muaHangList != null){
            Utils.muaHangList.clear();
        }
        tinhTongTien();
    }

    private void tinhTongTien() {
        tongtien = 0;
        for(int i = 0 ;i<Utils.muaHangList.size();i++){
            tongtien = tongtien + (Utils.muaHangList.get(i).getGiasp()*Utils.muaHangList.get(i).getSoluong());
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        txtTongTien.setText(decimalFormat.format(tongtien));
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rcvGioHang.setHasFixedSize(true);
        RecyclerView.LayoutManager  layoutManager = new LinearLayoutManager(this);
        rcvGioHang.setLayoutManager(layoutManager);
        if(Utils.gioHangList.size() == 0){
            txtGioHangTrong.setVisibility(View.VISIBLE);
        }
        else {
            giohangAdapter = new GiohangAdapter(getApplicationContext(),Utils.gioHangList);
            rcvGioHang.setAdapter(giohangAdapter);
        }

        btnMuaHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTT = new Intent(getApplicationContext(),ThanhToanActivity.class);
                intentTT.putExtra("tongtien",tongtien);
//                Utils.muaHangList.clear();
                startActivity(intentTT);
            }
        });
    }

    private void initData() {
        txtGioHangTrong  = findViewById(R.id.txtGioHangEmty);
        txtTongTien = findViewById(R.id.txtTongtien);
        btnMuaHang = findViewById(R.id.btnMuaHang);
        toolbar = findViewById(R.id.toolbarGH);
        rcvGioHang = findViewById(R.id.rcvGiohang);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public  void enventTinhTongTien(TinhTongEvent tinhTongEvent){
        if(tinhTongEvent != null){
            tinhTongTien();
        }
    }
}