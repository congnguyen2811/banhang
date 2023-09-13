package com.example.appbanhang.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangNhapActivity extends AppCompatActivity {
    TextView txtDangKi,txtQuenMK;
    EditText edtEmail_DN,edtPass_DN;
    AppCompatButton btnDangNhap;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        
        initView();
        initControl();
    }

    private void initControl() {
        txtDangKi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDK = new Intent(getApplicationContext(),DangKiActivity.class);
                startActivity(intentDK);
            }
        });
        txtQuenMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentReset = new Intent(getApplicationContext(),ResetPassActivity.class);
                startActivity(intentReset);

            }
        });
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmailDN = edtEmail_DN.getText().toString().trim();
                String strPassDN = edtPass_DN.getText().toString().trim();
                if(TextUtils.isEmpty(strEmailDN) || TextUtils.isEmpty(strPassDN)){
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
                else {
                    Paper.book().write("email",strEmailDN);
                    Paper.book().write("pass",strPassDN);
                    if(user != null){
                        // user da dang nhap firebase chua out
                        dangnhap(strEmailDN,strPassDN);
                    }
                    else {
                        // user da sign out
                        firebaseAuth.signInWithEmailAndPassword(strEmailDN,strPassDN)
                                .addOnCompleteListener(DangNhapActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            dangnhap(strEmailDN,strPassDN);
                                        }
                                    }
                                });
                    }

                }
            }
        });
    }

    private void initView() {
        Paper.init(this);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        txtDangKi = findViewById(R.id.txtDangKi);
        edtEmail_DN = findViewById(R.id.edtEmai_DN);
        edtPass_DN = findViewById(R.id.edtPass_DN);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        txtQuenMK = findViewById(R.id.txtQuenMK);
        firebaseAuth =FirebaseAuth.getInstance();
        user =firebaseAuth.getCurrentUser();

        //read data
        if(Paper.book().read("email") != null && Paper.book().read("pass") != null){
            edtEmail_DN.setText(Paper.book().read("email"));
            edtPass_DN.setText(Paper.book().read("pass"));

            if(Paper.book().read("islogin") != null){
                boolean flag = Paper.book().read("islogin");
                if(flag){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            dangnhap(Paper.book().read("email"),Paper.book().read("pass"));
                        }
                    },1000);
                }
            }
        }
    }

    private void dangnhap(String email ,String pass) {
        compositeDisposable.add(apiBanHang.dangNhap(email,pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.isSuccess()){
                                isLogin = true;
                                Paper.book().write("islogin",isLogin);
                                Utils.user = userModel.getResult().get(0);
                                // luu lai thong tin nguoi dung
                                Paper.book().write("user",userModel.getResult().get(0));
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        },
                        throwable -> {
                            Toast.makeText(DangNhapActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Utils.user.getEmail() != null && Utils.user.getPass() != null){
            edtEmail_DN.setText(Utils.user.getEmail());
            edtPass_DN.setText(Utils.user.getPass());
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}