package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(
                ConfiguracaoBanco.URL,
                ConfiguracaoBanco.USUARIO,
                ConfiguracaoBanco.SENHA
        );
    }
}