package com.example.appbanhang.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.appbanhang.R;
import com.example.appbanhang.Utils.Utils;
import com.example.appbanhang.adapter.LoaiSpAdapter;
import com.example.appbanhang.adapter.SanPhamMoiAdapter;
import com.example.appbanhang.model.LoaiSP;
import com.example.appbanhang.model.SanPhamMoi;
import com.example.appbanhang.model.User;
import com.example.appbanhang.retrofit.ApiBanHang;
import com.example.appbanhang.retrofit.RetrofitClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
//    ViewFlipper viewFlipper;
    RecyclerView rcvManHinhChinh;
    ListView lvManHinhChinh;
    NavigationView navView;
    DrawerLayout drawerLayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSP> spList;
    CompositeDisposable  compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<SanPhamMoi> sanPhamMoiList;
    SanPhamMoiAdapter sanPhamMoiAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout;
    ImageView imgSearch,imgChatMess;
    ImageSlider imageSlider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        Paper.init(this);
        if(Paper.book().read("user") != null){
            User user =Paper.book().read("user");
            Utils.user =user;
        }
        getToken();
        anhXa();
        actionBar();
        searchProduct();
        if(isConnect(this)){
            actionViewFlipper();
            getLoaiSP();
            getSpMoi();
            getEventClick();
        }
        else {
            Toast.makeText(this, "Khong co internet", Toast.LENGTH_SHORT).show();
        }
    }


    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (!TextUtils.isEmpty(s)) {
                            compositeDisposable.add(apiBanHang.updateToken(Utils.user.getId(),s)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            messageModel -> {
                                            },
                                            throwable -> {
                                                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                    ));
                        }
                    }
                });
        compositeDisposable.add(apiBanHang.getToken(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.isSuccess()){
                                Utils.ID_RECEIVED = String.valueOf(userModel.getResult().get(0).getId());
                            }
                        },
                        throwable -> {
                            Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void searchProduct() {
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSearch  =  new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(intentSearch);
            }
        });
    }

    private void getEventClick() {
        lvManHinhChinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent trangchu = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(trangchu);
                        break;
                    case 1:
                        Intent dienthoai = new Intent(MainActivity.this,DienThoaiActivity.class);
                        dienthoai.putExtra("loai",1);
                        startActivity(dienthoai);
                        break;
                    case 2:
                        Intent laptop = new Intent(MainActivity.this,DienThoaiActivity.class);
                        laptop.putExtra("loai",2);
                        startActivity(laptop);
                        break;

                    case 5:
                        Intent donHang = new Intent(MainActivity.this,XemDonActivity.class);
                        startActivity(donHang);
                        break;
                    case 6:
                        Paper.book().delete("user");
                        Intent dangxuat = new Intent(MainActivity.this,DangNhapActivity.class);
                        FirebaseAuth.getInstance().signOut();
                        startActivity(dangxuat);
                        finish();
                        break;
                }
            }
        });
    }

    private void getSpMoi() {
        compositeDisposable.add(apiBanHang.getSPMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {

                            sanPhamMoiList = sanPhamMoiModel.getResult();

                            sanPhamMoiAdapter = new SanPhamMoiAdapter(getApplicationContext(),sanPhamMoiList);
                            rcvManHinhChinh.setAdapter(sanPhamMoiAdapter);

                        },
                        throwable -> {
                            Toast.makeText(this, "Khong ket noi duoc voi sever roi " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void getLoaiSP() {
        compositeDisposable.add(apiBanHang.getLoaiSP()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSPModel -> {
                            if(loaiSPModel.isSuccess()){
                //                Toast.makeText(this, loaiSPModel.getResult().get(0).getTensanpham(), Toast.LENGTH_SHORT).show();
                                spList = loaiSPModel.getResult();
                                spList.add(new LoaiSP("Đăng xuất"," "));
                                loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(),spList);
                                lvManHinhChinh.setAdapter(loaiSpAdapter);
                            }
                        }
                ));
    }

    private void actionViewFlipper() {
        List<SlideModel> imglist = new ArrayList<>();
        compositeDisposable.add(apiBanHang.getKhuyenmai()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        khuyenMaiModel -> {
                            if(khuyenMaiModel.isSuccess()){
                               for(int i = 0 ; i < khuyenMaiModel.getResult().size();i++){
                                   imglist.add(new SlideModel(khuyenMaiModel.getResult().get(i).getUrl(),null));
                               }
                                imageSlider.setImageList(imglist, ScaleTypes.CENTER_CROP);
                                imageSlider.setItemClickListener(new ItemClickListener() {
                                    @Override
                                    public void onItemSelected(int i) {
                                        Intent intent = new Intent(getApplicationContext(),KhuyenMaiActivity.class);
                                        intent.putExtra("noidung",khuyenMaiModel.getResult().get(i).getThongtin());
                                        intent.putExtra("url",khuyenMaiModel.getResult().get(i).getUrl());
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void doubleClick(int i) {

                                    }
                                });
                            }
                            else {
                                Toast.makeText(this, "Lỗi", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Log.d("error",throwable.getMessage());
                        }
                ));
//        imglist.add(new SlideModel("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-Le-hoi-phu-kien-800-300.png",null));
//        imglist.add(new SlideModel("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-HC-Tra-Gop-800-300.png",null));
//        imglist.add(new SlideModel("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-big-ky-nguyen-800-300.jpg",null));
//        imageSlider.setImageList(imglist, ScaleTypes.CENTER_CROP);
//        imageSlider.setItemClickListener(new ItemClickListener() {
//            @Override
//            public void onItemSelected(int i) {
//                Intent intent = new Intent(getApplicationContext(),KhuyenMaiActivity.class);
//                startActivity(intent);
//            }
//
//            @Override
//            public void doubleClick(int i) {
//
//            }
//        });
//        List<String> listQuangCao = new ArrayList<>();
//        listQuangCao.add("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-Le-hoi-phu-kien-800-300.png");
//        listQuangCao.add("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-HC-Tra-Gop-800-300.png");
//        listQuangCao.add("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-big-ky-nguyen-800-300.jpg");
//        for(int i = 0 ;  i < listQuangCao.size();i++){
//            ImageView imageView = new ImageView(getApplicationContext());
//            Glide.with(getApplicationContext()).load(listQuangCao.get(i)).into(imageView);
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            viewFlipper.addView(imageView);
//        }
//        viewFlipper.setFlipInterval(3000);
//        viewFlipper.setAutoStart(true);
//        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in_right);
//        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_out_right);
//        viewFlipper.setInAnimation(slide_in);
//        viewFlipper.setOutAnimation(slide_out);
    }

    private void actionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START,true);
            }
        });
    }

    private void anhXa() {
        toolbar = findViewById(R.id.toolbarManHinhChinh);
//        viewFlipper = findViewById(R.id.viewFlipper);
        rcvManHinhChinh = findViewById(R.id.rcv);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        rcvManHinhChinh.setLayoutManager(layoutManager);
        rcvManHinhChinh.setHasFixedSize(true);
        lvManHinhChinh = findViewById(R.id.lvManHinhChinh);
        navView = findViewById(R.id.navView);
        drawerLayout = findViewById(R.id.drawerLayout);
        badge = findViewById(R.id.menu_sl);
        imgSearch = findViewById(R.id.imgSearch);
        frameLayout = findViewById(R.id.frameGioHang);
        imgChatMess = findViewById(R.id.imgMessChat);
        imageSlider = findViewById(R.id.image_slider);
        spList = new ArrayList<>();
        sanPhamMoiList = new ArrayList<>();
        if(Paper.book().read("giohang") != null){
            Utils.gioHangList = Paper.book().read("giohang");
        }
        if(Utils.gioHangList == null){
            Utils.gioHangList = new ArrayList<>();
        }
        else {
            int totalItem = 0;
            for(int i = 0 ; i< Utils.gioHangList.size();i++){
                totalItem = totalItem + Utils.gioHangList.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGH = new Intent(getApplicationContext(),GioHangActivity.class);
                startActivity(intentGH);
            }
        });
        imgChatMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChat = new Intent(getApplicationContext(),ChatActivity.class);
                startActivity(intentChat);
            }
        });
        // Khoi tao adapter
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for(int i = 0 ; i< Utils.gioHangList.size();i++){
            totalItem = totalItem + Utils.gioHangList.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    private boolean isConnect(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected())){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}