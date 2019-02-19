package com.example.whatsapp.fragments;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.activity.ChatActivity;
import com.example.whatsapp.activity.GroupActivity;
import com.example.whatsapp.adapter.AdapterContactsList;
import com.example.whatsapp.adapter.AdapterConversation;
import com.example.whatsapp.model.Conversations;
import com.example.whatsapp.model.Usuario;
import com.example.whatsapp.util.Action;
import com.example.whatsapp.util.Base64Custom;
import com.example.whatsapp.util.OnClickRecyclerView;
import com.example.whatsapp.util.SettingsFirebase;
import com.example.whatsapp.util.UsuarioFirebase;
import com.example.whatsapp.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment implements OnClickRecyclerView {

    private RecyclerView listaContacts;
    private List<Usuario> lstUsuario = new ArrayList<>();
    private List<Usuario> lstUserDatabase = new ArrayList<>();
    private DatabaseReference database = SettingsFirebase.getFirebaseDatabase();
    private String numberFormat;
    private ValueEventListener mValueListener;
    private DatabaseReference firebaseReference;
    private AdapterContactsList adapterContactsList;
    private ProgressBar progressBar;

    public ContactsFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.contatos_fragment, container, false);
        adapterContactsList = new AdapterContactsList(lstUsuario, getActivity(), this);

        progressBar = view.findViewById(R.id.progressBarContacts);
        progressBar.setVisibility(View.GONE);

        firebaseReference = database.child("usuarios");

        listaContacts = view.findViewById(R.id.recyclerListContacts);


        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
        listaContacts.setLayoutManager(lm);
        listaContacts.setHasFixedSize(true);
        listaContacts.setAdapter(adapterContactsList);

        return view;
    }

    public void getContactsBd() {
        lstUserDatabase.clear();
        lstUsuario.clear();
        mValueListener = firebaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Usuario usr = data.getValue(Usuario.class);
                    lstUserDatabase.add(usr);
                }

                adapterContactsList.notifyDataSetChanged();
                lstUsuario = Util.getContactList(lstUsuario,lstUserDatabase, true, getActivity());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }


    @Override
    public void onStop() {
        super.onStop();

        firebaseReference.removeEventListener(mValueListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        getContactsBd();
    }

    @Override
    public void onClick(int position) {
        Intent i = new Intent(getActivity(), ChatActivity.class);
        List<Usuario> lstUserUpdated = adapterContactsList.getContacts();
        Usuario selectedUser = lstUserUpdated.get(position);

        boolean cabecalho = selectedUser.getNumber().isEmpty();
        if (cabecalho) {
            startActivity(new Intent(getActivity(), GroupActivity.class));
        } else {
            i.putExtra("chatContato", selectedUser);
            startActivity(i);
        }

    }

    public void searchContacts(String s) {
        List<Usuario> lstContactsSeacrh = new ArrayList<>();
        for (Usuario usr : lstUsuario) {
            if (usr.getNome().toLowerCase().contains(s.toLowerCase())) {
                lstContactsSeacrh.add(usr);
            }
        }

        adapterContactsList = new AdapterContactsList(lstContactsSeacrh, getActivity(), this);
        listaContacts.setAdapter(adapterContactsList);
        adapterContactsList.notifyDataSetChanged();
    }

    public void reloadContacts() {
        adapterContactsList = new AdapterContactsList(lstUsuario, getActivity(), this);
        listaContacts.setAdapter(adapterContactsList);
        adapterContactsList.notifyDataSetChanged();
    }

    public List<Usuario> getContacts(){
        return this.lstUsuario;
    }
}
