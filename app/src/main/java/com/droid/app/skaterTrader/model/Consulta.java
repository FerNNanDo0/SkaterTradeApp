package com.droid.app.skaterTrader.model;

import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.google.firebase.database.DatabaseReference;
public class Consulta {
    private String nomeLoja;
    private String telefone;
    private String cpfOrCnpj;
    private String idConsulta;


    // contructor
    public Consulta(){
    }

    public void setIdConsulta(String id){
        this.idConsulta = id;
    }

    public String getNomeLoja() {
        return nomeLoja;
    }

    public void setNomeLoja(String nomeLoja) {
        this.nomeLoja = nomeLoja;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCpfOrCnpj() {
        return cpfOrCnpj;
    }

    public void setCpfOrCnpj(String cpfOrCnpj) {
        this.cpfOrCnpj = cpfOrCnpj;
    }


    // salvar dados de consulta
    public void salvar(){
        //salvar dados no Firebase
        DatabaseReference database = FirebaseRef.getDatabase();

        DatabaseReference consultasRef = database.child("consultas")
                .child(this.idConsulta);

        consultasRef.setValue(this);
    }
}