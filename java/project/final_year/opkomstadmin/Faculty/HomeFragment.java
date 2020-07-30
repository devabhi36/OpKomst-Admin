package project.final_year.opkomstadmin.Faculty;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import project.final_year.opkomstadmin.R;
import project.final_year.opkomstadmin.adapter.RecyclerViewAdapter1;
import project.final_year.opkomstadmin.model.Show_teacher_subjects;

public class HomeFragment extends Fragment {
    TextView wName;
    public static ArrayList<Show_teacher_subjects> saved_subjects;
    public static String name;
    RecyclerView savedSubjects;
    ProgressDialog progressDialog;
    public static int flag;
    int time = 3000, count=0;
    SwipeRefreshLayout swipeRefreshLayout;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        savedSubjects = (RecyclerView)root.findViewById(R.id.your_subjects);
        swipeRefreshLayout = root.findViewById(R.id.refreshLayout);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait..\nData is being fetched!");
        progressDialog.setCancelable(false);
        if(flag==0)
        progressDialog.show();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Faculty_Home.flag = 1;
        wName = view.findViewById(R.id.welcomeName);
        wName.setText("Welcome "+name+"!");
        initRecyclerView();
        if(flag==0){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                getFragmentManager().beginTransaction().detach(HomeFragment.this).attach(HomeFragment.this).commit();
                flag=1;
            }
        }, time);
        }

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.red), getResources().getColor(R.color.blue),
                getResources().getColor(R.color.green), getResources().getColor(R.color.yellow));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                int container = ((ViewGroup)getView().getParent()).getId();
                RecyclerViewAdapter1 recyclerViewAdapter1 = new RecyclerViewAdapter1(saved_subjects, container);
                LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
                savedSubjects.setLayoutManager(layoutManager);
                savedSubjects.setAdapter(recyclerViewAdapter1);
                count++;
                if (count>2)
                    Toast.makeText(getContext(), "Slow Internet Connection\nTry re-opening the app with active internet!", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, time);
            }
        });
    }

    private void initRecyclerView() {
        int container = ((ViewGroup)getView().getParent()).getId();
        RecyclerViewAdapter1 recyclerViewAdapter1 = new RecyclerViewAdapter1(saved_subjects, container);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
        savedSubjects.setLayoutManager(layoutManager);
        savedSubjects.setAdapter(recyclerViewAdapter1);
    }
}
