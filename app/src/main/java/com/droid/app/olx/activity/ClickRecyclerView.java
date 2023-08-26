package com.droid.app.olx.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.droid.app.olx.adapter.AdapterAnuncios;
import com.droid.app.olx.helper.RecyclerItemClickListener;
import com.droid.app.olx.model.Anuncio;

import java.io.Serializable;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class ClickRecyclerView {
    RecyclerView recyclerView;
    Anuncio anuncioSelected;
    List<Anuncio> anuncioList;
    AdapterAnuncios adapterAnuncios;
    Context context;
    // construtor da classe
    public ClickRecyclerView(
            RecyclerView recyclerView,List<Anuncio> anuncioList,
                             AdapterAnuncios adapterAnuncios, Context context
    ) {
        this.recyclerView = recyclerView;
        this.anuncioList = anuncioList;
        this.adapterAnuncios = adapterAnuncios;
        this.context = context;
    }

    // click para excluir meus anúncios
    public void clickRecyclerView(){
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        context,
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                anuncioSelected = anuncioList.get(position);
                                System.out.println("id: "+anuncioSelected.getIdAnuncio());

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Excluir anúncio");
                                builder.setMessage("Tem certeza que deseja excluir esse anúncio?");
                                builder.setCancelable(false);
                                builder.setPositiveButton("Sim", (dialog, which) -> {
                                    anuncioSelected.removerAnuncio();
                                    adapterAnuncios.notifyDataSetChanged();
                                });
                                builder.setNegativeButton("Não", (dialog, which) -> {

                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
    }

    // click para exibir de detalhes
    public void clickExibirRecyclerView(){
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        context,
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                anuncioSelected = anuncioList.get(position);
                                Intent i = new Intent(context, DetalhesProdutosActivity.class);
                                i.putExtra("anuncioSelected", (Parcelable) anuncioSelected);
                                context.startActivity(i);
                            }
                            @Override
                            public void onLongItemClick(View view, int position) {}
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
                        }
                )
        );
    }
}
