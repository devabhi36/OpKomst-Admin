package project.final_year.opkomstadmin.Faculty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;

import project.final_year.opkomstadmin.R;

import static android.app.Activity.RESULT_OK;

public class ViewGivenAssignments extends Fragment {
    public static String sub_name, faculty_code, deptName;
    ListView listAssignments;
    ArrayAdapter arrayAdapter;
    ArrayList<String> assignments,  assignmentUrl;
    Button add_assignment;
    int count = 0, flag, count1;
    ProgressDialog progressDialog, progressDialog1;
    TextView title, noStudents;
    SwipeRefreshLayout swipeRefreshLayout;
    int PICK_FILE_REQUEST = 342;
    private Uri filePath;
    StorageTask uploadTask;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("assignments").child(sub_name);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_given_assignments, container, false);
        title = view.findViewById(R.id.title);
        noStudents = view.findViewById(R.id.noStudents);
        listAssignments = view.findViewById(R.id.listAssignments);
        add_assignment = view.findViewById(R.id.add_assignment);
        swipeRefreshLayout = view.findViewById(R.id.refreshLayout);
        title.setText(deptName);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        assignments = new ArrayList<>();
        assignmentUrl = new ArrayList<>();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait..\nData is being fetched!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.red), getResources().getColor(R.color.blue),
                getResources().getColor(R.color.green), getResources().getColor(R.color.yellow));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            getData();
            new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 2000);
        });

        add_assignment.setOnClickListener(v -> {
            addAssignment();
        });
    }

    private void getData() {
        count=0;
        databaseReference.child("SubjectEnrollments").child(sub_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                if(dataSnapshot1.exists()){
                    noStudents.setVisibility(View.GONE);
                    flag = 0;
                    DatabaseReference reference = databaseReference.child("Assignment").child(sub_name).child(faculty_code);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                assignments.clear();
                                assignmentUrl.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    count++;
                                    Log.e("Key", snapshot.getKey());
                                    assignments.add(snapshot.getKey().toUpperCase());
                                    reference.child(snapshot.getKey()).child("url").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                            assignmentUrl.add(dataSnapshot1.getValue(String.class));
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    flag = 1;
                    noStudents.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                    add_assignment.setClickable(false);
                    add_assignment.setFocusable(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(flag==0){
            new Handler().postDelayed(() -> {
                Log.e("Size 1", assignments.size()+"");
                Log.e("Size 2", assignmentUrl.size()+"");
                initListView();
            }, 2000);
        }
    }

    private void initListView(){
        count1 = count+1;
        arrayAdapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, assignments);
        listAssignments.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        progressDialog.dismiss();
        listAssignments.setOnItemClickListener((parent, view, position, id) -> ItemClick(position));
    }

    public void ItemClick(int position){
        AssignmentActivity.sub_name = sub_name;
        AssignmentActivity.faculty_code = faculty_code;
        AssignmentActivity.assignmentNo = assignments.get(position);
        AssignmentActivity.assignmentUrl = assignmentUrl.get(position);
        startActivity(new Intent(getContext(), AssignmentActivity.class));
    }

    private void addAssignment() {
        Intent pickFile = new Intent(Intent.ACTION_GET_CONTENT);
        pickFile.setType("file/*");
        startActivityForResult(pickFile, PICK_FILE_REQUEST);
        Toast.makeText(getActivity().getApplicationContext(), "Open Documents", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            progressDialog1 = new ProgressDialog(getActivity());
            progressDialog1.setMessage("Please wait..");
            progressDialog1.setCancelable(false);
            progressDialog1.show();
            filePath = data.getData();
            String fileExt = String.valueOf(filePath).substring(String.valueOf(filePath).lastIndexOf("."));
            Log.e("Extention", fileExt);
            final StorageReference reference = storageReference.child("/Assignment"+count1+fileExt);
            uploadTask = reference.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        progressDialog1.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), "File Saved", Toast.LENGTH_SHORT).show();
                        getDownloadUrl(uploadTask, reference/*, type, key*/);
                    }
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    //displaying percentage in progress dialog
                    progressDialog1.setMessage("Uploading " + ((int) progress) + "%...");
                }
            });
        }
    }

    private void getDownloadUrl(StorageTask uploadTask, final StorageReference reference/*, final String type, final String key*/) {
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
                    DatabaseReference databaseReference1 = databaseReference.child("Assignment").child(sub_name)
                                            .child(faculty_code).child("assignment"+count1);
                    databaseReference1.child("url").setValue(downloadUri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            databaseReference1.child("window").setValue("Open").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Assignment Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                                    getData();
                                    updateAssignments();
                                }
                            });
                        }
                    });
                } else if(task.isCanceled()){
                    Toast.makeText(getActivity().getApplicationContext(), "Image Sending Failed!", Toast.LENGTH_SHORT).show();
                }
                else{

                }
            }
        });
    }

    private void updateAssignments() {
        DatabaseReference databaseReference1 = databaseReference.child("Subjects").child(sub_name).child("noofassignments");
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String noofassignments = dataSnapshot.getValue(String.class);
                databaseReference1.setValue(""+(Integer.parseInt(noofassignments)+1)).addOnSuccessListener(aVoid -> {
                    databaseReference.child("SubjectEnrollments").child(sub_name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                            for (DataSnapshot snapshot : dataSnapshot1.getChildren()){
                                databaseReference.child("StudentEnrollments").child(snapshot.getKey()).child(sub_name)
                                        .child("assignment"+count1).setValue("Pending");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
