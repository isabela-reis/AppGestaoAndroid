package com.example.appgestao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CadastrarActivity extends AppCompatActivity {

    private EditText TextUser;
    private EditText TextSenha;
    private Button btnCadastrar;
    private Button btnVoltar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);mAuth = FirebaseAuth.getInstance();
        TextUser = findViewById(R.id.TextUser);
        TextSenha = findViewById(R.id.TextSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrarUsuario();
            }
        });
    }

    private void cadastrarUsuario() {
        String email = TextUser.getText().toString();
        String password = TextSenha.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CadastrarActivity.this, "Cadastro bem-sucedido", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(CadastrarActivity.this, "Falha no cadastro", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}