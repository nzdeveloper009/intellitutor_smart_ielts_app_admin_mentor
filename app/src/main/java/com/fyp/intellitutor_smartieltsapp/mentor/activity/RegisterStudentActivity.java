package com.fyp.intellitutor_smartieltsapp.mentor.activity;

import static com.fyp.intellitutor_smartieltsapp.Utils.Util.GMAIL_PATTERN;
import static com.fyp.intellitutor_smartieltsapp.Utils.Util.PASSWORD_PATTERN;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.intellitutor_smartieltsapp.R;
import com.fyp.intellitutor_smartieltsapp.Utils.Util;
import com.fyp.intellitutor_smartieltsapp.model.StudentModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterStudentActivity extends AppCompatActivity {

    TextInputLayout nameTIL, userNameTIL, gmailTIL, passwordTIL;
    TextInputEditText nameEt, usernameEt, mailEt, passwordET;
    String nameStr, usernameStr, gmailStr, passwordStr, packageName, mentorName;
    Button submitBtn;
    private Dialog dialog;
    FirebaseAuth auth;
    boolean result;
    Util util;
    String myUid;
    String countStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);
        util = new Util();
        myUid = util.getUID();
        dialog = new Dialog(RegisterStudentActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCanceledOnTouchOutside(false);

        auth = FirebaseAuth.getInstance();
        result = false;
        initView();

        DatabaseReference count = FirebaseDatabase.getInstance().getReference("User").child(myUid);
        count.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    countStudents = snapshot.child("countStudents").getValue(String.class);
                    packageName = snapshot.child("packageName").getValue(String.class);
                    mentorName = snapshot.child("username").getValue(String.class);
                    if (packageName.equals("basic") && countStudents.equals("10")) {
                        TastyToast.makeText(RegisterStudentActivity.this, "Package is fulfil now, Please Update Package, Before Proceed", TastyToast.LENGTH_LONG, TastyToast.DEFAULT).show();
                        submitBtn.setEnabled(false);
                        return;
                    } else if (packageName.equals("pro") && countStudents.equals("50")) {
                        TastyToast.makeText(RegisterStudentActivity.this, "Package is fulfil now, Please Update Package, Before Proceed", TastyToast.LENGTH_LONG, TastyToast.DEFAULT).show();
                        submitBtn.setEnabled(false);
                        return;
                    } else {
                        submitBtn.setEnabled(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView() {
        nameTIL = findViewById(R.id.nameTIL);
        userNameTIL = findViewById(R.id.userNameTIL);
        gmailTIL = findViewById(R.id.gmailTIL);
        passwordTIL = findViewById(R.id.passwordTIL);

        nameEt = findViewById(R.id.nameEt);
        usernameEt = findViewById(R.id.usernameEt);
        mailEt = findViewById(R.id.mailEt);
        passwordET = findViewById(R.id.passwordET);

        submitBtn = findViewById(R.id.submitBtn);

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
        userNameTIL.setBoxStrokeColorStateList(setStrokeColorState());
        gmailTIL.setBoxStrokeColorStateList(setStrokeColorState());
        passwordTIL.setBoxStrokeColorStateList(setStrokeColorState());
        setListener();
    }

    private void setListener() {
        nameEt.addTextChangedListener(new TextFieldValidation(nameEt));
        mailEt.addTextChangedListener(new TextFieldValidation(mailEt));
        passwordET.addTextChangedListener(new TextFieldValidation(passwordET));

        submitBtn.setOnClickListener(view -> {
            if (isValidate()) {
                nameStr = String.valueOf(nameEt.getText());
                usernameStr = String.valueOf(usernameEt.getText());
                gmailStr = String.valueOf(mailEt.getText());
                passwordStr = String.valueOf(passwordET.getText());
                gotoCreateAndSaveDatabase();
            }

        });
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
        auth.createUserWithEmailAndPassword(gmailStr, passwordStr)
                .addOnCompleteListener(RegisterStudentActivity.this, new OnCompleteListener<AuthResult>() {
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

                            StudentModel role = new StudentModel(nameStr, usernameStr, gmailStr, passwordStr, useridd, "student", myUid, "");

                            databaseReference.setValue(role).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // Enter User Data into the Firebase Realtime Database.
                                    // Extracting User Reference from Database for "Credentials"
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Credentials").child("student");
                                    StudentModel studentDetial = new StudentModel(nameStr, usernameStr, gmailStr, passwordStr, useridd, "student", myUid, "");
                                    reference.child(usernameStr)
                                            .setValue(studentDetial)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        int oldCounter = Integer.parseInt(countStudents);
                                                        int newCounter = oldCounter + 1;
                                                        String counterStr = String.valueOf(newCounter);
                                                        HashMap<String, Object> counter = new HashMap<>();
                                                        counter.put("countStudents", counterStr);
                                                        DatabaseReference updateCounter = FirebaseDatabase.getInstance().getReference("Credentials").child("mentor").child(mentorName);
                                                        updateCounter.updateChildren(counter).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()) {
                                                                    DatabaseReference updateCount = FirebaseDatabase.getInstance().getReference("User").child(myUid);
                                                                    updateCount.updateChildren(counter).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                DatabaseReference mentorStudent = FirebaseDatabase.getInstance().getReference("mentorStudents");
                                                                                HashMap<String, Object> data = new HashMap<>();
                                                                                data.put("name", nameStr);
                                                                                data.put("username", usernameStr);
                                                                                data.put("gmail", gmailStr);
                                                                                data.put("studentUid", useridd);
                                                                                data.put("role", role);
                                                                                data.put("online", "");

                                                                                mentorStudent.child(myUid).child(useridd).setValue(data)
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    firebaseUser.sendEmailVerification();

                                                                                                    // open user profile after successful registration
                                                                                                    Intent intent = new Intent(RegisterStudentActivity.this, MentorDashboardActivity.class);
                                                                                                    // To prevent user from returning back to Regisster Activity on pressing back button after registration
                                                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                                    overridePendingTransition(R.anim.anim_slideup, R.anim.anim_slidebottom);
                                                                                                    startActivity(intent);
                                                                                                    TastyToast.makeText(getApplicationContext(), "Student Registered successfully. Please Verify your gmail", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                                                                                                    finish(); // to close Register Activity
                                                                                                } else {
                                                                                                    TastyToast.makeText(getApplicationContext(), "Student Registered failed. Please try again.", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                                                                                                }
                                                                                                // Hide ProgressBar whether user creation is successful or failed
                                                                                                dialog.dismiss();
                                                                                                // send verification Email
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });

                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                }
                            });

                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                TastyToast.makeText(getApplicationContext(), "Student is already registered with this gmail. Go back and Use another Gmail", TastyToast.LENGTH_LONG, TastyToast.INFO).show();
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