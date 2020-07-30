package project.final_year.opkomstadmin.adapter;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import project.final_year.opkomstadmin.R;
import project.final_year.opkomstadmin.model.Chat;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.messageVH> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private List<Chat> chats;
    String fuserId, sub_name;
    Dialog msgOptions, view_sent_image, openDocs;
    DatabaseReference databaseReference;

    public MessageAdapter(Context context, List<Chat> chats, String fuserId, String sub_name, DatabaseReference databaseReference){
        this.context = context;
        this.chats = chats;
        this.fuserId = fuserId;
        this.sub_name = sub_name;
        this.databaseReference = databaseReference;
    }

    @NonNull
    @Override
    public MessageAdapter.messageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            context = parent.getContext();
            return new MessageAdapter.messageVH(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            context = parent.getContext();
            return new MessageAdapter.messageVH(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.messageVH holder, int position) {
        final Chat chat = chats.get(position);
        holder.user.setText(chat.getUser());

        if(chat.getType().equals("text")){
            holder.message.setVisibility(View.VISIBLE);
            holder.time.setVisibility(View.VISIBLE);
            holder.image_msg.setVisibility(View.GONE);
            holder.time1.setVisibility(View.GONE);

            holder.message.setText(chat.getMessage());
            holder.message.setMovementMethod(LinkMovementMethod.getInstance());
            holder.time.setText(chat.getTime());

        } else if(chat.getType().equals("image")){

            holder.message.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
            holder.image_msg.setVisibility(View.VISIBLE);
            holder.time1.setVisibility(View.VISIBLE);

            Glide.with(context).load(chat.getMessage()).into(holder.image_msg);
            holder.time1.setText(chat.getTime());

        } else if(chat.getType().equals("files")){
            holder.message.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
            holder.image_msg.setVisibility(View.VISIBLE);
            holder.image_msg.getLayoutParams().height = 120;
            holder.image_msg.getLayoutParams().width = 120;
            String fileExt = MimeTypeMap.getFileExtensionFromUrl(chat.getMessage());
            if(fileExt.equals("pdf"))
                holder.image_msg.setBackgroundResource(R.drawable.pdf);
            else if(fileExt.equalsIgnoreCase("docx")||fileExt.equalsIgnoreCase("doc")||fileExt.equalsIgnoreCase("docm")||fileExt.equalsIgnoreCase("wps")||fileExt.equalsIgnoreCase("txt"))
                holder.image_msg.setBackgroundResource(R.drawable.word);
            else if(fileExt.equalsIgnoreCase("xls")||fileExt.equalsIgnoreCase("xlsx"))
                holder.image_msg.setBackgroundResource(R.drawable.excel);
            else if(fileExt.equalsIgnoreCase("ppt")||fileExt.equalsIgnoreCase("pptx"))
                holder.image_msg.setBackgroundResource(R.drawable.ppt);
            else if(fileExt.equalsIgnoreCase("jpg")||fileExt.equalsIgnoreCase("jpeg")||fileExt.equalsIgnoreCase("png"))
                holder.image_msg.setBackgroundResource(R.drawable.image);
            else
                holder.image_msg.setBackgroundResource(R.drawable.document);
            holder.time1.setVisibility(View.VISIBLE);
            holder.time1.setText(chat.getTime());
        }

        holder.message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longPressAction(chat);
                return true;
            }
        });

        holder.image_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(chat.getType().equals("image"))
                    thumbnailClickAction(chat);
                else if(chat.getType().equals("files")) {
                    /*String fileExt = MimeTypeMap.getFileExtensionFromUrl(chat.getMessage());
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setDataAndType(Uri.parse(chat.getMessage()), "application/"+fileExt);

                    Intent chooser = Intent.createChooser(browserIntent, "DOCUMENT");
                    chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // optional

                    context.startActivity(chooser);*/
                    final String fileExt = MimeTypeMap.getFileExtensionFromUrl(chat.getMessage());
                    Log.e("Extension", fileExt);
                    if (fileExt.equalsIgnoreCase("jpg") || fileExt.equalsIgnoreCase("jpeg")
                            || fileExt.equalsIgnoreCase("png")) {
                        thumbnailClickAction(chat);
                    } else {
                    openDocs = new Dialog(context, android.R.style.Theme_Light);
                    openDocs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    openDocs.setContentView(R.layout.web_view);
                    openDocs.show();

                    WebView webView = openDocs.findViewById(R.id.openDocs);
                    final ProgressDialog progDailog = ProgressDialog.show(context, "Loading..", "Please wait.. ", true);
                    progDailog.setCancelable(true);

                    webView.getSettings().setUseWideViewPort(true);
                    webView.getSettings().setLoadWithOverviewMode(true);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setBuiltInZoomControls(true);
                    webView.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            progDailog.show();
                            view.loadUrl(url);
                            return true;
                        }

                        @Override
                        public void onPageFinished(WebView view, final String url) {
                            progDailog.dismiss();
                        }
                    });
                    File file1 = new File("/storage/emulated/0/Android/data/project.final_year.opkomstadmin/files/Download/Chats/"+sub_name+"/"+ chat.getKey() + "." + fileExt);
                    if (file1.exists()) {
                        openDocs.dismiss();
                        progDailog.dismiss();
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        builder.detectFileUriExposure();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt);
                        if (fileExt.equalsIgnoreCase("") || mimetype == null) {
                            intent.setDataAndType(Uri.fromFile(file1), "text/*");
                        } else {
                            intent.setDataAndType(Uri.fromFile(file1), mimetype);
                        }
                        context.startActivity(Intent.createChooser(intent, "Choose an Application:"));
                    } else {
                        try {
                            webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + URLEncoder.encode(chat.getMessage(), "ISO-8859-1"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    ImageView back = openDocs.findViewById(R.id.back);
                    back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openDocs.dismiss();
                        }
                    });
                    TextView sender_name = openDocs.findViewById(R.id.sender_name);
                    sender_name.setText(chat.getUser());
                    TextView image_date = openDocs.findViewById(R.id.image_date);
                    image_date.setText(chat.getDate());
                    TextView image_time = openDocs.findViewById(R.id.image_time);
                    image_time.setText(chat.getTime());
                    ImageView download = openDocs.findViewById(R.id.download);
                    download.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
                            context.startActivity(intent);*/
                            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                            Uri uri = Uri.parse(chat.getMessage());
                            DownloadManager.Request request = new DownloadManager.Request(uri);
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalFilesDir(context, "Download/Chats/"+sub_name+"/", chat.getKey() + "." + fileExt);
                            downloadManager.enqueue(request);
                            //Toast.makeText(context, "Downloading File", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                }
            }
        });

        holder.image_msg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longPressAction(chat);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class messageVH extends RecyclerView.ViewHolder{

        TextView user, message, time, time1;
        ImageView image_msg;
        public messageVH(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.user);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
            time1 = itemView.findViewById(R.id.time1);
            image_msg = itemView.findViewById(R.id.image_msg);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(chats.get(position).getUserId().equals(fuserId)){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

    private void longPressAction(final Chat chat){
        msgOptions = new Dialog(context);
        msgOptions.setContentView(R.layout.chat_long_press);

        TextView copyText = msgOptions.findViewById(R.id.copyText);
        TextView unsendMessage = msgOptions.findViewById(R.id.unsendMessage);
        View line = msgOptions.findViewById(R.id.line);
        if(!chat.getUserId().equals(fuserId)){
            unsendMessage.setText("Delete Message");
        }
        else {
            unsendMessage.setText("Unsend Message");
        }

        if(chat.getType().equals("text")){
            copyText.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        } else if(chat.getType().equals("image")){
            copyText.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        } else if(chat.getType().equals("files")){
            copyText.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }

        copyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("CopyText", chat.getMessage());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
                msgOptions.dismiss();
            }
        });

        unsendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chat.getType().equals("text")){
                    databaseReference.child(chat.getKey()).setValue(null);
                } else if(chat.getType().equals("image")){
                    StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(chat.getMessage());
                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            databaseReference.child(chat.getKey()).setValue(null);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    });
                } else if(chat.getType().equals("files")){
                    StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(chat.getMessage());
                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            databaseReference.child(chat.getKey()).setValue(null);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    });
                }
                msgOptions.dismiss();
            }
        });

        msgOptions.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        msgOptions.show();
    }

    private void thumbnailClickAction(Chat chat){
        view_sent_image = new Dialog(context, android.R.style.Theme_Light);
        view_sent_image.requestWindowFeature(Window.FEATURE_NO_TITLE);
        view_sent_image.setContentView(R.layout.view_sent_image);
        view_sent_image.show();
                /*view_sent_image.setContentView(R.layout.view_sent_image);
                view_sent_image.show();
                Window window = view_sent_image.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);*/
        ImageView back = view_sent_image.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_sent_image.dismiss();
            }
        });
        TextView sender_name = view_sent_image.findViewById(R.id.sender_name);
        sender_name.setText(chat.getUser());
        TextView image_date = view_sent_image.findViewById(R.id.image_date);
        image_date.setText(chat.getDate());
        TextView image_time = view_sent_image.findViewById(R.id.image_time);
        image_time.setText(chat.getTime());
        PhotoView showImage = view_sent_image.findViewById(R.id.showImage);
        Glide.with(context).load(chat.getMessage()).into(showImage);
        String fileExt = MimeTypeMap.getFileExtensionFromUrl(chat.getMessage());
        ImageView download = view_sent_image.findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                            /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
                            context.startActivity(intent);*/
                File file1 = new File("/storage/emulated/0/Android/data/project.final_year.opkomstadmin/files/Download/Chats/"+sub_name+"/"+ chat.getKey() + "." + fileExt);
                if (file1.exists()) {
                    Toast.makeText(context, "File already exists at " +
                            "/storage/emulated/0/Android/data/project.final_year.opkomstadmin/files/Download/Chats/"+sub_name, Toast.LENGTH_LONG).show();
                } else {
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(chat.getMessage());
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalFilesDir(context, "Download/Chats/"+sub_name+"/", chat.getKey() + "." + fileExt);
                    downloadManager.enqueue(request);
                    //Toast.makeText(context, "Downloading File", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

// /storage/emulated/0/Android/data/project.final_year.opkomstadmin/files/Download/-M801GAFsZtrlTpiLjHn.pdf
/* if(fileExt.equalsIgnoreCase("pdf")){
                            Intent target = new Intent(Intent.ACTION_VIEW);
                            target.setDataAndType(Uri.fromFile(file1),"application/pdf");
                            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                            Intent intent = Intent.createChooser(target, "Open File");
                            try {
                                context.startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                // Instruct the user to install a PDF reader here, or something
                            }
                        }*/
