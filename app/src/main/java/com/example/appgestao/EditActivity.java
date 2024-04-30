package com.example.appgestao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.appgestao.model.Despesa;
import com.example.appgestao.MainActivity;
public class EditActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.example.android.expenselist.REPLY";


    private EditText TipoDespesa;
    private EditText TextDate;
    private EditText ValorDespesa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Despesa despesa = (Despesa) getIntent().getSerializableExtra("Despesa");
        // Agora você pode usar o objeto despesa para preencher seus campos EditText


        // Inicialize os elementos de interface do usuário
        TipoDespesa = findViewById(R.id.TipoDespesa);
        TextDate = findViewById(R.id.TextDate);
        ValorDespesa = findViewById(R.id.ValorDespesa);

        // Obtenha a despesa passada e preencha os campos EditText
        despesa = (Despesa) getIntent().getSerializableExtra("Despesa");
        assert despesa != null;
        TipoDespesa.setText(despesa.getTipoDespesa());
        TextDate.setText(despesa.getDate());
        ValorDespesa.setText(String.valueOf(despesa.getValor()));
    }
       /* // Recupere a despesa selecionada (passada como extra na Intent)
        Despesa despesa = getIntent().getParcelableExtra("despesa");

        // Preencha os campos de edição com os valores da despesa
        TipoDespesa.setText(despesa.getTipoDespesa());
        TextDate.setText(despesa.getDate());
        ValorDespesa.setText(String.valueOf(despesa.getValor()));
*/


    // Implemente a lógica para salvar as alterações (ao clicar em um botão "Salvar")
    public void saveEdit(View view) {
        Intent replyIntent = new Intent();
        if (TextUtils.isEmpty(TipoDespesa.getText())||
                TextUtils.isEmpty(TextDate.getText()) ||
                TextUtils.isEmpty(ValorDespesa.getText())) {
            setResult(RESULT_CANCELED, replyIntent);
        } else {
            String tipoDespesa = TipoDespesa.getText().toString();
            String textDate = TextDate.getText().toString();
            double valorDespesa = Double.parseDouble(ValorDespesa.getText().toString());


            Despesa despesa = new Despesa(tipoDespesa, textDate, valorDespesa);
            replyIntent.putExtra("EXTRA_REPLY_DESPESA", despesa);


            setResult(RESULT_OK, replyIntent);
        }
        finish();
    }
        // Implemente a lógica para cancelar a edição (ao clicar em um botão "Cancelar")

    }
