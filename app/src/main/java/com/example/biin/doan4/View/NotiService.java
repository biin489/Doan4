package com.example.biin.doan4.View;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.biin.doan4.R;
import com.example.biin.doan4.model.PurchaseOrder;
import com.example.biin.doan4.model.User;
import com.example.biin.doan4.model.UserChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotiService extends Service {

    private DatabaseReference mData;
    private FirebaseAuth mAuth;
    int idint = 1;

    @Override
    public void onCreate() {
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String id = "my_channel_01";
        CharSequence name = "Ten";
        String description = "Mota";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mNotificationManager.createNotificationChannel(mChannel);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel_01");
        Notification notification = builder.setOngoing(true)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Secondhand Market đang chạy")
                .setCategory(Notification.CATEGORY_SERVICE)
                .setAutoCancel(true)
                .build();
        startForeground(idint, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        listenerForNoti();
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public NotiService() {

    }

    private void showNotiOrder(Intent intent, String title, String content) {
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        String id = "my_channel_01";
//        CharSequence name = "Ten";
//        String description = "Mota";
//        int importance = NotificationManager.IMPORTANCE_LOW;
//        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
//        mChannel.setDescription(description);
//        mChannel.enableLights(true);
//        mChannel.setLightColor(Color.RED);
//        mChannel.enableVibration(true);
//        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//        mNotificationManager.createNotificationChannel(mChannel);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel_01")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                // Set the intent that will fire when the user taps the notification
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(++idint, builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void listenerForNoti() {
        final Intent intent1 = new Intent(this, MngOrderActivity.class);
        if (mAuth.getCurrentUser() != null) {
            mData.child("Orders").orderByChild("po_buyerid").equalTo(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    PurchaseOrder order = dataSnapshot.getValue(PurchaseOrder.class);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    PurchaseOrder order = dataSnapshot.getValue(PurchaseOrder.class);
                    Log.d("Biin", order.getPo_title());
                    if (order.getPo_status() == 1) {
                        String content = "Đơn hàng " + order.getPo_title() + " đã được chuyển đi";
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        showNotiOrder(intent1, "Đơn hàng đang giao tới tay bạn!", content);
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            final Intent intent2 = new Intent(this, InboxActivity.class);
            mData.child("Chats").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    final String url = dataSnapshot.getKey();
                    mData.child("User").orderByChild("user_id").equalTo(url).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dn : dataSnapshot.getChildren()) {
                                final User user = dn.getValue(User.class);
                                mData.child("Chats").child(mAuth.getCurrentUser().getUid()).child(user.getUser_id()).limitToLast(1).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        UserChat chat = dataSnapshot.getValue(UserChat.class);
                                        if (user.getUser_fullname().equals("")) {
                                            if (chat.getIdReceive().equals(mAuth.getCurrentUser().getUid())) {
                                                showNotiOrder(intent2, "Bạn có tin nhắn mới", "Bạn có tin nhắn mới từ " + user.getUser_email());
                                            }
                                        } else {
                                            if (chat.getIdReceive().equals(mAuth.getCurrentUser().getUid())) {
                                                showNotiOrder(intent2, "Bạn có tin nhắn mới", "Bạn có tin nhắn mới từ " + user.getUser_fullname());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            final Intent intent3 = new Intent(this, MainActivity.class);
            mData.child("User").orderByKey().equalTo(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getUser_rule() == 0) {
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        showNotiOrder(intent3, "Bạn đã bị hạn chế quyền hạn", "Chúng tôi đã xem xét báo cáo từ những người dùng khác và quyết định hạn chế một số quyền hạn của bạn trong ứng dụng 1 thời gian ngắn");
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            final Intent intent4 = new Intent(this, SellProductActivity.class);
            mData.child("Orders").orderByChild("po_sellerid").equalTo(mAuth.getCurrentUser().getUid()).limitToLast(1).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    showNotiOrder(intent4, "Có người vừa đặt mua hàng", "Bạn vừa nhận được một đơn hàng mới, hãy kiểm tra ngay!");
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
