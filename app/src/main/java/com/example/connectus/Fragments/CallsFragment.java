package com.example.connectus.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.connectus.R;
import com.example.connectus.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CallsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CallsFragment extends Fragment {
FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firebaseAuth=FirebaseAuth.getInstance();
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_calls, container, false);
        return  view;
    }
    private  void checkUserStatus(){
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user!=null){

        }else{
            startActivity(new Intent(getActivity(), SignInActivity.class));
            getActivity().finish();
        }
    }

    public void onCreate(Bundle savedInstanceState){
        setHasOptionsMenu(true); //to show menu option in fragment
        super.onCreate(savedInstanceState);
    }

    public  void onCreaOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    public boolean onOptionItemSelected(MenuItem item){
        int id =item.getItemId();
        if(id==R.id.logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}