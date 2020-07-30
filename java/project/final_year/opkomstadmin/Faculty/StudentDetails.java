package project.final_year.opkomstadmin.Faculty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import project.final_year.opkomstadmin.R;
import project.final_year.opkomstadmin.model.DaoStudent;

public class StudentDetails extends Fragment {
    public static String sub_name, deptName;
    ListView studentList;
    public ArrayList<String> student_list = new ArrayList<String>(), student_roll_list = new ArrayList<String>();;
    public ArrayList<DaoStudent> stu_details;
    ProgressDialog progressDialog;
    TextView title, noStudents;
    ArrayAdapter<String> arrayAdapter;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    SwipeRefreshLayout swipeRefreshLayout;
    int flag = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_details, container, false);
        title = view.findViewById(R.id.title);
        noStudents = view.findViewById(R.id.noStudents);
        studentList = view.findViewById(R.id.studentList);
        swipeRefreshLayout = view.findViewById(R.id.refreshLayout);
        stu_details = new ArrayList<DaoStudent>();
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, student_list);
                studentList.setAdapter(arrayAdapter);
                progressDialog.dismiss();
                arrayAdapter.notifyDataSetChanged();
                studentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ItemClick(stu_details.get(position));
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

    }

    public void getData(){
        databaseReference.child("SubjectEnrollments").child(sub_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    noStudents.setVisibility(View.GONE);
                    flag=0;
                    Log.e("PEHLE KE ANDAR", "DATA EXISTS");
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        final String roll = snapshot.getKey();
                        student_roll_list.add(roll);
                        Log.e(" Roll ", roll);
                        ValueEventListener valueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                DaoStudent daoStudent = dataSnapshot1.getValue(DaoStudent.class);
                                stu_details.add(daoStudent);
                                String name =  daoStudent.getName();
                                Log.e(roll, name);
                                student_list.add(roll+"\t\t"+name);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        };
                        databaseReference.child("Students").child(roll).addListenerForSingleValueEvent(valueEventListener);
                    }
                }
                else{
                    flag = 1;
                    noStudents.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(flag==0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("Size", Integer.toString(student_list.size()));
                    arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, student_list);
                    studentList.setAdapter(arrayAdapter);
                    progressDialog.dismiss();
                    arrayAdapter.notifyDataSetChanged();
                    studentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ItemClick(stu_details.get(position));
                        }
                    });
                }
            }, 2000);
        }
    }

    public void ItemClick(DaoStudent daoStudent){
        ShowDetails.daoStudent = daoStudent;
        ShowDetails.sub_name = sub_name;
        startActivity(new Intent(getContext(), ShowDetails.class));
    }
}
