package service;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    
    public List<ContaCorrente> filtrarContasSaldoAlto(){
        return contas.values()
                     .stream()
                     .filter(conta -> conta.getSaldo() > 10000)
                     .toList();
    }
    
    public double calcularSaldoTotal(){
        return contas.values()
                     .stream()
                     .map(conta -> conta.getSaldo())
                     .reduce(0.0, (total,  saldo) -> total + saldo);
    }
    
    public Map<String, List<ContaCorrente>> agruparPorSaldo(){
        return contas.values()
                     .stream()
                     .collect(Collectors.groupingBy(conta -> {
                         if(conta.getSaldo() <= 5000){
                             return "Até R$5.000";
                         } else if(conta.getSaldo() <= 10000){
                             return "R$5.001 a R$10.000";
                         } else {
                             return "Acima de R$10.000";
                         }
                     }));
    }
}