package com.example.pc.hackathonacumen;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AppliedSelecteed_tabFragment extends Fragment {

        int position;
        private TextView textView;

        public static Fragment getInstance(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt("pos", position);
            AppliedSelecteed_tabFragment tabFragment = new AppliedSelecteed_tabFragment();
            tabFragment.setArguments(bundle);
            return tabFragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            position = getArguments().getInt("pos");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.applied_selected_list, container, false);
        }

        @Override
        public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            final ListView list = (ListView) view.findViewById(R.id.studentRnoDisp);
            if(position==0){
                final ArrayList<String> students=new ArrayList<>();
                String company=FacultyAppliedSelected.companyName;
                FirebaseDatabase.getInstance().getReference("HR").child(company).child("studentslist").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                            students.add(dataSnapshot1.getKey());
                            Log.i("check1",dataSnapshot1.getKey());
                        }
                        FacultyStudentAdapter<String> adapter=new FacultyStudentAdapter<>(view.getContext(),students);
                        list.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else{
                final ArrayList<String> students=new ArrayList<>();
                String company=FacultyAppliedSelected.companyName;
                FirebaseDatabase.getInstance().getReference("HR").child(company).child("selectedStudents").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                            students.add(dataSnapshot1.getKey());
                            Log.i("check2",dataSnapshot1.getKey());
                        }
                        FacultyStudentAdapter<String> adapter=new FacultyStudentAdapter<>(view.getContext(),students);
                        list.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        }
    }


