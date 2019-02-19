package com.example.whatsapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.whatsapp.R;

import static com.example.whatsapp.activity.SettingsActivity.REQUESTCODECAMERA;
import static com.example.whatsapp.activity.SettingsActivity.REQUESTCODEGALERIA;

public class CustomAlertDiailog extends Dialog implements View.OnClickListener {
   private ImageView camera, galeria;
   private Context context;
   private Activity activity;
    public CustomAlertDiailog(Context context, Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
        setContentView(R.layout.dialog_layout);

        camera = findViewById(R.id.ivCamera);
        galeria = findViewById(R.id.ivGaleria);

        WindowManager.LayoutParams wm = (WindowManager.LayoutParams) getWindow().getAttributes();
        if(Build.VERSION.SDK_INT > 26){
            wm.width = 1000;
            wm.height = 600;
        }else{
            wm.width = 700;
            wm.height = 400;
        }

        wm.gravity = Gravity.BOTTOM;
//        wm.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wm.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(wm);

        setTitle(null);
        camera.setOnClickListener(this);
        galeria.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivCamera :
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
                if(intent.resolveActivity(activity.getPackageManager()) != null){
                    activity.startActivityForResult(intent, REQUESTCODECAMERA);
                }
                break;

            case  R.id.ivGaleria:
                Intent intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent2.resolveActivity(activity.getPackageManager()) != null){
                    activity.startActivityForResult(intent2, REQUESTCODEGALERIA);
                }
                break;
        }

    }

}
