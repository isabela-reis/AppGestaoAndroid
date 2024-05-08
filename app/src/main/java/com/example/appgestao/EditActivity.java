package com.example.appgestao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.appgestao.model.Despesa;
import com.example.appgestao.model.SyncService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.example.android.expenselist.REPLY";

    private EditText TipoDespesa;
    private EditText TextDate;
    private EditText ValorDespesa;
    private Button btnSalvarDesp;
    private String IdDespesa;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        IdDespesa = getIntent().getStringExtra("IdDespesa");


        TipoDespesa = findViewById(R.id.TipoDespesa);
        TextDate = findViewById(R.id.TextDate);
        ValorDespesa = findViewById(R.id.ValorDespesa);
        btnSalvarDesp = findViewById(R.id.btnSalvarDesp);

        Despesa despesa = (Despesa) getIntent().getSerializableExtra("despesa");

        if (despesa != null) {
            TipoDespesa.setText(despesa.getTipoDespesa());
            TextDate.setText(despesa.getDate());
            ValorDespesa.setText(String.valueOf(despesa.getValor()));
        }
        if (IdDespesa != null) {
            databaseReference.child("despesas").child(IdDespesa).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Despesa despesa = dataSnapshot.getValue(Despesa.class);
                    if (despesa != null) {
                        TipoDespesa.setText(despesa.getTipoDespesa());
                        TextDate.setText(despesa.getDate());
                        ValorDespesa.setText(String.valueOf(despesa.getValor()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Tratar erro
                }
            });
        }
        btnSalvarDesp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();

                despesa.setTipoDespesa(TipoDespesa.getText().toString());
                despesa.setDate(TextDate.getText().toString());
                despesa.setValor(Double.parseDouble(ValorDespesa.getText().toString()));

                Intent intent = new Intent(EditActivity.this, SyncService.class);
                intent.putExtra("despesa", despesa);
                startService(intent);

                if (IdDespesa == null) {
                    IdDespesa = databaseReference.child("despesas").push().getKey();
                }

                databaseReference.child("despesas").child(IdDespesa).setValue(despesa);

                replyIntent.putExtra("despesa", despesa);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }
    }
