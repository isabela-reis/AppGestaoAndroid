package com.example.appgestao.model;

public class Despesa {
    private String tipoDespesa;
    private Double valor;
    private String date;

    public Despesa(String tipoDespes, Double valor, String date) {
        this.tipoDespesa = tipoDespes;
        this.valor = valor;
        this.date = date;

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

}