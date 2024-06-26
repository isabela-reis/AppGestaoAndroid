package com.example.appgestao.model;

import java.io.Serializable;

public class Despesa implements Serializable {

    private static int nextId = 1;
    private int id;
    private String tipoDespesa;
    private Double valor;
    private String date;

    public Despesa(String tipoDespesa, String date, Double valor) {
        this.id = nextId++;
        this.tipoDespesa = tipoDespesa;
        this.date = date;
        this.valor = valor;
    }

    public Despesa() {

    }

    public String getTipoDespesa() {

        return tipoDespesa;
    }

    public void setTipoDespesa(String tipoDespesa) {

        this.tipoDespesa = tipoDespesa;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {

        this.valor = valor;
    }

    public String getDate() {

        return date;
    }

    public void setDate(String date) {

        this.date = date;
    }

    public int getId() {

        return id;
    }

}