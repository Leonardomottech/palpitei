package database;

import model.Participant;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para Participantes
 */
public class ParticipantDAO {
    private DatabaseManager db;

    public ParticipantDAO(DatabaseManager db) {
        this.db = db;
    }

    /**
     * Insere um novo participante
     */
    public boolean save(Participant participant) {
        try {
            String sql = "INSERT INTO participant (id, points) VALUES (?, ?)";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setInt(1, participant.getId());
            stmt.setInt(2, participant.getPoints());
            
            int result = stmt.executeUpdate();
            stmt.close();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca um participante pelo ID
     */
    public Participant findById(int id) {
        try {
            String sql = "SELECT p.*, pe.name, pe.email, pe.password FROM participant p " +
                        "JOIN person pe ON p.id = pe.id WHERE p.id = ?";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                int points = rs.getInt("points");
                
                Participant participant = new Participant(id, name, email, password);
                participant.setPoints(points);
                
                rs.close();
                stmt.close();
                return participant;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todos os participantes
     */
    public List<Participant> findAll() {
        List<Participant> participants = new ArrayList<>();
        try {
            String sql = "SELECT p.*, pe.name, pe.email, pe.password FROM participant p " +
                        "JOIN person pe ON p.id = pe.id";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                int points = rs.getInt("points");
                
                Participant participant = new Participant(id, name, email, password);
                participant.setPoints(points);
                participants.add(participant);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }

    /**
     * Atualiza os pontos de um participante
     */
    public boolean updatePoints(int participantId, int points) {
        try {
            String sql = "UPDATE participant SET points = ? WHERE id = ?";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setInt(1, points);
            stmt.setInt(2, participantId);
            
            int result = stmt.executeUpdate();
            stmt.close();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adiciona pontos a um participante
     */
    public boolean addPoints(int participantId, int points) {
        Participant participant = findById(participantId);
        if (participant != null) {
            return updatePoints(participantId, participant.getPoints() + points);
        }
        return false;
    }

    /**
     * Deleta um participante
     */
    public boolean delete(int id) {
        try {
            String sql = "DELETE FROM participant WHERE id = ?";
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
