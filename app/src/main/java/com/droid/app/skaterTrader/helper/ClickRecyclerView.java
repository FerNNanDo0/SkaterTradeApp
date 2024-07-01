package com.droid.app.skaterTrader.helper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import androidx.recyclerview.widget.RecyclerView;

import com.droid.app.skaterTrader.activity.CadastrarOuEditarAnunciosActivity;
import com.droid.app.skaterTrader.activity.DetalhesProdutosActivity;
import com.droid.app.skaterTrader.model.Anuncio;
import com.droid.app.skaterTrader.adapter.AdapterAnuncios;

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

    // click para excluir ou editar meus anúncios
    public void clickRecyclerView(){
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        context,
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                anuncioSelected = anuncioList.get(position);
                                Intent i = new Intent(context, CadastrarOuEditarAnunciosActivity.class);
                                i.putExtra("anuncioSelected",anuncioSelected);
                                context.startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                /*anuncioSelected = anuncioList.get(position);
                                System.out.println("id: "+anuncioSelected.getIdAnuncio());

                                alertExcluir();*/
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
                                i.putExtra("anuncioSelected",anuncioSelected);
                                context.startActivity(i);
                            }
                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
                        }
                )
        );
    }

    private void alertExcluir(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Excluir anúncio!");
        builder.setMessage("Tem certeza que deseja excluir esse anúncio?");
        builder.setCancelable(false);
        builder.setPositiveButton("Sim", (dialog, which) -> {
            anuncioSelected.removerAnuncio();
            adapterAnuncios.notifyDataSetChanged();
            dialog.dismiss();
        });
        builder.setNegativeButton("Não", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}