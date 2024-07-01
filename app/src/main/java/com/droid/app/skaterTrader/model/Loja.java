package com.droid.app.skaterTrader.model;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.droid.app.skaterTrader.R;
import com.droid.app.skaterTrader.firebase.UserFirebase;
import com.droid.app.skaterTrader.firebaseRefs.FirebaseRef;
import com.droid.app.skaterTrader.viewModel.ViewModelConfigDadosLoja;
import com.droid.app.skaterTrader.viewModel.ViewModelFirebase;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
public class Loja {
    Activity activity;
    StorageReference storage, imgAnuncio;
    DatabaseReference database, lojaRef;

    private String nomeLoja;
    private String nomeUser;
    private String cpfOrCnpj;
    private String email;
    private String senha;
    private String urlLogo;
    private String id;
    private ModelCnpj endereço;
    private String telefone;
    private String token;
    ViewModelConfigDadosLoja viewModelConfig;
    ViewModelFirebase viewModelFirebase;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public ModelCnpj getModelCnpj() {
        return endereço;
    }

    public void setModelCnpj(ModelCnpj modelCnpj) {
        this.endereço = modelCnpj;
    }

    public String getNomeLoja() {
        return nomeLoja;
    }

    public void setNomeLoja(String nomeLoja) {
        this.nomeLoja = nomeLoja;
    }

    public String getNomeUser() {
        return nomeUser;
    }

    public void setNomeUser(String nomeUser) {
        this.nomeUser = nomeUser;
    }

    public String getCpfOrCnpj() {
        return cpfOrCnpj;
    }

    public void setCpfOrCnpj(String cpfOrCnpj) {
        this.cpfOrCnpj = cpfOrCnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getUrlLogo() {
        return urlLogo;
    }

    public void setUrlLogo(String urlLogo) {
        this.urlLogo = urlLogo;
    }

    public Loja(ViewModelConfigDadosLoja viewModel) {
        this.viewModelConfig = viewModel;
    }

    public Loja() {

    }

    public void setViewModel(ViewModelFirebase viewModel){
        this.viewModelFirebase = viewModel;
    }

    // gerar ID para o usuario
    public String getIdLoja() {
        if( FirebaseRef.getAuth().getCurrentUser() != null ){
            this.id = FirebaseRef.getAuth().getCurrentUser().getUid();
        }
        return id;
    }

    public void salvarDados( byte[] dadosImg, Context context) {
        //salvar dados no Firebase
        database = FirebaseRef.getDatabase();
        lojaRef = database.child("lojas").child(getIdLoja());

        atulizarTipoDeUser(context);

        // salvar dados
        lojaRef.setValue(this)
            .addOnCompleteListener(task -> {

                // salvar dados para consulta
                if(task.isSuccessful()){
                    salvarImgLogoLoja(dadosImg, "cadastro");

                    // gerar um id para consulta
                    String idConsulta = lojaRef.push().getKey();

                    // definir e salvar dados para consultas
                    Consulta consulta = new Consulta();
                    consulta.setIdConsulta(idConsulta);
                    consulta.setNomeLoja(getNomeLoja());
                    consulta.setTelefone(getTelefone());
                    consulta.setCpfOrCnpj(getCpfOrCnpj());
                    consulta.salvar();

                    viewModelFirebase.setCadastrado(true);
                }
        });

    }

    // salvar imagens no storage
    public void salvarImgLogoLoja( byte[] img, String type) {
        try{
            // iniciar referencias do firebase
            storage = FirebaseRef.getStorage();
            imgAnuncio = storage.child("imagens")
                    .child("logo_lojas")
                    .child(getIdLoja())
                    .child("imagem" + 1);

            // fazer upload da imagem
            UploadTask uploadTask = imgAnuncio.putBytes(img);
            uploadTask.addOnSuccessListener( taskSnapshot -> fazerDownloadUrlFoto(type));

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void fazerDownloadUrlFoto(String type){
        // Faz download da Url da foto
        imgAnuncio.getDownloadUrl().addOnCompleteListener( task -> {

            System.out.println("Link foto " + task.getResult().toString());

            String urlImgStorage = task.getResult().toString();
            setUrlLogo(urlImgStorage);

            // update img perfil
            FirebaseRef.upDateImgPerfil(null, urlImgStorage);

            if(type.equals("atualizar")){
                atualizarDadosDB();
            }
        });
    }

    // atualizar dados no DB
    public void atualizarDadosDB(){
        database = FirebaseRef.getDatabase();
        lojaRef = database.child("lojas").child(getIdLoja());

        Map<String, Object> updateDB = new HashMap<>();
        if(getUrlLogo() != null && !getUrlLogo().isEmpty()){
            updateDB.put("urlLogo",getUrlLogo());
        }
        if(getNomeLoja() != null && !getNomeLoja().isEmpty()){
            updateDB.put("nomeLoja",getNomeLoja());
        }
        if(getNomeUser() != null && !getNomeUser().isEmpty()){
            updateDB.put("nomeUser",getNomeUser());
        }
        if(getTelefone() != null && !getTelefone().isEmpty()){
            updateDB.put("telefone",getTelefone());
        }

        lojaRef.updateChildren(updateDB).addOnCompleteListener(task -> {

            viewModelConfig.setBol(task.isSuccessful());

            /*if(task.isSuccessful() && progress != null && btn != null){
                progress.setVisibility(View.GONE);
                btn.setVisibility(View.GONE);
            }*/
        });
    }

    // salvar tipo de user
    public void atulizarTipoDeUser(Context context){
        try {
            if(UserFirebase.UserLogado()){
                FirebaseUser user = FirebaseRef.getAuth().getCurrentUser();
                String tipoUser = "L";
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(tipoUser)
                        .build();

                assert user != null;
                user.updateProfile( profile )
                        .addOnCompleteListener( (@NonNull Task<Void> task) -> {
                            if(!task.isSuccessful()){
                                Toast.makeText(activity,
                                        context.getString(R.string.erro_ao_definir_tipo_de_perfil),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void getNameLojaDb(TextView editNameLoja){
        //obter dados no Firebase
        DatabaseReference database = FirebaseRef.getDatabase();
        DatabaseReference lojaRef = database.child("lojas").child(getIdLoja());

        lojaRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Loja loja = task.getResult().getValue(Loja.class);
                if(loja != null) {
                    editNameLoja.setText( loja.getNomeLoja() );
                }
            }
        });
    }
}