package com.example.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.model.Usuario;
import com.example.whatsapp.util.Action;
import com.example.whatsapp.util.OnClickRecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterContactsList extends RecyclerView.Adapter<AdapterContactsList.Holder> {
    private List<Usuario> lstContatos;
    private Context context;
    private OnClickRecyclerView action;
    public AdapterContactsList(List<Usuario> lstContatos, Context context, OnClickRecyclerView action) {
        this.lstContatos = lstContatos;
        this.context = context;
        this.action = action;

    }


    public List<Usuario> getContacts(){
        return this.lstContatos;
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.layout_adapter_conversations,
                        viewGroup,
                        false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int i) {
        Usuario usuario = lstContatos.get(i);
        boolean cabecalho = usuario.getNumber().isEmpty();

        holder.nome.setText(usuario.getNome());
        holder.numero.setText(usuario.getNumber());

        if(usuario.getFoto() != null){
            Uri uri = Uri.parse(usuario.getFoto());
            Glide.with(context)
                    .load(uri)
                    .into(holder.imagem);
        }else{
            if(cabecalho){
                holder.imagem.setImageResource(R.drawable.group);
                holder.numero.setVisibility(View.GONE);
            }else{
                holder.imagem.setImageResource(R.drawable.padrao);
            }
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.onClick(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstContatos.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView nome, numero;
        private CircleImageView imagem;
        private LinearLayout linearLayout;

        public Holder(@NonNull View itemView) {
            super(itemView);
            imagem = itemView.findViewById(R.id.circleIVProfileConversas);
            nome = itemView.findViewById(R.id.textViewNomeConversas);
            numero = itemView.findViewById(R.id.textViewUltimaMsg);
            linearLayout = itemView.findViewById(R.id.linearConversas);

        }
    }
}
