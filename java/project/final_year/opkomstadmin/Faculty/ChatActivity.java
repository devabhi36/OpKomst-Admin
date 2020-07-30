package project.final_year.opkomstadmin.Faculty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fcm.androidtoandroid.FirebasePush;
import fcm.androidtoandroid.connection.PushNotificationTask;
import fcm.androidtoandroid.model.Notification;
import project.final_year.opkomstadmin.R;
import project.final_year.opkomstadmin.adapter.MessageAdapter;
import project.final_year.opkomstadmin.model.Chat;

public class ChatActivity extends AppCompatActivity {
    public static String sub_name, user, userId, sub_code;
    EditText msg_text;
    ImageButton attach, msg_send, camera;
    String msg ="", type="", key1;

    MessageAdapter messageAdapter;
    List<Chat> chats;
    RecyclerView msg_chat;
    SwipeRefreshLayout swipeRefreshLayout;
    DatabaseReference databaseReference, keyValue;

    //Dialog attachmentDialog;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("chats").child(sub_name);
    int PICK_FILE_REQUEST = 342, PICK_IMAGE_REQUEST = 234, CLICK_IMAGE_REQUEST = 423;
    private Uri filePath;
    StorageTask uploadTask;
    ProgressDialog progressDialog;
    private RequestQueue requestQueue;
    private  String url = "https://fcm.googleapis.com/fcm/send";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Log.e("SUBJECT CODE", sub_code);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        TextView chat_name_title = toolbar.findViewById(R.id.chat_name_title);
        chat_name_title.setText(sub_name);
        setSupportActionBar(toolbar);
        ImageView back = (ImageView)toolbar.findViewById(R.id.back);

        requestQueue = Volley.newRequestQueue(this);

        swipeRefreshLayout = findViewById(R.id.refreshLayoutChat);

        msg_chat = findViewById(R.id.msg_chat);
        msg_chat.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        msg_chat.setLayoutManager(linearLayoutManager);

        msg_text = findViewById(R.id.msg_text);
        msg_send = findViewById(R.id.msg_send);
        attach = findViewById(R.id.attachment);
        camera = findViewById(R.id.camera);

        /*attachmentDialog = new Dialog(this);
        Window window1 = attachmentDialog.getWindow();
        WindowManager.LayoutParams wlp = window1.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window1.setAttributes(wlp);*/

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Uploading");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.super.onBackPressed();
            }
        });


        msg_text.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                attach.setVisibility(View.VISIBLE);
                camera.setVisibility(View.VISIBLE);
                msg_send.setVisibility(View.GONE);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                attach.setVisibility(View.GONE);
                camera.setVisibility(View.GONE);
                msg_send.setVisibility(View.VISIBLE);
                // Here you can trigger your another code/function about which you are asking
            }
            public void afterTextChanged(Editable s) {
                if(msg_text.getText().toString().equals("")){
                    attach.setVisibility(View.VISIBLE);
                    camera.setVisibility(View.VISIBLE);
                    msg_send.setVisibility(View.GONE);
                }
            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickFile = new Intent(Intent.ACTION_GET_CONTENT);
                pickFile.setType("file/*");
                startActivityForResult(pickFile, PICK_FILE_REQUEST);
                Toast.makeText(ChatActivity.this, "Open Documents", Toast.LENGTH_SHORT).show();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(ChatActivity.this);
            }
        });

        msg_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg = msg_text.getText().toString();
                if(TextUtils.isEmpty(msg))
                Toast.makeText(ChatActivity.this, "Please type a message!", Toast.LENGTH_SHORT).show();
                else{
                    type="text";
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats").child(sub_name).push();
                    String key = databaseReference.getKey();
                    send_msg(user, sub_name, msg, userId, type, key);
                }
                msg_text.setText("");
            }
        });

        readMessages(sub_name, userId);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.red), getResources().getColor(R.color.blue),
                getResources().getColor(R.color.yellow), getResources().getColor(R.color.green));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                readMessages(sub_name, userId);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }
    /*private void attachment() {
        attachmentDialog.setContentView(R.layout.web_view);
        CircleImageView documents = attachmentDialog.findViewById(R.id.documents);
        CircleImageView gallery = attachmentDialog.findViewById(R.id.gallery);
        CircleImageView camera = attachmentDialog.findViewById(R.id.camera);

        documents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDocuments();
                attachmentDialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                attachmentDialog.dismiss();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
                attachmentDialog.dismiss();
            }
        });

        attachmentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        attachmentDialog.show();
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            progressDialog.show();
            filePath = data.getData();
            type = "files";
            String fileExt = String.valueOf(filePath).substring(String.valueOf(filePath).lastIndexOf("."));
            Log.e("Extention", fileExt);
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats").child(sub_name).push();
            final String key = databaseReference.getKey();
            final StorageReference reference = storageReference.child("/files/"+key+fileExt);
            uploadTask = reference.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        Toast.makeText(ChatActivity.this, "File Send", Toast.LENGTH_SHORT).show();
                        getDownloadUrl(uploadTask,reference, type, key);
                    }
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    //displaying percentage in progress dialog
                    progressDialog.setMessage("Uploading " + ((int) progress) + "%...");
                }
            });
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                progressDialog.show();
                Uri croppedImageUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), croppedImageUri);
                    type="image";
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats").child(sub_name).push();
                    final String key = databaseReference.getKey();
                    final StorageReference reference = storageReference.child("/images/"+key+".jpg");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                    byte[] bytes = baos.toByteArray();
                    uploadTask = reference.putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(ChatActivity.this, "Image Send", Toast.LENGTH_SHORT).show();
                                getDownloadUrl(uploadTask,reference, type, key);
                            }
                            else
                                Toast.makeText(ChatActivity.this, "Error: "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploading " + ((int) progress) + "%...");
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
                progressDialog.dismiss();
        }

    }

    private void getDownloadUrl(StorageTask uploadTask, final StorageReference reference, final String type, final String key) {
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    send_msg(user, sub_name, downloadUri.toString(), userId, type, key);
                } else if(task.isCanceled()){
                    Toast.makeText(ChatActivity.this, "Sending Failed!", Toast.LENGTH_SHORT).show();
                }
                else{

                }
            }
        });
    }

    private void send_msg(String user, String group_name, String message1, String userId, String type,  String key){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats").child(group_name).child(key);
        Date date1 = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        String time="";
        if(hours<10){
            if(minutes<10)
                time ="0"+hours+":"+"0"+minutes+" AM";
            else
                time ="0"+hours+":"+minutes+" AM";
        }
        else if(hours>9 && hours<13){
            if(minutes<10)
                time =hours+":"+"0"+minutes+" AM";
            else
                time =hours+":"+minutes+" AM";
        }
        else if(hours>12 && hours<22){
            hours = hours-12;
            if(minutes<10)
                time ="0"+hours+":"+"0"+minutes+" PM";
            else
                time ="0"+hours+":"+minutes+" PM";
        }
        else if(hours<21){
            hours = hours-12;
            if(minutes<10)
                time =hours+":"+"0"+minutes+" PM";
            else
                time =hours+":"+minutes+" PM";
        }

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        String date = "";
        int month1 = month+1;
        if(month1<10 && dayOfMonth<10){
            date = "0"+dayOfMonth+"-0"+month1+"-"+year;
        }
        else if(month1>9 && dayOfMonth>9){
            date = dayOfMonth+"-"+month1+"-"+year;
        }
        else if(month1>9 && dayOfMonth<10){
            date = "0"+dayOfMonth+"-"+month1+"-"+year;
        }
        else if(month1<10 && dayOfMonth>9){
            date = dayOfMonth+"-0"+month1+"-"+year;
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("user", user);
        hashMap.put("message", message1);
        hashMap.put("userId", userId);
        hashMap.put("date", date);
        hashMap.put("time", time);
        hashMap.put("type", type);
        hashMap.put("key", key);
        databaseReference.updateChildren(hashMap);
        readMessages(sub_name, userId);
        
        sendNotification(message1, type);

    }

    private void sendNotification(String msg,  String type) {
        JSONObject mainObj = new JSONObject();
        try {
            if(type.equals("files"))
                msg = "has sent a document!";
            else if(type.equals("image"))
                msg = "has sent an image!";
            mainObj.put("to", "/topics/"+sub_code);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", sub_name);
            notificationObj.put("body", user+": "+msg);

            JSONObject dataObj = new JSONObject();
            dataObj.put("sub_name", sub_name);
            dataObj.put("user", user);
            dataObj.put("userId", userId);
            mainObj.put("notification", notificationObj);
            mainObj.put("data", dataObj);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(ChatActivity.this, "Notification send", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ChatActivity.this, "Notification failed", Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAO90NXBM:APA91bESKQNwa2kYyw7PNtmMVRKEk2PcmwWofyYrIFXLZ_Q-skz6ViIk5l7KXUEaMUL0HcMOztxDvjmye2C317txGZb9ODxVbr00IFavE7Qi1wRwD5iXhueMGOFRlokBcg7T0h8i1HGh");
                    return header;
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void readMessages(String group_name, final String userId){
        chats = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats").child(group_name);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    chats.add(chat);

                    messageAdapter = new MessageAdapter(ChatActivity.this, chats, userId, sub_name, databaseReference);
                    msg_chat.setAdapter(messageAdapter);
                    messageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        msg = msg_text.getText().toString();
        if(!TextUtils.isEmpty(msg))
            new AlertDialog.Builder(this)
                    .setMessage("Typed message will be lost..")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        else{
            super.onBackPressed();
        }
    }
}
