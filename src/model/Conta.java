package model;

import exception.SaldoInsuficienteException;

public abstract class Conta {
    protected int numero;
    protected String titular;
    protected double saldo;

    public Conta(int numero, String titular, double saldo) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = saldo;
    }

    public int getNumero() {return numero;}

    public void setNumero(int numero) {this.numero = numero;}

    public String getTitular() {return titular;}

    public void setTitular(String titular) {this.titular = titular;}

    public double getSaldo() {return saldo;}

    public void setSaldo(double saldo) {this.saldo = saldo;}

    public abstract void sacar(double valor) throws SaldoInsuficienteException;

    public void depositar(double valor){
        if(valor > 0){
            saldo += valor;
        }
    }

    public void imprimirDados(){
        System.out.println("Numero: " + numero);
        System.out.println("Titular: " + titular);
        System.out.println("Saldo: R$" + String.format("%.2f", saldo));
    }
}