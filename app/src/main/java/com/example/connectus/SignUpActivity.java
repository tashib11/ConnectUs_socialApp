package com.example.connectus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.connectus.Models.ModelUser;
import com.example.connectus.databinding.ActivitySignUpBinding;
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

public class SignUpActivity extends AppCompatActivity {////////=+

    ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;// for shwing the loading
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ((Window) window).setStatusBarColor(this.getResources().getColor(R.color.black));
        }

        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        getSupportActionBar().hide();
        progressDialog=new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating Account");// box ar heading
        progressDialog.setMessage("Creating your account");
        auth=FirebaseAuth.getInstance();// email diye sign up ar jonno
        database=FirebaseDatabase.getInstance();// user ar nam password save rakhar jonno
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);

//        binding.btnSignUP.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progressDialog.show();
//                auth.createUserWithEmailAndPassword(binding.etEmail.getText().toString(),
//                        binding.etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        progressDialog.dismiss();//to close the loading
//
//                        if(task.isSuccessful()){
//                            Users user=new Users(binding.userName.getText().toString(),binding.etEmail.getText().toString(),
//                                    binding.etPassword.getText().toString());
//                            String id=task.getResult().getUser().getUid();
//                            database.getReference().child("Users").child(id).setValue(user);
//                            Toast.makeText(SignUpActivity.this, "id created succefully", Toast.LENGTH_SHORT).show();
//                        }
//                        else if(binding.etPassword.getText().toString().length()<6){
//                            Toast.makeText(SignUpActivity.this, "password should at least 6 digits", Toast.LENGTH_SHORT).show();
//                        }
//                        else{
////                            Toast.makeText(SignUpActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
        binding.btnGoogle.setOnClickListener(view1 -> {
            signIn();
        });
        // handle signUp button
        binding.btnSignUP.setOnClickListener(view -> {
            if(binding.userName.getText().toString().isEmpty()){
                binding.userName.setError("Enter your Name");
                return;
            }
            if(binding.etEmail.getText().toString().isEmpty()){
                binding.etEmail.setError("Enter your email");
                return;
            }
            if(binding.etPassword.getText().toString().isEmpty()) {
                binding.etPassword.setError("Enter your password");
                return;
            }

            String userName= binding.userName.getText().toString().trim();
            String email= binding.etEmail.getText().toString();
            String password= binding.etPassword.getText().toString().trim();
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.etEmail.setError("Invalid Email");
                binding.etEmail.setFocusable(true);
            }
            else if(password.length()<6){
                binding.etPassword.setError("password should be at least 6 digits");
                binding.etPassword.setFocusable(true);
            }else {
                signUpUser(userName,email,password);
            }

//            progressDialog.show();
//                auth.createUserWithEmailAndPassword(binding.etEmail.getText().toString(),
//                        binding.etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//
//
//                        if(task.isSuccessful()){
//                            progressDialog.dismiss();//to close the loading
////                            Users user=new Users(binding.userName.getText().toString(),binding.etEmail.getText().toString(),
////                                    binding.etPassword.getText().toString());
//                            FirebaseUser user= auth.getCurrentUser();
//                            String id=task.getResult().getUser().getUid();
////                            database.getReference().child("Users").child(id).setValue(user);
//
//
//
//
//                            Toast.makeText(SignUpActivity.this, "id created successfully", Toast.LENGTH_SHORT).show();
//
//
//                        }
//                        else if(binding.etPassword.getText().toString().length()<6 ){
//                            progressDialog.dismiss();//to close the loading
//        binding.etPassword.setError("password should at least 6 digits");
////                            Toast.makeText(SignUpActivity.this, "password should at least 6 digits", Toast.LENGTH_SHORT).show();
//                        }
//                        else{
//                            progressDialog.dismiss();//to close the loading
////                            Toast.makeText(SignUpActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                            binding.etEmail.setError("Invalid Email format");
//                        }
//                    }
//                });
        });
        binding.alreadyhaveAccount.setOnClickListener(view -> {
            Intent intent=new Intent(SignUpActivity.this,SignInActivity.class);
            startActivity(intent);
        });
    }

    private void signUpUser(String userName, String email, String password) {
        progressDialog.show();
        auth.createUserWithEmailAndPassword(binding.etEmail.getText().toString(),
                binding.etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    FirebaseUser user= auth.getCurrentUser() ;

                    String email= user.getEmail();
                    String uid= user.getUid();
                    String name=binding.userName.getText().toString();
                    String password = binding.etPassword.getText().toString().trim();
                    HashMap<Object,String>hashMap= new HashMap<>();

                    hashMap.put("mail",email);
            hashMap.put("userId",uid);
                    hashMap.put("userName",name);
                    hashMap.put("image","");
                    hashMap.put("phone","");
                    hashMap.put("cover","");
                    hashMap.put("password",password);
                    FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
                    //path to store user
                    DatabaseReference reference= database.getReference("Users");
                    reference.child(uid).setValue(hashMap);

                    Toast.makeText(SignUpActivity.this, "Account Created for "+user.getEmail(), Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(SignUpActivity.this,"Authentication failed",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SignUpActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private  void signIn(){
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
                            Intent intent=new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(SignUpActivity.this, "Sign in with Google", Toast.LENGTH_SHORT).show();
// realtime database a google id ar pic pass nite hobe .so below code

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signUpWithCredential:failure", task.getException());
                            // updateUI(null);
                        }
                    }
                });
    }

}