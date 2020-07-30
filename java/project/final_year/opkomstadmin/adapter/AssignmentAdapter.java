package project.final_year.opkomstadmin.adapter;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import fcm.androidtoandroid.FirebasePush;
import fcm.androidtoandroid.connection.PushNotificationTask;
import fcm.androidtoandroid.model.Notification;
import project.final_year.opkomstadmin.Faculty.PlagiarismReport;
import project.final_year.opkomstadmin.R;
import project.final_year.opkomstadmin.model.Assignment_Url;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.StudentList> {
    Context context;
    ArrayList<Assignment_Url> assignment_url;
    Dialog openDocs;
    String sub_name, assignmentNo;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("StudentEnrollments");
    public AssignmentAdapter(ArrayList<Assignment_Url> assignment_url, String sub_name, String assignmentNo) {
        this.assignment_url = assignment_url;
        this.sub_name = sub_name;
        this.assignmentNo = assignmentNo;
    }

    @NonNull
    @Override
    public StudentList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assignment_list_student_layout, parent, false);
        context = parent.getContext();
        return new StudentList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentList holder, int position) {
        Assignment_Url assignment_url_object = assignment_url.get(position);
        holder.stu_roll.setText(assignment_url_object.getRollNo());
        String url = assignment_url_object.getAssignmentUrl();
        holder.clickView.setOnClickListener(v -> {
            if(!url.equals(""))
            showSubmittedAssignment(assignment_url_object.getRollNo(), url);
            else
                Toast.makeText(context, "NO ASSIGNMENT", Toast.LENGTH_SHORT).show();
        });
        databaseReference.child(assignment_url_object.getRollNo()).child(sub_name).child(assignmentNo.toLowerCase())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String status = dataSnapshot.getValue(String.class);
                        if(status.equals("Accepted")){
                            holder.assignmentStatus.setChecked(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        holder.assignmentStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                changeAssignmentStatus("Accepted", assignment_url_object.getRollNo());
                holder.rejectAssignment.setFocusable(false);
                holder.rejectAssignment.setClickable(false);
            } else {
                changeAssignmentStatus("Submitted", assignment_url_object.getRollNo());
                holder.rejectAssignment.setFocusable(true);
                holder.rejectAssignment.setClickable(true);
            }
        });

        holder.checkPlagiarism.setOnClickListener(v -> {
            if(assignment_url.size()==1){
                Toast.makeText(context, "No other files available to compare!", Toast.LENGTH_SHORT).show();
            }
            else {
                FirebaseDatabase.getInstance().getReference().child("Plagiarism Report").child(sub_name).child(assignmentNo.toLowerCase())
                        .child(assignment_url_object.getRollNo()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            int size = (int) dataSnapshot.getChildrenCount();
                            if(size==assignment_url.size()-1){
                                PlagiarismReport.sub_name = sub_name;
                                PlagiarismReport.assignmentNo = assignmentNo;
                                PlagiarismReport.roll_no = assignment_url_object.getRollNo();
                                context.startActivity(new Intent(context, PlagiarismReport.class));
                            }
                            else {
                                checkPlagiarism(assignment_url, position);
                            }
                        }
                        else {
                            checkPlagiarism(assignment_url, position);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        holder.rejectAssignment.setOnClickListener(v -> {
            DatabaseReference reference1 =  FirebaseDatabase.getInstance().getReference().child("Plagiarism Report").child(sub_name).child(assignmentNo.toLowerCase())
                    .child(assignment_url_object.getRollNo());
            reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        reference1.setValue(null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Assignment").child(sub_name);
            DatabaseReference reference2 = reference.child(assignmentNo.toLowerCase()).child(assignment_url_object.getRollNo());
            deleteAssignment(assignment_url_object.getAssignmentUrl(), reference2, assignment_url_object.getRollNo());

        });
    }

    @Override
    public int getItemCount() {
        return assignment_url.size();
    }

    public class StudentList extends RecyclerView.ViewHolder {

        TextView stu_roll, clickView;
        Switch assignmentStatus;
        Button checkPlagiarism, rejectAssignment;
        public StudentList(@NonNull View itemView) {
            super(itemView);
            stu_roll = itemView.findViewById(R.id.stu_roll);
            clickView = itemView.findViewById(R.id.clickView);
            assignmentStatus = itemView.findViewById(R.id.assignmentStatus);
            checkPlagiarism = itemView.findViewById(R.id.checkPlagiarism);
            rejectAssignment = itemView.findViewById(R.id.rejectAssignment);
        }
    }

    private void showSubmittedAssignment(String rollNo, String url) {
        final String fileExt = MimeTypeMap.getFileExtensionFromUrl(url);
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

        File file1 = new File("/storage/emulated/0/Android/data/project.final_year.opkomstadmin/files/Download/Assignments/"+sub_name+"/"+assignmentNo+"/"+rollNo+ "." + fileExt);
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
                webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + URLEncoder.encode(url, "ISO-8859-1"));
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
        sender_name.setText(rollNo);
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
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(url);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(context, "Download/Assignments/"+sub_name+"/"+assignmentNo+"/", rollNo + "." + fileExt);
                downloadManager.enqueue(request);
            }
        });
    }

    private void changeAssignmentStatus(String status, String rollNo) {
        databaseReference.child(rollNo).child(sub_name).child(assignmentNo.toLowerCase()).setValue(status);
    }

    private void checkPlagiarism(ArrayList<Assignment_Url> assignment_url, int position) {
        ArrayList<String> roll_nos = new ArrayList<>();
        ArrayList<String> file_exts = new ArrayList<>();
        for(int i=0; i<assignment_url.size(); i++){
            String roll_no = assignment_url.get(i).getRollNo();
            roll_nos.add(roll_no);
            String url = assignment_url.get(i).getAssignmentUrl();
            String fileExt = MimeTypeMap.getFileExtensionFromUrl(url);
            file_exts.add(fileExt);
            File file1 = new File("/storage/emulated/0/Android/data/project.final_year.opkomstadmin/files/Download/Assignments/"+sub_name+"/"+assignmentNo+"/"+roll_no+ "." + fileExt);
            if(file1.exists()){
                if(i==assignment_url.size()-1){
                    Log.e("Send to",  "compareFiles");
                    compareFiles(assignment_url, position, roll_nos, file_exts);
                    break;
                }
                continue;
            }
            else {
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(url);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(context, "Download/Assignments/"+sub_name+"/"+assignmentNo+"/", roll_no + "." + fileExt);
                downloadManager.enqueue(request);
                if(i==assignment_url.size()-1){
                    Log.e("Send to",  "compareFiles");
                    compareFiles(assignment_url, position, roll_nos, file_exts);
                    break;
                }
            }
        }
    }

    private void compareFiles(ArrayList<Assignment_Url> assignment_url, int position, ArrayList<String> roll_nos, ArrayList<String> file_exts) {
        Log.e("Reached to",  "compareFiles");
        String current_data="";
        try {
            File current_file = new File("/storage/emulated/0/Android/data/project.final_year.opkomstadmin/files/Download/Assignments/"+sub_name+"/"+assignmentNo+"/"+roll_nos.get(position)+ "." + file_exts.get(position));
            FileInputStream fis = new FileInputStream(current_file.getAbsolutePath());
            XWPFDocument document = new XWPFDocument(fis);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for(int i=0;i<paragraphs.size();i++){
                current_data = current_data+paragraphs.get(i).getParagraphText().trim()+" ";
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(roll_nos.get(position), e.getMessage());
        }

        for(int i = 0; i<assignment_url.size(); i++){
            if(i==position)
                continue;
            else {
                try {
                    String data="";
                    File other_file = new File("/storage/emulated/0/Android/data/project.final_year.opkomstadmin/files/Download/Assignments/"+sub_name+"/"+assignmentNo+"/"+roll_nos.get(i)+ "." + file_exts.get(i));
                    FileInputStream fis = new FileInputStream(other_file.getAbsolutePath());
                    XWPFDocument document = new XWPFDocument(fis);
                    List<XWPFParagraph> paragraphs = document.getParagraphs();
                    for(int j=0;j<paragraphs.size();j++){
                        data=data+paragraphs.get(j).getParagraphText().trim()+" ";
                    }
                    fis.close();
                    Log.e("Send to",  "checkSimilarity");

                    SimilarityStrategy strategy = new JaroWinklerStrategy();
                    StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
                    double match = service.score(current_data, data);

                    if(current_data.contains(data) || data.contains(current_data))
                        match = 1.0;

                    DecimalFormat df = new DecimalFormat("#.####");
                    match = Double.parseDouble(df.format(match));

                    FirebaseDatabase.getInstance().getReference().child("Plagiarism Report").child(sub_name).child(assignmentNo.toLowerCase())
                            .child(roll_nos.get(position)).child(roll_nos.get(i)).setValue(match*100).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            PlagiarismReport.sub_name = sub_name;
                            PlagiarismReport.assignmentNo = assignmentNo;
                            PlagiarismReport.roll_no = roll_nos.get(position);
                            context.startActivity(new Intent(context, PlagiarismReport.class));
                        }
                    });

                    Log.e(roll_nos.get(position) + " and " + roll_nos.get(i) + " has ", match*100+"% match.");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(roll_nos.get(i), e.getMessage());
                }
            }
        }
    }

    private void deleteAssignment(String uploadedAssignmentUrl, DatabaseReference reference, String roll_no) {
        StorageReference assignmentref = FirebaseStorage.getInstance().getReferenceFromUrl(uploadedAssignmentUrl);
        assignmentref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                reference.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseDatabase.getInstance().getReference().child("StudentEnrollments").child(roll_no).child(sub_name)
                                .child(assignmentNo.toLowerCase()).setValue("Pending");

                        FirebaseDatabase.getInstance().getReference().child("Students").child(roll_no).child("token")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String token = dataSnapshot.getValue(String.class);

                                        Notification notification = new Notification();
                                        notification.setTitle(sub_name);
                                        notification.setBody("Your assignment no "+assignmentNo+" is rejected.");
                                        notification.setClickAction("");

                                        FirebasePush firebasePush = new FirebasePush("AAAAO90NXBM:APA91bESKQNwa2kYyw7PNtmMVRKEk2PcmwWofyYrIFXLZ_Q-skz6ViIk5l7KXUEaMUL0HcMOztxDvjmye2C317txGZb9ODxVbr00IFavE7Qi1wRwD5iXhueMGOFRlokBcg7T0h8i1HGh");
                                        firebasePush.setAsyncResponse(new PushNotificationTask.AsyncResponse() {
                                            @Override
                                            public void onFinishPush(@NotNull String ouput) {
                                                Log.e("OUTPUT", ouput);
                                                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                                notificationManager.cancelAll();
                                            }
                                        });
                                        firebasePush.setNotification(notification);
                                        firebasePush.sendToToken(token);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }
}
