package project.final_year.opkomstadmin.Faculty;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import project.final_year.opkomstadmin.R;
import project.final_year.opkomstadmin.adapter.NoticeAdapter;
import project.final_year.opkomstadmin.model.Notice;

import static android.app.Activity.RESULT_OK;

public class NoticeFragment extends Fragment {

    public static String deptName, isHOD;
    Button addNotice;
    RecyclerView noticeList;
    SwipeRefreshLayout refreshLayout;
    ProgressDialog progressDialog;
    private Uri filePath;
    StorageTask uploadTask;
    Dialog input_dialog;
    ArrayList<Notice> notices = new ArrayList<>();
    NoticeAdapter noticeAdapter;
    String noticeName;
    int PICK_FILE_REQUEST = 342;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("notices").child(deptName);
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Notices").child(deptName);

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        addNotice = view.findViewById(R.id.addNotice);
        noticeList = view.findViewById(R.id.noticeList);
        noticeList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        noticeList.setLayoutManager(linearLayoutManager);
        refreshLayout = view.findViewById(R.id.refreshLayout);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getSupportFragmentManager().popBackStack("HomeFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Faculty_Home.flag = 0;
        HomeFragment.flag = 1;

        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.red), getResources().getColor(R.color.blue),
                getResources().getColor(R.color.green), getResources().getColor(R.color.yellow));

        getData();

        refreshLayout.setOnRefreshListener(() ->{
            getData();
            new Handler().postDelayed(() -> refreshLayout.setRefreshing(false), 2000);
        });

        Log.e("IS HOD", isHOD);

        if(!isHOD.equals("YES"))
            addNotice.setVisibility(View.GONE);

        addNotice.setOnClickListener(v -> {
            input_dialog = new Dialog(getContext());
            input_dialog.setContentView(R.layout.input_dialog);
            input_dialog.show();
            TextView inputDialogTitle = input_dialog.findViewById(R.id.inputDialogTitle);
            inputDialogTitle.setText("Enter name of the Notice...");
            EditText inputNoticeName = input_dialog.findViewById(R.id.inputNoticeName);
            Button enterName = input_dialog.findViewById(R.id.enterName);
            enterName.setOnClickListener(v1 -> {
                noticeName = inputNoticeName.getText().toString();
                if(TextUtils.isEmpty(noticeName))
                    Toast.makeText(getContext(), "Enter the notice name first!", Toast.LENGTH_SHORT).show();
                else {
                    input_dialog.dismiss();
                    Intent pickFile = new Intent(Intent.ACTION_GET_CONTENT);
                    pickFile.setType("file/*");
                    startActivityForResult(pickFile, PICK_FILE_REQUEST);
                }
            });
        });
    }

    private void getData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    notices.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Notice notice = snapshot.getValue(Notice.class);
                        notices.add(notice);

                        noticeAdapter = new NoticeAdapter(notices);
                        noticeList.setAdapter(noticeAdapter);
                        noticeAdapter.notifyDataSetChanged();
                    }
                    progressDialog.dismiss();
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "No Notice Found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            progressDialog.show();
            filePath = data.getData();
            String fileExt = String.valueOf(filePath).substring(String.valueOf(filePath).lastIndexOf("."));
            Log.e("Extention", fileExt);
            DatabaseReference databaseReference1 = databaseReference.push();
            final String key = databaseReference1.getKey();
            final StorageReference reference = storageReference.child(key+fileExt);
            uploadTask = reference.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "File Send", Toast.LENGTH_SHORT).show();
                        getDownloadUrl(uploadTask,reference, key);
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
    }

    private void getDownloadUrl(StorageTask uploadTask, StorageReference reference, String key) {
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
                    send_msg(downloadUri.toString(), key);
                } else if(task.isCanceled()){
                    Toast.makeText(getContext(), "Sending Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void send_msg(String url, String key) {
        Date date1 = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        String time="nil";
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
        hashMap.put("name", noticeName);
        hashMap.put("date", date);
        hashMap.put("time", time);
        hashMap.put("url", url);
        databaseReference.child(key).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(), noticeName+" successfully sent!", Toast.LENGTH_SHORT).show();
                    getData();
                }
            }
        });
    }
}
