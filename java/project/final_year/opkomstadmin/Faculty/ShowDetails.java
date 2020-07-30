package project.final_year.opkomstadmin.Faculty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import project.final_year.opkomstadmin.R;
import project.final_year.opkomstadmin.model.DaoStudent;

public class ShowDetails extends AppCompatActivity {
    public static String sub_name;
    String classAttended, noOfAssignment;
    public static DaoStudent daoStudent;
    ImageView back;
    TextView nameV, rollV, branchV, emailV, contactV, yearV, lateralV, enrlV, classAttendedV, attendanceperV;
    CircleImageView profileImageHome;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ListView assignmentList;
    ArrayList<String> listAssignment = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        TextView chat_name_title = toolbar.findViewById(R.id.chat_name_title);
        chat_name_title.setText("DETAILS");
        setSupportActionBar(toolbar);
        back = (ImageView)toolbar.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDetails.super.onBackPressed();
            }
        });


        nameV = (TextView)findViewById(R.id.nameV);
        rollV = (TextView)findViewById(R.id.rollV);
        branchV = (TextView)findViewById(R.id.branchV);
        emailV = (TextView)findViewById(R.id.emailV);
        contactV = (TextView)findViewById(R.id.contactV);
        yearV = (TextView)findViewById(R.id.yearV);
        lateralV = (TextView)findViewById(R.id.lateralV);
        enrlV = (TextView)findViewById(R.id.enrlV);
        classAttendedV = (TextView)findViewById(R.id.classAttendedV);
        attendanceperV = (TextView)findViewById(R.id.attendanceperV);
        profileImageHome = findViewById(R.id.profileImageHome);
        assignmentList = findViewById(R.id.assignmentList);

        nameV.setText(daoStudent.getName());
        rollV.setText(daoStudent.getRoll());
        branchV.setText(daoStudent.getDepartment());
        emailV.setText(daoStudent.getEmail());
        contactV.setText(daoStudent.getContact());
        yearV.setText(daoStudent.getYear()+" YEAR, "+daoStudent.getSemester()+"th Semester");
        lateralV.setText(daoStudent.getLateral());
        enrlV.setText(daoStudent.getEnrollmentNo());
        if(!daoStudent.getDpurl().equals("")){
            Glide.with(this).load(daoStudent.getDpurl()).into(profileImageHome);
        }

        getClassAttended();
        calc_attendance();

    }

    private void getClassAttended() {
        databaseReference.child("StudentEnrollments").child(daoStudent.getRoll()).child(sub_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                classAttended = dataSnapshot.child("classattended").getValue(String.class);
                classAttendedV.setText(classAttended);
                double attendance = dataSnapshot.child("attendance").getValue(Double.class);
                attendanceperV.setText(attendance+"%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void calc_attendance() {
        databaseReference.child("Subjects").child(sub_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noOfAssignment = dataSnapshot.child("noofassignments").getValue(String.class);
                getAssignments(Integer.parseInt(noOfAssignment));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAssignments(int noOfAssignment) {
        listAssignment.clear();
        DatabaseReference databaseReference1 = databaseReference.child("StudentEnrollments").child(daoStudent.getRoll()).child(sub_name);
        for(int i=1; i<=noOfAssignment; i++){
            int j = i;
            databaseReference1.child("assignment"+i).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listAssignment.add("Assignment "+j+"   "+dataSnapshot.getValue(String.class));
                    arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, listAssignment);
                    assignmentList.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
