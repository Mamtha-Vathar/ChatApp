package com.example.chatapp.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment{
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    private List<User> display_chat;
    FirebaseUser fuser;
    DatabaseReference reference;

    private List<String> userslist;

    public ChatsFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view= inflater.inflate(R.layout.activity_chats_fragment,container,false);
       recyclerView=view.findViewById(R.id.recycler_view_chats);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       fuser= FirebaseAuth.getInstance().getCurrentUser();
       userslist=new ArrayList<String>();
       reference= FirebaseDatabase.getInstance().getReference("Chats");

       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               userslist.clear();
               for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                   Chat chat=snapshot.getValue(Chat.class);
                   if(chat.getSender().equals(fuser.getUid())){
                       userslist.add(chat.getReceiver());
                   }
                   if(chat.getReceiver().equals(fuser.getUid())){
                       userslist.add(chat.getSender());
                   }
               }
               readChats();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
       return view;
    }
    private void readChats(){
        mUsers=new ArrayList<>();
        display_chat=new ArrayList<>();
        reference =FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                display_chat.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user=snapshot.getValue(User.class);
                    for(String name:userslist){
                        if(user.getId().equals(name)){
                            display_chat.add(user);
                            break;
                        }
                    }
                }
                userAdapter=new UserAdapter(getContext(),display_chat,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

