package com.example.pc.hackathonacumen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.pc.hackathonacumen.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class arrayAdapterClass<T> extends ArrayAdapter<T>{
    Context context;
    ArrayList arrayList;
    String company;
    HashMap<String,Integer> selected;

    static class ViewHolder{
        //Define your holder here.. All the items in the list
        TextView studentrno;
        Button hirebtn;
    }

    public arrayAdapterClass(Context context, ArrayList<T> arrayList, String company,HashMap<String,Integer> selected){
        super(context,0,arrayList);
        this.context=context;
        this.arrayList=arrayList;
        this.company=company;
        this.selected=selected;
    }

    @Override
    public int getItemViewType(final int position) {

        //FOR RETURNING NUMBER OF VIEW IN LIST VIEW.. 0,1,2 (0th view, 1st view) and so on.. Comment the line below and write your own implementation
        if(selected.get(arrayList.get(position).toString())!=null)
            return 1;
        return 0;
        //return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        //FOR RETURNING TOTAL NUMBER OF VIEWS IN LIST VIEW.. 0,1,2 and so on.. Comment the line below and write your own implementation
        return 2;
        //return super.getViewTypeCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
         ViewHolder holder=new ViewHolder();

        //LayoutInflater mInflateer=(LayoutInflater) getApplicationContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){


            //list_item is the  layout(xml) which defines the view of each list item

            //UNCOMMENT BELOW
            convertView=LayoutInflater.from(context).inflate(R.layout.student_list_item,null,false);


            //INITIALISE VIEW HOLDER ITEMS

            //EXAMPLE:

//            holder.fileName=(TextView) convertView.findViewById(R.id.fileName);
//            holder.date=(TextView) convertView.findViewById(R.id.date);

            holder.studentrno=(TextView) convertView.findViewById(R.id.studentRno);
            holder.hirebtn=(Button) convertView.findViewById(R.id.hireBtn);

            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }

        //Get Each Item. RowItem is the class (java) which defines each list item

        //UNCOMMENT BELOW
        final String rollnum= (String) arrayList.get(position);

        final Button hirebutton=holder.hirebtn;
        //SET VALUES FOR VIEW HOLDER ITEMS

        //EXAMPLE:

//        holder.fileName.setText(circular.getFileName());
//        holder.timestamp.setText(circular.getTimestamp());
//        holder.date.setText(circular.getDate());

        holder.studentrno.setText(rollnum);
        holder.studentrno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,studentInfo.class);
                intent.putExtra("studentrno",rollnum);
                context.startActivity(intent);
                //startActivity)
            }
        });

        if(selected.get(arrayList.get(position).toString())!=null){
            hirebutton.setEnabled(false);
            hirebutton.setText("Hired");
        }
        else {
            holder.hirebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Do you want to hire " + rollnum + " ?");
                    builder.setCancelable(false);
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference("HR").child(company).child("selectedStudents").child(rollnum).setValue("1");

                            hirebutton.setEnabled(false);
                            hirebutton.setText("Hired");
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
            });
        }
        Animation animation= AnimationUtils.loadAnimation(context, R.anim.fade_in);
        convertView.startAnimation(animation);
        return convertView;
    }

        //return super.getView(position, convertView, parent);
    }

