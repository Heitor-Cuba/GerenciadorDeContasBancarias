package service;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import dao.ContaDAO;
import exception.SaldoInsuficienteException;
import model.ContaCorrente;

public class ContaService {

    private Map<Integer, ContaCorrente> contas = new HashMap<>();
    private ContaDAO dao = new ContaDAO();

    public Map<Integer, ContaCorrente> getContas() {
        return contas;
    }

    public void carregarContasDoBanco() throws SQLException {
        List<ContaCorrente> lista = dao.listar();

        for (ContaCorrente conta : lista) {
            contas.put(conta.getNumero(), conta);
        }
    }

    public void adicionarConta(ContaCorrente conta) throws SQLException {
        dao.inserir(conta);
        contas.put(conta.getNumero(), conta);
    }

    public void depositarValor(ContaCorrente conta, double valor)
            throws SaldoInsuficienteException, SQLException {

        conta.depositar(valor);

        dao.atualizarSaldo(
                conta.getNumero(),
                conta.getSaldo()
        );
    }

    public void sacarValor(ContaCorrente conta, double valor)
            throws SaldoInsuficienteException, SQLException {

        conta.sacar(valor);

        dao.atualizarSaldo(
                conta.getNumero(),
                conta.getSaldo()
        );
    }

    public void removerConta(int numero) throws SQLException {
        dao.remover(numero);
        contas.remove(numero);
    }

    public double calcularSaldoTotal() {
        return contas.values()
                .stream()
                .map(conta -> conta.getSaldo())
                .reduce(0.0, (total, saldo) -> total + saldo);
    }

    public Map<String, List<ContaCorrente>> agruparPorSaldo() {
        return contas.values()
                .stream()
                .collect(Collectors.groupingBy(conta -> {
                    if (conta.getSaldo() <= 5000) {
                        return "Até R$5.000";
                    } else if (conta.getSaldo() <= 10000) {
                        return "R$5.001 a R$10.000";
                    } else {
                        return "Acima de R$10.000";
                    }
                }));
    }

    public List<ContaCorrente> filtrarPorSaldo() {
        Predicate<ContaCorrente> saldoMaior5000 =
                conta -> conta.getSaldo() > 5000;

        return contas.values()
                .stream()
                .filter(saldoMaior5000)
                .toList();
    }

    public List<ContaCorrente> filtrarContasPar() {
        Predicate<ContaCorrente> numeroPar =
                conta -> conta.getNumero() % 2 == 0;

        return contas.values()
                .stream()
                .filter(numeroPar)
                .toList();
    }

    public List<ContaCorrente> ordenarPorSaldo() {
        Comparator<ContaCorrente> porSaldo =
                (conta1, conta2) ->
                        Double.compare(conta2.getSaldo(), conta1.getSaldo());

        return contas.values()
                .stream()
                .sorted(porSaldo)
                .toList();
    }

    public List<ContaCorrente> ordenarPorTitular() {
        Comparator<ContaCorrente> porTitular =
                (conta1, conta2) ->
                        conta1.getTitular()
                                .compareToIgnoreCase(conta2.getTitular());

        return contas.values()
                .stream()
                .sorted(porTitular)
                .toList();
    }
}