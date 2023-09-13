package com.example.appbanhang.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.appbanhang.R;
import com.example.appbanhang.Utils.Utils;
import com.example.appbanhang.retrofit.ApiBanHang;
import com.example.appbanhang.retrofit.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ResetPassActivity extends AppCompatActivity {
    EditText edtResetPass;
    ApiBanHang apiBanHang;
    ProgressBar progressBar;

    AppCompatButton btnQuenMk;


    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        initView();
        initControl();
    }

    private void initControl() {
        btnQuenMk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = edtResetPass.getText().toString().trim();
                if(TextUtils.isEmpty(strEmail)){
                    Toast.makeText(ResetPassActivity.this, "Bạn chưa nhập email", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
//                    compositeDisposable.add(apiBanHang.resetPass(strEmail)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(
//                                    userModel -> {
//                                        if(userModel.isSuccess()){
//                                            Toast.makeText(ResetPassActivity.this, userModel.getMessage(), Toast.LENGTH_SHORT).show();
//                                            Intent intent = new Intent(getApplicationContext(),DangNhapActivity.class);
//                                            startActivity(intent);
//                                            finish();
//                                        }
//                                        else {
//                                            Toast.makeText(ResetPassActivity.this, userModel.getMessage(), Toast.LENGTH_SHORT).show();
//                                        }
//                                        progressBar.setVisibility(View.INVISIBLE);
//                                    },
//                                    throwable -> {
//                                        Toast.makeText(ResetPassActivity.this, "", Toast.LENGTH_SHORT).show();
//                                        progressBar.setVisibility(View.INVISIBLE);
//                                    }
//                            ));

                    // reset pass onfire base
                    FirebaseAuth.getInstance().sendPasswordResetEmail(strEmail)
                            .addOnCompleteListener(task -> {
                               if(task.isSuccessful()){
                                   Toast.makeText(ResetPassActivity.this, "Kiểm tra email của banh", Toast.LENGTH_SHORT).show();
                               }
                               finish();
                            });

                }
            }
        });
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        edtResetPass = findViewById(R.id.edtResetPass);
        btnQuenMk = findViewById(R.id.btnQuenMK);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}