package com.example.appbanhang.retrofit;

import com.example.appbanhang.model.DonHangModel;
import com.example.appbanhang.model.KhuyenMaiModel;
import com.example.appbanhang.model.LoaiSPModel;
import com.example.appbanhang.model.MessageModel;
import com.example.appbanhang.model.SanPhamMoiModel;
import com.example.appbanhang.model.User;
import com.example.appbanhang.model.UserModel;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiBanHang {
    @GET("getloaiSP.php")
    Observable<LoaiSPModel> getLoaiSP();
    @GET("getspmoi.php")
    Observable<SanPhamMoiModel> getSPMoi();

    @POST("chitiet.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> getDienThoai(
            @Field("page") int page,
            @Field("loai") int loai
    );

    @POST("dangki.php")
    @FormUrlEncoded
    Observable<UserModel> dangKi(
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("username") String username,
            @Field("mobile") String mobile,
            @Field("uid") String uid
    );

    @POST("dangnhap.php")
    @FormUrlEncoded
    Observable<UserModel> dangNhap(
            @Field("email") String email,
            @Field("pass") String pass
    );

    @POST("reset.php")
    @FormUrlEncoded
    Observable<UserModel> resetPass(
            @Field("email") String email
    );

    @POST("donhang.php")
    @FormUrlEncoded
    Observable<MessageModel> creatOder(
            @Field("email") String email,
            @Field("sdt") String sdt,
            @Field("tongtien") String tongtien,
            @Field("iduser") int iduser,
            @Field("diachi") String diachi,
            @Field("soluong") int soluong,
            @Field("chitiet") String chitiet
    );

    @POST("xemdonhang.php")
    @FormUrlEncoded
    Observable<DonHangModel> xemDonHang(
            @Field("iduser") int id
    );

    @POST("timkiem.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> searchProducts(
            @Field("search") String search
    );
    @POST("updatetoken.php")
    @FormUrlEncoded
    Observable<MessageModel> updateToken(
            @Field("id") int id,
            @Field("token") String token
    );
    @POST("updatemomo.php")
    @FormUrlEncoded
    Observable<MessageModel> updateMoMo(
            @Field("id") int id,
            @Field("token") String token
    );
    @POST("gettoken.php")
    @FormUrlEncoded
    Observable<UserModel> getToken(
            @Field("status") int status
    );
    @GET("khuyenmai.php")
    Observable<KhuyenMaiModel> getKhuyenmai();

    @POST("deleteorder.php")
    @FormUrlEncoded
    Observable<MessageModel> deleteorder(
            @Field("iddonhang") int id
    );
}
