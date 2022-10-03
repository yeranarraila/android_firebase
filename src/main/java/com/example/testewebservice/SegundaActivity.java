package com.example.testewebservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.testewebservice.model.Pessoa;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;

public class SegundaActivity extends AppCompatActivity {

    private TextView txnome;
    private TextView txmail;
    private FirebaseDatabase database;
    private DatabaseReference meuBD;
    //----- SELECT -----
    private ArrayList<Pessoa> lista = new ArrayList<Pessoa>();
    private ArrayAdapter<Pessoa> adapter;
    private ListView listview;
   //-------------------
    //****** UPDATE e DELETE *********
    Pessoa pessoaSelecionada = new Pessoa();
    //********************************

    public void iniciarBD() {
        FirebaseApp.initializeApp(this); //seta o BD para esta activity
        database = FirebaseDatabase.getInstance(); //inicializa a instancia do BD
        //----------- Select ------------------
        database.setPersistenceEnabled(true);
        //-------------------------------------
        meuBD= database.getReference(); //inicializa acesso ao BD
    }

    public void listenerBD(){ //SELECT

        listview = (ListView) findViewById(R.id.listview);

        meuBD.child("Pessoa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista.clear(); //limpa lista

                for(DataSnapshot rs : dataSnapshot.getChildren()){ //percorro o BD (ResultSet)
                    Pessoa p = rs.getValue(Pessoa.class); //recupera objetos 'Pessoa' no BD
                    lista.add(p);//adiciono a pessoa
                }
                adapter = new ArrayAdapter<Pessoa>(SegundaActivity.this,android.R.layout.simple_list_item_1,lista);
                listview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segunda);
        iniciarBD();
        listenerBD();//atualiza sempre os campos (SELECT)

        txnome = (TextView)findViewById(R.id.nome);
        txmail = (TextView)findViewById(R.id.mail);

        //************ UPDATE e DELETE *******************
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pessoaSelecionada = (Pessoa)parent.getItemAtPosition(position);
                txnome.setText(pessoaSelecionada.getNome());
                txmail.setText(pessoaSelecionada.getEmail());
            }
        });
        //************************************************
    }

    public void limparDados(){
        txnome.setText("");
        txmail.setText("");
    }

    public void gravarDados(View view){

        Pessoa pessoa = new Pessoa();
        pessoa.setID(UUID.randomUUID().toString());//gera id aleatoria
        pessoa.setNome(txnome.getText().toString());
        pessoa.setEmail(txmail.getText().toString());


        meuBD.child("Pessoa").child(pessoa.getID()).setValue(pessoa);//tabela Pessoa, PK = id. Envia obj pessoa
        limparDados();
    }

    public void atualizarDados(View view){ //UPDATE
        Pessoa pessoa = new Pessoa();
        pessoa.setID(pessoaSelecionada.getID());
        pessoa.setNome(txnome.getText().toString().trim());
        pessoa.setEmail(txmail.getText().toString().trim());
        meuBD.child("Pessoa").child(pessoa.getID()).setValue(pessoa);//tabela Pessoa, PK = id. Envia obj pessoa
        limparDados();
    }

    public void excluiDados(View view){ //DELETE
        Pessoa pessoa = new Pessoa();
        pessoa.setID(pessoaSelecionada.getID());
        meuBD.child("Pessoa").child(pessoa.getID()).removeValue();
        limparDados();
    }
}
