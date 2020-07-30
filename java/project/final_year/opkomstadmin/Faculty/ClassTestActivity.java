package project.final_year.opkomstadmin.Faculty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import project.final_year.opkomstadmin.R;

public class ClassTestActivity extends AppCompatActivity {
    public static String ct_no, sub_name, dept_name;
    public static int flag1;
    int noOfStudents = 0;
    ImageView back;
    TextView title;
    TableLayout marksTable;
    Button submitMarks;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_test);

        Window window1 = this.getWindow();
        window1.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window1.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window1.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        TextView classTestTitle = toolbar.findViewById(R.id.chat_name_title);
        classTestTitle.setText(dept_name);
        setSupportActionBar(toolbar);
        back = (ImageView)toolbar.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassTestActivity.super.onBackPressed();
            }
        });

        title = findViewById(R.id.title);
        marksTable = findViewById(R.id.marksTable);
        submitMarks = findViewById(R.id.submitMarks);
        title.setText(ct_no);
        if(flag1 == 0)
            submitMarks.setText("Update Marks");
        else if(flag1 == 1)
            submitMarks.setText("Save Marks");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..\nData is being fetched!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final Typeface font = ResourcesCompat.getFont(this, R.font.worksans_medium);
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView tv11 = new TextView(this);
        tv11.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv11.setText("Roll No");
        tv11.setTextColor(Color.parseColor("#3E7C62"));
        tv11.setTypeface(font);
        row.addView(tv11);

        TextView tv12 = new TextView(this);
        tv12.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv12.setText("Marks");
        tv12.setTextColor(Color.parseColor("#3E7C62"));
        tv12.setTypeface(font);
        row.addView(tv12);

        marksTable.addView(row);

        fetchStuList(font);

        Handler handler = new Handler();
        handler.postDelayed(() ->
                progressDialog.dismiss(), 1000);

        submitMarks.setOnClickListener(v -> {
            final HashMap<String, String> marksMap = new HashMap<>();
            progressDialog.setMessage("Please Wait..");
            progressDialog.setCancelable(false);
            for(int i=1; i<=noOfStudents; i++){
                progressDialog.show();
                TableRow row1 = (TableRow) marksTable.getChildAt(i);
                TextView col0 = (TextView)row1.getChildAt(0);
                String roll_no = col0.getText().toString();
                EditText col1 = (EditText)row1.getChildAt(1);
                String marks = col1.getText().toString();
                if(marks.equals("")){
                    marks = "0";
                }
                databaseReference.child("Class Test Students").child(roll_no).child(ct_no).child(sub_name).setValue(marks).addOnFailureListener(e -> finish());
                marksMap.put(roll_no, marks);
            }
            databaseReference.child("Class Test Faculty").child(sub_name).child(ct_no).setValue(marksMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    UploadMarks.saved = 1;
                    if(flag1 == 0)
                    Toast.makeText(ClassTestActivity.this, "Marks Successfully Updated!", Toast.LENGTH_SHORT).show();
                    else if(flag1 == 1)
                        Toast.makeText(ClassTestActivity.this, "Marks Successfully Saved!", Toast.LENGTH_SHORT).show();
                    finish();
                    progressDialog.dismiss();
                }
            });
        });

    }

    private void fetchStuList(Typeface font) {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(2);
        if(flag1==0){

            databaseReference.child("SubjectEnrollments").child(sub_name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String roll = snapshot.getKey();
                            databaseReference.child("Class Test Faculty").child(sub_name).child(ct_no).child(roll).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                    String marks = "";
                                    if(dataSnapshot1.exists())
                                        marks = dataSnapshot1.getValue(String.class);

                                    TableRow row1 = new TableRow(getApplicationContext());
                                    row1.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


                                    TextView tv2 = new TextView(getApplicationContext());
                                    tv2.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    //tv2.setGravity(Gravity.CENTER);
                                    tv2.setText(roll);
                                    tv2.setTextColor(Color.parseColor("#222222"));
                                    tv2.setTypeface(font);
                                    row1.addView(tv2);

                                    EditText ed = new EditText(getApplicationContext());
                                    ed.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    ed.setGravity(Gravity.CENTER);
                                    ed.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    ed.setFilters(filterArray);
                                    ed.setText(marks);
                                    ed.setTextColor(Color.parseColor("#222222"));
                                    ed.setTypeface(font);
                                    row1.addView(ed);

                                    Log.e(roll, marks);
                                    noOfStudents++;
                                    marksTable.addView(row1);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(ClassTestActivity.this, "No records found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            /*databaseReference.child("Class Test Faculty").child(sub_name).child(ct_no).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String roll = snapshot.getKey();
                            String marks = snapshot.getValue(String.class);

                            TableRow row1 = new TableRow(getApplicationContext());
                            row1.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


                            TextView tv2 = new TextView(getApplicationContext());
                            tv2.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            //tv2.setGravity(Gravity.CENTER);
                            tv2.setText(roll);
                            tv2.setTextColor(Color.parseColor("#222222"));
                            tv2.setTypeface(font);
                            row1.addView(tv2);

                            EditText ed = new EditText(getApplicationContext());
                            ed.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            ed.setGravity(Gravity.CENTER);
                            ed.setInputType(InputType.TYPE_CLASS_NUMBER);
                            ed.setFilters(filterArray);
                            ed.setText(marks);
                            ed.setTextColor(Color.parseColor("#222222"));
                            ed.setTypeface(font);
                            row1.addView(ed);

                            Log.e(roll, marks);
                            noOfStudents++;
                            marksTable.addView(row1);
                        }
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(ClassTestActivity.this, "No records found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/
        }
        else if(flag1==1){
            databaseReference.child("SubjectEnrollments").child(sub_name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String roll = snapshot.getKey();
                            String marks = "";

                            TableRow row1 = new TableRow(getApplicationContext());
                            row1.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


                            TextView tv2 = new TextView(getApplicationContext());
                            tv2.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            //tv2.setGravity(Gravity.CENTER);
                            tv2.setText(roll);
                            tv2.setTextColor(Color.parseColor("#222222"));
                            tv2.setTypeface(font);
                            row1.addView(tv2);

                            EditText ed = new EditText(getApplicationContext());
                            ed.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            ed.setGravity(Gravity.CENTER);
                            ed.setInputType(InputType.TYPE_CLASS_NUMBER);
                            ed.setFilters(filterArray);
                            ed.setText(marks);
                            ed.setTextColor(Color.parseColor("#222222"));
                            ed.setTypeface(font);
                            row1.addView(ed);

                            Log.e(roll, marks);
                            noOfStudents++;
                            marksTable.addView(row1);
                        }
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(ClassTestActivity.this, "No records found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
