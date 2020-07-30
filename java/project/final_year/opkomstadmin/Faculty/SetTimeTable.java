package project.final_year.opkomstadmin.Faculty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import project.final_year.opkomstadmin.R;

public class SetTimeTable extends AppCompatActivity {

    public static String sem, day, deptName;
    ImageView back, addRow;
    TextView title;
    TableLayout timeTable;
    Button submitTimeTable;
    int noOfRows = 0;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time_table);

        Window window1 = this.getWindow();
        window1.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window1.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window1.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        TextView timeTableTitle = toolbar.findViewById(R.id.chat_name_title);
        timeTableTitle.setText(sem);
        setSupportActionBar(toolbar);
        back = (ImageView)toolbar.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetTimeTable.super.onBackPressed();
            }
        });
        title = findViewById(R.id.title);
        title.setText(day);

        addRow = findViewById(R.id.addRow);
        timeTable = findViewById(R.id.timeTable);
        submitTimeTable = findViewById(R.id.submitTimeTable);

        final Typeface font = ResourcesCompat.getFont(this, R.font.worksans_medium);
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView tv11 = new TextView(this);
        tv11.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv11.setText("TIME");
        tv11.setTextColor(Color.parseColor("#3E7C62"));
        tv11.setTypeface(font);
        row.addView(tv11);

        TextView tv12 = new TextView(this);
        tv12.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv12.setText("SUBJECT");
        tv12.setTextColor(Color.parseColor("#3E7C62"));
        tv12.setTypeface(font);
        row.addView(tv12);

        timeTable.addView(row);

        fetchList();

        addRow.setOnClickListener(v -> {
            createRow("", "");
        });

        submitTimeTable.setOnClickListener(v -> {
            saveTimeTable();
        });
    }

    private void fetchList() {
        DatabaseReference databaseReference1 = databaseReference.child("Time Table").child(deptName).child(sem).child(day);
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String time = snapshot.getKey();
                        String sub = snapshot.getValue(String.class);
                        createRow(time, sub);
                    }
                }
                else {
                    createRow("", "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createRow(String time, String subject) {
        final Typeface font = ResourcesCompat.getFont(this, R.font.worksans_medium);
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(5);
        EditText edtime = new EditText(getApplicationContext());
        edtime.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        edtime.setGravity(Gravity.CENTER);
        edtime.setTextColor(Color.parseColor("#222222"));
        edtime.setTypeface(font);
        edtime.setFilters(filterArray);
        edtime.setText(time);
        row.addView(edtime);

        InputFilter[] filterArray1 = new InputFilter[1];
        filterArray1[0] = new InputFilter.LengthFilter(30);
        EditText edsubject = new EditText(getApplicationContext());
        edsubject.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        edsubject.setGravity(Gravity.CENTER);
        edsubject.setTextColor(Color.parseColor("#222222"));
        edsubject.setTypeface(font);
        edsubject.setFilters(filterArray1);
        edsubject.setText(subject);
        row.addView(edsubject);

        noOfRows++;
        timeTable.addView(row);
    }

    private void saveTimeTable() {
        final HashMap<String, String> timeTableMap = new HashMap<>();
        DatabaseReference databaseReference1 = databaseReference.child("Time Table").child(deptName).child(sem).child(day);
        for(int i=1; i<=noOfRows; i++){
            TableRow row1 = (TableRow) timeTable.getChildAt(i);
            TextView col0 = (TextView)row1.getChildAt(0);
            String time = col0.getText().toString();
            EditText col1 = (EditText)row1.getChildAt(1);
            String sub_name = col1.getText().toString();
            if(!TextUtils.isEmpty(time)){
                timeTableMap.put(time, sub_name);
            }
            else {
                continue;
            }
        }
        databaseReference1.setValue(timeTableMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SetTimeTable.this, "Attendance Saved Successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
