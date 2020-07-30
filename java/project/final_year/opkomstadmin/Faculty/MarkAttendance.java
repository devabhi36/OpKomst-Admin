package project.final_year.opkomstadmin.Faculty;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.HashMap;
import project.final_year.opkomstadmin.R;

public class MarkAttendance extends Fragment {
    public static String sub_name, deptName;
    EditText attendanceDate;
    TableLayout attendanceTable;
    Button submitAttendance;
    String attendanceDate1 = "";
    int noOfStudents = 0, count = 0, flag = 0;
    TextView title;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mark_attendance, container, false);
        title = view.findViewById(R.id.title);
        attendanceDate = view.findViewById(R.id.attendanceDate);
        attendanceTable = view.findViewById(R.id.attendanceTable);
        submitAttendance = view.findViewById(R.id.submitAttendance);
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

        final Calendar calendar = Calendar.getInstance();
        attendanceDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int month1 = month+1;
                attendanceDate.setInputType(View.AUTOFILL_TYPE_NONE);
                if(month1<10 && dayOfMonth<10){
                    attendanceDate.setText("0"+dayOfMonth+"-0"+month1+"-"+year);
                    //attendanceDate1 = "0"+dayOfMonth+"0"+month1+year;
                    attendanceDate1 = year+"0"+month1+"0"+dayOfMonth;
                } else if(month1<10 && dayOfMonth>9){
                    attendanceDate.setText(dayOfMonth+"-0"+month1+"-"+year);
                    //attendanceDate1 = dayOfMonth+"0"+month1+year;
                    attendanceDate1 = year+"0"+month1+dayOfMonth;
                } else if(month1>9 && dayOfMonth>9){
                    attendanceDate.setText(dayOfMonth+"-"+month1+"-"+year);
                    //attendanceDate1 = ""+dayOfMonth+month1+year;
                    attendanceDate1 = year+month1+dayOfMonth+"";
                } else if(month1>9 && dayOfMonth<10){
                    attendanceDate.setText("0"+dayOfMonth+"-"+month1+"-"+year);
                    //attendanceDate1 = "0"+dayOfMonth+month1+year;
                    attendanceDate1 = year+month1+"0"+dayOfMonth;
                }
                Log.e("Date", attendanceDate1);

            }
        };

        final Typeface font = ResourcesCompat.getFont(getContext(), R.font.worksans_medium);
        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView tv11 = new TextView(getContext());
        tv11.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv11.setGravity(Gravity.CENTER);
        tv11.setText("Roll No");
        tv11.setTextColor(Color.parseColor("#3E7C62"));
        tv11.setTypeface(font);
        row.addView(tv11);

        TextView tv12 = new TextView(getContext());
        tv12.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //tv12.setGravity(Gravity.CENTER);
        tv12.setText("Name");
        tv12.setTextColor(Color.parseColor("#3E7C62"));
        tv12.setTypeface(font);
        row.addView(tv12);

        TextView tv13 = new TextView(getContext());
        tv13.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv13.setGravity(Gravity.CENTER);
        tv13.setText("Pres");
        tv13.setTextColor(Color.parseColor("#3E7C62"));
        tv13.setTypeface(font);
        row.addView(tv13);

        TextView tv14 = new TextView(getContext());
        tv14.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv14.setGravity(Gravity.CENTER);
        tv14.setText("Abs");
        tv14.setTextColor(Color.parseColor("#3E7C62"));
        tv14.setTypeface(font);
        row.addView(tv14);

        attendanceTable.addView(row);

        fetchStuList(font);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                progressDialog.dismiss();
            }
        }, 1000);



        submitAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Value of count", count+"");
                if(attendanceDate1.equals("")){
                    Toast.makeText(getContext(), "Please Fill in the date!", Toast.LENGTH_SHORT).show();
                }
                /*else if(count!=noOfStudents){
                    Toast.makeText(getContext(), "Please Mark Attendance for every student!", Toast.LENGTH_SHORT).show();
                }*/
                else {
                    final DatabaseReference dbref = databaseReference.child("SubjectAttendance").child(sub_name).child(attendanceDate1);
                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot4) {
                            if(!dataSnapshot4.exists()){
                                progressDialog.setMessage("Please Wait..");
                                progressDialog.setCancelable(false);
                                final DatabaseReference databaseReference2 = databaseReference.child("Subjects").child(sub_name).child("noofclasses");
                                databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int noofclasses = Integer.parseInt(dataSnapshot.getValue(String.class));
                                        int noofclasses1 = noofclasses+1;
                                        databaseReference2.setValue(Integer.toString(noofclasses1));

                                        final HashMap<String, String> subjectAttendance = new HashMap<>();
                                        for(int i=1; i<=noOfStudents; i++){
                                            progressDialog.show();
                                            TableRow row = (TableRow) attendanceTable.getChildAt(i);
                                            TextView col0 = (TextView)row.getChildAt(0);
                                            final String roll_no = col0.getText().toString();
                                            RadioButton col2 = (RadioButton) row.getChildAt(2);
                                            RadioButton col3 = (RadioButton) row.getChildAt(3);
                                            String result = "";
                                            if(col2.isChecked())
                                                result = "Present";
                                            else if(col3.isChecked())
                                                result = "Absent";

                                            subjectAttendance.put(roll_no, result);
                                        databaseReference.child("StudentAttendance").child(roll_no)
                                                .child(sub_name).child(attendanceDate1).setValue(result).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                        if(result.equals("Present")){
                                            final DatabaseReference databaseReference1 = databaseReference.child("StudentEnrollments").child(roll_no).child(sub_name);
                                            databaseReference1.child("classattended").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    int classAttended = Integer.parseInt(dataSnapshot.getValue(String.class));
                                                    int classAttended1 = classAttended+1;
                                                    databaseReference1.child("classattended").setValue(Integer.toString(classAttended1));
                                                    double attendance = (classAttended1*100/noofclasses1);
                                                    databaseReference1.child("attendance").setValue(attendance);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                        if(result.equals("Absent")){
                                            final DatabaseReference databaseReference1 = databaseReference.child("StudentEnrollments").child(roll_no).child(sub_name);
                                            databaseReference1.child("classattended").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    int classAttended = Integer.parseInt(dataSnapshot.getValue(String.class));
                                                    double attendance = (classAttended*100/noofclasses1);
                                                    databaseReference1.child("attendance").setValue(attendance);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            dbref.setValue(subjectAttendance).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getContext(), "Attendance Saved!", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();

                                                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                                    getActivity().getSupportFragmentManager().beginTransaction()
                                                            /*.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)*/
                                                            .replace(((ViewGroup)getView().getParent()).getId(), new SubjectActions()).
                                                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack("SubjectActions").commit();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                HashMap<String, String> studentAttendance = new HashMap<>();
                            }
                            else{
                                Toast.makeText(getContext(), "Attendance already taken!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
    }

    public void fetchStuList(final Typeface font){

        final ColorStateList colorStateListBlue = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[] {getResources().getColor(R.color.blue) }
        );
        final ColorStateList colorStateListRed = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[] {getResources().getColor(R.color.red) }
        );

        databaseReference.child("SubjectEnrollments").child(sub_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(final DataSnapshot snapshot : dataSnapshot.getChildren()){
                        final String roll = snapshot.getKey();
                        databaseReference.child("Students").child(roll).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                String name = dataSnapshot1.getValue(String.class);
                                TableRow row1 = new TableRow(getContext());
                                row1.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                                flag = 0;

                                TextView tv2 = new TextView(getContext());
                                tv2.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                //tv2.setGravity(Gravity.CENTER);
                                tv2.setText(roll);
                                tv2.setTextColor(Color.parseColor("#222222"));
                                tv2.setTypeface(font);
                                row1.addView(tv2);

                                TextView tv3 = new TextView(getContext());
                                tv3.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tv2.setGravity(Gravity.CENTER);
                                tv3.setText(name);
                                tv3.setTextColor(Color.parseColor("#222222"));
                                tv3.setTypeface(font);
                                row1.addView(tv3);

                                AppCompatRadioButton p = new AppCompatRadioButton(getContext());
                                p.setSupportButtonTintList(colorStateListBlue);
                                p.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                p.setGravity(Gravity.CENTER);
                                //p.setChecked(true);
                                row1.addView(p);

                                AppCompatRadioButton a = new AppCompatRadioButton(getContext());
                                a.setSupportButtonTintList(colorStateListRed);
                                a.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                a.setGravity(Gravity.CENTER);
                                row1.addView(a);

                                final RadioButton present = (RadioButton) row1.getChildAt(2);
                                final RadioButton absent = (RadioButton) row1.getChildAt(3);

                                present.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if(isChecked==true){
                                            absent.setChecked(false);
                                            if(flag==0){
                                                count++;
                                                flag = 1;
                                            }
                                        }
                                    }
                                });
                                absent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if(isChecked==true){
                                            present.setChecked(false);
                                            if(flag==0){
                                                count++;
                                                flag = 1;
                                            }
                                        }
                                    }
                                });
                                Log.e(roll, name);
                                noOfStudents++;
                                attendanceTable.addView(row1);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else {
                    Toast.makeText(getContext(), "No Students Available!", Toast.LENGTH_SHORT).show();
                    submitAttendance.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}