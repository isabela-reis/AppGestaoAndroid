package com.example.appgestao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.example.appgestao.model.Despesa;
import com.example.appgestao.model.SyncService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


 public class MainActivity extends AppCompatActivity {
    public static final int NEW_EXPENSE_ACTIVITY_REQUEST_CODE = 1;

    private EditText editTipoDespesa;
    private EditText editTextDate;
    private EditText editValorDespesa;
    private Button buttonAddDesp;
    private Button buttonEditDesp;
    private Button buttonCompartilhar;
    private RecyclerView recyclerViewDesp;
    private DespesaAdapter despesaAdapter;
    private Despesa despesaSelecionada;
    private AlertDialog dialog;
    private List<Despesa> despesas = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTipoDespesa = findViewById(R.id.editTipoDespesa);
        editTextDate = findViewById(R.id.editTextDate);
        editValorDespesa = findViewById(R.id.editValorDespesa);
        buttonAddDesp = findViewById(R.id.buttonAddDesp);
        buttonEditDesp = findViewById(R.id.buttonEditDesp);
        buttonCompartilhar = findViewById(R.id.buttonCompartilhar);
        recyclerViewDesp = findViewById(R.id.recyclerViewDesp);

        databaseReference = FirebaseDatabase.getInstance().getReference();

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

                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {

                        clean = clean + ddmmyyyy.substring(clean.length());

                    } else {

                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        if (mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);

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
                    Despesa despesa = new Despesa();
                    despesa.setTipoDespesa(tipoDespesa);
                    despesa.setDate(textData);
                    despesa.setValor(valorDespesa);

                    despesas.add(despesa);
                    despesaAdapter.notifyDataSetChanged();

                    Intent intent = new Intent(MainActivity.this, SyncService.class);
                    intent.putExtra("despesa", despesa);
                    startService(intent);

                    editTipoDespesa.setText("");
                    editTextDate.setText("");
                    editValorDespesa.setText("");

                    showDespAddedDialog(String.valueOf(despesa));
                } else {
                    Toast.makeText(getApplicationContext(), "Informe uma despesa! ", Toast.LENGTH_SHORT).show();
                }

            }
        });

        buttonCompartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDespByMessage();
            }
        });

        buttonEditDesp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(despesaSelecionada == null)) {
                    launchEditActivity(despesaSelecionada);

                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Descarta o diálogo se ele estiver sendo exibido
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void setDespesaSelecionada(Despesa despesa) {

        this.despesaSelecionada = despesa;
        Log.d("metodo", "Despesa selecionada: " + despesa.getTipoDespesa());
        System.out.println("Despesa selecionada: " + despesa.getTipoDespesa());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_EXPENSE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            String tipoDespesa = data.getStringExtra("EXTRA_REPLY_TIPODESPESA");
            String textDate = data.getStringExtra("EXTRA_REPLY_TEXTDATE");
            String valorDespesa = data.getStringExtra("EXTRA_REPLY_VALORDESPESA");

        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void launchEditActivity(Despesa despesa) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("despesa", despesa);
        intent.putExtra("IdDespesa", despesa.getId());
        mStartForResult.launch(intent);
    }

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Despesa despesaEditada = (Despesa) data.getSerializableExtra("despesa");

                    int index = despesas.indexOf(despesaSelecionada);
                    if (index != -1) {
                        despesas.set(index, despesaEditada);
                        despesaAdapter.notifyItemChanged(index);
                    }
                }
            });


    private void sendDespByMessage() {
        Intent messageIntent = new Intent(Intent.ACTION_SEND);
        messageIntent.setType("text/plain");
        messageIntent.putExtra(Intent.EXTRA_TEXT, generateDespText());
        startActivity(Intent.createChooser(messageIntent, "Enviar Mensagem de Texto"));
    }

    private String generateDespText() {
        StringBuilder sb = new StringBuilder();
        for (Despesa despesa : despesas) {
            sb.append("Título: ").append(despesa.getTipoDespesa()).append("\n");
            sb.append("Data: ").append(despesa.getDate()).append("\n");
            sb.append("Valor: ").append(despesa.getValor()).append("\n");
        }
        return sb.toString();
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

    private void SelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Selecione uma despesa!")
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
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) view.getContext()).setDespesaSelecionada(despesa);
                    Log.d("onbind", "Despesa selecionada: " + despesa.getTipoDespesa());
                    System.out.println("Despesa selecionada: " + despesa.getTipoDespesa());
                }
            });
        }

        @Override
        public int getItemCount() {
            return despesas.size();
        }

        public class DespesaViewHolder extends RecyclerView.ViewHolder  {
            TextView textViewTipoDespesa;
            TextView textViewTextDate;
            TextView textViewValorDespesa;

            public DespesaViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewTipoDespesa = itemView.findViewById(R.id.textViewTipoDespesa);
                textViewTextDate = itemView.findViewById(R.id.textViewTextDate);
                textViewValorDespesa = itemView.findViewById(R.id.textViewValorDespesa);
            }
            public void bind(Despesa despesa) {
                textViewTipoDespesa.setText("Despesa: " + despesa.getTipoDespesa());
                textViewTextDate.setText("Data: " + despesa.getDate());
                textViewValorDespesa.setText("Valor: " + despesa.getValor());

            }
        }
        }
    }





