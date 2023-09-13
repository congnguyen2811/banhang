package com.example.appbanhang.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appbanhang.Interface.ItemClickListener;
import com.example.appbanhang.R;
import com.example.appbanhang.activity.ChitietActivity;
import com.example.appbanhang.model.SanPhamMoi;

import java.text.DecimalFormat;
import java.util.List;

public class DienThoaiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<SanPhamMoi> array;
    private static final int VIEW_TYPE_DATA = 0 ;
    private static final int VIEW_TYPE_LOADING = 1 ;

    public DienThoaiAdapter(Context context, List<SanPhamMoi> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_DATA){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_dienthoai,parent,false);
            return new MyViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_loading,parent,false);
            return new loadingView(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder){
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            SanPhamMoi sanPhamMoi = array.get(position);
            myViewHolder.txtNameDT.setText(sanPhamMoi.getTensp());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            myViewHolder.txtPriceDT.setText("Giá:" +decimalFormat.format(Double.parseDouble(sanPhamMoi.getGiasp()))+" Đ");
            Glide.with(context).load(sanPhamMoi.getHinhanh()).into(myViewHolder.imgDT);
            myViewHolder.txtMoTaDT.setText(sanPhamMoi.getMota());
            myViewHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int pos, boolean isLongClick) {
                    if(!isLongClick){
                        Intent intent = new Intent(context, ChitietActivity.class);
                        intent.putExtra("chitiet",sanPhamMoi);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });
        }
        else {
            loadingView loadingV = (loadingView) holder;
            loadingV.progressBar.setIndeterminate(true);

        }
    }

    @Override
    public int getItemViewType(int position) {
        return array.get(position)  == null ? VIEW_TYPE_LOADING:VIEW_TYPE_DATA;
    }

    @Override
    public int getItemCount() {
        if(array != null){
            return array.size();
        }
        return 0;
    }
    public class loadingView extends RecyclerView.ViewHolder{
        ProgressBar progressBar;
        public loadingView(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progessbar);
        }
    }


    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtNameDT,txtPriceDT,txtMoTaDT;
        ImageView imgDT;
        private ItemClickListener itemClickListener;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPriceDT = itemView.findViewById(R.id.txtPriceDT);
            txtNameDT = itemView.findViewById(R.id.txtNameDT);
            imgDT = itemView.findViewById(R.id.imgDT);
            txtMoTaDT = itemView.findViewById(R.id.txtMotaDT);
            itemView.setOnClickListener(this);

        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),false);

        }
    }
}
