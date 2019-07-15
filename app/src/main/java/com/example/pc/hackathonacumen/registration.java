package com.example.pc.hackathonacumen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

public class registration extends AppCompatActivity {

    private Uri filepath;
    private String fileName=null,name,email,phonenum,rollnum,aboutme,achievements,password,cgpa;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    StorageReference storageReference=FirebaseStorage.getInstance().getReference();
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    private Uri downloadUrl=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ((Button)findViewById(R.id.resumeButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        ((Button)findViewById(R.id.signupButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInputData();
                uploadFile();
            }
        });
    }

    void getInputData(){

        name=((EditText)findViewById(R.id.name)).getText().toString();
        email=((EditText)findViewById(R.id.email)).getText().toString();
        phonenum=((EditText)findViewById(R.id.phone)).getText().toString();
        rollnum=((EditText)findViewById(R.id.rollnum)).getText().toString();
        aboutme=((EditText)findViewById(R.id.aboutme)).getText().toString();
        achievements=((EditText)findViewById(R.id.achievements)).getText().toString();
        //password=((EditText)findViewById(R.id.password)).getText().toString();
        password="temp123123";
        //cgpa=((EditText)findViewById(R.id.cgpa)).getText().toString();
        cgpa="9";
    }

    public boolean validateRollnum(String username){
        String rollnumPattern="1602-(1[56789]|20)-73[2-7]-(0[0-9][1-9]|0[1-9][0-9]|1[01][0-9]|120|30[1-9]|31[0-9]|32[0-4])";
        return username.matches(rollnumPattern);
    }

    public boolean validatePhoneNum(String phnum){
        //String phonenumPattern="[1-9][0-9]{9}]";
        //return phnum.matches(phonenumPattern);
        return true;
    }

    void registerUser(){

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //registrationDialog.dismiss();

                            Toast.makeText(registration.this,"Success!",Toast.LENGTH_SHORT).show();
                            String tempEmail=email.replace('.','-');
                            FirebaseDatabase.getInstance().getReference("studentData").child(email).setValue(rollnum);
                            Intent intent=new Intent(registration.this,AfterLogin.class);
                            intent.putExtra("user","student");
                            intent.putExtra("rollnum",rollnum);
                            startActivity(intent);

                        } else {
                            try{
                                throw task.getException();
                            }
                            catch (FirebaseAuthUserCollisionException e){
                                Toast.makeText(registration.this,"Email id is already in use",Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e) {
                                //Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                                StringWriter sw = new StringWriter();
                                e.printStackTrace(new PrintWriter(sw));
                                String exceptionAsString = sw.toString();
                                Toast.makeText(getApplicationContext(), exceptionAsString, Toast.LENGTH_SHORT).show();
                            }
                            //registrationDialog.dismiss();
                        }
                    }
                });

    }

    private void uploadFile(){

        if(name==null || email==null || phonenum==null || rollnum==null || aboutme==null || achievements==null || password==null || fileName==null){
            Toast.makeText(registration.this,"Please enter all the details",Toast.LENGTH_SHORT).show();
            return;
        }
        if(name.trim().equals("") || email.trim().equals("") || phonenum.trim().equals("") || rollnum.trim().equals("") || aboutme.trim().equals("") || achievements.trim().equals("") || password.trim().equals("") || fileName.trim().equals(""))
        {
            Toast.makeText(registration.this,"Please enter all the details",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(registration.this,"Wrong email address",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!validateRollnum(rollnum)){
            Toast.makeText(registration.this,"Wrong Roll number",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!validatePhoneNum(phonenum)){
            Toast.makeText(registration.this,"Wrong Phone number",Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length()<=6){
            Toast.makeText(registration.this,"Password length should be minimum 7 characters",Toast.LENGTH_SHORT).show();
            return;
        }

        if(filepath!=null) {
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Uploading..");
            progressDialog.setCancelable(false);
            progressDialog.show();

            final StorageReference fileRef = storageReference.child(rollnum+"_resume");

            UploadTask uploadTask=fileRef.putFile(filepath);

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100.0* taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage((int)progress+"% Uploaded..");
                }
            });
            Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        downloadUrl=task.getResult();
                        Log.i("download",downloadUrl.toString());
                        student std=new student(name,email,phonenum,rollnum,aboutme,achievements,cgpa,downloadUrl.toString());

//                        StringTokenizer temp=new StringTokenizer(fileName,".");
//                        String name=temp.nextToken();
//                        String time=getCurrentTimeUsingDate();
//                        String date=getDate();
//                        EditText noteField=(EditText)(noteDialog.findViewById(R.id.noteField));
//                        String note=noteField.getText().toString();
//                        if(note.trim().isEmpty())
//                            note="";

//                        RowItem circular=new RowItem(name,time,downloadUrl.toString(),note,date,temp.nextToken());
//
//
//                        for(int i=0;i<selectedRecipients.size();i++){
//                            branchDB.child(selectedRecipients.get(i)).child("circulars").child(String.valueOf(System.currentTimeMillis())).setValue(circular);
//                        }
//                        circular.setRecipients(selectedRecipients);
//                        branchDB.child("admin").child("circulars").child(String.valueOf(System.currentTimeMillis())).setValue(circular);
                        database.getReference("Student").child(rollnum).setValue(std);
                       // '.', '#', '$', '[', or ']';
                        String tempEmail=email;
                        tempEmail=email.replace('.','-');
                        tempEmail=tempEmail.replace('#','-');
                        tempEmail=tempEmail.replace('$','-');
                        tempEmail=tempEmail.replace('[','-');
                        tempEmail=tempEmail.replace(']','-');
                        database.getReference("registeredUsers").child(tempEmail).setValue("student");
                        database.getReference("studentData").child(tempEmail).setValue(rollnum);
                        progressDialog.dismiss();
                        Toast.makeText(registration.this,"Registration successful",Toast.LENGTH_SHORT).show();
                        registerUser();
                    }
                    else{
                        //failure
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Failed to upload",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(),"No file selected",Toast.LENGTH_SHORT).show();
        }
    }

    private void showFileChooser(){
        try {
            Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select the pdf"), 1);
        }
        catch(Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Toast.makeText(getApplicationContext(), exceptionAsString, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

                filepath = data.getData();
                fileName = getFileName(filepath);
                ((TextView)findViewById(R.id.selectedFile)).setText(fileName);
                //String displayMessage = "Selected File: " + fileName;
            }
        }
        catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Toast.makeText(getApplicationContext(), exceptionAsString, Toast.LENGTH_SHORT).show();

        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
