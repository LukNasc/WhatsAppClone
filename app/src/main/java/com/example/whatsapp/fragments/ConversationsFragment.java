package com.example.whatsapp.fragments;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.whatsapp.R;
import com.example.whatsapp.activity.ChatActivity;
import com.example.whatsapp.activity.HomeActivity;
import com.example.whatsapp.adapter.AdapterContactsList;
import com.example.whatsapp.adapter.AdapterConversation;
import com.example.whatsapp.model.Conversations;
import com.example.whatsapp.model.Group;
import com.example.whatsapp.model.Message;
import com.example.whatsapp.model.Usuario;
import com.example.whatsapp.service.JobService;
import com.example.whatsapp.util.OnClickRecyclerView;
import com.example.whatsapp.util.SettingsFirebase;
import com.example.whatsapp.util.UsuarioFirebase;
import com.example.whatsapp.util.Util;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationsFragment extends Fragment implements OnClickRecyclerView {
    private RecyclerView recyclerConversas;
    private List<Conversations> lstConversations = new ArrayList<>();
    private DatabaseReference database = SettingsFirebase.getFirebaseDatabase();
    private ChildEventListener eventListenerConversas;
    private DatabaseReference conversasRef;
    private AdapterConversation adapterConversation;
    private ValueEventListener mEventListener;
    private List<Usuario> lstUserDatabase = new ArrayList<>();

    public ConversationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        lstConversations.clear();
        View view = inflater.inflate(R.layout.conversas_fragment, container, false);

        recyclerConversas = view.findViewById(R.id.recyclerConversas);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
        recyclerConversas.setLayoutManager(lm);
        recyclerConversas.setHasFixedSize(true);
        adapterConversation = new AdapterConversation(lstConversations, getActivity(), this);
        recyclerConversas.setAdapter(adapterConversation);

        String idUser = UsuarioFirebase.getIdUser();
        conversasRef = database.child("conversas").child(idUser);

        return view;
    }

    public void searchConversations(String s) {
        List<Conversations> lstConversationSeacrh = new ArrayList<>();
        for (Conversations con : lstConversations) {

            if (con.getUsuario() != null) {
                String nome = con.getUsuario().getNome().toLowerCase();
                String msg = con.getUltimaMessagem().toLowerCase();
                if (nome.contains(s.toLowerCase()) || msg.contains(s.toLowerCase())) {
                    lstConversationSeacrh.add(con);
                }
            } else {
                String nome = con.getGroup().getNome().toLowerCase();
                String msg = con.getUltimaMessagem().toLowerCase();
                if (nome.contains(s.toLowerCase()) || msg.contains(s.toLowerCase())) {
                    lstConversationSeacrh.add(con);
                }
            }
        }
        adapterConversation = new AdapterConversation(lstConversationSeacrh, getActivity(), this);
        recyclerConversas.setAdapter(adapterConversation);
        adapterConversation.notifyDataSetChanged();
    }
    public void reloadConversations() {
        adapterConversation = new AdapterConversation(lstConversations, getActivity(), this);
        recyclerConversas.setAdapter(adapterConversation);
        adapterConversation.notifyDataSetChanged();
    }
  public void getContactsBd() {
        DatabaseReference firebaseReference = database.child("usuarios");
        mEventListener = firebaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Usuario usr = data.getValue(Usuario.class);
                    lstUserDatabase.add(usr);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    public void getConversations() {
        eventListenerConversas = conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Conversations conversations = dataSnapshot.getValue(Conversations.class);
                List<Usuario> lstContacts = ContactsFragment.getContacts();
                lstConversations.add(conversations);
                getContactsBd();
                adapterConversation.notifyDataSetChanged();
//                Util.showNotification(conversations, getActivity());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Conversations conversations = dataSnapshot.getValue(Conversations.class);
//                Util.showNotification(conversations, getActivity());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        lstConversations.clear();
        getConversations();

    }

    @Override
    public void onStop() {
        super.onStop();
        conversasRef.removeEventListener(eventListenerConversas);
    }

    @Override
    public void onClick(int position) {
        List<Conversations> lstConversationUpdated = adapterConversation.getLstConversations();
        Conversations conversationsSelected = lstConversationUpdated.get(position);


        if (conversationsSelected.getIsGroup().equals("true")) {
            Intent i = new Intent(getActivity(), ChatActivity.class);
            i.putExtra("chatGrupo", conversationsSelected.getGroup());
            startActivity(i);
        } else {
            Intent i = new Intent(getActivity(), ChatActivity.class);
            i.putExtra("chatContato", conversationsSelected.getUsuario());
            startActivity(i);
        }
    }


}
