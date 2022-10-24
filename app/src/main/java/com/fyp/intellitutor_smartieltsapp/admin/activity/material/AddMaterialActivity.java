package com.fyp.intellitutor_smartieltsapp.admin.activity.material;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.fyp.intellitutor_smartieltsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;
import java.util.Map;

public class AddMaterialActivity extends AppCompatActivity {

    private MaterialCardView addMaterial;
    private LottieAnimationView addMaterialAnim;

    private String selectedMaterialCategory = "";

    private AutoCompleteTextView materialCategory;

    private TextInputEditText shortDescriptionET, longDescriptionET, titleET;
    private String shortDescStr, longDescStr, titleStr;
    private TextInputLayout shortDescTIL, longDescriptionTIL, materialTIL, titleTIL;

    private String checker = "", myUri = "";
    private StorageTask uploadTask;
    private Uri fileUri;
    String timestamp;
    String imageRename;


    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private MaterialButton uploadMaterialBtn;

    private static final String TAG = "MyTag";
    private Dialog dialog;
    String username = "admin";
    String by = "admin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_material);
        runtimePermission();
        dialog = new Dialog(AddMaterialActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_bar);
        dialog.setCanceledOnTouchOutside(false);
        if(getIntent().hasExtra("type")){
            by = "mentor";
            username = getIntent().getStringExtra("username");
        }
        initView();
        SetListeners();
    }

    private void runtimePermission(){
        Dexter.withContext(AddMaterialActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void SetListeners() {
        addMaterial.setOnClickListener(this::PickFile);

        shortDescriptionET.addTextChangedListener(new TextFieldValidation(shortDescriptionET));
        longDescriptionET.addTextChangedListener(new TextFieldValidation(longDescriptionET));

        // https://developer.android.com/reference/android/widget/AdapterView.OnItemClickListener.html
        materialCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMaterialCategory = (String) adapterView.getItemAtPosition(i);
                Toast.makeText(AddMaterialActivity.this, "" + selectedMaterialCategory, Toast.LENGTH_SHORT).show();
            }
        });

        uploadMaterialBtn.setOnClickListener(this::UploadMaterial);
    }

    private void UploadMaterial(View view) {
        shortDescStr = String.valueOf(shortDescriptionET.getText());
        longDescStr = String.valueOf(longDescriptionET.getText());
        titleStr = String.valueOf(titleET.getText());

        if (myUri.isEmpty()) {
            TastyToast.makeText(getApplicationContext(), "No Material Found to Upload....!", TastyToast.LENGTH_SHORT, TastyToast.CONFUSING).show();
        } else {
            if (isValidate()) {
                if (String.valueOf(titleET.getText()).isEmpty()) {
                    materialTIL.setErrorEnabled(false);
                    titleTIL.setError("Please Select Category");
                    titleET.requestFocus();
                    return;
                } else if (selectedMaterialCategory.isEmpty()) {
                    materialTIL.setError("Please Select Category");
                    materialCategory.requestFocus();
                    return;
                } else {
                    materialTIL.setErrorEnabled(false);
                    titleTIL.setErrorEnabled(false);
                    dialog = new Dialog(AddMaterialActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.progress_bar);
                    dialog.setCanceledOnTouchOutside(false);
                    uploadToFirebase();
                }
            }
        }
    }

    private void uploadToFirebase() {
        dialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Material");
        String key = String.valueOf(reference.push().getKey());
        Map fileImageBody = new HashMap();
        String ts = "" + System.currentTimeMillis();

        fileImageBody.put("filePath", myUri);
        fileImageBody.put("name", timestamp);
        fileImageBody.put("type", checker);
        fileImageBody.put("shortDesc", shortDescStr);
        fileImageBody.put("longDesc", longDescStr);
        fileImageBody.put("title", titleStr);
        fileImageBody.put("category", selectedMaterialCategory);
        fileImageBody.put("uploadBy", username);
        fileImageBody.put("key", key);
        fileImageBody.put("timestamp", ts);

        Toast.makeText(getApplicationContext(), "Please Wait, We are uploading a file....", Toast.LENGTH_SHORT).show();



        reference.child(selectedMaterialCategory).child(key).setValue(fileImageBody)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            TastyToast.makeText(getApplicationContext(), "Material Upload Successfully", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                            clearField();
                        } else {
                            TastyToast.makeText(getApplicationContext(), ""+task.getException().getMessage(), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                        }
                        dialog.dismiss();
                    }
                });

    }

    private void clearField() {
        titleET.getText().clear();
        shortDescriptionET.getText().clear();
        longDescriptionET.getText().clear();
        materialCategory.getText().clear();
        myUri = "";
        addMaterialAnim.setAnimation(R.raw.material);
    }

    private void PickFile(View view) {
        CharSequence options[] = new CharSequence[]
                {
                        "Images",
                        "PDF Files",
                        "MS Word Files"
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(AddMaterialActivity.this);
        builder.setTitle("Select the File");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    checker = "image";

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), 438);
                }
                if (i == 1) {
                    checker = "pdf";
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(Intent.createChooser(intent, "Select PDF File"), 438);
                }
                if (i == 2) {
                    checker = "docx";
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/msword");
                    startActivityForResult(Intent.createChooser(intent, "Select MS Word File"), 438);
                }
            }
        });
        builder.show();
    }

    private void initView() {
        addMaterial = findViewById(R.id.addMaterial);
        addMaterialAnim = findViewById(R.id.addMaterialAnim);


        materialCategory = findViewById(R.id.materialCategory);
        shortDescriptionET = findViewById(R.id.shortDescriptionET);
        longDescriptionET = findViewById(R.id.longDescriptionET);
        titleTIL = findViewById(R.id.titleTIL);
        titleET = findViewById(R.id.titleET);
        shortDescTIL = findViewById(R.id.shortDescTIL);
        longDescriptionTIL = findViewById(R.id.longDescriptionTIL);
        materialTIL = findViewById(R.id.materialTIL);

        uploadMaterialBtn = findViewById(R.id.uploadMaterialBtn);

    }

    @Override
    protected void onResume() {
        super.onResume();
        String[] postCategoryStr = getResources().getStringArray(R.array.materials);
        ArrayAdapter<String> postCategoryAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, postCategoryStr);
        materialCategory.setAdapter(postCategoryAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            dialog.show();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            timestamp = "" + System.currentTimeMillis();
            if(checker.equals("image")){
                storageReference.child("Image Files").putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                        task.addOnCompleteListener(uri -> {
                            if (uri.isSuccessful()) {
                                myUri= uri.getResult().toString();
                                addMaterialAnim.setAnimation(R.raw.note);
                                dialog.dismiss();
                            } else {
                                dialog.dismiss();
                                TastyToast.makeText(AddMaterialActivity.this,"Error!! " + taskSnapshot.getError().getMessage(),TastyToast.LENGTH_SHORT,TastyToast.ERROR).show();
                            }

                        });
                    }
                });
            } else {
                storageReference.child("Document Files/"+timestamp+"."+checker).putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                        task.addOnCompleteListener(uri -> {
                            if (uri.isSuccessful()) {
                                myUri= uri.getResult().toString();
                                addMaterialAnim.setAnimation(R.raw.note);
                                dialog.dismiss();
                            } else {
                                dialog.dismiss();
                                TastyToast.makeText(AddMaterialActivity.this,"Error!! " + taskSnapshot.getError().getMessage(),TastyToast.LENGTH_SHORT,TastyToast.ERROR).show();
                            }
                        });
                    }
                });
            }

        }
    }

    private boolean isValidate() {
        return (validateLongDescription() && validateShortDescription());
    }

    /**
     * 1) field must not be empty
     * 2) Short Description length must not be greater than 80
     */
    private boolean validateShortDescription() {
        if (String.valueOf(shortDescriptionET.getText()).isEmpty()) {
            shortDescTIL.setError("Required Field!");
            shortDescriptionET.requestFocus();
            return false;
        } else if (String.valueOf(shortDescriptionET.getText()).length() > 80) {
            shortDescTIL.setError("Short Description can\'t more than 80 character long");
            shortDescriptionET.requestFocus();
            return false;
        } else {
            shortDescTIL.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * 1) field must not be empty
     * 2) Long Description length must not be greater than 4000
     */
    private boolean validateLongDescription() {
        if (String.valueOf(longDescriptionET.getText()).isEmpty()) {
            longDescriptionTIL.setError("Required Field!");
            longDescriptionET.requestFocus();
            return false;
        } else if (String.valueOf(shortDescriptionET.getText()).length() > 4000) {
            longDescriptionTIL.setError("Short Description can\'t more than 4000 character long");
            longDescriptionET.requestFocus();
            return false;
        } else {
            longDescriptionTIL.setErrorEnabled(false);
        }
        return true;
    }


    /**
     * applying text watcher on each text field
     */
    class TextFieldValidation implements TextWatcher {

        /*       TextInputEditText v;

               public TextFieldValidation(TextInputEditText view) {
                   this.v = view;
               }*/
        View v;

        public TextFieldValidation(View view) {
            this.v = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // checking ids of each text field and applying functions accordingly.
            switch (v.getId()) {
                case R.id.shortDescriptionET:
                    validateShortDescription();
                    break;
                case R.id.longDescriptionET:
                    validateLongDescription();
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}