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
import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.model.Anuncio;
import com.squareup.picasso.Picasso;
import java.util.List;
public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder> {
    List<Anuncio> listAnuncios;
    Context context;
    public AdapterAnuncios(List<Anuncio> listAnuncios, Context context) {
        this.context = context;
        this.listAnuncios = listAnuncios;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_anuncio, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Anuncio anuncio = listAnuncios.get(position);
        holder.titulo.setText( anuncio.getTitulo() );
        holder.valor.setText( anuncio.getValor() );

        // pegar primeira imagem da lista
        String urlCapa = anuncio.getFotos().get(0);
        Picasso.get().load(urlCapa).into(holder.foto);

        holder.progressBarImg.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return listAnuncios.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, valor;
        ImageView foto;
        ProgressBar progressBarImg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titulo);
            valor = itemView.findViewById(R.id.valor);
            foto = itemView.findViewById(R.id.imageAnuncio);
            progressBarImg = itemView.findViewById(R.id.progressBarImg);
        }
    }
}
