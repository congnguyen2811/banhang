package com.example.appbanhang.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appbanhang.R;
import com.example.appbanhang.Utils.Utils;
import com.example.appbanhang.retrofit.ApiBanHang;
import com.example.appbanhang.retrofit.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangKiActivity extends AppCompatActivity {
    EditText edtEmail_DK,edtPass_DK,edtrePass_DK,edtMobile,edtUserName_DK;
    AppCompatButton btnDangki;
    ApiBanHang apiBanHang;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ki);

        initView();
        initControl();
    }

    private void initControl() {
        btnDangki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangKi();
            }
        });
    }

    private void dangKi() {
        String strEmail = edtEmail_DK.getText().toString().trim();
        String strPass = edtPass_DK.getText().toString().trim();
        String strRePass = edtrePass_DK.getText().toString().trim();
        String strMobile = edtMobile.getText().toString().trim();
        String strUsername = edtUserName_DK.getText().toString().trim();

        if(TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPass) || TextUtils.isEmpty(strRePass) || TextUtils.isEmpty(strMobile) || TextUtils.isEmpty(strUsername)){
            Toast.makeText(this, "Bạn chưa nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
        else {
            if(strPass.equals(strRePass)){
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(strEmail,strPass)
                        .addOnCompleteListener(DangKiActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser user =firebaseAuth.getCurrentUser();
                                    if(user != null){
                                        postData(strEmail,strPass,strUsername,strMobile,user.getUid());
                                    }
                                }
                                else {
                                    Toast.makeText(DangKiActivity.this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else {
                Toast.makeText(this, "Pass chưa khớp nhau", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void postData(String strEmail,String strPass,String strUsername,String strMobile,String uid){
        compositeDisposable.add(apiBanHang.dangKi(strEmail,"onfirebase",strUsername,strMobile,uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.isSuccess()) {
                                Utils.user.setEmail(strEmail);
                                Utils.user.setPass("onfirebase");
                                Intent intentDN =  new Intent(getApplicationContext(),DangNhapActivity.class);
                                startActivity(intentDN);
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        },
                        throwable -> {
                            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }
    private void initView() {
        edtEmail_DK = findViewById(R.id.edtEmail_DK);
        edtPass_DK = findViewById(R.id.edtPass_DK);
        edtrePass_DK = findViewById(R.id.edtrePass_DK);
        btnDangki = findViewById(R.id.btnDangKi);
        edtMobile = findViewById(R.id.edtMobile);
        edtUserName_DK = findViewById(R.id.edtUserName_DK);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}