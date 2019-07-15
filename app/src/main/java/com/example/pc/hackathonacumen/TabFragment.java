package com.example.pc.hackathonacumen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class TabFragment extends Fragment {

    int position;
    private TextView textView;


    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        TabFragment tabFragment = new TabFragment();
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
        if(position==0)
            return inflater.inflate(R.layout.fac_company_list, container, false);
        else
            return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(position==0){

            //fac_company_list and fab addDrive

            FloatingActionButton addDrive=view.findViewById(R.id.addDrive);

            addDrive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View dialogLayout = inflater.inflate(R.layout.add_drive, null);
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setTitle("Add Drive");
                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            final String cmpName=((EditText)dialogLayout.findViewById(R.id.companyInput)).getText().toString().trim();
                            final String password=((EditText)dialogLayout.findViewById(R.id.companyPassword)).getText().toString();
                            final String minCgpa=((EditText)dialogLayout.findViewById(R.id.cmpMinCgpa)).getText().toString();
                            if(cmpName==null || password==null || minCgpa==null || cmpName.trim()=="" || password.trim()=="" || minCgpa.trim()==""){
                                Toast.makeText(view.getContext(), "Invalid Details", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                            String temp=cmpName.replace(' ','-');
                            temp=temp.replace('.','-');
                            final String email=temp.toLowerCase()+"@vasaviplacements.com";
                            FirebaseAuth auth=FirebaseAuth.getInstance();

                            auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                //registrationDialog.dismiss();

                                                Toast.makeText(view.getContext(),"Success!",Toast.LENGTH_SHORT).show();
                                                //Intent intent=new Intent(view.getContext(),AfterLogin.class);
                                                /*intent.putExtra("user","student");
                                                    */
                                                //startActivity(intent);
                                                String cmpName1=cmpName.toLowerCase();
                                                cmpName1=cmpName1.replace(' ','-');
                                                cmpName1=cmpName1.replace('.','-');
                                                FirebaseDatabase.getInstance().getReference("HR").child(cmpName1).child("cgpa").setValue(minCgpa);
                                                String tempEmail=email.toLowerCase();
                                                tempEmail=tempEmail.replace('.','-');
                                                FirebaseDatabase.getInstance().getReference("HRList").child(tempEmail).setValue(cmpName1);
                                                FirebaseDatabase.getInstance().getReference("registeredUsers").child(tempEmail).setValue("hr");
                                                dialog.dismiss();
                                                AlertDialog.Builder emailBuilder=new AlertDialog.Builder(view.getContext());
                                                emailBuilder.setTitle("NOTE:");
                                                emailBuilder.setMessage("The email for the added HR is "+email+" and Password is "+password+" .Please note this and provide to HR for further reference");
                                                emailBuilder.setCancelable(false);
                                                emailBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                emailBuilder.create().show();
                                            } else {
                                                try{
                                                    throw task.getException();
                                                }
                                                catch (FirebaseAuthUserCollisionException e){
                                                    Toast.makeText(view.getContext(),"Company already registered",Toast.LENGTH_SHORT).show();
                                                }
                                                catch (Exception e) {
                                                    //Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                                                    StringWriter sw = new StringWriter();
                                                    e.printStackTrace(new PrintWriter(sw));
                                                    String exceptionAsString = sw.toString();
                                                    Toast.makeText(view.getContext(), exceptionAsString, Toast.LENGTH_SHORT).show();
                                                }
                                                //registrationDialog.dismiss();
                                            }
                                        }
                                    });
                        }
                    });
                    builder.setCancelable(false);
                    builder.setView(dialogLayout);
                    AlertDialog selectTypeDialog = builder.create();
                    selectTypeDialog.show();
                }
            });

            final ListView companyList=(ListView)view.findViewById(R.id.facultyCompanyList);
            final ArrayList<String> companies=new ArrayList<>();
            FirebaseDatabase.getInstance().getReference("HR").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    companies.clear();
                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                        companies.add(dataSnapshot1.getKey());
                    FacultyCompanyAdapter<String> adapter=new FacultyCompanyAdapter<>(view.getContext(),companies);
                    companyList.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            final ListView studentList=(ListView)view.findViewById(R.id.facultyStudentList);
            final ArrayList<String> students = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference("Student").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                        students.add(dataSnapshot1.getKey());
                    FacultyStudentAdapter<String> adapter=new FacultyStudentAdapter<>(view.getContext(),students);
                    studentList.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        //textView = (TextView) view.findViewById(R.id.textView);

        //textView.setText("Fragment " + (position + 1));

    }
}