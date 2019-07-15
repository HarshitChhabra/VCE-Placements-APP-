package com.example.pc.hackathonacumen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.*;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TabFragment55 extends Fragment {

    String roll = "1602-16-733-011";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("HR");


    int position;
    private TextView textView;

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        TabFragment55 tabFragment55 = new TabFragment55();
        tabFragment55.setArguments(bundle);
        return tabFragment55;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roll=AfterLogin.rollnum;
        position = getArguments().getInt("pos");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(position == 0)
            return inflater.inflate(R.layout.fragment_tab55, container, false);
        else
            return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(position == 0)
        {
            myRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.i("check","yolo");
                    LinearLayout l = (LinearLayout) view.findViewById(R.id.lv);
                    l.removeAllViews();
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
//                    Log.i("check",dataSnapshot1.getKey());

                        Context context = view.getContext();
                        TextView tv = new TextView(context);
                        LayoutParams lparams = new LayoutParams(
                                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        tv.setLayoutParams(lparams);
                        final String compName = dataSnapshot1.getKey();
                        CardView card = new CardView(context);
                        LayoutParams params = new LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.WRAP_CONTENT
                        );
                        card.setLayoutParams(params);
                        card.setRadius(9);
                        card.setContentPadding(15, 15, 15, 15);
                        tv = new TextView(context);
                        //tv.setLayoutParams(params);
                        tv.setText(compName);
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextView t=(TextView)v;

                                String company=t.getText().toString();
                                Toast.makeText(getContext(), company, Toast.LENGTH_SHORT).show();

                                Intent intent=new Intent(getActivity(),FacultyAppliedSelected.class);
                                intent.putExtra("company",company);
                                startActivity(intent);
                            }
                        });
                        card.addView(tv);
                        final TextView tv2 = new TextView(context);

                        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
                        p2.setMarginStart(700);
                        tv2.setLayoutParams(p2);

                        myRef.child(compName).child("studentslist").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(roll))
                                {
                                    tv2.setText("Applied");
                                }
                                else
                                {
                                    tv2.setText("Apply");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        tv2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                        if(tv2.getText().toString().equals( "Apply"))
                            tv2.setTextColor(Color.BLUE);
                        else
                            tv2.setTextColor(Color.GREEN);
                        tv2.setGravity(Gravity.END|Gravity.BOTTOM);
                        tv2.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                        tv2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextView t = (TextView) v;
                                if (t.getText() == "Apply") {
                                    t.setText("Applied");
                                    myRef.child(compName).child("studentslist").child(roll).setValue("1");
                                }
                            }
                        });
                        card.addView(tv2);
                        l.addView(card);
                    }
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
    }
}