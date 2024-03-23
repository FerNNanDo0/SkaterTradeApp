package com.droid.app.skaterTrader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.droid.app.skaterTrader.model.Anuncio;
import com.droid.app.skaterTrader.R;
import java.util.List;
public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder> {
    List<Anuncio> listAnuncios;
    Context context;
    View view;

    public AdapterAnuncios(List<Anuncio> listAnuncios, Context context) {
        this.context = context;
        this.listAnuncios = listAnuncios;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_anuncio, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Anuncio anuncio = listAnuncios.get(position);

        if(anuncio != null){
            holder.titulo.setText( anuncio.getTitulo() );
            holder.valor.setText( anuncio.getValor() );
            holder.estado.setText( anuncio.getEstado() );

            // pegar primeira imagem da lista
            String urlCapa = anuncio.getFotos().get(0);

            // define a imagem
            //Picasso.get().load(urlCapa).into(holder.foto);
            Glide.with(context).load(urlCapa).into(holder.foto);

            holder.progressBarImg.setVisibility(View.GONE);
        }else{
            holder.progressBarImg.setVisibility(View.VISIBLE);
            onBindViewHolder(holder, position);
        }


    }

    @Override
    public int getItemCount() {
        return listAnuncios.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, valor, estado;
        ImageView foto;
        ProgressBar progressBarImg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titulo);
            valor = itemView.findViewById(R.id.valor);
            estado = itemView.findViewById(R.id.estado);
            foto = itemView.findViewById(R.id.imageAnuncio);
            progressBarImg = itemView.findViewById(R.id.progressBarImg);
        }
    }
}
