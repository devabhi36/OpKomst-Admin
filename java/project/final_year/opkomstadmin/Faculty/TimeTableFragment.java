package project.final_year.opkomstadmin.Faculty;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import project.final_year.opkomstadmin.R;

public class TimeTableFragment extends Fragment {

    public static String deptName, isHOD;
    Button sem1, sem2, sem3, sem4, sem5, sem6, sem7, sem8;
    ListView  sem1Days, sem2Days, sem3Days, sem4Days, sem5Days, sem6Days, sem7Days, sem8Days;
    Switch oddEven;
    TextView semType;
    ArrayList<String> days = new ArrayList<>();
    int showHide=0;
    ArrayList<String> sem = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_table, container, false);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        oddEven = view.findViewById(R.id.oddEven);
        semType = view.findViewById(R.id.semType);

        sem1 = view.findViewById(R.id.sem1);
        sem2 = view.findViewById(R.id.sem2);
        sem3 = view.findViewById(R.id.sem3);
        sem4 = view.findViewById(R.id.sem4);
        sem5 = view.findViewById(R.id.sem5);
        sem6 = view.findViewById(R.id.sem6);
        sem7 = view.findViewById(R.id.sem7);
        sem8 = view.findViewById(R.id.sem8);

        sem1Days = view.findViewById(R.id.sem1Days);
        sem2Days = view.findViewById(R.id.sem2Days);
        sem3Days = view.findViewById(R.id.sem3Days);
        sem4Days = view.findViewById(R.id.sem4Days);
        sem5Days = view.findViewById(R.id.sem5Days);
        sem6Days = view.findViewById(R.id.sem6Days);
        sem7Days = view.findViewById(R.id.sem7Days);
        sem8Days = view.findViewById(R.id.sem8Days);

        days.add("Monday");
        days.add("Tuesday");
        days.add("Wednesday");
        days.add("Thursday");
        days.add("Friday");
        days.add("Saturday");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getSupportFragmentManager().popBackStack("HomeFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Faculty_Home.flag = 0;
        HomeFragment.flag = 1;

        if(!oddEven.isChecked()){
            sem2.setVisibility(View.GONE);
            sem4.setVisibility(View.GONE);
            sem6.setVisibility(View.GONE);
            sem8.setVisibility(View.GONE);
            sem1.setVisibility(View.VISIBLE);
            sem3.setVisibility(View.VISIBLE);
            sem5.setVisibility(View.VISIBLE);
            sem7.setVisibility(View.VISIBLE);
            semType.setText("Odd Semester");
        }

        oddEven.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if(oddEven.isChecked()){
                sem1.setVisibility(View.GONE);
                sem3.setVisibility(View.GONE);
                sem5.setVisibility(View.GONE);
                sem7.setVisibility(View.GONE);
                sem2.setVisibility(View.VISIBLE);
                sem4.setVisibility(View.VISIBLE);
                sem6.setVisibility(View.VISIBLE);
                sem8.setVisibility(View.VISIBLE);
                semType.setText("Even Semester");
            }
            else {
                sem2.setVisibility(View.GONE);
                sem4.setVisibility(View.GONE);
                sem6.setVisibility(View.GONE);
                sem8.setVisibility(View.GONE);
                sem1.setVisibility(View.VISIBLE);
                sem3.setVisibility(View.VISIBLE);
                sem5.setVisibility(View.VISIBLE);
                sem7.setVisibility(View.VISIBLE);
                semType.setText("Odd Semester");
            }
        });

        sem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showHide==0){
                    sem1Days.setVisibility(View.VISIBLE);
                    showHide=1;
                }
                else if(showHide==1){
                    sem1Days.setVisibility(View.GONE);
                    showHide=0;
                }
                sem2Days.setVisibility(View.GONE);
                sem3Days.setVisibility(View.GONE);
                sem4Days.setVisibility(View.GONE);
                sem5Days.setVisibility(View.GONE);
                sem6Days.setVisibility(View.GONE);
                sem7Days.setVisibility(View.GONE);
                sem8Days.setVisibility(View.GONE);
                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, days);
                sem1Days.setAdapter(adapter);
                sem1Days.setOnItemClickListener((parent, view, position1, id) ->
                        ItemClick(days.get(position1),  "Semester 1"));
            }
        });
        sem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showHide==0){
                    sem2Days.setVisibility(View.VISIBLE);
                    showHide=1;
                }
                else if(showHide==1){
                    sem2Days.setVisibility(View.GONE);
                    showHide=0;
                }
                sem1Days.setVisibility(View.GONE);
                sem3Days.setVisibility(View.GONE);
                sem4Days.setVisibility(View.GONE);
                sem5Days.setVisibility(View.GONE);
                sem6Days.setVisibility(View.GONE);
                sem7Days.setVisibility(View.GONE);
                sem8Days.setVisibility(View.GONE);
                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, days);
                sem2Days.setAdapter(adapter);
                sem2Days.setOnItemClickListener((parent, view, position1, id) ->
                        ItemClick(days.get(position1),  "Semester 2"));
            }
        });
        sem3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showHide==0){
                    sem3Days.setVisibility(View.VISIBLE);
                    showHide=1;
                }
                else if(showHide==1){
                    sem3Days.setVisibility(View.GONE);
                    showHide=0;
                }
                sem2Days.setVisibility(View.GONE);
                sem1Days.setVisibility(View.GONE);
                sem4Days.setVisibility(View.GONE);
                sem5Days.setVisibility(View.GONE);
                sem6Days.setVisibility(View.GONE);
                sem7Days.setVisibility(View.GONE);
                sem8Days.setVisibility(View.GONE);
                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, days);
                sem3Days.setAdapter(adapter);
                sem3Days.setOnItemClickListener((parent, view, position1, id) ->
                        ItemClick(days.get(position1),  "Semester 3"));
            }
        });
        sem4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showHide==0){
                    sem4Days.setVisibility(View.VISIBLE);
                    showHide=1;
                }
                else if(showHide==1){
                    sem4Days.setVisibility(View.GONE);
                    showHide=0;
                }
                sem2Days.setVisibility(View.GONE);
                sem3Days.setVisibility(View.GONE);
                sem1Days.setVisibility(View.GONE);
                sem5Days.setVisibility(View.GONE);
                sem6Days.setVisibility(View.GONE);
                sem7Days.setVisibility(View.GONE);
                sem8Days.setVisibility(View.GONE);
                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, days);
                sem4Days.setAdapter(adapter);
                sem4Days.setOnItemClickListener((parent, view, position1, id) ->
                        ItemClick(days.get(position1),  "Semester 4"));
            }
        });
        sem5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showHide==0){
                    sem5Days.setVisibility(View.VISIBLE);
                    showHide=1;
                }
                else if(showHide==1){
                    sem5Days.setVisibility(View.GONE);
                    showHide=0;
                }
                sem2Days.setVisibility(View.GONE);
                sem3Days.setVisibility(View.GONE);
                sem4Days.setVisibility(View.GONE);
                sem1Days.setVisibility(View.GONE);
                sem6Days.setVisibility(View.GONE);
                sem7Days.setVisibility(View.GONE);
                sem8Days.setVisibility(View.GONE);
                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, days);
                sem5Days.setAdapter(adapter);
                sem5Days.setOnItemClickListener((parent, view, position1, id) ->
                        ItemClick(days.get(position1),  "Semester 5"));
            }
        });
        sem6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showHide==0){
                    sem7Days.setVisibility(View.VISIBLE);
                    showHide=1;
                }
                else if(showHide==1){
                    sem7Days.setVisibility(View.GONE);
                    showHide=0;
                }
                sem2Days.setVisibility(View.GONE);
                sem3Days.setVisibility(View.GONE);
                sem4Days.setVisibility(View.GONE);
                sem5Days.setVisibility(View.GONE);
                sem1Days.setVisibility(View.GONE);
                sem7Days.setVisibility(View.GONE);
                sem8Days.setVisibility(View.GONE);
                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, days);
                sem6Days.setAdapter(adapter);
                sem6Days.setOnItemClickListener((parent, view, position1, id) ->
                        ItemClick(days.get(position1),  "Semester 6"));
            }
        });
        sem7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showHide==0){
                    sem7Days.setVisibility(View.VISIBLE);
                    showHide=1;
                }
                else if(showHide==1){
                    sem7Days.setVisibility(View.GONE);
                    showHide=0;
                }
                sem2Days.setVisibility(View.GONE);
                sem3Days.setVisibility(View.GONE);
                sem4Days.setVisibility(View.GONE);
                sem5Days.setVisibility(View.GONE);
                sem6Days.setVisibility(View.GONE);
                sem1Days.setVisibility(View.GONE);
                sem8Days.setVisibility(View.GONE);
                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, days);
                sem7Days.setAdapter(adapter);
                sem7Days.setOnItemClickListener((parent, view, position1, id) ->
                        ItemClick(days.get(position1),  "Semester 7"));
            }
        });
        sem8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showHide==0){
                    sem8Days.setVisibility(View.VISIBLE);
                    showHide=1;
                }
                else if(showHide==1){
                    sem8Days.setVisibility(View.GONE);
                    showHide=0;
                }
                sem2Days.setVisibility(View.GONE);
                sem3Days.setVisibility(View.GONE);
                sem4Days.setVisibility(View.GONE);
                sem5Days.setVisibility(View.GONE);
                sem6Days.setVisibility(View.GONE);
                sem7Days.setVisibility(View.GONE);
                sem1Days.setVisibility(View.GONE);
                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, days);
                sem8Days.setAdapter(adapter);
                sem8Days.setOnItemClickListener((parent, view, position1, id) ->
                        ItemClick(days.get(position1),  "Semester 8"));
            }
        });
    }

    private void ItemClick(String day, String semVal) {
        SetTimeTable.deptName = deptName;
        SetTimeTable.sem = semVal;
        SetTimeTable.day = day;
        getActivity().startActivity(new Intent(getContext(), SetTimeTable.class));
    }
}
