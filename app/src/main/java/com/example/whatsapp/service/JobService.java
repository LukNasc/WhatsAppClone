package com.example.whatsapp.service;

import android.app.job.JobParameters;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.whatsapp.model.Conversations;
import com.example.whatsapp.util.SettingsFirebase;
import com.example.whatsapp.util.UsuarioFirebase;
import com.example.whatsapp.util.Util;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class JobService extends android.app.job.JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        if (UsuarioFirebase.getCurrentUser() == null) {
            onStopJob(params);
        }
        Log.i("statusJob", "OnStartJob");
        new AsyncTaskCustom(this).execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("statusJob", "onStopJob");
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

            conversasRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Conversations conversations = dataSnapshot.getValue(Conversations.class);
                    if (conversations.getView() != null) {
                        if (!conversations.getView().equals("true")) {
                            Util.showNotification(conversations, jobService);
                        }
                    }

                    Log.i(TAG, "OnChildAdded");
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Conversations conversations = dataSnapshot.getValue(Conversations.class);
                    if (!conversations.getView().equals("true")) {
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