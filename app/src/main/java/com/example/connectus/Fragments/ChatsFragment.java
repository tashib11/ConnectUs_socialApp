package com.example.connectus.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.connectus.Adapters.AdapterChatlist;
import com.example.connectus.Adapters.AdapterUsers;
import com.example.connectus.Models.ModelChat;
import com.example.connectus.Models.ModelChatlist;
import com.example.connectus.Models.ModelUser;
import com.example.connectus.R;
import com.example.connectus.SignInActivity;
import com.example.connectus.SignUpActivity;
import com.example.connectus.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    FirebaseUser currentUser;
AdapterUsers adapterUsers;
RecyclerView recyclerView;
List<ModelUser> userList;
List<ModelChatlist>chatlistlist;
AdapterChatlist adapterChatlist;
    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firebaseAuth= FirebaseAuth.getInstance();
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView= view.findViewById(R.id.usersRecyclerView);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //init user list
//        userList = new ArrayList<>();
//        getAllUsers();
        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = view.findViewById(R.id.usersRecyclerView);
        chatlistlist= new ArrayList<>();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(currentUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatlistlist.clear();
                    for(DataSnapshot ds: snapshot.getChildren()){
                        ModelChatlist chatlist=ds.getValue(ModelChatlist.class);
                        chatlistlist.add(chatlist);
                    }
                    loadChats();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Intent intent = new Intent(getActivity(), SignUpActivity.class);
            startActivity(intent);
            getActivity().finish();
        }


        return  view;
    }

    private void loadChats() {
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelUser user = ds.getValue(ModelUser.class);
                    for(ModelChatlist chatlist : chatlistlist){
                        if(user.getUserId() != null && user.getUserId().equals(chatlist.getId())){
                            userList.add(user);
                            break;
                        }
                    }
                    adapterChatlist = new AdapterChatlist(getContext(),userList);
                    recyclerView.setAdapter(adapterChatlist);
                    // set last message
                    for(int i = 0 ; i<userList.size(); i++){
                        lastMessage(userList.get(i).getUserId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void lastMessage(String userId) {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chats");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMessage = "default";
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat == null) {
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if (sender == null || receiver == null) {
                        continue;
                    }
                    if ((chat.getReceiver().equals(currentUser.getUid()) && chat.getSender().equals(userId)) ||
                            (chat.getReceiver().equals(userId) && chat.getSender().equals(currentUser.getUid()))) {
                        String chatType = chat.getType();
                        if (chatType != null && chatType.equals("image")) {
                            theLastMessage = "Sent a photo";
                        } else {
                            theLastMessage = chat.getMessage();
                        }
                    }
                }
                adapterChatlist.setLastMessageMap(userId, theLastMessage);
                adapterChatlist.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private  void checkUserStatus(){
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user!=null){

        }else{
            startActivity(new Intent(getActivity(), SignInActivity.class));
            getActivity().finish();
        }
    }


    // for searh bar
    private void searchUsers(String query) {
        //get current user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of datbase named "Users" containing users info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        // get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    //get all serached users except currently signed in user
                    if (!modelUser.getUserId().equals(fUser.getUid())) {
                        if(modelUser.getUserName().toLowerCase().contains(query.toLowerCase()) ||
                                modelUser.getMail().toLowerCase().contains(query.toLowerCase())){
                            userList.add(modelUser);
                        }
                    }
                    //adapter
                    adapterUsers = new AdapterUsers(getActivity(), userList);
                    //refresh adawpter
                    adapterUsers.notifyDataSetChanged();
                    //set adapter to recycler view
                    recyclerView.setAdapter((adapterUsers));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void onCreate(Bundle savedInstanceState){
        setHasOptionsMenu(true); //to show menu option in fragment
        super.onCreate(savedInstanceState);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);

        // Get the SearchView from the menu item
        MenuItem menuItem=menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView=(androidx.appcompat.widget.SearchView) menuItem.getActionView();

        // Set up the query text listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //called when user press search buttpm from  keyboard
                //if search query os npt empty then search
                if(!TextUtils.isEmpty(s.trim())){
                    searchUsers(s);
                }else{
                    //search text empty  ,get all users
//                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                //if search query os npt empty then search
                if(!TextUtils.isEmpty(s.trim())){
                    searchUsers(s);
                }else{
                    //search text empty  ,get all users
//                    getAllUsers();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionItemSelected(MenuItem item){
        int id =item.getItemId();
        if(id==R.id.logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    } // end of code for search bar
}