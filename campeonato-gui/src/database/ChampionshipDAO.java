package database;

import model.Championship;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para Campeonatos
 */
public class ChampionshipDAO {
    private DatabaseManager db;

    public ChampionshipDAO(DatabaseManager db) {
        this.db = db;
    }

    /**
     * Insere um novo campeonato
     */
    public int save(Championship championship) {
        try {
            String sql = "INSERT INTO championship (name, year, status) VALUES (?, ?, ?)";
            PreparedStatement stmt = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, championship.getName());
            stmt.setInt(2, championship.getYear());
            stmt.setString(3, "ABERTO");
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    rs.close();
                    stmt.close();
                    return id;
                }
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Busca um campeonato pelo ID
     */
    public Championship findById(int id) {
        try {
            String sql = "SELECT * FROM championship WHERE id = ?";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                int year = rs.getInt("year");
                
                Championship championship = new Championship(name, year);
                championship.setId(id);
                
                rs.close();
                stmt.close();
                return championship;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca um campeonato pelo nome
     */
    public Championship findByName(String name) {
        try {
            String sql = "SELECT * FROM championship WHERE name = ?";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("id");
                int year = rs.getInt("year");
                
                Championship championship = new Championship(name, year);
                championship.setId(id);
                
                rs.close();
                stmt.close();
                return championship;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todos os campeonatos
     */
    public List<Championship> findAll() {
        List<Championship> championships = new ArrayList<>();
        try {
            String sql = "SELECT * FROM championship";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int year = rs.getInt("year");
                
                Championship championship = new Championship(name, year);
                championship.setId(id);
                championships.add(championship);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return championships;
    }

    /**
     * Atualiza status de um campeonato
     */
    public boolean updateStatus(int championshipId, String status) {
        try {
            String sql = "UPDATE championship SET status = ? WHERE id = ?";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, championshipId);
            
            int result = stmt.executeUpdate();
            stmt.close();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deleta um campeonato
     */
    public boolean delete(int id) {
        try {
            String sql = "DELETE FROM championship WHERE id = ?";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setInt(1, id);
            
            int result = stmt.executeUpdate();
            stmt.close();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
