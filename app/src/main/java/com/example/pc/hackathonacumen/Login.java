package com.example.pc.hackathonacumen;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.Console;
import java.util.Arrays;
import java.util.List;

public class Login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    String username,password,currentUser="";
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginBtn=(Button)findViewById(R.id.button2);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((TextInputEditText)findViewById(R.id.username)).getText()!=null)
                    username=((TextInputEditText)findViewById(R.id.username)).getText().toString();
                else
                    username="";
                if(((TextInputEditText)findViewById(R.id.password)).getText()!=null)
                    password=((TextInputEditText)findViewById(R.id.password)).getText().toString();
                else
                    password="";
                loginUser(username,password);
            }
        });

        Button regBtn=(Button)findViewById(R.id.button);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,registration.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        updateUI(user);


    }

    void updateUI(FirebaseUser user){
        if(user!=null){
            Log.i("check","user not null");
            final Intent intent=new Intent(this,AfterLogin.class);
            final String email=user.getEmail();

            database.getReference("registeredUsers").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String tempEmail=email;
                    tempEmail=email.replace('.','-');
                    tempEmail=tempEmail.replace('#','-');
                    tempEmail=tempEmail.replace('$','-');
                    tempEmail=tempEmail.replace('[','-');
                    tempEmail=tempEmail.replace(']','-');
                    final String finEmail=tempEmail;
                    for(DataSnapshot item:dataSnapshot.getChildren()){
                        if(item.getKey()!=null && item.getKey().equals(tempEmail)){
                            currentUser=item.getValue().toString();
                            intent.putExtra("user",currentUser);
                            if(currentUser.equals("hr")){
                                database.getReference("HRList").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                            Log.i("check",dataSnapshot1.getKey());
                                            Log.i("check1",finEmail);
                                            if(dataSnapshot1.getKey().equals(finEmail)){
                                                intent.putExtra("company",dataSnapshot1.getValue().toString());
                                                startActivity(intent);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else if(currentUser.equals("student")){
                                String tempEmail2=email.replace('.','-');
                                database.getReference("studentData").child(tempEmail2).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            //Log.i("check",dataSnapshot.getKey());
                                            //Log.i("check1",finEmail);
                                            intent.putExtra("rollnum",dataSnapshot.getValue().toString());
                                            startActivity(intent);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else {
                                startActivity(intent);
                                return;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //To redirect to next screen after login

        }
    }

    void loginUser(String email,String password){

        if(email==null || password==null || email.trim().equals("") || password.trim().equals("")){
            Toast.makeText(Login.this,"Please enter the required details",Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email.toLowerCase().trim(), password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

}
