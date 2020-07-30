package project.final_year.opkomstadmin.adapter;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import project.final_year.opkomstadmin.R;
import project.final_year.opkomstadmin.model.Notice;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.noticesVH> {

    ArrayList<Notice> notices;
    Context context;
    Dialog view_sent_image, openDocs;
    public NoticeAdapter(ArrayList<Notice> notices){
        this.notices = notices;
    }

    @NonNull
    @Override
    public noticesVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_layout, parent, false);
        context = parent.getContext();
        return new noticesVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull noticesVH holder, int position) {
        Notice notice = notices.get(position);
        holder.noticeName.setText(notice.getName());
        holder.noticeDateTime.setText(notice.getDate()+",  "+notice.getTime());
        String url = notice.getUrl();
        String fileExt = MimeTypeMap.getFileExtensionFromUrl(url);
        if(fileExt.equals("pdf"))
            holder.fileIcon.setBackgroundResource(R.drawable.pdf);
        else if(fileExt.equalsIgnoreCase("docx")||fileExt.equalsIgnoreCase("doc")||fileExt.equalsIgnoreCase("docm")||fileExt.equalsIgnoreCase("wps")||fileExt.equalsIgnoreCase("txt"))
            holder.fileIcon.setBackgroundResource(R.drawable.word);
        else if(fileExt.equalsIgnoreCase("xls")||fileExt.equalsIgnoreCase("xlsx"))
            holder.fileIcon.setBackgroundResource(R.drawable.excel);
        else if(fileExt.equalsIgnoreCase("ppt")||fileExt.equalsIgnoreCase("pptx"))
            holder.fileIcon.setBackgroundResource(R.drawable.ppt);
        else if(fileExt.equalsIgnoreCase("jpg")||fileExt.equalsIgnoreCase("jpeg")||fileExt.equalsIgnoreCase("png"))
            holder.fileIcon.setBackgroundResource(R.drawable.image);
        else
            holder.fileIcon.setBackgroundResource(R.drawable.document);

        holder.cardLayout.setOnClickListener(v -> {
            if(fileExt.equalsIgnoreCase("jpg")||fileExt.equalsIgnoreCase("jpeg")||fileExt.equalsIgnoreCase("png"))
                openImage(notice.getName(), notice.getDate(), notice.getTime(), url, fileExt);
            else
                openDoc(notice.getName(), notice.getDate(), notice.getTime(), url, fileExt);
        });

        holder.noticeName.setOnClickListener(v -> {
            if(fileExt.equalsIgnoreCase("jpg")||fileExt.equalsIgnoreCase("jpeg")||fileExt.equalsIgnoreCase("png"))
                openImage(notice.getName(), notice.getDate(), notice.getTime(), url, fileExt);
            else
                openDoc(notice.getName(), notice.getDate(), notice.getTime(), url, fileExt);
        });

        holder.fileIcon.setOnClickListener(v -> {
            if(fileExt.equalsIgnoreCase("jpg")||fileExt.equalsIgnoreCase("jpeg")||fileExt.equalsIgnoreCase("png"))
                openImage(notice.getName(), notice.getDate(), notice.getTime(), url, fileExt);
            else
                openDoc(notice.getName(), notice.getDate(), notice.getTime(), url, fileExt);
        });
    }

    @Override
    public int getItemCount() {
        return notices.size();
    }

    public class noticesVH extends RecyclerView.ViewHolder {
        CardView cardLayout;
        ImageView fileIcon;
        TextView noticeName, noticeDateTime;
        public noticesVH(@NonNull View itemView) {
            super(itemView);
            cardLayout = itemView.findViewById(R.id.cardLayout);
            fileIcon = itemView.findViewById(R.id.fileIcon);
            noticeName = itemView.findViewById(R.id.noticeName);
            noticeDateTime = itemView.findViewById(R.id.noticeDateTime);
        }
    }

    private void openImage(String name, String date, String time, String url, String fileExt) {
        view_sent_image = new Dialog(context, android.R.style.Theme_Light);
        view_sent_image.requestWindowFeature(Window.FEATURE_NO_TITLE);
        view_sent_image.setContentView(R.layout.view_sent_image);
        view_sent_image.show();

        ImageView back = view_sent_image.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_sent_image.dismiss();
            }
        });

        TextView sender_name = view_sent_image.findViewById(R.id.sender_name);
        sender_name.setText(name);
        TextView image_date = view_sent_image.findViewById(R.id.image_date);
        image_date.setText(date);
        TextView image_time = view_sent_image.findViewById(R.id.image_time);
        image_time.setText(time);
        PhotoView showImage = view_sent_image.findViewById(R.id.showImage);
        Glide.with(context).load(url).into(showImage);
        ImageView download = view_sent_image.findViewById(R.id.download);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(url);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalFilesDir(context, "Download/Notices/", name + "." + fileExt);
                    downloadManager.enqueue(request);
            }
        });
    }

    private void openDoc(String name, String date, String time, String url, String fileExt) {
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
            try {
                webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + URLEncoder.encode(url, "ISO-8859-1"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        ImageView back = openDocs.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDocs.dismiss();
            }
        });
        TextView sender_name = openDocs.findViewById(R.id.sender_name);
        sender_name.setText(name);
        TextView image_date = openDocs.findViewById(R.id.image_date);
        image_date.setText(date);
        TextView image_time = openDocs.findViewById(R.id.image_time);
        image_time.setText(time);
        ImageView download = openDocs.findViewById(R.id.download);
        download.setOnClickListener(v -> {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalFilesDir(context, "Download/Notices/", name + "." + fileExt);
            downloadManager.enqueue(request);
        });
    }
}
