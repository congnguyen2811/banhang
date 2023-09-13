package com.example.appbanhang.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appbanhang.R;
import com.example.appbanhang.Utils.Utils;
import com.example.appbanhang.model.GioHang;
import com.example.appbanhang.model.NotiSendData;
import com.example.appbanhang.retrofit.ApiBanHang;
import com.example.appbanhang.retrofit.ApiPushNotification;
import com.example.appbanhang.retrofit.RetrofitClienNoti;
import com.example.appbanhang.retrofit.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.momo.momo_partner.AppMoMoLib;

public class ThanhToanActivity extends AppCompatActivity {
    Toolbar toolbar_TT;
    TextView txtTongtien_TT,txtSoDT_TT,txtEmail_TT;
    EditText edtDiachi_TT;
    AppCompatButton btnDathang,btnThanhtoanmomo;
    ApiBanHang apiBanHang;
    ApiPushNotification apiPushNotification;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    long tongTien ;
    int totalItem;
    int iddonhang;

    private String amount = "10000";
    private String fee = "0";
    int environment = 0;//developer default
    private String merchantName = "NguyenCong";
    private String merchantCode = "SCB01";
    private String merchantNameLabel = "NguyenCongShop";
    private String description = "Mua hàng online";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);

        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION
        initview();
        countItem();
        initControl();
    }

    private void countItem() {
         totalItem = 0;
        for(int i = 0 ; i< Utils.muaHangList.size();i++){
            totalItem = totalItem + Utils.muaHangList.get(i).getSoluong();
        }
    }
    //Get token through MoMo app
    private void requestPayment(String iddonhang) {
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);


        Map<String, Object> eventValue = new HashMap<>();
        //client Required
        eventValue.put("merchantname", merchantName); //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue.put("merchantcode", merchantCode); //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue.put("amount", amount); //Kiểu integer
        eventValue.put("orderId", iddonhang); //uniqueue id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
        eventValue.put("orderLabel", iddonhang); //gán nhãn

        //client Optional - bill info
        eventValue.put("merchantnamelabel", "Dịch vụ");//gán nhãn
        eventValue.put("fee", "0"); //Kiểu integer
        eventValue.put("description", description); //mô tả đơn hàng - short description

        //client extra data
        eventValue.put("requestId",  merchantCode+"merchant_billId_"+System.currentTimeMillis());
        eventValue.put("partnerCode", merchantCode);
        //Example extra data
        JSONObject objExtraData = new JSONObject();
        try {
            objExtraData.put("site_code", "008");
            objExtraData.put("site_name", "CGV Cresent Mall");
            objExtraData.put("screen_code", 0);
            objExtraData.put("screen_name", "Special");
            objExtraData.put("movie_name", "Kẻ Trộm Mặt Trăng 3");
            objExtraData.put("movie_format", "2D");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        eventValue.put("extraData", objExtraData.toString());

        eventValue.put("extra", "");
        AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue);


    }
    //Get token callback from MoMo app an submit to server side
      protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if(data != null) {
                if(data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE
                    Log.d("thanhcong",data.getStringExtra("message"));
                    String token = data.getStringExtra("data"); //Token response
                   compositeDisposable.add(apiBanHang.updateMoMo(iddonhang,token)
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(
                                   messageModel -> {
                                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                   },
                                   throwable -> {
                                       Log.d("error",throwable.getMessage());
                                   }
                           ));
                    String phoneNumber = data.getStringExtra("phonenumber");
                    String env = data.getStringExtra("env");
                    if(env == null){
                        env = "app";
                    }

                    if(token != null && !token.equals("")) {
                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                        // IF Momo topup success, continue to process your order
                    } else {
                        Log.d("log","Không thành công");
                    }
                } else if(data.getIntExtra("status", -1) == 1) {
                    //TOKEN FAIL
                    String message = data.getStringExtra("message") != null?data.getStringExtra("message"):"Thất bại";
                    Log.d("log","Không thành công");
                } else if(data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
                    Log.d("log","Không thành công");
                } else {
                    //TOKEN FAIL
                    Log.d("log","Không thành công");
                }
            } else {
                Log.d("log","Không thành công");
            }
        } else {
            Log.d("log","Không thành công");
        }
    }
    private void initControl() {
        setSupportActionBar(toolbar_TT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar_TT.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tongTien = getIntent().getLongExtra("tongtien",0);
        txtTongtien_TT.setText(decimalFormat.format(tongTien));
        txtEmail_TT.setText(Utils.user.getEmail());
        txtSoDT_TT.setText(Utils.user.getMobile());

        btnDathang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strDiachi = edtDiachi_TT.getText().toString().trim();
                if(TextUtils.isEmpty(strDiachi)){
                    Toast.makeText(ThanhToanActivity.this, "Bạn chưa nhập địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                }
                else {
                    // post data
                    String email = Utils.user.getEmail();
                    String sdt = Utils.user.getMobile();
                    int iduser = Utils.user.getId();
//                    Gson gson = new GsonBuilder()
//                            .setLenient()
//                            .create();
                    Log.d("test",new Gson().toJson(Utils.muaHangList));
                    compositeDisposable.add(apiBanHang.creatOder(email,sdt,String.valueOf(tongTien),iduser,strDiachi,totalItem,new Gson().toJson(Utils.muaHangList))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel -> {
                                        if(messageModel.isSuccess()){
                                            pushNotiToUser();
                                            Toast.makeText(ThanhToanActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
                                            // clear giohanglist bang cach chay qua muahanglist va clear item trung
                                            for(int i = 0 ; i<Utils.muaHangList.size();i++){
                                                GioHang gioHang = Utils.muaHangList.get(i);
                                                if(Utils.gioHangList.contains(gioHang)){
                                                    Utils.gioHangList.remove(gioHang);
                                                }
                                            }

                                            Utils.muaHangList.clear();
                                            Paper.book().write("giohang",Utils.gioHangList);
                                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else {
                                            Log.d("logtt",messageModel.getMessage());
                                        }
                                    },
                                    throwable -> {
                                        Log.d("logtt",throwable.getMessage());
                                    }
                            ));
                }
            }
        });

        btnThanhtoanmomo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strDiachi = edtDiachi_TT.getText().toString().trim();
                if(TextUtils.isEmpty(strDiachi)){
                    Toast.makeText(ThanhToanActivity.this, "Bạn chưa nhập địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                }
                else {
                    // post data
                    String email = Utils.user.getEmail();
                    String sdt = Utils.user.getMobile();
                    int iduser = Utils.user.getId();
                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();

                    Log.d("test",new Gson().toJson(Utils.muaHangList));
                    compositeDisposable.add(apiBanHang.creatOder(email,sdt,String.valueOf(tongTien),iduser,strDiachi,totalItem,gson.toJson(Utils.muaHangList))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel -> {
                                        if(messageModel.isSuccess()){
                                            pushNotiToUser();
                                            Toast.makeText(ThanhToanActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
                                            Utils.muaHangList.clear();
                                            iddonhang = Integer.parseInt(messageModel.getIddonhang());
                                            requestPayment(messageModel.getIddonhang());

//                                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//                                            startActivity(intent);
                                            finish();
                                        }
                                        else {
                                            Log.d("logtt",messageModel.getMessage());
                                        }
                                    },
                                    throwable -> {
                                        Toast.makeText(ThanhToanActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                            ));
                }
            }
        });
    }

    private void pushNotiToUser() {
        compositeDisposable.add(apiBanHang.getToken(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.isSuccess()){
                                for(int i = 0 ; i < userModel.getResult().size();i++){
                                    //String token = "cnG07WWqSLmQONOAntsFh5:APA91bFSJPDI0bxWBE6pUt7kb94NqPWbtKU5xAi3OQ3P5ZYilmWtrxVQGL9Idfke5FZ84PrgbfEdU2dFElKe43oTEljRXG5jV57dBNG6MNx05PH5Ekhh6j-ypNgpZTXP2FLpsNR6jvhy";
                                    Map<String,String> data = new HashMap<>();
                                    data.put("title","Thong Bao");
                                    data.put("body","Ban co don hang moi");
                                    NotiSendData  notiSendData = new NotiSendData(userModel.getResult().get(i).getToken(),data);
                                    apiPushNotification = RetrofitClienNoti.getInstance().create(ApiPushNotification.class);
                                    compositeDisposable.add(apiPushNotification.sendNotification(notiSendData)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    notiReponse -> {
                                                    },
                                                    throwable -> {
                                                        Log.d("log", throwable.getMessage());
                                                    }
                                            ));
                                }
                            }
                        },
                        throwable -> {
                            Log.d("log", throwable.getMessage());
                        }
                ));

    }

    private void initview() {
        txtEmail_TT = findViewById(R.id.txtEmail_TT);
        txtSoDT_TT = findViewById(R.id.txtSoDT_TT);
        txtTongtien_TT = findViewById(R.id.txtTongtien_TT);
        edtDiachi_TT = findViewById(R.id.edtDiachi_TT);
        btnDathang = findViewById(R.id.btnDatHang);
        toolbar_TT = findViewById(R.id.toolbarTT);
        btnThanhtoanmomo = findViewById(R.id.btnThanhToanMOMO);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();

    }
}