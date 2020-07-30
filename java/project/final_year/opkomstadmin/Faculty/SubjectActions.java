package project.final_year.opkomstadmin.Faculty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import project.final_year.opkomstadmin.R;

public class SubjectActions extends Fragment {
    public static String sub_name;
    TextView selectedSubject;
    Button mark_attendance, give_assignment, student_details, view_attendance, upload_marks;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subject_actions, container, false);
        selectedSubject = view.findViewById(R.id.selectedSubject);
        mark_attendance = view.findViewById(R.id.mark_attendance);
        give_assignment = view.findViewById(R.id.give_assignment);
        student_details = view.findViewById(R.id.student_details);
        view_attendance = view.findViewById(R.id.view_attendance);
        upload_marks = view.findViewById(R.id.upload_marks);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Faculty_Home.flag = 0;
        HomeFragment.flag = 1;

        selectedSubject.setText(sub_name);

        mark_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkAttendance.sub_name = sub_name;
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(((ViewGroup)getView().getParent()).getId(), new MarkAttendance()).
                        addToBackStack("SubjectActions").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            }
        });
        give_assignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGivenAssignments.sub_name = sub_name;
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(((ViewGroup)getView().getParent()).getId(), new ViewGivenAssignments()).
                        addToBackStack("SubjectActions").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            }
        });
        student_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentDetails.sub_name = sub_name;
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(((ViewGroup)getView().getParent()).getId(), new StudentDetails()).
                        addToBackStack("SubjectActions").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            }
        });
        view_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewAttendance.sub_name = sub_name;
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(((ViewGroup)getView().getParent()).getId(), new ViewAttendance()).
                        addToBackStack("SubjectActions").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            }
        });

        upload_marks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadMarks.sub_name = sub_name;
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(((ViewGroup)getView().getParent()).getId(), new UploadMarks()).
                        addToBackStack("SubjectActions").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            }
        });
    }
}
