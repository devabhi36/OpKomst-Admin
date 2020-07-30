package project.final_year.opkomstadmin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import project.final_year.opkomstadmin.Faculty.ClassTestActivity;
import project.final_year.opkomstadmin.Faculty.UploadMarks;
import project.final_year.opkomstadmin.Faculty.ViewGivenAssignments;
import project.final_year.opkomstadmin.Faculty.MarkAttendance;
import project.final_year.opkomstadmin.model.Show_teacher_subjects;
import project.final_year.opkomstadmin.Faculty.StudentDetails;
import project.final_year.opkomstadmin.Faculty.SubjectActions;
import project.final_year.opkomstadmin.Faculty.ViewAttendance;
import project.final_year.opkomstadmin.R;

public class RecyclerViewAdapter1 extends RecyclerView.Adapter<RecyclerViewAdapter1.savedSubjectsVH>{

    ArrayList<Show_teacher_subjects> subjectList;
    Context context;
    int id;

    public RecyclerViewAdapter1(ArrayList<Show_teacher_subjects> subjectList, int id) {
        this.subjectList = subjectList;
        this.id = id;

    }

    @NonNull
    @Override
    public savedSubjectsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_subjects_details, parent, false);
        context = parent.getContext();
        return new savedSubjectsVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull savedSubjectsVH holder, int position) {
        final Show_teacher_subjects show_teacher_subjects = subjectList.get(position);
        holder.subject_name.setText(show_teacher_subjects.getSubject_name());
        holder.dept_name.setText(show_teacher_subjects.getDept_name());
        holder.yearName.setText(show_teacher_subjects.getYear_name());
        holder.semName.setText(show_teacher_subjects.getSemester());
        holder.class_held.setText(show_teacher_subjects.getClass_held());
        holder.assignments_given.setText((show_teacher_subjects.getAssignments_given()));

        holder.linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                SubjectActions myFragment = new SubjectActions();
                SubjectActions.sub_name = show_teacher_subjects.getSubject_name();
                MarkAttendance.deptName = show_teacher_subjects.getDept_name();
                StudentDetails.deptName = show_teacher_subjects.getDept_name();
                ViewGivenAssignments.deptName = show_teacher_subjects.getDept_name();
                ViewAttendance.deptName = show_teacher_subjects.getDept_name();
                UploadMarks.deptName = show_teacher_subjects.getDept_name();
                activity.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(id, myFragment).addToBackStack("HomeFragment")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    class savedSubjectsVH extends RecyclerView.ViewHolder{

        TextView subject_name, dept_name, yearName, semName, class_held, assignments_given;
        LinearLayout linearLayout1;
        public savedSubjectsVH(@NonNull View itemView) {
            super(itemView);

            subject_name = itemView.findViewById(R.id.subject_name);
            dept_name = itemView.findViewById(R.id.deptName);
            yearName = itemView.findViewById(R.id.yearName);
            semName = itemView.findViewById(R.id.semName);
            class_held = itemView.findViewById(R.id.classHeld);
            assignments_given = itemView.findViewById(R.id.assignments_given);
            linearLayout1 = itemView.findViewById(R.id.linerlayout1);
        }
    }
}
