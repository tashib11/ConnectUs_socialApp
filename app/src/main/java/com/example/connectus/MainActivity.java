package com.example.connectus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.connectus.Adapters.FragmentsAdapter;
import com.example.connectus.Fragments.ChatsFragment;
import com.example.connectus.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    ActivityMainBinding binding;
    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // for  status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ((Window) window).setStatusBarColor(this.getResources().getColor(R.color.black));
        }

//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance();

        drawerLayout=findViewById(R.id.drawerLayout);
        navigationView=findViewById((R.id.nav_drawer));
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        toolbar=findViewById((R.id.toolbar));
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle =new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.OpenDrawer,R.string.CloseDrawer);
                //activity ,drawer,toolbar and 2flex-open-close reference push kora lagbe in ActionBarDrawerToggle()
        //flex ar value int dite hobe but int ta string hishebe thakbe .so go vlues->string a jao
        drawerLayout.addDrawerListener(toggle);//for sliding , mane shob vabei drawer use kora jabe
        toggle.syncState();// open hole bolbe ji open vai , close hole state ta hobe close

        if(auth.getCurrentUser()==null){
            Intent intent=new Intent(MainActivity.this,SignInActivity.class);
            startActivity(intent);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id= item.getItemId();
                if(id==R.id.logout){
             auth.signOut();
            Intent intent=new Intent(MainActivity.this,SignInActivity.class);
                startActivity(intent);
                }
                else if(id==R.id.settings){
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "settings", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
        binding.viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id= item.getItemId();
                if(id==R.id.bchat){
                    binding.viewPager.setCurrentItem(0);
//                    getActionBar().setTitle("Chats");
                    Toast.makeText(MainActivity.this, "chat", Toast.LENGTH_SHORT).show();
//                    loadFrag(new chatFragment());
                    return  true;
                }
          else   if(id==R.id.bpeople){
                    binding.viewPager.setCurrentItem(1);

                    Toast.makeText(MainActivity.this, "people", Toast.LENGTH_SHORT).show();
//                    loadFrag(new peopleFragment());
                    return  true;
                }
                return false;
            }
        });

//update token

    }



  public void onBackPressed() {
      if(drawerLayout.isDrawerOpen(GravityCompat.START)){
          drawerLayout.closeDrawer(GravityCompat.START);
          // if u pressed back button then drawer will be off
      }else {
//          super.onBackPressed();// if already drwer is off then by pressing backbutton ,app exit
          finishAffinity();
          finish();
      }

  }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }


}