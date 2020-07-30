package project.final_year.opkomstadmin.Faculty;

import android.app.ProgressDialog;
import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import project.final_year.opkomstadmin.R;
import project.final_year.opkomstadmin.model.DaoStudent;

public class UploadMarks extends Fragment {
    public static String sub_name, deptName;
    public static int saved=0;
    TextView title, noStudents;
    ListView listClassTests;
    Button next_classTest;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<String> classTest = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    int count = 0, flag;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_marks, container, false);
        title = view.findViewById(R.id.title);
        noStudents = view.findViewById(R.id.noStudents);
        listClassTests = view.findViewById(R.id.listClassTests);
        next_classTest = view.findViewById(R.id.next_classTest);
        swipeRefreshLayout = view.findViewById(R.id.refreshLayout);
        title.setText(deptName);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

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

        next_classTest.setOnClickListener(v ->{
            Log.e("Count", classTest.size()+"");
            ClassTestActivity.ct_no = "Class Test "+(classTest.size()+1);
            ClassTestActivity.sub_name = sub_name;
            ClassTestActivity.dept_name = deptName;
            ClassTestActivity.flag1 = 1;
            startActivity(new Intent(getContext(), ClassTestActivity.class));
        });
    }

    private void getData() {
        databaseReference.child("SubjectEnrollments").child(sub_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                if(dataSnapshot1.exists()){
                    count = 0;
                    noStudents.setVisibility(View.GONE);
                    flag = 0;
                    databaseReference.child("Class Test Faculty").child(sub_name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                classTest.clear();
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    count++;
                                    classTest.add(snapshot.getKey());
                                }
                                Log.e("COUNT", classTest.size()+"");
                            }
                            else{
                                flag = 1;
                                progressDialog.dismiss();
                                Toast.makeText(getActivity().getApplicationContext(), "No Class Test Found!", Toast.LENGTH_SHORT).show();
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
                    next_classTest.setClickable(false);
                    next_classTest.setFocusable(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(flag==0){
            new Handler().postDelayed(() -> {
                Log.e("Size 1", classTest.size()+"");
                initListView();
            }, 2000);
        }
    }

    private void initListView() {
        arrayAdapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, classTest);
        listClassTests.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        progressDialog.dismiss();
        listClassTests.setOnItemClickListener((parent, view, position, id) -> ItemClick(position));
    }

    private void ItemClick(int position) {
        ClassTestActivity.ct_no = classTest.get(position);
        ClassTestActivity.sub_name = sub_name;
        ClassTestActivity.dept_name = deptName;
        ClassTestActivity.flag1 = 0;
        startActivity(new Intent(getContext(), ClassTestActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(saved==1) {
            count = 0;
            progressDialog.setMessage("Please wait..");
            progressDialog.setCancelable(false);
            progressDialog.dismiss();
            getData();
            saved=0;
        }
    }


}
