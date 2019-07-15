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

public class FacultyStudentAdapter<T> extends ArrayAdapter<T> {
    Context context;
    ArrayList arrayList;

    static class ViewHolder{
        //Define your holder here.. All the items in the list
        TextView studentName;
    }

    public FacultyStudentAdapter(Context context, ArrayList<T> arrayList){
        super(context,0,arrayList);
        this.context=context;
        this.arrayList=arrayList;
    }

    @Override
    public int getItemViewType(final int position) {

        //FOR RETURNING NUMBER OF VIEW IN LIST VIEW.. 0,1,2 (0th view, 1st view) and so on.. Comment the line below and write your own implementation
//        if(selected.get(arrayList.get(position).toString())!=null)
//            return 1;
//        return 0;
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        //FOR RETURNING TOTAL NUMBER OF VIEWS IN LIST VIEW.. 0,1,2 and so on.. Comment the line below and write your own implementation
        return 1;
        //return super.getViewTypeCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FacultyStudentAdapter.ViewHolder holder=new FacultyStudentAdapter.ViewHolder();

        //LayoutInflater mInflateer=(LayoutInflater) getApplicationContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){


            //list_item is the  layout(xml) which defines the view of each list item

            //UNCOMMENT BELOW
            convertView=LayoutInflater.from(context).inflate(R.layout.faculty_student_item,null,false);


            //INITIALISE VIEW HOLDER ITEMS

            //EXAMPLE:

//            holder.fileName=(TextView) convertView.findViewById(R.id.fileName);
//            holder.date=(TextView) convertView.findViewById(R.id.date);

            holder.studentName=(TextView) convertView.findViewById(R.id.facultyStudentName);


            convertView.setTag(holder);
        }
        else{
            holder=(FacultyStudentAdapter.ViewHolder)convertView.getTag();
        }

        //Get Each Item. RowItem is the class (java) which defines each list item

        //UNCOMMENT BELOW
        final String studentname= (String) arrayList.get(position);

        //SET VALUES FOR VIEW HOLDER ITEMS

        //EXAMPLE:

//        holder.fileName.setText(circular.getFileName());
//        holder.timestamp.setText(circular.getTimestamp());
//        holder.date.setText(circular.getDate());

        holder.studentName.setText(studentname);
        holder.studentName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,studentInfo.class);
                intent.putExtra("studentrno",studentname);
                context.startActivity(intent);
                //startActivity)
            }
        });

        Animation animation= AnimationUtils.loadAnimation(context, R.anim.fade_in);
        convertView.startAnimation(animation);
        return convertView;
    }

    //return super.getView(position, convertView, parent);
}
