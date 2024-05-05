package com.example.appgestao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.appgestao.model.Despesa;
import com.example.appgestao.MainActivity;
public class EditActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.example.android.expenselist.REPLY";


    private EditText TipoDespesa;
    private EditText TextDate;
    private EditText ValorDespesa;
    private Button btnSalvarDesp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Inicialize os elementos de interface do usuário
        TipoDespesa = findViewById(R.id.TipoDespesa);
        TextDate = findViewById(R.id.TextDate);
        ValorDespesa = findViewById(R.id.ValorDespesa);
        btnSalvarDesp = findViewById(R.id.btnSalvarDesp);

        Despesa despesa = (Despesa) getIntent().getSerializableExtra("despesa");

        // Preencha os campos EditText
        if (despesa != null) {
            TipoDespesa.setText(despesa.getTipoDespesa());
            TextDate.setText(despesa.getDate());
            ValorDespesa.setText(String.valueOf(despesa.getValor()));
        }

        btnSalvarDesp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();
                despesa.setTipoDespesa(TipoDespesa.getText().toString());
                despesa.setDate(TextDate.getText().toString());
                despesa.setValor(Double.parseDouble(ValorDespesa.getText().toString()));
                replyIntent.putExtra("despesa", despesa);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }


    }
