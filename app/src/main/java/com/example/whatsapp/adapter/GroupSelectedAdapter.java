package com.example.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.model.Usuario;
import com.example.whatsapp.util.OnClickRecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupSelectedAdapter extends RecyclerView.Adapter<GroupSelectedAdapter.HolderGroup> {
    private Context context;
    private List<Usuario> lstUsuario;
    private OnClickRecyclerView onClickRecyclerView;

    public GroupSelectedAdapter(Context context, List<Usuario> lstUsuario, OnClickRecyclerView onClickRecyclerView) {
        this.context = context;
        this.lstUsuario = lstUsuario;
        this.onClickRecyclerView = onClickRecyclerView;
    }

    @NonNull
    @Override
    public HolderGroup onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.adapter_group_selected,
                viewGroup, false);
        return new HolderGroup(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroup holderGroup, final int i) {
        Usuario usuario = lstUsuario.get(i);
        holderGroup.nome.setText(usuario.getNome());
        if(usuario.getFoto() != null){
            String url = Uri.parse(usuario.getFoto()).toString();
            Glide.with(context)
                    .load(url)
                    .into(holderGroup.profile);
        }else{
            holderGroup.profile.setImageResource(R.drawable.padrao);
        }
        holderGroup.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRecyclerView.onClick(i);
            }
        });
    }


    @Override
    public int getItemCount() {
        return lstUsuario.size();
    }

    class HolderGroup extends RecyclerView.ViewHolder{
        TextView nome;
        CircleImageView profile;


    public HolderGroup(@NonNull View itemView) {
        super(itemView);
        nome = itemView.findViewById(R.id.textNameMemberSelected);
        profile = itemView.findViewById(R.id.imageProfileSelected);

    }
}
}
