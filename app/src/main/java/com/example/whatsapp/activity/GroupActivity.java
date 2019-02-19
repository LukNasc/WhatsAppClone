package com.example.whatsapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.adapter.AdapterContactsList;
import com.example.whatsapp.adapter.GroupSelectedAdapter;
import com.example.whatsapp.model.Usuario;
import com.example.whatsapp.util.OnClickRecyclerView;
import com.example.whatsapp.util.SettingsFirebase;
import com.example.whatsapp.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {
    private RecyclerView recyclerUserSelected, recyclerUser;
    private AdapterContactsList contactsListAdpter;
    private GroupSelectedAdapter groupSelectedAdapter;
    private List<Usuario> lstMember = new ArrayList<>(),
            lstUserDatabase = new ArrayList<>(),
            lstMemberSelected =new ArrayList<>();
    private ValueEventListener eventListenerMember;
    private DatabaseReference firebaseReference;
    private DatabaseReference database = SettingsFirebase.getFirebaseDatabase();
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo grupo");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerUserSelected = findViewById(R.id.recyclerUsrSelected);
        recyclerUser = findViewById(R.id.recyclerUser);

        firebaseReference = database.child("usuarios");

        contactsListAdpter = new AdapterContactsList(lstMember, this, new OnClickRecyclerView() {
            @Override
            public void onClick(int position) {
                Usuario usuarioSelected = lstMember.get(position);

                lstMember.remove(usuarioSelected);
                contactsListAdpter.notifyDataSetChanged();

                lstMemberSelected.add(usuarioSelected);

                groupSelectedAdapter.notifyDataSetChanged();

                updateMemberToolbar();
            }
        });
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        recyclerUser.setLayoutManager(lm);
        recyclerUser.setHasFixedSize(true);
        recyclerUser.setAdapter(contactsListAdpter);

        groupSelectedAdapter = new GroupSelectedAdapter(getApplicationContext(), lstMemberSelected, new OnClickRecyclerView() {
            @Override
            public void onClick(int position) {
                Usuario usuarioSelected = lstMemberSelected.get(position);
                lstMemberSelected.remove(usuarioSelected);
                groupSelectedAdapter.notifyDataSetChanged();

                lstMember.add(usuarioSelected);
                contactsListAdpter.notifyDataSetChanged();

                updateMemberToolbar();
            }
        });
        RecyclerView.LayoutManager lmH =new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,false);
        recyclerUserSelected.setLayoutManager(lmH);
        recyclerUserSelected.setHasFixedSize(true);
        recyclerUserSelected.setAdapter(groupSelectedAdapter);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(lstMemberSelected.size()>0){
                   Intent i = new Intent(GroupActivity.this, RegisterGroupActivity.class);
                   i.putExtra("member",(Serializable) lstMemberSelected);
                   startActivity(i);
               }else{
                   Toast.makeText(GroupActivity.this, getResources().getString(R.string.select_member_error), Toast.LENGTH_SHORT).show();
               }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseReference.removeEventListener(eventListenerMember);
    }

    @Override
    public void onStart() {
        super.onStart();
        getContactsBd();
    }

    public void getContactsBd() {
        lstMember.clear();
        eventListenerMember = firebaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Usuario usr = data.getValue(Usuario.class);
                    lstUserDatabase.add(usr);
                }

                contactsListAdpter.notifyDataSetChanged();
                lstMember = Util.getContactList(lstMember, lstUserDatabase,false, getApplicationContext());

                updateMemberToolbar();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    public void updateMemberToolbar(){
        toolbar.setSubtitle(lstMemberSelected.size()+" de "+(lstMember.size() + lstMemberSelected.size()) +" selecionados");
    }
}
