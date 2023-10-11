package com.example.connectus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.connectus.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {
ActivitySignInBinding binding;
    private FirebaseAuth auth;
    ProgressDialog progressDialog;// for shwing the loading
    GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ((Window) window).setStatusBarColor(this.getResources().getColor(R.color.black));
        }

        binding= ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(SignInActivity.this);
        progressDialog.setTitle("Login Account");// box ar heading
        progressDialog.setMessage("logging in your account");
    database=FirebaseDatabase.getInstance();
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail().build();
        mGoogleSignInClient=GoogleSignIn.getClient(this,gso);


        binding.btnSignIN.setOnClickListener(view -> {
            if(binding.etEmail.getText().toString().isEmpty()){
                binding.etEmail.setError("Enter your email");
                return;
            }
            if(binding.etPassword.getText().toString().isEmpty()){
                binding.etPassword.setError("Enter your password");
                return;
            }
            progressDialog.show();
            auth.signInWithEmailAndPassword(binding.etEmail.getText().toString(),
                    binding.etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                        Intent intent=new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(SignInActivity.this, "email or password invalid", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        });

        //recover password
        binding.recoverPassTv.setOnClickListener(view -> {
            showRecoverPasswordDialog();
        });

        binding.btnGoogle.setOnClickListener(view1 -> {
            signIn();
        });
        // age thekei login kora thakle direct vitore cholejabe ba main jinish dekhabe

        binding.btnCreateAcc.setOnClickListener(view -> {
            Intent intent=new Intent(SignInActivity.this,SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder((this));
        builder.setTitle("Recover Password");
        LinearLayout linearLayout=new LinearLayout(this);
     final   EditText emailEt = new EditText(this);
     emailEt.setHint("Email");
     emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

     emailEt.setMinEms(16);

     linearLayout.addView(emailEt);
     linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);
        //buttons recover
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //input email
                String  email= emailEt.getText().toString().trim();
                beginRecovery(email);
            }
        });
        //buttons cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        //show dialog
        builder.create().show();
    }

    private void beginRecovery(String email) {
//        progressDialog.setTitle("Login Account");// box ar heading
        progressDialog.setMessage("Sending email...");
        progressDialog.show();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(SignInActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SignInActivity.this, "Failed...", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SignInActivity.this,"+e.getMessage",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private  void signIn(){
        progressDialog.show();

        Intent signInIntent= mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,65);
    }


    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialog.dismiss();

        // Check condition
        if (requestCode == 65) {

            // When request code is equal to 100 initialize task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            // check condition
//            if (task.isSuccessful()) {
//                // When google sign in successful initialize string
//                String s = "Google sign in successful";
//                // Display Toast
//                displayToast(s);
                // Initialize sign in account
                try {

                    // Initialize sign in account
                    GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                    Log.d("TAG","firebaseAuthWithGoogle:" + googleSignInAccount.getId());
                    firebaseAuthWithGoogle(googleSignInAccount.getIdToken());

                            } catch (ApiException ex) {
//                    throw new RuntimeException(ex);
                    Log.w("TAG","Google sign in failed",ex);

                }

}
}


        // Got an ID token from Google. Use it to authenticate
        // with Firebase.
    private  void firebaseAuthWithGoogle(String idToken) {
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();

                            //if user is signing in first time then get and show user infp from google account
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {

                                                String email= user.getEmail();
                            String uid= user.getUid();
                            String name=user.getDisplayName();
                            HashMap<Object,String> hashMap= new HashMap<>();

                            hashMap.put("mail",email);
                            hashMap.put("userId",uid);
                            hashMap.put("userName",name);
                                hashMap.put("onlineStatus","online");
                            hashMap.put("image","");
                            hashMap.put("cover","");
                            hashMap.put("phone","");
                            FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
                            //path to store user
                            DatabaseReference reference= database.getReference("Users");
                            reference.child(uid).setValue(hashMap);

                        }
                            // updateUI(user);
                            Intent intent=new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(SignInActivity.this, "Signed in with Google", Toast.LENGTH_SHORT).show();
// realtime database a google id ar pic pass nite hobe .so below code

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            // updateUI(null);
                        }
                    }
                });
    }

}