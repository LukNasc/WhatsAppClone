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
import com.example.whatsapp.model.Conversations;
import com.example.whatsapp.model.Group;
import com.example.whatsapp.util.OnClickRecyclerView;

import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterConversation extends RecyclerView.Adapter<AdapterConversation.HolderConversations> {
    private List<Conversations> lstConversations;
    private Context context;
    private OnClickRecyclerView onClickRecyclerView;

    public AdapterConversation(List<Conversations> lstConversations, Context context, OnClickRecyclerView onClickRecyclerView) {
        this.lstConversations = lstConversations;
        this.context = context;
        this.onClickRecyclerView = onClickRecyclerView;
    }

    public List<Conversations> getLstConversations(){
        return this.lstConversations;
    }

    @NonNull
    @Override
    public HolderConversations onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater
                        .from(viewGroup.getContext())
                .inflate(R.layout.layout_adapter_conversations,
                        viewGroup,
                        false);
        return new HolderConversations(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderConversations holder, final int i) {
        Conversations conversations = lstConversations.get(i);

        holder.msg.setText(conversations.getUltimaMessagem());

        if(conversations.getIsGroup().equals("true")){
            Group group = conversations.getGroup();
            holder.nome.setText(group.getNome());

            if(group.getFoto() != null){
                Uri url = Uri.parse(group.getFoto());
                Glide.with(context)
                        .load(url)
                        .into(holder.circleImageView);
            }else{
                holder.circleImageView.setImageResource(R.drawable.padrao);
            }

        }else{
            holder.nome.setText(conversations.getUsuario().getNome());

            if(lstConversations.get(i).getUsuario().getFoto() != null){
                Uri url = Uri.parse(lstConversations.get(i).getUsuario().getFoto());
                Glide.with(context)
                        .load(url)
                        .into(holder.circleImageView);
            }else{
                holder.circleImageView.setImageResource(R.drawable.padrao);
            }
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRecyclerView.onClick(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return lstConversations.size();
    }

    class HolderConversations extends RecyclerView.ViewHolder{
        private CircleImageView circleImageView;
        private TextView nome,msg;
        private LinearLayout linearLayout;

        public HolderConversations(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.circleIVProfileConversas);
            nome = itemView.findViewById(R.id.textViewNomeConversas);
            msg = itemView.findViewById(R.id.textViewUltimaMsg);
            linearLayout = itemView.findViewById(R.id.linearConversas);

        }
    }
}
