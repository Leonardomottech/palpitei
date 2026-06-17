package database;

import java.sql.*;
import java.util.*;

/**
 * Gerenciador de banco de dados SQLite.
 * Mantém a conexão e oferece métodos para executar queries.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:palpitei.db";
    private Connection connection;

    public DatabaseManager() {
        try {
            // Carrega o driver SQLite
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            initializeTables();
        } catch (ClassNotFoundException e) {
            System.err.println("Driver SQLite não encontrado!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados!");
            e.printStackTrace();
        }
    }

    /**
     * Cria as tabelas se não existirem
     */
    private void initializeTables() throws SQLException {
        String[] tables = {
            // Tabela de Pessoas (Admin e Participant herdam desta)
            "CREATE TABLE IF NOT EXISTS person (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  name TEXT NOT NULL," +
            "  email TEXT UNIQUE NOT NULL," +
            "  password TEXT NOT NULL," +
            "  type TEXT NOT NULL" + // 'ADMIN' ou 'PARTICIPANT'
            ");",

            // Tabela de Participantes
            "CREATE TABLE IF NOT EXISTS participant (" +
            "  id INTEGER PRIMARY KEY," +
            "  points INTEGER DEFAULT 0," +
            "  FOREIGN KEY(id) REFERENCES person(id)" +
            ");",

            // Tabela de Clubes
            "CREATE TABLE IF NOT EXISTS club (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  name TEXT NOT NULL UNIQUE," +
            "  country TEXT NOT NULL" +
            ");",

            // Tabela de Campeonatos
            "CREATE TABLE IF NOT EXISTS championship (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  name TEXT NOT NULL UNIQUE," +
            "  year INTEGER NOT NULL," +
            "  status TEXT DEFAULT 'ABERTO'" + // ABERTO, FECHADO, FINALIZADO
            ");",

            // Tabela de Grupos
            "CREATE TABLE IF NOT EXISTS group_table (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  championship_id INTEGER NOT NULL," +
            "  name TEXT NOT NULL," +
            "  FOREIGN KEY(championship_id) REFERENCES championship(id)" +
            ");",

            // Tabela de Partidas
            "CREATE TABLE IF NOT EXISTS match_table (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  group_id INTEGER NOT NULL," +
            "  home_club_id INTEGER NOT NULL," +
            "  away_club_id INTEGER NOT NULL," +
            "  status TEXT DEFAULT 'PENDENTE'," + // PENDENTE, FINALIZADO
            "  FOREIGN KEY(group_id) REFERENCES group_table(id)," +
            "  FOREIGN KEY(home_club_id) REFERENCES club(id)," +
            "  FOREIGN KEY(away_club_id) REFERENCES club(id)" +
            ");",

            // Tabela de Resultados de Partidas
            "CREATE TABLE IF NOT EXISTS match_result (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  match_id INTEGER UNIQUE NOT NULL," +
            "  home_goals INTEGER NOT NULL," +
            "  away_goals INTEGER NOT NULL," +
            "  FOREIGN KEY(match_id) REFERENCES match_table(id)" +
            ");",

            // Tabela de Apostas
            "CREATE TABLE IF NOT EXISTS bet (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  participant_id INTEGER NOT NULL," +
            "  match_id INTEGER NOT NULL," +
            "  predicted_result TEXT NOT NULL," + // 'WIN', 'DRAW', 'LOSS'
            "  points_earned INTEGER DEFAULT 0," +
            "  FOREIGN KEY(participant_id) REFERENCES participant(id)," +
            "  FOREIGN KEY(match_id) REFERENCES match_table(id)" +
            ");",

            // Tabela de Pontuações
            "CREATE TABLE IF NOT EXISTS score (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  participant_id INTEGER NOT NULL," +
            "  championship_id INTEGER NOT NULL," +
            "  points INTEGER DEFAULT 0," +
            "  FOREIGN KEY(participant_id) REFERENCES participant(id)," +
            "  FOREIGN KEY(championship_id) REFERENCES championship(id)" +
            ");"
        };

        try (Statement stmt = connection.createStatement()) {
            for (String sql : tables) {
                stmt.execute(sql);
            }
            System.out.println("✓ Tabelas inicializadas com sucesso!");
        }
    }

    /**
     * Executa uma query SELECT
     */
    public ResultSet query(String sql) throws SQLException {
        return connection.createStatement().executeQuery(sql);
    }

    /**
     * Executa um UPDATE, INSERT ou DELETE
     */
    public int update(String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            return stmt.executeUpdate(sql);
        }
    }

    /**
     * Executa um INSERT com PreparedStatement
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    /**
     * Fecha a conexão
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
