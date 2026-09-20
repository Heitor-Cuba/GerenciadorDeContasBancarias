package service;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import exception.SaldoInsuficienteException;
import model.ContaCorrente;

public class ContaService {
    private Map<Integer, ContaCorrente> contas = new HashMap<>();
    
    public Map<Integer, ContaCorrente> getContas(){
        return contas;
    }
    
    public void lerContas(String caminho) throws IOException {
        try (var linhas = Files.lines(Paths.get(caminho))) {
            linhas.forEach(linha -> {
                String[] dados = linha.split(", ");

                int numero = Integer.parseInt(dados[0].trim());
                String titular = dados[1].trim();
                double saldo = Double.parseDouble(dados[2].trim());

                ContaCorrente conta = new ContaCorrente(numero, titular, saldo);
                contas.put(numero, conta);
            });  
        }
        
    }
    
    public void sacarValor(ContaCorrente conta, double valor) throws SaldoInsuficienteException {
        conta.sacar(valor);
    }
    
    public void atualizarContas(String caminho) throws IOException {
        StringBuilder dados = new StringBuilder();
        
        for(ContaCorrente conta : contas.values()){
            dados.append(conta.getNumero())
                 .append(", ")
                 .append(conta.getTitular())
                 .append(", ")
                 .append(conta.getSaldo())
                 .append(System.lineSeparator());
        }
        
        Files.write(Paths.get(caminho), dados.toString().getBytes());
    }
}