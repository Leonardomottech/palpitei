package database;

import model.Club;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para Clubes
 */
public class ClubDAO {
    private DatabaseManager db;

    public ClubDAO(DatabaseManager db) {
        this.db = db;
    }

    /**
     * Insere um novo clube
     */
    public int save(Club club) {
        try {
            String sql = "INSERT INTO club (name, country) VALUES (?, ?)";
            PreparedStatement stmt = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, club.getName());
            stmt.setString(2, club.getCountry());
            
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
     * Busca um clube pelo ID
     */
    public Club findById(int id) {
        try {
            String sql = "SELECT * FROM club WHERE id = ?";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                String country = rs.getString("country");
                
                Club club = new Club(id, name, country);
                
                rs.close();
                stmt.close();
                return club;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca um clube pelo nome
     */
    public Club findByName(String name) {
        try {
            String sql = "SELECT * FROM club WHERE name = ?";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("id");
                String country = rs.getString("country");
                
                Club club = new Club(id, name, country);
                
                rs.close();
                stmt.close();
                return club;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todos os clubes
     */
    public List<Club> findAll() {
        List<Club> clubs = new ArrayList<>();
        try {
            String sql = "SELECT * FROM club";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String country = rs.getString("country");
                
                clubs.add(new Club(id, name, country));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clubs;
    }

    /**
     * Atualiza informações de um clube
     */
    public boolean update(Club club) {
        try {
            String sql = "UPDATE club SET name = ?, country = ? WHERE id = ?";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setString(1, club.getName());
            stmt.setString(2, club.getCountry());
            stmt.setInt(3, club.getId());
            
            int result = stmt.executeUpdate();
            stmt.close();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deleta um clube
     */
    public boolean delete(int id) {
        try {
            String sql = "DELETE FROM club WHERE id = ?";
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
