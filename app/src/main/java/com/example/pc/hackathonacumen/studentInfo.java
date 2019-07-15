package com.example.pc.hackathonacumen;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class studentInfo extends AppCompatActivity {

    FirebaseDatabase database=FirebaseDatabase.getInstance();
    TextView name,email,phone,cgpa,achievements,about;
    Button downloadBtn;
    String rno;
    student std;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);
        rno=getIntent().getStringExtra("studentrno");
        setFields();
        setData();
    }

    void setFields(){
        name=(TextView)findViewById(R.id.studentName);
        email=(TextView)findViewById(R.id.studentEmail);
        phone=(TextView)findViewById(R.id.studentPhone);
        cgpa=(TextView)findViewById(R.id.studentCgpa);
        achievements=(TextView)findViewById(R.id.studentAchievements);
        about=(TextView)findViewById(R.id.studentAbout);
        downloadBtn=(Button)findViewById(R.id.downloadResume);
    }

    void setData(){
        Toast.makeText(studentInfo.this,rno,Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance().getReference("Student").child(rno).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.i("testing","yolo");
                std=dataSnapshot.getValue(student.class);
                name.setText(std.name);
                email.setText(std.email);
                phone.setText(std.phonenum);
                cgpa.setText(std.cgpa);
                achievements.setText(std.achievements);
                about.setText(std.aboutme);
                final String downloadurl=std.resume;
                downloadBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent downloadIntent = new Intent(Intent.ACTION_VIEW);
                        downloadIntent.setData(Uri.parse(downloadurl));
                        startActivity(downloadIntent);
                    }
                });
//                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
//                    //Log.i("testing",dataSnapshot1.getKey());
//                    if(dataSnapshot1.getKey().equals("name"))
//                        name.setText(dataSnapshot1.getValue().toString());
//                    if(dataSnapshot1.getKey().equals("email"))
//                        email.setText(dataSnapshot1.getValue().toString());
//                    if(dataSnapshot1.getKey().equals("phonenum"))
//                        phone.setText(dataSnapshot1.getValue().toString());
//                    if(dataSnapshot1.getKey().equals("cgpa"))
//                        cgpa.setText(dataSnapshot1.getValue().toString());
//                    if(dataSnapshot1.getKey().equals("achievements"))
//                        achievements.setText(dataSnapshot1.getValue().toString());
//                    if(dataSnapshot1.getKey().equals("aboutme"))
//                        about.setText(dataSnapshot1.getValue().toString());
//                    final String downloadurl;
//                    if(dataSnapshot1.getKey().equals("resume")) {
//                        downloadurl = dataSnapshot1.getValue().toString();
//                        downloadBtn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent downloadIntent = new Intent(Intent.ACTION_VIEW);
//                                downloadIntent.setData(Uri.parse(downloadurl));
//                                startActivity(downloadIntent);
//                            }
//                        });
//                    }
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
