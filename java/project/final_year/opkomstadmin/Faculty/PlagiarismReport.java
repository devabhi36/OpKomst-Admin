package project.final_year.opkomstadmin.Faculty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import project.final_year.opkomstadmin.R;

public class PlagiarismReport extends AppCompatActivity {
    public static String sub_name, assignmentNo, roll_no;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView plagiarismReport;
    ImageView back;
    ProgressDialog progressDialog;
    ArrayList<String> roll_pagiarism = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plagiarism_report);

        Window window1 = this.getWindow();
        window1.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window1.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window1.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        TextView plagirasmReportTitle = toolbar.findViewById(R.id.chat_name_title);
        plagirasmReportTitle.setText(roll_no);
        setSupportActionBar(toolbar);
        back = (ImageView)toolbar.findViewById(R.id.back);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlagiarismReport.super.onBackPressed();
            }
        });

        plagiarismReport = findViewById(R.id.plagiarismReport);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Generating Report...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        getData();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            getData();
            new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1000);
        });
    }

    private void getData() {
        FirebaseDatabase.getInstance().getReference().child("Plagiarism Report").child(sub_name).child(assignmentNo.toLowerCase())
                .child(roll_no).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    roll_pagiarism.clear();
                    roll_pagiarism.add("Roll No\t\t\t\t\t\t\t\tMatched");
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String key = snapshot.getKey();
                        String value = snapshot.getValue().toString();
                        roll_pagiarism.add(key+"\t\t\t\t\t"+value+"%");

                        arrayAdapter = new ArrayAdapter(getApplicationContext(),  android.R.layout.simple_list_item_1, roll_pagiarism);
                        plagiarismReport.setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}