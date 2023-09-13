package com.example.appbanhang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbanhang.R;
import com.example.appbanhang.model.ChatMessage;
import com.google.android.play.core.integrity.i;

import java.util.List;

import io.paperdb.Paper;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ChatMessage> chatMessageList;
    private String sendid;
    private static final int TYPE_SEND = 1;
    private static final int TYPE_RECEIVED = 2;

    public ChatAdapter(Context context, List<ChatMessage> chatMessageList, String sendid) {
        this.context = context;
        this.chatMessageList = chatMessageList;
        this.sendid = sendid;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == TYPE_SEND){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_send_mess,parent,false);
            return new SendViewHolder(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_received,parent,false);
            return new RecivedViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == TYPE_SEND){
            ((SendViewHolder) holder).item_TxtMessSend_Chat.setText(chatMessageList.get(position).mess);
            ((SendViewHolder) holder).item_TxtTime_Chat.setText(chatMessageList.get(position).datetime);
        }
        else {
            ((RecivedViewHolder) holder).item_TxtMessReceived_Chat.setText(chatMessageList.get(position).mess);
            ((RecivedViewHolder) holder).item_TxtTimeReceived_Chat.setText(chatMessageList.get(position).datetime);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessageList.get(position).sendid.equals(sendid)){
            return TYPE_SEND;
        }
        else {
            return TYPE_RECEIVED;
        }
    }

    public class SendViewHolder extends RecyclerView.ViewHolder{
        TextView item_TxtMessSend_Chat,item_TxtTime_Chat;
        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            item_TxtMessSend_Chat = itemView.findViewById(R.id.item_txtMessSend_Chat);
            item_TxtTime_Chat = itemView.findViewById(R.id.item_txtTime_Chat);
        }
    }
    public class RecivedViewHolder extends RecyclerView.ViewHolder{
        TextView item_TxtMessReceived_Chat,item_TxtTimeReceived_Chat;
        public RecivedViewHolder(@NonNull View itemView) {
            super(itemView);
            item_TxtMessReceived_Chat = itemView.findViewById(R.id.item_txtMessReceived);
            item_TxtTimeReceived_Chat = itemView.findViewById(R.id.item_TxtTimeReceived);
        }
    }
}
