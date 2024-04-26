package com.example.appgestao;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.example.appgestao.model.Despesa;

public class MainActivity extends AppCompatActivity {

    private EditText editTipoDespesa;
    private EditText editTextDate;
    private EditText editValorDespesa;
    private Button buttonAddDesp;
    private RecyclerView recyclerViewDesp;
    private DespesaAdapter despesaAdapter;
    private List<Despesa> despesas = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTipoDespesa = findViewById(R.id.editTipoDespesa);
        editTextDate = findViewById(R.id.editTextDate);
        editValorDespesa = findViewById(R.id.editValorDespesa);

        buttonAddDesp = findViewById(R.id.buttonAddDesp);
        recyclerViewDesp = findViewById(R.id.recyclerViewDesp);

        editTextDate.addTextChangedListener(new TextWatcher() {

            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        if (mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    editTextDate.setText(current);
                    editTextDate.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // RecyclerView
        recyclerViewDesp.setLayoutManager(new LinearLayoutManager(this));
        despesaAdapter = new DespesaAdapter(despesas);
        recyclerViewDesp.setAdapter(despesaAdapter);


        // Botão adicionar nova despesa
        buttonAddDesp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tipoDespesa = editTipoDespesa.getText().toString().trim();
                String textData = editTextDate.getText().toString().trim();
                Double valorDespesa = Double.valueOf(editValorDespesa.getText().toString().trim());

                if (!tipoDespesa.isEmpty()) {
                    Despesa despesa= new Despesa();
                    despesa.setTipoDespesa(tipoDespesa);
                    despesa.setDate(textData);
                    despesa.setValor(valorDespesa);

                    despesas.add(despesa);
                    despesaAdapter.notifyDataSetChanged();

                    editTipoDespesa.setText("");
                    editTextDate.setText("");
                    editValorDespesa.setText("");

                    showDespAddedDialog (String.valueOf(despesa));
                } else {
                    Toast.makeText(getApplicationContext(), "Informe uma despesa! ", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void addTaskToView(String despesa) {
        TextView textView = new TextView(this);
        textView.setText(despesa);
        recyclerViewDesp.addView(textView);
    }
    private void showDespAddedDialog(String despesa) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Despesa adicionada com sucesso!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private class DespesaAdapter extends RecyclerView.Adapter<DespesaAdapter.DespesaViewHolder> {
        private List<Despesa> despesas;
        public DespesaAdapter(List<Despesa> despesas) {
            this.despesas = despesas;
        }
        @NonNull
        @Override

        public DespesaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_despesa, parent, false);
            return new DespesaViewHolder(view);
        }

        @Override

        public void onBindViewHolder(@NonNull DespesaViewHolder holder, int position) {

            Despesa despesa = despesas.get(position);
            holder.bind(despesa);
        }

        @Override
        public int getItemCount() {
            return despesas.size();
        }
        public class DespesaViewHolder extends RecyclerView.ViewHolder {
            private TextView textViewTipoDespesa;
            private TextView textViewTextDate;
            private TextView textViewValorDespesa;

            public DespesaViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewTipoDespesa = itemView.findViewById(R.id.textViewTipoDespesa);
                textViewTextDate= itemView.findViewById(R.id.textViewTextDate);
                textViewValorDespesa= itemView.findViewById(R.id.textViewValorDespesa);

            }
            public void bind(Despesa despesa) {
                textViewTipoDespesa.setText("Despesa: " + despesa.getTipoDespesa());
                textViewTextDate.setText("Data: " + despesa.getDate());
                textViewValorDespesa.setText("Valor: " + despesa.getValor());

            }
        }
    }

}
