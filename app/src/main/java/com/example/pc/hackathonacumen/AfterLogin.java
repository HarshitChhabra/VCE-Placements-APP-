package com.example.pc.hackathonacumen;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AfterLogin extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    String user;
    static String rollnum;
    FirebaseDatabase database=FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user=getIntent().getStringExtra("user");
        if (getIntent().getStringExtra("user").equals("faculty")) {
            setContentView(R.layout.activity_after_login);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            viewPager = (ViewPager) findViewById(R.id.viewpager);
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(adapter);

            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
        }
        else if(getIntent().getStringExtra("user").equals("student")){
            setContentView(R.layout.activity_after_login_student);

            toolbar = (Toolbar) findViewById(R.id.toolbar55);
            setSupportActionBar(toolbar);

            rollnum=getIntent().getStringExtra("rollnum");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            viewPager = (ViewPager) findViewById(R.id.viewpager55);
            ViewPagerAdapter55 adapter = new ViewPagerAdapter55(getSupportFragmentManager());
            viewPager.setAdapter(adapter);

            tabLayout = (TabLayout) findViewById(R.id.tabs55);
            tabLayout.setupWithViewPager(viewPager);
        }
        else{
            setContentView(R.layout.hr_view);


            ((Button)findViewById(R.id.dummySignOut)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }
            });

            Log.i("checkafter",getIntent().getStringExtra("company"));
            final ListView studentList=(ListView)findViewById(R.id.studentList);
            final String company=getIntent().getStringExtra("company");
            final ArrayList<String> appliedStudentRnos=new ArrayList<String>();
            ((TextView)findViewById(R.id.noApplText)).setVisibility(View.INVISIBLE);

            database.getReference("HR").child(company).child("studentslist").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                        appliedStudentRnos.add(dataSnapshot1.getKey());
                    }
                    if(appliedStudentRnos.size()==0)
                        ((TextView)findViewById(R.id.noApplText)).setVisibility(View.VISIBLE);
                   final HashMap<String,Integer> selected=new HashMap<>();
                    database.getReference("HR").child(company).child("selectedStudents").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                                if(dataSnapshot1.getKey()!=null)
                                    selected.put(dataSnapshot1.getKey().toString(),1);
                            arrayAdapterClass<String> adapter=new arrayAdapterClass<>(AfterLogin.this,appliedStudentRnos,company,selected);
                            studentList.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(user.equals("student"))
            getMenuInflater().inflate(R.menu.navigation_menu55,menu);
        else
            getMenuInflater().inflate(R.menu.navigation_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_profile)
        {
            Intent intent=new Intent(AfterLogin.this,studentInfo.class);
            intent.putExtra("studentrno",getIntent().getStringExtra("rollnum"));
            startActivity(intent);
        }
         if (item.getItemId() == R.id.nav_logout)
        {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.nav_edit)
        {
            Intent i = new Intent(AfterLogin.this, updateProfile.class);
            i.putExtra("roll",getIntent().getStringExtra("rollnum"));
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}
