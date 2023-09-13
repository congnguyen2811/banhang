package com.example.appbanhang.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.appbanhang.Interface.ItemClickDeleteListener;
import com.example.appbanhang.R;
import com.example.appbanhang.Utils.Utils;
import com.example.appbanhang.adapter.DonHangAdapter;
import com.example.appbanhang.model.DonHang;
import com.example.appbanhang.retrofit.ApiBanHang;
import com.example.appbanhang.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class XemDonActivity extends AppCompatActivity {
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    RecyclerView rcv_DH;
    Toolbar toolbar;
    List<DonHang> donHangList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_don);

        initview();
        inittoolbar();
        getOrder();


    }
    private void getOrder() {
        donHangList = new ArrayList<>();
        compositeDisposable.add(apiBanHang.xemDonHang(Utils.user.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        donHangModel -> {
                            if(donHangModel.isSuccess()){
                                donHangList = donHangModel.getResult();
                                DonHangAdapter donHangAdapter = new DonHangAdapter(this, donHangList, new ItemClickDeleteListener() {
                                    @Override
                                    public void onClickDelete(int iddonhang) {
                                        showDeleteOrder(iddonhang);
                                    }
                                });
                                rcv_DH.setAdapter(donHangAdapter);
                                donHangAdapter.notifyDataSetChanged();
                            }

                        },
                        throwable -> {
                            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void showDeleteOrder(int iddonhang) {
        PopupMenu menu = new PopupMenu(this,rcv_DH.findViewById(R.id.item_txtTinhTrangDon));
        menu.inflate(R.menu.menu_delete);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.deleteOrder){
                    deleteOrder(iddonhang);
                }
                return false;
            }
        });
        menu.show();
    }

    private void deleteOrder(int iddonhang) {
        compositeDisposable.add(apiBanHang.deleteorder(iddonhang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if(messageModel.isSuccess()){
                                getOrder();
                            }

                        },
                        throwable -> {
                            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));

    }

    private void inittoolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initview() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        toolbar = findViewById(R.id.toolbar_DH);
        rcv_DH = findViewById(R.id.rcv_DH);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcv_DH.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}