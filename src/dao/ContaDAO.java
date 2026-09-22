package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.Conexao;
import model.Conta;
import model.ContaCorrente;

public class ContaDAO {
    public void inserir(Conta conta) throws SQLException {
        String sql = "INSERT INTO contas (numero, titular, saldo) VALUES (?, ?, ?)";
        
        try (Connection conexao = Conexao.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)){
                    stmt.setInt(1, conta.getNumero());
                    stmt.setString(2, conta.getTitular());
                    stmt.setDouble(3, conta.getSaldo());
                    stmt.executeUpdate();
                }
    }
   
    public List<ContaCorrente> listar() throws SQLException {
        List<ContaCorrente> contas = new ArrayList<>();
        
        String sql = "SELECT * FROM contas";
        
        try (Connection conexao = Conexao.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql);
                
                ResultSet resultado = stmt.executeQuery()) {
                    while (resultado.next()) {
                        int numero = resultado.getInt("numero");
                        String titular = resultado.getString("titular");
                        double saldo = resultado.getDouble("saldo");

                        ContaCorrente conta = new ContaCorrente(numero, titular, saldo);

                        contas.add(conta);
                    }
                }
        
        return contas;
    }
    
    public ContaCorrente buscarPorNumero(int numero) throws SQLException{
        String sql = "SELECT * FROM contas WHERE numero = ?";
        
        try (Connection conexao = Conexao.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {
                    stmt.setInt(1, numero);

                    try (ResultSet resultado = stmt.executeQuery()) {
                        if (resultado.next()) {
                            int numeroConta = resultado.getInt("numero");
                            String titular = resultado.getString("titular");
                            double saldo = resultado.getDouble("saldo");

                            return new ContaCorrente(
                                numeroConta,
                                titular,
                                saldo
                            );
                        }
                    }
                }
        
        return null;
    }
    
    public void atualizarSaldo(int numero, double novoSaldo) throws SQLException {
        String sql = "UPDATE contas SET saldo = ? WHERE numero = ?";

        try (Connection conexao = Conexao.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {
                    stmt.setDouble(1, novoSaldo);
                    stmt.setInt(2, numero);
                    stmt.executeUpdate();
                }
    }
    
    public void remover(int numero) throws SQLException {
        String sql = "DELETE FROM contas WHERE numero = ?";

        try (Connection conexao = Conexao.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {
                        stmt.setInt(1, numero);
                        stmt.executeUpdate();
                }
    }
    
    public void transferir(int numeroOrigem, int numeroDestino, double valor)
            throws SQLException {
        if (valor <= 0) {
            throw new SQLException("O valor da transferência deve ser maior que zero.");
        }

        if (numeroOrigem == numeroDestino) {
            throw new SQLException("A conta de origem e destino devem ser diferentes.");
        }

        String sqlBuscarSaldo = "SELECT saldo FROM contas WHERE numero = ? FOR UPDATE";

        String sqlAtualizarSaldo = "UPDATE contas SET saldo = ? WHERE numero = ?";

        try (Connection conexao = Conexao.conectar();
                PreparedStatement stmtBuscarOrigem =
                        conexao.prepareStatement(sqlBuscarSaldo);
                PreparedStatement stmtBuscarDestino =
                        conexao.prepareStatement(sqlBuscarSaldo);
                PreparedStatement stmtAtualizar =
                        conexao.prepareStatement(sqlAtualizarSaldo)) {

            try {
                conexao.setAutoCommit(false);

                double saldoOrigem;
                double saldoDestino;

                stmtBuscarOrigem.setInt(1, numeroOrigem);

                try (ResultSet resultado = stmtBuscarOrigem.executeQuery()) {
                    if (!resultado.next()) {
                        throw new SQLException("Conta de origem não encontrada.");
                    }

                    saldoOrigem = resultado.getDouble("saldo");
                }

                stmtBuscarDestino.setInt(1, numeroDestino);

                try (ResultSet resultado = stmtBuscarDestino.executeQuery()) {
                    if (!resultado.next()) {
                        throw new SQLException("Conta de destino não encontrada.");
                    }

                    saldoDestino = resultado.getDouble("saldo");
                }

                if (valor > saldoOrigem) {
                    throw new SQLException("Saldo insuficiente para transferência.");
                }

                double novoSaldoOrigem = saldoOrigem - valor;
                double novoSaldoDestino = saldoDestino + valor;

                stmtAtualizar.setDouble(1, novoSaldoOrigem);
                stmtAtualizar.setInt(2, numeroOrigem);
                stmtAtualizar.executeUpdate();

                stmtAtualizar.setDouble(1, novoSaldoDestino);
                stmtAtualizar.setInt(2, numeroDestino);
                stmtAtualizar.executeUpdate();

                conexao.commit();
            } catch (SQLException e) {
                conexao.rollback();
                throw e;
            }
        }
    }
}