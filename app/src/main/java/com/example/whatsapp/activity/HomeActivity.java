package com.example.whatsapp.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.whatsapp.R;
import com.example.whatsapp.adapter.AdapterConversation;
import com.example.whatsapp.fragments.ContactsFragment;
import com.example.whatsapp.fragments.ConversationsFragment;
import com.example.whatsapp.model.Conversations;
import com.example.whatsapp.service.JobService;
import com.example.whatsapp.util.SettingsFirebase;
import com.example.whatsapp.util.UsuarioFirebase;
import com.example.whatsapp.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {
    private MaterialSearchView materialSearchView;
    private static HomeActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        if(UsuarioFirebase.getCurrentUser() == null){
            finish();
        }


        instance = this;


        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("Conversas", ConversationsFragment.class)
                        .add("Contatos", ContactsFragment.class)
                        .create()
        );

        final ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout smartTabLayout = findViewById(R.id.smartTabLayout);
        smartTabLayout.setViewPager(viewPager);

        materialSearchView = findViewById(R.id.material_search_principal);
        materialSearchView.setHint("Pesquisar");
        //SearchView
        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                ConversationsFragment fragmentConversations = (ConversationsFragment) adapter.getPage(0);
                fragmentConversations.reloadConversations();
            }
        });


        //Caixa de Texto
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                switch (viewPager.getCurrentItem()){
                    case 0:
                        ConversationsFragment fragmentConversations = (ConversationsFragment) adapter.getPage(0);
                        if(newText != null && !newText.isEmpty()){
                            fragmentConversations.searchConversations(newText);
                        }else{
                            fragmentConversations.reloadConversations();
                        }

                        break;
                    case 1:
                        ContactsFragment fragmentContacts = (ContactsFragment) adapter.getPage(1);
                        if(newText != null && !newText.isEmpty()){
                            fragmentContacts.searchContacts(newText);
                        }else{
                            fragmentContacts.reloadContacts();
                        }
                        break;
                }
                return true;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);


        MenuItem item = menu.findItem(R.id.pesquisa);
        materialSearchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.config:
               startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.logout:
                FirebaseAuth auth = SettingsFirebase.getFirebaseAuth();
                auth.signOut();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static HomeActivity getIntance(){
            return instance;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Util.startJob(JobService.class, this);
    }
}
