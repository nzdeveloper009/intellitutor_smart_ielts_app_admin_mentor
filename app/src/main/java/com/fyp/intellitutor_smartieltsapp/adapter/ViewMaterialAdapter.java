package com.fyp.intellitutor_smartieltsapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fyp.intellitutor_smartieltsapp.R;
import com.fyp.intellitutor_smartieltsapp.activity.ViewerActivity;
import com.fyp.intellitutor_smartieltsapp.model.MaterialModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewMaterialAdapter extends RecyclerView.Adapter<ViewMaterialAdapter.ViewHolder> {

    private Context context;
    public ArrayList<MaterialModel> materialArrayList;

    public ViewMaterialAdapter(Context context, ArrayList<MaterialModel> materialArrayList) {
        this.context = context;
        this.materialArrayList = materialArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.material_row_2, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MaterialModel model = materialArrayList.get(position);
        String titleStr = "Title: " + model.getTitle();
        String shortDescStr = "Short Desc:\n" + model.getShortDesc();
        String uploadByStr = model.getUploadBy();

        holder.txtTitle.setText(titleStr);
        holder.txtShortDesc.setText(shortDescStr);
        holder.uploadBy.setText("Upload By: " + uploadByStr);
        if (model.getType().equals("image")) {
            Glide.with(context).load(R.drawable.ic_image).placeholder(R.drawable.logo).into(holder.imgContactUserInfo);
        } else if (model.getType().equals("pdf")) {
            Glide.with(context).load(R.drawable.ic_pdf).placeholder(R.drawable.logo).into(holder.imgContactUserInfo);

        } else {
            Glide.with(context).load(R.drawable.ic_doc).placeholder(R.drawable.logo).into(holder.imgContactUserInfo);
        }

        holder.imgMaterialDelete.setOnClickListener(delete -> {
            String setMessage = "Deleting this [" + model.getTitle() + "] will result in completely removing and won't be able to view?";
            AlertDialog.Builder alertbox = new AlertDialog.Builder(delete.getRootView().getContext());
            alertbox.setMessage(setMessage);
            alertbox.setTitle("Are you sure");
            alertbox.setIcon(android.R.drawable.ic_dialog_alert);
            alertbox.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Material").child(model.getCategory()).child(model.getKey());
                    ref.removeValue();
                    Toast.makeText(context, "Material Delete Successfully", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }
            });
            alertbox.show();
        });

        holder.itemView.setOnClickListener(ChooseNextActivity -> {
            Intent intent = new Intent(context, ViewerActivity.class);
            if (model.getType().equals("image")) {
                intent.putExtra("type", "image");
            } else if (model.getType().equals("pdf")) {
                intent.putExtra("type", "pdf");
            } else {
//                intent.putExtra("type", "msword");
                intent.putExtra("type", "docx");
                intent.putExtra("name",model.getName());
            }
            intent.putExtra("filePath", model.getFilePath());
            intent.putExtra("shortDesc", model.getShortDesc());
            intent.putExtra("longDesc", model.getLongDesc());
            intent.putExtra("uploadBy", model.getUploadBy());
            intent.putExtra("title", model.getTitle());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return materialArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgContactUserInfo;
        ImageView imgMaterialDelete;
        TextView uploadBy, txtShortDesc, txtTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgContactUserInfo = itemView.findViewById(R.id.imgContactUserInfo);
            imgMaterialDelete = itemView.findViewById(R.id.imgMaterialDelete);
            uploadBy = itemView.findViewById(R.id.uploadBy);
            txtShortDesc = itemView.findViewById(R.id.txtShortDesc);
            txtTitle = itemView.findViewById(R.id.txtTitle);
        }
    }
}
