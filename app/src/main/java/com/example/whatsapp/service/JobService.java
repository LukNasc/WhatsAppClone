package com.example.whatsapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.example.whatsapp.R;
import com.example.whatsapp.activity.HomeActivity;
import com.example.whatsapp.model.Conversations;
import com.example.whatsapp.model.Message;
import com.example.whatsapp.util.SettingsFirebase;
import com.example.whatsapp.util.StaticUser;
import com.example.whatsapp.util.UsuarioFirebase;
import com.example.whatsapp.util.Util;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class JobService extends android.app.job.JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("statusJob", "OnStartJob");
        new AsyncTaskCustom(this).execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    class AsyncTaskCustom extends AsyncTask<JobParameters, Void, String> {
        JobService jobService;
        public final String TAG = "statusJob";

        public AsyncTaskCustom(JobService jobService) {
            this.jobService = jobService;
        }

        @Override
        protected String doInBackground(JobParameters... jobParameters) {
            getConversations(jobParameters);
            Log.i(TAG, "execute getConversations()");
            return "CREATED";
        }


        public void getConversations(JobParameters... jobParameters) {
            final DatabaseReference database = SettingsFirebase.getFirebaseDatabase();
            final DatabaseReference conversasRef = database.child("conversas").child(UsuarioFirebase.getIdUser());
            final SharedPreferences sharedPreferences = jobService.getSharedPreferences("MESSAGE", Context.MODE_PRIVATE);

            conversasRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Conversations conversations = dataSnapshot.getValue(Conversations.class);
                    String codMessage = sharedPreferences.getString("codMessage", null);
                    if (codMessage != null && conversations.getCodMessage().equals(codMessage)) {
                    }else{
                        Util.showNotification(conversations, jobService);
                    }
                    Log.i(TAG, "OnChildAdded");
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Conversations conversations = dataSnapshot.getValue(Conversations.class);
                    String codMessage = sharedPreferences.getString("codMessage", null);
                    if (!conversations.getCodMessage().equals(codMessage)) {
                        Util.showNotification(conversations, jobService);
                    }
                    Log.i(TAG, "OnChildChanged");
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.i(TAG, "OnChildRemoved");

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    Log.i(TAG, "OnChildMoved");

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i(TAG, databaseError.getMessage());
                }
            });
            jobService.jobFinished(jobParameters[0], true);
        }



    }


}