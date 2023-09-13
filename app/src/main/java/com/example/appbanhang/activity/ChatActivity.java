package com.example.appbanhang.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.appbanhang.R;
import com.example.appbanhang.Utils.Utils;
import com.example.appbanhang.adapter.ChatAdapter;
import com.example.appbanhang.model.ChatMessage;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    RecyclerView rcvChat;
    ImageView imgChat;
    EditText edtChat;
    FirebaseFirestore db;
    ChatAdapter adapter;
    List<ChatMessage> chatMessageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initView();
        initControl();
        listenMess();
        insertUser();
    }

    private void insertUser() {
        HashMap<String,Object> user = new HashMap<>();
        user.put("id",Utils.user.getId());
        user.put("username",Utils.user.getUsername());
        db.collection("users").document(String.valueOf(Utils.user.getId())).set(user);
    }

    private void initControl() {
        imgChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMesstoFire();
            }
        });
        edtChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkKeyBoard();
            }
        });
    }

    private void checkKeyBoard() {
            final View activityRootview = findViewById(R.id.activityRoot);
            activityRootview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect rect = new Rect();
                    activityRootview.getWindowVisibleDisplayFrame(rect);
                    int heightDiff = activityRootview.getRootView().getHeight() - rect.height();
                    if(heightDiff > 0.25*activityRootview.getRootView().getHeight()){
                        if(chatMessageList.size() > 0){
                            rcvChat.scrollToPosition(chatMessageList.size() -1 );
                            activityRootview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                }
            });
    }

    private void sendMesstoFire() {
        String strMess = edtChat.getText().toString().trim();
        if(TextUtils.isEmpty(strMess)){
            Toast.makeText(this, "Vui lòng nhập nội dung chat", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String,Object> message = new HashMap<>();
            message.put(Utils.SENDID,String.valueOf(Utils.user.getId()));
            message.put(Utils.RECEIVEDID,Utils.ID_RECEIVED);
            message.put(Utils.MESS,strMess);
            message.put(Utils.DATETIME,new Date());
            db.collection(Utils.PATH_CHAT).add(message);
            edtChat.setText("");
        }
    }
    private void listenMess(){
        db.collection(Utils.PATH_CHAT)
                .whereEqualTo(Utils.SENDID,String.valueOf(Utils.user.getId()))
                .whereEqualTo(Utils.RECEIVEDID,Utils.ID_RECEIVED)
                .addSnapshotListener(eventListener);
        db.collection(Utils.PATH_CHAT)
                .whereEqualTo(Utils.SENDID,Utils.ID_RECEIVED)
                .whereEqualTo(Utils.RECEIVEDID,String.valueOf(Utils.user.getId()))
                .addSnapshotListener(eventListener);
    }
    private final EventListener<QuerySnapshot> eventListener  =((value, error) -> {
        if(error != null){
            return;
        }
        if(value != null){
            int count = chatMessageList.size();
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.sendid = documentChange.getDocument().getString(Utils.SENDID);
                    chatMessage.receivedid = documentChange.getDocument().getString(Utils.RECEIVEDID);
                    chatMessage.mess = documentChange.getDocument().getString(Utils.MESS);
                    chatMessage.dateObj = documentChange.getDocument().getDate(Utils.DATETIME);
                    chatMessage.datetime = format_date(documentChange.getDocument().getDate(Utils.DATETIME));
                    chatMessageList.add(chatMessage);

                }
            }
            Collections.sort(chatMessageList,(obj1,obj2) -> obj1.dateObj.compareTo(obj2.dateObj) );
            if(count == 0){
                adapter.notifyDataSetChanged();
            }
            else {
                adapter.notifyItemRangeInserted(chatMessageList.size(),chatMessageList.size());
                rcvChat.smoothScrollToPosition(chatMessageList.size()-1);
            }
        }
    });
    private String format_date(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy- hh:mm a", Locale.getDefault()).format(date);
    }
    private void initView() {
        chatMessageList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        rcvChat = findViewById(R.id.rcvChat);
        imgChat = findViewById(R.id.imgChat);
        edtChat = findViewById(R.id.edtChat);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcvChat.setLayoutManager(layoutManager);
        rcvChat.setHasFixedSize(true);
        adapter = new ChatAdapter(getApplicationContext(),chatMessageList,String.valueOf(Utils.user.getId()));
        rcvChat.setAdapter(adapter);
    }
}