package app;

import java.io.IOException;
import java.util.Scanner;

import service.ContaService;
import model.ContaCorrente;
import exception.SaldoInsuficienteException;

public class Main {

    public static void main(String[] args) {
        
        ContaService cs = new ContaService();
        
        ContaCorrente c1 = null;
        
        try {
            c1 = cs.lerConta("conta.txt");
            
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("Digite o valor para saque: ");
            double valorSaque = scanner.nextDouble();
            
            try {
                cs.sacarValor(c1, valorSaque);
                System.out.println("Saque efetivado com sucesso!");
            } catch (SaldoInsuficienteException e) {
                System.out.println(e.getMessage());
            }
            
            scanner.close();
            
        } catch(IOException e) {
            System.out.println("Erro ao ler o arquivo.");
        }
        
        if(c1 != null) {
            try {
                cs.atualizarConta(c1, "conta_atualizada.txt");

            } catch (IOException e) {
                System.out.println("Erro ao gravar o arquivo.");
            }
        }
        
    }
}