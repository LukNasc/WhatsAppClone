package com.example.whatsapp.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.whatsapp.R;
import com.example.whatsapp.activity.HomeActivity;
import com.example.whatsapp.model.Conversations;
import com.example.whatsapp.model.Message;
import com.example.whatsapp.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class Util {
    public static void checkUser(FirebaseAuth auth, Action act) {
        if (auth.getCurrentUser() != null) {
            act.openAct();
        }
    }


    public static List<Usuario> getContactList(List<Usuario> lstUsuario, List<Usuario> lstUserDatabase, boolean group, Context context) {
        lstUsuario.clear();
        int first = 0;
        String numberFormat = null;
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        String phoneNo = "";
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID)
                );
                String name = cur.getString(
                        cur.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME
                        )
                );

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ?",
                            new String[]{id}, null
                    );

                    while (pCur.moveToNext()) {
                        phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                        ));


                        numberFormat = phoneNo
                                .replace("+55", "")
                                .replaceAll("( | )", "")
                                .replaceAll("[( | )]", "")
                                .replaceAll("[-]", "");

                    }
                    pCur.close();
                    if (first == 0 && group) {
                        Usuario itemGroup = new Usuario();
                        itemGroup.setNome("Novo grupo");
                        itemGroup.setNumber("");
                        lstUsuario.add(itemGroup);
                        first = 1;
                    }

                    for (Usuario usr : lstUserDatabase) {
                        String compararBD = usr.getNumber().substring(10);

                        String compararTEL;
                        if (numberFormat != null && numberFormat.length() >= 5) {
                            compararTEL = numberFormat.substring(numberFormat.length() - 4);
                        } else {
                            compararTEL = numberFormat;
                        }

                        if (compararTEL != null && compararTEL.contains(compararBD)) {
                            usr.setNumber("+55" + numberFormat);
                            usr.setNome(name);
                            lstUsuario.add(usr);
                        }

                    }
                }
            }
        }
        if (cur != null) {
            cur.close();
        }

        return lstUsuario;
    }


    public static void startJob(Class classType, Context context) {
        ComponentName componentName = new ComponentName(context, classType);
        long min = 1;
        long max = 1;
        JobInfo.Builder jobInfo = new JobInfo.Builder(1, componentName);
        jobInfo.setMinimumLatency(min);
        jobInfo.setOverrideDeadline(max); // maximum delay


        JobScheduler jobScheduler = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            jobScheduler = context.getSystemService(JobScheduler.class);
            jobScheduler.schedule(jobInfo.build());
        }


        if (jobScheduler != null) {
            if (jobScheduler.schedule(jobInfo.build()) == JobScheduler.RESULT_SUCCESS) {
                Log.i("statusJob", "job created witch success");
            } else {
                Log.e("statusJob", "failed to create");
            }
        }
    }
    public static void showNotification(Conversations conversations, Context context) {
        NotificationManager mNotificationManager;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "notify_001");

        Intent ii = new Intent(context, HomeActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(conversations.getUltimaMessagem());
        if (conversations.getIsGroup().equals("true")) {
            bigText.setBigContentTitle(conversations.getGroup().getNome());
            mBuilder.setContentTitle(conversations.getGroup().getNome());
        } else {
            bigText.setBigContentTitle(conversations.getUsuario().getNome());
            bigText.setSummaryText(conversations.getUsuario().getNumber());
            mBuilder.setContentTitle(conversations.getUsuario().getNome());
            mBuilder.setSubText(conversations.getUsuario().getNumber());

        }

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
        mBuilder.setContentText(conversations.getUltimaMessagem());
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setLights(Color.RED, 000, 000);
        long[] partner = {500, 500, 500, 500, 500, 500, 500};
        mBuilder.setVibrate(partner);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "CANALID";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Notificações de Mensagens do WhatsApp Clone",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());

        Log.i("statusJob", "send notification");

        updateConversation(conversations);

    }

    private static void updateConversation(Conversations conv) {
        conv.setView("true");
        conv.salvar();

    }
}
