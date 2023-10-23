package com.example.connectus;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.connectus.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseStorage storage;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    //path where images of user profile and cover will be stored
    String storagePath="Users_Profile_Cover_Imgs/";
    Uri image_uri;
    String profileOrCover;
//    permission constants
//    private  static  final  int CAMERA_REQUEST_CODE = 100;
//    private  static  final  int STORAGE_REQUEST_CODE = 200;
    private  static  final  int GALLERY_REQUEST_CODE = 300;




ProgressDialog progressDialog;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ((Window) window).setStatusBarColor(this.getResources().getColor(R.color.black));
        }

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//       FirebaseDatabase storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase= getInstance();
         user=firebaseAuth.getCurrentUser();
        databaseReference=firebaseDatabase.getReference("Users");
         binding.emailTv.setText(user.getEmail());
        progressDialog=new ProgressDialog(SettingsActivity.this);
      storage = FirebaseStorage.getInstance();
storageReference = storage.getReference();//firebase storage reference
        //init arrays of permissions

        Query query= databaseReference.orderByChild("mail").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren() ){
                    String name = ""+ ds.child("userName").getValue();
                    String email =""+ ds.child("mail").getValue();
                    String phone =""+ ds.child("phone").getValue();
                    String image = ""+ ds.child("image").getValue();
                    String cover= ""+ ds.child("cover").getValue();
                    binding.nameTv.setText(name);
                    binding.emailTv.setText(email);
                    binding.phoneTv.setText(phone);

                    try {
                        Glide.with(SettingsActivity.this)
                                .load(image)
                                .placeholder(R.drawable.avatar)
                                .into(binding.avatarIv);
                    } catch (Exception e) {
                        Glide.with(SettingsActivity.this)
                                .load(R.drawable.avatar)
                                .into(binding.avatarIv);
                    }

                    try{
//                        Picasso.get().load(cover).into(binding.coverIv);
                        Glide.with(SettingsActivity.this) // picasso use korle high pic a app crush kore
                                .load(cover)
                                .into(binding.coverIv);
                    }catch (Exception e){
//                        Picasso.get().load(R.drawable.avatar).into(binding.avatarIv);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfileDialog();
            }
        });




    }

    private void showEditProfileDialog() {
        String[] options ={"Edit Profile Picture", "Edit Cover Photo ","Edit Name","Edit Phone", "Change password"};

        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
           if(i==0){
        progressDialog.setMessage("Updating Profile Picture");
        profileOrCover="image";//changinf profile picture ,make sure to assign same value
        pickImageFromeGallery();
           }else  if(i==1){
               progressDialog.setMessage("Updating Cover Picture");
               profileOrCover="cover";
               pickImageFromeGallery();

           }else  if(i==2){
               progressDialog.setMessage("Updating Name");
               showNamePhoneUpdateDialog("userName");

           }else  if(i==3){
               progressDialog.setMessage("Updating Phone Number");
               showNamePhoneUpdateDialog("phone");
             }else  if(i==4){
               progressDialog.setMessage("Updating Password");
               showChangePasswordDialog();
           }
            }
        });
        builder.create().show();
    }

    private void showChangePasswordDialog() {
View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_password, null);
EditText passwordEt= view.findViewById(R.id.etPassword);
EditText newPasswordEt= view.findViewById(R.id.newPasswordet);
Button updatePasswordBtn = view.findViewById(R.id.updatePasswordBtn);
       final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
        updatePasswordBtn.setOnClickListener(view1 -> {
            String oldPasword= passwordEt.getText().toString();
            String newPasword= newPasswordEt.getText().toString();
            if(TextUtils.isEmpty(oldPasword)){
                Toast.makeText(this, "Enter your current Password", Toast.LENGTH_LONG).show();
                return;
            }
            if(newPasword.length()<6){
                Toast.makeText(this, "Password length must be atleast 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            dialog.dismiss();
            updatePassword(oldPasword,newPasword);
        });

    }

    private void updatePassword(String oldPasword, String newPasword) {
        progressDialog.show();
        FirebaseUser user1=firebaseAuth.getCurrentUser();

        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(),oldPasword);
        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //succesfully authenticated , begin update
                user.updatePassword(newPasword).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();

                        // Update password in Firebase Realtime Database
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                        userRef.child("password").setValue(newPasword);

                        Toast.makeText(SettingsActivity.this, "Password Updated...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SettingsActivity.this, "Wrong current password", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showNamePhoneUpdateDialog(String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update "+key);
        //set layout of dialog

        LinearLayout linearLayout= new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        // add editext
        EditText editText= new EditText(this);
        editText.setHint("Enter "+key);
        linearLayout.addView(editText);
        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value= editText.getText().toString().trim();

                if(!TextUtils.isEmpty(value)){
                    progressDialog.show();
                    HashMap<String,Object> result= new HashMap<>();
                    result.put(key,value);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    Toast.makeText(SettingsActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(SettingsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }else{
                    Toast.makeText(SettingsActivity.this, "Please Enter "+key, Toast.LENGTH_SHORT).show();

                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            progressDialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void pickImageFromeGallery() {
        Intent igallery= new Intent(Intent.ACTION_PICK);
//        igallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        igallery.setType("image/*");
        startActivityForResult(igallery,GALLERY_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==GALLERY_REQUEST_CODE){
                image_uri=data.getData();
                uploadProfileCoverPhoto(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    private void uploadProfileCoverPhoto(final Uri uri) {
        progressDialog.show();
        String filePathAndName = storagePath + ""+ profileOrCover +" "+user.getUid();
        StorageReference storageReference2nd=storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image is uploaded to storage , now get it's url and store in user's database
                        Task<Uri> uriTask= taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        Uri downloadUri= uriTask.getResult();
// check if image is uploaded or not amd url is received
                        if(uriTask.isSuccessful()){
                            //image uploaded
                            // add update url in users database
                            HashMap<String,Object>results= new HashMap<>();
                            results.put(profileOrCover,downloadUri.toString());
                            databaseReference.child(user.getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //url in database of user is added successfully
                                    progressDialog.dismiss();
                                    Toast.makeText(SettingsActivity.this, "Image Updated", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(SettingsActivity.this, "Image Updating failed", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }
    public void onBackPressed(){

            Intent intent= new Intent(SettingsActivity.this, MainActivity.class);
            startActivity( intent);

    }

}