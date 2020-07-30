package project.final_year.opkomstadmin.Faculty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import project.final_year.opkomstadmin.R;
import project.final_year.opkomstadmin.adapter.AssignmentAdapter;
import project.final_year.opkomstadmin.model.Assignment_Url;

public class AssignmentActivity extends AppCompatActivity {

    public static String sub_name, assignmentNo, assignmentUrl, faculty_code;
    Button view_given_assignment;
    RecyclerView stu_assignment_details;
    ImageView back;
    ArrayList<Assignment_Url> assignment_url = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    Dialog openDocs;
    TextView noSubmissions;
    AssignmentAdapter assignmentAdapter;
    Switch windowStatus;
    String window = "NO STATUS";
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Assignment").child(sub_name).child(assignmentNo.toLowerCase());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        Window window1 = this.getWindow();
        window1.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window1.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window1.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        TextView assignment_name_title = toolbar.findViewById(R.id.chat_name_title);
        assignment_name_title.setText(assignmentNo);
        setSupportActionBar(toolbar);
        back = (ImageView)toolbar.findViewById(R.id.back);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssignmentActivity.super.onBackPressed();
            }
        });



        view_given_assignment = findViewById(R.id.view_given_assignment);
        view_given_assignment.setOnClickListener(v -> {
            if(!assignmentUrl.equals(""))
                showGivenAssignment();
            else
                Toast.makeText(this, "NO ASSIGNMENTS GIVEN", Toast.LENGTH_SHORT).show();
        });
        stu_assignment_details = findViewById(R.id.stu_assignment_details);
        noSubmissions = findViewById(R.id.noSubmissions);
        windowStatus = findViewById(R.id.windowStatus);

        setwindowStatus();

        fetchSubmissionList();

        windowStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
                assignmentWindowStatus("Open");
            else
                assignmentWindowStatus("Closed");
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchSubmissionList();
            new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 2000);
        });

    }

    private void setwindowStatus() {
        FirebaseDatabase.getInstance().getReference().child("Assignment").child(sub_name)
                .child(faculty_code).child(assignmentNo.toLowerCase()).child("window")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                window = dataSnapshot.getValue(String.class);
                if(window.equals("Open"))
                    windowStatus.setChecked(true);
                else if(window.equals("Closed"))
                    windowStatus.setChecked(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchSubmissionList() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    assignment_url.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String rollNo = snapshot.getKey();
                        String url = snapshot.getValue(String.class);
                        if(!url.equals(""))
                            assignment_url.add(new Assignment_Url(rollNo, url));
                    }
                    Log.e("SIZE LIST", assignment_url.size()+"");
                }
                if(assignment_url.size()>0)
                    initRecyclerView();
                else
                    noSubmissions.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void assignmentWindowStatus(String status) {
        FirebaseDatabase.getInstance().getReference().child("Assignment").child(sub_name)
                .child(faculty_code).child(assignmentNo.toLowerCase()).child("window").setValue(status)
                .addOnCompleteListener(task -> {
                    Toast.makeText(this, "Assignment Accepting "+status, Toast.LENGTH_SHORT).show();
        });
    }

    private void initRecyclerView() {
        noSubmissions.setVisibility(View.GONE);
        assignmentAdapter = new AssignmentAdapter(assignment_url, sub_name, assignmentNo);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL, false);
        stu_assignment_details.setLayoutManager(layoutManager);
        stu_assignment_details.setAdapter(assignmentAdapter);
        assignmentAdapter.notifyDataSetChanged();
    }

    private void showGivenAssignment() {
        final String fileExt = MimeTypeMap.getFileExtensionFromUrl(assignmentUrl);
        openDocs = new Dialog(this, android.R.style.Theme_Light);
        openDocs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDocs.setContentView(R.layout.web_view);
        openDocs.show();

        WebView webView = openDocs.findViewById(R.id.openDocs);
        final ProgressDialog progDailog = ProgressDialog.show(this, "Loading..", "Please wait.. ", true);
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

        File file1 = new File("/storage/emulated/0/Android/data/project.final_year.opkomstadmin/files/Download/Assignments/"+sub_name+"/"+assignmentNo + "." + fileExt);
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
            startActivity(Intent.createChooser(intent, "Choose an Application:"));
        } else {
            try {
                webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + URLEncoder.encode(assignmentUrl, "ISO-8859-1"));
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
        sender_name.setText(assignmentNo);
        TextView image_date = openDocs.findViewById(R.id.image_date);
        image_date.setVisibility(View.GONE);
        TextView image_time = openDocs.findViewById(R.id.image_time);
        image_time.setVisibility(View.GONE);
        ImageView download = openDocs.findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                            /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
                            context.startActivity(intent);*/
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(assignmentUrl);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(getApplicationContext(), "Download/Assignments/"+sub_name+"/", assignmentNo + "." + fileExt);
                downloadManager.enqueue(request);
                //Toast.makeText(context, "Downloading File", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
