package project.final_year.opkomstadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;

import project.final_year.opkomstadmin.Faculty.ChatActivity;
import project.final_year.opkomstadmin.Faculty.Faculty_Home;
import project.final_year.opkomstadmin.Faculty.NoticeFragment;
import project.final_year.opkomstadmin.Faculty.SubjectChats;
import project.final_year.opkomstadmin.Faculty.TimeTableFragment;
import project.final_year.opkomstadmin.Faculty.ViewGivenAssignments;
import project.final_year.opkomstadmin.model.DaoFaculty;
import project.final_year.opkomstadmin.model.DaoSubject;
import project.final_year.opkomstadmin.Faculty.HomeFragment;
import project.final_year.opkomstadmin.model.Show_teacher_subjects;

public class MainActivity extends AppCompatActivity {

    TextView sign_up;
    EditText uname, pass;
    String uname1, pass1;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    Button login;
    DaoSubject daoSubject;
    public ArrayList<Show_teacher_subjects> saved_subjects;
    public static final String PREFS_NAME = "LoginPrefsFaculty";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uname = (EditText) findViewById(R.id.uname);
        pass = (EditText) findViewById(R.id.epass);
        login = (Button) findViewById(R.id.login);
        HomeFragment.flag = 0;

        SharedPreferences logged = getSharedPreferences(PREFS_NAME, 0);
        String user = logged.getString("USER", "");
        String dpUrl = logged.getString("DPURL", "");
        Gson gson = new Gson();
        String json1 = logged.getString("DaoFaculty", "");
        DaoFaculty object = gson.fromJson(json1, DaoFaculty.class);
        if(logged.getString("ROLE", "").equals("Faculty")){
            //saved_Subject(user);
            //HomeFragment.daoStudent = object;
            saved_subjects = savedSubject(user);
            HomeFragment.name = object.getName();
            ChatActivity.user = object.getName();
            ChatActivity.userId = user;
            ViewGivenAssignments.faculty_code = user;
            Faculty_Home.uId = user;
            Faculty_Home.profileImageUrl = dpUrl;
            HomeFragment.saved_subjects = saved_subjects;
            NoticeFragment.deptName = object.getDepartment();
            TimeTableFragment.deptName = object.getDepartment();
            NoticeFragment.isHOD = object.getHod();
            //TimeTableFragment.isHOD = object.getHod();
            Faculty_Home.isHOD = object.getHod();
            Intent intent = new Intent(MainActivity.this, Faculty_Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        sign_up = (TextView) findViewById(R.id.sign_up);
        sign_up.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Sign_Up.class)));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uname1 = uname.getText().toString();
                pass1 = pass.getText().toString();
                if (uname1.equals("") || pass1.equals("")) {
                    Toast.makeText(MainActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();
                }
                else{
                    databaseReference.child("Faculty").child(uname1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                DaoFaculty daoFaculty = dataSnapshot.getValue(DaoFaculty.class);
                                String value = daoFaculty.getPassword();
                                if (pass1.equals(value)) {
                                    saved_subjects = savedSubject(uname1);
                                    HomeFragment.name = daoFaculty.getName();
                                    ViewGivenAssignments.faculty_code = uname1;
                                    ChatActivity.user = daoFaculty.getName();
                                    ChatActivity.userId = uname1;
                                    Faculty_Home.uId = uname1;
                                    Faculty_Home.profileImageUrl = daoFaculty.getDpurl();
                                    HomeFragment.saved_subjects = saved_subjects;
                                    NoticeFragment.deptName = daoFaculty.getDepartment();
                                    TimeTableFragment.deptName = daoFaculty.getDepartment();
                                    NoticeFragment.isHOD = daoFaculty.getHod();
                                    //TimeTableFragment.isHOD = daoFaculty.getHod();
                                    Faculty_Home.isHOD = daoFaculty.getHod();

                                    SharedPreferences login = getSharedPreferences(PREFS_NAME, 0);
                                    SharedPreferences.Editor editor = login.edit();
                                    editor.putString("ROLE", "Faculty");
                                    editor.putString("USER", uname1);
                                    editor.putString("DPURL", daoFaculty.getDpurl());
                                    Gson gson = new Gson();
                                    String json1 = gson.toJson(daoFaculty);
                                    editor.putString("DaoFaculty", json1);
                                    editor.apply();
                                    editor.commit();
                                    Log.e("SAVED", "DATA SAVED");

                                    Intent intent = new Intent(MainActivity.this, Faculty_Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                } else
                                    Toast.makeText(MainActivity.this, "Password Incorrect!", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(MainActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    public ArrayList<Show_teacher_subjects> savedSubject(String uname){
        final ArrayList<Show_teacher_subjects> saved_subjects1 = new ArrayList<>();
        final ArrayList<String> subjects = new ArrayList<>();
        final ArrayList<String> subjects_codes = new ArrayList<>();
        Query query = databaseReference.child("Subjects").orderByChild("funiqueid").equalTo(uname);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        final String subject_name = snapshot.getKey();
                        subjects.add(subject_name);
                        daoSubject = snapshot.getValue(DaoSubject.class);
                        saved_subjects1.add(new Show_teacher_subjects(subject_name, "Year: "+daoSubject.getYear(), "Semester: "+daoSubject.getSemester(),
                                "Classes Held: "+daoSubject.getNoofclasses(), "Assignments Given: "+daoSubject.getNoofassignments(), daoSubject.getDepartment()));
                        subjects_codes.add(daoSubject.getCode());
                        FirebaseMessaging.getInstance().subscribeToTopic(daoSubject.getCode()).addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Log.e("Subscribed to", daoSubject.getCode());
                            }
                        });
                    }
                    SubjectChats.subjects = subjects;
                    SubjectChats.subject_codes = subjects_codes;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return saved_subjects1;
    }
}
