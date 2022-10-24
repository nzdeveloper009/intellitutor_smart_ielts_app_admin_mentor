package com.fyp.intellitutor_smartieltsapp.admin.activity.add_mentor;

import static com.fyp.intellitutor_smartieltsapp.Utils.Util.GMAIL_PATTERN;
import static com.fyp.intellitutor_smartieltsapp.Utils.Util.PASSWORD_PATTERN;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.intellitutor_smartieltsapp.R;
import com.fyp.intellitutor_smartieltsapp.admin.activity.MainActivity;
import com.fyp.intellitutor_smartieltsapp.model.MentorModel;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterMentorActivity extends AppCompatActivity {

    TextInputLayout nameTIL, ageTIL, qualificationTIL, institudeTIL, userNameTIL, gmailTIL, passwordTIL;
    TextInputEditText nameEt, ageEt, qualificationEt, institudeEt, usernameEt, mailEt, passwordET;
    String nameStr, ageStr, qualificationStr, institudeStr, usernameStr, gmailStr, passwordStr, imageCvUriStr;
    TextView cvTv;
    ImageView cvIV;
    Button submitBtn;
    ProgressBar progressBar;
    private FirebaseAuth auth;
    boolean result;
    private Dialog dialog;
    FirebaseStorage storage;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mentor);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        result = false;
        dialog = new Dialog(RegisterMentorActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCanceledOnTouchOutside(false);

        initView();
    }

    private void initView() {
        nameTIL = findViewById(R.id.nameTIL);
        ageTIL = findViewById(R.id.ageTIL);
        qualificationTIL = findViewById(R.id.qualificationTIL);
        institudeTIL = findViewById(R.id.institudeTIL);
        userNameTIL = findViewById(R.id.userNameTIL);
        gmailTIL = findViewById(R.id.gmailTIL);
        passwordTIL = findViewById(R.id.passwordTIL);

        nameEt = findViewById(R.id.nameEt);
        ageEt = findViewById(R.id.ageEt);
        qualificationEt = findViewById(R.id.qualificationEt);
        institudeEt = findViewById(R.id.institudeEt);
        usernameEt = findViewById(R.id.usernameEt);
        mailEt = findViewById(R.id.mailEt);
        passwordET = findViewById(R.id.passwordET);

        cvTv = findViewById(R.id.cvTv);
        cvIV = findViewById(R.id.cvIV);
        submitBtn = findViewById(R.id.submitBtn);
        progressBar = findViewById(R.id.progressBar);

        initFunctionality();

    }

    private ColorStateList setStrokeColorState() {
        //Color from hex string
        int color = Color.parseColor("#329DB9");

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_focused}, // focused
                new int[]{android.R.attr.state_hovered}, // hovered
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{}  //
        };

        int[] colors = new int[]{
                color,
                color,
                color,
                color
        };

        ColorStateList mColorList = new ColorStateList(states, colors);
        return mColorList;
    }


    private void initFunctionality() {
        // set color of TIL
        nameTIL.setBoxStrokeColorStateList(setStrokeColorState());
        ageTIL.setBoxStrokeColorStateList(setStrokeColorState());
        qualificationTIL.setBoxStrokeColorStateList(setStrokeColorState());
        institudeTIL.setBoxStrokeColorStateList(setStrokeColorState());
        userNameTIL.setBoxStrokeColorStateList(setStrokeColorState());
        gmailTIL.setBoxStrokeColorStateList(setStrokeColorState());
        passwordTIL.setBoxStrokeColorStateList(setStrokeColorState());
        setListener();
    }

    private void setListener() {
        nameEt.addTextChangedListener(new TextFieldValidation(nameEt));
        ageEt.addTextChangedListener(new TextFieldValidation(ageEt));
        mailEt.addTextChangedListener(new TextFieldValidation(mailEt));
        passwordET.addTextChangedListener(new TextFieldValidation(passwordET));

        cvIV.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            ImagePicker.Companion.with(RegisterMentorActivity.this)
                    .crop()                    //Crop image(Optional), Check Customization for more option
                    .compress(1024)            //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });

        submitBtn.setOnClickListener(view -> {
            if (isValidate()) {
                nameStr = String.valueOf(nameEt.getText());
                ageStr = String.valueOf(ageEt.getText());
                qualificationStr = String.valueOf(qualificationEt.getText());
                institudeStr = String.valueOf(institudeEt.getText());
                usernameStr = String.valueOf(usernameEt.getText());
                gmailStr = String.valueOf(mailEt.getText());
                passwordStr = String.valueOf(passwordET.getText());
                gotoCreateAndSaveDatabase();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            progressBar.setVisibility(View.GONE);
            cvTv.setText("NIC is Uploaded");
        }
    }


    public boolean isCheckEmail() {

        auth.fetchSignInMethodsForEmail(String.valueOf(mailEt.getText())).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                Log.d("MyTag", "" + task.getResult().getSignInMethods().size());
                if (task.getResult().getSignInMethods().size() == 0) {
                    // email not existed
                    result = false;
                } else {
                    gmailTIL.setError("Already available");
                    mailEt.requestFocus();
                    result = true;
                    // email existed
                    TastyToast.makeText(getApplicationContext(), "User is already registered with this gmail. Use another Gmail", TastyToast.LENGTH_LONG, TastyToast.INFO).show();
                    return;
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                result = false;
            }
        });

        return result;
    }

    private void gotoCreateAndSaveDatabase() {
        dialog.show();
        // create user in database
        String timestamp = "" + System.currentTimeMillis();
        StorageReference storageReference = storage.getReference().child("NIC").child(timestamp);
        if (imageUri != null) {
            storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageCvUriStr = uri.toString();
                                auth.createUserWithEmailAndPassword(gmailStr, passwordStr)
                                        .addOnCompleteListener(RegisterMentorActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                                    String useridd = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                                    // Update Display Name of User
                                                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                                            .setDisplayName(nameStr)
                                                            .build();
                                                    firebaseUser.updateProfile(profileChangeRequest);

                                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(useridd);


                                                    MentorModel userDetial = new MentorModel(nameStr, institudeStr, usernameStr, gmailStr, passwordStr, "", useridd, "", "mentor");


                                                    databaseReference.setValue(userDetial).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            // Enter User Data into the Firebase Realtime Database.
                                                            MentorModel writeMentorData = new MentorModel(nameStr, ageStr, qualificationStr, institudeStr, usernameStr, gmailStr, passwordStr, imageCvUriStr, "", firebaseUser.getUid(), "", "mentor");
                                                            // Extracting User Reference from Database for "Credentials"
                                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Credentials").child("mentor");

                                                            reference.child(usernameStr).setValue(writeMentorData)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if (task.isSuccessful()) {
                                                                                firebaseUser.sendEmailVerification();
                                                                                auth.signOut();

                                                                                // open user profile after successful registration
                                                                                Intent intent = new Intent(RegisterMentorActivity.this, RegisterMentorActivity2.class);
                                                                                intent.putExtra("username", usernameStr);
                                                                                intent.putExtra("uid", useridd);
                                                                                // To prevent user from returning back to Regisster Activity on pressing back button after registration
                                                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                overridePendingTransition(R.anim.anim_slideup, R.anim.anim_slidebottom);
                                                                                startActivity(intent);
                                                                                TastyToast.makeText(getApplicationContext(), "Mentor Registered successfully. Please Verify your gmail", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                                                                                finish(); // to close Register Activity
                                                                            } else {
                                                                                TastyToast.makeText(getApplicationContext(), "Mentor Registered failed. Please try again.", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                                                                            }
                                                                            // Hide ProgressBar whether user creation is successful or failed
                                                                            dialog.dismiss();
                                                                            // send verification Email
                                                                        }
                                                                    });
                                                        }
                                                    });

                                                } else {
                                                    try {
                                                        throw task.getException();
                                                    } catch (FirebaseAuthUserCollisionException e) {
                                                        TastyToast.makeText(getApplicationContext(), "Mentor is already registered with this gmail. Go back and Use another Gmail", TastyToast.LENGTH_LONG, TastyToast.INFO).show();
                                                    } catch (Exception e) {
                                                        Log.e("MyTag", e.getMessage());
                                                    }
                                                    // Hide ProgressBar whether user creation is successful or failed
                                                    dialog.dismiss();
                                                    TastyToast.makeText(getApplicationContext(), "" + task.getException().getMessage(), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                TastyToast.makeText(getApplicationContext(), "" + e.getMessage(), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                                            }
                                        });
                            }
                        });
                    }
                }
            });
        } else {
            dialog.dismiss();
            TastyToast.makeText(RegisterMentorActivity.this, "NIC is not Uploaded! please select NIC Image", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterMentorActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public static boolean isValidPattern(final String input, final String CHECK_PATTERN) {
        Pattern pattern = Pattern.compile(CHECK_PATTERN);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }


    private boolean isValidate() {
        return (validatePassword() && validateGmail() && validateFullName());
    }

    private boolean validateFullName() {
        if (String.valueOf(nameEt.getText()).isEmpty()) {
            nameTIL.setError("Required Field!");
            nameEt.requestFocus();
            return false;
        } else if (String.valueOf(nameEt.getText()).length() < 4) {
            nameTIL.setError("Please Enter Full Name");
            nameEt.requestFocus();
            return false;
        } else {
            nameTIL.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateGmail() {
        if (String.valueOf(mailEt.getText()).isEmpty()) {
            gmailTIL.setError("Required Field!");
            mailEt.requestFocus();
            return false;
        } else if (!isValidPattern(String.valueOf(mailEt.getText()), GMAIL_PATTERN)) {
            gmailTIL.setError("Gmail is not valid");
            mailEt.requestFocus();
            return false;
        } else if (isCheckEmail()) {
            gmailTIL.setError("Already available");
            mailEt.requestFocus();
            return false;
        } else {
            gmailTIL.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validatePassword() {

        if (String.valueOf(passwordET.getText()).isEmpty()) {
            passwordTIL.setError("Required Field!");
            passwordET.requestFocus();
            return false;
        } else if (String.valueOf(passwordET.getText()).length() < 8) {
            passwordTIL.setError("Password must more then 8");
            passwordET.requestFocus();
            return false;
        } else if (!isValidPattern(String.valueOf(passwordET.getText()), PASSWORD_PATTERN)) {
            passwordTIL.setError("Please enter strong password");
            passwordET.requestFocus();
            return false;
        } else {
            passwordTIL.setErrorEnabled(false);
        }

        return true;
    }


    /**
     * applying text watcher on each text field
     */
    class TextFieldValidation implements TextWatcher {

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
                case R.id.nameEt:
                    validateFullName();
                    break;
                case R.id.mailEt:
                    validateGmail();
                    break;
                case R.id.passwordET:
                    validatePassword();
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }


}