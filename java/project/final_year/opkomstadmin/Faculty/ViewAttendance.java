package project.final_year.opkomstadmin.Faculty;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import project.final_year.opkomstadmin.R;

public class ViewAttendance extends Fragment {
    public static String sub_name, deptName;
    EditText attendanceDate;
    TableLayout attendanceTable;
    String attendanceDate1 = "";
    CardView attendaceCardView;
    TextView title;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    Typeface font;
    ColorStateList colorStateListRed, colorStateListBlue;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_attendance, container, false);
        title = view.findViewById(R.id.title);
        attendanceDate = view.findViewById(R.id.attendanceDate);
        attendaceCardView = view.findViewById(R.id.attendaceCardView);
        attendanceTable = view.findViewById(R.id.attendanceTable);

        title.setText(deptName);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        font = ResourcesCompat.getFont(getContext(), R.font.worksans_medium);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        colorStateListBlue = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[] {getResources().getColor(R.color.blue) }
        );
        colorStateListRed = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[] {getResources().getColor(R.color.red) }
        );

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
                attendanceTable.removeAllViews();
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
                getRecords();
                Log.e("Date", attendanceDate1);
            }
        };
    }

    public void getRecords(){
        if (attendanceDate1.equals("")) {
            Toast.makeText(getContext(), "Please Fill in the date!", Toast.LENGTH_SHORT).show();
        } else {
            databaseReference.child("SubjectAttendance").child(sub_name).child(attendanceDate1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        createHeading();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final String roll = snapshot.getKey();
                            final String attendanceValue = snapshot.getValue(String.class);
                            databaseReference.child("Students").child(roll).child("name").addValueEventListener(new ValueEventListener() {
                                @SuppressLint("RestrictedApi")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                    String name = dataSnapshot1.getValue(String.class);
                                    TableRow row1 = new TableRow(getContext());
                                    row1.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

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
                                    p.setClickable(false);
                                    if (attendanceValue.equals("Present"))
                                        p.setChecked(true);
                                    row1.addView(p);

                                    AppCompatRadioButton a = new AppCompatRadioButton(getContext());
                                    a.setSupportButtonTintList(colorStateListRed);
                                    a.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    a.setGravity(Gravity.CENTER);
                                    a.setClickable(false);
                                    if (attendanceValue.equals("Absent"))
                                        a.setChecked(true);
                                    row1.addView(a);

                                    attendanceTable.addView(row1);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        attendaceCardView.setVisibility(View.VISIBLE);
                    } else {
                        attendaceCardView.setVisibility(View.GONE);
                        Toast.makeText(getActivity().getApplicationContext(), "No Record Found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void createHeading(){
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
    }

}
