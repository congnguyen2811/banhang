package com.example.appbanhang.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appbanhang.Interface.ImageClickListener;
import com.example.appbanhang.R;
import com.example.appbanhang.Utils.Utils;
import com.example.appbanhang.model.EventBus.TinhTongEvent;
import com.example.appbanhang.model.GioHang;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

import io.paperdb.Paper;

public class GiohangAdapter extends RecyclerView.Adapter<GiohangAdapter.MyViewHolder> {
    Context context;
    List<GioHang> gioHangList;

    public GiohangAdapter(Context context, List<GioHang> gioHangList) {
        this.context = context;
        this.gioHangList = gioHangList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_giohang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GioHang gioHang = gioHangList.get(position);
        holder.txtTenSP.setText(gioHang.getTensp());
        holder.txtSoLuongSP_GH.setText(gioHang.getSoluong() + " ");
        Glide.with(context).load(gioHang.getHinhanh()).into(holder.imgGioHang);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
//        holder.txtGiaSP_GH.setText(decimalFormat.format(("Giá: "+gioHang.getGiasp())));
        holder.txtGiaSP_GH.setText(decimalFormat.format((gioHang.getGiasp())));
        long gia = gioHang.getSoluong() * gioHang.getGiasp();
        holder.txtTongTienSP_GH.setText(decimalFormat.format(gia));
        holder.item_check_GH.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Utils.gioHangList.get(holder.getAdapterPosition()).setChecked(true);
                    if(!Utils.muaHangList.contains(gioHang)){
                        Utils.muaHangList.add(gioHang);
                    }

                    EventBus.getDefault().postSticky(new TinhTongEvent());

                } else {
                    Utils.gioHangList.get(holder.getAdapterPosition()).setChecked(false);
                    for (int i = 0; i < Utils.muaHangList.size(); i++) {
                        if (Utils.muaHangList.get(i).getIdsp() == gioHang.getIdsp()) {
                            Utils.muaHangList.remove(i);
                            EventBus.getDefault().postSticky(new TinhTongEvent());
                        }
                    }
                }
            }
        });
        holder.item_check_GH.setChecked(gioHang.isChecked());

        holder.setImageClickListener(new ImageClickListener() {
            @Override
            public void onImageClick(View view, int pos, int giatri) {
                if (giatri == 1) {
                    if (gioHangList.get(pos).getSoluong() > 1) {
                        int soLuongMoi = gioHangList.get(pos).getSoluong() - 1;
                        gioHangList.get(pos).setSoluong(soLuongMoi);

                        holder.txtSoLuongSP_GH.setText(gioHangList.get(pos).getSoluong() + "");
                        long gia = gioHangList.get(pos).getSoluong() * gioHangList.get(pos).getGiasp();
                        holder.txtTongTienSP_GH.setText(decimalFormat.format(gia));
                        EventBus.getDefault().postSticky(new TinhTongEvent());
                    } else if (gioHangList.get(pos).getSoluong() == 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                        builder.setTitle("Thông báo");
                        builder.setMessage("Bạn có muốn xóa sản phẩm khỏi giỏ hàng");
                        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.muaHangList.remove(gioHang);
                                Utils.gioHangList.remove(pos);
                                Paper.book().write("giohang",Utils.gioHangList);
                                notifyDataSetChanged();
                                EventBus.getDefault().postSticky(new TinhTongEvent());
                            }
                        });
                        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();

                    }
                } else if (giatri == 2) {
                    if (gioHangList.get(pos).getSoluong() < gioHangList.get(pos).getSoluongtonkho()) {
                        int soLuongMoi = gioHangList.get(pos).getSoluong() + 1;
                        gioHangList.get(pos).setSoluong(soLuongMoi);

                    }
                    holder.txtSoLuongSP_GH.setText(gioHangList.get(pos).getSoluong() + "");
                    long gia = gioHangList.get(pos).getSoluong() * gioHangList.get(pos).getGiasp();
                    holder.txtTongTienSP_GH.setText(decimalFormat.format(gia));
                    EventBus.getDefault().postSticky(new TinhTongEvent());
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return gioHangList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtTenSP, txtGiaSP_GH, txtSoLuongSP_GH, txtTongTienSP_GH;
        ImageView imgGioHang, imgdauTru, imgdauCong, imgdauBang;
        ImageClickListener imageClickListener;
        CheckBox item_check_GH;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtGiaSP_GH = itemView.findViewById(R.id.txtGiaSP_GH);
            txtSoLuongSP_GH = itemView.findViewById(R.id.txtSoLuongSP_GH);
            txtTongTienSP_GH = itemView.findViewById(R.id.txtTongTienSP_GH);
            txtTenSP = itemView.findViewById(R.id.txtNameSP_GH);
            imgdauBang = itemView.findViewById(R.id.imgdaubang);
            imgdauCong = itemView.findViewById(R.id.imgdauCong);
            imgdauTru = itemView.findViewById(R.id.imgdauTru);
            imgGioHang = itemView.findViewById(R.id.imgGH);
            item_check_GH = itemView.findViewById(R.id.item_ckeck_GH);

            imgdauTru.setOnClickListener(this);
            imgdauCong.setOnClickListener(this);

        }

        public void setImageClickListener(ImageClickListener imageClickListener) {
            this.imageClickListener = imageClickListener;
        }

        @Override
        public void onClick(View v) {
            if (v == imgdauTru) {
                imageClickListener.onImageClick(v, getAdapterPosition(), 1);
            } else if (v == imgdauCong) {
                imageClickListener.onImageClick(v, getAdapterPosition(), 2);
            }
        }
    }
}
