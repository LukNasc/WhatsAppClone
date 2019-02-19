package com.example.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.model.Message;
import com.example.whatsapp.model.Usuario;
import com.example.whatsapp.util.Action;
import com.example.whatsapp.util.UsuarioFirebase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.HolderMessage> {
    private List<Message> lstMessage;
    private Context context;
    private static final int TYPE_REM = 0;
    private static final int TYPE_DES = 1;


    public AdapterMessage(List<Message> lstMessage, Context c) {
        this.context = c;
        this.lstMessage = lstMessage;
    }

    @NonNull
    @Override
    public HolderMessage onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int layout = 0;
        if (i == TYPE_REM) {
            layout = R.layout.adapter_message_remetente;
        } else {
            layout = R.layout.adapter_message_destinatario;
        }

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
        return new HolderMessage(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMessage holderMessage, int i) {
        Message msg = lstMessage.get(i);

        String nome = msg.getNome();
        if (msg.getImage() != null) {

            Uri url = Uri.parse(msg.getImage());
            Glide.with(context)
                    .load(url)
                    .into(holderMessage.image);

            if (nome.isEmpty()) {
                holderMessage.textNome.setVisibility(View.GONE);
            } else {
                holderMessage.textNome.setText(msg.getNome());
            }

            holderMessage.image.setVisibility(View.VISIBLE);
            holderMessage.textMessage.setVisibility(View.GONE);
        } else {

            holderMessage.textMessage.setText(msg.getMessage());

            if (nome.isEmpty()) {
                holderMessage.textNome.setVisibility(View.GONE);
            } else {
                holderMessage.textNome.setText(msg.getNome());
            }
        }

    }

    @Override
    public int getItemCount() {
        return lstMessage.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = lstMessage.get(position);
        if (message.getIdUser().equals(UsuarioFirebase.getIdUser())) {
            return TYPE_REM;
        } else {
            return TYPE_DES;
        }
    }

    class HolderMessage extends RecyclerView.ViewHolder {
        private TextView textMessage, textNome;
        private ImageView image;
        private LinearLayout caixa;


        public HolderMessage(@NonNull View itemView) {
            super(itemView);

            textMessage = itemView.findViewById(R.id.textMessageTexto);
            image = itemView.findViewById(R.id.imageMessageFoto);
            textNome = itemView.findViewById(R.id.textNameRemetente);
            caixa = itemView.findViewById(R.id.caixa);

        }
    }
}
