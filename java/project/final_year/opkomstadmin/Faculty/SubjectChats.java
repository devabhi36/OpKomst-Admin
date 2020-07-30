package project.final_year.opkomstadmin.Faculty;

import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import project.final_year.opkomstadmin.R;
import project.final_year.opkomstadmin.adapter.Subject_chat_adapter;

public class SubjectChats extends Fragment {

    public static ArrayList<String> subjects, subject_codes;
    RecyclerView listSubjects;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_subjects_chats, container, false);
        listSubjects = (RecyclerView)root.findViewById(R.id.listSubjects);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getSupportFragmentManager().popBackStack("HomeFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Faculty_Home.flag = 0;
        HomeFragment.flag = 1;
//        Log.e("size", subjects.get(0)+" "+subjects.get(1));
        initRecyclerView();
    }

    private void initRecyclerView() {
        Log.e("this", "called");
        //int container = ((ViewGroup)getView().getParent()).getId();
        Subject_chat_adapter subject_chat_adapter = new Subject_chat_adapter(subjects, subject_codes);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
        listSubjects.setLayoutManager(layoutManager);
        listSubjects.setAdapter(subject_chat_adapter);
    }
}
