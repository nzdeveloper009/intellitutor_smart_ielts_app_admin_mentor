package com.fyp.intellitutor_smartieltsapp.adapter;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.intellitutor_smartieltsapp.R;
import com.fyp.intellitutor_smartieltsapp.mentor.activity.StudentDetailActivity;
import com.fyp.intellitutor_smartieltsapp.model.StudentModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> {
    private Context context;
    public ArrayList<StudentModel> usersArrayList;

    public StudentListAdapter(Context context, ArrayList<StudentModel> usersArrayList) {
        this.context = context;
        this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final StudentModel users = usersArrayList.get(position);
        String username = "Name: " + users.getName();
        String role = "Type: " + users.getRole();
        String gmail = "Mail: " + users.getGmail();

        holder.txtUserName.setText(username);
        holder.txtUserStatus.setText(role);
        holder.txtUserMail.setText(gmail);
        holder.txtUserMail.setOnClickListener(mailto -> {
            OpenMail(users.getGmail());
        });

        holder.imgUserDelete.setOnClickListener(delete -> {
            String setMessage = "Deleting this [" + users.getName() + "] account will result in completely removing and won't be able to access the app ?";
            AlertDialog.Builder alertbox = new AlertDialog.Builder(delete.getRootView().getContext());
            alertbox.setMessage(setMessage);
            alertbox.setTitle("Are you sure");
            alertbox.setIcon(android.R.drawable.ic_dialog_alert);
            alertbox.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User").child(users.getUid());
                    ref.removeValue();
                    ref = FirebaseDatabase.getInstance().getReference("Credentials").child(users.getRole()).child(users.getUid());
                    ref.removeValue();
                    ref = FirebaseDatabase.getInstance().getReference("Group Detail").child(users.getUid());
                    Toast.makeText(context, "User Delete Successfully", Toast.LENGTH_SHORT).show();

                }
            });
            alertbox.show();
        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, StudentDetailActivity.class);
            intent.putExtra("type",users.getRole());
            intent.putExtra("uid",users.getUid());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    private void OpenMail(String gmail) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + gmail));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }


    public void filterList(ArrayList<StudentModel> filteredList){
        usersArrayList = filteredList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserName, txtUserStatus, txtUserMail;
        ImageView imgUserDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtUserStatus = itemView.findViewById(R.id.txtUserStatus);
            imgUserDelete = itemView.findViewById(R.id.imgUserDelete);
            txtUserMail = itemView.findViewById(R.id.txtUserMail);
        }
    }
}
