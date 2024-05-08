package com.example.appgestao.model;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SyncService extends Service {

    private DatabaseReference databaseReference;


    public void onCreate() {
        super.onCreate();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("despesas");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Despesa despesa = (Despesa) intent.getSerializableExtra("despesa");
        new SyncDesp(databaseReference).execute(despesa);
        return START_NOT_STICKY;
    }
    private static class SyncDesp extends AsyncTask<Despesa, Void, Void> {
        private final DatabaseReference databaseRef;
        SyncDesp(DatabaseReference dbRef) {
            this.databaseRef = dbRef;
        }
        protected Void doInBackground(Despesa... despesas) {
            databaseRef.child("despesas").push().setValue(despesas[0]);
            return null;
        }
    }
}