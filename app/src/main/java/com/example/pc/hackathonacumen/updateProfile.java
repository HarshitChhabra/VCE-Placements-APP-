package com.example.pc.hackathonacumen;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.PrintWriter;
import java.io.StringWriter;

public class updateProfile extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    StorageReference storageReference= FirebaseStorage.getInstance().getReference();
    DatabaseReference myRef = database.getReference("Student");
    String roll = "1602-16-733-014";
    EditText nameet, phoneet, cgpaet, aboutet, achievementet;
    private Uri filepath;
    private Uri downloadUrl=null;
    private String fileName=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        final Context context = this;
        Intent i = getIntent();
        //roll = i.getStringExtra("roll");

        roll=AfterLogin.rollnum;

        myRef.child(roll).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                student s = dataSnapshot.getValue(student.class);
                nameet = findViewById(R.id.nameip);
                nameet.setText(s.name);
                phoneet = findViewById(R.id.phoneip);
                phoneet.setText(s.phonenum);
                cgpaet = findViewById(R.id.cgpaip);
                cgpaet.setText(s.cgpa);
                aboutet = findViewById(R.id.aboutip);
                aboutet.setText(s.aboutme);
                achievementet = findViewById(R.id.achievementip);
                achievementet.setText(s.achievements);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button b = findViewById(R.id.save);

        ((Button)findViewById(R.id.reupload)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                uploadFile();
            }
        });
    }

    void storeChanges(){
        try {
            String about = aboutet.getText().toString(), achievement = achievementet.getText().toString();
            myRef.child(roll).child("name").setValue(nameet.getText().toString());
            myRef.child(roll).child("phonenum").setValue(phoneet.getText().toString());
            myRef.child(roll).child("cgpa").setValue(cgpaet.getText().toString());
            myRef.child(roll).child("aboutme").setValue(about);
            myRef.child(roll).child("achievements").setValue(achievement);
            if(downloadUrl!=null){
                myRef.child(roll).child("resume").setValue(downloadUrl.toString());
            }
            Toast.makeText(updateProfile.this, "Changes Saved", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
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
                String text="Reuploaded resume (optional): "+fileName;
                ((TextView)findViewById(R.id.reuploadname)).setText(text);
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

    private void uploadFile(){

        if(nameet==null || cgpaet==null || phoneet==null ||  aboutet==null || achievementet==null){
            Toast.makeText(updateProfile.this,"Please enter all the details",Toast.LENGTH_SHORT).show();
            return;
        }
        if(nameet.getText().toString().trim().equals("") || cgpaet.getText().toString().trim().equals("") ||  phoneet.getText().toString().trim().equals("") || aboutet.getText().toString().trim().equals("") || achievementet.getText().toString().trim().equals(""))
        {
            Toast.makeText(updateProfile.this,"The fields can't be left empty",Toast.LENGTH_SHORT).show();
            return;
        }

        if(filepath!=null) {
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Uploading..");
            progressDialog.setCancelable(false);
            progressDialog.show();

            final StorageReference fileRef = storageReference.child(roll+"_resume");

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
                        storeChanges();
                        progressDialog.dismiss();
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Failed to upload",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            storeChanges();
        }
    }
}
